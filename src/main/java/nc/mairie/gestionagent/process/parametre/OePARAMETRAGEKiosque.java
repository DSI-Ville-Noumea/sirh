package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.parametrage.ReferentRhDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGEElection Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEKiosque extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	public static final int STATUT_RECHERCHER_AGENT_CREATE = 1;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private String[] LB_REFERENT_RH;
	private String[] LB_SERVICE_AUTRES;
	private String[] LB_SERVICE_UTILISATEUR;

	private ArrayList<Service> listeServiceAutres;
	private ArrayList<Service> listeServiceUtilisateur;

	private ReferentRhDao referentRhDao;
	private AgentDao agentDao;

	private ReferentRh referentRhCourant;
	private ArrayList<ReferentRh> listeReferentRh;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_EF_ID_REFERENT_RH(), agt.getNomatr().toString());
			}
		}

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		if (getListeReferentRh().size() == 0) {
			initialiseListeReferentRh(request);
		}

		// Initialisation des Services

		if (getListeServiceUtilisateur().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Service> list = getListeServiceUtilisateur().listIterator(); list.hasNext();) {
				Service de = (Service) list.next();
				String ligne[] = { de.getSigleService() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_SERVICE_UTILISATEUR(aFormat.getListeFormatee());
		} else {
			setLB_SERVICE_UTILISATEUR(null);
		}

		if (getListeServiceAutres().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Service> list = getListeServiceAutres().listIterator(); list.hasNext();) {
				Service de = (Service) list.next();
				String ligne[] = { de.getSigleService() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_SERVICE_AUTRES(aFormat.getListeFormatee());
		} else {
			setLB_SERVICE_AUTRES(null);
		}

	}

	private void initialiseListeReferentRh(HttpServletRequest request) throws Exception {
		setListeReferentRh((ArrayList<ReferentRh>) getReferentRhDao().listerDistinctReferentRh());
		if (getListeReferentRh().size() != 0) {
			int tailles[] = { 90 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<ReferentRh> list = getListeReferentRh().listIterator(); list.hasNext();) {
				ReferentRh ref = (ReferentRh) list.next();
				Agent ag = getAgentDao().chercherAgent(ref.getIdAgentReferent());
				String ligne[] = { ag.getNomAgent() + " " + ag.getPrenomAgent() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_REFERENT_RH(aFormat.getListeFormatee());
		} else {
			setLB_REFERENT_RH(null);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getReferentRhDao() == null) {
			setReferentRhDao(new ReferentRhDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	public String getNOM_ST_ACTION_REFERENT_RH() {
		return "NOM_ST_ACTION_REFERENT_RH";
	}

	public String getVAL_ST_ACTION_REFERENT_RH() {
		return getZone(getNOM_ST_ACTION_REFERENT_RH());
	}

	public String getNOM_PB_VALIDER_REFERENT_RH() {
		return "NOM_PB_VALIDER_REFERENT_RH";
	}

	public boolean performPB_VALIDER_REFERENT_RH(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieReferentRh(request))
			return false;

		if (!performControlerRegleGestionReferentRh(request))
			return false;

		if (getVAL_ST_ACTION_REFERENT_RH() != null && getVAL_ST_ACTION_REFERENT_RH() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_CREATION)) {
				// on verifie que l'agent existe
				try {
					Agent ag = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_EF_ID_REFERENT_RH()));
					// on crée les entrées
					for (Service serv : getListeServiceUtilisateur()) {
						getReferentRhCourant().setIdAgentReferent(ag.getIdAgent());
						getReferentRhCourant().setNumeroTelephone(Integer.valueOf(getVAL_EF_NUMERO_TELEPHONE()));
						getReferentRhCourant().setServi(serv.getCodService());
						getReferentRhDao().creerReferentRh(getReferentRhCourant().getServi(),
								getReferentRhCourant().getIdAgentReferent(),
								getReferentRhCourant().getNumeroTelephone());
					}
				} catch (Exception e) {
					// "ERR503",
					// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", getVAL_EF_ID_REFERENT_RH()));
					return false;
				}
			} else if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_SUPPRESSION)) {
				// on supprime toutes les entrées
				for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(
						getReferentRhCourant().getIdAgentReferent())) {
					getReferentRhDao().supprimerReferentRh(ref.getServi());
				}
			} else if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_MODIFICATION)) {
				// on supprime toutes les entrées
				for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(
						getReferentRhCourant().getIdAgentReferent())) {
					getReferentRhDao().supprimerReferentRh(ref.getServi());
				}
				// on crée les entrées
				for (Service serv : getListeServiceUtilisateur()) {
					setReferentRhCourant(new ReferentRh());
					getReferentRhCourant().setIdAgentReferent(Integer.valueOf("900" + getVAL_EF_ID_REFERENT_RH()));
					getReferentRhCourant().setNumeroTelephone(Integer.valueOf(getVAL_EF_NUMERO_TELEPHONE()));
					getReferentRhCourant().setServi(serv.getCodService());
					getReferentRhDao().creerReferentRh(getReferentRhCourant().getServi(),
							getReferentRhCourant().getIdAgentReferent(), getReferentRhCourant().getNumeroTelephone());
				}
			}
			initialiseListeReferentRh(request);
			setReferentRhCourant(null);
			addZone(getNOM_ST_ACTION_REFERENT_RH(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	private boolean performControlerRegleGestionReferentRh(HttpServletRequest request) {

		// Vérification des contraintes d'unicité du référent
		if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_CREATION)) {

			for (ReferentRh repre : getListeReferentRh()) {
				if (repre.getIdAgentReferent().toString().equals("900" + getVAL_EF_ID_REFERENT_RH().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un référent", "ce matricule"));
					return false;
				}
			}
		}

		return true;
	}

	private boolean performControlerSaisieReferentRh(HttpServletRequest request) throws Exception {
		// Verification agent not null
		if (getZone(getNOM_EF_ID_REFERENT_RH()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		}

		// format agent
		if (!Services.estNumerique(getVAL_EF_ID_REFERENT_RH())) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "agent"));
			return false;
		}
		// Verification numero téléphone not null
		if (getZone(getNOM_EF_NUMERO_TELEPHONE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "téléphone"));
			return false;
		}

		// format numero téléphone
		if (!Services.estNumerique(getVAL_EF_NUMERO_TELEPHONE())) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "téléphone"));
			return false;
		}
		return true;
	}

	private boolean initialiserAgentSelectionne() throws Exception {
		// On vide la zone de saisie
		viderFormulaire();
		Agent ag = getAgentDao().chercherAgent(getReferentRhCourant().getIdAgentReferent());
		addZone(getNOM_EF_ID_REFERENT_RH(), ag.getNomatr().toString());
		addZone(getNOM_EF_NOM_REFERENT_RH(), ag.getNomAgent() + " " + ag.getPrenomAgent());
		addZone(getNOM_EF_NUMERO_TELEPHONE(), getReferentRhCourant().getNumeroTelephone().toString());

		setListeServiceAutres(Service.listerServiceActifAvecCodeService(getTransaction()));
		ArrayList<Service> listeServ = new ArrayList<Service>();
		for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(
				getReferentRhCourant().getIdAgentReferent())) {
			Service serv = Service.chercherService(getTransaction(), ref.getServi());
			listeServ.add(serv);
		}
		setListeServiceUtilisateur(listeServ);
		// on enleve les service de l'agent deja existant
		getListeServiceAutres().removeAll(getListeServiceUtilisateur());

		return true;
	}

	public String getNOM_PB_MODIFIER_REFERENT_RH() {
		return "NOM_PB_MODIFIER_REFERENT_RH";
	}

	public boolean performPB_MODIFIER_REFERENT_RH(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_REFERENT_RH_SELECT()) ? Integer
				.parseInt(getVAL_LB_REFERENT_RH_SELECT()) : -1);
		if (indice != -1 && indice < getListeReferentRh().size()) {
			ReferentRh ref = getListeReferentRh().get(indice);
			setReferentRhCourant(ref);
			initialiserAgentSelectionne();
			addZone(getNOM_ST_ACTION_REFERENT_RH(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "référents"));
		}

		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_REFERENT_RH() {
		return "NOM_PB_SUPPRIMER_REFERENT_RH";
	}

	public boolean performPB_SUPPRIMER_REFERENT_RH(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_REFERENT_RH_SELECT()) ? Integer
				.parseInt(getVAL_LB_REFERENT_RH_SELECT()) : -1);
		if (indice != -1 && indice < getListeReferentRh().size()) {
			ReferentRh ref = getListeReferentRh().get(indice);
			setReferentRhCourant(ref);
			initialiserAgentSelectionne();
			addZone(getNOM_ST_ACTION_REFERENT_RH(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "référents"));
		}

		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;

	}

	public String getNOM_PB_CREER_REFERENT_RH() {
		return "NOM_PB_CREER_REFERENT_RH";
	}

	public boolean performPB_CREER_REFERENT_RH(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_REFERENT_RH(), ACTION_CREATION);

		// On vide la zone de saisie
		viderFormulaire();
		setReferentRhCourant(new ReferentRh());
		setListeServiceAutres(Service.listerServiceActifAvecCodeService(getTransaction()));

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	private void viderFormulaire() {
		addZone(getNOM_EF_ID_REFERENT_RH(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_REFERENT_RH(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUMERO_TELEPHONE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT(), "-1");
		addZone(getNOM_LB_SERVICE_AUTRES_SELECT(), "-1");

		setListeServiceAutres(null);
		setListeServiceUtilisateur(null);
	}

	public String getNOM_PB_ANNULER_REFERENT_RH() {
		return "NOM_PB_ANNULER_REFERENT_RH";
	}

	public boolean performPB_ANNULER_REFERENT_RH(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_REFERENT_RH(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	public OePARAMETRAGEKiosque() {
		super();
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_REFERENT_RH
			if (testerParametre(request, getNOM_PB_ANNULER_REFERENT_RH())) {
				return performPB_ANNULER_REFERENT_RH(request);
			}

			// Si clic sur le bouton PB_CREER_REFERENT_RH
			if (testerParametre(request, getNOM_PB_CREER_REFERENT_RH())) {
				return performPB_CREER_REFERENT_RH(request);
			}

			// Si clic sur le bouton PB_MODIFIER_REFERENT_RH
			if (testerParametre(request, getNOM_PB_MODIFIER_REFERENT_RH())) {
				return performPB_MODIFIER_REFERENT_RH(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REFERENT_RH
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REFERENT_RH())) {
				return performPB_SUPPRIMER_REFERENT_RH(request);
			}

			// Si clic sur le bouton PB_VALIDER_REFERENT_RH
			if (testerParametre(request, getNOM_PB_VALIDER_REFERENT_RH())) {
				return performPB_VALIDER_REFERENT_RH(request);
			}

			// Si clic sur le bouton PB_AJOUTER_SERVICE
			if (testerParametre(request, getNOM_PB_AJOUTER_SERVICE())) {
				return performPB_AJOUTER_SERVICE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_TOUT
			if (testerParametre(request, getNOM_PB_AJOUTER_TOUT())) {
				return performPB_AJOUTER_TOUT(request);
			}

			// Si clic sur le bouton PB_RETIRER_SERVICE
			if (testerParametre(request, getNOM_PB_RETIRER_SERVICE())) {
				return performPB_RETIRER_SERVICE(request);
			}

			// Si clic sur le bouton PB_RETIRER_TOUT
			if (testerParametre(request, getNOM_PB_RETIRER_TOUT())) {
				return performPB_RETIRER_TOUT(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEKiosque.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-KIOSQUE";
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	private String[] getLB_REFERENT_RH() {
		if (LB_REFERENT_RH == null)
			LB_REFERENT_RH = initialiseLazyLB();
		return LB_REFERENT_RH;
	}

	private void setLB_REFERENT_RH(String[] newLB_REFERENT_RH) {
		LB_REFERENT_RH = newLB_REFERENT_RH;
	}

	public String getNOM_LB_REFERENT_RH() {
		return "NOM_LB_REFERENT_RH";
	}

	public String getNOM_LB_REFERENT_RH_SELECT() {
		return "NOM_LB_REFERENT_RH_SELECT";
	}

	public String[] getVAL_LB_REFERENT_RH() {
		return getLB_REFERENT_RH();
	}

	public String getVAL_LB_REFERENT_RH_SELECT() {
		return getZone(getNOM_LB_REFERENT_RH_SELECT());
	}

	public ReferentRhDao getReferentRhDao() {
		return referentRhDao;
	}

	public void setReferentRhDao(ReferentRhDao referentRhDao) {
		this.referentRhDao = referentRhDao;
	}

	public ArrayList<ReferentRh> getListeReferentRh() {
		if (listeReferentRh == null)
			return new ArrayList<ReferentRh>();
		return listeReferentRh;
	}

	public void setListeReferentRh(ArrayList<ReferentRh> listeReferentRh) {
		this.listeReferentRh = listeReferentRh;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public ReferentRh getReferentRhCourant() {
		return referentRhCourant;
	}

	public void setReferentRhCourant(ReferentRh referentRhCourant) {
		this.referentRhCourant = referentRhCourant;
	}

	private String[] getLB_SERVICE_AUTRES() {
		if (LB_SERVICE_AUTRES == null)
			LB_SERVICE_AUTRES = initialiseLazyLB();
		return LB_SERVICE_AUTRES;
	}

	private void setLB_SERVICE_AUTRES(String[] newLB_SERVICE_AUTRES) {
		LB_SERVICE_AUTRES = newLB_SERVICE_AUTRES;
	}

	public String getNOM_LB_SERVICE_AUTRES() {
		return "NOM_LB_SERVICE_AUTRES";
	}

	public String getNOM_LB_SERVICE_AUTRES_SELECT() {
		return "NOM_LB_SERVICE_AUTRES_SELECT";
	}

	public String[] getVAL_LB_SERVICE_AUTRES() {
		return getLB_SERVICE_AUTRES();
	}

	public String getVAL_LB_SERVICE_AUTRES_SELECT() {
		return getZone(getNOM_LB_SERVICE_AUTRES_SELECT());
	}

	private String[] getLB_SERVICE_UTILISATEUR() {
		if (LB_SERVICE_UTILISATEUR == null)
			LB_SERVICE_UTILISATEUR = initialiseLazyLB();
		return LB_SERVICE_UTILISATEUR;
	}

	private void setLB_SERVICE_UTILISATEUR(String[] newLB_SERVICE_UTILISATEUR) {
		LB_SERVICE_UTILISATEUR = newLB_SERVICE_UTILISATEUR;
	}

	public String getNOM_LB_SERVICE_UTILISATEUR() {
		return "NOM_LB_SERVICE_UTILISATEUR";
	}

	public String getNOM_LB_SERVICE_UTILISATEUR_SELECT() {
		return "NOM_LB_SERVICE_UTILISATEUR_SELECT";
	}

	public String[] getVAL_LB_SERVICE_UTILISATEUR() {
		return getLB_SERVICE_UTILISATEUR();
	}

	public String getVAL_LB_SERVICE_UTILISATEUR_SELECT() {
		return getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT());
	}

	public String getNOM_PB_AJOUTER_SERVICE() {
		return "NOM_PB_AJOUTER_SERVICE";
	}

	public boolean performPB_AJOUTER_SERVICE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_SERVICE_AUTRES_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_SERVICE_AUTRES_SELECT())) : -1);
		if (numLigne == -1 || getListeServiceAutres().size() == 0 || numLigne > getListeServiceAutres().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres services"));
			return false;
		}

		Service groupe = (Service) getListeServiceAutres().get(numLigne);
		getListeServiceAutres().remove(groupe);
		getListeServiceUtilisateur().add(groupe);

		return true;
	}

	public String getNOM_PB_AJOUTER_TOUT() {
		return "NOM_PB_AJOUTER_TOUT";
	}

	public boolean performPB_AJOUTER_TOUT(HttpServletRequest request) throws Exception {

		getListeServiceUtilisateur().addAll(getListeServiceAutres());
		getListeServiceAutres().clear();

		return true;
	}

	public String getNOM_PB_RETIRER_SERVICE() {
		return "NOM_PB_RETIRER_SERVICE";
	}

	public boolean performPB_RETIRER_SERVICE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) : -1);
		if (numLigne == -1 || getListeServiceUtilisateur().size() == 0
				|| numLigne > getListeServiceUtilisateur().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Services de l'utilisateur"));
			return false;
		}

		Service groupe = (Service) getListeServiceUtilisateur().get(numLigne);
		getListeServiceUtilisateur().remove(groupe);
		getListeServiceAutres().add(groupe);

		return true;
	}

	public String getNOM_PB_RETIRER_TOUT() {
		return "NOM_PB_RETIRER_TOUT";
	}

	public boolean performPB_RETIRER_TOUT(HttpServletRequest request) throws Exception {

		getListeServiceAutres().addAll(getListeServiceUtilisateur());
		getListeServiceUtilisateur().clear();

		return true;
	}

	private ArrayList<Service> getListeServiceUtilisateur() {
		if (listeServiceUtilisateur == null)
			listeServiceUtilisateur = new ArrayList<Service>();
		return listeServiceUtilisateur;
	}

	private void setListeServiceUtilisateur(ArrayList<Service> listeGroupesUtilisateur) {
		this.listeServiceUtilisateur = listeGroupesUtilisateur;
	}

	private ArrayList<Service> getListeServiceAutres() {
		if (listeServiceAutres == null)
			listeServiceAutres = new ArrayList<Service>();
		return listeServiceAutres;
	}

	private void setListeServiceAutres(ArrayList<Service> listeGroupesAutres) {
		this.listeServiceAutres = listeGroupesAutres;
	}

	public String getNOM_EF_ID_REFERENT_RH() {
		return "NOM_EF_ID_REFERENT_RH";
	}

	public String getVAL_EF_ID_REFERENT_RH() {
		return getZone(getNOM_EF_ID_REFERENT_RH());
	}

	public String getNOM_EF_NUMERO_TELEPHONE() {
		return "NOM_EF_NUMERO_TELEPHONE";
	}

	public String getVAL_EF_NUMERO_TELEPHONE() {
		return getZone(getNOM_EF_NUMERO_TELEPHONE());
	}

	public String getNOM_EF_NOM_REFERENT_RH() {
		return "NOM_EF_NOM_REFERENT_RH";
	}

	public String getVAL_EF_NOM_REFERENT_RH() {
		return getZone(getNOM_EF_NOM_REFERENT_RH());
	}

	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATE, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_EF_ID_REFERENT_RH(), Const.CHAINE_VIDE);
		return true;
	}
}
