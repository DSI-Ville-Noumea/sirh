package nc.mairie.gestionagent.process.parametre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.parametrage.AccueilKiosque;
import nc.mairie.metier.parametrage.AlerteKiosque;
import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.parametrage.AccueilKiosqueDao;
import nc.mairie.spring.dao.metier.parametrage.AlerteKiosqueDao;
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
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;

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
	private String[] LB_ALERTE_KIOSQUE;

	private ArrayList<EntiteDto> listeServiceUtilisateur;

	private ReferentRhDao referentRhDao;
	private AccueilKiosqueDao accueilKiosqueDao;
	private AgentDao agentDao;
	private AlerteKiosqueDao alerteKiosqueDao;

	private IAdsService adsService;

	private ReferentRh referentRhGlobalCourant;
	private ReferentRh referentRhCourant;
	private ArrayList<ReferentRh> listeReferentRh;

	private AccueilKiosque accueilKiosqueCourant;
	private ArrayList<AccueilKiosque> listeAccueilKiosque;

	private AlerteKiosque alerteKiosqueCourant;
	private ArrayList<AlerteKiosque> listeAlerteKiosque;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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
		if (getListeAlerteKiosque().size() == 0) {
			initialiseListeAlerteKiosque(request);
		}

		// Initialisation des Services
		if (getListeServiceUtilisateur().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EntiteDto> list = getListeServiceUtilisateur().listIterator(); list.hasNext();) {
				EntiteDto de = (EntiteDto) list.next();
				String ligne[] = { de.getSigle() + " " + de.getLabel() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_SERVICE_UTILISATEUR(aFormat.getListeFormatee());
		} else {
			setLB_SERVICE_UTILISATEUR(null);
		}

	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, true);
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
				String ligne[] = { ref.getTitre() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TEXTE_KIOSQUE(aFormat.getListeFormatee());
		} else {
			setLB_TEXTE_KIOSQUE(null);
		}
	}

	private void initialiseListeAlerteKiosque(HttpServletRequest request) throws Exception {
		setListeAlerteKiosque((ArrayList<AlerteKiosque>) getAlerteKiosqueDao().getAlerteKiosque());
		if (getListeAlerteKiosque().size() != 0) {
			int tailles[] = { 90 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<AlerteKiosque> list = getListeAlerteKiosque().listIterator(); list.hasNext();) {
				AlerteKiosque ref = (AlerteKiosque) list.next();
				String ligne[] = { ref.getTitre() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ALERTE_KIOSQUE(aFormat.getListeFormatee());
		} else {
			setLB_ALERTE_KIOSQUE(null);
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
		if (getAlerteKiosqueDao() == null) {
			setAlerteKiosqueDao(new AlerteKiosqueDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
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
					for (EntiteDto serv : getListeServiceUtilisateur()) {
						getReferentRhCourant().setIdAgentReferent(ag.getIdAgent());
						getReferentRhCourant().setNumeroTelephone(Integer.valueOf(getVAL_EF_NUMERO_TELEPHONE()));
						getReferentRhCourant().setIdServiceAds(serv.getIdEntite());
						EntiteDto serviceAs400 = adsService.getInfoSiservByIdEntite(serv.getIdEntite());
						getReferentRhCourant().setServi(serviceAs400 == null ? null : serviceAs400.getCodeServi());
						getReferentRhDao().creerReferentRh(getReferentRhCourant().getIdAgentReferent(), getReferentRhCourant().getNumeroTelephone(), getReferentRhCourant().getIdServiceAds(),
								getReferentRhCourant().getServi());
					}
				} catch (Exception e) {
					// "ERR503",
					// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", getVAL_EF_ID_REFERENT_RH()));
					return false;
				}
			} else if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_SUPPRESSION)) {
				// on supprime toutes les entrées
				for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(getReferentRhCourant().getIdAgentReferent())) {
					getReferentRhDao().supprimerReferentRh(ref.getIdReferentRh());
				}
			} else if (getVAL_ST_ACTION_REFERENT_RH().equals(ACTION_MODIFICATION)) {
				// on supprime toutes les entrées
				for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(getReferentRhCourant().getIdAgentReferent())) {
					getReferentRhDao().supprimerReferentRh(ref.getIdReferentRh());
				}
				// on crée les entrées
				for (EntiteDto serv : getListeServiceUtilisateur()) {
					setReferentRhCourant(new ReferentRh());
					getReferentRhCourant().setIdAgentReferent(Integer.valueOf("900" + getVAL_EF_ID_REFERENT_RH()));
					getReferentRhCourant().setNumeroTelephone(Integer.valueOf(getVAL_EF_NUMERO_TELEPHONE()));
					getReferentRhCourant().setIdServiceAds(serv.getIdEntite().intValue());
					EntiteDto serviceAs400 = adsService.getInfoSiservByIdEntite(serv.getIdEntite());
					getReferentRhCourant().setServi(serviceAs400 == null ? null : serviceAs400.getCodeServi());
					getReferentRhDao().creerReferentRh(getReferentRhCourant().getIdAgentReferent(), getReferentRhCourant().getNumeroTelephone(), getReferentRhCourant().getIdServiceAds(),
							getReferentRhCourant().getServi());
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
					// "Attention, il existe déjà  @ avec @. Veuillez contrôler."
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

		ArrayList<EntiteDto> listeServ = new ArrayList<EntiteDto>();
		for (ReferentRh ref : getReferentRhDao().listerServiceAvecReferentRh(getReferentRhCourant().getIdAgentReferent())) {
			EntiteDto serv = adsService.getEntiteByIdEntite(ref.getIdServiceAds());
			listeServ.add(serv);
		}
		setListeServiceUtilisateur(listeServ);

		return true;
	}

	public String getNOM_PB_MODIFIER_REFERENT_RH() {
		return "NOM_PB_MODIFIER_REFERENT_RH";
	}

	public boolean performPB_MODIFIER_REFERENT_RH(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_REFERENT_RH_SELECT()) ? Integer.parseInt(getVAL_LB_REFERENT_RH_SELECT()) : -1);
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
		int indice = (Services.estNumerique(getVAL_LB_REFERENT_RH_SELECT()) ? Integer.parseInt(getVAL_LB_REFERENT_RH_SELECT()) : -1);
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
		addZone(getNOM_EF_TITRE_ACCUEIL_KIOSQUE(), Const.CHAINE_VIDE);

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
			// Si clic sur le bouton PB_ANNULER_ALERTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_ANNULER_ALERTE_KIOSQUE())) {
				return performPB_ANNULER_ALERTE_KIOSQUE(request);
			}
			// Si clic sur le bouton PB_CREER_ALERTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_CREER_ALERTE_KIOSQUE())) {
				return performPB_CREER_ALERTE_KIOSQUE(request);
			}
			// Si clic sur le bouton PB_MODIFIER_ALERTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_MODIFIER_ALERTE_KIOSQUE())) {
				return performPB_MODIFIER_ALERTE_KIOSQUE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ALERTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ALERTE_KIOSQUE())) {
				return performPB_SUPPRIMER_ALERTE_KIOSQUE(request);
			}
			// Si clic sur le bouton PB_VALIDER_ALERTE_KIOSQUE
			if (testerParametre(request, getNOM_PB_VALIDER_ALERTE_KIOSQUE())) {
				return performPB_VALIDER_ALERTE_KIOSQUE(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 *            focus à  définir.
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
		String idServiceAds = getVAL_EF_ID_SERVICE_ADS();

		if (idServiceAds.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres services"));
			return false;
		}
		EntiteDto serv = adsService.getEntiteByIdEntite(new Integer(idServiceAds));
		if (!getListeServiceUtilisateur().contains(serv))
			getListeServiceUtilisateur().add(serv);

		return true;
	}

	public String getNOM_PB_AJOUTER_TOUT() {
		return "NOM_PB_AJOUTER_TOUT";
	}

	public boolean performPB_AJOUTER_TOUT(HttpServletRequest request) throws Exception {
		// Recup du service sélectionné
		String idServiceAds = getVAL_EF_ID_SERVICE_ADS();

		if (idServiceAds.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres services"));
			return false;
		}

		// On recupere les sous services
		EntiteDto listeSousServ = adsService.getEntiteWithChildrenByIdEntite(new Integer(idServiceAds));

		// On ajoute le service et les sous services
		if (null != listeSousServ && !getListeServiceUtilisateur().contains(listeSousServ)) {
			getListeServiceUtilisateur().add(listeSousServ);
		}
		addServicesUtilisateur(listeSousServ);

		return true;
	}

	private void addServicesUtilisateur(EntiteDto entiteDto) {
		if (null != entiteDto && null != entiteDto.getEnfants()) {
			for (EntiteDto sousServ : entiteDto.getEnfants()) {
				if (!getListeServiceUtilisateur().contains(sousServ)) {
					getListeServiceUtilisateur().add(sousServ);
				}
				addServicesUtilisateur(sousServ);
			}
		}
	}

	public String getNOM_PB_RETIRER_SERVICE() {
		return "NOM_PB_RETIRER_SERVICE";
	}

	public boolean performPB_RETIRER_SERVICE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) : -1);
		if (numLigne == -1 || getListeServiceUtilisateur().size() == 0 || numLigne > getListeServiceUtilisateur().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Services de l'utilisateur"));
			return false;
		}

		EntiteDto groupe = (EntiteDto) getListeServiceUtilisateur().get(numLigne);
		if (getListeServiceUtilisateur().contains(groupe))
			getListeServiceUtilisateur().remove(groupe);

		return true;
	}

	public String getNOM_PB_RETIRER_TOUT() {
		return "NOM_PB_RETIRER_TOUT";
	}

	public boolean performPB_RETIRER_TOUT(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_SERVICE_UTILISATEUR_SELECT())) : -1);
		if (numLigne == -1 || getListeServiceUtilisateur().size() == 0 || numLigne > getListeServiceUtilisateur().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Services de l'utilisateur"));
			return false;
		}

		EntiteDto groupe = (EntiteDto) getListeServiceUtilisateur().get(numLigne);

		// On recupere les sous services
		EntiteDto serv = adsService.getEntiteWithChildrenByIdEntite(groupe.getIdEntite().intValue());

		// On ajoute le service et les sous services
		removeServicesUtilisateur(serv);

		if (getListeServiceUtilisateur().contains(groupe))
			getListeServiceUtilisateur().remove(groupe);

		return true;
	}

	private void removeServicesUtilisateur(EntiteDto entiteDto) {
		if (null != entiteDto && null != entiteDto.getEnfants()) {
			for (EntiteDto sousServ : entiteDto.getEnfants()) {
				if (getListeServiceUtilisateur().contains(sousServ)) {
					getListeServiceUtilisateur().remove(sousServ);
				}
				removeServicesUtilisateur(sousServ);
			}
		}
	}

	private ArrayList<EntiteDto> getListeServiceUtilisateur() {
		if (listeServiceUtilisateur == null)
			listeServiceUtilisateur = new ArrayList<EntiteDto>();
		return listeServiceUtilisateur;
	}

	private void setListeServiceUtilisateur(ArrayList<EntiteDto> listeGroupesUtilisateur) {
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
		// On enleve l'agent selectionnée
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
		viderFormulaire();

		int indice = (Services.estNumerique(getVAL_LB_TEXTE_KIOSQUE_SELECT()) ? Integer.parseInt(getVAL_LB_TEXTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAccueilKiosque().size()) {
			AccueilKiosque ref = getListeAccueilKiosque().get(indice);
			setAccueilKiosqueCourant(ref);
			addZone(getNOM_EF_TEXTE_KIOSQUE(), getAccueilKiosqueCourant().getTexteAccueilKiosque());
			addZone(getNOM_EF_TITRE_ACCUEIL_KIOSQUE(), getAccueilKiosqueCourant().getTitre());
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
		viderFormulaire();
		int indice = (Services.estNumerique(getVAL_LB_TEXTE_KIOSQUE_SELECT()) ? Integer.parseInt(getVAL_LB_TEXTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAccueilKiosque().size()) {
			AccueilKiosque ref = getListeAccueilKiosque().get(indice);
			setAccueilKiosqueCourant(ref);
			addZone(getNOM_EF_TEXTE_KIOSQUE(), getAccueilKiosqueCourant().getTexteAccueilKiosque());
			addZone(getNOM_EF_TITRE_ACCUEIL_KIOSQUE(), getAccueilKiosqueCourant().getTitre());
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
		viderFormulaire();

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
		viderFormulaire();
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
				getAccueilKiosqueCourant().setTitre(getVAL_EF_TITRE_ACCUEIL_KIOSQUE());
				getAccueilKiosqueCourant().setTexteAccueilKiosque(getVAL_EF_TEXTE_KIOSQUE());
				getAccueilKiosqueDao().creerAccueilKiosque(getAccueilKiosqueCourant().getTitre(), getAccueilKiosqueCourant().getTexteAccueilKiosque());
			} else if (getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(ACTION_SUPPRESSION)) {
				getAccueilKiosqueDao().supprimerAccueilKiosque(getAccueilKiosqueCourant().getIdAccueilKiosque());
			} else if (getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(ACTION_MODIFICATION)) {
				getAccueilKiosqueCourant().setTitre(getVAL_EF_TITRE_ACCUEIL_KIOSQUE());
				getAccueilKiosqueCourant().setTexteAccueilKiosque(getVAL_EF_TEXTE_KIOSQUE());
				getAccueilKiosqueDao().modifierAccueilKiosque(getAccueilKiosqueCourant().getIdAccueilKiosque(), getAccueilKiosqueCourant().getTitre(),
						getAccueilKiosqueCourant().getTexteAccueilKiosque());

			}
			initialiseListeAccueilKiosque(request);
			setAccueilKiosqueCourant(null);
			addZone(getNOM_ST_ACTION_TEXTE_KIOSQUE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_TEXTE_KIOSQUE());
		return true;
	}

	public String getNOM_EF_TITRE_ACCUEIL_KIOSQUE() {
		return "NOM_EF_TITRE_ACCUEIL_KIOSQUE";
	}

	public String getVAL_EF_TITRE_ACCUEIL_KIOSQUE() {
		return getZone(getNOM_EF_TITRE_ACCUEIL_KIOSQUE());
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
		// On enleve l'agent selectionnée
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
		getReferentRhGlobalCourant().setIdServiceAds(null);
		getReferentRhGlobalCourant().setServi(null);
		getReferentRhDao().creerReferentRh(getReferentRhGlobalCourant().getIdAgentReferent(), getReferentRhGlobalCourant().getNumeroTelephone(), getReferentRhCourant().getIdServiceAds(),
				getReferentRhCourant().getServi());

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

	public String getNOM_EF_ID_SERVICE_ADS() {
		return "NOM_EF_ID_SERVICE_ADS";
	}

	public String getVAL_EF_ID_SERVICE_ADS() {
		return getZone(getNOM_EF_ID_SERVICE_ADS());
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public AlerteKiosque getAlerteKiosqueCourant() {
		return alerteKiosqueCourant;
	}

	public void setAlerteKiosqueCourant(AlerteKiosque alerteKiosqueCourant) {
		this.alerteKiosqueCourant = alerteKiosqueCourant;
	}

	public ArrayList<AlerteKiosque> getListeAlerteKiosque() {
		if (listeAlerteKiosque == null)
			return new ArrayList<AlerteKiosque>();
		return listeAlerteKiosque;
	}

	public void setListeAlerteKiosque(ArrayList<AlerteKiosque> listeAlerteKiosque) {
		this.listeAlerteKiosque = listeAlerteKiosque;
	}

	public AlerteKiosqueDao getAlerteKiosqueDao() {
		return alerteKiosqueDao;
	}

	public void setAlerteKiosqueDao(AlerteKiosqueDao alerteKiosqueDao) {
		this.alerteKiosqueDao = alerteKiosqueDao;
	}

	private String[] getLB_ALERTE_KIOSQUE() {
		if (LB_ALERTE_KIOSQUE == null)
			LB_ALERTE_KIOSQUE = initialiseLazyLB();
		return LB_ALERTE_KIOSQUE;
	}

	private void setLB_ALERTE_KIOSQUE(String[] newLB_ALERTE_KIOSQUE) {
		LB_ALERTE_KIOSQUE = newLB_ALERTE_KIOSQUE;
	}

	public String getNOM_LB_ALERTE_KIOSQUE() {
		return "NOM_LB_ALERTE_KIOSQUE";
	}

	public String getNOM_LB_ALERTE_KIOSQUE_SELECT() {
		return "NOM_LB_ALERTE_KIOSQUE_SELECT";
	}

	public String[] getVAL_LB_ALERTE_KIOSQUE() {
		return getLB_ALERTE_KIOSQUE();
	}

	public String getVAL_LB_ALERTE_KIOSQUE_SELECT() {
		return getZone(getNOM_LB_ALERTE_KIOSQUE_SELECT());
	}

	public String getNOM_PB_MODIFIER_ALERTE_KIOSQUE() {
		return "NOM_PB_MODIFIER_ALERTE_KIOSQUE";
	}

	public boolean performPB_MODIFIER_ALERTE_KIOSQUE(HttpServletRequest request) throws Exception {
		viderZoneSaisieAlerte(request);

		int indice = (Services.estNumerique(getVAL_LB_ALERTE_KIOSQUE_SELECT()) ? Integer.parseInt(getVAL_LB_ALERTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAlerteKiosque().size()) {
			AlerteKiosque ref = getListeAlerteKiosque().get(indice);
			setAlerteKiosqueCourant(ref);
			addZone(getNOM_EF_ALERTE_KIOSQUE(), getAlerteKiosqueCourant().getTexteAlerteKiosque());
			addZone(getNOM_EF_TITRE_ALERTE_KIOSQUE(), getAlerteKiosqueCourant().getTitre());
			addZone(getNOM_CK_AGENT(), getAlerteKiosqueCourant().isAgent() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_APPRO_ABS(), getAlerteKiosqueCourant().isApprobateurABS() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_APPRO_PTG(), getAlerteKiosqueCourant().isApprobateurPTG() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_OPE_ABS(), getAlerteKiosqueCourant().isOperateurABS() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_OPE_PTG(), getAlerteKiosqueCourant().isOperateurPTG() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_VISEUR_ABS(), getAlerteKiosqueCourant().isViseurABS() ? getCHECKED_ON() : getCHECKED_OFF());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			addZone(getNOM_EF_DATE_DEBUT(), sdf.format(getAlerteKiosqueCourant().getDateDebut()));
			addZone(getNOM_EF_DATE_FIN(), sdf.format(getAlerteKiosqueCourant().getDateFin()));
			addZone(getNOM_ST_ACTION_ALERTE_KIOSQUE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "texte d'alerte"));
		}

		setFocus(getNOM_PB_ANNULER_ALERTE_KIOSQUE());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_ALERTE_KIOSQUE() {
		return "NOM_PB_SUPPRIMER_ALERTE_KIOSQUE";
	}

	public boolean performPB_SUPPRIMER_ALERTE_KIOSQUE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ALERTE_KIOSQUE_SELECT()) ? Integer.parseInt(getVAL_LB_ALERTE_KIOSQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeAlerteKiosque().size()) {
			AlerteKiosque ref = getListeAlerteKiosque().get(indice);
			setAlerteKiosqueCourant(ref);
			addZone(getNOM_EF_ALERTE_KIOSQUE(), getAlerteKiosqueCourant().getTexteAlerteKiosque());
			addZone(getNOM_EF_TITRE_ALERTE_KIOSQUE(), getAlerteKiosqueCourant().getTitre());
			addZone(getNOM_ST_ACTION_ALERTE_KIOSQUE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "texte d'alerte"));
		}

		setFocus(getNOM_PB_ANNULER_REFERENT_RH());
		return true;

	}

	private void viderZoneSaisieAlerte(HttpServletRequest request) {
		addZone(getNOM_EF_ALERTE_KIOSQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TITRE_ALERTE_KIOSQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_CK_AGENT(), getCHECKED_OFF());
		addZone(getNOM_CK_APPRO_ABS(), getCHECKED_OFF());
		addZone(getNOM_CK_APPRO_PTG(), getCHECKED_OFF());
		addZone(getNOM_CK_OPE_ABS(), getCHECKED_OFF());
		addZone(getNOM_CK_OPE_PTG(), getCHECKED_OFF());
		addZone(getNOM_CK_VISEUR_ABS(), getCHECKED_OFF());
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);

	}

	public String getNOM_PB_CREER_ALERTE_KIOSQUE() {
		return "NOM_PB_CREER_ALERTE_KIOSQUE";
	}

	public boolean performPB_CREER_ALERTE_KIOSQUE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_ALERTE_KIOSQUE(), ACTION_CREATION);

		// On vide la zone de saisie
		setAlerteKiosqueCourant(new AlerteKiosque());
		viderZoneSaisieAlerte(request);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_ALERTE_KIOSQUE());
		return true;
	}

	public String getNOM_ST_ACTION_ALERTE_KIOSQUE() {
		return "NOM_ST_ACTION_ALERTE_KIOSQUE";
	}

	public String getVAL_ST_ACTION_ALERTE_KIOSQUE() {
		return getZone(getNOM_ST_ACTION_ALERTE_KIOSQUE());
	}

	public String getNOM_PB_ANNULER_ALERTE_KIOSQUE() {
		return "NOM_PB_ANNULER_ALERTE_KIOSQUE";
	}

	public boolean performPB_ANNULER_ALERTE_KIOSQUE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ALERTE_KIOSQUE(), Const.CHAINE_VIDE);
		setAlerteKiosqueCourant(null);
		viderZoneSaisieAlerte(request);
		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_ALERTE_KIOSQUE());
		return true;
	}

	public String getNOM_PB_VALIDER_ALERTE_KIOSQUE() {
		return "NOM_PB_VALIDER_ALERTE_KIOSQUE";
	}

	public boolean performPB_VALIDER_ALERTE_KIOSQUE(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_ALERTE_KIOSQUE() != null && getVAL_ST_ACTION_ALERTE_KIOSQUE() != Const.CHAINE_VIDE) {

			if (!getVAL_ST_ACTION_ALERTE_KIOSQUE().equals(ACTION_SUPPRESSION)) {
				// Vérification de la validité du formulaire
				if (!performControlerChampsAlerte(request))
					return false;

				// on rempli l'objet
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				getAlerteKiosqueCourant().setTitre(getVAL_EF_TITRE_ALERTE_KIOSQUE());
				getAlerteKiosqueCourant().setTexteAlerteKiosque(getVAL_EF_ALERTE_KIOSQUE());
				getAlerteKiosqueCourant().setAgent(getVAL_CK_AGENT().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setApprobateurABS(getVAL_CK_APPRO_ABS().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setApprobateurPTG(getVAL_CK_APPRO_PTG().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setOperateurABS(getVAL_CK_OPE_ABS().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setOperateurPTG(getVAL_CK_OPE_PTG().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setViseurABS(getVAL_CK_VISEUR_ABS().equals(getCHECKED_ON()));
				getAlerteKiosqueCourant().setDateDebut(sdf.parse(Services.formateDate(getVAL_EF_DATE_DEBUT())));
				getAlerteKiosqueCourant().setDateFin(sdf.parse(Services.formateDate(getVAL_EF_DATE_FIN())));

				if (getVAL_ST_ACTION_ALERTE_KIOSQUE().equals(ACTION_CREATION)) {
					getAlerteKiosqueDao().creerAlerteKiosque(getAlerteKiosqueCourant());
				} else if (getVAL_ST_ACTION_ALERTE_KIOSQUE().equals(ACTION_MODIFICATION)) {
					getAlerteKiosqueDao().modifierAlerteKiosque(getAlerteKiosqueCourant().getIdAlerteKiosque(), getAlerteKiosqueCourant());

				}
			} else if (getVAL_ST_ACTION_ALERTE_KIOSQUE().equals(ACTION_SUPPRESSION)) {
				getAlerteKiosqueDao().supprimerAlerteKiosque(getAlerteKiosqueCourant().getIdAlerteKiosque());
			}
			initialiseListeAlerteKiosque(request);
			setAlerteKiosqueCourant(null);
			addZone(getNOM_ST_ACTION_ALERTE_KIOSQUE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_ALERTE_KIOSQUE());
		return true;
	}

	private boolean performControlerChampsAlerte(HttpServletRequest request) {

		// titre obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_TITRE_ALERTE_KIOSQUE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "titre"));
			return false;
		}

		// texte obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_ALERTE_KIOSQUE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "texte"));
			return false;
		}

		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_DEBUT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_EF_DATE_DEBUT())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}

		// date de fin obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_FIN())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		// format date de fin
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN()) && !Services.estUneDate(getVAL_EF_DATE_FIN())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de fin"));
			return false;
		}

		// testdate debut < date fin
		if (Services.compareDates(getVAL_EF_DATE_DEBUT(), getVAL_EF_DATE_FIN()) >= 0) {
			// "ERR204", "La date @ doit être inferieure à  la date @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR204", "de début", "de fin"));
			return false;
		}
		return true;
	}

	public String getNOM_EF_TITRE_ALERTE_KIOSQUE() {
		return "NOM_EF_TITRE_ALERTE_KIOSQUE";
	}

	public String getVAL_EF_TITRE_ALERTE_KIOSQUE() {
		return getZone(getNOM_EF_TITRE_ALERTE_KIOSQUE());
	}

	public String getNOM_EF_ALERTE_KIOSQUE() {
		return "NOM_EF_ALERTE_KIOSQUE";
	}

	public String getVAL_EF_ALERTE_KIOSQUE() {
		return getZone(getNOM_EF_ALERTE_KIOSQUE());
	}

	public String getNOM_CK_AGENT() {
		return "NOM_CK_AGENT";
	}

	public String getVAL_CK_AGENT() {
		return getZone(getNOM_CK_AGENT());
	}

	public String getNOM_CK_APPRO_ABS() {
		return "NOM_CK_APPRO_ABS";
	}

	public String getVAL_CK_APPRO_ABS() {
		return getZone(getNOM_CK_APPRO_ABS());
	}

	public String getNOM_CK_APPRO_PTG() {
		return "NOM_CK_APPRO_PTG";
	}

	public String getVAL_CK_APPRO_PTG() {
		return getZone(getNOM_CK_APPRO_PTG());
	}

	public String getNOM_CK_OPE_ABS() {
		return "NOM_CK_OPE_ABS";
	}

	public String getVAL_CK_OPE_ABS() {
		return getZone(getNOM_CK_OPE_ABS());
	}

	public String getNOM_CK_OPE_PTG() {
		return "NOM_CK_OPE_PTG";
	}

	public String getVAL_CK_OPE_PTG() {
		return getZone(getNOM_CK_OPE_PTG());
	}

	public String getNOM_CK_VISEUR_ABS() {
		return "NOM_CK_VISEUR_ABS";
	}

	public String getVAL_CK_VISEUR_ABS() {
		return getZone(getNOM_CK_VISEUR_ABS());
	}

	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}
}
