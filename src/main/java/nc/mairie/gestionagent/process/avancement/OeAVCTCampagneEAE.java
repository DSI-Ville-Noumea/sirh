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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.avancement.Avancement;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.EAE.CampagneActeurDao;
import nc.mairie.spring.dao.metier.EAE.CampagneActionDao;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeDocumentDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.domain.metier.EAE.CampagneActeur;
import nc.mairie.spring.domain.metier.EAE.CampagneAction;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeDocument;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagneEAE extends nc.mairie.technique.BasicProcess {

	private ArrayList<CampagneEAE> listeCampagne;
	private CampagneEAE campagneCourante;

	public String ACTION_VISUALISATION = "Consultation d'une campagne.";
	public String ACTION_MODIFICATION = "Modification d'une campagne.";
	public String ACTION_CREATION = "Création d'une campagne.";

	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche visite médicale.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document d'une fiche visite médicale.";

	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private EaeDocument lienEaeDocument;
	private String urlFichier;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	public boolean dateDebutModifiable = false;

	private static Logger logger = Logger.getLogger(OeAVCTCampagneEAE.class.getName());

	private CampagneEAEDao campagneEAEDao;
	private CampagneActionDao campagneActionDao;
	private CampagneActeurDao campagneActeurDao;
	private EaeDocumentDao eaeDocumentDao;
	private EAEDao eaeDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeEvalueDao eaeEvalueDao;

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

		initialiseDao();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// initialisation de la liste des campagnes
		initialiseListeCampagne(request);

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}

		if (getCampagneActionDao() == null) {
			setCampagneActionDao((CampagneActionDao) context.getBean("campagneActionDao"));
		}

		if (getCampagneActeurDao() == null) {
			setCampagneActeurDao((CampagneActeurDao) context.getBean("campagneActeurDao"));
		}

		if (getEaeDocumentDao() == null) {
			setEaeDocumentDao((EaeDocumentDao) context.getBean("eaeDocumentDao"));
		}

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}

		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}

		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao((EaeEvalueDao) context.getBean("eaeEvalueDao"));
		}
	}

	private void initialiseListeCampagne(HttpServletRequest request) throws Exception {
		// Recherche des campagnes
		ArrayList<CampagneEAE> listeCampagne = getCampagneEAEDao().listerCampagneEAE();
		setListeCampagne(listeCampagne);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indiceCamp = 0;
		if (getListeCampagne() != null) {
			for (int i = 0; i < getListeCampagne().size(); i++) {
				CampagneEAE campagne = (CampagneEAE) getListeCampagne().get(i);
				// calcul du nb de docs
				ArrayList<EaeDocument> listeDocCamp = getEaeDocumentDao().listerEaeDocument(campagne.getIdCampagneEAE(), null, "CAMP");
				int nbDoc = 0;
				if (listeDocCamp != null) {
					nbDoc = listeDocCamp.size();
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
	 * Initialisation des liste déroulantes.
	 */
	private void initialiseListeDeroulante() throws Exception {

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
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTCampagneEAE() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_OUVRIR_KIOSQUE(HttpServletRequest request, int elementAOuvrir) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(elementAOuvrir);
		setCampagneCourante(campagneCourante);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		getCampagneCourante().setDateOuvertureKiosque(sdf.parse(Services.dateDuJour()));
		// RG-EAE-4
		getCampagneEAEDao().modifierOuvertureKiosqueCampagneEAE(getCampagneCourante().getIdCampagneEAE(),
				getCampagneCourante().getDateOuvertureKiosque());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_FERMER_KIOSQUE(HttpServletRequest request, int elementAFermer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(elementAFermer);
		setCampagneCourante(campagneCourante);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		getCampagneCourante().setDateFermetureKiosque(sdf.parse(Services.dateDuJour()));
		// RG-EAE-5
		getCampagneEAEDao().modifierFermetureKiosqueCampagneEAE(getCampagneCourante().getIdCampagneEAE(),
				getCampagneCourante().getDateFermetureKiosque());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CLOTURER_CAMPAGNE(HttpServletRequest request, int elementACloturer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(elementACloturer);
		setCampagneCourante(campagneCourante);

		// RG-EAE-6
		// on cherche pour chaque EAE de la campagne si il y a une ligne dans
		// Avanacement pourla meme année
		MotifAvancement motifRevalo = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "REVALORISATION");
		MotifAvancement motifAD = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "AVANCEMENT DIFFERENCIE");
		MotifAvancement motifPromo = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "PROMOTION");
		ArrayList<EAE> listeEAE = getEaeDao().listerEAEFinaliseControlePourCampagne(getCampagneCourante().getIdCampagneEAE());
		for (int i = 0; i < listeEAE.size(); i++) {
			EAE eae = listeEAE.get(i);
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEAE());
			Avancement avct = Avancement.chercherAvancementAvecAnneeEtAgent(getTransaction(), getCampagneCourante().getAnnee().toString(), evalue
					.getIdAgent().toString());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (avct.getGrade() != null) {
				Grade gradeAgent = Grade.chercherGrade(getTransaction(), avct.getGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					avct.setIdMotifAvct(null);
					avct.setAvisSHD(null);
				} else {
					String typeAvct = gradeAgent.getCodeTava().trim();
					if (!typeAvct.equals(Const.CHAINE_VIDE)) {
						// on cherche le type avancement correspondant
						MotifAvancement motif = MotifAvancement.chercherMotifAvancement(getTransaction(), typeAvct);
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							avct.setIdMotifAvct(null);
							avct.setAvisSHD(null);
						} else {
							avct.setIdMotifAvct(motif.getIdMotifAvct());
							EaeEvaluation eval = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEAE());
							if (typeAvct.equals(motifRevalo.getIdMotifAvct())) {
								avct.setAvisSHD(eval.getAvisRevalorisation() == 1 ? "Favorable" : "Défavorable");
							} else if (typeAvct.equals(motifAD.getIdMotifAvct())) {
								avct.setAvisSHD(eval.getPropositionAvancement());
							} else if (typeAvct.equals(motifPromo.getIdMotifAvct())) {
								avct.setAvisSHD(eval.getAvisChangementClasse() == 1 ? "Favorable" : "Défavorable");
							} else {
								avct.setAvisSHD(null);
							}
						}
					} else {
						avct.setIdMotifAvct(null);
						avct.setAvisSHD(null);
					}
				}
			} else {
				avct.setIdMotifAvct(null);
				avct.setAvisSHD(null);
			}
			avct.modifierAvancement(getTransaction());
		}

		if (getTransaction().isErreur())
			return false;

		// tout s'est bien passé
		commitTransaction();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		getCampagneCourante().setDateFin(sdf.parse(Services.dateDuJour()));
		// RG-EAE-6
		getCampagneEAEDao().modifierFinCampagneEAE(getCampagneCourante().getIdCampagneEAE(), getCampagneCourante().getDateFin());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean peutOuvrirCampagne(int element) throws Exception {
		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(element);
		// RG-EAE-4
		// campagne ouverte si date début campagne < date du jour ET date fin
		// kiosque vide ET date
		// fin campagne vide et date ouverture kiosque vide
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if ((Services.compareDates(sdf.format(campagneCourante.getDateDebut()).toString(), Services.dateDuJour()) < 0)
				&& campagneCourante.getDateFin() == null && campagneCourante.getDateFermetureKiosque() == null
				&& campagneCourante.getDateOuvertureKiosque() == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean peutFermerCampagne(int element) throws Exception {
		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(element);
		// RG-EAE-5
		// date début kiosque < date du jour ET date fin kiosque vide
		// ET QUE DATE FERMETURE_KIOSQUE n’est pas saisie
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if ((Services.compareDates(sdf.format(campagneCourante.getDateDebut()).toString(), Services.dateDuJour()) < 0)
				&& campagneCourante.getDateFin() == null && campagneCourante.getDateFermetureKiosque() == null
				&& campagneCourante.getDateOuvertureKiosque() != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean peutCloturerCampagne(int element) throws Exception {
		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(element);
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// RG_AG_CA_A07
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(indiceEltAModifier);
		setCampagneCourante(campagneCourante);

		if (!initialiseCampagneCourante(request))
			return false;

		initialiseListeDocuments(request);

		// RG-EAE-3 : date de debut modifibale seulement si datedebut > datejour
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
		CampagneEAE camp = getCampagneCourante();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

		CampagneEAE campagneCourante = (CampagneEAE) getListeCampagne().get(indiceEltAConsulter);
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

	public ArrayList<CampagneEAE> getListeCampagne() {
		if (listeCampagne == null)
			return new ArrayList<CampagneEAE>();
		return listeCampagne;
	}

	public void setListeCampagne(ArrayList<CampagneEAE> listeCampagne) {
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

		setCampagneCourante(null);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);

		// RG-EAE-2 : on regarde si toutes les campagnes sont cloturées
		for (CampagneEAE camp : getListeCampagne()) {
			if (camp.getDateFin() == null || camp.getDateFin().equals(Const.CHAINE_VIDE)) {
				// "ERR210",
				// "Toutes les campgnes ne sont pas cloturées. Vous ne pouvez pas en créer une nouvelle."
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

			alimenterCampagne(request);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getCampagneEAEDao().modifierCampagneEAE(getCampagneCourante().getIdCampagneEAE(), getCampagneCourante().getDateDebut(),
						getCampagneCourante().getCommentaire());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				Integer idCampagneCreer = getCampagneEAEDao().creerCampagneEAE(getCampagneCourante().getAnnee(),
						getCampagneCourante().getDateDebut(), getCampagneCourante().getCommentaire());
				// RG-EAE-11
				// on duplique les actions de la campagne precedente
				Integer anneePrecedente = getCampagneCourante().getAnnee() - 1;
				try {
					CampagneEAE campagnePrecedente = getCampagneEAEDao().chercherCampagneEAEAnnee(anneePrecedente);
					if (campagnePrecedente != null) {
						// on a trouvé une campagne précédente
						// maintenant on cherche ses actions
						ArrayList<CampagneAction> listeActionsCampagnePrecedente = getCampagneActionDao().listerCampagneActionPourCampagne(
								campagnePrecedente.getIdCampagneEAE());
						for (int i = 0; i < listeActionsCampagnePrecedente.size(); i++) {
							CampagneAction action = listeActionsCampagnePrecedente.get(i);
							// on duplique l'action
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							String transmettreLe = Services.ajouteAnnee(sdf.format(action.getDateTransmission()), 1);
							String pourLe = Services.ajouteAnnee(sdf.format(action.getDateAFaireLe()), 1);

							Integer idActionCreer = getCampagneActionDao().creerCampagneAction(action.getNomAction(), action.getMessage(),
									sdf.parse(transmettreLe),  sdf.parse(pourLe), null, null, action.getIdAgentRealisation(), idCampagneCreer);
							// on cherche les acteurs de cette action pour les
							// dupliquer egalement
							ArrayList<CampagneActeur> listeActeursAction = getCampagneActeurDao().listerCampagneActeur(action.getIdCampagneAction());
							for (int j = 0; j < listeActeursAction.size(); j++) {
								CampagneActeur acteur = listeActeursAction.get(j);
								getCampagneActeurDao().creerCampagneActeur(idActionCreer, acteur.getIdAgent());
							}
						}
					}
				} catch (Exception e) {
					// pas de campagne precedente
					// on ne fait rien
				}
			}
			if (getTransaction().isErreur())
				return false;

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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());

		String commentaire = getVAL_ST_COMMENTAIRE();
		String annee = getVAL_ST_ANNEE();

		if (getCampagneCourante() == null)
			setCampagneCourante(new CampagneEAE());

		getCampagneCourante().setDateDebut(dateDebut);
		getCampagneCourante().setCommentaire(commentaire);
		getCampagneCourante().setAnnee(Integer.valueOf(annee));
	}

	private boolean performRegleGestion(HttpServletRequest request) {
		// RG-EAE-1 : année unique
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			for (CampagneEAE camp : getListeCampagne()) {
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

	public CampagneEAE getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEAE campagneCourante) {
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

	/**
	 * Retourne le doc en cours.
	 * 
	 * @return documentCourant
	 */
	private Document getDocumentCourant() {
		return documentCourant;
	}

	/**
	 * Met à jour le doc en cours.
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
		String codTypeDoc = "CAMP";
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
		getEaeDocumentDao().creerEaeDocument(getLienEaeDocument().getIdCampagneEae(), null, getLienEaeDocument().getIdDocument(),
				getLienEaeDocument().getTypeDocument());

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
			listeDocuments = new ArrayList();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des documents
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de la campagne

		ArrayList<EaeDocument> listeDoc = getEaeDocumentDao().listerEaeDocument(getCampagneCourante().getIdCampagneEAE(), null, "CAMP");
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

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}

	public CampagneActionDao getCampagneActionDao() {
		return campagneActionDao;
	}

	public void setCampagneActionDao(CampagneActionDao campagneActionDao) {
		this.campagneActionDao = campagneActionDao;
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

	public EaeDocument getLienEaeDocument() {
		return lienEaeDocument;
	}

	public void setLienEaeDocument(EaeDocument lienEaeDocument) {
		this.lienEaeDocument = lienEaeDocument;
	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
	}
}