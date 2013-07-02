package nc.mairie.gestionagent.process.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.hsct.Handicap;
import nc.mairie.metier.hsct.MaladiePro;
import nc.mairie.metier.hsct.NomHandicap;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process OeAGENTHandicap Date de cr�ation : (01/07/11 09:42:08)
 * 
 */
public class OeAGENTHandicap extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private Logger logger = LoggerFactory.getLogger(OeAGENTHandicap.class);

	private String[] LB_NOM;
	private String[] LB_NOM_MP;

	private AgentNW agentCourant;
	private Handicap handicapCourant;

	private ArrayList<Handicap> listeHandicap;
	private ArrayList<NomHandicap> listeNomHandicap;
	private ArrayList<MaladiePro> listeMaladiePro;

	private Hashtable<String, NomHandicap> hashNomHandicap;
	private Hashtable<String, MaladiePro> hashMaladiePro;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche handicap.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche handicap.";
	private String ACTION_MODIFICATION = "Modification d'une fiche handicap.";
	private String ACTION_CREATION = "Cr�ation d'une fiche handicap.";

	public boolean showMaladiePro = false;
	public boolean showNumCarte = false;

	public String ACTION_DOCUMENT = "Documents d'une fiche handicap.";
	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche handicap.";
	public String ACTION_DOCUMENT_CREATION = "Cr�ation d'un document d'une fiche handicap.";
	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private LienDocumentAgent lienDocumentAgentCourant;
	private String urlFichier;
	public boolean isImporting = false;
	public com.oreilly.servlet.MultipartRequest multi = null;
	public File fichierUpload = null;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 * RG_AG_HC_C04 RG_AG_HC_C05
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setHandicapCourant(null);
			multi = null;
			isImporting = false;
		}

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Si hashtable des noms de handicap vide
		// RG_AG_HC_C04
		if (getHashNomHandicap().size() == 0) {
			ArrayList<NomHandicap> listeNomHandicap = NomHandicap.listerNomHandicap(getTransaction());
			setListeNomHandicap(listeNomHandicap);

			int[] tailles = { 40 };
			String[] champs = { "nomTypeHandicap" };
			setLB_NOM(new FormateListe(tailles, listeNomHandicap, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (int i = 0; i < listeNomHandicap.size(); i++) {
				NomHandicap n = (NomHandicap) listeNomHandicap.get(i);
				getHashNomHandicap().put(n.getIdTypeHandicap(), n);
			}
		}

		// Si hashtable des maladies pro vide
		// RG_AG_HC_C05
		if (getHashMaladiePro().size() == 0) {
			ArrayList<MaladiePro> listeMaladiePro = MaladiePro.listerMaladiePro(getTransaction());
			setListeMaladiePro(listeMaladiePro);

			int[] tailles = { 255 };
			String[] champs = { "libMaladiePro" };
			setLB_NOM_MP(new FormateListe(tailles, listeMaladiePro, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (int i = 0; i < listeMaladiePro.size(); i++) {
				MaladiePro m = (MaladiePro) listeMaladiePro.get(i);
				getHashMaladiePro().put(m.getIdMaladiePro(), m);
			}
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeHandicap(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * Initialisation de la liste des handicaps de l'agent courant Date de
	 * cr�ation : (01/07/11)
	 * 
	 */
	private void initialiseListeHandicap(HttpServletRequest request) throws Exception {
		// Recherche des handicaps de l'agent
		ArrayList<Handicap> listeHandicap = Handicap.listerHandicapAgent(getTransaction(), getAgentCourant());
		setListeHandicap(listeHandicap);

		int indiceHandi = 0;
		if (getListeHandicap() != null) {
			for (int i = 0; i < getListeHandicap().size(); i++) {
				Handicap h = (Handicap) getListeHandicap().get(i);
				NomHandicap n = (NomHandicap) getHashNomHandicap().get(h.getIdTypeHandicap());
				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(), getAgentCourant(), "HSCT", "HANDI",
						h.getIdHandicap());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}

				addZone(getNOM_ST_TYPE(indiceHandi), n.getNomTypeHandicap().equals(Const.CHAINE_VIDE) ? "&nbsp;" : n.getNomTypeHandicap());
				addZone(getNOM_ST_DEBUT(indiceHandi), h.getDateDebutHandicap());
				addZone(getNOM_ST_FIN(indiceHandi), h.getDateFinHandicap().equals(Const.DATE_NULL)
						|| h.getDateFinHandicap().equals(Const.CHAINE_VIDE) ? "&nbsp;" : h.getDateFinHandicap());
				addZone(getNOM_ST_INCAPACITE(indiceHandi), h.getPourcentIncapacite().equals(Const.ZERO) ? "&nbsp;" : h.getPourcentIncapacite() + " %");
				addZone(getNOM_ST_MALADIE_PROF(indiceHandi), h.isReconnaissanceMP() ? "OUI" : "NON");
				addZone(getNOM_ST_CRDHNC(indiceHandi), h.isHandicapCRDHNC() ? "OUI" : "NON");
				addZone(getNOM_ST_NUM_CARTE(indiceHandi), h.getNumCarteCRDHNC().equals(Const.CHAINE_VIDE) ? "&nbsp;" : h.getNumCarteCRDHNC());
				addZone(getNOM_ST_RENOUVELLEMENT(indiceHandi), h.isRenouvellement() ? "OUI" : "NON");
				addZone(getNOM_ST_AMENAGEMENT(indiceHandi), h.isAmenagementPoste() ? "OUI" : "NON");
				addZone(getNOM_ST_NB_DOC(indiceHandi), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceHandi++;
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTHandicap. Date de cr�ation : (01/07/11
	 * 09:42:08)
	 * 
	 */
	public OeAGENTHandicap() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setHandicapCourant(new Handicap());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * R�initilise les champs du formulaire de cr�ation/modification d'un
	 * accident de travail Date de cr�ation : 01/07/11
	 * 
	 * RG_AG_HC_C03 RG_AG_HC_C08
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		showMaladiePro = false;
		showNumCarte = false;
		// On vide les zone de saisie
		addZone(getNOM_LB_NOM_SELECT(), Const.ZERO);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_INCAPACITE(), Const.CHAINE_VIDE);
		// RG_AG_HC_C03
		addZone(getNOM_RG_RECO_MP(), getNOM_RB_RECO_MP_NON());
		addZone(getNOM_LB_NOM_MP_SELECT(), Const.ZERO);
		addZone(getNOM_RG_RECO_CRDHNC(), getNOM_RB_RECO_CRDHNC_NON());
		addZone(getNOM_EF_NUM_CRDHNC(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_RENOUV_CRDHNC(), getNOM_RB_RENOUV_CRDHNC_NON());
		// RG_AG_HC_C08
		addZone(getNOM_RG_AMENAGEMENT(), getNOM_RB_AMENAGEMENT_NON());
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'un handicap
	 * Date de cr�ation : 11/07/01
	 * 
	 */
	private boolean initialiseHandicapCourant(HttpServletRequest request) throws Exception {
		NomHandicap nom = (NomHandicap) getHashNomHandicap().get(getHandicapCourant().getIdTypeHandicap());
		MaladiePro maladiePro = null;
		if (getHandicapCourant().isReconnaissanceMP())
			maladiePro = (MaladiePro) getHashMaladiePro().get(getHandicapCourant().getIdMaladiePro());

		// Alim zones
		int ligneNom = getListeNomHandicap().indexOf(nom);
		addZone(getNOM_LB_NOM_SELECT(), String.valueOf(ligneNom + 1));

		addZone(getNOM_EF_DATE_DEBUT(), getHandicapCourant().getDateDebutHandicap());
		addZone(getNOM_EF_DATE_FIN(), getHandicapCourant().getDateFinHandicap().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : getHandicapCourant()
				.getDateFinHandicap());
		addZone(getNOM_EF_INCAPACITE(), getHandicapCourant().getPourcentIncapacite().equals(Const.ZERO) ? Const.CHAINE_VIDE : getHandicapCourant()
				.getPourcentIncapacite());

		showMaladiePro = getHandicapCourant().isReconnaissanceMP();
		showNumCarte = getHandicapCourant().isHandicapCRDHNC();

		if (getHandicapCourant().isReconnaissanceMP()) {
			int ligneNomMP = getListeMaladiePro().indexOf(maladiePro);
			addZone(getNOM_LB_NOM_MP_SELECT(), String.valueOf(ligneNomMP + 1));
			addZone(getNOM_RG_RECO_MP(), getNOM_RB_RECO_MP_OUI());
		} else {
			addZone(getNOM_LB_NOM_MP_SELECT(), Const.ZERO);
			addZone(getNOM_RG_RECO_MP(), getNOM_RB_RECO_MP_NON());
		}

		if (getHandicapCourant().isHandicapCRDHNC()) {
			addZone(getNOM_EF_NUM_CRDHNC(), getHandicapCourant().getNumCarteCRDHNC());
			addZone(getNOM_RG_RECO_CRDHNC(), getNOM_RB_RECO_CRDHNC_OUI());
		} else {
			addZone(getNOM_EF_NUM_CRDHNC(), Const.CHAINE_VIDE);
			addZone(getNOM_RG_RECO_CRDHNC(), getNOM_RB_RECO_CRDHNC_NON());
		}

		if (getHandicapCourant().isRenouvellement())
			addZone(getNOM_RG_RENOUV_CRDHNC(), getNOM_RB_RENOUV_CRDHNC_OUI());
		else
			addZone(getNOM_RG_RENOUV_CRDHNC(), getNOM_RB_RENOUV_CRDHNC_NON());

		if (getHandicapCourant().isAmenagementPoste())
			addZone(getNOM_RG_AMENAGEMENT(), getNOM_RB_AMENAGEMENT_OUI());
		else
			addZone(getNOM_RG_AMENAGEMENT(), getNOM_RB_AMENAGEMENT_NON());

		addZone(getNOM_EF_COMMENTAIRE(), getHandicapCourant().getCommentaireHandicap());

		return true;
	}

	/**
	 * Initialisation de la suppression d'un handicap
	 * 
	 * @param request
	 * @return true si la suppression peut �tre effectu�e
	 * @throws Exception
	 */
	private boolean initialiseHandicapSuppression(HttpServletRequest request) throws Exception {
		NomHandicap nom = (NomHandicap) getHashNomHandicap().get(getHandicapCourant().getIdTypeHandicap());
		MaladiePro maladiePro = null;

		if (getHandicapCourant().isReconnaissanceMP())
			maladiePro = (MaladiePro) getHashMaladiePro().get(getHandicapCourant().getIdMaladiePro());

		// Alim zones
		addZone(getNOM_ST_NOM(), nom.getNomTypeHandicap());
		addZone(getNOM_ST_DATE_DEBUT(), getHandicapCourant().getDateDebutHandicap());
		addZone(getNOM_ST_DATE_FIN(), getHandicapCourant().getDateFinHandicap().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : getHandicapCourant()
				.getDateFinHandicap());
		addZone(getNOM_ST_INCAPACITE(), getHandicapCourant().getPourcentIncapacite().equals(Const.ZERO) ? Const.CHAINE_VIDE : getHandicapCourant()
				.getPourcentIncapacite());

		addZone(getNOM_ST_RECO_MP(), getHandicapCourant().isReconnaissanceMP() ? "Oui" : "Non");
		if (getHandicapCourant().isReconnaissanceMP())
			addZone(getNOM_ST_NOM_MP(), maladiePro.getLibMaladiePro());
		addZone(getNOM_ST_RECO_CRDHNC(), getHandicapCourant().isHandicapCRDHNC() ? "Oui" : "Non");
		addZone(getNOM_ST_NUM_CRDHNC(), getHandicapCourant().getNumCarteCRDHNC());
		addZone(getNOM_ST_RENOUV_CRDHNC(), getHandicapCourant().isRenouvellement() ? "Oui" : "Non");
		addZone(getNOM_ST_AMENAGEMENT(), getHandicapCourant().isAmenagementPoste() ? "Oui" : "Non");
		addZone(getNOM_ST_COMMENTAIRE(), getHandicapCourant().getCommentaireHandicap());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {

			// Suppression
			getHandicapCourant().supprimerHandicap(getTransaction());
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// v�rification de la validit� du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// r�cup�ration des informations remplies dans les zones de saisie
			int numLigneNom = (Services.estNumerique(getZone(getNOM_LB_NOM_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_NOM_SELECT())) : -1);

			if (numLigneNom == -1 || getListeNomHandicap().size() == 0 || numLigneNom > getListeNomHandicap().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "noms de handicap"));
				return false;
			}

			NomHandicap nom = (NomHandicap) getListeNomHandicap().get(numLigneNom - 1);

			String dateDebut = getVAL_EF_DATE_DEBUT();
			String dateFin = getVAL_EF_DATE_FIN();

			String incapacite = getVAL_EF_INCAPACITE();

			Boolean recoMP = getVAL_RG_RECO_MP().equals(getNOM_RB_RECO_MP_OUI());

			MaladiePro maladiePro = null;

			if (recoMP) {
				int numLigneMP = (Services.estNumerique(getZone(getNOM_LB_NOM_MP_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_NOM_MP_SELECT()))
						: -1);

				if (numLigneMP == -1 || getListeMaladiePro().size() == 0 || numLigneMP > getListeMaladiePro().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "maladies professionnelles"));
					return false;
				}

				maladiePro = (MaladiePro) getListeMaladiePro().get(numLigneMP - 1);
			}

			Boolean recoCRDHNC = getZone(getNOM_RG_RECO_CRDHNC()).equals(getNOM_RB_RECO_CRDHNC_OUI());

			String numCRDHNC = getZone(getNOM_EF_NUM_CRDHNC());

			Boolean renouvCRDHNC = getZone(getNOM_RG_RENOUV_CRDHNC()).equals(getNOM_RB_RENOUV_CRDHNC_OUI());

			Boolean amenagement = getZone(getNOM_RG_AMENAGEMENT()).equals(getNOM_RB_AMENAGEMENT_OUI());

			String commentaire = getZone(getNOM_EF_COMMENTAIRE());

			// Cr�ation de l'objet VisiteMedicale � cr�er/modifier
			AgentNW agentCourant = getAgentCourant();
			getHandicapCourant().setIdAgent(agentCourant.getIdAgent());
			getHandicapCourant().setIdTypeHandicap(nom.getIdTypeHandicap());
			getHandicapCourant().setDateDebutHandicap(dateDebut);
			getHandicapCourant().setDateFinHandicap(dateFin);
			getHandicapCourant().setPourcentIncapacite(incapacite);
			getHandicapCourant().setReconnaissanceMP(recoMP);
			if (recoMP)
				getHandicapCourant().setIdMaladiePro(maladiePro.getIdMaladiePro());
			getHandicapCourant().setHandicapCRDHNC(recoCRDHNC);
			if (recoCRDHNC) {
				getHandicapCourant().setNumCarteCRDHNC(numCRDHNC);
				getHandicapCourant().setRenouvellement(renouvCRDHNC);
			}
			getHandicapCourant().setAmenagementPoste(amenagement);
			getHandicapCourant().setCommentaireHandicap(commentaire);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getHandicapCourant().modifierHandicap(getTransaction());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Cr�ation
				getHandicapCourant().creerHandicap(getTransaction());
			}
			if (getTransaction().isErreur())
				return false;
		}

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien pass�
		commitTransaction();
		initialiseListeHandicap(request);

		return true;
	}

	/**
	 * V�rifie les r�gles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire de handicap
	 * 
	 * @param request
	 * @return true si les r�gles de gestion sont respect�es. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_HC_C01 RG_AG_HC_C02 RG_AG_HC_C06 RG_AG_HC_C07
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// nom obligatoire
		int indiceNom = (Services.estNumerique(getVAL_LB_NOM_SELECT()) ? Integer.parseInt(getVAL_LB_NOM_SELECT()) : -1);
		if (indiceNom < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom"));
			return false;
		}

		// date de d�but de handicap
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de d�but"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de d�but"));
			return false;
		}

		// date de fin > date de d�but
		// RG_AG_HC_C01
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))) {
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
				return false;
			} else if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) != -1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de d�but"));
				return false;
			}
		}

		// pourcentage incapacite doit etre un nombre entre 0 (non compris) et
		// 100
		// RG_AG_HC_C02
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_INCAPACITE()))) {
			if (!Services.estNumerique(getZone(getNOM_EF_INCAPACITE()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "% incapacit�"));
				return false;
			}
			if (Float.parseFloat(getZone(getNOM_EF_INCAPACITE())) <= 0.0 || Float.parseFloat(getZone(getNOM_EF_INCAPACITE())) > 100.0) {
				// erreur pourcentage entre 0 et 100
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR981", "% incapacit�"));
				return false;
			}
		}

		// commentaire longeur <= 100
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_COMMENTAIRE())) && getZone(getNOM_EF_COMMENTAIRE()).length() > 100) {
			// ERR980 : La zone commentaire ne peut exc�der 100 caract�res.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR980", "commentaire", "100"));
			return false;
		}

		// si reconnu maladie pro, la maladie doit etre pr�ciser
		// RG_AG_HC_C06
		if (getZone(getNOM_RG_RECO_MP()).equals(getNOM_RB_RECO_MP_OUI())) {
			int indiceMP = (Services.estNumerique(getVAL_LB_NOM_MP_SELECT()) ? Integer.parseInt(getVAL_LB_NOM_MP_SELECT()) : -1);
			if (indiceMP < 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "maladie professionnelle"));
				return false;
			}
		}

		// si maladie CRDHNC le num�ro de carte doit etre renseign�
		// RG_AG_HC_C07
		if (getZone(getNOM_RG_RECO_CRDHNC()).equals(getNOM_RB_RECO_CRDHNC_OUI()) && Const.CHAINE_VIDE.equals(getZone(getNOM_EF_NUM_CRDHNC()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "n�carte CRDHNC"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE() {
		return "NOM_ST_COMMENTAIRE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE() {
		return getZone(getNOM_ST_COMMENTAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT() {
		return "NOM_ST_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT() {
		return getZone(getNOM_ST_DATE_DEBUT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_DATE_FIN() {
		return "NOM_ST_DATE_FIN";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_DATE_FIN() {
		return getZone(getNOM_ST_DATE_FIN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INCAPACITE Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_INCAPACITE() {
		return "NOM_ST_INCAPACITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_INCAPACITE
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_INCAPACITE() {
		return getZone(getNOM_ST_INCAPACITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM Date de cr�ation
	 * : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_NOM() {
		return "NOM_ST_NOM";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_NOM() {
		return getZone(getNOM_ST_NOM());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_MP Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_NOM_MP() {
		return "NOM_ST_NOM_MP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_MP Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_NOM_MP() {
		return getZone(getNOM_ST_NOM_MP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CRDHNC Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_ST_NUM_CRDHNC() {
		return "NOM_ST_NUM_CRDHNC";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM_CRDHNC
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_ST_NUM_CRDHNC() {
		return getZone(getNOM_ST_NUM_CRDHNC());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENTAIRE Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_INCAPACITE Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_EF_INCAPACITE() {
		return "NOM_EF_INCAPACITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_INCAPACITE Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_EF_INCAPACITE() {
		return getZone(getNOM_EF_INCAPACITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_CRDHNC Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_EF_NUM_CRDHNC() {
		return "NOM_EF_NUM_CRDHNC";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NUM_CRDHNC Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_EF_NUM_CRDHNC() {
		return getZone(getNOM_EF_NUM_CRDHNC());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NOM Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	private String[] getLB_NOM() {
		if (LB_NOM == null)
			LB_NOM = initialiseLazyLB();
		return LB_NOM;
	}

	/**
	 * Setter de la liste: LB_NOM Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	private void setLB_NOM(String[] newLB_NOM) {
		LB_NOM = newLB_NOM;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NOM Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_LB_NOM() {
		return "NOM_LB_NOM";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NOM_SELECT Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_LB_NOM_SELECT() {
		return "NOM_LB_NOM_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NOM Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String[] getVAL_LB_NOM() {
		return getLB_NOM();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NOM Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_LB_NOM_SELECT() {
		return getZone(getNOM_LB_NOM_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NOM_MP Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	private String[] getLB_NOM_MP() {
		if (LB_NOM_MP == null)
			LB_NOM_MP = initialiseLazyLB();
		return LB_NOM_MP;
	}

	/**
	 * Setter de la liste: LB_NOM_MP Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	private void setLB_NOM_MP(String[] newLB_NOM_MP) {
		LB_NOM_MP = newLB_NOM_MP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NOM_MP Date de cr�ation :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_LB_NOM_MP() {
		return "NOM_LB_NOM_MP";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NOM_MP_SELECT Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_LB_NOM_MP_SELECT() {
		return "NOM_LB_NOM_MP_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NOM_MP Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String[] getVAL_LB_NOM_MP() {
		return getLB_NOM_MP();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NOM_MP Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_LB_NOM_MP_SELECT() {
		return getZone(getNOM_LB_NOM_MP_SELECT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_AMENAGEMENT Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RG_AMENAGEMENT() {
		return "NOM_RG_AMENAGEMENT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_AMENAGEMENT Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_RG_AMENAGEMENT() {
		return getZone(getNOM_RG_AMENAGEMENT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_RECO_CRDHNC Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RG_RECO_CRDHNC() {
		return "NOM_RG_RECO_CRDHNC";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_RECO_CRDHNC Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_RG_RECO_CRDHNC() {
		return getZone(getNOM_RG_RECO_CRDHNC());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_RECO_MP
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RG_RECO_MP() {
		return "NOM_RG_RECO_MP";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_RECO_MP
	 * Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_RG_RECO_MP() {
		return getZone(getNOM_RG_RECO_MP());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AMENAGEMENT_NON Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_AMENAGEMENT_NON() {
		return "NOM_RB_AMENAGEMENT_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AMENAGEMENT_OUI Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_AMENAGEMENT_OUI() {
		return "NOM_RB_AMENAGEMENT_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECO_CRDHNC_NON Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RECO_CRDHNC_NON() {
		return "NOM_RB_RECO_CRDHNC_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECO_CRDHNC_OUI Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RECO_CRDHNC_OUI() {
		return "NOM_RB_RECO_CRDHNC_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECO_MP_NON Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RECO_MP_NON() {
		return "NOM_RB_RECO_MP_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECO_MP_OUI Date de
	 * cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RECO_MP_OUI() {
		return "NOM_RB_RECO_MP_OUI";
	}

	/**
	 * Getter de l'agent courant.
	 * 
	 * @return AgentNW
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Getter du handicap courant.
	 * 
	 * @return Handicap
	 */
	private Handicap getHandicapCourant() {
		return handicapCourant;
	}

	/**
	 * Setter du handicap courant.
	 * 
	 * @param handicapCourant
	 */
	private void setHandicapCourant(Handicap handicapCourant) {
		this.handicapCourant = handicapCourant;
	}

	/**
	 * Getter de la HashTable MaladiePro.
	 * 
	 * @return Hashtable<String, MaladiePro>
	 */
	private Hashtable<String, MaladiePro> getHashMaladiePro() {
		if (hashMaladiePro == null)
			hashMaladiePro = new Hashtable<String, MaladiePro>();
		return hashMaladiePro;
	}

	/**
	 * Getter de la HashTable NomHandicap.
	 * 
	 * @return Hashtable<String, NomHandicap>
	 */
	private Hashtable<String, NomHandicap> getHashNomHandicap() {
		if (hashNomHandicap == null)
			hashNomHandicap = new Hashtable<String, NomHandicap>();
		return hashNomHandicap;
	}

	/**
	 * Getter de la liste des handicaps.
	 * 
	 * @return ArrayList<Handicap>
	 */
	public ArrayList<Handicap> getListeHandicap() {
		return listeHandicap;
	}

	/**
	 * Setter de la liste des handicaps.
	 * 
	 * @param listeHandicap
	 */
	private void setListeHandicap(ArrayList<Handicap> listeHandicap) {
		this.listeHandicap = listeHandicap;
	}

	/**
	 * Getter de la liste des maladies pro.
	 * 
	 * @return ArrayList<MaladiePro>
	 */
	private ArrayList<MaladiePro> getListeMaladiePro() {
		return listeMaladiePro;
	}

	/**
	 * Setter de la liste des maladies pro.
	 * 
	 * @param listeMaladiePro
	 */
	private void setListeMaladiePro(ArrayList<MaladiePro> listeMaladiePro) {
		this.listeMaladiePro = listeMaladiePro;
	}

	/**
	 * Getter de la liste des noms de handicap.
	 * 
	 * @return ArrayList<NomHandicap>
	 */
	private ArrayList<NomHandicap> getListeNomHandicap() {
		return listeNomHandicap;
	}

	/**
	 * Setter de la liste des noms de handicap.
	 * 
	 * @param listeNomHandicap
	 */
	private void setListeNomHandicap(ArrayList<NomHandicap> listeNomHandicap) {
		this.listeNomHandicap = listeNomHandicap;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AMENAGEMENT Date de
	 * cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getNOM_ST_AMENAGEMENT() {
		return "NOM_ST_AMENAGEMENT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AMENAGEMENT
	 * Date de cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getVAL_ST_AMENAGEMENT() {
		return getZone(getNOM_ST_AMENAGEMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECO_CRDHNC Date de
	 * cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getNOM_ST_RECO_CRDHNC() {
		return "NOM_ST_RECO_CRDHNC";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RECO_CRDHNC
	 * Date de cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getVAL_ST_RECO_CRDHNC() {
		return getZone(getNOM_ST_RECO_CRDHNC());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECO_MP Date de
	 * cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getNOM_ST_RECO_MP() {
		return "NOM_ST_RECO_MP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RECO_MP Date
	 * de cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getVAL_ST_RECO_MP() {
		return getZone(getNOM_ST_RECO_MP());
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-HANDICAPS";
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (01/07/11 09:42:08)
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

			// Si clic sur le bouton PB_SELECT_CRDHNC
			if (testerParametre(request, getNOM_PB_SELECT_CRDHNC())) {
				return performPB_SELECT_CRDHNC(request);
			}

			// Si clic sur le bouton PB_SELECT_MALADIE_PRO
			if (testerParametre(request, getNOM_PB_SELECT_MALADIE_PRO())) {
				return performPB_SELECT_MALADIE_PRO(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeHandicap().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeHandicap().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeHandicap().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeHandicap().size(); i++) {
				if (testerParametre(request, getNOM_PB_DOCUMENT(i))) {
					return performPB_DOCUMENT(request, i);
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

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION())) {
				return performPB_VALIDER_DOCUMENT_SUPPRESSION(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (28/10/11 09:51:26)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTHandicap.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_CRDHNC Date de
	 * cr�ation : (28/10/11 09:51:26)
	 * 
	 */
	public String getNOM_PB_SELECT_CRDHNC() {
		return "NOM_PB_SELECT_CRDHNC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (28/10/11 09:51:26)
	 * 
	 */
	public boolean performPB_SELECT_CRDHNC(HttpServletRequest request) throws Exception {
		showNumCarte = getZone(getNOM_RG_RECO_CRDHNC()).equals(getNOM_RB_RECO_CRDHNC_OUI());
		addZone(getNOM_EF_NUM_CRDHNC(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_RENOUV_CRDHNC(), getNOM_RB_RENOUV_CRDHNC_NON());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_MALADIE_PRO Date de
	 * cr�ation : (28/10/11 09:51:26)
	 * 
	 */
	public String getNOM_PB_SELECT_MALADIE_PRO() {
		return "NOM_PB_SELECT_MALADIE_PRO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (28/10/11 09:51:26)
	 * 
	 */
	public boolean performPB_SELECT_MALADIE_PRO(HttpServletRequest request) throws Exception {
		showMaladiePro = getVAL_RG_RECO_MP().equals(getNOM_RB_RECO_MP_OUI());
		addZone(getNOM_LB_NOM_MP_SELECT(), Const.ZERO);
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_RENOUV_CRDHNC Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RG_RENOUV_CRDHNC() {
		return "NOM_RG_RENOUV_CRDHNC";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_RENOUV_CRDHNC Date de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getVAL_RG_RENOUV_CRDHNC() {
		return getZone(getNOM_RG_RENOUV_CRDHNC());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RENOUV_CRDHNC_NON Date
	 * de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RENOUV_CRDHNC_NON() {
		return "NOM_RB_RENOUV_CRDHNC_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RENOUV_CRDHNC_OUI Date
	 * de cr�ation : (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_RB_RENOUV_CRDHNC_OUI() {
		return "NOM_RB_RENOUV_CRDHNC_OUI";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RENOUV_CRDHNC Date
	 * de cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getNOM_ST_RENOUV_CRDHNC() {
		return "NOM_ST_RENOUV_CRDHNC";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RENOUV_CRDHNC
	 * Date de cr�ation : (04/07/11 16:06:38)
	 * 
	 */
	public String getVAL_ST_RENOUV_CRDHNC() {
		return getZone(getNOM_ST_RENOUV_CRDHNC());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE TYPE de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TYPE TYPE de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DEBUT DEBUT de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEBUT(int i) {
		return "NOM_ST_DEBUT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DEBUT DEBUT de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEBUT(int i) {
		return getZone(getNOM_ST_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FIN FIN de cr�ation
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_FIN(int i) {
		return "NOM_ST_FIN" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_FIN FIN de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_FIN(int i) {
		return getZone(getNOM_ST_FIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INCAPACITE
	 * INCAPACITE de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_INCAPACITE(int i) {
		return "NOM_ST_INCAPACITE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_INCAPACITE
	 * INCAPACITE de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_INCAPACITE(int i) {
		return getZone(getNOM_ST_INCAPACITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MALADIE_PROF
	 * MALADIE_PROF de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MALADIE_PROF(int i) {
		return "NOM_ST_MALADIE_PROF" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MALADIE_PROF
	 * MALADIE_PROF de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MALADIE_PROF(int i) {
		return getZone(getNOM_ST_MALADIE_PROF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CRDHNC CRDHNC de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CRDHNC(int i) {
		return "NOM_ST_CRDHNC" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CRDHNC CRDHNC
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CRDHNC(int i) {
		return getZone(getNOM_ST_CRDHNC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CARTE NUM_CARTE
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM_CARTE(int i) {
		return "NOM_ST_NUM_CARTE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM_CARTE
	 * NUM_CARTE de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM_CARTE(int i) {
		return getZone(getNOM_ST_NUM_CARTE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RENOUVELLEMENT
	 * RENOUVELLEMENT de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_RENOUVELLEMENT(int i) {
		return "NOM_ST_RENOUVELLEMENT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RENOUVELLEMENT
	 * RENOUVELLEMENT de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_RENOUVELLEMENT(int i) {
		return getZone(getNOM_ST_RENOUVELLEMENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AMENAGEMENT
	 * AMENAGEMENT de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AMENAGEMENT(int i) {
		return "NOM_ST_AMENAGEMENT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AMENAGEMENT
	 * AMENAGEMENT de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AMENAGEMENT(int i) {
		return getZone(getNOM_ST_AMENAGEMENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_DOC Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NB_DOC Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup du handicap courant
		Handicap handicapCourant = (Handicap) getListeHandicap().get(indiceEltAModifier);
		setHandicapCourant(handicapCourant);

		// init du diplome courant
		if (!initialiseHandicapCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup du handicap courant
		Handicap handicapCourant = (Handicap) getListeHandicap().get(indiceEltAConsulter);
		setHandicapCourant(handicapCourant);

		// init du diplome courant
		if (!initialiseHandicapSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de cr�ation
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup du handicap courant
		Handicap handicapCourant = (Handicap) getListeHandicap().get(indiceEltASuprimer);
		setHandicapCourant(handicapCourant);

		// init du diplome courant
		if (!initialiseHandicapSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DOCUMENT Date de cr�ation :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup du Handicap courante
		Handicap handiCourante = (Handicap) getListeHandicap().get(indiceEltDocument);
		setHandicapCourant(handiCourante);

		// init des documents VM de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (27/03/2003
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
	 * Initialisation de la liste des contacts
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(), getAgentCourant(), "HSCT", "HANDI",
				getHandicapCourant().getIdHandicap());
		setListeDocuments(listeDocAgent);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) TypeDocument.chercherTypeDocument(getTransaction(), doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument());
				addZone(getNOM_ST_TYPE_DOC(indiceActeVM), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE_DOCUMENT(indiceActeVM),
						doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());

				indiceActeVM++;
			}
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_DOC Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TYPE_DOC Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DOC Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DOC Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE_DOCUMENT
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE_DOCUMENT(int i) {
		return "NOM_ST_COMMENTAIRE_DOCUMENT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE_DOCUMENT(int i) {
		return getZone(getNOM_ST_COMMENTAIRE_DOCUMENT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC Date de cr�ation :
	 * (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_DOC Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");

		// R�cup du document courant
		Document d = (Document) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

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
	 * cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {

		// R�cup du Diplome courant
		Document d = getDocumentCourant();

		LienDocumentAgent lda = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant().getIdAgent(), getDocumentCourant()
				.getIdDocument());
		setLienDocumentAgentCourant(lda);

		if (getTransaction().isErreur())
			return false;

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
	 * Met � jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	private LienDocumentAgent getLienDocumentAgentCourant() {
		return lienDocumentAgentCourant;
	}

	/**
	 * Met � jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setLienDocumentAgentCourant(LienDocumentAgent lienDocumentAgentCourant) {
		this.lienDocumentAgentCourant = lienDocumentAgentCourant;
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

	public String getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION() {
		return "NOM_PB_VALIDER_DOCUMENT_SUPPRESSION";
	}

	private boolean performPB_VALIDER_DOCUMENT_SUPPRESSION(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		// suppression dans table DOCUMENT_AGENT
		getLienDocumentAgentCourant().supprimerLienDocumentAgent(getTransaction());
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
			logger.error("Erreur suppression physique du fichier : " + e.toString());
		}

		// tout s'est bien pass�
		commitTransaction();
		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		initialiseListeDocuments(request);
		return true;
	}

	public String getNOM_EF_COMMENTAIRE_DOCUMENT() {
		return "NOM_EF_COMMENTAIRE_DOCUMENT";
	}

	public String getVAL_EF_COMMENTAIRE_DOCUMENT() {
		return getZone(getNOM_EF_COMMENTAIRE_DOCUMENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de cr�ation : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT Date de cr�ation : (11/10/11 08:38:48)
	 * 
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Contr�le des champs
		if (!performControlerSaisieDocument(request))
			return false;

		Handicap handi = getHandicapCourant();

		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {
			String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();

			// on controle si il y a dej� un fichier pour ce handicap
			if (!performControlerFichier(request, "HANDI_" + handi.getIdHandicap() + "_" + dateJour)) {
				// alors on affiche un message pour prevenir que l'on va ecraser
				// le fichier precedent
				addZone(getNOM_ST_WARNING(),
						"Attention un fichier du m�me type existe d�j� pour ce handicap. Etes-vous s�r de vouloir �craser la version pr�c�dente ?");
				return true;
			}

			if (!creeDocument(request, handi)) {
				return false;
			}

		} else {
			// on supprime le document existant dans la base de donn�es
			Document d = Document.chercherDocumentByContainsNom(getTransaction(), "HANDI_" + handi.getIdHandicap());
			LienDocumentAgent l = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant().getIdAgent(), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			l.supprimerLienDocumentAgent(getTransaction());
			d.supprimerDocument(getTransaction());

			if (!creeDocument(request, handi)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met � jour le tableau des Handicaps pour avoir le nombre de
		// documents
		initialiseListeHandicap(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, Handicap handi) throws Exception {
		// on cr�e l'entr�e dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recup�re le type de document
		String codTypeDoc = "HANDI";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + handi.getIdHandicap() + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf"))
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		else
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);

		if (!upload)
			return false;

		// on cr�e le document en base de donn�es
		// String repPartage = (String)
		// ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE_DOCUMENT()));
		getDocumentCourant().creerDocument(getTransaction());

		setLienDocumentAgentCourant(new LienDocumentAgent());
		getLienDocumentAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocumentAgentCourant().setIdDocument(getDocumentCourant().getIdDocument());
		getLienDocumentAgentCourant().creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien pass�
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), Const.CHAINE_VIDE);

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
			logger.error("erreur d'execution " + e.toString());
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
		// on verifie d�j� que le repertoire source existe
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
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), multi.getParameter(getNOM_EF_COMMENTAIRE_DOCUMENT()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	private boolean performControlerFichier(HttpServletRequest request, String nomFichier) {
		boolean result = true;
		// on regarde dans la liste des document si il y a une entr�e avec ce
		// nom de contrat
		for (Iterator<Document> iter = getListeDocuments().iterator(); iter.hasNext();) {
			Document doc = (Document) iter.next();
			// on supprime l'extension
			String nomDocSansExtension = doc.getNomDocument().substring(0, doc.getNomDocument().indexOf("."));
			if (nomFichier.equals(nomDocSansExtension)) {
				result = false;
			}
		}
		return result;
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_WARNING Date de cr�ation : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * M�thode qui teste si un param�tre se trouve dans le formulaire
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