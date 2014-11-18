package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.parametrage.AccueilKiosque;
import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.parametrage.AccueilKiosqueDao;
import nc.mairie.spring.dao.metier.parametrage.ReferentRhDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
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
	public static final int STATUT_RECHERCHER_AGENT_GLOBAL = 2;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private String[] LB_REFERENT_RH;
	private String[] LB_SERVICE_UTILISATEUR;
	private String[] LB_TEXTE_KIOSQUE;

	public Hashtable<String, TreeHierarchy> hTree = null;
	private ArrayList<Service> listeServices;
	private ArrayList<Service> listeServiceUtilisateur;

	private ReferentRhDao referentRhDao;
	private AccueilKiosqueDao accueilKiosqueDao;
	private AgentDao agentDao;

	private ReferentRh referentRhGlobalCourant;
	private ReferentRh referentRhCourant;
	private ArrayList<ReferentRh> listeReferentRh;

	private AccueilKiosque accueilKiosqueCourant;
	private ArrayList<AccueilKiosque> listeAccueilKiosque;

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

		if (etatStatut() == STATUT_RECHERCHER_AGENT_GLOBAL) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_EF_ID_REFERENT_RH_GLOBAL(), agt.getNomatr().toString());
			}
		}

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		if (getReferentRhGlobalCourant() == null) {
			initialiseReferentRhGlobal(request);
		}

		if (getListeReferentRh().size() == 0) {
			initialiseListeReferentRh(request);
		}
		if (getListeAccueilKiosque().size() == 0) {
			initialiseListeAccueilKiosque(request);
		}
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().isEmpty()) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			hTree = new Hashtable<>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService())) {
					continue;
				}

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}

		// Initialisation des Services
		if (getListeServiceUtilisateur().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Service> list = getListeServiceUtilisateur().listIterator(); list.hasNext();) {
				Service de = (Service) list.next();
				String ligne[] = { de.getSigleService() + " " + de.getLibService() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_SERVICE_UTILISATEUR(aFormat.getListeFormatee());
		} else {
			setLB_SERVICE_UTILISATEUR(null);
		}

	}

	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	private void initialiseReferentRhGlobal(HttpServletRequest request) {
		try {
			setReferentRhGlobalCourant(getReferentRhDao().getReferentRhGlobal());
			Agent ag = getAgentDao().chercherAgent(getReferentRhGlobalCourant().getIdAgentReferent());
			addZone(getNOM_EF_ID_REFERENT_RH_GLOBAL(), ag.getNomatr().toString());
			addZone(getNOM_EF_NUMERO_TELEPHONE_GLOBAL(), getReferentRhGlobalCourant().getNumeroTelephone().toString());
		} catch (Exception e) {
			// aucun referent trouvé
			setReferentRhGlobalCourant(new ReferentRh());
			addZone(getNOM_EF_ID_REFERENT_RH_GLOBAL(), Const.CHAINE_VIDE);
			addZone(getNOM_EF_NUMERO_TELEPHONE_GLOBAL(), Const.CHAINE_VIDE);
		}
	}

	private void initialiseListeAccueilKiosque(HttpServletRequest request) throws Exception {
		setListeAccueilKiosque((ArrayList<AccueilKiosque>) getAccueilKiosqueDao().getAccueilKiosque());
		if (getListeAccueilKiosque().size() != 0) {
			int tailles[] = { 90 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<AccueilKiosque> list = getListeAccueilKiosque().listIterator(); list.hasNext();) {
				AccueilKiosque ref = (AccueilKiosque) list.next();
				String ligne[] = { ref.getTexteAccueilKiosque() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TEXTE_KIOSQUE(aFormat.getListeFormatee());
		} else {
			setLB_TEXTE_KIOSQUE(null);
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
		if (getAccueilKiosqueDao() == null) {
			setAccueilKiosqueDao(new AccueilKiosqueDao((SirhDao) context.getBean("sirhDao")));
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
					getReferentRhDao().supprimerReferentRh(ref.getIdReferentRh());
				}
			} else if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_MODIFICATION)) {
				// on supprime toutes les entrées
				for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(
						getReferentRhCourant().getIdAgentReferent())) {
					getReferentRhDao().supprimerReferentRh(ref.getIdReferentRh());
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

		ArrayList<Service> listeServ = new ArrayList<Service>();
		for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(
				getReferentRhCourant().getIdAgentReferent())) {
			Service serv = Service.chercherService(getTransaction(), ref.getServi());
			listeServ.add(serv);
		}
		setListeServiceUtilisateur(listeServ);

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

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	private void viderFormulaire() {
		addZone(getNOM_EF_ID_REFERENT_RH(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_REFERENT_RH(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUMERO_TELEPHONE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT(), "-1");
		addZone(getNOM_EF_TEXTE_KIOSQUE(), Const.CHAINE_VIDE);

		setListeServiceUtilisateur(null);
	}

	public String getNOM_PB_ANNULER_REFERENT_RH() {
		return "NOM_PB_ANNULER_REFERENT_RH";
	}

	public boolean performPB_ANNULER_REFERENT_RH(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_REFERENT_RH(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		viderFormulaire();
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
			// Si clic sur le bouton PB_ANNULER_TEXTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_ANNULER_TEXTE_KIOSQUE())) {
				return performPB_ANNULER_TEXTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_CREER_TEXTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_CREER_TEXTE_KIOSQUE())) {
				return performPB_CREER_TEXTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_TEXTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_MODIFIER_TEXTE_KIOSQUE())) {
				return performPB_MODIFIER_TEXTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TEXTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TEXTE_KIOSQUE())) {
				return performPB_SUPPRIMER_TEXTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TEXTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_VALIDER_TEXTE_KIOSQUE())) {
				return performPB_VALIDER_TEXTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER_REFERENT_RH_GLOBAL
			if (testerParametre(request, getNOM_PB_VALIDER_REFERENT_RH_GLOBAL())) {
				return performPB_VALIDER_REFERENT_RH_GLOBAL(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_GLOBAL
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_GLOBAL())) {
				return performPB_RECHERCHER_AGENT_GLOBAL(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL(request);
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
		// Recup du service sélectionné
		String codServ = getVAL_EF_CODESERVICE();

		if (codServ.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres services"));
			return false;
		}
		Service serv = Service.chercherService(getTransaction(), codServ);
		getListeServiceUtilisateur().add(serv);

		return true;
	}

	public String getNOM_PB_AJOUTER_TOUT() {
		return "NOM_PB_AJOUTER_TOUT";
	}

	public boolean performPB_AJOUTER_TOUT(HttpServletRequest request) throws Exception {
		// Recup du service sélectionné
		String codServ = getVAL_EF_CODESERVICE();

		if (codServ.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres services"));
			return false;
		}

		// On recupere les sous services
		Service serv = Service.chercherService(getTransaction(), codServ);
		ArrayList<String> listeSousServ = Service.listSousService(getTransaction(), serv.getSigleService());

		// On ajoute le service et les sous services
		for (String codeServSele : listeSousServ) {
			Service sousServ = Service.chercherService(getTransaction(), codeServSele);
			getListeServiceUtilisateur().add(sousServ);
		}

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

		return true;
	}

	public String getNOM_PB_RETIRER_TOUT() {
		return "NOM_PB_RETIRER_TOUT";
	}

	public boolean performPB_RETIRER_TOUT(HttpServletRequest request) throws Exception {
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

	private String[] getLB_TEXTE_KIOSQUE() {
		if (LB_TEXTE_KIOSQUE == null)
			LB_TEXTE_KIOSQUE = initialiseLazyLB();
		return LB_TEXTE_KIOSQUE;
	}

	private void setLB_TEXTE_KIOSQUE(String[] newLB_TEXTE_KIOSQUE) {
		LB_TEXTE_KIOSQUE = newLB_TEXTE_KIOSQUE;
	}

	public String getNOM_LB_TEXTE_KIOSQUE() {
		return "NOM_LB_TEXTE_KIOSQUE";
	}

	public String getNOM_LB_TEXTE_KIOSQUE_SELECT() {
		return "NOM_LB_TEXTE_KIOSQUE_SELECT";
	}

	public String[] getVAL_LB_TEXTE_KIOSQUE() {
		return getLB_TEXTE_KIOSQUE();
	}

	public String getVAL_LB_TEXTE_KIOSQUE_SELECT() {
		return getZone(getNOM_LB_TEXTE_KIOSQUE_SELECT());
	}

	public String getNOM_PB_MODIFIER_TEXTE_KIOSQUE() {
		return "NOM_PB_MODIFIER_TEXTE_KIOSQUE";
	}

	public boolean performPB_MODIFIER_TEXTE_KIOSQUE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TEXTE_KIOSQUE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TEXTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAccueilKiosque().size()) {
			AccueilKiosque ref = getListeAccueilKiosque().get(indice);
			setAccueilKiosqueCourant(ref);
			addZone(getNOM_EF_TEXTE_KIOSQUE(), getAccueilKiosqueCourant().getTexteAccueilKiosque());
			addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "texte d'accueil"));
		}

		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_TEXTE_KIOSQUE() {
		return "NOM_PB_SUPPRIMER_TEXTE_KIOSQUE";
	}

	public boolean performPB_SUPPRIMER_TEXTE_KIOSQUE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_TEXTE_KIOSQUE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TEXTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAccueilKiosque().size()) {
			AccueilKiosque ref = getListeAccueilKiosque().get(indice);
			setAccueilKiosqueCourant(ref);
			addZone(getNOM_EF_TEXTE_KIOSQUE(), getAccueilKiosqueCourant().getTexteAccueilKiosque());
			addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "texte d'accueil"));
		}

		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;

	}

	public String getNOM_PB_CREER_TEXTE_KIOSQUE() {
		return "NOM_PB_CREER_TEXTE_KIOSQUE";
	}

	public boolean performPB_CREER_TEXTE_KIOSQUE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), ACTION_CREATION);

		// On vide la zone de saisie
		setAccueilKiosqueCourant(new AccueilKiosque());

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;
	}

	public String getNOM_ST_ACTION_TEXTE_KIOSQUE() {
		return "NOM_ST_ACTION_TEXTE_KIOSQUE";
	}

	public String getVAL_ST_ACTION_TEXTE_KIOSQUE() {
		return getZone(getNOM_ST_ACTION_TEXTE_KIOSQUE());
	}

	public String getNOM_PB_ANNULER_TEXTE_KIOSQUE() {
		return "NOM_PB_ANNULER_TEXTE_KIOSQUE";
	}

	public boolean performPB_ANNULER_TEXTE_KIOSQUE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), Const.CHAINE_VIDE);
		setAccueilKiosqueCourant(null);
		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;
	}

	public String getNOM_PB_VALIDER_TEXTE_KIOSQUE() {
		return "NOM_PB_VALIDER_TEXTE_KIOSQUE";
	}

	public boolean performPB_VALIDER_TEXTE_KIOSQUE(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_TEXTE_KIOSQUE() != null && getVAL_ST_ACTION_TEXTE_KIOSQUE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(ACTION_CREATION)) {
				getAccueilKiosqueCourant().setTexteAccueilKiosque(getVAL_EF_TEXTE_KIOSQUE());
				getAccueilKiosqueDao().creerAccueilKiosque(getAccueilKiosqueCourant().getTexteAccueilKiosque());
			} else if (getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(ACTION_SUPPRESSION)) {
				getAccueilKiosqueDao().supprimerAccueilKiosque(getAccueilKiosqueCourant().getIdAccueilKiosque());
			} else if (getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(ACTION_MODIFICATION)) {
				getAccueilKiosqueCourant().setTexteAccueilKiosque(getVAL_EF_TEXTE_KIOSQUE());
				getAccueilKiosqueDao().modifierAccueilKiosque(getAccueilKiosqueCourant().getIdAccueilKiosque(),
						getAccueilKiosqueCourant().getTexteAccueilKiosque());

			}
			initialiseListeAccueilKiosque(request);
			setAccueilKiosqueCourant(null);
			addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;
	}

	public String getNOM_EF_TEXTE_KIOSQUE() {
		return "NOM_EF_TEXTE_KIOSQUE";
	}

	public String getVAL_EF_TEXTE_KIOSQUE() {
		return getZone(getNOM_EF_TEXTE_KIOSQUE());
	}

	public AccueilKiosqueDao getAccueilKiosqueDao() {
		return accueilKiosqueDao;
	}

	public void setAccueilKiosqueDao(AccueilKiosqueDao accueilKiosqueDao) {
		this.accueilKiosqueDao = accueilKiosqueDao;
	}

	public AccueilKiosque getAccueilKiosqueCourant() {
		return accueilKiosqueCourant;
	}

	public void setAccueilKiosqueCourant(AccueilKiosque accueilKiosqueCourant) {
		this.accueilKiosqueCourant = accueilKiosqueCourant;
	}

	public ArrayList<AccueilKiosque> getListeAccueilKiosque() {
		if (listeAccueilKiosque == null)
			return new ArrayList<AccueilKiosque>();
		return listeAccueilKiosque;
	}

	public void setListeAccueilKiosque(ArrayList<AccueilKiosque> listeAccueilKiosque) {
		this.listeAccueilKiosque = listeAccueilKiosque;
	}

	public String getNOM_EF_ID_REFERENT_RH_GLOBAL() {
		return "NOM_EF_ID_REFERENT_RH_GLOBAL";
	}

	public String getVAL_EF_ID_REFERENT_RH_GLOBAL() {
		return getZone(getNOM_EF_ID_REFERENT_RH_GLOBAL());
	}

	public String getNOM_PB_RECHERCHER_AGENT_GLOBAL() {
		return "NOM_PB_RECHERCHER_AGENT_GLOBAL";
	}

	public boolean performPB_RECHERCHER_AGENT_GLOBAL(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_GLOBAL, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_EF_ID_REFERENT_RH_GLOBAL(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_EF_NUMERO_TELEPHONE_GLOBAL() {
		return "NOM_EF_NUMERO_TELEPHONE_GLOBAL";
	}

	public String getVAL_EF_NUMERO_TELEPHONE_GLOBAL() {
		return getZone(getNOM_EF_NUMERO_TELEPHONE_GLOBAL());
	}

	public String getNOM_PB_VALIDER_REFERENT_RH_GLOBAL() {
		return "NOM_PB_VALIDER_REFERENT_RH_GLOBAL";
	}

	public boolean performPB_VALIDER_REFERENT_RH_GLOBAL(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieReferentRhGlobal(request))
			return false;

		// suppression et creation
		getReferentRhDao().supprimerReferentRh(getReferentRhGlobalCourant().getIdReferentRh());
		Agent ag = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_EF_ID_REFERENT_RH_GLOBAL()));
		getReferentRhGlobalCourant().setIdAgentReferent(ag.getIdAgent());
		getReferentRhGlobalCourant().setNumeroTelephone(Integer.valueOf(getVAL_EF_NUMERO_TELEPHONE_GLOBAL()));
		getReferentRhGlobalCourant().setServi(null);
		getReferentRhDao().creerReferentRh(getReferentRhGlobalCourant().getServi(),
				getReferentRhGlobalCourant().getIdAgentReferent(), getReferentRhGlobalCourant().getNumeroTelephone());

		initialiseReferentRhGlobal(request);
		setReferentRhGlobalCourant(null);

		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;
	}

	public ReferentRh getReferentRhGlobalCourant() {
		return referentRhGlobalCourant;
	}

	public void setReferentRhGlobalCourant(ReferentRh referentRhGlobalCourant) {
		this.referentRhGlobalCourant = referentRhGlobalCourant;
	}

	private boolean performControlerSaisieReferentRhGlobal(HttpServletRequest request) throws Exception {
		// Verification agent not null
		if (getZone(getNOM_EF_ID_REFERENT_RH_GLOBAL()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		}

		// format agent
		if (!Services.estNumerique(getVAL_EF_ID_REFERENT_RH_GLOBAL())) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "agent"));
			return false;
		}
		// Verification numero téléphone not null
		if (getZone(getNOM_EF_NUMERO_TELEPHONE_GLOBAL()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "téléphone"));
			return false;
		}

		// format numero téléphone
		if (!Services.estNumerique(getVAL_EF_NUMERO_TELEPHONE_GLOBAL())) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "téléphone"));
			return false;
		}
		return true;
	}

	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	public String getNOM_EF_CODESERVICE() {
		return "NOM_EF_CODESERVICE";
	}

	public String getVAL_EF_CODESERVICE() {
		return getZone(getNOM_EF_CODESERVICE());
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}
}
