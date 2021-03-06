package nc.mairie.gestionagent.process.avancement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.mairie.alfresco.cmis.CmisUtils;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagneEAE extends BasicProcess {

	/**
	 * 
	 */
	private static final long		serialVersionUID			= 1L;
	private List<CampagneEaeDto>	listeCampagne;
	private CampagneEaeDto			campagneCourante;

	public String					ACTION_VISUALISATION		= "Consultation d'une campagne.";
	public String					ACTION_MODIFICATION			= "Modification d'une campagne.";
	public String					ACTION_CREATION				= "Création d'une campagne.";

	public String					ACTION_DOCUMENT_SUPPRESSION	= "Suppression d'un document d'une fiche visite médicale.";
	public String					ACTION_DOCUMENT_CREATION	= "Création d'un document d'une fiche visite médicale.";

	private ArrayList<Document>		listeDocuments;
	private Document				documentCourant;
	private EaeDocumentDto			lienEaeDocument;
	public boolean					isImporting					= false;
	public MultipartRequest			multi						= null;
	public File						fichierUpload				= null;

	public boolean					dateDebutModifiable			= false;

	private TypeDocumentDao			typeDocumentDao;
	private DocumentDao				documentDao;
	private AgentDao				agentDao;

	private IAlfrescoCMISService	alfrescoCMISService;
	private IRadiService			radiService;
	private IEaeService				eaeService;

	private SimpleDateFormat		sdf							= new SimpleDateFormat("dd/MM/yyyy");

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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		// initialisation de la liste des campagnes
		initialiseListeCampagne(request);

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
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
		if (radiService == null) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (alfrescoCMISService == null) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
		}
	}

	private void initialiseListeCampagne(HttpServletRequest request) throws Exception {
		// Recherche des campagnes
		List<CampagneEaeDto> listeCampagne = eaeService.getListeCampagnesEae(getAgentConnecte(request).getIdAgent());
		setListeCampagne(listeCampagne);

		int indiceCamp = 0;
		if (getListeCampagne() != null) {
			for (int i = 0; i < getListeCampagne().size(); i++) {
				CampagneEaeDto campagne = (CampagneEaeDto) getListeCampagne().get(i);
				// calcul du nb de docs
				int nbDoc = 0;
				if (null != campagne.getListeEaeDocument()) {
					nbDoc = campagne.getListeEaeDocument().size();
				}

				addZone(getNOM_ST_ANNEE(indiceCamp), campagne.getAnnee().toString());
				addZone(getNOM_ST_DATE_DEBUT(indiceCamp), sdf.format(campagne.getDateDebut()));
				addZone(getNOM_ST_DATE_FIN(indiceCamp), campagne.getDateFin() == null ? "&nbsp;" : sdf.format(campagne.getDateFin()));
				addZone(getNOM_ST_DATE_DEBUT_KIOSQUE(indiceCamp),
						campagne.getDateOuvertureKiosque() == null ? "&nbsp;" : sdf.format(campagne.getDateOuvertureKiosque()));
				addZone(getNOM_ST_DATE_FIN_KIOSQUE(indiceCamp),
						campagne.getDateFermetureKiosque() == null ? "&nbsp;" : sdf.format(campagne.getDateFermetureKiosque()));
				addZone(getNOM_ST_NB_DOC(indiceCamp), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceCamp++;
			}
		}
	}

	/**
	 * Initialisation des liste deroulantes.
	 */
	private void initialiseListeDeroulante() throws Exception {

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

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeCampagne().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISER
			for (int i = 0; i < getListeCampagne().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}

			// Si clic sur le bouton PB_OUVRIR_KIOSQUE
			for (int i = 0; i < getListeCampagne().size(); i++) {
				if (testerParametre(request, getNOM_PB_OUVRIR_KIOSQUE(i))) {
					return performPB_OUVRIR_KIOSQUE(request, i);
				}
			}

			// Si clic sur le bouton PB_FERMER_KIOSQUE
			for (int i = 0; i < getListeCampagne().size(); i++) {
				if (testerParametre(request, getNOM_PB_FERMER_KIOSQUE(i))) {
					return performPB_FERMER_KIOSQUE(request, i);
				}
			}

			// Si clic sur le bouton PB_CLOTURER_CAMPAGNE
			for (int i = 0; i < getListeCampagne().size(); i++) {
				if (testerParametre(request, getNOM_PB_CLOTURER_CAMPAGNE(i))) {
					return performPB_CLOTURER_CAMPAGNE(request, i);
				}
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
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTCampagneEAE() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTCampagneEAE.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OUVRIR_KIOSQUE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_OUVRIR_KIOSQUE(int i) {
		return "NOM_PB_OUVRIR_KIOSQUE_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_OUVRIR_KIOSQUE(HttpServletRequest request, int elementAOuvrir) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(elementAOuvrir);
		setCampagneCourante(campagneCourante);
		getCampagneCourante().setDateOuvertureKiosque(sdf.parse(Services.dateDuJour()));
		getCampagneCourante().setDateFermetureKiosque(null);
		// RG-EAE-4
		ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_FERMER_KIOSQUE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_FERMER_KIOSQUE(int i) {
		return "NOM_PB_FERMER_KIOSQUE_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_FERMER_KIOSQUE(HttpServletRequest request, int elementAFermer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(elementAFermer);
		setCampagneCourante(campagneCourante);
		getCampagneCourante().setDateFermetureKiosque(sdf.parse(Services.dateDuJour()));
		// RG-EAE-5
		ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());
		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CLOTURER_CAMPAGNE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_CLOTURER_CAMPAGNE(int i) {
		return "NOM_PB_CLOTURER_CAMPAGNE_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CLOTURER_CAMPAGNE(HttpServletRequest request, int elementACloturer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(elementACloturer);
		setCampagneCourante(campagneCourante);

		getCampagneCourante().setDateFin(sdf.parse(Services.dateDuJour()));
		// RG-EAE-6
		ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean peutOuvrirKiosque(int element) throws Exception {
		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(element);
		// RG-EAE-4
		// campagne ouverte si date début campagne < date du jour ET date fin
		// kiosque vide ET date
		// fin campagne vide et date ouverture kiosque vide
		if ((Services.compareDates(sdf.format(campagneCourante.getDateDebut()).toString(), Services.dateDuJour()) < 0)
				&& campagneCourante.getDateFin() == null && campagneCourante.getDateFermetureKiosque() != null) {
			return true;
		} else {
			if ((Services.compareDates(sdf.format(campagneCourante.getDateDebut()).toString(), Services.dateDuJour()) < 0)
					&& campagneCourante.getDateFermetureKiosque() == null && campagneCourante.getDateOuvertureKiosque() == null) {
				return true;
			}
			return false;
		}
	}

	public boolean peutFermerKiosque(int element) throws Exception {
		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(element);
		// RG-EAE-5
		// date début kiosque < date du jour ET date fin kiosque vide
		// ET QUE DATE FERMETURE_KIOSQUE n est pas saisie
		if ((Services.compareDates(sdf.format(campagneCourante.getDateDebut()).toString(), Services.dateDuJour()) < 0)
				&& campagneCourante.getDateFin() == null && campagneCourante.getDateFermetureKiosque() == null
				&& campagneCourante.getDateOuvertureKiosque() != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean peutCloturerCampagne(int element) throws Exception {
		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(element);
		// RG-EAE-6
		// si DATE FERMETURE_KIOSQUE est saisie
		if (campagneCourante.getDateFin() == null && campagneCourante.getDateFermetureKiosque() != null) {
			return true;
		} else {
			return false;
		}
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
		isImporting = false;
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
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
		// RG_AG_CA_A07
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(indiceEltAModifier);
		setCampagneCourante(campagneCourante);

		if (!initialiseCampagneCourante(request))
			return false;

		initialiseListeDocuments(request);

		// RG-EAE-3 : date de debut modifibale seulement si datedebut > datejour
		if (Services.compareDates(Services.dateDuJour(), sdf.format(getCampagneCourante().getDateDebut()).toString()) < 0) {
			dateDebutModifiable = true;
		} else {
			dateDebutModifiable = false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseCampagneCourante(HttpServletRequest request) throws Exception {
		videZonesDeSaisie(request);

		// Alim zones
		CampagneEaeDto camp = getCampagneCourante();
		addZone(getNOM_ST_ANNEE(), camp.getAnnee().toString());
		addZone(getNOM_ST_DATE_DEBUT(), sdf.format(camp.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), camp.getDateFin() == null ? Const.CHAINE_VIDE : sdf.format(camp.getDateFin()));
		addZone(getNOM_ST_COMMENTAIRE(), camp.getCommentaire());

		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une
	 * carriere
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ANNEE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE(), Const.CHAINE_VIDE);
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

		CampagneEaeDto campagneCourante = (CampagneEaeDto) getListeCampagne().get(indiceEltAConsulter);
		setCampagneCourante(campagneCourante);

		// init de la carriere courante
		if (!initialiseCampagneCourante(request))
			return false;

		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

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

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-CAMPAGNE-EAE";
	}

	public List<CampagneEaeDto> getListeCampagne() {
		if (listeCampagne == null)
			return new ArrayList<CampagneEaeDto>();
		return listeCampagne;
	}

	public void setListeCampagne(List<CampagneEaeDto> listeCampagne) {
		this.listeCampagne = listeCampagne;
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

		setCampagneCourante(null);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);

		// RG-EAE-2 : on regarde si toutes les campagnes sont cloturees
		for (CampagneEaeDto camp : getListeCampagne()) {
			if (camp.getDateFin() == null) {
				// "ERR210",
				// "Toutes les campgnes ne sont pas cloturees. Vous ne pouvez
				// pas en créer une nouvelle."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR210"));
				return false;
			}
		}

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

			alimenterCampagne(request);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());

				if (!result.getErrors().isEmpty()) {
					getTransaction().declarerErreur(result.getErrors().get(0).toString());
					return false;
				}
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {

				// RG-EAE-11
				// on duplique les actions de la campagne precedente
				Integer anneePrecedente = getCampagneCourante().getAnnee() - 1;

				CampagneEaeDto campagnePrecedente = eaeService.getCampagneAnneePrecedente(getAgentConnecte(request).getIdAgent(), anneePrecedente);
				if (campagnePrecedente != null) {
					// on a trouvé une campagne précédente
					// maintenant on cherche ses actions
					if (null != campagnePrecedente.getListeCampagneAction()) {
						for (int i = 0; i < campagnePrecedente.getListeCampagneAction().size(); i++) {
							EaeCampagneActionDto action = campagnePrecedente.getListeCampagneAction().get(i);
							// on duplique l'action
							String transmettreLe = Services.ajouteAnnee(sdf.format(action.getDateTransmission()), 1);
							String pourLe = Services.ajouteAnnee(sdf.format(action.getDateAFaireLe()), 1);

							EaeCampagneActionDto newAction = new EaeCampagneActionDto();
							newAction.setDateTransmission(sdf.parse(transmettreLe));
							newAction.setDateAFaireLe(sdf.parse(pourLe));
							newAction.setIdAgentRealisation(action.getIdAgentRealisation());
							newAction.setNomAction(action.getNomAction());
							newAction.setMessage(action.getMessage());

							if (null != action.getListeCampagneActeurs()) {
								for (int j = 0; j < action.getListeCampagneActeurs().size(); j++) {
									EaeCampagneActeursDto acteur = action.getListeCampagneActeurs().get(j);

									EaeCampagneActeursDto newActeur = new EaeCampagneActeursDto();
									newActeur.setIdAgent(acteur.getIdAgent());

									newAction.getListeCampagneActeurs().add(newActeur);
								}
							}
							getCampagneCourante().getListeCampagneAction().add(newAction);
						}
					}
				}

				// Création
				ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), getCampagneCourante());

				if (!result.getErrors().isEmpty()) {
					getTransaction().declarerErreur(result.getErrors().get(0).toString());
					return false;
				}
			}
			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_CREATION(request);
		}

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();

		isImporting = false;
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		return true;
	}

	private void alimenterCampagne(HttpServletRequest request) throws ParseException {

		// récupération des informations remplies dans les zones de saisie
		Date dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());

		String commentaire = getVAL_ST_COMMENTAIRE();
		String annee = getVAL_ST_ANNEE();

		if (getCampagneCourante() == null)
			setCampagneCourante(new CampagneEaeDto());

		getCampagneCourante().setDateDebut(dateDebut);
		getCampagneCourante().setCommentaire(commentaire);
		getCampagneCourante().setAnnee(Integer.valueOf(annee));
	}

	private boolean performRegleGestion(HttpServletRequest request) {
		// RG-EAE-1 : année unique
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			for (CampagneEaeDto camp : getListeCampagne()) {
				if (camp.getAnnee().toString().equals(getVAL_ST_ANNEE())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une campagne", "cette année"));
					return false;
				}
			}
		}

		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// RG-EAE-2 :année et date debut obligatoire
		// année obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_ANNEE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			return false;
		}

		// format année
		if (!Services.estNumerique(getVAL_ST_ANNEE())) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
			return false;
		}
		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_DATE_DEBUT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_ST_DATE_DEBUT())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
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

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT_KIOSQUE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT_KIOSQUE(int i) {
		return "NOM_ST_DATE_DEBUT_KIOSQUE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_DEBUT_KIOSQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT_KIOSQUE(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_KIOSQUE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN_KIOSQUE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN_KIOSQUE(int i) {
		return "NOM_ST_DATE_FIN_KIOSQUE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_FIN_KIOSQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN_KIOSQUE(int i) {
		return getZone(getNOM_ST_DATE_FIN_KIOSQUE(i));
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

	public CampagneEaeDto getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEaeDto campagneCourante) {
		this.campagneCourante = campagneCourante;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DATE_DEBUT Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT() {
		return "NOM_ST_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DATE_DEBUT Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT() {
		return getZone(getNOM_ST_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DATE_FIN Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_DATE_FIN() {
		return "NOM_ST_DATE_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DATE_FIN Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_DATE_FIN() {
		return getZone(getNOM_ST_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_ANNEE Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_ANNEE Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
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

		// Récup du Diplome courant
		Document d = getDocumentCourant();

		EaeDocumentDto ldoc = eaeService.getDocumentEaeByIdDocument(getAgentConnecte(request).getIdAgent(), d.getIdDocument());
		setLienEaeDocument(ldoc);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_DATE_DOC(), sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	/**
	 * Retourne le doc en cours.
	 * 
	 * @return documentCourant
	 */
	private Document getDocumentCourant() {
		return documentCourant;
	}

	/**
	 * Met a jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
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

				getCampagneCourante().getListeEaeDocument().remove(getLienEaeDocument());
			}

		}

		initialiseListeDocuments(request);
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

	private boolean creeDocument(HttpServletRequest request, CampagneEaeDto camp) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		String codTypeDoc = CmisUtils.CODE_TYPE_CAMP;
		TypeDocument td = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);

		// on crée le document en base de données
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().setReference(camp.getIdCampagneEae());

		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), null, getDocumentCourant(), fichierUpload,
				camp.getAnnee(), codTypeDoc);

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

		camp.getListeEaeDocument().add(getLienEaeDocument());

		ReturnMessageDto result = eaeService.createOrModifyCampagneEae(getAgentConnecte(request).getIdAgent(), camp);

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

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

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeDocuments
	 *            ArrayList
	 */
	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des documents
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de la campagne
		List<EaeDocumentDto> listeDoc = getCampagneCourante().getListeEaeDocument();
		setListeDocuments(new ArrayList<Document>());
		if (listeDoc != null) {
			for (int i = 0; i < listeDoc.size(); i++) {
				EaeDocumentDto lien = listeDoc.get(i);
				Document d = getDocumentDao().chercherDocumentById(lien.getIdDocument());
				getListeDocuments().add(d);
			}
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

	public EaeDocumentDto getLienEaeDocument() {
		return lienEaeDocument;
	}

	public void setLienEaeDocument(EaeDocumentDto lienEaeDocument) {
		this.lienEaeDocument = lienEaeDocument;
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