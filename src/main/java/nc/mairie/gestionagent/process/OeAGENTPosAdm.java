package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PAAgent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.paye.Matricule;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Process OeAGENTPosAdm Date de création : (04/08/11 09:22:55)
 * 
 */
public class OeAGENTPosAdm extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private ArrayList<PositionAdmAgent> listePAAgent;
	private ArrayList<PositionAdm> listePA;

	private Hashtable<String, PositionAdm> hashPA;

	private AgentNW agentCourant;
	private PositionAdmAgent paCourante;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche PA.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche PA.";
	public String ACTION_MODIFICATION = "Modification d'une fiche PA.";
	public String ACTION_CREATION = "Création d'une fiche PA.";

	private String messageInf = Const.CHAINE_VIDE;
	public boolean DateDebutEditable = true;

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName("PERSONNEL", "CALCUL", "DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent.getMesParametres().get("HOST_SGBD_PAYE"),
			(String) ServletAgent.getMesParametres().get("HOST_SGBD_ADMIN"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")),
			CALC_PATH.getPath());
	private String calculPaye;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

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
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListePA(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
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
		ArrayList<PositionAdmAgent> listePAAgent = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(), getAgentCourant());
		setListePAAgent(listePAAgent);

		int indicePaAgent = 0;
		if (getListePAAgent() != null) {
			for (int i = 0; i < getListePAAgent().size(); i++) {
				PositionAdmAgent paa = (PositionAdmAgent) getListePAAgent().get(i);
				PositionAdm pa = (PositionAdm) getHashPA().get(paa.getCdpadm());

				addZone(getNOM_ST_POSA(indicePaAgent), pa.getCdpadm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : pa.getCdpadm());
				addZone(getNOM_ST_LIB_POSA(indicePaAgent), pa.getLiPAdm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : pa.getLiPAdm());
				addZone(getNOM_ST_REF_ARR(indicePaAgent), paa.getRefarr().equals(Const.CHAINE_VIDE) ? "&nbsp;" : paa.getRefarr());
				addZone(getNOM_ST_DATE_ARR(indicePaAgent), paa.getDateArrete() == null ? "&nbsp;" : paa.getDateArrete());
				addZone(getNOM_ST_DATE_DEBUT(indicePaAgent), paa.getDatdeb() == null ? "&nbsp;" : paa.getDatdeb());
				addZone(getNOM_ST_DATE_FIN(indicePaAgent), paa.getDatfin() == null ? "&nbsp;" : paa.getDatfin());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
			PositionAdmAgent posAdmPrec = PositionAdmAgent.chercherPositionAdmAgentPrec(getTransaction(), getAgentCourant().getNoMatricule(),
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
				getLastPA() != null && getLastPA().getDatfin() != null ? Services.ajouteJours(Services.formateDate(getLastPA().getDatfin()), 1)
						: Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_POSA(), Const.CHAINE_VIDE);
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
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
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_REF_ARR())) && !Services.estNumerique(getZone(getNOM_EF_REF_ARR()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Ref. arrêté"));
			return false;
		}

		// date de l'arrêté
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_ARR())) && !Services.estUneDate(getZone(getNOM_EF_DATE_ARR()))) {
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
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN())) && !Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
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
				if (lastPA.getDatfin() != null && Services.compareDates(lastPA.getDatfin(), getPaCourante().getDatdeb()) > 0) {
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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

			// Suppression du lien
			PAAgent paAgent = PAAgent.chercherPAAgent(getTransaction(), getAgentCourant().idAgent, getAgentCourant().getNoMatricule(),
					getPaCourante().getDatdeb());
			paAgent.supprimerPAAgent(getTransaction());

			PositionAdmAgent prev = getPrevPA();
			// RG_AG_PA_A07
			// RG_AG_PA_A08
			if (prev != null) {
				prev.setDatfin(null);
				prev.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user);

				if (getPaCourante().estPAInactive(getTransaction()) && !prev.estPAInactive(getTransaction()))
					messageInf = MessageUtils.getMessage("INF006");
			}

			// suppression
			getPaCourante().supprimerPositionAdmAgent(getTransaction(), user);
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// récupération des informations remplies dans les zones de saisie
			String refArr = getZone(getNOM_EF_REF_ARR());
			String dateFin = Services.formateDate(getZone(getNOM_EF_DATE_FIN()));
			String dateDebut = Services.formateDate(getZone(getNOM_EF_DATE_DEBUT()));
			String dateArrete = Services.formateDate(getZone(getNOM_EF_DATE_ARR()));

			PositionAdm pa = getSelectedPA();

			// Création de l'objet PositionAdministrative à créer/modifier
			AgentNW agentCourant = getAgentCourant();
			getPaCourante().setNomatr(agentCourant.getNoMatricule());
			getPaCourante().setCdpadm(pa.getCdpadm());
			getPaCourante().setRefarr(refArr);
			getPaCourante().setDatdeb(dateDebut);
			getPaCourante().setDatfin(dateFin);
			getPaCourante().setDateArrete(dateArrete);

			// règles de gestions
			if (!checkChevauchement())
				return false;

			PositionAdmAgent prevPA = getPrevPA();
			// RG_AG_PA_A03
			// RG_AG_PA_A06
			if (prevPA != null && (prevPA.getDatfin() == null || Services.compareDates(prevPA.getDatfin(), getPaCourante().getDatdeb()) != 0)) {
				prevPA.setDatfin(getPaCourante().getDatdeb());
				if (!prevPA.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			}

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				if (!getPaCourante().modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				if (getPaCourante().estPAInactive(getTransaction())) {
					// RG_AG_PA_C03
					ArrayList<Affectation> affActive = Affectation.listerAffectationActiveAvecAgent(getTransaction(), getAgentCourant());

					if (affActive.size() > 0) {
						Affectation active = (Affectation) affActive.get(0);
						if (Services.compareDates(active.getDateDebutAff(), getPaCourante().getDatdeb()) > 0) {
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
							return false;
						}

						// si l'affectation a une date de fin
						if (!active.getDateFinAff().equals(Const.CHAINE_VIDE)) {
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
							return false;
						}
						FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), active.getIdFichePoste());
						active.setDateFinAff(getPaCourante().getDatdeb());
						if (!active.modifierAffectation(getTransaction(), user, getAgentCourant(), fp)) {
							// "ERR009",
							// "Une erreur s'est produite sur la base de données.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
							return false;
						}

					}
				}

				if (!getPaCourante().creerPositionAdmAgent(getTransaction(), user)) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}

				PAAgent paAgent = new PAAgent(getAgentCourant().getIdAgent(), getAgentCourant().getNoMatricule(), getPaCourante().getDatdeb());
				if (!paAgent.creerPAAgent(getTransaction())) {
					// "ERR009",
					// "Une erreur s'est produite sur la base de données.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
					return false;
				}
			}
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_POSA Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
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

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
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
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (18/10/11 12:00:03)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTPosAdm.jsp";
	}

	/**
	 * Fourni le noom de l'écran utilisé pour la gestion des droits
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_POSA
	 * Date de création : (30/08/11 10:25:41)
	 * 
	 */
	public String getVAL_EF_POSA() {
		return getZone(getNOM_EF_POSA());
	}

	/**
	 * Récupere la rubrique selectionnée
	 * 
	 * @return
	 * @throws Exception
	 */
	private PositionAdm getSelectedPA() throws Exception {
		// récupération de la rubrique et vérification de son existence.
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_POSA Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_POSA Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_ARR Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ARR Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
}
