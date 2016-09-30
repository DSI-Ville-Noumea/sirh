package nc.mairie.gestionagent.process.avancement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneActeursDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneActionDto;
import nc.mairie.gestionagent.eae.dto.EaeDocumentDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.alfresco.cmis.CmisUtils;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagnePlanification extends BasicProcess {
	/**
	 * 
	 */
	private static final long			serialVersionUID			= 1L;
	public static final int				STATUT_RECHERCHER_AGENT		= 1;
	public static final int				STATUT_DESTINATAIRE			= 2;

	private List<EaeCampagneActionDto>	listeAction;
	private EaeCampagneActionDto		actionCourante;

	public String						ACTION_VISUALISATION		= "Consultation d'une action.";
	public String						ACTION_MODIFICATION			= "Modification d'une action.";
	public String						ACTION_CREATION				= "Création d'une action.";
	public String						ACTION_SUPPRESSION			= "Suppression d'une action.";

	private String[]					LB_ANNEE;
	private List<CampagneEaeDto>		listeCampagneEAE;
	private CampagneEaeDto				campagneCourante;

	public String						ACTION_DOCUMENT_SUPPRESSION	= "Suppression d'un document d'une fiche visite médicale.";
	public String						ACTION_DOCUMENT_CREATION	= "Création d'un document d'une fiche visite médicale.";
	private ArrayList<Document>			listeDocuments;
	private Document					documentCourant;
	private EaeDocumentDto				lienEaeDocument;
	public boolean						isImporting					= false;
	public MultipartRequest				multi						= null;
	public File							fichierUpload				= null;

	private ArrayList<Agent>			listeDestinataireMulti;

	private TypeDocumentDao				typeDocumentDao;
	private DocumentDao					documentDao;
	private AgentDao					agentDao;

	private IAlfrescoCMISService		alfrescoCMISService;
	private IRadiService				radiService;
	private IEaeService					eaeService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null) {
			addZone(getNOM_ST_AGENT(), agt.getNomAgent().toUpperCase() + " " + agt.getPrenomAgent());
			addZone(getNOM_ST_ID_AGENT(), agt.getIdAgent().toString());
		}

		// Initialisation des listes deroulantes
		initialiseListeDeroulante(request);

		// initialisation de la liste des actions
		initialiseListeAction(request);

		// initialisation de la liste des destinataires
		initialiseDestinataire();

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (alfrescoCMISService == null) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
		}
		if (radiService == null) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
	}

	private void initialiseListeAction(HttpServletRequest request) throws Exception {
		// Recherche des actions de la campagne en fonction de l'année
		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		if (indiceCampagne > -1 && !getListeCampagneEAE().isEmpty()) {
			setCampagneCourante((CampagneEaeDto) getListeCampagneEAE().get(indiceCampagne));

			List<EaeCampagneActionDto> listeAction = getCampagneCourante().getListeCampagneAction();
			setListeAction(listeAction);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			int indiceAction = 0;
			for (int i = 0; i < getListeAction().size(); i++) {
				EaeCampagneActionDto action = (EaeCampagneActionDto) getListeAction().get(i);
				// calcul du nb de docs
				List<EaeDocumentDto> listeDocCamp = action.getListeEaeDocument();
				int nbDoc = 0;
				if (listeDocCamp != null) {
					nbDoc = listeDocCamp.size();
				}
				addZone(getNOM_ST_NOM_ACTION(indiceAction), action.getNomAction());
				addZone(getNOM_ST_TRANSMETTRE(indiceAction), sdf.format(action.getDateTransmission()));
				addZone(getNOM_ST_MESSAGE(indiceAction), action.getMessage());
				Agent agt = getAgentDao().chercherAgent(action.getIdAgentRealisation());
				addZone(getNOM_ST_REALISER_PAR(indiceAction), agt.getNomAgent().toUpperCase() + " " + agt.getPrenomAgent());
				addZone(getNOM_ST_POUR_LE(indiceAction), sdf.format(action.getDateAFaireLe()));
				addZone(getNOM_ST_FAIT_LE(indiceAction), action.getDateFaitLe() == null ? "&nbsp;" : sdf.format(action.getDateFaitLe()));
				addZone(getNOM_ST_NB_DOC(indiceAction), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceAction++;
			}
		}
	}

	/**
	 * Initialisation des liste deroulantes.
	 */
	private void initialiseListeDeroulante(HttpServletRequest request) throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			List<CampagneEaeDto> listeCamp = eaeService.getListeCampagnesEae(getAgentConnecte(request).getIdAgent());
			setListeCampagneEAE(listeCamp);
			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<CampagneEaeDto> list = listeCamp.listIterator(); list.hasNext();) {
				CampagneEaeDto camp = (CampagneEaeDto) list.next();
				String ligne[] = { camp.getAnnee().toString() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		EaeCampagneActionDto actionCourante = (EaeCampagneActionDto) getListeAction().get(indiceEltAModifier);
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
		EaeCampagneActionDto action = getActionCourante();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_ST_NOM_ACTION(), action.getNomAction());
		addZone(getNOM_ST_MESSAGE(), action.getMessage());
		addZone(getNOM_ST_TRANSMETTRE(), action.getDateTransmission() == null ? Const.CHAINE_VIDE : sdf.format(action.getDateTransmission()));
		Agent agt = getAgentDao().chercherAgent(action.getIdAgentRealisation());
		addZone(getNOM_ST_AGENT(), agt.getNomAgent().toUpperCase() + " " + agt.getPrenomAgent());
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		EaeCampagneActionDto actionCourante = (EaeCampagneActionDto) getListeAction().get(indiceEltAConsulter);
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASupprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		EaeCampagneActionDto actionCourante = (EaeCampagneActionDto) getListeAction().get(indiceEltASupprimer);
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date
	 * de création : (05/09/11 11:39:24)
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

	public List<EaeCampagneActionDto> getListeAction() {
		if (listeAction == null)
			return new ArrayList<EaeCampagneActionDto>();
		return listeAction;
	}

	public void setListeAction(List<EaeCampagneActionDto> listeAction) {
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			if (!performRegleGestion(request))
				return false;

			alimenterAction(request);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				for (EaeCampagneActionDto actionExistante : getCampagneCourante().getListeCampagneAction()) {
					if (null != actionExistante.getIdCampagneAction() && null != getActionCourante().getIdCampagneAction()
							&& actionExistante.getIdCampagneAction().equals(getActionCourante().getIdCampagneAction())) {
						actionExistante.setDateTransmission(getActionCourante().getDateTransmission());
						actionExistante.setDateAFaireLe(getActionCourante().getDateAFaireLe());
						actionExistante.setDateFaitLe(getActionCourante().getDateFaitLe());
						actionExistante.setIdAgentRealisation(getActionCourante().getIdAgentRealisation());
						actionExistante.setNomAction(getActionCourante().getNomAction());
						actionExistante.setMessage(getActionCourante().getMessage());
						actionExistante.setCommentaire(getActionCourante().getCommentaire());
					}
				}
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				EaeCampagneActionDto newAction = new EaeCampagneActionDto();
				newAction.setDateTransmission(getActionCourante().getDateTransmission());
				newAction.setDateAFaireLe(getActionCourante().getDateAFaireLe());
				newAction.setDateFaitLe(getActionCourante().getDateFaitLe());
				newAction.setIdAgentRealisation(getActionCourante().getIdAgentRealisation());
				newAction.setNomAction(getActionCourante().getNomAction());
				newAction.setMessage(getActionCourante().getMessage());
				newAction.setCommentaire(getActionCourante().getCommentaire());

				getCampagneCourante().getListeCampagneAction().add(newAction);
			}
			if (getTransaction().isErreur())
				return false;

			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_CREATION(request);

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			// suppression des acteurs
			for (EaeCampagneActionDto actionExistante : getCampagneCourante().getListeCampagneAction()) {
				if (null != actionExistante.getIdCampagneAction() && null != getActionCourante().getIdCampagneAction()
						&& actionExistante.getIdCampagneAction().equals(getActionCourante().getIdCampagneAction())) {
					getCampagneCourante().getListeCampagneAction().remove(getActionCourante());
					break;
				}
			}

			// il faut supprimer les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document d = getListeDocuments().get(i);

				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;

				// suppression dans table EAE_DOCUMENT
				ReturnMessageDto result = eaeService.deleteDocumentEae(getAgentConnecte(request).getIdAgent(),
						getLienEaeDocument().getIdEaeDocument());
				if (!result.getErrors().isEmpty()) {
					getTransaction().declarerErreur(result.getErrors().get(0).toString());
					return false;
				}

				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentDao().supprimerDocument(d.getIdDocument());

				if (getTransaction().isErreur())
					return false;

			}
		}

		// on sauvegarde les destinataires
		for (int i = 0; i < getListeDestinataireMulti().size(); i++) {
			Agent a = getListeDestinataireMulti().get(i);
			ajouterDestinataire(a);
		}

		ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
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

	private boolean declarerErreurFromReturnMessageDto(ReturnMessageDto rmd) {

		if (!rmd.getErrors().isEmpty()) {
			String errors = "";
			for (String error : rmd.getErrors()) {
				errors += error;
			}

			getTransaction().declarerErreur("Err : " + errors);
			return true;
		}
		return false;
	}

	private void alimenterAction(HttpServletRequest request) throws ParseException {
		// récupération des informations remplies dans les zones de saisie
		if (getActionCourante() == null)
			setActionCourante(new EaeCampagneActionDto());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		getActionCourante().setNomAction(getVAL_ST_NOM_ACTION());
		getActionCourante().setMessage(getVAL_ST_MESSAGE());
		getActionCourante().setDateTransmission(sdf.parse(getVAL_ST_TRANSMETTRE()));
		getActionCourante().setDateAFaireLe(sdf.parse(getVAL_ST_POUR_LE()));
		getActionCourante().setDateFaitLe(getVAL_ST_FAIT_LE().equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_ST_FAIT_LE()));
		getActionCourante().setCommentaire(getVAL_ST_COMMENTAIRE());
		getActionCourante().setIdAgentRealisation(Integer.valueOf(getVAL_ST_ID_AGENT()));
	}

	private boolean performRegleGestion(HttpServletRequest request) throws Exception {
		if (getActionCourante() != null && isMailDiffuse(getActionCourante())) {
			return true;
		}
		// La date de l'action doit être supérieure à  la date du jour
		if (Services.compareDates(getVAL_ST_TRANSMETTRE(), Services.dateDuJour()) <= 0) {
			// "ERR170",
			// "La date du champ 'transmettre le' doit être supérieure à  la
			// date du jour."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR170"));
			return false;
		}
		return true;
	}

	public boolean isMailDiffuse(EaeCampagneActionDto action) throws Exception {
		if (getCampagneCourante() == null || action == null) {
			return false;
		}
		// La date de l'action doit être supérieure à  la date du jour
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateTransmission = sdf.format(action.getDateTransmission());
		if (Services.compareDates(dateTransmission, Services.dateDuJour()) <= 0) {
			// alors le message est diffuse
			return true;
		}
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
		// a transmettre le obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_TRANSMETTRE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à  transmettre le"));
			return false;
		}

		// format a transmettre le
		if (!Services.estUneDate(getVAL_ST_TRANSMETTRE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "à  transmettre le"));
			return false;
		}
		// a realiser par obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_AGENT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à  réaliser par"));
			return false;
		}
		// a faire pour le le obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_POUR_LE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "à  faire pour le"));
			return false;
		}

		// format a transmettre le
		if (!Services.estUneDate(getVAL_ST_POUR_LE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "à  faire pour le"));
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

	public EaeCampagneActionDto getActionCourante() {
		return actionCourante;
	}

	public void setActionCourante(EaeCampagneActionDto actionCourante) {
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NOM_ACTION
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TRANSMETTRE
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MESSAGE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_REALISER_PAR
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_POUR_LE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_FAIT_LE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ID_AGENT Date
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
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	private List<CampagneEaeDto> getListeCampagneEAE() {
		if (listeCampagneEAE == null)
			return new ArrayList<CampagneEaeDto>();
		return listeCampagneEAE;
	}

	private void setListeCampagneEAE(List<CampagneEaeDto> listeCampagneEAE) {
		this.listeCampagneEAE = listeCampagneEAE;
	}

	public CampagneEaeDto getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEaeDto campagneCourante) {
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_DESTINATAIRE(HttpServletRequest request) throws Exception {
		ArrayList<Agent> listeDesti = new ArrayList<Agent>();
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DESTINATAIRE(HttpServletRequest request, int elemASupprimer) throws Exception {
		Agent a = (Agent) getListeDestinataireMulti().get(elemASupprimer);

		if (a != null) {
			if (getListeDestinataireMulti() != null) {
				getListeDestinataireMulti().remove(a);
				supprimeDestinataire(a);
			}
		}

		return true;
	}

	private void supprimeDestinataire(Agent a) throws Exception {
		if (getActionCourante() != null) {
			for (EaeCampagneActeursDto campagneActeurDto : getActionCourante().getListeCampagneActeurs()) {
				if (null != campagneActeurDto.getIdAgent() && campagneActeurDto.getIdAgent().equals(a.getIdAgent())) {
					getActionCourante().getListeCampagneActeurs().remove(campagneActeurDto);
					break;
				}
			}
		} else {
			if (getListeDestinataireMulti().contains(a)) {
				getListeDestinataireMulti().remove(a);
			}
		}
	}

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeDestinataireMulti ArrayList
	 */
	public ArrayList<Agent> getListeDestinataireMulti() {
		if (listeDestinataireMulti == null)
			listeDestinataireMulti = new ArrayList<Agent>();
		return listeDestinataireMulti;
	}

	/**
	 * Met a jour la liste des activités principales.
	 * 
	 * @param listeDestinataireMulti
	 *            ArrayList
	 */
	private void setListeDestinataireMulti(ArrayList<Agent> listeDestinataireMulti) {
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_AGENT
	 * Date de création : (18/08/11 10:21:15)
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
				List<EaeCampagneActeursDto> listeDestinataire = getActionCourante().getListeCampagneActeurs();
				ArrayList<Agent> listeAgentDestinataire = new ArrayList<Agent>();
				for (int i = 0; i < listeDestinataire.size(); i++) {
					EaeCampagneActeursDto campAct = listeDestinataire.get(i);
					Agent ag = getAgentDao().chercherAgent(campAct.getIdAgent());
					listeAgentDestinataire.add(ag);
				}
				setListeDestinataireMulti(listeAgentDestinataire);
			}
		}
		// on recupere les Destinataire selectionnées dans l'ecran de
		// selection
		@SuppressWarnings("unchecked")
		ArrayList<Agent> listeDestinataireSelect = (ArrayList<Agent>) VariablesActivite.recuperer(this, "ACTEURS");
		if (listeDestinataireSelect != null && listeDestinataireSelect.size() != 0) {
			for (int i = 0; i < listeDestinataireSelect.size(); i++) {
				Agent a = (Agent) listeDestinataireSelect.get(i);
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
				Agent ag = (Agent) getListeDestinataireMulti().get(i);
				addZone(getNOM_ST_LIB_AGENT(indiceActeur), ag.getNomAgent() + " " + ag.getPrenomAgent());

				indiceActeur++;
			}
		}

	}

	private void ajouterDestinataire(Agent a) throws Exception {
		if (getActionCourante() != null && null != getActionCourante().getListeCampagneActeurs()) {
			// Sauvegarde des nouveaux acteurs
			boolean isActeurDejaExistant = false;
			for (EaeCampagneActeursDto acteur : getActionCourante().getListeCampagneActeurs()) {
				if (null != acteur.getIdAgent() && acteur.getIdAgent().equals(a.getIdAgent())) {
					isActeurDejaExistant = true;
					break;
				}
			}
			if (isActeurDejaExistant) {
				EaeCampagneActeursDto newActeur = new EaeCampagneActeursDto();
				newActeur.setIdAgent(a.getIdAgent());
				getActionCourante().getListeCampagneActeurs().add(newActeur);
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NB_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NOM_DOC Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_DOC Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
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
	 * Retourne la valeur à  afficher par la JSP pour la zone :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Recherche des documents de la campagne

		List<EaeDocumentDto> listeDoc = getActionCourante().getListeEaeDocument();
		setListeDocuments(new ArrayList<Document>());
		for (int i = 0; i < listeDoc.size(); i++) {
			EaeDocumentDto lien = listeDoc.get(i);
			Document d = getDocumentDao().chercherDocumentById(lien.getIdDocument());
			getListeDocuments().add(d);
		}

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActeVM), doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());
				addZone(getNOM_ST_URL_DOC(indiceActeVM), (null == doc.getNodeRefAlfresco() || doc.getNodeRefAlfresco().equals(Const.CHAINE_VIDE))
						? "&nbsp;" : AlfrescoCMISService.getUrlOfDocument(doc.getNodeRefAlfresco()));

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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Récup du Diplome courant
		Document d = getDocumentCourant();

		EaeDocumentDto ldoc = eaeService.getDocumentEaeByIdDocument(getAgentConnecte(request).getIdAgent(), getDocumentCourant().getIdDocument());
		setLienEaeDocument(ldoc);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_DATE_DOC(), sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	public EaeDocumentDto getLienEaeDocument() {
		return lienEaeDocument;
	}

	public void setLienEaeDocument(EaeDocumentDto lienEaeDocument) {
		this.lienEaeDocument = lienEaeDocument;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {

			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());

				// Controle des champs
				if (!performControlerSaisieDocument(request))
					return false;

				CampagneEaeDto camp = getCampagneCourante();

				if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_CREATION)) {
					if (!creeDocument(request, camp)) {
						return false;
					}
				}
			}
		} else {
			if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_SUPPRESSION)) {
				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(getDocumentCourant());
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;

				// suppression dans table EAE_DOCUMENT
				ReturnMessageDto result = eaeService.deleteDocumentEae(getAgentConnecte(request).getIdAgent(),
						getLienEaeDocument().getIdEaeDocument());
				if (!result.getErrors().isEmpty()) {
					getTransaction().declarerErreur(result.getErrors().get(0).toString());
					return false;
				}

				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentDao().supprimerDocument(getDocumentCourant().getIdDocument());

				if (getTransaction().isErreur())
					return false;

				// tout s'est bien passé
				commitTransaction();
			}
		}

		initialiseListeDocuments(request);
		return true;
	}

	private boolean creeDocument(HttpServletRequest request, CampagneEaeDto camp) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		String codTypeDoc = CmisUtils.CODE_TYPE_ACT;
		TypeDocument td = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);

		// on crée le document en base de données
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));

		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), null, getDocumentCourant(), fichierUpload,
				codTypeDoc);

		if (declarerErreurFromReturnMessageDto(rmd))
			return false;

		// on crée le document en base de données
		Integer id = getDocumentDao().creerDocument(getDocumentCourant().getClasseDocument(), getDocumentCourant().getNomDocument(),
				getDocumentCourant().getLienDocument(), getDocumentCourant().getDateDocument(), getDocumentCourant().getCommentaire(),
				getDocumentCourant().getIdTypeDocument(), getDocumentCourant().getNomOriginal(), getDocumentCourant().getNodeRefAlfresco(),
				getDocumentCourant().getCommentaireAlfresco(), getDocumentCourant().getReference());

		setLienEaeDocument(new EaeDocumentDto());
		getLienEaeDocument().setIdDocument(id);
		getLienEaeDocument().setTypeDocument(codTypeDoc);
		getLienEaeDocument().setIdCampagneAction(getActionCourante().getIdCampagneAction());

		getActionCourante().getListeEaeDocument().add(getLienEaeDocument());

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
	 * méthode qui teste si un parametre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null
				|| (multi != null && multi.getParameter(param) != null));
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
		@SuppressWarnings("unused")
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}

	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
	}
}