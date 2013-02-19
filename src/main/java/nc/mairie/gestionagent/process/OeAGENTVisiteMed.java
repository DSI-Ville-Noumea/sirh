package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.hsct.Inaptitude;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.hsct.TypeInaptitude;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.domain.metier.suiviMedical.MotifVisiteMed;
import nc.mairie.spring.domain.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.utils.ApplicationContextProvider;
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
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTVisiteMed Date de création : (20/06/11 15:25:51)
 * 
 */
public class OeAGENTVisiteMed extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private Logger logger = LoggerFactory.getLogger(OeAGENTVisiteMed.class);

	private String[] LB_TYPE;
	private String[] LB_MEDECIN;
	private String[] LB_MOTIF;
	private String[] LB_RECOMMANDATION;

	private nc.mairie.metier.agent.AgentNW agentCourant;
	private VisiteMedicale visiteCourante;
	private Inaptitude inaptitudeCourante;

	private ArrayList<VisiteMedicale> listeVisites;
	private ArrayList<Medecin> listeMedecin;
	private ArrayList<MotifVisiteMed> listeMotif;
	private ArrayList<Recommandation> listeRecommandation;
	private ArrayList<Inaptitude> listeInaptitude;
	private ArrayList<TypeInaptitude> listeTypeInaptitude;

	private Hashtable<String, Medecin> hashMedecin;
	private Hashtable<String, MotifVisiteMed> hashMotif;
	private Hashtable<String, Recommandation> hashRecommandation;
	private Hashtable<String, TypeInaptitude> hashTypeInaptitude;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche visite médicale.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche visite médicale.";
	private String ACTION_MODIFICATION = "Modification d'une fiche visite médicale.";
	private String ACTION_CREATION = "Création d'une fiche visite médicale.";

	public String ACTION_INAPTITUDE_SUPPRESSION = "Suppression d'une fiche inpatitude.";
	public String ACTION_INAPTITUDE_CONSULTATION = "Consultation d'une fiche inpatitude.";
	private String ACTION_INAPTITUDE_MODIFICATION = "Modification d'une fiche inpatitude.";
	private String ACTION_INAPTITUDE_CREATION = "Création d'une fiche inpatitude.";

	public String ACTION_DOCUMENT = "Documents d'une fiche visite médicale.";
	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche visite médicale.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document d'une fiche visite médicale.";
	private ArrayList listeDocuments;
	private Document documentCourant;
	private LienDocumentAgent lienDocumentAgentCourant;
	private String urlFichier;
	public boolean isImporting = false;
	public com.oreilly.servlet.MultipartRequest multi = null;
	public File fichierUpload = null;

	public String focus = null;

	private String messageInf = Const.CHAINE_VIDE;
	public boolean elementModifibale = true;
	public boolean champMotifModifiable = true;

	private SuiviMedicalDao suiviMedDao;

	private MotifVisiteMedDao motifVisiteMedDao;

	public SuiviMedicalDao getSuiviMedDao() {
		return suiviMedDao;
	}

	public void setSuiviMedDao(SuiviMedicalDao suiviMedDao) {
		this.suiviMedDao = suiviMedDao;
	}

	public MotifVisiteMedDao getMotifVisiteMedDao() {
		return motifVisiteMedDao;
	}

	public void setMotifVisiteMedDao(MotifVisiteMedDao motifVisiteMedDao) {
		this.motifVisiteMedDao = motifVisiteMedDao;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/06/11 09:36:41)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
			setVisiteCourante(null);
			multi = null;
			isImporting = false;
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

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeVisiteMed(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getSuiviMedDao() == null)
			setSuiviMedDao((SuiviMedicalDao) context.getBean("suiviMedicalDao"));
		if (getMotifVisiteMedDao() == null)
			setMotifVisiteMedDao((MotifVisiteMedDao) context.getBean("motifVisiteMedDao"));
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si hashtable des medecins vide
		if (getHashMedecin().size() == 0) {
			ArrayList listeMedecin = Medecin.listerMedecin(getTransaction());
			setListeMedecin(listeMedecin);

			int[] tailles = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, true);
			for (ListIterator list = getListeMedecin().listIterator(); list.hasNext();) {
				Medecin m = (Medecin) list.next();
				String ligne[] = { m.getPrenomMedecin() + " " + m.getNomMedecin() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MEDECIN(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < listeMedecin.size(); i++) {
				Medecin m = (Medecin) listeMedecin.get(i);
				getHashMedecin().put(m.getIdMedecin(), m);
			}
		}
		// Si hashtable des motifs vide
		if (getHashMotif().size() == 0) {
			ArrayList listeMotif = getMotifVisiteMedDao().listerMotifVisiteMed();
			setListeMotif(listeMotif);

			int[] tailles = { 40 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator list = listeMotif.listIterator(); list.hasNext();) {
				MotifVisiteMed motif = (MotifVisiteMed) list.next();
				String ligne[] = { motif.getLibMotifVM() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee(true));

			// remplissage de la hashTable
			for (int i = 0; i < listeMotif.size(); i++) {
				MotifVisiteMed m = (MotifVisiteMed) listeMotif.get(i);
				getHashMotif().put(m.getIdMotifVM().toString(), m);
			}
		}

		// Si hashtable des recommandations vide
		if (getHashRecommandation().size() == 0) {
			ArrayList listeRecommandation = Recommandation.listerRecommandation(getTransaction());
			setListeRecommandation(listeRecommandation);

			int[] tailles = { 150 };
			String[] champs = { "descRecommandation" };
			setLB_RECOMMANDATION(new FormateListe(tailles, listeRecommandation, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (int i = 0; i < listeRecommandation.size(); i++) {
				Recommandation r = (Recommandation) listeRecommandation.get(i);
				getHashRecommandation().put(r.getIdRecommandation(), r);
			}
		}

		// Si hashtable des types d'inpatitude vide
		if (getHashTypeInaptitude().size() == 0) {
			ArrayList listeTypeInaptitude = TypeInaptitude.listerTypeInaptitude(getTransaction());
			setListeTypeInaptitude(listeTypeInaptitude);

			int[] tailles = { 150 };
			String[] champs = { "descTypeInaptitude" };
			setLB_TYPE(new FormateListe(tailles, listeTypeInaptitude, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeInaptitude.size(); i++) {
				TypeInaptitude t = (TypeInaptitude) listeTypeInaptitude.get(i);
				getHashTypeInaptitude().put(t.getIdTypeInaptitude(), t);
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTVisiteMed. Date de création : (21/06/11
	 * 09:36:44)
	 * 
	 */
	public OeAGENTVisiteMed() {
		super();
	}

	/**
	 * Initialisation de la liste des visites médicale de l'agent courant
	 * 
	 */
	private void initialiseListeVisiteMed(HttpServletRequest request) throws Exception {
		// Recherche des visites médicales de l'agent
		ArrayList listeVisiteMed = VisiteMedicale.listerVisiteMedicaleAgent(getTransaction(), getAgentCourant());

		// Recherche des suivi médicaux de l'agent en statut planifié ou
		// convoqué
		ArrayList listeSuiviMed = getSuiviMedDao().listerSuiviMedicalEtatAgent(Integer.valueOf(getAgentCourant().getIdAgent()),
				EnumEtatSuiviMed.CONVOQUE.getCode(), EnumEtatSuiviMed.PLANIFIE.getCode(), EnumEtatSuiviMed.ACCOMP.getCode());
		for (int i = 0; i < listeSuiviMed.size(); i++) {
			// on recupere le suivi medical que l'on transforme en visite
			// medicale
			SuiviMedical sm = (SuiviMedical) listeSuiviMed.get(i);
			VisiteMedicale vm = new VisiteMedicale();
			vm.setIdVisite(null);
			vm.setIdAgent(sm.getIdAgent().toString());
			vm.setIdMedecin(sm.getIdMedecin() == null ? null : sm.getIdMedecin().toString());
			vm.setIdRecommandation(null);
			vm.setDateDerniereVisite(sm.getDateProchaineVisite() == null ? null : Services.convertitDate(sm.getDateProchaineVisite().toString(),
					"yyyy-MM-dd", "dd/MM/yyyy"));
			vm.setDureeValidite(Const.ZERO);
			vm.setApte(null);
			vm.setIdMotif(sm.getIdMotifVM().toString());
			vm.setIdSuiviMed(sm.getIdSuiviMed().toString());
			listeVisiteMed.add(vm);
		}
		// on trie par date la liste des VM
		Collections.sort(listeVisiteMed, new Comparator<VisiteMedicale>() {
			public int compare(VisiteMedicale o1, VisiteMedicale o2) {
				if (o1.getDateDerniereVisite() == null || o2.getDateDerniereVisite() == null) {
					return 0;
				}
				return Services.compareDates(Services.formateDate(o1.getDateDerniereVisite()), Services.formateDate(o2.getDateDerniereVisite()));
			}
		});
		Collections.reverse(listeVisiteMed);
		setListeVisites(listeVisiteMed);

		int indiceVisite = 0;
		if (getListeVisites() != null) {
			for (int i = 0; i < getListeVisites().size(); i++) {
				VisiteMedicale vm = (VisiteMedicale) getListeVisites().get(i);
				Medecin m = null;
				if (vm.getIdMedecin() != null) {
					m = (Medecin) getHashMedecin().get(vm.getIdMedecin());
				}
				MotifVisiteMed motif = null;
				if (vm.getIdMotif() != null) {
					motif = (MotifVisiteMed) getHashMotif().get(vm.getIdMotif());
				}
				Recommandation r = null;
				if (vm.getIdRecommandation() != null) {
					r = (Recommandation) getHashRecommandation().get(vm.getIdRecommandation());
				}
				// calcul du nb de docs
				ArrayList listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(), getAgentCourant(), "HSCT", "VM",
						vm.getIdVisite());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				addZone(getNOM_ST_DATE_VISITE(indiceVisite), vm.getDateDerniereVisite() == null ? "&nbsp;" : vm.getDateDerniereVisite());
				addZone(getNOM_ST_DUREE(indiceVisite),
						vm == null || vm.getDureeValidite().equals(Const.CHAINE_VIDE) || vm.getDureeValidite().equals(Const.ZERO) ? "&nbsp;" : vm
								.getDureeValidite());
				addZone(getNOM_ST_NOM_MEDECIN(indiceVisite),
						m == null || m.getNomMedecin().equals(Const.CHAINE_VIDE) ? "&nbsp;" : m.getTitreMedecin() + " " + m.getPrenomMedecin() + " "
								+ m.getNomMedecin());
				addZone(getNOM_ST_MOTIF(indiceVisite),
						motif == null || motif.getLibMotifVM().equals(Const.CHAINE_VIDE) ? "&nbsp;" : motif.getLibMotifVM());
				addZone(getNOM_ST_AVIS(indiceVisite), vm.getApte() == null ? "&nbsp;" : vm.getApte().equals("1") ? "Apte" : "Inapte");
				addZone(getNOM_ST_RECOMMANDATION(indiceVisite), r == null || r.getDescRecommandation().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: r.getDescRecommandation());
				addZone(getNOM_ST_NB_DOC(indiceVisite), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceVisite++;
			}
		}
	}

	/**
	 * Initialisation la liste des inaptitudes pour la visite médicale
	 * sélectionnée
	 * 
	 */
	private void initialiseListeInpatitude(HttpServletRequest request) throws Exception {
		// Recherche des visites médicales de l'agent
		ArrayList listeInaptitudes = Inaptitude.listerInaptitudeVisite(getTransaction(), getVisiteCourante());
		setListeInaptitude(listeInaptitudes);
		int indiceInaptitude = 0;
		if (getListeInaptitude() != null) {
			for (int i = 0; i < getListeInaptitude().size(); i++) {
				Inaptitude inapt = (Inaptitude) getListeInaptitude().get(i);
				TypeInaptitude ti = (TypeInaptitude) getHashTypeInaptitude().get(inapt.getIdTypeInaptitude());

				addZone(getNOM_ST_TYPE_INAPT(indiceInaptitude), ti.getDescTypeInaptitude().equals(Const.CHAINE_VIDE) ? "&nbsp;" : ti
						.getDescTypeInaptitude());
				addZone(getNOM_ST_DEBUT_INAPT(indiceInaptitude), inapt.getDateDebutInaptitude());
				addZone(getNOM_ST_ANNEES_INAPT(indiceInaptitude), inapt.getDureeAnnee() != null ? inapt.getDureeAnnee() : Const.ZERO);
				addZone(getNOM_ST_MOIS_INAPT(indiceInaptitude), inapt.getDureeMois() != null ? inapt.getDureeMois() : Const.ZERO);
				addZone(getNOM_ST_JOURS_INAPT(indiceInaptitude), inapt.getDureeJour() != null ? inapt.getDureeJour() : Const.ZERO);

				indiceInaptitude++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (21/06/11 09:36:45)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/06/11 09:36:45)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setVisiteCourante(new VisiteMedicale());
		videZonesDeSaisie(request);
		champMotifModifiable = true;
		elementModifibale = true;

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une
	 * visite médicales
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_EF_DATE_VISITE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MEDECIN_SELECT(), Const.ZERO);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		addZone(getNOM_RG_AVIS(), getNOM_RB_APTE());
		addZone(getNOM_LB_RECOMMANDATION_SELECT(), Const.ZERO);
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une
	 * inaptitude
	 * 
	 */
	private void videZonesDeSaisieInaptitude(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_LB_TYPE_SELECT(), Const.ZERO);
		addZone(getNOM_EF_DEBUT_INAPTITUDE(), Const.CHAINE_VIDE);

		addZone(getNOM_EF_DUREE_ANNEES(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE_MOIS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE_JOURS(), Const.CHAINE_VIDE);
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'une visite
	 * médicale Date de création : 11/06/27
	 * 
	 */
	private boolean initialiseVisiteCourante() throws Exception {

		// Récup de la visite médicale courante
		Medecin medecin = (Medecin) getHashMedecin().get(getVisiteCourante().getIdMedecin());
		Recommandation recommandation = null;
		if (getVisiteCourante().getIdRecommandation() != null) {
			recommandation = (Recommandation) getHashRecommandation().get(getVisiteCourante().getIdRecommandation());
			int ligneRecommandation = getListeRecommandation().indexOf(recommandation);
			addZone(getNOM_LB_RECOMMANDATION_SELECT(), String.valueOf(ligneRecommandation + 1));
		} else {
			addZone(getNOM_LB_RECOMMANDATION_SELECT(), Const.ZERO);
		}

		MotifVisiteMed motif = null;
		if (getVisiteCourante().getIdMotif() != null) {
			motif = (MotifVisiteMed) getHashMotif().get(getVisiteCourante().getIdMotif());
			int ligneMotif = getListeMotif().indexOf(motif);
			addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif + 1));
		} else {
			addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		}

		// Alim zones
		addZone(getNOM_EF_DATE_VISITE(), getVisiteCourante().getDateDerniereVisite());
		addZone(getNOM_EF_DUREE(), getVisiteCourante().getDureeValidite().equals(Const.ZERO) ? Const.CHAINE_VIDE : getVisiteCourante()
				.getDureeValidite());

		int ligneMedecin = getListeMedecin().indexOf(medecin);
		addZone(getNOM_LB_MEDECIN_SELECT(), String.valueOf(ligneMedecin));

		if (getVisiteCourante().getApte() != null) {
			if (getVisiteCourante().getApte().equals("1"))
				addZone(getNOM_RG_AVIS(), getNOM_RB_APTE());
			else
				addZone(getNOM_RG_AVIS(), getNOM_RB_INAPTE());
		}

		return true;
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'une
	 * inaptitude Date de création : 11/06/27
	 * 
	 */
	private boolean initialiseInaptitudeCourante(HttpServletRequest request) throws Exception {

		// Récup de la visite médicale courante
		Inaptitude inaptitude = getInaptitudeCourante();
		setInaptitudeCourante(inaptitude);
		TypeInaptitude type = (TypeInaptitude) getHashTypeInaptitude().get(inaptitude.getIdTypeInaptitude());

		// Alim zones
		addZone(getNOM_EF_DEBUT_INAPTITUDE(), inaptitude.getDateDebutInaptitude());
		addZone(getNOM_EF_DUREE_ANNEES(), inaptitude.getDureeAnnee());
		addZone(getNOM_EF_DUREE_MOIS(), inaptitude.getDureeMois());
		addZone(getNOM_EF_DUREE_JOURS(), inaptitude.getDureeJour());

		int ligneType = getListeTypeInaptitude().indexOf(type);
		addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));

		return true;
	}

	/**
	 * Initialisation de la suppression d'une visite médicale
	 * 
	 * @param request
	 * @return true si la suppression peut être effectuée
	 * @throws Exception
	 */
	private boolean initialiseVisiteSuppression(HttpServletRequest request) throws Exception {
		Medecin medecin = (Medecin) getHashMedecin().get(getVisiteCourante().getIdMedecin());
		Recommandation recommandation = null;
		if (getVisiteCourante().getIdRecommandation() != null) {
			recommandation = (Recommandation) getHashRecommandation().get(getVisiteCourante().getIdRecommandation());
		}
		MotifVisiteMed motif = null;
		if (getVisiteCourante().getIdMotif() != null) {
			motif = (MotifVisiteMed) getHashMotif().get(getVisiteCourante().getIdMotif());
		}
		if (getTransaction().isErreur())
			return false;

		addZone(getNOM_ST_DATE_VISITE(), getVisiteCourante().getDateDerniereVisite());
		addZone(getNOM_ST_DUREE_VALIDITE(), getVisiteCourante().getDureeValidite().equals(Const.ZERO) ? Const.CHAINE_VIDE : getVisiteCourante()
				.getDureeValidite());
		addZone(getNOM_ST_NOM_MEDECIN(), medecin.getTitreMedecin() + " " + medecin.getPrenomMedecin() + " " + medecin.getNomMedecin());
		addZone(getNOM_ST_MOTIF(), motif != null ? motif.getLibMotifVM() : Const.CHAINE_VIDE);
		addZone(getNOM_ST_AVIS(), getVisiteCourante().getApte() == null ? Const.CHAINE_VIDE : getVisiteCourante().getApte().equals("1") ? "APTE"
				: "INAPTE");
		addZone(getNOM_ST_RECOMMANDATION(), recommandation == null ? Const.CHAINE_VIDE : recommandation.getDescRecommandation());

		return true;
	}

	/**
	 * Initialisation de la suppression d'une inaptitude
	 * 
	 * @param request
	 * @return true si la suppression peut être effectuée
	 * @throws Exception
	 */
	private boolean initialiseInaptitudeSuppression(HttpServletRequest request) throws Exception {

		// Récup de la visite médicale courante
		Inaptitude inaptitude = getInaptitudeCourante();
		TypeInaptitude type = (TypeInaptitude) getHashTypeInaptitude().get(inaptitude.getIdTypeInaptitude());

		if (getTransaction().isErreur())
			return false;

		addZone(getNOM_ST_DEBUT_INAPTITUDE(), inaptitude.dateDebutInaptitude);
		addZone(getNOM_ST_DUREE_ANNEES(), inaptitude.dureeAnnee);
		addZone(getNOM_ST_DUREE_MOIS(), inaptitude.dureeMois);
		addZone(getNOM_ST_DUREE_JOURS(), inaptitude.dureeJour);
		addZone(getNOM_ST_TYPE(), type.descTypeInaptitude);

		return true;
	}

	public nc.mairie.metier.agent.AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(nc.mairie.metier.agent.AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_VISITE Date de
	 * création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_EF_DATE_VISITE() {
		return "NOM_EF_DATE_VISITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_VISITE Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getVAL_EF_DATE_VISITE() {
		return getZone(getNOM_EF_DATE_VISITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE Date de
	 * création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_EF_DUREE() {
		return "NOM_EF_DUREE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getVAL_EF_DUREE() {
		return getZone(getNOM_EF_DUREE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de création
	 * : (21/06/11 15:04:18)
	 * 
	 */
	private String[] getLB_MEDECIN() {
		if (LB_MEDECIN == null)
			LB_MEDECIN = initialiseLazyLB();
		return LB_MEDECIN;
	}

	/**
	 * Setter de la liste: LB_MEDECIN Date de création : (21/06/11 15:04:18)
	 * 
	 */
	private void setLB_MEDECIN(String[] newLB_MEDECIN) {
		LB_MEDECIN = newLB_MEDECIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de création
	 * : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_MEDECIN() {
		return "NOM_LB_MEDECIN";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MEDECIN_SELECT Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT() {
		return "NOM_LB_MEDECIN_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MEDECIN Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN() {
		return getLB_MEDECIN();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MEDECIN Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT() {
		return getZone(getNOM_LB_MEDECIN_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (21/06/11 15:04:18)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (21/06/11 15:04:18)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RECOMMANDATION Date de
	 * création : (21/06/11 15:04:18)
	 * 
	 */
	private String[] getLB_RECOMMANDATION() {
		if (LB_RECOMMANDATION == null)
			LB_RECOMMANDATION = initialiseLazyLB();
		return LB_RECOMMANDATION;
	}

	/**
	 * Setter de la liste: LB_RECOMMANDATION Date de création : (21/06/11
	 * 15:04:18)
	 * 
	 */
	private void setLB_RECOMMANDATION(String[] newLB_RECOMMANDATION) {
		LB_RECOMMANDATION = newLB_RECOMMANDATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RECOMMANDATION Date de
	 * création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION() {
		return "NOM_LB_RECOMMANDATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RECOMMANDATION_SELECT Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION_SELECT() {
		return "NOM_LB_RECOMMANDATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RECOMMANDATION Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String[] getVAL_LB_RECOMMANDATION() {
		return getLB_RECOMMANDATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RECOMMANDATION Date de création : (21/06/11 15:04:18)
	 * 
	 */
	public String getVAL_LB_RECOMMANDATION_SELECT() {
		return getZone(getNOM_LB_RECOMMANDATION_SELECT());
	}

	public ArrayList getListeVisites() {
		if (listeVisites == null) {
			listeVisites = new ArrayList();
		}
		return listeVisites;
	}

	private void setListeVisites(ArrayList listeVisites) {
		this.listeVisites = listeVisites;
	}

	private ArrayList<Medecin> getListeMedecin() {
		return listeMedecin;
	}

	private void setListeMedecin(ArrayList<Medecin> listeMedecin) {
		this.listeMedecin = listeMedecin;
	}

	private ArrayList<MotifVisiteMed> getListeMotif() {
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifVisiteMed> listeMotif) {
		this.listeMotif = listeMotif;
	}

	public VisiteMedicale getVisiteCourante() {
		return visiteCourante;
	}

	private void setVisiteCourante(VisiteMedicale visiteCourante) {
		this.visiteCourante = visiteCourante;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (21/06/11 16:31:57)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (21/06/11 16:31:57)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (22/06/11 10:20:56)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 10:20:56)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION()) && Const.CHAINE_VIDE.equals(getVAL_ST_ACTION_INAPTITUDE())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else if (!Const.CHAINE_VIDE.equals(getVAL_ST_ACTION_INAPTITUDE())) {
			// initialiseVisiteCourante();
			setVisiteCourante(null);
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		} else {
			setVisiteCourante(null);
			// initialiseVisiteCourante();
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (22/06/11 10:20:56)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 10:20:56)
	 * 
	 * RG_AG_VM_C01 RG_AG_VM_A01 RG-SVM-20 RG-SVM-19
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		messageInf = Const.CHAINE_VIDE;

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0 && getZone(getNOM_ST_ACTION_INAPTITUDE()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		if (getZone(getNOM_ST_ACTION()).length() != 0) {
			// Si Action Suppression
			if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
				try {
					// si la visite est liée à une convocation alors on supprime
					// aussi la convocation
					// RG-SVM-19
					if (getVisiteCourante().getIdSuiviMed() != null) {
						getSuiviMedDao().supprimerSuiviMedicalById(Integer.valueOf(getVisiteCourante().getIdSuiviMed()));
					}
				} catch (Exception e) {
					return false;
				}
				// Suppression
				getVisiteCourante().supprimerVisiteMedicale(getTransaction());
				if (getTransaction().isErreur())
					return false;

				setVisiteCourante(null);
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				setStatut(STATUT_MEME_PROCESS);

			} else {
				if (elementModifibale) {
					// vérification de la validité du formulaire
					if (!performControlerChamps(request)) {
						return false;
					}
				}
				// RG_AG_VM_C01 - Vérification si la PA de l'agent donne le
				// droit à visite médicale à la date donnée
				PositionAdmAgent posAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(), getAgentCourant()
						.getNoMatricule(), Services.convertitDate(Services.formateDate(getZone(getNOM_EF_DATE_VISITE())), "dd/MM/yyyy", "yyyyMMdd"));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR132"));
					return false;
				} else {
					if (!posAgent.permetVM()) {
						messageInf = MessageUtils.getMessage("INF009", "visites médicales");
					}
				}

				// récupération des informations remplies dans les zones de
				// saisie
				String dateVisite = getZone(getNOM_EF_DATE_VISITE());
				String duree = getZone(getNOM_EF_DUREE());
				Boolean apte = getZone(getNOM_RG_AVIS()).equals(getNOM_RB_APTE());
				String apteVM = null;
				if (elementModifibale) {
					if (apte) {
						apteVM = "1";
					} else {
						apteVM = "0";
					}
				}

				int numLigneMedecin = (Services.estNumerique(getZone(getNOM_LB_MEDECIN_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_MEDECIN_SELECT())) : -1);

				if (numLigneMedecin == -1 || getListeMedecin().size() == 0 || numLigneMedecin > getListeMedecin().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Médecins"));
					return false;
				}

				Medecin medecin = (Medecin) getListeMedecin().get(numLigneMedecin - 1);

				int numLigneMotif = (Services.estNumerique(getZone(getNOM_LB_MOTIF_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_MOTIF_SELECT()))
						: -1);

				if (numLigneMotif == -1 || getListeMotif().size() == 0 || numLigneMotif > getListeMotif().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motif"));
					return false;
				}

				MotifVisiteMed motif = (MotifVisiteMed) getListeMotif().get(numLigneMotif - 1);

				Recommandation recommandation = null;
				if (elementModifibale) {
					int numLigneRecommandation = (Services.estNumerique(getZone(getNOM_LB_RECOMMANDATION_SELECT())) ? Integer
							.parseInt(getZone(getNOM_LB_RECOMMANDATION_SELECT())) : -1);

					if (numLigneRecommandation == -1 || getListeRecommandation().size() == 0
							|| numLigneRecommandation > getListeRecommandation().size()) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Recommandations"));
						return false;
					}
					recommandation = (Recommandation) getListeRecommandation().get(numLigneRecommandation - 1);
				}

				// RG_AG_VM_C01 - Vérification si la PA de l'agent donne le
				// droit à visite médicale.
				ArrayList<PositionAdmAgent> listePA = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(), getAgentCourant());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				for (PositionAdmAgent pa : listePA) {
					if (Services.compareDates(pa.getDatdeb(), dateVisite) <= 0
							&& (pa.getDatfin() == null || Services.compareDates(pa.getDatfin(), dateVisite) >= 0)) {
						if (!pa.permetVM()) {
							messageInf = MessageUtils.getMessage("INF009", "visites médicales");
						}
						break;
					}
				}

				// Création de l'objet VisiteMedicale à créer/modifier
				AgentNW agentCourant = getAgentCourant();
				getVisiteCourante().setIdAgent(agentCourant.getIdAgent());
				getVisiteCourante().setDateDerniereVisite(dateVisite);
				getVisiteCourante().setDureeValidite(duree);
				getVisiteCourante().setApte(apteVM);
				getVisiteCourante().setIdMedecin(medecin.getIdMedecin());
				getVisiteCourante().setIdMotif(motif.getIdMotifVM().toString());
				getVisiteCourante().setIdRecommandation(recommandation != null ? recommandation.getIdRecommandation() : null);

				if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
					// si tranformation d'un suivi medical en VM
					// RG-SVM-20
					if (getVisiteCourante().getIdVisite() == null) {
						// fermeture des lignes de convocations
						try {
							SuiviMedical sm = getSuiviMedDao().chercherSuiviMedical(Integer.valueOf(getVisiteCourante().getIdSuiviMed()));
							ArrayList<SuiviMedical> listeSmAModif = getSuiviMedDao().listerSuiviMedicalAgentAnterieurDate(
									Integer.valueOf(getAgentCourant().getIdAgent()), sm.getMois(), sm.getAnnee());
							for (int i = 0; i < listeSmAModif.size(); i++) {
								SuiviMedical smModif = listeSmAModif.get(i);
								smModif.setEtat(EnumEtatSuiviMed.EFFECTUE.getCode());
								// on passe toutes ces lignes à l'état effectué
								getSuiviMedDao().modifierSuiviMedicalTravail(smModif.getIdSuiviMed(), smModif);
							}
						} catch (Exception e) {
							return false;
						}
						// si le motif est : a la demande...
						// alors il faut supprimer la visite medicale
						// correspondante
						Medecin med = Medecin.chercherMedecinByLib(getTransaction(), Const.CHAINE_VIDE, "A", "RENSEIGNER");
						if (getVisiteCourante().getIdMotif().equals(EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode())
								|| getVisiteCourante().getIdMotif().equals(EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode())) {
							VisiteMedicale vmASupp = VisiteMedicale.chercherVisiteMedicaleCriteres(getTransaction(), getAgentCourant().getIdAgent(),
									med.getIdMedecin(), getVisiteCourante().getIdMotif());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							} else {
								vmASupp.supprimerVisiteMedicale(getTransaction());
							}
						}
						// si tout s'est bien passé
						// Création de la visite medicale
						getVisiteCourante().creerVisiteMedicale(getTransaction());
					} else {
						// Modification
						getVisiteCourante().modifierVisiteMedicale(getTransaction());
					}
				} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
					// Création
					getVisiteCourante().creerVisiteMedicale(getTransaction());
				}
				if (getTransaction().isErreur())
					return false;
			}

			// On a fini l'action
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			// Tout s'est bien passé
			commitTransaction();
			initialiseListeVisiteMed(request);

			if (!Const.CHAINE_VIDE.equals(messageInf)) {
				setFocus(null);
				getTransaction().declarerErreur(messageInf);
			}

		} else {
			// Si Action Suppression
			if (getZone(getNOM_ST_ACTION_INAPTITUDE()).equals(ACTION_INAPTITUDE_SUPPRESSION)) {

				// Suppression
				getInaptitudeCourante().supprimerInaptitude(getTransaction());
				if (getTransaction().isErreur())
					return false;

				setVisiteCourante(null);
				setInaptitudeCourante(null);
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				setStatut(STATUT_MEME_PROCESS);
			} else {

				// vérification de la validité du formulaire
				if (!performControlerChampsInaptitude(request)) {
					return false;
				}

				// récupération des informations remplies dans les zones de
				// saisie
				String debutInaptitude = getZone(getNOM_EF_DEBUT_INAPTITUDE());
				String dureeAnnees = getZone(getNOM_EF_DUREE_ANNEES());
				String dureeMois = getZone(getNOM_EF_DUREE_MOIS());
				String dureeJours = getZone(getNOM_EF_DUREE_JOURS());

				int numLigneTypeInaptitude = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);

				if (numLigneTypeInaptitude == -1 || getListeTypeInaptitude().size() == 0 || numLigneTypeInaptitude >= getListeTypeInaptitude().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'inaptitude"));
					return false;
				}

				TypeInaptitude typeInaptitude = (TypeInaptitude) getListeTypeInaptitude().get(numLigneTypeInaptitude - 1);

				// verification des regles de gestions
				// RG_AG_VM_A01
				ArrayList<Inaptitude> listeInaptitudes = getListeInaptitude();
				if (listeInaptitudes != null) {
					for (int i = 0; i < listeInaptitudes.size(); i++) {
						Inaptitude inaptitude = (Inaptitude) listeInaptitudes.get(i);
						if (!inaptitude.getIdInaptitude().equals(getInaptitudeCourante().getIdInaptitude())
								&& inaptitude.getIdTypeInaptitude().equals(typeInaptitude.getIdTypeInaptitude())) {
							if (!testDate(inaptitude.getDateDebutInaptitude(), debutInaptitude, dureeAnnees, dureeMois, dureeJours)
									|| !testDate(debutInaptitude, inaptitude.getDateDebutInaptitude(), inaptitude.getDureeAnnee(),
											inaptitude.getDureeMois(), inaptitude.getDureeJour())) {
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR041"));
								return false;
							}
						}
					}
				}

				// Création de l'objet VisiteMedicale à créer/modifier
				VisiteMedicale visiteCourante = getVisiteCourante();
				getInaptitudeCourante().setIdVisite(visiteCourante.getIdVisite());

				getInaptitudeCourante().setIdTypeInaptitude(typeInaptitude.getIdTypeInaptitude());
				getInaptitudeCourante().setDateDebutInaptitude(debutInaptitude);

				getInaptitudeCourante().setDureeAnnee(dureeAnnees);
				getInaptitudeCourante().setDureeMois(dureeMois);
				getInaptitudeCourante().setDureeJour(dureeJours);

				if (getZone(getNOM_ST_ACTION_INAPTITUDE()).equals(ACTION_INAPTITUDE_MODIFICATION)) {
					// Modification
					getInaptitudeCourante().modifierInaptitude(getTransaction());
				} else if (getZone(getNOM_ST_ACTION_INAPTITUDE()).equals(ACTION_INAPTITUDE_CREATION)) {
					// Création
					getInaptitudeCourante().creerInaptitude(getTransaction());
				}
				if (getTransaction().isErreur())
					return false;
			}

			// On a fini l'action
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);

			// Tout s'est bien passé
			commitTransaction();
			initialiseListeInpatitude(request);
		}

		return true;
	}

	/**
	 * Test si la date1 est comprise entre date2 et date2 + annees + mois +
	 * jours
	 * 
	 * @return
	 */
	private boolean testDate(String dateTest, String dateDebut, String annees, String mois, String jours) {

		int nbJours = (jours != null && !jours.equals(Const.CHAINE_VIDE)) ? Integer.parseInt(jours) : 0;
		int nbMois = (mois != null && !mois.equals(Const.CHAINE_VIDE)) ? Integer.parseInt(mois) : 0;
		int nbAnnees = (annees != null && !annees.equals(Const.CHAINE_VIDE)) ? Integer.parseInt(annees) : 0;

		String dateFin = Services.ajouteJours(Services.ajouteMois(Services.ajouteAnnee(Services.formateDate(dateDebut), nbAnnees), nbMois), nbJours);

		int compareTestDebut = Services.compareDates(dateTest, dateDebut);
		int compareTestFin = Services.compareDates(dateTest, dateFin);

		// Test si la date a tester est entre la date début( == 0 -> inclue) et
		// la date fin (exclue)
		if (compareTestDebut == 0 || (compareTestDebut == 1 && compareTestFin == -1))
			return false;
		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire de visite médicale
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// date de visite obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_VISITE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de visite"));
			setFocus(getNOM_EF_DATE_VISITE());
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE_VISITE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", Const.CHAINE_VIDE));
			return false;
		}

		// duree de validite
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DUREE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "duree de validite"));
			setFocus(getNOM_EF_DUREE());
			return false;
		} else if (!Services.estNumerique(getZone(getNOM_EF_DUREE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "duree de validite"));
			setFocus(getNOM_EF_DUREE());
			return false;
		}

		// médecin obligatoire
		int indiceMedecin = (Services.estNumerique(getVAL_LB_MEDECIN_SELECT()) ? Integer.parseInt(getVAL_LB_MEDECIN_SELECT()) : -1);
		if (indiceMedecin < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "médecin"));
			setFocus(getNOM_LB_MEDECIN());
			return false;
		}

		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indiceMotif < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			setFocus(getNOM_LB_MOTIF());
			return false;
		}

		// recommandation obligatoire
		int indiceRecommandation = (Services.estNumerique(getVAL_LB_RECOMMANDATION_SELECT()) ? Integer.parseInt(getVAL_LB_RECOMMANDATION_SELECT())
				: -1);
		if (indiceRecommandation < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "recommandation"));
			setFocus(getNOM_LB_MEDECIN());
			return false;
		}

		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'inaptitude
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 */
	public boolean performControlerChampsInaptitude(HttpServletRequest request) throws Exception {

		// type inaptitude obligatoire
		int indiceTypeInap = (Services.estNumerique(getVAL_LB_TYPE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_SELECT()) : -1);
		if (indiceTypeInap < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type"));
			setFocus(getNOM_LB_TYPE());
			return false;
		}

		// date de début obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DEBUT_INAPTITUDE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			setFocus(getNOM_EF_DEBUT_INAPTITUDE());
			return false;
		}

		// duree obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DUREE_ANNEES())) && (Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DUREE_MOIS()))
				&& (Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DUREE_JOURS()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée (année ou mois ou jours)"));
			setFocus(getNOM_EF_DUREE_ANNEES());
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_AVIS
	 * Date de création : (22/06/11 11:10:15)
	 * 
	 */
	public String getNOM_RG_AVIS() {
		return "NOM_RG_AVIS";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_AVIS Date
	 * de création : (22/06/11 11:10:15)
	 * 
	 */
	public String getVAL_RG_AVIS() {
		return getZone(getNOM_RG_AVIS());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_APTE Date de création :
	 * (22/06/11 11:10:15)
	 * 
	 */
	public String getNOM_RB_APTE() {
		return "NOM_RB_APTE";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_INAPTE Date de création
	 * : (22/06/11 11:10:15)
	 * 
	 */
	public String getNOM_RB_INAPTE() {
		return "NOM_RB_INAPTE";
	}

	private ArrayList<Recommandation> getListeRecommandation() {
		return listeRecommandation;
	}

	private void setListeRecommandation(ArrayList<Recommandation> listeRecommandation) {
		this.listeRecommandation = listeRecommandation;
	}

	private Hashtable<String, Medecin> getHashMedecin() {
		if (hashMedecin == null) {
			hashMedecin = new Hashtable<String, Medecin>();
		}
		return hashMedecin;
	}

	private Hashtable<String, MotifVisiteMed> getHashMotif() {
		if (hashMotif == null) {
			hashMotif = new Hashtable<String, MotifVisiteMed>();
		}
		return hashMotif;
	}

	private Hashtable<String, Recommandation> getHashRecommandation() {
		if (hashRecommandation == null) {
			hashRecommandation = new Hashtable<String, Recommandation>();
		}
		return hashRecommandation;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_AVIS() {
		return "NOM_ST_AVIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_AVIS() {
		return getZone(getNOM_ST_AVIS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_VISITE Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_DATE_VISITE() {
		return "NOM_ST_DATE_VISITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_VISITE
	 * Date de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_DATE_VISITE() {
		return getZone(getNOM_ST_DATE_VISITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_VALIDITE Date
	 * de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_DUREE_VALIDITE() {
		return "NOM_ST_DUREE_VALIDITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE_VALIDITE
	 * Date de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_DUREE_VALIDITE() {
		return getZone(getNOM_ST_DUREE_VALIDITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_MEDECIN Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_NOM_MEDECIN() {
		return "NOM_ST_NOM_MEDECIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_MEDECIN
	 * Date de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_NOM_MEDECIN() {
		return getZone(getNOM_ST_NOM_MEDECIN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_MOTIF() {
		return "NOM_ST_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_MOTIF() {
		return getZone(getNOM_ST_MOTIF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECOMMANDATION Date
	 * de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getNOM_ST_RECOMMANDATION() {
		return "NOM_ST_RECOMMANDATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RECOMMANDATION
	 * Date de création : (27/06/11 09:10:43)
	 * 
	 */
	public String getVAL_ST_RECOMMANDATION() {
		return getZone(getNOM_ST_RECOMMANDATION());
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_INAPTITUDE
	 * Date de création : (27/06/11 14:32:47)
	 * 
	 */
	public String getNOM_ST_ACTION_INAPTITUDE() {
		return "NOM_ST_ACTION_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_INAPTITUDE Date de création : (27/06/11 14:32:47)
	 * 
	 */
	public String getVAL_ST_ACTION_INAPTITUDE() {
		return getZone(getNOM_ST_ACTION_INAPTITUDE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DEBUT_INAPTITUDE
	 * Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_ST_DEBUT_INAPTITUDE() {
		return "NOM_ST_DEBUT_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DEBUT_INAPTITUDE Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_ST_DEBUT_INAPTITUDE() {
		return getZone(getNOM_ST_DEBUT_INAPTITUDE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_ANNEES Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_ST_DUREE_ANNEES() {
		return "NOM_ST_DUREE_ANNEES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE_ANNEES
	 * Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_ST_DUREE_ANNEES() {
		return getZone(getNOM_ST_DUREE_ANNEES());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_JOURS Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_ST_DUREE_JOURS() {
		return "NOM_ST_DUREE_JOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE_JOURS
	 * Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_ST_DUREE_JOURS() {
		return getZone(getNOM_ST_DUREE_JOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_MOIS Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_ST_DUREE_MOIS() {
		return "NOM_ST_DUREE_MOIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE_MOIS
	 * Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_ST_DUREE_MOIS() {
		return getZone(getNOM_ST_DUREE_MOIS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_ST_TYPE() {
		return "NOM_ST_TYPE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_ST_TYPE() {
		return getZone(getNOM_ST_TYPE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DEBUT_INAPTITUDE
	 * Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_EF_DEBUT_INAPTITUDE() {
		return "NOM_EF_DEBUT_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DEBUT_INAPTITUDE Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_EF_DEBUT_INAPTITUDE() {
		return getZone(getNOM_EF_DEBUT_INAPTITUDE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_ANNEES Date
	 * de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_EF_DUREE_ANNEES() {
		return "NOM_EF_DUREE_ANNEES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_ANNEES Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_EF_DUREE_ANNEES() {
		return getZone(getNOM_EF_DUREE_ANNEES());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_JOURS Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_EF_DUREE_JOURS() {
		return "NOM_EF_DUREE_JOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_JOURS Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_EF_DUREE_JOURS() {
		return getZone(getNOM_EF_DUREE_JOURS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_MOIS Date de
	 * création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_EF_DUREE_MOIS() {
		return "NOM_EF_DUREE_MOIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_MOIS Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_EF_DUREE_MOIS() {
		return getZone(getNOM_EF_DUREE_MOIS());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE Date de création :
	 * (27/06/11 15:30:43)
	 * 
	 */
	private String[] getLB_TYPE() {
		if (LB_TYPE == null)
			LB_TYPE = initialiseLazyLB();
		return LB_TYPE;
	}

	/**
	 * Setter de la liste: LB_TYPE Date de création : (27/06/11 15:30:43)
	 * 
	 */
	private void setLB_TYPE(String[] newLB_TYPE) {
		LB_TYPE = newLB_TYPE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE Date de création :
	 * (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_LB_TYPE() {
		return "NOM_LB_TYPE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_SELECT Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getNOM_LB_TYPE_SELECT() {
		return "NOM_LB_TYPE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String[] getVAL_LB_TYPE() {
		return getLB_TYPE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE Date de création : (27/06/11 15:30:43)
	 * 
	 */
	public String getVAL_LB_TYPE_SELECT() {
		return getZone(getNOM_LB_TYPE_SELECT());
	}

	private Hashtable<String, TypeInaptitude> getHashTypeInaptitude() {
		if (hashTypeInaptitude == null) {
			hashTypeInaptitude = new Hashtable<String, TypeInaptitude>();
		}

		return hashTypeInaptitude;
	}

	private ArrayList<TypeInaptitude> getListeTypeInaptitude() {
		return listeTypeInaptitude;
	}

	private void setListeTypeInaptitude(ArrayList<TypeInaptitude> listeTypeInaptitude) {
		this.listeTypeInaptitude = listeTypeInaptitude;
	}

	public ArrayList<Inaptitude> getListeInaptitude() {
		if (listeInaptitude == null) {
			listeInaptitude = new ArrayList();
		}
		return listeInaptitude;
	}

	private void setListeInaptitude(ArrayList<Inaptitude> listeInaptitude) {
		this.listeInaptitude = listeInaptitude;
	}

	private Inaptitude getInaptitudeCourante() {
		return inaptitudeCourante;
	}

	private void setInaptitudeCourante(Inaptitude inaptitudeCourante) {
		this.inaptitudeCourante = inaptitudeCourante;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AMENAGEMENT Date de
	 * création : (04/07/11 16:01:48)
	 * 
	 */
	public String getNOM_ST_AMENAGEMENT() {
		return "NOM_ST_AMENAGEMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AMENAGEMENT
	 * Date de création : (04/07/11 16:01:48)
	 * 
	 */
	public String getVAL_ST_AMENAGEMENT() {
		return getZone(getNOM_ST_AMENAGEMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECO_CRDHNC Date de
	 * création : (04/07/11 16:01:48)
	 * 
	 */
	public String getNOM_ST_RECO_CRDHNC() {
		return "NOM_ST_RECO_CRDHNC";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RECO_CRDHNC
	 * Date de création : (04/07/11 16:01:48)
	 * 
	 */
	public String getVAL_ST_RECO_CRDHNC() {
		return getZone(getNOM_ST_RECO_CRDHNC());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECO_MP Date de
	 * création : (04/07/11 16:01:48)
	 * 
	 */
	public String getNOM_ST_RECO_MP() {
		return "NOM_ST_RECO_MP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RECO_MP Date
	 * de création : (04/07/11 16:01:48)
	 * 
	 */
	public String getVAL_ST_RECO_MP() {
		return getZone(getNOM_ST_RECO_MP());
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-VISITESMED";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (20/06/11 15:25:51)
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

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeVisites().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeVisites().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeVisites().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_INIT_INAP
			for (int i = 0; i < getListeVisites().size(); i++) {
				if (testerParametre(request, getNOM_PB_INIT_INAPT(i))) {
					return performPB_INIT_INAPT(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER_INAPTITUDE
			for (int i = 0; i < getListeInaptitude().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_INAPTITUDE(i))) {
					return performPB_CONSULTER_INAPTITUDE(request, i);
				}
			}

			// Si clic sur le bouton PB_SELECT_INAPTITUDE
			for (int i = 0; i < getListeInaptitude().size(); i++) {
				if (testerParametre(request, getNOM_PB_SELECT_INAPTITUDE(i))) {
					return performPB_SELECT_INAPTITUDE(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_CREER_INAPTITUDE())) {
				return performPB_CREER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_INAPTITUDE
			for (int i = 0; i < getListeInaptitude().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_INAPTITUDE(i))) {
					return performPB_MODIFIER_INAPTITUDE(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_INAPTITUDE
			for (int i = 0; i < getListeInaptitude().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_INAPTITUDE(i))) {
					return performPB_SUPPRIMER_INAPTITUDE(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeVisites().size(); i++) {
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

			// Si clic sur le bouton PB_SELECT_MOTIF
			if (testerParametre(request, getNOM_PB_SELECT_MOTIF())) {
				return performPB_SELECT_MOTIF(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 15:48:16)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTVisiteMed.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_VISITE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_VISITE(int i) {
		return "NOM_ST_DATE_VISITE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_VISITE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_VISITE(int i) {
		return getZone(getNOM_ST_DATE_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DUREE(int i) {
		return "NOM_ST_DUREE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DUREE(int i) {
		return getZone(getNOM_ST_DUREE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_MEDECIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_MEDECIN(int i) {
		return "NOM_ST_NOM_MEDECIN" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_MEDECIN
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_MEDECIN(int i) {
		return getZone(getNOM_ST_NOM_MEDECIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AVIS(int i) {
		return "NOM_ST_AVIS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AVIS(int i) {
		return getZone(getNOM_ST_AVIS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RECOMMANDATION Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_RECOMMANDATION(int i) {
		return "NOM_ST_RECOMMANDATION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RECOMMANDATION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_RECOMMANDATION(int i) {
		return getZone(getNOM_ST_RECOMMANDATION(i));
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

		// Récup de la visite médicale courante
		VisiteMedicale visiteCourante = (VisiteMedicale) getListeVisites().get(indiceEltAModifier);
		setVisiteCourante(visiteCourante);

		// init de la VM courante
		if (!initialiseVisiteCourante())
			return false;
		champMotifModifiable = true;
		elementModifibale = true;
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// CAS D'UNE MODIF SUR VM
		if (getVisiteCourante().getIdVisite() != null) {
			champMotifModifiable = false;
			if (getVisiteCourante().getIdMotif() != null
					&& (getVisiteCourante().getIdMotif().equals(EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode()) || getVisiteCourante().getIdMotif()
							.equals(EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode())) && getVisiteCourante().getIdRecommandation() == null
					&& getVisiteCourante().getApte() == null) {
				elementModifibale = false;
			} else {
				if (getVisiteCourante().getIdMotif() == null) {
					champMotifModifiable = true;
				}
				elementModifibale = true;
			}
		}

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
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

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la visite médicale courante
		VisiteMedicale visiteCourante = (VisiteMedicale) getListeVisites().get(indiceEltAConsulter);
		setVisiteCourante(visiteCourante);

		// init du diplome courant
		if (!initialiseVisiteSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la visite médicale courante
		VisiteMedicale visiteCourante = (VisiteMedicale) getListeVisites().get(indiceEltASuprimer);
		setVisiteCourante(visiteCourante);

		// init du diplome courant
		if (!initialiseVisiteSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_INIT_INAPT Date de création
	 * : (28/06/11 11:58:09)
	 * 
	 */
	public String getNOM_PB_INIT_INAPT(int i) {
		return "NOM_PB_INIT_INAPT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/06/11 11:58:09)
	 * 
	 */
	public boolean performPB_INIT_INAPT(HttpServletRequest request, int indiceEltSelectionne) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);

		// Récup de la visite médicale courante
		VisiteMedicale visiteCourante = (VisiteMedicale) getListeVisites().get(indiceEltSelectionne);
		setVisiteCourante(visiteCourante);
		if (visiteCourante.getApte() != null)
			if (visiteCourante.getApte().equals("0"))
				initialiseListeInpatitude(request);

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_INAPT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_INAPT(int i) {
		return "NOM_ST_TYPE_INAPT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_INAPT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_INAPT(int i) {
		return getZone(getNOM_ST_TYPE_INAPT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DEBUT_INAPT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEBUT_INAPT(int i) {
		return "NOM_ST_DEBUT_INAPT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DEBUT_INAPT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEBUT_INAPT(int i) {
		return getZone(getNOM_ST_DEBUT_INAPT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEES_INAPT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ANNEES_INAPT(int i) {
		return "NOM_ST_ANNEES_INAPT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEES_INAPT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ANNEES_INAPT(int i) {
		return getZone(getNOM_ST_ANNEES_INAPT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOIS_INAPT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MOIS_INAPT(int i) {
		return "NOM_ST_MOIS_INAPT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOIS_INAPT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MOIS_INAPT(int i) {
		return getZone(getNOM_ST_MOIS_INAPT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_JOURS_INAPT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_JOURS_INAPT(int i) {
		return "NOM_ST_JOURS_INAPT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_JOURS_INAPT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_JOURS_INAPT(int i) {
		return getZone(getNOM_ST_JOURS_INAPT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_INAPTITUDE Date de
	 * création : (29/06/11 09:16:38)
	 * 
	 */
	public String getNOM_PB_SELECT_INAPTITUDE(int i) {
		return "NOM_PB_SELECT_INAPTITUDE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/06/11 09:16:38)
	 * 
	 */
	public boolean performPB_SELECT_INAPTITUDE(HttpServletRequest request, int indiceEltSelectionne) throws Exception {
		if (!getNOM_ST_ACTION_INAPTITUDE().equals(ACTION_INAPTITUDE_CREATION))
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_INAPTITUDE Date de
	 * création : (27/06/11 14:51:36)
	 * 
	 */
	public String getNOM_PB_CREER_INAPTITUDE() {
		return "NOM_PB_CREER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/06/11 14:51:36)
	 * 
	 * RG_AG_VM_A02
	 */
	public boolean performPB_CREER_INAPTITUDE(HttpServletRequest request) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		if (getVisiteCourante() == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "visites médicales"));
			return false;
		}

		// RG_AG_VM_A02
		VisiteMedicale dernVisite = (VisiteMedicale) getListeVisites().get(0);
		if (!dernVisite.getIdVisite().equals(getVisiteCourante().getIdVisite())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR040"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_INAPTITUDE_CREATION);

		// init de l'inpatitude courante
		setInaptitudeCourante(new Inaptitude());
		videZonesDeSaisieInaptitude(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_INAPTITUDE Date de
	 * création : (27/06/11 14:51:36)
	 * 
	 */
	public String getNOM_PB_MODIFIER_INAPTITUDE(int i) {
		return "NOM_PB_MODIFIER_INAPTITUDE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/06/11 14:51:36)
	 * 
	 */
	public boolean performPB_MODIFIER_INAPTITUDE(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);

		if (getVisiteCourante() == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "visites médicales"));
			return false;
		}

		// Récup de la visite médicale courante
		setInaptitudeCourante((Inaptitude) getListeInaptitude().get(indiceEltAModifier));

		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_INAPTITUDE_MODIFICATION);

		//
		initialiseInaptitudeCourante(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_INAPTITUDE Date de
	 * création : (27/06/11 14:51:36)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_INAPTITUDE(int i) {
		return "NOM_PB_SUPPRIMER_INAPTITUDE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/06/11 14:51:36)
	 * 
	 */
	public boolean performPB_SUPPRIMER_INAPTITUDE(HttpServletRequest request, int indiceEltASupprimer) throws Exception {

		addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);

		if (getVisiteCourante() == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "visites médicales"));
			return false;
		}

		// Récup de la visite médicale courante
		setInaptitudeCourante((Inaptitude) getListeInaptitude().get(indiceEltASupprimer));

		// init du diplome courant
		initialiseInaptitudeSuppression(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_INAPTITUDE_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_INAPTITUDE Date de
	 * création : (17/10/11 15:48:16)
	 * 
	 */
	public String getNOM_PB_CONSULTER_INAPTITUDE(int i) {
		return "NOM_PB_CONSULTER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 15:48:16)
	 * 
	 */
	public boolean performPB_CONSULTER_INAPTITUDE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		if (getVisiteCourante() == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "visites médicales"));
			return false;
		}

		// Récup de la visite médicale courante
		setInaptitudeCourante((Inaptitude) getListeInaptitude().get(indiceEltAConsulter));

		// init du diplome courant
		initialiseInaptitudeSuppression(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_INAPTITUDE_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DOCUMENT Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la visite médicale courante
		VisiteMedicale visiteCourante = (VisiteMedicale) getListeVisites().get(indiceEltDocument);
		setVisiteCourante(visiteCourante);

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
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeDocuments
	 *            ArrayList
	 */
	private void setListeDocuments(ArrayList newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des contacts
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de l'agent
		ArrayList listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(), getAgentCourant(), "HSCT", "VM",
				getVisiteCourante().getIdVisite());
		setListeDocuments(listeDocAgent);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) TypeDocument.chercherTypeDocument(getTransaction(), doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument()
						);
				addZone(getNOM_ST_TYPE_DOC(indiceActeVM), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());

				indiceActeVM++;
			}
		}
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
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
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
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

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

		// Récup du document courant
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
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
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

		// Récup du Diplome courant
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
	 * Met à jour le doc en cours.
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
	 * Met à jour le doc en cours.
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

		// tout s'est bien passé
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

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
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
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Contrôle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		VisiteMedicale vm = getVisiteCourante();

		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {
			String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();

			// on controle si il y a dejà un fichier pour cette VM
			if (!performControlerFichier(request, "VM_" + vm.getIdVisite() + "_" + dateJour)) {
				// alors on affiche un message pour prevenir que l'on va ecraser
				// le fichier precedent
				addZone(getNOM_ST_WARNING(),
						"Attention un fichier du même type existe déjà pour cette visite médicale. Etes-vous sûr de vouloir écraser la version précédente ?");
				return true;
			}

			if (!creeDocument(request, vm)) {
				return false;
			}

		} else {
			// on supprime le document existant dans la base de données
			Document d = Document.chercherDocumentByContainsNom(getTransaction(), "VM_" + vm.getIdVisite());
			LienDocumentAgent l = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant().getIdAgent(), d.getIdDocument());
			File f = new File(d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			l.supprimerLienDocumentAgent(getTransaction());
			d.supprimerDocument(getTransaction());

			if (!creeDocument(request, vm)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met à jour le tableau des VM pour avoir le nombre de documents
		initialiseListeVisiteMed(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, VisiteMedicale vm) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupère le type de document
		String codTypeDoc = "VM";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + vm.getIdVisite() + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
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

		setLienDocumentAgentCourant(new LienDocumentAgent());
		getLienDocumentAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocumentAgentCourant().setIdDocument(getDocumentCourant().getIdDocument());
		getLienDocumentAgentCourant().creerLienDocumentAgent(getTransaction());

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

	private boolean performControlerFichier(HttpServletRequest request, String nomFichier) {
		boolean result = true;
		// on regarde dans la liste des document si il y a une entrée avec ce
		// nom de contrat
		for (Iterator iter = getListeDocuments().iterator(); iter.hasNext();) {
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
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
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_MOTIF Date de
	 * création : (26/09/11 09:04:08)
	 * 
	 */
	public String getNOM_PB_SELECT_MOTIF() {
		return "NOM_PB_SELECT_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un MOTIF en fonction de ces
	 * règles : setMOTIF(MOTIF, boolean veutRetour) ou setMOTIF(MOTIF,Message
	 * d'erreur) Date de création : (26/09/11 09:04:08)
	 * 
	 */
	public boolean performPB_SELECT_MOTIF(HttpServletRequest request) throws Exception {
		if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {

			int numMotif = (Services.estNumerique(getZone(getNOM_LB_MOTIF_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_MOTIF_SELECT())) : -1);
			if (numMotif <= 0 || getListeMotif().size() == 0 || numMotif > getListeMotif().size())
				return false;
			MotifVisiteMed motif = (MotifVisiteMed) getListeMotif().get(numMotif - 1);
			// RG-SVM-19
			if (motif.getLibMotifVM().equals(EnumMotifVisiteMed.VM_DEMANDE_AGENT.getValue())
					|| motif.getLibMotifVM().equals(EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getValue())) {
				elementModifibale = false;

				addZone(getNOM_LB_RECOMMANDATION_SELECT(), Const.ZERO);
				addZone(getNOM_EF_DATE_VISITE(), Services.dateDuJour());
				addZone(getNOM_EF_DUREE(), Const.ZERO);
				Medecin medecin = Medecin.chercherMedecinByLib(getTransaction(), Const.CHAINE_VIDE, "A", "RENSEIGNER");
				int ligneMedecin = getListeMedecin().indexOf(getHashMedecin().get(medecin.getIdMedecin()));
				addZone(getNOM_LB_MEDECIN_SELECT(), String.valueOf(ligneMedecin + 1));
				addZone(getNOM_RG_AVIS(), Const.CHAINE_VIDE);
			} else {
				// si autre motif alors on cherche la derniere convocation
				// si elle est de type 'non-effectuée' alors on en peut pas
				// créer de VM
				try {
					SuiviMedical smInterdit = getSuiviMedDao().chercherDernierSuiviMedicalAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
					if (!smInterdit.getEtat().equals(EnumEtatSuiviMed.EFFECTUE.getCode())) {
						// "ERR091",
						// "Une convocation est en attente, vous ne pouvez pas créer de visite médicale avec ce motif.");
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR091"));
						return false;
					}
				} catch (Exception e) {
					// on laisse la création
					logger.error("Erreur : " + e.getMessage());
				}
			}
		}

		return true;
	}
}
