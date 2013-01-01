package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeCommentaireDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeCommentaire;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
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
	private EAE eaeCourant;

	private EAEDao eaeDao;
	private CampagneEAEDao campagneEaeDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private EaeFichePosteDao eaeFichePosteDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeCommentaireDao eaeCommentaireDao;

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
				addZone(getNOM_ST_DOCUMENTS(indiceEae), "&nbsp;");
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
	 * Retourne pour la JSP le nom de la zone statique : ST_DOCUMENTS Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DOCUMENTS(int i) {
		return "NOM_ST_DOCUMENTS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DOCUMENTS Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DOCUMENTS(int i) {
		return getZone(getNOM_ST_DOCUMENTS(i));
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
		if (evaluation.getIdCommEvaluateur() != null) {
			EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(evaluation.getIdCommEvaluateur());
			addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), commEvaluateur == null ? "non renseigné" : commEvaluateur.getCommentaire());
		}
		addZone(getNOM_ST_NIVEAU(), evaluation == null || evaluation.getNiveau() == null ? "non renseigné" : evaluation.getNiveau());
		addZone(getNOM_ST_NOTE(), evaluation == null || evaluation.getNoteAnnee() == null ? "non renseigné" : evaluation.getNoteAnnee().toString());

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
}
