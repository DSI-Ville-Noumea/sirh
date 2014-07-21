package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.hsct.Inaptitude;
import nc.mairie.metier.hsct.MaladiePro;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.hsct.SiegeLesion;
import nc.mairie.metier.hsct.TypeAT;
import nc.mairie.metier.hsct.TypeInaptitude;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.hsct.AccidentTravailDao;
import nc.mairie.spring.dao.metier.hsct.HandicapDao;
import nc.mairie.spring.dao.metier.hsct.MaladieProDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.RecommandationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGEHSCT Date de création : (15/09/11 08:57:49)
 * 
 */
public class OePARAMETRAGEHSCT extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_AT;
	private String[] LB_INAPTITUDE;
	private String[] LB_LESION;
	private String[] LB_MALADIE;
	private String[] LB_MEDECIN;
	private String[] LB_RECOMMANDATION;
	private String[] LB_TYPE_DOCUMENT;

	private ArrayList<Medecin> listeMedecin;
	private Medecin medecinCourant;

	private ArrayList<Recommandation> listeRecommandation;
	private Recommandation recommandationCourante;

	private ArrayList<TypeInaptitude> listeInaptitude;
	private TypeInaptitude inaptitudeCourante;

	private ArrayList<TypeAT> listeAT;
	private TypeAT atCourant;

	private ArrayList<SiegeLesion> listeLesion;
	private SiegeLesion lesionCourant;

	private ArrayList<MaladiePro> listeMaladie;
	private MaladiePro maladieCourante;

	private ArrayList<TypeDocument> listeTypeDocument;
	private TypeDocument typeDocumentCourant;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	private TypeDocumentDao typeDocumentDao;
	private AccidentTravailDao accidentTravailDao;
	private HandicapDao handicapDao;
	private MaladieProDao maladieProDao;
	private MedecinDao medecinDao;
	private RecommandationDao recommandationDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (15/09/11 08:57:49)
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

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//

		if (getListeMedecin() == null) {
			// Recherche des médecins
			ArrayList<Medecin> listeMedecin = getMedecinDao().listerMedecin();
			setListeMedecin(listeMedecin);
			initialiseListeMedecin(request);
		}

		if (getListeRecommandation() == null) {
			// Recherche des recommandations
			ArrayList<Recommandation> listeRecommandation = getRecommandationDao().listerRecommandation();
			setListeRecommandation(listeRecommandation);
			initialiseListeRecommandation(request);
		}

		if (getListeInaptitude() == null) {
			// Recherche des types d'inaptitude
			ArrayList<TypeInaptitude> listeInaptitude = TypeInaptitude.listerTypeInaptitude(getTransaction());
			setListeInaptitude(listeInaptitude);
			initialiseListeInaptitude(request);
		}

		if (getListeAT() == null) {
			// Recherche des types d'AT
			ArrayList<TypeAT> listeAT = TypeAT.listerTypeAT(getTransaction());
			setListeAT(listeAT);
			initialiseListeAT(request);
		}

		if (getListeLesion() == null) {
			// Recherche des des sièges de lésions
			ArrayList<SiegeLesion> listeLesion = SiegeLesion.listerSiegeLesion(getTransaction());
			setListeLesion(listeLesion);
			initialiseListeLesion(request);
		}

		if (getListeMaladie() == null) {
			// Recherche des des maladies professionnelles
			ArrayList<MaladiePro> listeMaladie = getMaladieProDao().listerMaladiePro();
			setListeMaladie(listeMaladie);
			initialiseListeMaladie(request);
		}

		if (getListeTypeDocument() == null) {
			// Recherche des types de documents
			ArrayList<TypeDocument> listeTypeDocument = getTypeDocumentDao().listerTypeDocumentAvecModule("HSCT");
			setListeTypeDocument(listeTypeDocument);
			initialiseListeTypeDocument(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAccidentTravailDao() == null) {
			setAccidentTravailDao(new AccidentTravailDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHandicapDao() == null) {
			setHandicapDao(new HandicapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMaladieProDao() == null) {
			setMaladieProDao(new MaladieProDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRecommandationDao() == null) {
			setRecommandationDao(new RecommandationDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation de la listes des médecins Date de création : (15/09/11)
	 * 
	 */
	private void initialiseListeMedecin(HttpServletRequest request) throws Exception {
		setListeMedecin(getMedecinDao().listerMedecin());
		if (getListeMedecin().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Medecin> list = getListeMedecin().listIterator(); list.hasNext();) {
				Medecin m = (Medecin) list.next();
				String ligne[] = { m.getTitreMedecin() + " " + m.getPrenomMedecin() + " " + m.getNomMedecin() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MEDECIN(aFormat.getListeFormatee());
		} else {
			setLB_MEDECIN(null);
		}
	}

	/**
	 * Initialisation de la listes des recommandations Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeRecommandation(HttpServletRequest request) throws Exception {
		setListeRecommandation(getRecommandationDao().listerRecommandation());
		if (getListeRecommandation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Recommandation> list = getListeRecommandation().listIterator(); list.hasNext();) {
				Recommandation r = (Recommandation) list.next();
				String ligne[] = { r.getDescRecommandation() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_RECOMMANDATION(aFormat.getListeFormatee());
		} else {
			setLB_RECOMMANDATION(null);
		}
	}

	/**
	 * Initialisation de la listes des types d'inaptitude Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeInaptitude(HttpServletRequest request) throws Exception {
		setListeInaptitude(TypeInaptitude.listerTypeInaptitude(getTransaction()));
		if (getListeInaptitude().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeInaptitude> list = getListeInaptitude().listIterator(); list.hasNext();) {
				TypeInaptitude ti = (TypeInaptitude) list.next();
				String ligne[] = { ti.getDescTypeInaptitude() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_INAPTITUDE(aFormat.getListeFormatee());
		} else {
			setLB_INAPTITUDE(null);
		}
	}

	/**
	 * Initialisation de la listes des types d'AT Date de création : (15/09/11)
	 * 
	 */
	private void initialiseListeAT(HttpServletRequest request) throws Exception {
		setListeAT(TypeAT.listerTypeAT(getTransaction()));
		if (getListeAT().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeAT> list = getListeAT().listIterator(); list.hasNext();) {
				TypeAT td = (TypeAT) list.next();
				String ligne[] = { td.getDescTypeAT() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_AT(aFormat.getListeFormatee());
		} else {
			setLB_AT(null);
		}
	}

	/**
	 * Initialisation de la listes des sièges de lésions Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeLesion(HttpServletRequest request) throws Exception {
		setListeLesion(SiegeLesion.listerSiegeLesion(getTransaction()));
		if (getListeLesion().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<SiegeLesion> list = getListeLesion().listIterator(); list.hasNext();) {
				SiegeLesion sl = (SiegeLesion) list.next();
				String ligne[] = { sl.getDescSiege() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_LESION(aFormat.getListeFormatee());
		} else {
			setLB_LESION(null);
		}
	}

	/**
	 * Initialisation de la listes des maladies professionnelles Date de
	 * création : (15/09/11)
	 * 
	 */
	private void initialiseListeMaladie(HttpServletRequest request) throws Exception {
		setListeMaladie(getMaladieProDao().listerMaladiePro());
		if (getListeMaladie().size() != 0) {
			int tailles[] = { 20, 50 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MaladiePro> list = getListeMaladie().listIterator(); list.hasNext();) {
				MaladiePro mp = (MaladiePro) list.next();
				String ligne[] = { mp.getCodeMaladiePro(), mp.getLibMaladiePro() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MALADIE(aFormat.getListeFormatee());
		} else {
			setLB_MALADIE(null);
		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_AT
			if (testerParametre(request, getNOM_PB_ANNULER_AT())) {
				return performPB_ANNULER_AT(request);
			}

			// Si clic sur le bouton PB_ANNULER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_ANNULER_INAPTITUDE())) {
				return performPB_ANNULER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_ANNULER_LESION
			if (testerParametre(request, getNOM_PB_ANNULER_LESION())) {
				return performPB_ANNULER_LESION(request);
			}

			// Si clic sur le bouton PB_ANNULER_MALADIE
			if (testerParametre(request, getNOM_PB_ANNULER_MALADIE())) {
				return performPB_ANNULER_MALADIE(request);
			}

			// Si clic sur le bouton PB_ANNULER_MEDECIN
			if (testerParametre(request, getNOM_PB_ANNULER_MEDECIN())) {
				return performPB_ANNULER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_ANNULER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_ANNULER_RECOMMANDATION())) {
				return performPB_ANNULER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_CREER_AT
			if (testerParametre(request, getNOM_PB_CREER_AT())) {
				return performPB_CREER_AT(request);
			}

			// Si clic sur le bouton PB_CREER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_CREER_INAPTITUDE())) {
				return performPB_CREER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_CREER_LESION
			if (testerParametre(request, getNOM_PB_CREER_LESION())) {
				return performPB_CREER_LESION(request);
			}

			// Si clic sur le bouton PB_CREER_MALADIE
			if (testerParametre(request, getNOM_PB_CREER_MALADIE())) {
				return performPB_CREER_MALADIE(request);
			}

			// Si clic sur le bouton PB_CREER_MEDECIN
			if (testerParametre(request, getNOM_PB_CREER_MEDECIN())) {
				return performPB_CREER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_CREER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_CREER_RECOMMANDATION())) {
				return performPB_CREER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_AT())) {
				return performPB_SUPPRIMER_AT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_INAPTITUDE())) {
				return performPB_SUPPRIMER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_LESION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_LESION())) {
				return performPB_SUPPRIMER_LESION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MALADIE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MALADIE())) {
				return performPB_SUPPRIMER_MALADIE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MEDECIN
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MEDECIN())) {
				return performPB_SUPPRIMER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECOMMANDATION())) {
				return performPB_SUPPRIMER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_AT
			if (testerParametre(request, getNOM_PB_VALIDER_AT())) {
				return performPB_VALIDER_AT(request);
			}

			// Si clic sur le bouton PB_VALIDER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_VALIDER_INAPTITUDE())) {
				return performPB_VALIDER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_VALIDER_LESION
			if (testerParametre(request, getNOM_PB_VALIDER_LESION())) {
				return performPB_VALIDER_LESION(request);
			}

			// Si clic sur le bouton PB_VALIDER_MALADIE
			if (testerParametre(request, getNOM_PB_VALIDER_MALADIE())) {
				return performPB_VALIDER_MALADIE(request);
			}

			// Si clic sur le bouton PB_VALIDER_MEDECIN
			if (testerParametre(request, getNOM_PB_VALIDER_MEDECIN())) {
				return performPB_VALIDER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_VALIDER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_VALIDER_RECOMMANDATION())) {
				return performPB_VALIDER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_DOCUMENT())) {
				return performPB_VALIDER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_DOCUMENT())) {
				return performPB_ANNULER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_CREER_TYPE_DOCUMENT())) {
				return performPB_CREER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_DOCUMENT())) {
				return performPB_SUPPRIMER_TYPE_DOCUMENT(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePARAMETRAGEHSCT. Date de création : (15/09/11
	 * 08:57:49)
	 * 
	 */
	public OePARAMETRAGEHSCT() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEHSCT.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-AG-HSCT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_AT Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_AT() {
		return "NOM_PB_ANNULER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_AT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_AT(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_INAPTITUDE() {
		return "NOM_PB_ANNULER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_INAPTITUDE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_LESION() {
		return "NOM_PB_ANNULER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_LESION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_LESION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_MALADIE() {
		return "NOM_PB_ANNULER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_MALADIE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MALADIE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_MEDECIN() {
		return "NOM_PB_ANNULER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_MEDECIN(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MEDECIN(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_RECOMMANDATION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_RECOMMANDATION() {
		return "NOM_PB_ANNULER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_RECOMMANDATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_AT() {
		return "NOM_PB_CREER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_AT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_AT(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_AT(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_INAPTITUDE() {
		return "NOM_PB_CREER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_INAPTITUDE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_INAPTITUDE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_LESION() {
		return "NOM_PB_CREER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_LESION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_LESION(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_LESION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_MALADIE() {
		return "NOM_PB_CREER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_MALADIE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MALADIE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_MALADIE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIBELLE_MALADIE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_MEDECIN() {
		return "NOM_PB_CREER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_MEDECIN(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MEDECIN(), ACTION_CREATION);
		addZone(getNOM_EF_NOM_MEDECIN(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_RECOMMANDATION() {
		return "NOM_PB_CREER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_RECOMMANDATION(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_RECOMMANDATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AT() {
		return "NOM_PB_SUPPRIMER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AT(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_AT_SELECT()) ? Integer.parseInt(getVAL_LB_AT_SELECT()) : -1);

		if (indice != -1 && indice < getListeAT().size()) {
			TypeAT at = getListeAT().get(indice);
			setAtCourant(at);
			addZone(getNOM_EF_DESC_AT(), at.getDescTypeAT());
			addZone(getNOM_ST_ACTION_AT(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'AT"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_INAPTITUDE() {
		return "NOM_PB_SUPPRIMER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_INAPTITUDE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_INAPTITUDE_SELECT()) ? Integer
				.parseInt(getVAL_LB_INAPTITUDE_SELECT()) : -1);

		if (indice != -1 && indice < getListeInaptitude().size()) {
			TypeInaptitude ti = getListeInaptitude().get(indice);
			setInaptitudeCourante(ti);
			addZone(getNOM_EF_DESC_INAPTITUDE(), ti.getDescTypeInaptitude());
			addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'inaptitude"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_LESION() {
		return "NOM_PB_SUPPRIMER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_LESION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_LESION_SELECT()) ? Integer.parseInt(getVAL_LB_LESION_SELECT())
				: -1);

		if (indice != -1 && indice < getListeLesion().size()) {
			SiegeLesion sl = getListeLesion().get(indice);
			setLesionCourant(sl);
			addZone(getNOM_EF_DESC_LESION(), sl.getDescSiege());
			addZone(getNOM_ST_ACTION_LESION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "sièges de lésion"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MALADIE() {
		return "NOM_PB_SUPPRIMER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MALADIE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MALADIE_SELECT()) ? Integer.parseInt(getVAL_LB_MALADIE_SELECT())
				: -1);

		if (indice != -1 && indice < getListeMaladie().size()) {
			MaladiePro mp = getListeMaladie().get(indice);
			setMaladieCourante(mp);
			addZone(getNOM_EF_CODE_MALADIE(), mp.getCodeMaladiePro());
			addZone(getNOM_EF_LIBELLE_MALADIE(), mp.getLibMaladiePro());
			addZone(getNOM_ST_ACTION_MALADIE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "maladie professionelles"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MEDECIN() {
		return "NOM_PB_SUPPRIMER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MEDECIN(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MEDECIN_SELECT()) ? Integer.parseInt(getVAL_LB_MEDECIN_SELECT())
				: -1);

		if (indice != -1 && indice < getListeMedecin().size()) {
			Medecin m = getListeMedecin().get(indice);
			setMedecinCourant(m);
			addZone(getNOM_EF_NOM_MEDECIN(), m.getNomMedecin());
			addZone(getNOM_EF_PRENOM_MEDECIN(), m.getPrenomMedecin());
			addZone(getNOM_EF_TITRE_MEDECIN(), m.getTitreMedecin());
			addZone(getNOM_ST_ACTION_MEDECIN(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "médecins"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECOMMANDATION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECOMMANDATION() {
		return "NOM_PB_SUPPRIMER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_RECOMMANDATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_RECOMMANDATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeRecommandation().size()) {
			Recommandation r = getListeRecommandation().get(indice);
			setRecommandationCourante(r);
			addZone(getNOM_EF_DESC_RECOMMANDATION(), r.getDescRecommandation());
			addZone(getNOM_ST_ACTION_RECOMMANDATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "recommandations"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AT Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_AT() {
		return "NOM_PB_VALIDER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_AT(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieAT(request))
			return false;

		if (!performControlerRegleGestionAT(request))
			return false;

		if (getVAL_ST_ACTION_AT() != null && getVAL_ST_ACTION_AT() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_AT().equals(ACTION_CREATION)) {
				setAtCourant(new TypeAT());
				getAtCourant().setDescTypeAT(getVAL_EF_DESC_AT());
				getAtCourant().creerTypeAT(getTransaction());
				if (!getTransaction().isErreur())
					getListeAT().add(getAtCourant());
			} else if (getVAL_ST_ACTION_AT().equals(ACTION_SUPPRESSION)) {
				getAtCourant().supprimerTypeAT(getTransaction());
				if (!getTransaction().isErreur())
					getListeAT().remove(getAtCourant());
				setAtCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeAT(request);
			addZone(getNOM_ST_ACTION_AT(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type d'AT Date de création : (15/09/11)
	 */
	private boolean performControlerSaisieAT(HttpServletRequest request) throws Exception {

		// Verification description type d'AT not null
		if (getZone(getNOM_EF_DESC_AT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type d'AT Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionAT(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type d'AT utilisé sur un accident du
		// travail
		if (getVAL_ST_ACTION_AT().equals(ACTION_SUPPRESSION)
				&& getAccidentTravailDao().listerAccidentTravailAvecTypeAT(
						Integer.valueOf(getAtCourant().getIdTypeAT())).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction()
					.declarerErreur(MessageUtils.getMessage("ERR989", "un accident du travail", "ce type d'AT"));
			return false;
		}

		// Vérification des contraintes d'unicité du type d'AT
		if (getVAL_ST_ACTION_AT().equals(ACTION_CREATION)) {

			for (TypeAT at : getListeAT()) {
				if (at.getDescTypeAT().equals(getVAL_EF_DESC_AT().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type d'AT", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_INAPTITUDE() {
		return "NOM_PB_VALIDER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_INAPTITUDE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieInaptitude(request))
			return false;

		if (!performControlerRegleGestionInaptitude(request))
			return false;

		if (getVAL_ST_ACTION_INAPTITUDE() != null && getVAL_ST_ACTION_INAPTITUDE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_CREATION)) {
				setInaptitudeCourante(new TypeInaptitude());
				getInaptitudeCourante().setDescTypeInaptitude(getVAL_EF_DESC_INAPTITUDE());
				getInaptitudeCourante().creerTypeInaptitude(getTransaction());
				if (!getTransaction().isErreur())
					getListeInaptitude().add(getInaptitudeCourante());
			} else if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_SUPPRESSION)) {
				getInaptitudeCourante().supprimerTypeInaptitude(getTransaction());
				if (!getTransaction().isErreur())
					getListeInaptitude().remove(getInaptitudeCourante());
				setInaptitudeCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeInaptitude(request);
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type d'inaptitude Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieInaptitude(HttpServletRequest request) throws Exception {

		// Verification description type d'inaptitude not null
		if (getZone(getNOM_EF_DESC_INAPTITUDE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type d'inaptitude Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionInaptitude(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type d'inaptitude utilisé sur une
		// inaptitude
		if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_SUPPRESSION)
				&& Inaptitude.listerInaptitudeAvecTypeInaptitude(getTransaction(), getInaptitudeCourante()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "inaptitude", "ce type d'inaptitude"));
			return false;
		}

		// Vérification des contraintes d'unicité du type d'inaptitude
		if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_CREATION)) {

			for (TypeInaptitude titre : getListeInaptitude()) {
				if (titre.getDescTypeInaptitude().equals(getVAL_EF_DESC_INAPTITUDE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type d'inaptitude", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_LESION() {
		return "NOM_PB_VALIDER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_LESION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieLesion(request))
			return false;

		if (!performControlerRegleGestionLesion(request))
			return false;

		if (getVAL_ST_ACTION_LESION() != null && getVAL_ST_ACTION_LESION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_LESION().equals(ACTION_CREATION)) {
				setLesionCourant(new SiegeLesion());
				getLesionCourant().setDescSiege(getVAL_EF_DESC_LESION());
				getLesionCourant().creerSiegeLesion(getTransaction());
				if (!getTransaction().isErreur())
					getListeLesion().add(getLesionCourant());
			} else if (getVAL_ST_ACTION_LESION().equals(ACTION_SUPPRESSION)) {
				getLesionCourant().supprimerSiegeLesion(getTransaction());
				if (!getTransaction().isErreur())
					getListeLesion().remove(getLesionCourant());
				setLesionCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeLesion(request);
			addZone(getNOM_ST_ACTION_LESION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un siège de lésion Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieLesion(HttpServletRequest request) throws Exception {

		// Verification desription siege de lesion not null
		if (getZone(getNOM_EF_DESC_LESION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un siège de lésion Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionLesion(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un siège de lésion utilisé sur un
		// accident du travail
		if (getVAL_ST_ACTION_LESION().equals(ACTION_SUPPRESSION)
				&& getAccidentTravailDao().listerAccidentTravailAvecSiegeLesion(
						Integer.valueOf(getLesionCourant().getIdSiege())).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "accident du travail", "ce siège de lésion"));
			return false;
		}

		// Vérification des contraintes d'unicité du siège de lésion
		if (getVAL_ST_ACTION_LESION().equals(ACTION_CREATION)) {

			for (SiegeLesion siege : getListeLesion()) {
				if (siege.getDescSiege().equals(getVAL_EF_DESC_LESION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un siège de lésion", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_MALADIE() {
		return "NOM_PB_VALIDER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_MALADIE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMaladie(request))
			return false;

		if (!performControlerRegleGestionMaladie(request))
			return false;

		if (getVAL_ST_ACTION_MALADIE() != null && getVAL_ST_ACTION_MALADIE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MALADIE().equals(ACTION_CREATION)) {
				setMaladieCourante(new MaladiePro());
				getMaladieCourante().setCodeMaladiePro(getVAL_EF_CODE_MALADIE());
				getMaladieCourante().setLibMaladiePro(getVAL_EF_LIBELLE_MALADIE());
				getMaladieProDao().creerMaladiePro(getMaladieCourante().getCodeMaladiePro(),
						getMaladieCourante().getLibMaladiePro());
				if (!getTransaction().isErreur())
					getListeMaladie().add(getMaladieCourante());
			} else if (getVAL_ST_ACTION_MALADIE().equals(ACTION_SUPPRESSION)) {
				getMaladieProDao().supprimerMaladiePro(getMaladieCourante().getIdMaladiePro());
				if (!getTransaction().isErreur())
					getListeMaladie().remove(getMaladieCourante());
				setMaladieCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMaladie(request);
			addZone(getNOM_ST_ACTION_MALADIE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une maladie professionnelle Date de création
	 * : (15/09/11)
	 */
	private boolean performControlerSaisieMaladie(HttpServletRequest request) throws Exception {

		// Verification libellé maladie not null
		if (getZone(getNOM_EF_LIBELLE_MALADIE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code maladie not null
		if (getZone(getNOM_EF_CODE_MALADIE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une maladie professionnelle Date de
	 * création : (15/09/11)
	 */
	private boolean performControlerRegleGestionMaladie(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une maladie professionnelle utilisé sur
		// un handicap
		if (getVAL_ST_ACTION_MALADIE().equals(ACTION_SUPPRESSION)
				&& getHandicapDao().listerHandicapAvecMaladiePro(
						Integer.valueOf(getMaladieCourante().getIdMaladiePro())).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "handicap", "cette maladie professionnelle"));
			return false;
		}

		// Vérification des contraintes d'unicité de la maladie professionnelle
		if (getVAL_ST_ACTION_MALADIE().equals(ACTION_CREATION)) {

			for (MaladiePro maladie : getListeMaladie()) {
				if (maladie.getCodeMaladiePro().equals(getVAL_EF_CODE_MALADIE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une maladie professionnelle", "ce code"));
					return false;
				}

				if (maladie.getLibMaladiePro().equals(getVAL_EF_LIBELLE_MALADIE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une maladie professionnelle", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_MEDECIN() {
		return "NOM_PB_VALIDER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_MEDECIN(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMedecin(request))
			return false;

		if (!performControlerRegleGestionMedecin(request))
			return false;

		if (getVAL_ST_ACTION_MEDECIN() != null && getVAL_ST_ACTION_MEDECIN() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_CREATION)) {
				setMedecinCourant(new Medecin());
				getMedecinCourant().setNomMedecin(getVAL_EF_NOM_MEDECIN());
				getMedecinCourant().setPrenomMedecin(getVAL_EF_PRENOM_MEDECIN());
				getMedecinCourant().setTitreMedecin(getVAL_EF_TITRE_MEDECIN());
				getMedecinDao().creerMedecin(getMedecinCourant().getTitreMedecin(),
						getMedecinCourant().getPrenomMedecin(), getMedecinCourant().getNomMedecin());
				if (!getTransaction().isErreur())
					getListeMedecin().add(getMedecinCourant());
			} else if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_SUPPRESSION)) {
				getMedecinDao().supprimerMedecin(getMedecinCourant().getIdMedecin());
				if (!getTransaction().isErreur())
					getListeMedecin().remove(getMedecinCourant());
				setMedecinCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMedecin(request);
			addZone(getNOM_ST_ACTION_MEDECIN(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un medecin Date de création : (15/09/11)
	 */
	private boolean performControlerSaisieMedecin(HttpServletRequest request) throws Exception {

		// Verification nom medecin not null
		if (getZone(getNOM_EF_NOM_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom"));
			return false;
		}

		// Verification prenom medecin not null
		if (getZone(getNOM_EF_PRENOM_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "prénom"));
			return false;
		}

		// Verification titre medecin not null
		if (getZone(getNOM_EF_TITRE_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "titre"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un medecin Date de création : (15/09/11)
	 */
	private boolean performControlerRegleGestionMedecin(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un medecin utilisé sur une visite
		// médicale
		if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_SUPPRESSION)
				&& VisiteMedicale.listerVisiteMedicaleAvecMedecin(getTransaction(), getMedecinCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une visite médicale", "ce médecin"));
			return false;
		}

		// Vérification des contraintes d'unicité du medecin
		if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_CREATION)) {

			for (Medecin medecin : getListeMedecin()) {
				if (medecin.getNomMedecin().equals(getVAL_EF_NOM_MEDECIN().toUpperCase())
						&& medecin.getPrenomMedecin().equals(getVAL_EF_PRENOM_MEDECIN().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un médecin", "ce nom et ce prénom"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_RECOMMANDATION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_RECOMMANDATION() {
		return "NOM_PB_VALIDER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieRecommandation(request))
			return false;

		if (!performControlerRegleGestionRecommandation(request))
			return false;

		if (getVAL_ST_ACTION_RECOMMANDATION() != null && getVAL_ST_ACTION_RECOMMANDATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_CREATION)) {
				setRecommandationCourante(new Recommandation());
				getRecommandationCourante().setDescRecommandation(getVAL_EF_DESC_RECOMMANDATION());
				getRecommandationDao().creerRecommandation(getRecommandationCourante().getDescRecommandation());
				if (!getTransaction().isErreur())
					getListeRecommandation().add(getRecommandationCourante());
			} else if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_SUPPRESSION)) {
				getRecommandationDao().supprimerRecommandation(getRecommandationCourante().getIdRecommandation());
				if (!getTransaction().isErreur())
					getListeRecommandation().remove(getRecommandationCourante());
				setRecommandationCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeRecommandation(request);
			addZone(getNOM_ST_ACTION_RECOMMANDATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une recommandation Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieRecommandation(HttpServletRequest request) throws Exception {

		// Verification description recomandation not null
		if (getZone(getNOM_EF_DESC_RECOMMANDATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une recommandation Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionRecommandation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une recommandation utilisé sur une
		// visite médicale
		if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_SUPPRESSION)
				&& VisiteMedicale.listerVisiteMedicaleAvecRecommandation(getTransaction(), getRecommandationCourante())
						.size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "une visite médicale", "cette recommandation"));
			return false;
		}

		// Vérification des contraintes d'unicité de la recommandation
		if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_CREATION)) {

			for (Recommandation rec : getListeRecommandation()) {
				if (rec.getDescRecommandation().equals(getVAL_EF_DESC_RECOMMANDATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une recommandation", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_AT() {
		return "NOM_ST_ACTION_AT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_AT Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_AT() {
		return getZone(getNOM_ST_ACTION_AT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_INAPTITUDE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_INAPTITUDE() {
		return "NOM_ST_ACTION_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_INAPTITUDE() {
		return getZone(getNOM_ST_ACTION_INAPTITUDE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_LESION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_LESION() {
		return "NOM_ST_ACTION_LESION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_LESION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_LESION() {
		return getZone(getNOM_ST_ACTION_LESION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MALADIE Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_MALADIE() {
		return "NOM_ST_ACTION_MALADIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_MALADIE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_MALADIE() {
		return getZone(getNOM_ST_ACTION_MALADIE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_MEDECIN() {
		return "NOM_ST_ACTION_MEDECIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_MEDECIN
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_MEDECIN() {
		return getZone(getNOM_ST_ACTION_MEDECIN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_RECOMMANDATION() {
		return "NOM_ST_ACTION_RECOMMANDATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_RECOMMANDATION() {
		return getZone(getNOM_ST_ACTION_RECOMMANDATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_MALADIE Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_CODE_MALADIE() {
		return "NOM_EF_CODE_MALADIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_CODE_MALADIE() {
		return getZone(getNOM_EF_CODE_MALADIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_AT() {
		return "NOM_EF_DESC_AT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_AT() {
		return getZone(getNOM_EF_DESC_AT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_INAPTITUDE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_INAPTITUDE() {
		return "NOM_EF_DESC_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_INAPTITUDE() {
		return getZone(getNOM_EF_DESC_INAPTITUDE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_LESION() {
		return "NOM_EF_DESC_LESION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_LESION() {
		return getZone(getNOM_EF_DESC_LESION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_RECOMMANDATION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_RECOMMANDATION() {
		return "NOM_EF_DESC_RECOMMANDATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_RECOMMANDATION() {
		return getZone(getNOM_EF_DESC_RECOMMANDATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIBELLE_MALADIE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_LIBELLE_MALADIE() {
		return "NOM_EF_LIBELLE_MALADIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_LIBELLE_MALADIE() {
		return getZone(getNOM_EF_LIBELLE_MALADIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_NOM_MEDECIN() {
		return "NOM_EF_NOM_MEDECIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_NOM_MEDECIN() {
		return getZone(getNOM_EF_NOM_MEDECIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_PRENOM_MEDECIN() {
		return "NOM_EF_PRENOM_MEDECIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_PRENOM_MEDECIN() {
		return getZone(getNOM_EF_PRENOM_MEDECIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_TITRE_MEDECIN() {
		return "NOM_EF_TITRE_MEDECIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_TITRE_MEDECIN() {
		return getZone(getNOM_EF_TITRE_MEDECIN());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_AT() {
		if (LB_AT == null)
			LB_AT = initialiseLazyLB();
		return LB_AT;
	}

	/**
	 * Setter de la liste: LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_AT(String[] newLB_AT) {
		LB_AT = newLB_AT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_AT() {
		return "NOM_LB_AT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AT_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_AT_SELECT() {
		return "NOM_LB_AT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_AT() {
		return getLB_AT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_AT_SELECT() {
		return getZone(getNOM_LB_AT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_INAPTITUDE() {
		if (LB_INAPTITUDE == null)
			LB_INAPTITUDE = initialiseLazyLB();
		return LB_INAPTITUDE;
	}

	/**
	 * Setter de la liste: LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_INAPTITUDE(String[] newLB_INAPTITUDE) {
		LB_INAPTITUDE = newLB_INAPTITUDE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_INAPTITUDE() {
		return "NOM_LB_INAPTITUDE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_INAPTITUDE_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_INAPTITUDE_SELECT() {
		return "NOM_LB_INAPTITUDE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_INAPTITUDE() {
		return getLB_INAPTITUDE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_INAPTITUDE_SELECT() {
		return getZone(getNOM_LB_INAPTITUDE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LESION Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_LESION() {
		if (LB_LESION == null)
			LB_LESION = initialiseLazyLB();
		return LB_LESION;
	}

	/**
	 * Setter de la liste: LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_LESION(String[] newLB_LESION) {
		LB_LESION = newLB_LESION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LESION Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_LESION() {
		return "NOM_LB_LESION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LESION_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_LESION_SELECT() {
		return "NOM_LB_LESION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_LESION() {
		return getLB_LESION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_LESION_SELECT() {
		return getZone(getNOM_LB_LESION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MALADIE Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_MALADIE() {
		if (LB_MALADIE == null)
			LB_MALADIE = initialiseLazyLB();
		return LB_MALADIE;
	}

	/**
	 * Setter de la liste: LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_MALADIE(String[] newLB_MALADIE) {
		LB_MALADIE = newLB_MALADIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MALADIE Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MALADIE() {
		return "NOM_LB_MALADIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MALADIE_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MALADIE_SELECT() {
		return "NOM_LB_MALADIE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_MALADIE() {
		return getLB_MALADIE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_MALADIE_SELECT() {
		return getZone(getNOM_LB_MALADIE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_MEDECIN() {
		if (LB_MEDECIN == null)
			LB_MEDECIN = initialiseLazyLB();
		return LB_MEDECIN;
	}

	/**
	 * Setter de la liste: LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_MEDECIN(String[] newLB_MEDECIN) {
		LB_MEDECIN = newLB_MEDECIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MEDECIN() {
		return "NOM_LB_MEDECIN";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MEDECIN_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT() {
		return "NOM_LB_MEDECIN_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN() {
		return getLB_MEDECIN();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT() {
		return getZone(getNOM_LB_MEDECIN_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_RECOMMANDATION() {
		if (LB_RECOMMANDATION == null)
			LB_RECOMMANDATION = initialiseLazyLB();
		return LB_RECOMMANDATION;
	}

	/**
	 * Setter de la liste: LB_RECOMMANDATION Date de création : (15/09/11
	 * 08:57:49)
	 * 
	 */
	private void setLB_RECOMMANDATION(String[] newLB_RECOMMANDATION) {
		LB_RECOMMANDATION = newLB_RECOMMANDATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION() {
		return "NOM_LB_RECOMMANDATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RECOMMANDATION_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION_SELECT() {
		return "NOM_LB_RECOMMANDATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_RECOMMANDATION() {
		return getLB_RECOMMANDATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_RECOMMANDATION_SELECT() {
		return getZone(getNOM_LB_RECOMMANDATION_SELECT());
	}

	private TypeAT getAtCourant() {
		return atCourant;
	}

	private void setAtCourant(TypeAT atCourant) {
		this.atCourant = atCourant;
	}

	private TypeInaptitude getInaptitudeCourante() {
		return inaptitudeCourante;
	}

	private void setInaptitudeCourante(TypeInaptitude inaptitudeCourante) {
		this.inaptitudeCourante = inaptitudeCourante;
	}

	private SiegeLesion getLesionCourant() {
		return lesionCourant;
	}

	private void setLesionCourant(SiegeLesion lesionCourant) {
		this.lesionCourant = lesionCourant;
	}

	private ArrayList<TypeAT> getListeAT() {
		return listeAT;
	}

	private void setListeAT(ArrayList<TypeAT> listeAT) {
		this.listeAT = listeAT;
	}

	private ArrayList<TypeInaptitude> getListeInaptitude() {
		return listeInaptitude;
	}

	private void setListeInaptitude(ArrayList<TypeInaptitude> listeInaptitude) {
		this.listeInaptitude = listeInaptitude;
	}

	private ArrayList<SiegeLesion> getListeLesion() {
		return listeLesion;
	}

	private void setListeLesion(ArrayList<SiegeLesion> listeLesion) {
		this.listeLesion = listeLesion;
	}

	private ArrayList<MaladiePro> getListeMaladie() {
		return listeMaladie;
	}

	private void setListeMaladie(ArrayList<MaladiePro> listeMaladie) {
		this.listeMaladie = listeMaladie;
	}

	private ArrayList<Medecin> getListeMedecin() {
		return listeMedecin;
	}

	private void setListeMedecin(ArrayList<Medecin> listeMedecin) {
		this.listeMedecin = listeMedecin;
	}

	private ArrayList<Recommandation> getListeRecommandation() {
		return listeRecommandation;
	}

	private void setListeRecommandation(ArrayList<Recommandation> listeRecommandation) {
		this.listeRecommandation = listeRecommandation;
	}

	private MaladiePro getMaladieCourante() {
		return maladieCourante;
	}

	private void setMaladieCourante(MaladiePro maladieCourante) {
		this.maladieCourante = maladieCourante;
	}

	private Medecin getMedecinCourant() {
		return medecinCourant;
	}

	private void setMedecinCourant(Medecin medecinCourant) {
		this.medecinCourant = medecinCourant;
	}

	private Recommandation getRecommandationCourante() {
		return recommandationCourante;
	}

	private void setRecommandationCourante(Recommandation recommandationCourante) {
		this.recommandationCourante = recommandationCourante;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DOCUMENT
	 * 
	 */
	public String getNOM_LB_TYPE_DOCUMENT() {
		return "NOM_LB_TYPE_DOCUMENT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_DOCUMENT
	 * 
	 */
	public String getVAL_LB_TYPE_DOCUMENT_SELECT() {
		return getZone(getNOM_LB_TYPE_DOCUMENT_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DOCUMENT_SELECT
	 * 
	 */
	public String getNOM_LB_TYPE_DOCUMENT_SELECT() {
		return "NOM_LB_TYPE_DOCUMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DOCUMENT
	 * 
	 */
	public String[] getVAL_LB_TYPE_DOCUMENT() {
		return getLB_TYPE_DOCUMENT();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DOCUMENT
	 * 
	 */
	private String[] getLB_TYPE_DOCUMENT() {
		if (LB_TYPE_DOCUMENT == null)
			LB_TYPE_DOCUMENT = initialiseLazyLB();
		return LB_TYPE_DOCUMENT;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_DOUMENT Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_DOCUMENT() {
		return "NOM_PB_CREER_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_DOUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_DOCUMENT() {
		return "NOM_PB_SUPPRIMER_TYPE_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_DOUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_DOCUMENT() {
		return getZone(getNOM_ST_ACTION_TYPE_DOCUMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_DOUMENT
	 * Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_DOCUMENT() {
		return "NOM_ST_ACTION_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_EF_TYPE_DOCUMENT() {
		return "NOM_EF_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_TYPE_DOCUMENT
	 * Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_EF_CODE_TYPE_DOCUMENT() {
		return "NOM_EF_CODE_TYPE_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_DOCUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_EF_TYPE_DOCUMENT() {
		return getZone(getNOM_EF_TYPE_DOCUMENT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_TYPE_DOCUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_EF_CODE_TYPE_DOCUMENT() {
		return getZone(getNOM_EF_CODE_TYPE_DOCUMENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_DOCUMENT() {
		return "NOM_PB_VALIDER_TYPE_DOCUMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeDocument(request))
			return false;

		if (!performControlerRegleGestionTypeDocument(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_DOCUMENT() != null && getVAL_ST_ACTION_TYPE_DOCUMENT() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_CREATION)) {
				setTypeDocumentCourant(new TypeDocument());
				getTypeDocumentCourant().setLibTypeDocument(getVAL_EF_TYPE_DOCUMENT());
				getTypeDocumentCourant().setCodTypeDocument(getVAL_EF_CODE_TYPE_DOCUMENT());
				getTypeDocumentCourant().setModuleTypeDocument("HSCT");
				getTypeDocumentDao()
						.creerTypeDocument(getTypeDocumentCourant().getLibTypeDocument(),
								getTypeDocumentCourant().getCodTypeDocument(),
								getTypeDocumentCourant().getModuleTypeDocument());
				if (!getTransaction().isErreur())
					getListeTypeDocument().add(getTypeDocumentCourant());
				else
					return false;
			} else if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_SUPPRESSION)) {
				getTypeDocumentDao().supprimerTypeDocument(getTypeDocumentCourant().getIdTypeDocument());
				if (!getTransaction().isErreur())
					getListeTypeDocument().remove(getTypeDocumentCourant());
				else
					return false;
				setTypeDocumentCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeDocument(request);
			addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_DOCUMENT() {
		return "NOM_PB_ANNULER_TYPE_DOCUMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private TypeDocument getTypeDocumentCourant() {
		return typeDocumentCourant;
	}

	private void setTypeDocumentCourant(TypeDocument typeDocumentCourant) {
		this.typeDocumentCourant = typeDocumentCourant;
	}

	private ArrayList<TypeDocument> getListeTypeDocument() {
		return listeTypeDocument;
	}

	private void setListeTypeDocument(ArrayList<TypeDocument> listeTypeDocument) {
		this.listeTypeDocument = listeTypeDocument;
	}

	/**
	 * Setter de la liste: LB_TYPE_DOCUMENT Date de création : (15/09/11
	 * 14:14:43)
	 * 
	 */
	private void setLB_TYPE_DOCUMENT(String[] newLB_TYPE_DOCUMENT) {
		LB_TYPE_DOCUMENT = newLB_TYPE_DOCUMENT;
	}

	/**
	 * Contrôle les zones saisies d'un type de document Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTypeDocument(HttpServletRequest request) throws Exception {

		// Verification libellé type document not null
		if (getZone(getNOM_EF_TYPE_DOCUMENT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification libellé du code du type document not null
		if (getZone(getNOM_EF_CODE_TYPE_DOCUMENT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une autre administration Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeDocument(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de document utilisé sur
		// document agent

		if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_SUPPRESSION)
				&& Document.listerDocument(getTransaction(), getTypeDocumentCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un document", "ce type"));
			return false;
		}

		// Vérification des contraintes d'unicité du type de document
		if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_CREATION)) {

			for (TypeDocument typeDoc : getListeTypeDocument()) {
				if (typeDoc.getLibTypeDocument().equals(getVAL_EF_TYPE_DOCUMENT().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type de document", "ce libellé"));
					return false;
				}
				if (typeDoc.getCodTypeDocument().equals(getVAL_EF_CODE_TYPE_DOCUMENT().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un code type de document", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Initialisation de la listes des types de documents Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeTypeDocument(HttpServletRequest request) throws Exception {
		setListeTypeDocument(getTypeDocumentDao().listerTypeDocumentAvecModule("HSCT"));
		if (getListeTypeDocument().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeDocument> list = getListeTypeDocument().listIterator(); list.hasNext();) {
				TypeDocument type = (TypeDocument) list.next();
				String ligne[] = { type.getLibTypeDocument() + " - " + type.getCodTypeDocument() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DOCUMENT(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_DOCUMENT(null);
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_CREER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_TYPE_DOCUMENT(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_DOCUMENT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DOCUMENT_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeDocument().size()) {
			TypeDocument type = getListeTypeDocument().get(indice);
			setTypeDocumentCourant(type);
			addZone(getNOM_EF_TYPE_DOCUMENT(), type.getLibTypeDocument());
			addZone(getNOM_EF_CODE_TYPE_DOCUMENT(), type.getCodTypeDocument());
			addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de documents"));
		}

		return true;
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public AccidentTravailDao getAccidentTravailDao() {
		return accidentTravailDao;
	}

	public void setAccidentTravailDao(AccidentTravailDao accidentTravailDao) {
		this.accidentTravailDao = accidentTravailDao;
	}

	public HandicapDao getHandicapDao() {
		return handicapDao;
	}

	public void setHandicapDao(HandicapDao handicapDao) {
		this.handicapDao = handicapDao;
	}

	public MaladieProDao getMaladieProDao() {
		return maladieProDao;
	}

	public void setMaladieProDao(MaladieProDao maladieProDao) {
		this.maladieProDao = maladieProDao;
	}

	public MedecinDao getMedecinDao() {
		return medecinDao;
	}

	public void setMedecinDao(MedecinDao medecinDao) {
		this.medecinDao = medecinDao;
	}

	public RecommandationDao getRecommandationDao() {
		return recommandationDao;
	}

	public void setRecommandationDao(RecommandationDao recommandationDao) {
		this.recommandationDao = recommandationDao;
	}
}
