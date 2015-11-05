package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.HistoPrime;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.HistoPrimeDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IAvancementService;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTMasseSalarialeConvention extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_ANNEE;

	private String[] listeAnnee;

	private ArrayList<AvancementConvCol> listeAvct;

	public String ACTION_CALCUL = "Calcul";

	private AvancementConvColDao avancementConvColDao;
	private FichePosteDao fichePosteDao;
	private HistoPrimeDao histoPrimeDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	private IAdsService adsService;
	private IAvancementService avancementService;
	public String agentEnErreur = Const.CHAINE_VIDE;
	public String agentEnErreurPrime = Const.CHAINE_VIDE;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getAvancementConvColDao() == null) {
			setAvancementConvColDao(new AvancementConvColDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoPrimeDao() == null) {
			setHistoPrimeDao(new HistoPrimeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == avancementService) {
			avancementService = (IAvancementService) context.getBean("avancementService");
		}
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		initialiseListeDeroulante();

		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null) {
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
			performPB_LANCER(request);
		}
	}

	private void initialiseListeDeroulante() throws Exception {

		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_MASSE_SALARIALE");
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// Si clic sur le bouton PB_LANCER
			if (testerParametre(request, getNOM_PB_LANCER())) {
				return performPB_LANCER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CHANGER_ANNEE
			if (testerParametre(request, getNOM_PB_CHANGER_ANNEE())) {
				return performPB_CHANGER_ANNEE(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AFFECTER
			if (testerParametre(request, getNOM_PB_AFFECTER())) {
				return performPB_AFFECTER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTMasseSalarialeConvention() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeConvention.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-CONV";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_LANCER Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_PB_LANCER() {
		return "NOM_PB_LANCER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public boolean performPB_LANCER(HttpServletRequest request) throws Exception {

		// Mise à jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		String an = getListeAnnee()[0];

		// Suppression des avancements a l'etat 'Travail' de la categorie donnée
		// et de l'année
		getAvancementConvColDao().supprimerAvancementConvColTravailAvecAnnee(Integer.valueOf(an));

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		if (!performCalculConvCol(getVAL_ST_ID_SERVICE_ADS(), an, agent))
			return false;

		commitTransaction();
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT, an);

		// "INF200","Simulation effectuee"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF200"));

		return true;
	}

	/**
	 * méthode de calcul des avancements Conventions collectives.
	 * 
	 * @param codeService
	 * @param annee
	 * @param agent
	 * @throws Exception
	 */
	private boolean performCalculConvCol(String idServiceAds, String annee, Agent agent) throws Exception {
		ArrayList<Agent> la = new ArrayList<Agent>();
		if (agent != null) {
			ReturnMessageDto result = avancementService.isAvancementConventionCollective(getTransaction(), agent);
			if (result.getErrors().size() > 0) {
				String erreur = Const.CHAINE_VIDE;
				for (String err : result.getErrors()) {
					erreur += err;
				}
				getTransaction().declarerErreur(erreur);
				return false;
			}
			la.add(agent);
		} else {
			la = (ArrayList<Agent>) avancementService.listAgentAvctConvCol(getTransaction(), idServiceAds, annee, adsService, getAgentDao());
		}

		// Parcours des agents
		for (Agent a : la) {
			AvancementConvCol avct = avancementService.calculAvancementConventionCollective(getTransaction(), a, annee, adsService, getFichePosteDao(), getAffectationDao());
			if (avct == null) {
				// on informe les agents en erreur
				agentEnErreur += a.getNomAgent() + " " + a.getPrenomAgent() + " (" + a.getNomatr() + "); ";
				continue;
			} else if (avct.getIdAgent() == null) {
				// l'agent n'a pas 3 ans d'ancienneté
				continue;
			} else if (avct.getMontantPrime1200() == null) {
				// l'agent n'a pas de prime ouverte
				agentEnErreurPrime += a.getNomAgent() + " " + a.getPrenomAgent() + " (" + a.getNomatr() + "); ";
				continue;
			}
			avancementService.creerAvancementConventionCollective(avct, getAvancementConvColDao());
		}
		return true;
	}

	/**
	 * Getter de la liste des années possibles de simulation.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles de simulation.
	 * 
	 * @param listeAnnee
	 *            listeAnnee à définir
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_CHANGER_ANNEE() {
		return "NOM_PB_CHANGER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CHANGER_ANNEE(HttpServletRequest request) throws Exception {
		String annee = getListeAnnee()[0];
		setListeAvct(getAvancementConvColDao().listerAvancementConvColAvecAnnee(Integer.valueOf(annee)));

		afficherListeAvct(request);

		return true;
	}

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementConvCol av = (AvancementConvCol) getListeAvct().get(j);
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());

			addZone(getNOM_ST_GRADE(i), av.getGrade());
			addZone(getNOM_ST_GRADE_LIB(i), av.getLibGrade() == null ? "&nbsp;" : av.getLibGrade());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DATE_EMBAUCHE(i), av.getDateEmbauche() == null ? "&nbsp;" : sdf.format(av.getDateEmbauche()));
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());

			addZone(getNOM_CK_VALID_DRH(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i), "REVALORISATION");
			addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) || av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF()
					: getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i), av.getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(av.getDateArrete()));
			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue()) || av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
			addZone(getNOM_ST_MONTANT_PRIME(i), (av.getMontantPrime1200() == null || av.getMontantPrime1200().equals(Const.CHAINE_VIDE) ? "&nbsp;"
					: Integer.valueOf(av.getMontantPrime1200()) == 30 ? "30" : String.valueOf(Integer.valueOf(av.getMontantPrime1200()) - 1))
					+ " <br> " + (av.getMontantPrime1200() == null ? "&nbsp;" : av.getMontantPrime1200()));
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementConvCol avct = (AvancementConvCol) getListeAvct().get(j);
			Integer i = avct.getIdAvct();

			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else if (getVAL_CK_PROJET_ARRETE(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				} else if (getVAL_CK_VALID_DRH(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
				}
				// on traite le numero et la date d'arrete
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_EF_DATE_ARRETE(i)));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(), avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(),
					avct.getDateEmbauche(), avct.getGrade(), avct.getLibGrade(), avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(), avct.getMontantPrime1200(),
					avct.getCodePa());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementConvCol avct = (AvancementConvCol) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {

					// on crée une ligne de prime
					Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());

					// on check la si prime saisie en simu
					if (avancementService.isPrimeAvctConvColSimu(getTransaction(), agent, avct)) {
						// c'est qu'il existe une prime pour cette date

						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste a l'ecran
						agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
						// on met un 'S' dans son avancement
						avct.setCarriereSimu("S");
						getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(), avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(),
								avct.getDateEmbauche(), avct.getGrade(), avct.getLibGrade(), avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(), avct.getMontantPrime1200(),
								avct.getCodePa());
						continue;
					} else {
						avct.setCarriereSimu(null);
					}

					// alors on fait les modifs sur avancement
					avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
					addZone(getNOM_ST_ETAT(i), avct.getEtat());

					// on traite le numero et la date d'arrete
					avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_EF_DATE_ARRETE(i)));
					avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
					getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(), avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(),
							avct.getDateEmbauche(), avct.getGrade(), avct.getLibGrade(), avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(), avct.getMontantPrime1200(),
							avct.getCodePa());

					// on recherche la derniere ligne de prime pour la rubrique
					// 1200(prime anciennete)
					Prime prime = Prime.chercherDernierePrimeOuverteAvecRubrique(getTransaction(), agent.getNomatr(), "1200");
					// si il y en a une alors on la ferme et on en crée une
					// nouvelle
					if (!getTransaction().isErreur()) {
						if (!prime.getDatDeb().equals("01/01/" + avct.getAnnee())) {
							prime.setDatFin("01/01/" + avct.getAnnee());
							prime.setNoRubr(prime.getNoRubr());
							prime.setDatDeb(prime.getDatDeb());
							// RG_AG_PR_A04
							HistoPrime histo = new HistoPrime(prime);
							getHistoPrimeDao().creerHistoPrime(histo, user, EnumTypeHisto.MODIFICATION);
							prime.modifierPrime(getTransaction(), agent, user);

							Prime newPrime = avancementService.getNewPrimeConventionCollective(getTransaction(), agent, avct);
							// RG_AG_PR_A04
							HistoPrime histo2 = new HistoPrime(newPrime);
							getHistoPrimeDao().creerHistoPrime(histo2, user, EnumTypeHisto.CREATION);
							newPrime.creerPrime(getTransaction(), user);
						}
					} else {
						getTransaction().traiterErreur();

						Prime newPrime = avancementService.getNewPrimeConventionCollective(getTransaction(), agent, avct);
						// RG_AG_PR_A04
						HistoPrime histo2 = new HistoPrime(newPrime);
						getHistoPrimeDao().creerHistoPrime(histo2, user, EnumTypeHisto.CREATION);
						newPrime.creerPrime(getTransaction(), user);
					}

					if (getTransaction().isErreur()) {
						return false;
					} else {
						nbAgentAffectes += 1;
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();
		afficherListeAvct(request);

		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setListeAvct(new ArrayList<AvancementConvCol>());
		afficherListeAvct(request);
		return true;
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementConvCol> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementConvCol>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementConvCol> listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MONTANT_PRIME Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MONTANT_PRIME(int i) {
		return "NOM_ST_MONTANT_PRIME_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MONTANT_PRIME
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MONTANT_PRIME(int i) {
		return getZone(getNOM_ST_MONTANT_PRIME(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_PROJET_ARRETE(int i) {
		return getZone(getNOM_CK_PROJET_ARRETE(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_DRH(int i) {
		return "NOM_CK_VALID_DRH_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_EMBAUCHE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_EMBAUCHE(int i) {
		return "NOM_ST_DATE_EMBAUCHE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_EMBAUCHE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_EMBAUCHE(int i) {
		return getZone(getNOM_ST_DATE_EMBAUCHE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE(int i) {
		return "NOM_EF_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE(int i) {
		return getZone(getNOM_EF_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_NUM_ARRETE(int i) {
		return "NOM_EF_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_NUM_ARRETE(int i) {
		return getZone(getNOM_EF_NUM_ARRETE(i));
	}

	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public AvancementConvColDao getAvancementConvColDao() {
		return avancementConvColDao;
	}

	public void setAvancementConvColDao(AvancementConvColDao avancementConvColDao) {
		this.avancementConvColDao = avancementConvColDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public HistoPrimeDao getHistoPrimeDao() {
		return histoPrimeDao;
	}

	public void setHistoPrimeDao(HistoPrimeDao histoPrimeDao) {
		this.histoPrimeDao = histoPrimeDao;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}