package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

import nc.mairie.enums.EnumModificationPA;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.HistoPositionAdm;
import nc.mairie.metier.carriere.MATMUT;
import nc.mairie.metier.carriere.MATMUTHIST;
import nc.mairie.metier.paye.Matricule;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.carriere.HistoPositionAdmDao;
import nc.mairie.spring.dao.metier.carriere.MATMUTDao;
import nc.mairie.spring.dao.metier.carriere.MATMUTHistDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IRadiService;

/**
 * Process OeAGENTPosAdm Date de création : (04/08/11 09:22:55)
 * 
 */
public class OeAGENTPosAdm extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private ArrayList<PositionAdmAgent> listePAAgent;
	private ArrayList<PositionAdm> listePA;

	private Hashtable<String, PositionAdm> hashPA;

	private Agent agentCourant;
	private PositionAdmAgent paCourante;

	private HistoPositionAdmDao histoPositionAdmDao;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche PA.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche PA.";
	public String ACTION_MODIFICATION = "Modification d'une fiche PA.";
	public String ACTION_CREATION = "Création d'une fiche PA.";

	private String messageInf = Const.CHAINE_VIDE;
	public boolean DateDebutEditable = true;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat perrepSdf = new SimpleDateFormat("YYYYMM");
	
	private MATMUTDao matmutDao;
	private MATMUTHistDao matmutHistDao;
	private AgentDao agentDao;
	
	private Logger logger = LoggerFactory.getLogger(OeAGENTPosAdm.class);
	
	private IRadiService radiService;

	// #44481 : Liste des PA provoquant ou non la charge de cotisation mutuelle.
	private static final List<String> PA_INACTIVES = Arrays.asList("26", "40", "41", "45", "24");
	private static final List<String> PA_ACTIVES = Arrays.asList("01");

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName((String) ServletAgent.getMesParametres().get(
			"DTAARA_SCHEMA"), (String) ServletAgent.getMesParametres().get("DTAARA_NAME"), "DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent
			.getMesParametres().get("HOST_SGBD_PAYE"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_ADMIN"),
			(String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")), CALC_PATH.getPath());
	private String calculPaye;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (04/08/11 09:22:55)
	 * 
	 * RG_AG_PA_C01 RG_AG_PA_C02
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// SI CALCUL PAYE EN COURS
		String percou = DTAARA_CALC.read().toString();
		if (!percou.trim().equals(Const.CHAINE_VIDE)) {
			setCalculPaye(percou);
		} else {
			setCalculPaye(Const.CHAINE_VIDE);
		}

		// Si hashtable des positions administrative vide
		// RG_AG_PA_C01
		// RG_AG_PA_C02

		if (getHashPA().size() == 0) {
			ArrayList<PositionAdm> listePA = PositionAdm.listerPositionAdm(getTransaction());
			setListePA(new ArrayList<PositionAdm>());
			for (PositionAdm pa : listePA) {
				getListePA().add(pa);
				getHashPA().put(pa.getCdpadm(), pa);
			}
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListePA(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getHistoPositionAdmDao() == null) {
			setHistoPositionAdmDao(new HistoPositionAdmDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMatmutDao() == null) {
			setMatmutDao(new MATMUTDao((MairieDao) context.getBean("mairieDao")));
		}
		if (getMatmutHistDao() == null) {
			setMatmutHistDao(new MATMUTHistDao((MairieDao) context.getBean("mairieDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
	}

	/**
	 * fonction qui permet de savoir si une paye est en cours.
	 * 
	 * @return calculPaye
	 */
	public String getCalculPaye() {
		return calculPaye;
	}

	private void setCalculPaye(String calculPaye) {
		this.calculPaye = calculPaye;
	}

	/**
	 * Initialisation de la liste des positions administratives de l'agent
	 * courant Date de création : (04/08/11)
	 * 
	 */
	private void initialiseListePA(HttpServletRequest request) throws Exception {
		// Recherche des accidents du travail de l'agent
		ArrayList<PositionAdmAgent> listePAAgent = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(),
				getAgentCourant());
		setListePAAgent(listePAAgent);

		int indicePaAgent = 0;
		if (getListePAAgent() != null) {
			for (int i = 0; i < getListePAAgent().size(); i++) {
				PositionAdmAgent paa = (PositionAdmAgent) getListePAAgent().get(i);
				PositionAdm pa = (PositionAdm) getHashPA().get(paa.getCdpadm());

				addZone(getNOM_ST_POSA(indicePaAgent),
						pa.getCdpadm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : pa.getCdpadm());
				addZone(getNOM_ST_LIB_POSA(indicePaAgent),
						pa.getLiPAdm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : pa.getLiPAdm());
				addZone(getNOM_ST_REF_ARR(indicePaAgent),
						paa.getRefarr().equals(Const.CHAINE_VIDE) ? "&nbsp;" : paa.getRefarr());
				addZone(getNOM_ST_DATE_ARR(indicePaAgent),
						paa.getDateArrete() == null || paa.getDateArrete().equals(Const.DATE_NULL) ? "&nbsp;" : paa
								.getDateArrete());
				addZone(getNOM_ST_DATE_DEBUT(indicePaAgent),
						paa.getDatdeb() == null || paa.getDatdeb().equals(Const.DATE_NULL) ? "&nbsp;" : paa.getDatdeb());
				addZone(getNOM_ST_DATE_FIN(indicePaAgent),
						paa.getDatfin() == null || paa.getDatfin().equals(Const.DATE_NULL) ? "&nbsp;" : paa.getDatfin());

				indicePaAgent++;
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTPosAdm. Date de création : (04/08/11
	 * 09:22:55)
	 * 
	 */
	public OeAGENTPosAdm() {
		super();
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 09:22:55)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		DateDebutEditable = true;

		// init de la visite courante
		setPaCourante(new PositionAdmAgent());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public PositionAdmAgent getLastPA() {

		if (getListePAAgent().size() > 0)
			return getListePAAgent().get(getListePAAgent().size() - 1);

		return null;
	}

	public PositionAdmAgent getPrevPA() throws Exception {

		if (getVAL_ST_ACTION().equals(ACTION_CREATION) && getListePAAgent().size() > 0) {
			return getListePAAgent().get(getListePAAgent().size() - 1);
		}
		if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION) && getListePAAgent().size() > 1) {
			PositionAdmAgent paBase = (PositionAdmAgent) getPaCourante().getBasicMetierBase();
			PositionAdmAgent posAdmPrec = PositionAdmAgent.chercherPositionAdmAgentPrec(getTransaction(),
					getAgentCourant().getNomatr(),
					Services.convertitDate(Services.formateDate(paBase.getDatdeb()), "dd/MM/yyyy", "yyyyMMdd"));
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				return null;
			} else {
				return posAdmPrec;
			}
		}
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION) && getListePAAgent().size() > 1) {
			return getListePAAgent().get(getListePAAgent().size() - 2);
		}

		return null;

	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'un
	 * accident de travail
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		// On vide les zone de saisie
		addZone(getNOM_EF_DATE_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(),
				getLastPA() != null && getLastPA().getDatfin() != null
						&& !getLastPA().getDatfin().equals(Const.DATE_NULL) ? Services.ajouteJours(
						Services.formateDate(getLastPA().getDatfin()), 1) : Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_POSA(), Const.CHAINE_VIDE);
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 09:22:55)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_PA_A02
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// RG_AG_PA_A02

		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_POSA()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "position administrative"));
			return false;
		}

		// reference arrete
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_REF_ARR()))
				&& !Services.estNumerique(getZone(getNOM_EF_REF_ARR()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Ref. arrêté"));
			return false;
		}

		// date de l'arrêté
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_ARR()))
				&& !Services.estUneDate(getZone(getNOM_EF_DATE_ARR()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de l'arrêté"));
			return false;
		}

		// date de début obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// date de début
		if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}

		// date de fin
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))
				&& !Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
			return false;
		}

		// date de fin > date de début
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))
				&& Services.compareDates(getZone(getNOM_EF_DATE_FIN()), getZone(getNOM_EF_DATE_DEBUT())) <= 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de début"));
			return false;
		}

		return true;
	}

	/**
	 * Verifie la regle de gestion du chevauchement des primes Date de création
	 * : (09/08/11)
	 * 
	 * @return RG_AG_PA_A02 RG_AG_PA_A04
	 */
	public boolean checkChevauchement() throws Exception {
		// RG_AG_PA_A02
		// RG_AG_PA_A04

		PositionAdmAgent lastPA = getPrevPA();

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			if (lastPA != null && !lastPA.equals(getPaCourante())) {
				if (Services.compareDates(lastPA.getDatdeb(), getPaCourante().getDatdeb()) > 0) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR070"));
					return false;
				}
			}
		} else {
			if (lastPA != null) {
				if (lastPA.getDatfin() != null && !lastPA.getDatfin().equals(Const.DATE_NULL)
						&& Services.compareDates(lastPA.getDatfin(), getPaCourante().getDatdeb()) > 0) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR070"));
					return false;
				} else if (Services.compareDates(lastPA.getDatdeb(), getPaCourante().getDatdeb()) >= 0) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR070"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 09:22:55)
	 * 
	 * RG_AG_PA_A07 RG_AG_PA_A08 RG_AG_PA_A03 RG_AG_PA_A06 RG_AG_PA_C03
	 * RG_AG_PA_A09
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			PositionAdmAgent prev = getPrevPA();
			// RG_AG_PA_A07
			// RG_AG_PA_A08
			if (prev != null) {
				prev.setDatfin(null);

				// RG_AG_PA_A01
				HistoPositionAdm histo = new HistoPositionAdm(prev);
				getHistoPositionAdmDao().creerHistoPositionAdm(histo, user, EnumTypeHisto.MODIFICATION);
				prev.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user);

				if (getPaCourante().estPAInactive(getTransaction()) && !prev.estPAInactive(getTransaction()))
					messageInf = MessageUtils.getMessage("INF006");
			}

			// #44481 : régularisations mutuelle
			supprimerMatmut(request);
			
			// On ne peut pas supprimer une PA s'il y a eu une charge générée en paye.
			if (getTransaction().isErreur())
				return false;

			// suppression
			// RG_AG_PA_A01
			HistoPositionAdm histo = new HistoPositionAdm(getPaCourante());
			getHistoPositionAdmDao().creerHistoPositionAdm(histo, user, EnumTypeHisto.SUPPRESSION);
			getPaCourante().supprimerPositionAdmAgent(getTransaction(), user);
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// récupération des informations remplies dans les zones de saisie
			String refArr = getZone(getNOM_EF_REF_ARR());
			String dateFin = Services.formateDate(getZone(getNOM_EF_DATE_FIN()));
			String dateDebut = Services.formateDate(getZone(getNOM_EF_DATE_DEBUT()));
			String dateArrete = Services.formateDate(getZone(getNOM_EF_DATE_ARR()));

			PositionAdm pa = getSelectedPA();

			// Création de l'objet PositionAdministrative a créer/modifier
			Agent agentCourant = getAgentCourant();
			getPaCourante().setNomatr(agentCourant.getNomatr().toString());
			getPaCourante().setCdpadm(pa.getCdpadm());
			getPaCourante().setRefarr(refArr);
			getPaCourante().setDatdeb(dateDebut);
			getPaCourante().setDatfin(dateFin);
			getPaCourante().setDateArrete(dateArrete);

			// regles de gestions
			if (!checkChevauchement())
				return false;

			PositionAdmAgent prevPA = getPrevPA();
			// RG_AG_PA_A03
			// RG_AG_PA_A06
			if (prevPA != null
					&& (prevPA.getDatfin() == null || prevPA.getDatfin().equals(Const.DATE_NULL) || Services
							.compareDates(prevPA.getDatfin(), getPaCourante().getDatdeb()) != 0)) {
				prevPA.setDatfin(getPaCourante().getDatdeb());

				// RG_AG_PA_A01
				HistoPositionAdm histo = new HistoPositionAdm(prevPA);
				getHistoPositionAdmDao().creerHistoPositionAdm(histo, user, EnumTypeHisto.MODIFICATION);
				if (!prevPA.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			}

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				
				// #44481 : régularisations mutuelle
				modifierMatmut(request);
				if (getTransaction().isErreur())
					return false;

				// RG_AG_PA_A01
				HistoPositionAdm histo = new HistoPositionAdm(getPaCourante());
				getHistoPositionAdmDao().creerHistoPositionAdm(histo, user, EnumTypeHisto.MODIFICATION);
				if (!getPaCourante().modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// RG_AG_PA_A01

				// #44481 : régularisations mutuelle
				if (isRegularisationMutuelle())
					createMatmut(request);

				if (getTransaction().isErreur())
					return false;
				
				HistoPositionAdm histo = new HistoPositionAdm(getPaCourante());
				getHistoPositionAdmDao().creerHistoPositionAdm(histo, user, EnumTypeHisto.CREATION);
				if (!getPaCourante().creerPositionAdmAgent(getTransaction(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			}

			if (getTransaction().isErreur())
				return false;
			
			// RG_AG_PA_A09
			if (!Matricule.updateMatricule(getTransaction(), getAgentCourant(), getPaCourante().getDatdeb())) {
				// "ERR009",
				// "Une erreur s'est produite sur la base de données.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
				return false;
			}

			if (getTransaction().isErreur())
				return false;
		}

		if (getTransaction().isErreur())
			return false;

		initialiseListePA(request);

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		commitTransaction();

		if (!messageInf.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(messageInf);
			messageInf = Const.CHAINE_VIDE;
		}

		return true;
	}
	
	/* DEBUT DU CODE SPECIFIQUE A MATMUT */

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre
					// identifiant. Merci de contacter le responsable du
					// projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}
	
	private void isMatmutVentile() throws Exception {
		// On regarde si une ligne existe dans MATMUTHIST (Les lignes à l'état 'V' vont directement dans cette table)
		MATMUTHIST hist = getMatmutHistDao().chercherMATMUTHISTVentileByAgentAndPeriod(getAgentCourant().getNomatr(), getFormattedPerrep(false));
		if (hist != null) {
			getTransaction().declarerErreur("Modification impossible, car une charge a déjà été passée en paye pour cet agent à cette période !");
			logger.error("Modification de la PA impossible pour l'agent matr {}, car une charge a déjà été passée en paye pour cet agent à cette période ({}) !", hist.getNomatr(), hist.getPerrep());
		}
	}

	private Integer getFormattedPerrep(boolean getOldDateDeb) throws Exception {
		String periode = getPaCourante().getDatfin() == null ? getPaCourante().getDatdeb() : getPaCourante().getDatfin();
		
		if (getOldDateDeb && getPaCourante().oldDateDeb != null)
			periode = getPaCourante().oldDateDeb;
			
		Date dateDebut = sdf.parse(periode);
		return Integer.valueOf(perrepSdf.format(dateDebut));
	}

	/**
	 * Cette fonction détermine si l'enchainement des PA doit entrainer une ligne dans MATMUT
	 * @return
	 * @throws Exception
	 */
	private boolean isRegularisationMutuelle() throws Exception {
		PositionAdmAgent currentPA = getPaCourante();
		
		if (!(PA_ACTIVES.contains(currentPA.getCdpadm()) || PA_INACTIVES.contains(currentPA.getCdpadm())))
			return false;

		PositionAdmAgent firstPA = null;
		PositionAdmAgent secondPA = null;
		
		// Il faut prendre l'ancienne date de début dans le cas d'une modification de date.
		String dateDebut = currentPA.oldDateDeb == null ? currentPA.getDatdeb() : currentPA.oldDateDeb;
		
		// Si la PA est active, on cherche la précédente
		if (PA_ACTIVES.contains(currentPA.getCdpadm())) {
			firstPA = PositionAdmAgent.chercherPositionAdmAgentPrec(getTransaction(),
					getAgentCourant().getNomatr(),
					Services.convertitDate(Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMMdd"));
			secondPA = currentPA;
		}
		
		// Si la PA est inactive, on cherche la suivante
		if (PA_INACTIVES.contains(currentPA.getCdpadm())) {
			firstPA = currentPA;
			secondPA = PositionAdmAgent.chercherPositionAdmAgentSuiv(getTransaction(),
							getAgentCourant().getNomatr().toString(),
							Services.convertitDate(Services.formateDate(dateDebut), "dd/MM/yyyy", "yyyyMMdd"));
		}
		
		return PA_INACTIVES.contains(firstPA.getCdpadm()) &&
				PA_ACTIVES.contains(secondPA.getCdpadm());
	}
	
	private void modifierMatmut(HttpServletRequest request) throws Exception {
		isMatmutVentile();
		if (getTransaction().isErreur())
			return;
		
		// On regarde si une ligne existe dans MATMUT
		MATMUT previousMatmut = getMatmutDao().chercherMatmutByMatrAndPeriod(getAgentCourant().getNomatr(), getFormattedPerrep(false));

		Agent agentConnecte = getAgentConnecte(request);

		// Si une ligne existait sur l'ancienne date, on va la supprimer
		MATMUT matmutIfDateChanged = getMatmutDao().chercherMatmutByMatrAndPeriod(getAgentCourant().getNomatr(), getFormattedPerrep(true));
		if (matmutIfDateChanged != null && (previousMatmut == null || !previousMatmut.equals(matmutIfDateChanged))) {
			logger.debug("Changement de période pour la charge concernant l'agent matricule {}, pour la période {}", matmutIfDateChanged.getNomatr(), matmutIfDateChanged.getPerrep());
			MATMUT matmutToCancel = new MATMUT(matmutIfDateChanged);
			matmutToCancel.setIduser(agentConnecte.getIdAgent().toString());
			matmutToCancel.setCodval(EnumModificationPA.ANNULE.getCode());
			matmutToCancel.setPkey(getMatmutDao().getNextPKVal());
			
			updateMatmut(matmutIfDateChanged, matmutToCancel);
		}

		// S'il n'y a pas d'enregistrement, et que les conditions sont remplies, on créé l'enregistrement
		if (previousMatmut == null) {
			if (isRegularisationMutuelle())
				createMatmut(request);
		}
		// Si un enregistrement existe, il faut mettre à jour l'historique.
		else {
			MATMUT newMatmut = new MATMUT();
			// S'il est éligible, et que la période diffère, on va mettre à jour ce champ
			if (isRegularisationMutuelle()) {
				if (!getFormattedPerrep(false).equals(previousMatmut.getPerrep()) 
						|| !previousMatmut.getCodval().equals(EnumModificationPA.CREE.getCode())) {
					newMatmut.setPkey(getMatmutDao().getNextPKVal());
					newMatmut.setNomatr(previousMatmut.getNomatr());
					newMatmut.setPerrep(getFormattedPerrep(false));
					newMatmut.setCodval(EnumModificationPA.CREE.getCode());
					newMatmut.setIduser(agentConnecte.getIdAgent().toString());
				}
			}
			// Sinon on est dans le cas de la suppression
			else if (!previousMatmut.getCodval().equals(EnumModificationPA.ANNULE.getCode())) {
				newMatmut.setPkey(getMatmutDao().getNextPKVal());
				newMatmut.setNomatr(previousMatmut.getNomatr());
				newMatmut.setPerrep(previousMatmut.getPerrep());
				newMatmut.setCodval(EnumModificationPA.ANNULE.getCode());
				newMatmut.setIduser(agentConnecte.getIdAgent().toString());
			}
			
			if (newMatmut.getCodval() != null)
				updateMatmut(previousMatmut, newMatmut);
		}
	}
	
	private void supprimerMatmut(HttpServletRequest request) throws Exception {
		
		isMatmutVentile();
		if (getTransaction().isErreur())
			return;

		// On regarde si une ligne existe dans MATMUT
		MATMUT previousMatmut = getMatmutDao().chercherMatmutByMatrAndPeriod(getAgentCourant().getNomatr(), getFormattedPerrep(false));
		
		if (previousMatmut != null) {
			Agent agentConnecte = getAgentConnecte(request);
			// Copie, suppression, creation
			MATMUT newMatmut = new MATMUT();
			newMatmut.setPkey(getMatmutDao().getNextPKVal());
			newMatmut.setNomatr(previousMatmut.getNomatr());
			newMatmut.setPerrep(previousMatmut.getPerrep());
			newMatmut.setCodval(EnumModificationPA.ANNULE.getCode());
			newMatmut.setTimelog(new Date());
			newMatmut.setIduser(agentConnecte.getIdAgent().toString());
			
			updateMatmut(previousMatmut, newMatmut);
		}
	}
	
	/**
	 * Etapes de la mise à jour de MATMUT :
	 * - Copie de l'ancienne ligne vers MATMUTHISTO
	 * - Suppression de l'ancienne ligne dans MATMUT
	 * - Ajout de la nouvelle ligne dans MATMUT
	 * 
	 * @param previousMatmut : l'ancienne ligne
	 * @param newMatmut : la ligne à insérer
	 */
	private void updateMatmut(MATMUT previousMatmut, MATMUT newMatmut) {
		logger.debug("Historisation du MATMUT ID {}, et création du MATMUT de l'agent {} pour la période {}.", previousMatmut.getPkey(), newMatmut.getNomatr(), newMatmut.getPerrep());
		// 1 - Copie de l'ancienne ligne vers MATMUTHISTO
		getMatmutHistDao().creerMATMUTHIST(previousMatmut);
		
		// 2 - Suppression de l'ancienne ligne dans MATMUT
		getMatmutDao().supprimerMATMUT(previousMatmut);
		
		// 2 - Suppression de l'ancienne ligne dans MATMUT
		getMatmutDao().creerMATMUT(newMatmut);
	}
	
	// #44481 : Création d'une charge pour la régularisation de la mutuelle.
	private void createMatmut(HttpServletRequest request) throws Exception {
		isMatmutVentile();
		if (getTransaction().isErreur())
			return;
		
		MATMUT matmut = new MATMUT();
		Agent agentConnecte = getAgentConnecte(request);
		
		matmut.setPkey(getMatmutDao().getNextPKVal());
		matmut.setNomatr(getAgentCourant().getNomatr());
		matmut.setPerrep(getFormattedPerrep(false));
		matmut.setCodval(EnumModificationPA.CREE.getCode());
		matmut.setTimelog(new Date());
		matmut.setIduser(agentConnecte.getIdAgent().toString());
		
		// Si une ligne existe déjà (cas : Création / Suppression / Création), il faut l'archiver
		MATMUT existingMatmut = getMatmutDao().chercherMatmutByMatrAndPeriod(getAgentCourant().getNomatr(), getFormattedPerrep(false));
		
		if (existingMatmut != null) {
			if (existingMatmut.getCodval() != EnumModificationPA.CREE.getCode())
				updateMatmut(existingMatmut, matmut);
		} else {
			logger.debug("Création du MATMUT de l'agent {} pour la période {}.", matmut.getNomatr(), matmut.getPerrep());
			getMatmutDao().creerMATMUT(matmut);
		}
	}
	
	/* FIN DU CODE SPECIFIQUE A MATMUT */

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (04/08/11 09:25:27)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (04/08/11 09:25:27)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'une PA Date
	 * de création : 11/08/04
	 * 
	 */
	private boolean initialisePACourant(HttpServletRequest request) throws Exception {

		PositionAdm pa = (PositionAdm) getHashPA().get(getPaCourante().getCdpadm());

		// Alim zones
		addZone(getNOM_ST_POSA(), pa.getLiPAdm());
		addZone(getNOM_EF_POSA(), pa.getCdpadm() + " " + pa.getLiPAdm());

		addZone(getNOM_EF_REF_ARR(), getPaCourante().getRefarr());
		addZone(getNOM_EF_DATE_ARR(), getPaCourante().getDateArrete());
		addZone(getNOM_EF_DATE_DEBUT(), getPaCourante().getDatdeb());
		addZone(getNOM_EF_DATE_FIN(), getPaCourante().getDatfin());

		return true;
	}

	/**
	 * Initilise les zones du formulaire lors d'une consultation Date de
	 * création : 11/08/04
	 */
	private boolean initialisePAConsulter(HttpServletRequest request) throws Exception {
		PositionAdm pa = (PositionAdm) getHashPA().get(getPaCourante().getCdpadm());

		// Alim zones
		addZone(getNOM_ST_POSA(), pa.getLiPAdm());
		addZone(getNOM_EF_POSA(), pa.getCdpadm() + " " + pa.getLiPAdm());

		addZone(getNOM_EF_REF_ARR(), getPaCourante().getRefarr());
		addZone(getNOM_EF_DATE_ARR(), getPaCourante().getDateArrete());
		addZone(getNOM_EF_DATE_DEBUT(), getPaCourante().getDatdeb());
		addZone(getNOM_EF_DATE_FIN(), getPaCourante().getDatfin());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (04/08/11 09:25:27)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POSA Date de
	 * création : (04/08/11 09:25:27)
	 * 
	 */
	public String getNOM_ST_POSA() {
		return "NOM_ST_POSA";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_POSA Date de
	 * création : (04/08/11 09:25:27)
	 * 
	 */
	public String getVAL_ST_POSA() {
		return getZone(getNOM_ST_POSA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARR Date de
	 * création : (04/08/11 09:25:27)
	 * 
	 */
	public String getNOM_EF_DATE_ARR() {
		return "NOM_EF_DATE_ARR";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARR Date de création : (04/08/11 09:25:28)
	 * 
	 */
	public String getVAL_EF_DATE_ARR() {
		return getZone(getNOM_EF_DATE_ARR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (04/08/11 09:25:28)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (04/08/11 09:25:28)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (04/08/11 09:25:28)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (04/08/11 09:25:28)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_ARR Date de
	 * création : (04/08/11 09:25:28)
	 * 
	 */
	public String getNOM_EF_REF_ARR() {
		return "NOM_EF_REF_ARR";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARR Date de création : (04/08/11 09:25:28)
	 * 
	 */
	public String getVAL_EF_REF_ARR() {
		return getZone(getNOM_EF_REF_ARR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (04/08/11 09:28:25)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/08/11 09:28:25)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private Hashtable<String, PositionAdm> getHashPA() {
		if (hashPA == null)
			hashPA = new Hashtable<String, PositionAdm>();
		return hashPA;
	}

	public ArrayList<PositionAdm> getListePA() {
		return listePA;
	}

	private void setListePA(ArrayList<PositionAdm> listePA) {
		this.listePA = listePA;
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	public ArrayList<PositionAdmAgent> getListePAAgent() {
		return listePAAgent;
	}

	private void setListePAAgent(ArrayList<PositionAdmAgent> listePAAgent) {
		this.listePAAgent = listePAAgent;
	}

	private PositionAdmAgent getPaCourante() {
		return paCourante;
	}

	private void setPaCourante(PositionAdmAgent paCourante) {
		this.paCourante = paCourante;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (04/08/11 09:22:55)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListePAAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListePAAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListePAAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (18/10/11 12:00:03)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTPosAdm.jsp";
	}

	/**
	 * Fourni le noom de l'écran utilise pour la gestion des droits
	 */
	public String getNomEcran() {
		return "ECR-AG-ELTSAL-PA";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_POSA Date de
	 * création : (30/08/11 10:25:41)
	 * 
	 */
	public String getNOM_EF_POSA() {
		return "NOM_EF_POSA";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie : EF_POSA
	 * Date de création : (30/08/11 10:25:41)
	 * 
	 */
	public String getVAL_EF_POSA() {
		return getZone(getNOM_EF_POSA());
	}

	/**
	 * Récupere la rubrique selectionnée
	 * 
	 * @return PositionAdm
	 * @throws Exception
	 */
	private PositionAdm getSelectedPA() throws Exception {
		// récupération de la rubrique et Vérification de son existence.
		String idPA = Const.CHAINE_VIDE;
		for (int i = 0; i < getListePA().size(); i++) {
			PositionAdm pa = (PositionAdm) getListePA().get(i);
			String textPA = pa.getCdpadm() + " " + pa.getLiPAdm();
			if (textPA.equals(getVAL_EF_POSA())) {
				idPA = pa.getCdpadm();
				break;
			}
		}
		if (idPA.length() == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "positions administratives"));
		}
		return PositionAdm.chercherPositionAdm(getTransaction(), idPA);
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POSA Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_POSA(int i) {
		return "NOM_ST_POSA" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_POSA Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_POSA(int i) {
		return getZone(getNOM_ST_POSA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_POSA Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_POSA(int i) {
		return "NOM_ST_LIB_POSA" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_POSA Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_POSA(int i) {
		return getZone(getNOM_ST_LIB_POSA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REF_ARR(int i) {
		return "NOM_ST_REF_ARR" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_REF_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REF_ARR(int i) {
		return getZone(getNOM_ST_REF_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_ARR(int i) {
		return "NOM_ST_DATE_ARR" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_ARR(int i) {
		return getZone(getNOM_ST_DATE_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 * RG_AG_PA_A05
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		DateDebutEditable = true;
		// RG_AG_PA_A05
		if (indiceEltAModifier != getListePAAgent().size() - 1) {
			DateDebutEditable = false;
		}
		PositionAdmAgent paCourante = (PositionAdmAgent) getListePAAgent().get(indiceEltAModifier);
		setPaCourante(paCourante);

		// init du diplome courant
		if (!initialisePACourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}
		PositionAdmAgent paCourante = (PositionAdmAgent) getListePAAgent().get(indiceEltAConsulter);
		setPaCourante(paCourante);

		// init
		if (!initialisePAConsulter(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}
		PositionAdmAgent paCourante = (PositionAdmAgent) getListePAAgent().get(indiceEltASuprimer);
		setPaCourante(paCourante);

		// init du diplome courant
		if (!initialisePACourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public HistoPositionAdmDao getHistoPositionAdmDao() {
		return histoPositionAdmDao;
	}

	public void setHistoPositionAdmDao(HistoPositionAdmDao histoPositionAdmDao) {
		this.histoPositionAdmDao = histoPositionAdmDao;
	}

	public MATMUTDao getMatmutDao() {
		return matmutDao;
	}

	public void setMatmutDao(MATMUTDao matmutDao) {
		this.matmutDao = matmutDao;
	}

	public MATMUTHistDao getMatmutHistDao() {
		return matmutHistDao;
	}

	public void setMatmutHistDao(MATMUTHistDao matmutHistDao) {
		this.matmutHistDao = matmutHistDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
