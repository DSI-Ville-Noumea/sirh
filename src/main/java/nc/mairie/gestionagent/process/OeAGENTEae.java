package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeCommentaireDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvolutionDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.dao.metier.EAE.EaePlanActionDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeCommentaire;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
import nc.mairie.spring.domain.metier.EAE.EaePlanAction;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTDIPLOMEGestion Date de création : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTEae extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String ACTION_MODIFICATION = "Modification d'un EAE.";
	public String ACTION_CONSULTATION = "Consultation d'un EAE.";

	private AgentNW AgentCourant;
	private ArrayList<EAE> listeEae;
	private ArrayList<EaeEvaluateur> listeEvaluateurEae;
	private ArrayList<EaePlanAction> listeObjectifPro;
	private ArrayList<EaePlanAction> listeObjectifIndi;
	private EAE eaeCourant;

	private EAEDao eaeDao;
	private CampagneEAEDao campagneEaeDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private EaeFichePosteDao eaeFichePosteDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeCommentaireDao eaeCommentaireDao;
	private EaeFinalisationDao eaeFinalisationDao;
	private EaePlanActionDao eaePlanActionDao;
	private EaeEvolutionDao eaeEvolutionDao;

	private String urlFichier;

	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
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

		initialiseDao();

		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeEae(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * Initialisation de la liste des primes de l'agent courant Date de création
	 * : (04/08/11)
	 * 
	 */
	private void initialiseListeEae(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des EAE de l'agent
		ArrayList<EaeEvalue> listeEAEEvalue = getEaeEvalueDao().listerEaeEvalue(Integer.valueOf(getAgentCourant().getIdAgent()));

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		int indiceEae = 0;
		if (listeEAEEvalue != null) {
			for (int i = 0; i < listeEAEEvalue.size(); i++) {
				EaeEvalue evalue = (EaeEvalue) listeEAEEvalue.get(i);
				EAE eae = getEaeDao().chercherEAE(evalue.getIdEae());
				listeEAE.add(eae);
				EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(evalue.getIdEae(), true);
				CampagneEAE camp = getCampagneEaeDao().chercherCampagneEAE(eae.getIdCampagneEAE());
				ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(evalue.getIdEae());
				String evaluateur = Const.CHAINE_VIDE;
				for (int j = 0; j < listeEvaluateur.size(); j++) {
					EaeEvaluateur eval = listeEvaluateur.get(j);
					AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
					evaluateur += agentEvaluateur.getNomPatronymique() + " " + agentEvaluateur.getPrenomUsage() + " ("
							+ agentEvaluateur.getNoMatricule() + ") <br/> ";
				}

				addZone(getNOM_ST_ANNEE(indiceEae), camp.getAnnee().toString());
				addZone(getNOM_ST_EVALUATEUR(indiceEae), evaluateur.equals(Const.CHAINE_VIDE) ? "&nbsp;" : evaluateur);
				addZone(getNOM_ST_DATE_ENTRETIEN(indiceEae), eae.getDateEntretien() == null ? "&nbsp;" : sdf.format(eae.getDateEntretien()));
				addZone(getNOM_ST_SERVICE(indiceEae), eaeFDP.getSectionServ() == null ? "&nbsp;" : eaeFDP.getSectionServ());
				addZone(getNOM_ST_STATUT(indiceEae), EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()));

				indiceEae++;
			}
		}
		setListeEae(listeEAE);
	}

	private void initialiseListeDeroulante() throws Exception {

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}
		if (getCampagneEaeDao() == null) {
			setCampagneEaeDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}
		if (getEaeEvaluateurDao() == null) {
			setEaeEvaluateurDao((EaeEvaluateurDao) context.getBean("eaeEvaluateurDao"));
		}
		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao((EaeFichePosteDao) context.getBean("eaeFichePosteDao"));
		}
		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao((EaeEvalueDao) context.getBean("eaeEvalueDao"));
		}
		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}
		if (getEaeCommentaireDao() == null) {
			setEaeCommentaireDao((EaeCommentaireDao) context.getBean("eaeCommentaireDao"));
		}
		if (getEaeFinalisationDao() == null) {
			setEaeFinalisationDao((EaeFinalisationDao) context.getBean("eaeFinalisationDao"));
		}
		if (getEaePlanActionDao() == null) {
			setEaePlanActionDao((EaePlanActionDao) context.getBean("eaePlanActionDao"));
		}
		if (getEaeEvolutionDao() == null) {
			setEaeEvolutionDao((EaeEvolutionDao) context.getBean("eaeEvolutionDao"));
		}
	}

	/**
	 * @return Agent
	 */
	public AgentNW getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * @param newAgentCourant
	 *            Agent
	 */
	private void setAgentCourant(AgentNW newAgentCourant) {
		AgentCourant = newAgentCourant;
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
		// return getNOM_EF_DATE_OBTENTION();
		return "";
	}

	/**
	 * @param focus
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}
			// Si clic sur le bouton PB_VISUALISER_DOC
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISER_DOC(i))) {
					return performPB_VISUALISER_DOC(request, i);
				}
			}

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 10:36:22)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEae.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-AG-EAE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR(int i) {
		return "NOM_ST_EVALUATEUR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EVALUATEUR
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR(int i) {
		return getZone(getNOM_ST_EVALUATEUR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN(int i) {
		return "NOM_ST_DATE_ENTRETIEN" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN(int i) {
		return getZone(getNOM_ST_DATE_ENTRETIEN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
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

		// Récup de l'eae courant
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAConsulter);
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseEae() throws Exception {
		// TODO

		// Récup de l'EAE courant
		EAE eae = getEaeCourant();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Alim zone Informations
		addZone(getNOM_ST_DATE_ENTRETIEN(), eae.getDateEntretien() == null ? "non renseigné" : sdf.format(eae.getDateEntretien()));
		ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEAE());
		setListeEvaluateurEae(listeEvaluateur);
		for (int j = 0; j < listeEvaluateur.size(); j++) {
			EaeEvaluateur eval = listeEvaluateur.get(j);
			AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
			String evaluateur = agentEvaluateur.getNomPatronymique() + " " + agentEvaluateur.getPrenomUsage() + " ("
					+ agentEvaluateur.getNoMatricule() + ") ";

			addZone(getNOM_ST_EVALUATEUR_NOM(j), evaluateur.equals(Const.CHAINE_VIDE) ? "non renseigné" : evaluateur);
			addZone(getNOM_ST_EVALUATEUR_FONCTION(j), eval.getFonction().equals(Const.CHAINE_VIDE) ? "non renseigné" : eval.getFonction());
		}
		EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEAE(), true);
		String direction = eaeFDP.getDirectionServ() == null ? Const.CHAINE_VIDE : eaeFDP.getDirectionServ();
		String serv = eaeFDP.getServiceServ() == null ? Const.CHAINE_VIDE : eaeFDP.getServiceServ();
		addZone(getNOM_ST_SERVICE(), direction.equals(Const.CHAINE_VIDE) ? serv.equals(Const.CHAINE_VIDE) ? "&nbsp;" : serv : direction + " / "
				+ serv);

		// Alim zone evaluation
		EaeEvaluation evaluation = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEAE());
		if (evaluation == null) {
			addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), "non renseigné");
			addZone(getNOM_ST_NIVEAU(), "non renseigné");
			addZone(getNOM_ST_NOTE(), "non renseigné");
			addZone(getNOM_ST_AVIS_SHD(), "non renseigné");
			addZone(getNOM_ST_AVCT_DIFF(), "non renseigné");
			addZone(getNOM_ST_CHANGEMENT_CLASSE(), "non renseigné");
			addZone(getNOM_ST_AVIS_REVALO(), "non renseigné");
			addZone(getNOM_ST_RAPPORT_CIRCON(), "non renseigné");
		} else {
			if (evaluation.getIdCommEvaluateur() != null) {
				EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(evaluation.getIdCommEvaluateur());
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), commEvaluateur == null ? "non renseigné" : commEvaluateur.getCommentaire());
			} else {
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), "non renseigné");
			}
			addZone(getNOM_ST_NIVEAU(), evaluation.getNiveau() == null ? "non renseigné" : evaluation.getNiveau());
			addZone(getNOM_ST_NOTE(), evaluation.getNoteAnnee() == null ? "non renseigné" : evaluation.getNoteAnnee().toString());
			addZone(getNOM_ST_AVIS_SHD(), evaluation.getAvis_shd() == null ? "non renseigné " : evaluation.getAvis_shd());
			addZone(getNOM_ST_AVCT_DIFF(), evaluation.getPropositionAvancement() == null ? "non renseigné" : evaluation.getPropositionAvancement());
			addZone(getNOM_ST_CHANGEMENT_CLASSE(),
					evaluation.getAvisChangementClasse() == null ? "non renseigné" : evaluation.getAvisChangementClasse() == 1 ? "favorable"
							: "défavorable");
			addZone(getNOM_ST_AVIS_REVALO(), evaluation.getAvisRevalorisation() == null ? "non renseigné"
					: evaluation.getAvisRevalorisation() == 1 ? "favorable" : "défavorable");
			addZone(getNOM_ST_RAPPORT_CIRCON(), "non renseigné");
		}

		// alim zone plan action
		ArrayList<EaePlanAction> listeObjectifPro = getEaePlanActionDao().listerPlanActionParType(eae.getIdEAE(), 1);
		setListeObjectifPro(listeObjectifPro);
		for (int j = 0; j < listeObjectifPro.size(); j++) {
			EaePlanAction plan = listeObjectifPro.get(j);

			addZone(getNOM_ST_LIB_OBJ_PRO(j), plan.getObjectif());
			addZone(getNOM_ST_LIB_MESURE_PRO(j), plan.getMesure());
		}

		ArrayList<EaePlanAction> listeObjectifIndi = getEaePlanActionDao().listerPlanActionParType(eae.getIdEAE(), 2);
		setListeObjectifIndi(listeObjectifIndi);
		for (int j = 0; j < listeObjectifIndi.size(); j++) {
			EaePlanAction plan = listeObjectifIndi.get(j);

			addZone(getNOM_ST_LIB_OBJ_INDI(j), plan.getObjectif());
		}

		// Alim zone Evolution
		EaeEvolution evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEAE());
		if (evolution == null) {
			addZone(getNOM_ST_MOB_GEO(), "non renseigné");
			addZone(getNOM_ST_MOB_FONCT(), "non renseigné");
			addZone(getNOM_ST_CHANGEMENT_METIER(), "non renseigné");
			addZone(getNOM_ST_DELAI(), "non renseigné");
			addZone(getNOM_ST_MOB_SERV(), "non renseigné");
			addZone(getNOM_ST_MOB_DIR(), "non renseigné");
			addZone(getNOM_ST_MOB_COLL(), "non renseigné");
			addZone(getNOM_ST_NOM_COLL(), "non renseigné");
			addZone(getNOM_ST_MOB_AUTRE(), "non renseigné");
			addZone(getNOM_ST_CONCOURS(), "non renseigné");
			addZone(getNOM_ST_NOM_CONCOURS(), "non renseigné");
			addZone(getNOM_ST_VAE(), "non renseigné");
			addZone(getNOM_ST_NOM_VAE(), "non renseigné");
			addZone(getNOM_ST_TPS_PARTIEL(), "non renseigné");
			addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseigné");
			addZone(getNOM_ST_RETRAITE(), "non renseigné");
			addZone(getNOM_ST_DATE_RETRAITE(), "non renseigné");
			addZone(getNOM_ST_AUTRE_PERSP(), "non renseigné");
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), "non renseigné");
		} else {
			if (evolution.getIdComEvolution() != null) {
				EaeCommentaire commEvolution = getEaeCommentaireDao().chercherEaeCommentaire(evolution.getIdComEvolution());
				addZone(getNOM_ST_COM_EVOLUTION(), commEvolution == null ? "non renseigné" : commEvolution.getCommentaire());
			} else {
				addZone(getNOM_ST_COM_EVOLUTION(), "non renseigné");
			}
			addZone(getNOM_ST_MOB_GEO(), evolution.isMobiliteGeo() ? "oui" : "non");
			addZone(getNOM_ST_MOB_FONCT(), evolution.isMobiliteFonct() ? "oui" : "non");
			addZone(getNOM_ST_CHANGEMENT_METIER(), evolution.isChangementMetier() ? "oui" : "non");
			addZone(getNOM_ST_DELAI(), evolution.getDelaiEnvisage() == null ? "non renseigné" : evolution.getDelaiEnvisage());
			addZone(getNOM_ST_MOB_SERV(), evolution.isMobiliteService() ? "oui" : "non");
			addZone(getNOM_ST_MOB_DIR(), evolution.isMobiliteDirection() ? "oui" : "non");
			addZone(getNOM_ST_MOB_COLL(), evolution.isMobiliteCollectivite() ? "oui" : "non");
			addZone(getNOM_ST_NOM_COLL(), evolution.getNomCollectivite() == null ? "non renseigné" : evolution.getNomCollectivite());
			addZone(getNOM_ST_MOB_AUTRE(), evolution.isMobiliteAutre() ? "oui" : "non");
			addZone(getNOM_ST_CONCOURS(), evolution.isConcours() ? "oui" : "non");
			addZone(getNOM_ST_NOM_CONCOURS(), evolution.getNomConcours() == null ? "non renseigné" : evolution.getNomConcours());
			addZone(getNOM_ST_VAE(), evolution.isVae() ? "oui" : "non");
			addZone(getNOM_ST_NOM_VAE(), evolution.getNomVae() == null ? "non renseigné" : evolution.getNomVae());
			addZone(getNOM_ST_TPS_PARTIEL(), evolution.isTempsPartiel() ? "oui" : "non");
			addZone(getNOM_ST_POURC_TPS_PARTIEL(), evolution.getPourcTempsPartiel() == null ? "non renseigné" : evolution.getPourcTempsPartiel()
					.toString());
			addZone(getNOM_ST_RETRAITE(), evolution.isRetraite() ? "oui" : "non");
			addZone(getNOM_ST_DATE_RETRAITE(), evolution.getDateRetraite() == null ? "non renseigné" : evolution.getDateRetraite().toString());
			addZone(getNOM_ST_AUTRE_PERSP(), evolution.isAutrePerspective() ? "oui" : "non");
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), evolution.getLibAutrePerspective() == null ? "non renseigné" : evolution.getLibAutrePerspective());
		}

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
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'eae courant
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAModifier);
		setEaeCourant(eaeCourant);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<EAE> getListeEae() {
		if (listeEae == null)
			return new ArrayList<EAE>();
		return listeEae;
	}

	public void setListeEae(ArrayList<EAE> listeEae) {
		this.listeEae = listeEae;
	}

	public EAE getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EAE eaeCourant) {
		this.eaeCourant = eaeCourant;
	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public CampagneEAEDao getCampagneEaeDao() {
		return campagneEaeDao;
	}

	public void setCampagneEaeDao(CampagneEAEDao campagneEaeDao) {
		this.campagneEaeDao = campagneEaeDao;
	}

	public EaeEvaluateurDao getEaeEvaluateurDao() {
		return eaeEvaluateurDao;
	}

	public void setEaeEvaluateurDao(EaeEvaluateurDao eaeEvaluateurDao) {
		this.eaeEvaluateurDao = eaeEvaluateurDao;
	}

	public EaeFichePosteDao getEaeFichePosteDao() {
		return eaeFichePosteDao;
	}

	public void setEaeFichePosteDao(EaeFichePosteDao eaeFichePosteDao) {
		this.eaeFichePosteDao = eaeFichePosteDao;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		// addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN() {
		return "NOM_ST_DATE_ENTRETIEN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN() {
		return getZone(getNOM_ST_DATE_ENTRETIEN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_NOM Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_NOM(int i) {
		return "NOM_ST_EVALUATEUR_NOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EVALUATEUR_NOM
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_NOM(int i) {
		return getZone(getNOM_ST_EVALUATEUR_NOM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_FONCTION
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_FONCTION(int i) {
		return "NOM_ST_EVALUATEUR_FONCTION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EVALUATEUR_FONCTION Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_FONCTION(int i) {
		return getZone(getNOM_ST_EVALUATEUR_FONCTION(i));
	}

	public ArrayList<EaeEvaluateur> getListeEvaluateurEae() {
		return listeEvaluateurEae == null ? new ArrayList<EaeEvaluateur>() : listeEvaluateurEae;
	}

	public void setListeEvaluateurEae(ArrayList<EaeEvaluateur> listeEvaluateurEae) {
		this.listeEvaluateurEae = listeEvaluateurEae;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NIVEAU Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NIVEAU() {
		return "NOM_ST_NIVEAU";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NIVEAU() {
		return getZone(getNOM_ST_NIVEAU());
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOTE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NOTE() {
		return "NOM_ST_NOTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOTE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NOTE() {
		return getZone(getNOM_ST_NOTE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_COMMENTAIRE_EVALUATEUR Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE_EVALUATEUR() {
		return "NOM_ST_COMMENTAIRE_EVALUATEUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_COMMENTAIRE_EVALUATEUR Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE_EVALUATEUR() {
		return getZone(getNOM_ST_COMMENTAIRE_EVALUATEUR());
	}

	public EaeCommentaireDao getEaeCommentaireDao() {
		return eaeCommentaireDao;
	}

	public void setEaeCommentaireDao(EaeCommentaireDao eaeCommentaireDao) {
		this.eaeCommentaireDao = eaeCommentaireDao;
	}

	public String getNOM_PB_VISUALISER_DOC(int i) {
		return "NOM_PB_VISUALISER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VISUALISER_DOC(HttpServletRequest request, int indiceEltAVisualiser) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_GED");

		// Récup de l'EAE courant
		EAE eae = (EAE) getListeEae().get(indiceEltAVisualiser);
		String finalisation = getEaeFinalisationDao().chercherDernierDocumentFinalise(eae.getIdEAE());
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + finalisation));

		return true;
	}

	public EaeFinalisationDao getEaeFinalisationDao() {
		return eaeFinalisationDao;
	}

	public void setEaeFinalisationDao(EaeFinalisationDao eaeFinalisationDao) {
		this.eaeFinalisationDao = eaeFinalisationDao;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_SHD Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_SHD() {
		return "NOM_ST_AVIS_SHD";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS_SHD Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_SHD() {
		return getZone(getNOM_ST_AVIS_SHD());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVCT_DIFF Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVCT_DIFF() {
		return "NOM_ST_AVCT_DIFF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVCT_DIFF Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVCT_DIFF() {
		return getZone(getNOM_ST_AVCT_DIFF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_CLASSE
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_CLASSE() {
		return "NOM_ST_CHANGEMENT_CLASSE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_CLASSE Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_CLASSE() {
		return getZone(getNOM_ST_CHANGEMENT_CLASSE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_REVALO Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_REVALO() {
		return "NOM_ST_AVIS_REVALO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS_REVALO
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_REVALO() {
		return getZone(getNOM_ST_AVIS_REVALO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RAPPORT_CIRCON Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_RAPPORT_CIRCON() {
		return "NOM_ST_RAPPORT_CIRCON";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RAPPORT_CIRCON
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_RAPPORT_CIRCON() {
		return getZone(getNOM_ST_RAPPORT_CIRCON());
	}

	public EaePlanActionDao getEaePlanActionDao() {
		return eaePlanActionDao;
	}

	public void setEaePlanActionDao(EaePlanActionDao eaePlanActionDao) {
		this.eaePlanActionDao = eaePlanActionDao;
	}

	public ArrayList<EaePlanAction> getListeObjectifPro() {
		return listeObjectifPro;
	}

	public void setListeObjectifPro(ArrayList<EaePlanAction> listeObjectifPro) {
		this.listeObjectifPro = listeObjectifPro;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_PRO Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_PRO(int i) {
		return "NOM_ST_LIB_OBJ_PRO" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_PRO(int i) {
		return getZone(getNOM_ST_LIB_OBJ_PRO(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_MESURE_PRO Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_MESURE_PRO(int i) {
		return "NOM_ST_LIB_MESURE_PRO" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_MESURE_PRO(int i) {
		return getZone(getNOM_ST_LIB_MESURE_PRO(i));
	}

	public ArrayList<EaePlanAction> getListeObjectifIndi() {
		return listeObjectifIndi;
	}

	public void setListeObjectifIndi(ArrayList<EaePlanAction> listeObjectifIndi) {
		this.listeObjectifIndi = listeObjectifIndi;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_INDI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_INDI(int i) {
		return "NOM_ST_LIB_OBJ_INDI" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_INDI(int i) {
		return getZone(getNOM_ST_LIB_OBJ_INDI(i));
	}

	public EaeEvolutionDao getEaeEvolutionDao() {
		return eaeEvolutionDao;
	}

	public void setEaeEvolutionDao(EaeEvolutionDao eaeEvolutionDao) {
		this.eaeEvolutionDao = eaeEvolutionDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_GEO Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_GEO() {
		return "NOM_ST_MOB_GEO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_GEO Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_GEO() {
		return getZone(getNOM_ST_MOB_GEO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_FONCT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_FONCT() {
		return "NOM_ST_MOB_FONCT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_FONCT Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_FONCT() {
		return getZone(getNOM_ST_MOB_FONCT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_METIER
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_METIER() {
		return "NOM_ST_CHANGEMENT_METIER";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_METIER Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_METIER() {
		return getZone(getNOM_ST_CHANGEMENT_METIER());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DELAI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DELAI() {
		return "NOM_ST_DELAI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DELAI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DELAI() {
		return getZone(getNOM_ST_DELAI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_SERV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_SERV() {
		return "NOM_ST_MOB_SERV";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_SERV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_SERV() {
		return getZone(getNOM_ST_MOB_SERV());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_DIR Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_DIR() {
		return "NOM_ST_MOB_DIR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_DIR Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_DIR() {
		return getZone(getNOM_ST_MOB_DIR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_COLL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_COLL() {
		return "NOM_ST_MOB_COLL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_COLL Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_COLL() {
		return getZone(getNOM_ST_MOB_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_COLL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_COLL() {
		return "NOM_ST_NOM_COLL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_COLL Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_COLL() {
		return getZone(getNOM_ST_NOM_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_AUTRE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_AUTRE() {
		return "NOM_ST_MOB_AUTRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_AUTRE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_AUTRE() {
		return getZone(getNOM_ST_MOB_AUTRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONCOURS Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CONCOURS() {
		return "NOM_ST_CONCOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CONCOURS Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CONCOURS() {
		return getZone(getNOM_ST_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_CONCOURS Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_CONCOURS() {
		return "NOM_ST_NOM_CONCOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_CONCOURS
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_CONCOURS() {
		return getZone(getNOM_ST_NOM_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_VAE Date de création
	 * : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_VAE() {
		return "NOM_ST_VAE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_VAE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_VAE() {
		return getZone(getNOM_ST_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_VAE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_VAE() {
		return "NOM_ST_NOM_VAE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_VAE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_VAE() {
		return getZone(getNOM_ST_NOM_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_PARTIEL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_TPS_PARTIEL() {
		return "NOM_ST_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TPS_PARTIEL
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_TPS_PARTIEL() {
		return getZone(getNOM_ST_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POURC_TPS_PARTIEL
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_POURC_TPS_PARTIEL() {
		return "NOM_ST_POURC_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_POURC_TPS_PARTIEL Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_POURC_TPS_PARTIEL() {
		return getZone(getNOM_ST_POURC_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RETRAITE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_RETRAITE() {
		return "NOM_ST_RETRAITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RETRAITE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_RETRAITE() {
		return getZone(getNOM_ST_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_RETRAITE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_RETRAITE() {
		return "NOM_ST_DATE_RETRAITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_RETRAITE
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_RETRAITE() {
		return getZone(getNOM_ST_DATE_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTRE_PERSP Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_AUTRE_PERSP() {
		return "NOM_ST_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AUTRE_PERSP
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_AUTRE_PERSP() {
		return getZone(getNOM_ST_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AUTRE_PERSP Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_AUTRE_PERSP() {
		return "NOM_ST_LIB_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_AUTRE_PERSP Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_AUTRE_PERSP() {
		return getZone(getNOM_ST_LIB_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COM_EVOLUTION Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_COM_EVOLUTION() {
		return "NOM_ST_COM_EVOLUTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COM_EVOLUTION Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_COM_EVOLUTION() {
		return getZone(getNOM_ST_COM_EVOLUTION());
	}
}
