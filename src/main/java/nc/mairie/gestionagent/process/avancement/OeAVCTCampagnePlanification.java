package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.EAE.CampagneActeurDao;
import nc.mairie.spring.dao.metier.EAE.CampagneActionDao;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeDocumentDao;
import nc.mairie.spring.domain.metier.EAE.CampagneActeur;
import nc.mairie.spring.domain.metier.EAE.CampagneAction;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EaeDocument;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagnePlanification extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHER_AGENT = 1;
	public static final int STATUT_DESTINATAIRE = 2;

	private ArrayList<CampagneAction> listeAction;
	private CampagneAction actionCourante;

	public String ACTION_VISUALISATION = "Consultation d'une action.";
	public String ACTION_MODIFICATION = "Modification d'une action.";
	public String ACTION_CREATION = "Création d'une action.";
	public String ACTION_SUPPRESSION = "Suppression d'une action.";

	private String[] LB_ANNEE;
	private ArrayList<CampagneEAE> listeCampagneEAE;
	private CampagneEAE campagneCourante;

	private static Logger logger = Logger.getLogger(OeAVCTCampagnePlanification.class.getName());

	private CampagneActionDao campagneActionDao;
	private CampagneEAEDao campagneEAEDao;
	private CampagneActeurDao campagneActeurDao;
	private EaeDocumentDao eaeDocumentDao;

	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche visite médicale.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document d'une fiche visite médicale.";
	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private EaeDocument lienEaeDocument;
	private String urlFichier;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

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
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null && !agt.getIdAgent().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT(), agt.getNomUsage().toUpperCase() + " " + agt.getPrenom());
			addZone(getNOM_ST_ID_AGENT(), agt.getIdAgent());
		}

		initialiseDao();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// initialisation de la liste des actions
		initialiseListeAction(request);

		// initialisation de la liste des destinataires
		initialiseDestinataire();

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getCampagneActionDao() == null) {
			setCampagneActionDao((CampagneActionDao) context.getBean("campagneActionDao"));
		}

		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}

		if (getCampagneActeurDao() == null) {
			setCampagneActeurDao((CampagneActeurDao) context.getBean("campagneActeurDao"));
		}

		if (getEaeDocumentDao() == null) {
			setEaeDocumentDao((EaeDocumentDao) context.getBean("eaeDocumentDao"));
		}
	}

	private void initialiseListeAction(HttpServletRequest request) throws Exception {
		// Recherche des actions de la campagne en fonction de l'année
		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		if (indiceCampagne > -1 && !getListeCampagneEAE().isEmpty()) {
			setCampagneCourante((CampagneEAE) getListeCampagneEAE().get(indiceCampagne));

			ArrayList<CampagneAction> listeAction = getCampagneActionDao().listerCampagneActionPourCampagne(getCampagneCourante().getIdCampagneEAE());
			setListeAction(listeAction);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			int indiceAction = 0;
			for (int i = 0; i < getListeAction().size(); i++) {
				CampagneAction action = (CampagneAction) getListeAction().get(i);
				// calcul du nb de docs
				ArrayList<EaeDocument> listeDocCamp = getEaeDocumentDao().listerEaeDocument(action.getIdCampagneEAE(), action.getIdCampagneAction(),
						"ACT");
				int nbDoc = 0;
				if (listeDocCamp != null) {
					nbDoc = listeDocCamp.size();
				}
				addZone(getNOM_ST_NOM_ACTION(indiceAction), action.getNomAction());
				addZone(getNOM_ST_TRANSMETTRE(indiceAction), sdf.format(action.getDateTransmission()));
				addZone(getNOM_ST_MESSAGE(indiceAction), action.getMessage());
				AgentNW agt = AgentNW.chercherAgent(getTransaction(), action.getIdAgentRealisation().toString());
				addZone(getNOM_ST_REALISER_PAR(indiceAction), agt.getNomUsage().toUpperCase() + " " + agt.getPrenom());
				addZone(getNOM_ST_POUR_LE(indiceAction), sdf.format(action.getDateAFaireLe()));
				addZone(getNOM_ST_FAIT_LE(indiceAction), action.getDateFaitLe() == null ? "&nbsp;" : sdf.format(action.getDateFaitLe()));
				addZone(getNOM_ST_NB_DOC(indiceAction), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceAction++;
			}
		}
	}

	/**
	 * Initialisation des liste déroulantes.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			ArrayList<CampagneEAE> listeCamp = getCampagneEAEDao().listerCampagneEAE();
			setListeCampagneEAE(listeCamp);
			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator list = listeCamp.listIterator(); list.hasNext();) {
				CampagneEAE camp = (CampagneEAE) list.next();
				String ligne[] = { camp.getAnnee().toString() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		String JSP = null;
		if (null == request.getParameter("JSP")) {
			if (multi != null) {
				JSP = multi.getParameter("JSP");
			}
		} else {
			JSP = request.getParameter("JSP");
		}

		// Si on arrive de la JSP alors on traite le get
		if (JSP != null && JSP.equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_LANCER
			if (testerParametre(request, getNOM_PB_LANCER())) {
				return performPB_LANCER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAction().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISER
			for (int i = 0; i < getListeAction().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeAction().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DESTINATAIRE
			for (int i = 0; i < getListeDestinataireMulti().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DESTINATAIRE(i))) {
					return performPB_SUPPRIMER_DESTINATAIRE(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_DESTINATAIRE
			if (testerParametre(request, getNOM_PB_AJOUTER_DESTINATAIRE())) {
				return performPB_AJOUTER_DESTINATAIRE(request);
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_CONSULTER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(i))) {
					return performPB_CONSULTER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(i))) {
					return performPB_SUPPRIMER_DOC(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTCampagnePlanification. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTCampagnePlanification() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTCampagnePlanification.jsp";
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		videZonesDeSaisie(request);
		setStatut(STATUT_MEME_PROCESS);
		return true;
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
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneAction actionCourante = (CampagneAction) getListeAction().get(indiceEltAModifier);
		setActionCourante(actionCourante);

		if (!initialiseActionCourante(request))
			return false;

		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseActionCourante(HttpServletRequest request) throws Exception {
		videZonesDeSaisie(request);

		// Alim zones
		CampagneAction action = getActionCourante();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_ST_NOM_ACTION(), action.getNomAction());
		addZone(getNOM_ST_MESSAGE(), action.getMessage());
		addZone(getNOM_ST_TRANSMETTRE(), action.getDateTransmission() == null ? Const.CHAINE_VIDE : sdf.format(action.getDateTransmission()));
		AgentNW agt = AgentNW.chercherAgent(getTransaction(), action.getIdAgentRealisation().toString());
		addZone(getNOM_ST_AGENT(), agt.getNomUsage().toUpperCase() + " " + agt.getPrenom());
		addZone(getNOM_ST_ID_AGENT(), action.getIdAgentRealisation().toString());
		addZone(getNOM_ST_NOM_ACTION(), action.getNomAction());
		addZone(getNOM_ST_POUR_LE(), action.getDateAFaireLe() == null ? Const.CHAINE_VIDE : sdf.format(action.getDateAFaireLe()));
		addZone(getNOM_ST_FAIT_LE(), action.getDateFaitLe() == null ? Const.CHAINE_VIDE : sdf.format(action.getDateFaitLe()));
		addZone(getNOM_ST_COMMENTAIRE(), action.getCommentaire());

		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une
	 * carriere
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NOM_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MESSAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TRANSMETTRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_AGENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_POUR_LE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_FAIT_LE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE(), Const.CHAINE_VIDE);
		setListeDestinataireMulti(null);
		setListeDocuments(null);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneAction actionCourante = (CampagneAction) getListeAction().get(indiceEltAConsulter);
		setActionCourante(actionCourante);

		// init de l'action courante
		if (!initialiseActionCourante(request))
			return false;

		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER Date de création :
	 * (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASupprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneAction actionCourante = (CampagneAction) getListeAction().get(indiceEltASupprimer);
		setActionCourante(actionCourante);

		// init de l'action courante
		if (!initialiseActionCourante(request))
			return false;

		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-CAMPAGNE-PLANIF";
	}

	public ArrayList<CampagneAction> getListeAction() {
		if (listeAction == null)
			return new ArrayList<CampagneAction>();
		return listeAction;
	}

	public void setListeAction(ArrayList<CampagneAction> listeAction) {
		this.listeAction = listeAction;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG-EAE-2
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		setActionCourante(null);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION) || getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {

			// vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			if (!performRegleGestion(request))
				return false;

			alimenterAction(request);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getCampagneActionDao().modifierCampagneAction(getActionCourante().getIdCampagneAction(), getActionCourante().getNomAction(),
						getActionCourante().getMessage(), getActionCourante().getDateTransmission(), getActionCourante().getDateAFaireLe(),
						getActionCourante().getDateFaitLe(), getActionCourante().getCommentaire(), getActionCourante().getIdAgentRealisation());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				Integer idAction = getCampagneActionDao().creerCampagneAction(getActionCourante().getNomAction(), getActionCourante().getMessage(),
						getActionCourante().getDateTransmission(), getActionCourante().getDateAFaireLe(), getActionCourante().getDateFaitLe(),
						getActionCourante().getCommentaire(), getActionCourante().getIdAgentRealisation(), getCampagneCourante().getIdCampagneEAE());
				getActionCourante().setIdCampagneAction(idAction);
			}
			if (getTransaction().isErreur())
				return false;

			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_CREATION(request);

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			// suppression des acteurs
			getCampagneActeurDao().supprimerTousCampagneActeurCampagne(getActionCourante().getIdCampagneAction());

			// Suppression de l'action
			getCampagneActionDao().supprimerCampagneAction(getActionCourante().getIdCampagneAction());
			setActionCourante(null);

			// il faut supprimer les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document d = getListeDocuments().get(i);
				EaeDocument lien = getEaeDocumentDao().chercherEaeDocument(Integer.valueOf(d.getIdDocument()));
				// suppression dans table EAE_DOCUMENT
				getEaeDocumentDao().supprimerEaeDocument(lien.getIdEaeDocument());
				// Suppression dans la table DOCUMENT_ASSOCIE
				d.supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = getDocumentCourant().getLienDocument();
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.severe("Erreur suppression physique du fichier : " + e.toString());
				}
			}
		}

		// on sauvegarde les destinataires
		for (int i = 0; i < getListeDestinataireMulti().size(); i++) {
			AgentNW a = getListeDestinataireMulti().get(i);
			ajouterDestinataire(a);
		}

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void alimenterAction(HttpServletRequest request) throws ParseException {
		// récupération des informations remplies dans les zones de saisie
		if (getActionCourante() == null)
			setActionCourante(new CampagneAction());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		getActionCourante().setNomAction(getVAL_ST_NOM_ACTION());
		getActionCourante().setMessage(getVAL_ST_MESSAGE());
		getActionCourante().setDateTransmission(sdf.parse(getVAL_ST_TRANSMETTRE()));
		getActionCourante().setDateAFaireLe(sdf.parse(getVAL_ST_POUR_LE()));
		getActionCourante().setDateFaitLe(getVAL_ST_FAIT_LE().equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_ST_FAIT_LE()));
		getActionCourante().setCommentaire(getVAL_ST_COMMENTAIRE());
		getActionCourante().setIdAgentRealisation(Integer.valueOf(getVAL_ST_ID_AGENT()));
	}

	private boolean performRegleGestion(HttpServletRequest request) {
		if (getActionCourante() != null && isMailDiffuse(getActionCourante())) {
			return true;
		}
		// La date de l'action doit être supérieure à la date du jour
		if (Services.compareDates(getVAL_ST_TRANSMETTRE(), Services.dateDuJour()) <= 0) {
			// "ERR170",
			// "La date du champ 'transmettre le' doit être supérieure à la date du jour."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR170"));
			return false;
		}
		return true;
	}

	public boolean isMailDiffuse(CampagneAction action) {
		if (getCampagneCourante() == null || action == null) {
			return false;
		}
		// La date de l'action doit être supérieure à la date du jour
		if (Services.compareDates(action.getDateTransmission().toString(), Services.dateDuJour()) <= 0) {
			// alors le message est diffuse
			return true;
		}
		// TODO
		return false;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// action obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_NOM_ACTION())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "action"));
			return false;
		}
		// message obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_MESSAGE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "message"));
			return false;
		}
		// à transmettre le obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_TRANSMETTRE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à transmettre le"));
			return false;
		}

		// format à transmettre le
		if (!Services.estUneDate(getVAL_ST_TRANSMETTRE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "à transmettre le"));
			return false;
		}
		// à réaliser par obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_AGENT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à réaliser par"));
			return false;
		}
		// à faire pour le le obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_POUR_LE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à faire pour le"));
			return false;
		}

		// format à transmettre le
		if (!Services.estUneDate(getVAL_ST_POUR_LE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "à faire pour le"));
			return false;
		}

		// **********************************
		// Verification Destinataire
		// **********************************
		if (getListeDestinataireMulti() == null || getListeDestinataireMulti().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Destinataire"));
			return false;
		}
		return true;
	}

	public CampagneAction getActionCourante() {
		return actionCourante;
	}

	public void setActionCourante(CampagneAction actionCourante) {
		this.actionCourante = actionCourante;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_ACTION Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_ACTION(int i) {
		return "NOM_ST_NOM_ACTION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_ACTION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_ACTION(int i) {
		return getZone(getNOM_ST_NOM_ACTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TRANSMETTRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TRANSMETTRE(int i) {
		return "NOM_ST_TRANSMETTRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TRANSMETTRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TRANSMETTRE(int i) {
		return getZone(getNOM_ST_TRANSMETTRE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MESSAGE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MESSAGE(int i) {
		return "NOM_ST_MESSAGE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MESSAGE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MESSAGE(int i) {
		return getZone(getNOM_ST_MESSAGE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REALISER_PAR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REALISER_PAR(int i) {
		return "NOM_ST_REALISER_PAR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REALISER_PAR
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REALISER_PAR(int i) {
		return getZone(getNOM_ST_REALISER_PAR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POUR_LE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_POUR_LE(int i) {
		return "NOM_ST_POUR_LE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_POUR_LE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_POUR_LE(int i) {
		return getZone(getNOM_ST_POUR_LE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FAIT_LE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_FAIT_LE(int i) {
		return "NOM_ST_FAIT_LE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FAIT_LE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_FAIT_LE(int i) {
		return getZone(getNOM_ST_FAIT_LE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_NOM_ACTION Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_NOM_ACTION() {
		return "NOM_ST_NOM_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_NOM_ACTION Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_NOM_ACTION() {
		return getZone(getNOM_ST_NOM_ACTION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_MESSAGE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_MESSAGE() {
		return "NOM_ST_MESSAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_MESSAGE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_MESSAGE() {
		return getZone(getNOM_ST_MESSAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_TRANSMETTRE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_TRANSMETTRE() {
		return "NOM_ST_TRANSMETTRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_TRANSMETTRE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_TRANSMETTRE() {
		return getZone(getNOM_ST_TRANSMETTRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_COMMENTAIRE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE() {
		return "NOM_ST_COMMENTAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_COMMENTAIRE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE() {
		return getZone(getNOM_ST_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_FAIT_LE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_FAIT_LE() {
		return "NOM_ST_FAIT_LE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_FAIT_LE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_FAIT_LE() {
		return getZone(getNOM_ST_FAIT_LE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_POUR_LE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_POUR_LE() {
		return "NOM_ST_POUR_LE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_POUR_LE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_POUR_LE() {
		return getZone(getNOM_ST_POUR_LE());
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_ID_AGENT() {
		return "NOM_ST_ID_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_AGENT Date
	 * de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_ID_AGENT() {
		return getZone(getNOM_ST_ID_AGENT());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_AGENT(), Const.CHAINE_VIDE);
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public boolean performPB_LANCER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		setListeAction(null);
		setCampagneCourante(null);
		setActionCourante(null);

		return true;
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	private ArrayList<CampagneEAE> getListeCampagneEAE() {
		if (listeCampagneEAE == null)
			return new ArrayList<CampagneEAE>();
		return listeCampagneEAE;
	}

	private void setListeCampagneEAE(ArrayList<CampagneEAE> listeCampagneEAE) {
		this.listeCampagneEAE = listeCampagneEAE;
	}

	public CampagneEAE getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEAE campagneCourante) {
		this.campagneCourante = campagneCourante;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DESTINATAIRE Date de
	 * création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DESTINATAIRE() {
		return "NOM_PB_AJOUTER_DESTINATAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_DESTINATAIRE(HttpServletRequest request) throws Exception {
		ArrayList listeDesti = new ArrayList();
		if (getListeDestinataireMulti() != null) {
			listeDesti.addAll(getListeDestinataireMulti());
		}
		VariablesActivite.ajouter(this, "LISTEACTEURS", listeDesti);
		setStatut(STATUT_DESTINATAIRE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DESTINATAIRE Date
	 * de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DESTINATAIRE(int i) {
		return "NOM_PB_SUPPRIMER_DESTINATAIRE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DESTINATAIRE(HttpServletRequest request, int elemASupprimer) throws Exception {
		AgentNW a = (AgentNW) getListeDestinataireMulti().get(elemASupprimer);

		if (a != null) {
			if (getListeDestinataireMulti() != null) {
				getListeDestinataireMulti().remove(a);
				supprimeDestinataire(a);
			}
		}

		return true;
	}

	private void supprimeDestinataire(AgentNW a) throws Exception {
		if (getActionCourante() != null) {
			CampagneActeur acteur = getCampagneActeurDao().chercherCampagneActeur(getActionCourante().getIdCampagneAction(),
					Integer.valueOf(a.getIdAgent()));
			if (acteur != null) {
				getCampagneActeurDao().supprimerCampagneActeur(acteur.getIdCampagneActeur());
			}
		} else {
			if (getListeDestinataireMulti().contains(a)) {
				getListeDestinataireMulti().remove(a);
			}
		}
	}

	private ArrayList<AgentNW> listeDestinataireMulti;

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeDestinataireMulti ArrayList
	 */
	public ArrayList<AgentNW> getListeDestinataireMulti() {
		if (listeDestinataireMulti == null)
			listeDestinataireMulti = new ArrayList();
		return listeDestinataireMulti;
	}

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeDestinataireMulti
	 *            ArrayList
	 */
	private void setListeDestinataireMulti(ArrayList<AgentNW> listeDestinataireMulti) {
		this.listeDestinataireMulti = listeDestinataireMulti;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_AGENT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	/**
	 * Initialise la liste des Destinataire sélectionnées par l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseDestinataire() throws Exception {
		if (getActionCourante() != null) {
			if (getListeDestinataireMulti().size() == 0 && getActionCourante().getIdCampagneAction() != null) {
				ArrayList<CampagneActeur> listeDestinataire = getCampagneActeurDao().listerCampagneActeur(getActionCourante().getIdCampagneAction());
				ArrayList<AgentNW> listeAgentDestinataire = new ArrayList<AgentNW>();
				for (int i = 0; i < listeDestinataire.size(); i++) {
					CampagneActeur campAct = listeDestinataire.get(i);
					AgentNW ag = AgentNW.chercherAgent(getTransaction(), campAct.getIdAgent().toString());
					listeAgentDestinataire.add(ag);
				}
				setListeDestinataireMulti(listeAgentDestinataire);
			}
		}
		// on recupere les Destinataire selectionnées dans l'ecran de
		// selection
		ArrayList listeDestinataireSelect = (ArrayList) VariablesActivite.recuperer(this, "ACTEURS");
		if (listeDestinataireSelect != null && listeDestinataireSelect.size() != 0) {
			for (int i = 0; i < listeDestinataireSelect.size(); i++) {
				AgentNW a = (AgentNW) listeDestinataireSelect.get(i);
				if (a != null) {
					if (!getListeDestinataireMulti().contains(a)) {
						getListeDestinataireMulti().add(a);
						ajouterDestinataire(a);
					}
				}
			}

		}
		VariablesActivite.enlever(this, "ACTEURS");

		int indiceActeur = 0;
		if (getListeDestinataireMulti() != null) {
			for (int i = 0; i < getListeDestinataireMulti().size(); i++) {
				AgentNW ag = (AgentNW) getListeDestinataireMulti().get(i);
				addZone(getNOM_ST_LIB_AGENT(indiceActeur), ag.getNomUsage() + " " + ag.getPrenom());

				indiceActeur++;
			}
		}

	}

	private void ajouterDestinataire(AgentNW a) throws Exception {
		if (getActionCourante() != null) {
			// Sauvegarde des nouveaux acteurs
			try {
				CampagneActeur campAct = getCampagneActeurDao().chercherCampagneActeur(getActionCourante().getIdCampagneAction(),
						Integer.valueOf(a.getIdAgent()));
			} catch (Exception e) {
				getCampagneActeurDao().creerCampagneActeur(getActionCourante().getIdCampagneAction(), Integer.valueOf(a.getIdAgent()));
			}
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NB_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public CampagneActionDao getCampagneActionDao() {
		return campagneActionDao;
	}

	public void setCampagneActionDao(CampagneActionDao campagneActionDao) {
		this.campagneActionDao = campagneActionDao;
	}

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}

	public CampagneActeurDao getCampagneActeurDao() {
		return campagneActeurDao;
	}

	public void setCampagneActeurDao(CampagneActeurDao campagneActeurDao) {
		this.campagneActeurDao = campagneActeurDao;
	}

	public EaeDocumentDao getEaeDocumentDao() {
		return eaeDocumentDao;
	}

	public void setEaeDocumentDao(EaeDocumentDao eaeDocumentDao) {
		this.eaeDocumentDao = eaeDocumentDao;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null)
			return new ArrayList<Document>();
		return listeDocuments;
	}

	public void setListeDocuments(ArrayList<Document> listeDocuments) {
		this.listeDocuments = listeDocuments;
	}

	public Document getDocumentCourant() {
		return documentCourant;
	}

	public void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_DOC Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");

		// Récup du document courant
		Document d = (Document) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		return true;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT Date
	 * de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION_DOCUMENT() {
		return "NOM_ST_ACTION_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION_DOCUMENT() {
		return getZone(getNOM_ST_ACTION_DOCUMENT());
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_DATE_DOC() {
		return getZone(getNOM_ST_DATE_DOC());
	}

	public String getNOM_ST_DATE_DOC() {
		return "NOM_ST_DATE_DOC";
	}

	public String getVAL_ST_COMMENTAIRE_DOC() {
		return getZone(getNOM_ST_COMMENTAIRE_DOC());
	}

	public String getNOM_ST_COMMENTAIRE_DOC() {
		return "NOM_ST_COMMENTAIRE_DOC";
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	/**
	 * Initialisation de la liste des documents
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de la campagne

		ArrayList<EaeDocument> listeDoc = getEaeDocumentDao().listerEaeDocument(getCampagneCourante().getIdCampagneEAE(),
				getActionCourante().getIdCampagneAction(), "ACT");
		setListeDocuments(new ArrayList<Document>());
		for (int i = 0; i < listeDoc.size(); i++) {
			EaeDocument lien = listeDoc.get(i);
			Document d = Document.chercherDocumentById(getTransaction(), lien.getIdDocument().toString());
			getListeDocuments().add(d);
		}

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument()
						.trim());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire()
						.trim());

				indiceActeVM++;
			}
		}

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC Date de création :
	 * (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_DOC Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		// Récup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {

		// Récup du Diplome courant
		Document d = getDocumentCourant();

		EaeDocument ldoc = getEaeDocumentDao().chercherEaeDocument(Integer.valueOf(getDocumentCourant().getIdDocument()));
		setLienEaeDocument(ldoc);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_DATE_DOC(), d.getDateDocument());
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	public EaeDocument getLienEaeDocument() {
		return lienEaeDocument;
	}

	public void setLienEaeDocument(EaeDocument lienEaeDocument) {
		this.lienEaeDocument = lienEaeDocument;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {

			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());

				// Contrôle des champs
				if (!performControlerSaisieDocument(request))
					return false;

				CampagneEAE camp = getCampagneCourante();

				if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_CREATION)) {
					if (!creeDocument(request, camp)) {
						return false;
					}
				}
			}
		} else {
			if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_SUPPRESSION)) {
				// suppression dans table EAE_DOCUMENT
				getEaeDocumentDao().supprimerEaeDocument(getLienEaeDocument().getIdEaeDocument());
				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentCourant().supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = getDocumentCourant().getLienDocument();
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.severe("Erreur suppression physique du fichier : " + e.toString());
				}

				// tout s'est bien passé
				commitTransaction();
			}
		}

		initialiseListeDocuments(request);
		return true;
	}

	private boolean creeDocument(HttpServletRequest request, CampagneEAE camp) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupère le type de document
		String codTypeDoc = "ACT";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + camp.getIdCampagneEAE() + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf"))
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		else
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);

		if (!upload)
			return false;

		// on crée le document en base de données
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().creerDocument(getTransaction());

		setLienEaeDocument(new EaeDocument());
		getLienEaeDocument().setIdCampagneEae(camp.getIdCampagneEAE());
		getLienEaeDocument().setIdDocument(Integer.valueOf(getDocumentCourant().getIdDocument()));
		getLienEaeDocument().setTypeDocument(codTypeDoc);
		getLienEaeDocument().setIdCampagneAction(getActionCourante().getIdCampagneAction());
		getEaeDocumentDao().creerEaeDocument(getLienEaeDocument().getIdCampagneEae(), getLienEaeDocument().getIdCampagneAction(),
				getLienEaeDocument().getIdDocument(), getLienEaeDocument().getTypeDocument());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean uploadFichierPDF(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		File newFile = new File(repPartage + codTypeDoc + "/" + nomFichier);

		FileInputStream in = new FileInputStream(f);

		try {
			FileOutputStream out = new FileOutputStream(newFile);
			try {
				byte[] byteBuffer = new byte[in.available()];
				int s = in.read(byteBuffer);
				out.write(byteBuffer);
				out.flush();
				resultat = true;
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

		return resultat;
	}

	private boolean uploadFichier(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.toFileObject(f);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(repPartage + codTypeDoc + "/" + nomFichier);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		try {
			while ((ligne = in.readLine()) != null) {
				out.write(ligne);
			}
			resultat = true;
		} catch (Exception e) {
			logger.severe("erreur d'execution " + e.toString());
		}

		// FERMETURE DES FLUX
		in.close();
		inR.close();
		is.close();
		fo.close();

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();

		return resultat;
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	/**
	 * Méthode qui teste si un paramètre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi.getParameter(param) != null));
	}

	/**
	 * Process incoming requests for information*
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			multi = new com.oreilly.servlet.MultipartRequest(request, repTemp, 10 * 1024 * 1024);
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}
}