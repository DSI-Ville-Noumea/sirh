package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.diplome.FormationAgent;
import nc.mairie.metier.diplome.PermisAgent;
import nc.mairie.metier.parametrage.CentreFormation;
import nc.mairie.metier.parametrage.SpecialiteDiplome;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TitreFormation;
import nc.mairie.metier.parametrage.TitrePermis;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.metier.referentiel.AutreAdministration;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.diplome.FormationAgentDao;
import nc.mairie.spring.dao.metier.diplome.PermisAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CentreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.SpecialiteDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.TitrePermisDao;
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
 * Process OePARAMETRAGEDonneesPerso Date de création : (14/09/11 15:57:59)
 * 
 */
public class OePARAMETRAGEDonneesPerso extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_DIPLOME;
	private String[] LB_SPECIALITE;
	private String[] LB_ADMIN;
	private String[] LB_TYPE_DOCUMENT;
	private String[] LB_TITRE_FORMATION;
	private String[] LB_CENTRE_FORMATION;
	private String[] LB_TITRE_PERMIS;

	private ArrayList<TitreDiplome> listeDiplome;
	private TitreDiplome diplomeCourant;

	private ArrayList<SpecialiteDiplome> listeSpecialite;
	private SpecialiteDiplome specialiteCourante;

	private ArrayList<AutreAdministration> listeAdmin;
	private AutreAdministration adminCourante;

	private ArrayList<TypeDocument> listeTypeDocument;
	private TypeDocument typeDocumentCourant;

	private ArrayList<TitreFormation> listeTitreFormation;
	private TitreFormation titreFormationCourant;

	private ArrayList<CentreFormation> listeCentreFormation;
	private CentreFormation centreFormationCourant;

	private ArrayList<TitrePermis> listeTitrePermis;
	private TitrePermis titrePermisCourant;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private TitreFormationDao titreFormationDao;
	private CentreFormationDao centreFormationDao;
	private FormationAgentDao formationAgentDao;
	private TitrePermisDao titrePermisDao;
	private PermisAgentDao permisAgentDao;
	private SpecialiteDiplomeDao specialiteDiplomeDao;
	private TitreDiplomeDao titreDiplomeDao;
	private TypeDocumentDao typeDocumentDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 15:57:59)
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
		if (getListeDiplome() == null) {
			ArrayList<TitreDiplome> listeDiplome = getTitreDiplomeDao().listerTitreDiplome();
			setListeDiplome(listeDiplome);
			initialiseListeDiplome(request);
		}

		if (getListeSpecialite() == null) {
			// Recherche des spécialités de diplôme
			ArrayList<SpecialiteDiplome> listeSpecialite = getSpecialiteDiplomeDao().listerSpecialiteDiplome();
			setListeSpecialite(listeSpecialite);
			initialiseListeSpecialite(request);
		}

		if (getListeAdmin() == null) {
			// Recherche des qdministrations
			ArrayList<AutreAdministration> listeAdmin = AutreAdministration.listerAutreAdministration(getTransaction());
			setListeAdmin(listeAdmin);
			initialiseListeAdmin(request);
		}

		if (getListeTypeDocument() == null) {
			// Recherche des types de documents
			ArrayList<TypeDocument> listeTypeDocument = getTypeDocumentDao().listerTypeDocumentAvecModule(
					"DONNEES PERSONNELLES");
			setListeTypeDocument(listeTypeDocument);
			initialiseListeTypeDocument(request);
		}

		if (getListeCentreFormation() == null) {
			// Recherche des centres de formations
			ArrayList<CentreFormation> listeCentreFormation = getCentreFormationDao().listerCentreFormation();
			setListeCentreFormation(listeCentreFormation);
			initialiseListeCentreFormationt(request);
		}

		if (getListeTitreFormation() == null) {
			// Recherche des titres de formation
			ArrayList<TitreFormation> listeTitreFormation = getTitreFormationDao().listerTitreFormation();
			setListeTitreFormation(listeTitreFormation);
			initialiseListeTitreFormation(request);
		}

		if (getListeTitrePermis() == null) {
			// Recherche des titres de permis
			ArrayList<TitrePermis> listeTitrePermis = (ArrayList<TitrePermis>) getTitrePermisDao().listerTitrePermis();
			setListeTitrePermis(listeTitrePermis);
			initialiseListeTitrePermis(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTitreFormationDao() == null) {
			setTitreFormationDao(new TitreFormationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCentreFormationDao() == null) {
			setCentreFormationDao(new CentreFormationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFormationAgentDao() == null) {
			setFormationAgentDao(new FormationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getPermisAgentDao() == null) {
			setPermisAgentDao(new PermisAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePermisDao() == null) {
			setTitrePermisDao(new TitrePermisDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSpecialiteDiplomeDao() == null) {
			setSpecialiteDiplomeDao(new SpecialiteDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitreDiplomeDao() == null) {
			setTitreDiplomeDao(new TitreDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation de la listes des titres de formation Date de création :
	 * (14/09/11)
	 */
	private void initialiseListeCentreFormationt(HttpServletRequest request) throws Exception {
		if (getListeCentreFormation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<CentreFormation> list = getListeCentreFormation().listIterator(); list.hasNext();) {
				CentreFormation centre = (CentreFormation) list.next();
				String ligne[] = { centre.getLibCentreFormation().toUpperCase() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CENTRE_FORMATION(aFormat.getListeFormatee());
		} else {
			setLB_CENTRE_FORMATION(null);
		}
	}

	/**
	 * Initialisation de la listes des titres de diplôme Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTitrePermis(HttpServletRequest request) throws Exception {
		if (getListeTitrePermis().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TitrePermis> list = getListeTitrePermis().listIterator(); list.hasNext();) {
				TitrePermis titre = (TitrePermis) list.next();
				String ligne[] = { titre.getLibPermis().toUpperCase() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TITRE_PERMIS(aFormat.getListeFormatee());
		} else {
			setLB_TITRE_PERMIS(null);
		}
	}

	/**
	 * Initialisation de la listes des titres de formation Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTitreFormation(HttpServletRequest request) throws Exception {
		if (getListeTitreFormation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TitreFormation> list = getListeTitreFormation().listIterator(); list.hasNext();) {
				TitreFormation titre = (TitreFormation) list.next();
				String ligne[] = { titre.getLibTitreFormation().toUpperCase() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TITRE_FORMATION(aFormat.getListeFormatee());
		} else {
			setLB_TITRE_FORMATION(null);
		}
	}

	/**
	 * Initialisation de la listes des titres de diplôme Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeDiplome(HttpServletRequest request) throws Exception {
		setListeDiplome(getTitreDiplomeDao().listerTitreDiplome());
		if (getListeDiplome().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TitreDiplome> list = getListeDiplome().listIterator(); list.hasNext();) {
				TitreDiplome td = (TitreDiplome) list.next();
				String ligne[] = { td.getLibTitreDiplome() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_DIPLOME(aFormat.getListeFormatee());
		} else {
			setLB_DIPLOME(null);
		}
	}

	/**
	 * Initialisation de la listes des motifs de non recrutement Date de
	 * création : (14/09/11)
	 * 
	 */
	private void initialiseListeSpecialite(HttpServletRequest request) throws Exception {
		setListeSpecialite(getSpecialiteDiplomeDao().listerSpecialiteDiplome());
		if (getListeSpecialite().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<SpecialiteDiplome> list = getListeSpecialite().listIterator(); list.hasNext();) {
				SpecialiteDiplome sd = (SpecialiteDiplome) list.next();
				String ligne[] = { sd.getLibSpecialiteDiplome() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_SPECIALITE(aFormat.getListeFormatee());
		} else {
			setLB_SPECIALITE(null);
		}
	}

	/**
	 * Initialisation de la listes des administrations Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeAdmin(HttpServletRequest request) throws Exception {
		setListeAdmin(AutreAdministration.listerAutreAdministration(getTransaction()));
		if (getListeAdmin().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<AutreAdministration> list = getListeAdmin().listIterator(); list.hasNext();) {
				AutreAdministration admin = (AutreAdministration) list.next();
				String ligne[] = { admin.getLibAutreAdmin() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ADMIN(aFormat.getListeFormatee());
		} else {
			setLB_ADMIN(null);
		}
	}

	/**
	 * Initialisation de la listes des types de documents Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeTypeDocument(HttpServletRequest request) throws Exception {
		setListeTypeDocument(getTypeDocumentDao().listerTypeDocumentAvecModule("DONNEES PERSONNELLES"));
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
	 * Constructeur du process OePARAMETRAGEDonneesPerso. Date de création :
	 * (14/09/11 15:57:59)
	 * 
	 */
	public OePARAMETRAGEDonneesPerso() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CENTRE_PERMIS Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_ANNULER_TITRE_PERMIS() {
		return "NOM_PB_ANNULER_TITRE_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_ANNULER_TITRE_PERMIS(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TITRE_PERMIS(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CENTRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_ANNULER_CENTRE_FORMATION() {
		return "NOM_PB_ANNULER_CENTRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_ANNULER_CENTRE_FORMATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CENTRE_FORMATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TITRE_FORMATION Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_ANNULER_TITRE_FORMATION() {
		return "NOM_PB_ANNULER_TITRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_ANNULER_TITRE_FORMATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TITRE_FORMATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_ANNULER_DIPLOME() {
		return "NOM_PB_ANNULER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_ANNULER_DIPLOME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_ANNULER_SPECIALITE() {
		return "NOM_PB_ANNULER_SPECIALITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_ANNULER_SPECIALITE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_SPECIALITE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CENTRE_FORMATION Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_CREER_CENTRE_FORMATION() {
		return "NOM_PB_CREER_CENTRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_CREER_CENTRE_FORMATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_CENTRE_FORMATION(), ACTION_CREATION);
		addZone(getNOM_EF_CENTRE_FORMATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TITRE_PERMIS Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_CREER_TITRE_PERMIS() {
		return "NOM_PB_CREER_TITRE_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_CREER_TITRE_PERMIS(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TITRE_PERMIS(), ACTION_CREATION);
		addZone(getNOM_EF_TITRE_PERMIS(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TITRE_FORMATION Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_CREER_TITRE_FORMATION() {
		return "NOM_PB_CREER_TITRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_CREER_TITRE_FORMATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TITRE_FORMATION(), ACTION_CREATION);
		addZone(getNOM_EF_TITRE_FORMATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_CREER_DIPLOME() {
		return "NOM_PB_CREER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_CREER_DIPLOME(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_CREATION);
		addZone(getNOM_EF_DIPLOME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NIV_ETUDE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_CREER_SPECIALITE() {
		return "NOM_PB_CREER_SPECIALITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_CREER_SPECIALITE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_SPECIALITE(), ACTION_CREATION);
		addZone(getNOM_EF_SPECIALITE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TITRE_PERMIS Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TITRE_PERMIS() {
		return "NOM_PB_SUPPRIMER_TITRE_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TITRE_PERMIS(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TITRE_PERMIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_TITRE_PERMIS_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitrePermis().size()) {
			TitrePermis titre = getListeTitrePermis().get(indice);
			setTitrePermisCourant(titre);
			addZone(getNOM_EF_TITRE_PERMIS(), titre.getLibPermis());
			addZone(getNOM_ST_ACTION_TITRE_PERMIS(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de permis"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TITRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TITRE_FORMATION() {
		return "NOM_PB_SUPPRIMER_TITRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TITRE_FORMATION(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TITRE_FORMATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_TITRE_FORMATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitreFormation().size()) {
			TitreFormation titre = getListeTitreFormation().get(indice);
			setTitreFormationCourant(titre);
			addZone(getNOM_EF_TITRE_FORMATION(), titre.getLibTitreFormation());
			addZone(getNOM_ST_ACTION_TITRE_FORMATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de diplômes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CENTRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CENTRE_FORMATION() {
		return "NOM_PB_SUPPRIMER_CENTRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CENTRE_FORMATION(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_CENTRE_FORMATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_CENTRE_FORMATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeCentreFormation().size()) {
			CentreFormation centre = getListeCentreFormation().get(indice);
			setCentreFormationCourant(centre);
			addZone(getNOM_EF_CENTRE_FORMATION(), centre.getLibCentreFormation());
			addZone(getNOM_ST_ACTION_CENTRE_FORMATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de diplômes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DIPLOME() {
		return "NOM_PB_SUPPRIMER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DIPLOME(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_DIPLOME_SELECT()) ? Integer.parseInt(getVAL_LB_DIPLOME_SELECT())
				: -1);

		if (indice != -1 && indice < getListeDiplome().size()) {
			TitreDiplome td = getListeDiplome().get(indice);
			setDiplomeCourant(td);
			addZone(getNOM_EF_DIPLOME(), td.getLibTitreDiplome());
			addZone(getNOM_EF_NIV_ETUDE(), td.getNiveauEtude());
			addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de diplômes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_SPECIALITE() {
		return "NOM_PB_SUPPRIMER_SPECIALITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_SUPPRIMER_SPECIALITE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_SPECIALITE_SELECT()) ? Integer
				.parseInt(getVAL_LB_SPECIALITE_SELECT()) : -1);

		if (indice != -1 && indice < getListeSpecialite().size()) {
			SpecialiteDiplome sd = getListeSpecialite().get(indice);
			setSpecialiteCourante(sd);
			addZone(getNOM_EF_SPECIALITE(), sd.getLibSpecialiteDiplome());
			addZone(getNOM_ST_ACTION_SPECIALITE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "spécialités de diplômes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_CENTRE_FORMATION() {
		return "NOM_PB_VALIDER_CENTRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_VALIDER_CENTRE_FORMATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieCentreFormation(request))
			return false;

		if (!performControlerRegleGestionCentreFormation(request))
			return false;

		if (getVAL_ST_ACTION_CENTRE_FORMATION() != null && getVAL_ST_ACTION_CENTRE_FORMATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_CREATION)) {
				setCentreFormationCourant(new CentreFormation());
				getCentreFormationCourant().setLibCentreFormation(getVAL_EF_CENTRE_FORMATION());
				getCentreFormationDao().creerCentreFormation(getCentreFormationCourant().getLibCentreFormation());
				getListeCentreFormation().add(getCentreFormationCourant());
			} else if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_SUPPRESSION)) {
				getCentreFormationDao().supprimerCentreFormation(getCentreFormationCourant().getIdCentreFormation());
				getListeCentreFormation().remove(getCentreFormationCourant());
				setCentreFormationCourant(null);
			} else if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_MODIFICATION)) {
				getCentreFormationCourant().setLibCentreFormation(getVAL_EF_CENTRE_FORMATION());
				getCentreFormationDao().modifierCentreFormation(getCentreFormationCourant().getIdCentreFormation(),
						getCentreFormationCourant().getLibCentreFormation());

			}

			commitTransaction();
			initialiseListeCentreFormationt(request);
			addZone(getNOM_ST_ACTION_CENTRE_FORMATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un titre de formation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieCentreFormation(HttpServletRequest request) throws Exception {

		// Verification libellé centre formation not null
		if (getZone(getNOM_EF_CENTRE_FORMATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un titre de formation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionCentreFormation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un centre de formation utilisé sur une
		// formation d'agent
		if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_SUPPRESSION)) {
			ArrayList<FormationAgent> listeFormationAgent = getFormationAgentDao()
					.listerFormationAgentAvecCentreFormation(getCentreFormationCourant().getIdCentreFormation());
			if (listeFormationAgent.size() > 0) {
				// "ERR989",
				// "Suppression impossible. Il existe au moins @ rattaché à @."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR989", "une formation d'agent", "ce centre de formation"));
				return false;
			}
		}

		// Vérification des contraintes d'unicité du centre de formation
		if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_CREATION)) {
			for (CentreFormation centre : getListeCentreFormation()) {
				if (centre.getLibCentreFormation().equals(getVAL_EF_CENTRE_FORMATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un centre de formation", "ce libellé"));
					return false;
				}
			}
		}

		// Vérification des contraintes d'unicité du titre de formation
		if (getVAL_ST_ACTION_CENTRE_FORMATION().equals(ACTION_MODIFICATION)) {
			for (CentreFormation centre : getListeCentreFormation()) {
				if (centre.getLibCentreFormation().equals(getVAL_EF_CENTRE_FORMATION().toUpperCase())
						&& (!centre.equals(getCentreFormationCourant()))) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un centre de formation", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TITRE_FORMATION Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_TITRE_FORMATION() {
		return "NOM_PB_VALIDER_TITRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_VALIDER_TITRE_FORMATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTitreFormation(request))
			return false;

		if (!performControlerRegleGestionTitreFormation(request))
			return false;

		if (getVAL_ST_ACTION_TITRE_FORMATION() != null && getVAL_ST_ACTION_TITRE_FORMATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_CREATION)) {
				setTitreFormationCourant(new TitreFormation());
				getTitreFormationCourant().setLibTitreFormation(getVAL_EF_TITRE_FORMATION());
				getTitreFormationDao().creerTitreFormation(getTitreFormationCourant().getLibTitreFormation());
				getListeTitreFormation().add(getTitreFormationCourant());
			} else if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_SUPPRESSION)) {
				getTitreFormationDao().supprimerTitreFormation(getTitreFormationCourant().getIdTitreFormation());
				getListeTitreFormation().remove(getTitreFormationCourant());
				setTitreFormationCourant(null);
			} else if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_MODIFICATION)) {
				getTitreFormationCourant().setLibTitreFormation(getVAL_EF_TITRE_FORMATION());
				getTitreFormationDao().modifierTitreFormation(getTitreFormationCourant().getIdTitreFormation(),
						getTitreFormationCourant().getLibTitreFormation());

			}

			commitTransaction();
			initialiseListeTitreFormation(request);
			addZone(getNOM_ST_ACTION_TITRE_FORMATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un titre de formation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTitreFormation(HttpServletRequest request) throws Exception {

		// Verification libellé titre formation not null
		if (getZone(getNOM_EF_TITRE_FORMATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un titre de formation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTitreFormation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de formation utilisé sur une
		// formation d'agent
		if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_SUPPRESSION)) {
			ArrayList<FormationAgent> listeFormationAgent = getFormationAgentDao()
					.listerFormationAgentAvecTitreFormation(getTitreFormationCourant().getIdTitreFormation());
			if (listeFormationAgent.size() > 0) {
				// "ERR989",
				// "Suppression impossible. Il existe au moins @ rattaché à @."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR989", "une formation d'agent", "ce titre de formation"));
				return false;
			}
		}

		// Vérification des contraintes d'unicité du titre de formation
		if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_CREATION)) {

			for (TitreFormation titre : getListeTitreFormation()) {
				if (titre.getLibTitreFormation().equals(getVAL_EF_TITRE_FORMATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de formation", "ce libellé"));
					return false;
				}
			}
		}

		// Vérification des contraintes d'unicité du titre de formation
		if (getVAL_ST_ACTION_TITRE_FORMATION().equals(ACTION_MODIFICATION)) {
			for (TitreFormation titre : getListeTitreFormation()) {
				if (titre.getLibTitreFormation().equals(getVAL_EF_TITRE_FORMATION().toUpperCase())
						&& (!titre.equals(getTitreFormationCourant()))) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de formation", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TITRE_FORMATION Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_TITRE_PERMIS() {
		return "NOM_PB_VALIDER_TITRE_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_VALIDER_TITRE_PERMIS(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTitrePermis(request))
			return false;

		if (!performControlerRegleGestionTitrePermis(request))
			return false;

		if (getVAL_ST_ACTION_TITRE_PERMIS() != null && getVAL_ST_ACTION_TITRE_PERMIS() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_CREATION)) {
				setTitrePermisCourant(new TitrePermis());
				getTitrePermisCourant().setLibPermis(getVAL_EF_TITRE_PERMIS());
				getTitrePermisDao().creerTitrePermis(getTitrePermisCourant().getLibPermis());
				getListeTitrePermis().add(getTitrePermisCourant());
			} else if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_SUPPRESSION)) {
				getTitrePermisDao().supprimerTitrePermis(getTitrePermisCourant().getIdPermis());
				getListeTitrePermis().remove(getTitrePermisCourant());
				setTitrePermisCourant(null);
			} else if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_MODIFICATION)) {
				getTitrePermisCourant().setLibPermis(getVAL_EF_TITRE_PERMIS());
				getTitrePermisDao().modifierTitrePermis(getTitrePermisCourant().getIdPermis(),
						getTitrePermisCourant().getLibPermis());

			}

			commitTransaction();
			initialiseListeTitrePermis(request);
			addZone(getNOM_ST_ACTION_TITRE_PERMIS(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un titre de formation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTitrePermis(HttpServletRequest request) throws Exception {

		// Verification libellé titre permis not null
		if (getZone(getNOM_EF_TITRE_PERMIS()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un titre de permis Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTitrePermis(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de permis utilisé sur un
		// permis d'agent
		if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_SUPPRESSION)) {
			ArrayList<PermisAgent> listePermisAgent = getPermisAgentDao().listerPermisAgentAvecTitrePermis(
					getTitrePermisCourant().getIdPermis());
			if (listePermisAgent.size() > 0) {
				// "ERR989",
				// "Suppression impossible. Il existe au moins @ rattaché à @."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR989", "un permis d'agent", "ce titre de permis"));
				return false;
			}
		}

		// Vérification des contraintes d'unicité du titre de permis
		if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_CREATION)) {
			for (TitrePermis titre : getListeTitrePermis()) {
				if (titre.getLibPermis().equals(getVAL_EF_TITRE_PERMIS().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de permis", "ce libellé"));
					return false;
				}
			}
		}

		// Vérification des contraintes d'unicité du titre de permis
		if (getVAL_ST_ACTION_TITRE_PERMIS().equals(ACTION_MODIFICATION)) {
			for (TitrePermis titre : getListeTitrePermis()) {
				if (titre.getLibPermis().equals(getVAL_EF_TITRE_PERMIS().toUpperCase())
						&& (!titre.equals(getTitrePermisCourant()))) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de permis", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_DIPLOME() {
		return "NOM_PB_VALIDER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_VALIDER_DIPLOME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieDiplome(request))
			return false;

		if (!performControlerRegleGestionDiplome(request))
			return false;

		if (getVAL_ST_ACTION_DIPLOME() != null && getVAL_ST_ACTION_DIPLOME() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_CREATION)) {
				setDiplomeCourant(new TitreDiplome());
				getDiplomeCourant().setLibTitreDiplome(getVAL_EF_DIPLOME());
				getDiplomeCourant().setNiveauEtude(getVAL_EF_NIV_ETUDE());
				getTitreDiplomeDao().creerTitreDiplome(getDiplomeCourant().getLibTitreDiplome(),
						getDiplomeCourant().getNiveauEtude());
				if (!getTransaction().isErreur())
					getListeDiplome().add(getDiplomeCourant());
			} else if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_SUPPRESSION)) {
				getTitreDiplomeDao().supprimerTitreDiplome(getDiplomeCourant().getIdTitreDiplome());
				if (!getTransaction().isErreur())
					getListeDiplome().remove(getDiplomeCourant());
				setDiplomeCourant(null);
			} else if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_MODIFICATION)) {
				getDiplomeCourant().setLibTitreDiplome(getVAL_EF_DIPLOME());
				getDiplomeCourant().setNiveauEtude(getVAL_EF_NIV_ETUDE());
				getTitreDiplomeDao().modifierTitreDiplome(getDiplomeCourant().getIdTitreDiplome(),
						getDiplomeCourant().getLibTitreDiplome(), getDiplomeCourant().getNiveauEtude());

			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeDiplome(request);
			addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un titre de diplôme Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieDiplome(HttpServletRequest request) throws Exception {

		// Verification libellé titre diplome not null
		if (getZone(getNOM_EF_DIPLOME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification niveau etude diplome not null
		if (getZone(getNOM_EF_NIV_ETUDE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "niveau d'étude"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un titre de diplome Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionDiplome(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de diplome utilisé sur un
		// diplome d'agent
		if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_SUPPRESSION)
				&& DiplomeAgent.listerDiplomeAgentAvecTitreDiplome(getTransaction(), getDiplomeCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "un diplôme d'agent", "ce titre de diplôme"));
			return false;
		}

		// Vérification des contraintes d'unicité du titre de diplome
		if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_CREATION)) {

			for (TitreDiplome titre : getListeDiplome()) {
				if (titre.getLibTitreDiplome().equals(getVAL_EF_DIPLOME().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de diplôme", "ce libellé"));
					return false;
				}
			}
		}

		// Vérification des contraintes d'unicité du titre de diplome
		if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_MODIFICATION)) {
			for (TitreDiplome titre : getListeDiplome()) {
				if (titre.getLibTitreDiplome().equals(getVAL_EF_DIPLOME().toUpperCase())
						&& (!titre.equals(diplomeCourant))) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de diplôme", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_SPECIALITE() {
		return "NOM_PB_VALIDER_SPECIALITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_VALIDER_SPECIALITE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieSpecialite(request))
			return false;

		if (!performControlerRegleGestionSpecialite(request))
			return false;

		if (getVAL_ST_ACTION_SPECIALITE() != null && getVAL_ST_ACTION_SPECIALITE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_SPECIALITE().equals(ACTION_CREATION)) {
				setSpecialiteCourante(new SpecialiteDiplome());
				getSpecialiteCourante().setLibSpeDiplome(getVAL_EF_SPECIALITE());
				getSpecialiteDiplomeDao().creerSpecialiteDiplome(getSpecialiteCourante().getLibSpecialiteDiplome());
				if (!getTransaction().isErreur())
					getListeSpecialite().add(getSpecialiteCourante());
			} else if (getVAL_ST_ACTION_SPECIALITE().equals(ACTION_SUPPRESSION)) {
				getSpecialiteDiplomeDao().supprimerSpecialiteDiplome(getSpecialiteCourante().getIdSpecialiteDiplome());
				if (!getTransaction().isErreur())
					getListeSpecialite().remove(getSpecialiteCourante());
				setSpecialiteCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeSpecialite(request);
			addZone(getNOM_ST_ACTION_SPECIALITE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une spécialité de diplôme Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieSpecialite(HttpServletRequest request) throws Exception {

		// Verification libellé spécialité not null
		if (getZone(getNOM_EF_SPECIALITE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une spécialité de diplôme Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionSpecialite(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une spécialité utilisée sur un diplome
		// d'agent
		if (getVAL_ST_ACTION_SPECIALITE().equals(ACTION_SUPPRESSION)
				&& DiplomeAgent.listerDiplomeAgentAvecSpecialiteDiplome(getTransaction(), getSpecialiteCourante())
						.size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "un diplôme d'agent", "cette spécialité de diplôme"));
			return false;
		}

		// Vérification des contraintes d'unicité de la spécialite de diplome
		if (getVAL_ST_ACTION_SPECIALITE().equals(ACTION_CREATION)) {

			for (SpecialiteDiplome specialite : getListeSpecialite()) {
				if (specialite.getLibSpecialiteDiplome().equals(getVAL_EF_SPECIALITE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une spécialité de diplôme", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TITRE_PERMIS
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_ST_ACTION_TITRE_PERMIS() {
		return "NOM_ST_ACTION_TITRE_PERMIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TITRE_PERMIS Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_ST_ACTION_TITRE_PERMIS() {
		return getZone(getNOM_ST_ACTION_TITRE_PERMIS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_TITRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_ST_ACTION_TITRE_FORMATION() {
		return "NOM_ST_ACTION_TITRE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TITRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_ST_ACTION_TITRE_FORMATION() {
		return getZone(getNOM_ST_ACTION_TITRE_FORMATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_CENTRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_ST_ACTION_CENTRE_FORMATION() {
		return "NOM_ST_ACTION_CENTRE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_CENTRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_ST_ACTION_CENTRE_FORMATION() {
		return getZone(getNOM_ST_ACTION_CENTRE_FORMATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DIPLOME Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_ST_ACTION_DIPLOME() {
		return "NOM_ST_ACTION_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_DIPLOME
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_ST_ACTION_DIPLOME() {
		return getZone(getNOM_ST_ACTION_DIPLOME());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_SPECIALITE
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_ST_ACTION_SPECIALITE() {
		return "NOM_ST_ACTION_SPECIALITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_SPECIALITE Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_ST_ACTION_SPECIALITE() {
		return getZone(getNOM_ST_ACTION_SPECIALITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_PERMIS Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_EF_TITRE_PERMIS() {
		return "NOM_EF_TITRE_PERMIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_PERMIS Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_EF_TITRE_PERMIS() {
		return getZone(getNOM_EF_TITRE_PERMIS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_EF_TITRE_FORMATION() {
		return "NOM_EF_TITRE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_EF_TITRE_FORMATION() {
		return getZone(getNOM_EF_TITRE_FORMATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CENTRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_EF_CENTRE_FORMATION() {
		return "NOM_EF_CENTRE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CENTRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_EF_CENTRE_FORMATION() {
		return getZone(getNOM_EF_CENTRE_FORMATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_EF_DIPLOME() {
		return "NOM_EF_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DIPLOME Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_EF_DIPLOME() {
		return getZone(getNOM_EF_DIPLOME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_EF_SPECIALITE() {
		return "NOM_EF_SPECIALITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SPECIALITE Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_EF_SPECIALITE() {
		return getZone(getNOM_EF_SPECIALITE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME Date de création
	 * : (14/09/11 15:57:59)
	 * 
	 */
	private String[] getLB_DIPLOME() {
		if (LB_DIPLOME == null)
			LB_DIPLOME = initialiseLazyLB();
		return LB_DIPLOME;
	}

	/**
	 * Setter de la liste: LB_DIPLOME Date de création : (14/09/11 15:57:59)
	 * 
	 */
	private void setLB_DIPLOME(String[] newLB_DIPLOME) {
		LB_DIPLOME = newLB_DIPLOME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME Date de création
	 * : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_DIPLOME() {
		return "NOM_LB_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_SELECT Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_DIPLOME_SELECT() {
		return "NOM_LB_DIPLOME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String[] getVAL_LB_DIPLOME() {
		return getLB_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_LB_DIPLOME_SELECT() {
		return getZone(getNOM_LB_DIPLOME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CENTRE_FORMATION Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	private String[] getLB_CENTRE_FORMATION() {
		if (LB_CENTRE_FORMATION == null)
			LB_CENTRE_FORMATION = initialiseLazyLB();
		return LB_CENTRE_FORMATION;
	}

	/**
	 * Setter de la liste: LB_CENTRE_FORMATION Date de création : (14/09/11
	 * 15:57:59)
	 * 
	 */
	private void setLB_CENTRE_FORMATION(String[] newLB_CENTRE_FORMATION) {
		LB_CENTRE_FORMATION = newLB_CENTRE_FORMATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CENTRE_FORMATION Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_CENTRE_FORMATION() {
		return "NOM_LB_CENTRE_FORMATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CENTRE_FORMATION_SELECT Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_CENTRE_FORMATION_SELECT() {
		return "NOM_LB_CENTRE_FORMATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CENTRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String[] getVAL_LB_CENTRE_FORMATION() {
		return getLB_CENTRE_FORMATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CENTRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_LB_CENTRE_FORMATION_SELECT() {
		return getZone(getNOM_LB_CENTRE_FORMATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_FORMATION Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	private String[] getLB_TITRE_FORMATION() {
		if (LB_TITRE_FORMATION == null)
			LB_TITRE_FORMATION = initialiseLazyLB();
		return LB_TITRE_FORMATION;
	}

	/**
	 * Setter de la liste: LB_TITRE_FORMATION Date de création : (14/09/11
	 * 15:57:59)
	 * 
	 */
	private void setLB_TITRE_FORMATION(String[] newLB_TITRE_FORMATION) {
		LB_TITRE_FORMATION = newLB_TITRE_FORMATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_FORMATION Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_TITRE_FORMATION() {
		return "NOM_LB_TITRE_FORMATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_FORMATION_SELECT Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_TITRE_FORMATION_SELECT() {
		return "NOM_LB_TITRE_FORMATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String[] getVAL_LB_TITRE_FORMATION() {
		return getLB_TITRE_FORMATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_FORMATION Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_LB_TITRE_FORMATION_SELECT() {
		return getZone(getNOM_LB_TITRE_FORMATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_PERMIS Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	private String[] getLB_TITRE_PERMIS() {
		if (LB_TITRE_PERMIS == null)
			LB_TITRE_PERMIS = initialiseLazyLB();
		return LB_TITRE_PERMIS;
	}

	/**
	 * Setter de la liste: LB_TITRE_PERMIS Date de création : (14/09/11
	 * 15:57:59)
	 * 
	 */
	private void setLB_TITRE_PERMIS(String[] newLB_TITRE_PERMIS) {
		LB_TITRE_PERMIS = newLB_TITRE_PERMIS;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_PERMIS Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_TITRE_PERMIS() {
		return "NOM_LB_TITRE_PERMIS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_PERMIS_SELECT Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_TITRE_PERMIS_SELECT() {
		return "NOM_LB_TITRE_PERMIS_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_PERMIS Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String[] getVAL_LB_TITRE_PERMIS() {
		return getLB_TITRE_PERMIS();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_PERMIS Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_LB_TITRE_PERMIS_SELECT() {
		return getZone(getNOM_LB_TITRE_PERMIS_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	private String[] getLB_SPECIALITE() {
		if (LB_SPECIALITE == null)
			LB_SPECIALITE = initialiseLazyLB();
		return LB_SPECIALITE;
	}

	/**
	 * Setter de la liste: LB_SPECIALITE Date de création : (14/09/11 15:57:59)
	 * 
	 */
	private void setLB_SPECIALITE(String[] newLB_SPECIALITE) {
		LB_SPECIALITE = newLB_SPECIALITE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SPECIALITE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_SPECIALITE() {
		return "NOM_LB_SPECIALITE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_SPECIALITE_SELECT Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_LB_SPECIALITE_SELECT() {
		return "NOM_LB_SPECIALITE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_SPECIALITE Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String[] getVAL_LB_SPECIALITE() {
		return getLB_SPECIALITE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_SPECIALITE Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getVAL_LB_SPECIALITE_SELECT() {
		return getZone(getNOM_LB_SPECIALITE_SELECT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NIV_ETUDE Date de
	 * création : (14/09/11 16:15:50)
	 * 
	 */
	public String getNOM_EF_NIV_ETUDE() {
		return "NOM_EF_NIV_ETUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NIV_ETUDE Date de création : (14/09/11 16:15:50)
	 * 
	 */
	public String getVAL_EF_NIV_ETUDE() {
		return getZone(getNOM_EF_NIV_ETUDE());
	}

	private TitreDiplome getDiplomeCourant() {
		return diplomeCourant;
	}

	private void setDiplomeCourant(TitreDiplome diplomeCourant) {
		this.diplomeCourant = diplomeCourant;
	}

	private ArrayList<TitreDiplome> getListeDiplome() {
		return listeDiplome;
	}

	private void setListeDiplome(ArrayList<TitreDiplome> listeDiplome) {
		this.listeDiplome = listeDiplome;
	}

	private ArrayList<SpecialiteDiplome> getListeSpecialite() {
		return listeSpecialite;
	}

	private void setListeSpecialite(ArrayList<SpecialiteDiplome> listeSpecialite) {
		this.listeSpecialite = listeSpecialite;
	}

	private SpecialiteDiplome getSpecialiteCourante() {
		return specialiteCourante;
	}

	private void setSpecialiteCourante(SpecialiteDiplome specialiteCourante) {
		this.specialiteCourante = specialiteCourante;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_ADMIN
			if (testerParametre(request, getNOM_PB_ANNULER_ADMIN())) {
				return performPB_ANNULER_ADMIN(request);
			}

			// Si clic sur le bouton PB_CREER_ADMIN
			if (testerParametre(request, getNOM_PB_CREER_ADMIN())) {
				return performPB_CREER_ADMIN(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ADMIN
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ADMIN())) {
				return performPB_SUPPRIMER_ADMIN(request);
			}

			// Si clic sur le bouton PB_VALIDER_ADMIN
			if (testerParametre(request, getNOM_PB_VALIDER_ADMIN())) {
				return performPB_VALIDER_ADMIN(request);
			}

			// Si clic sur le bouton PB_ANNULER_DIPLOME
			if (testerParametre(request, getNOM_PB_ANNULER_DIPLOME())) {
				return performPB_ANNULER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_ANNULER_SPECIALITE
			if (testerParametre(request, getNOM_PB_ANNULER_SPECIALITE())) {
				return performPB_ANNULER_SPECIALITE(request);
			}

			// Si clic sur le bouton PB_CREER_DIPLOME
			if (testerParametre(request, getNOM_PB_CREER_DIPLOME())) {
				return performPB_CREER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_CREER_SPECIALITE
			if (testerParametre(request, getNOM_PB_CREER_SPECIALITE())) {
				return performPB_CREER_SPECIALITE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DIPLOME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DIPLOME())) {
				return performPB_SUPPRIMER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_SPECIALITE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_SPECIALITE())) {
				return performPB_SUPPRIMER_SPECIALITE(request);
			}

			// Si clic sur le bouton PB_VALIDER_DIPLOME
			if (testerParametre(request, getNOM_PB_VALIDER_DIPLOME())) {
				return performPB_VALIDER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_VALIDER_SPECIALITE
			if (testerParametre(request, getNOM_PB_VALIDER_SPECIALITE())) {
				return performPB_VALIDER_SPECIALITE(request);
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

			// Si clic sur le bouton PB_MODIFIER_DIPLOME
			if (testerParametre(request, getNOM_PB_MODIFIER_DIPLOME())) {
				return performPB_MODIFIER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_ANNULER_CENTRE_FORMATION
			if (testerParametre(request, getNOM_PB_ANNULER_CENTRE_FORMATION())) {
				return performPB_ANNULER_CENTRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_CREER_CENTRE_FORMATION
			if (testerParametre(request, getNOM_PB_CREER_CENTRE_FORMATION())) {
				return performPB_CREER_CENTRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CENTRE_FORMATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CENTRE_FORMATION())) {
				return performPB_SUPPRIMER_CENTRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_CENTRE_FORMATION
			if (testerParametre(request, getNOM_PB_VALIDER_CENTRE_FORMATION())) {
				return performPB_VALIDER_CENTRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_MODIFIER_CENTRE_FORMATION
			if (testerParametre(request, getNOM_PB_MODIFIER_CENTRE_FORMATION())) {
				return performPB_MODIFIER_CENTRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_TITRE_FORMATION
			if (testerParametre(request, getNOM_PB_ANNULER_TITRE_FORMATION())) {
				return performPB_ANNULER_TITRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_CREER_TITRE_FORMATION
			if (testerParametre(request, getNOM_PB_CREER_TITRE_FORMATION())) {
				return performPB_CREER_TITRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TITRE_FORMATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TITRE_FORMATION())) {
				return performPB_SUPPRIMER_TITRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_TITRE_FORMATION
			if (testerParametre(request, getNOM_PB_VALIDER_TITRE_FORMATION())) {
				return performPB_VALIDER_TITRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_MODIFIER_TITRE_FORMATION
			if (testerParametre(request, getNOM_PB_MODIFIER_TITRE_FORMATION())) {
				return performPB_MODIFIER_TITRE_FORMATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_TITRE_PERMIS
			if (testerParametre(request, getNOM_PB_ANNULER_TITRE_PERMIS())) {
				return performPB_ANNULER_TITRE_PERMIS(request);
			}

			// Si clic sur le bouton PB_CREER_TITRE_PERMIS
			if (testerParametre(request, getNOM_PB_CREER_TITRE_PERMIS())) {
				return performPB_CREER_TITRE_PERMIS(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TITRE_PERMIS
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TITRE_PERMIS())) {
				return performPB_SUPPRIMER_TITRE_PERMIS(request);
			}

			// Si clic sur le bouton PB_VALIDER_TITRE_PERMIS
			if (testerParametre(request, getNOM_PB_VALIDER_TITRE_PERMIS())) {
				return performPB_VALIDER_TITRE_PERMIS(request);
			}

			// Si clic sur le bouton PB_MODIFIER_TITRE_PERMIS
			if (testerParametre(request, getNOM_PB_MODIFIER_TITRE_PERMIS())) {
				return performPB_MODIFIER_TITRE_PERMIS(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEDonneesPerso.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-AG-DP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ADMIN Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_ANNULER_ADMIN() {
		return "NOM_PB_ANNULER_ADMIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_ANNULER_ADMIN(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ADMIN(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ADMIN Date de création
	 * : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_CREER_ADMIN() {
		return "NOM_PB_CREER_ADMIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_CREER_ADMIN(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_ADMIN(), ACTION_CREATION);
		addZone(getNOM_EF_ADMIN(), Const.CHAINE_VIDE);

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
	public boolean performPB_CREER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_TYPE_DOCUMENT(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ADMIN Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ADMIN() {
		return "NOM_PB_SUPPRIMER_ADMIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ADMIN(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_ADMIN_SELECT()) ? Integer.parseInt(getVAL_LB_ADMIN_SELECT()) : -1);

		if (indice != -1 && indice < getListeAdmin().size()) {
			AutreAdministration admin = getListeAdmin().get(indice);
			setAdminCourante(admin);
			addZone(getNOM_EF_ADMIN(), admin.getLibAutreAdmin());
			addZone(getNOM_ST_ACTION_ADMIN(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "autres administration"));
		}

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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ADMIN Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_VALIDER_ADMIN() {
		return "NOM_PB_VALIDER_ADMIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_VALIDER_ADMIN(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieAdmin(request))
			return false;

		if (!performControlerRegleGestionAdmin(request))
			return false;

		if (getVAL_ST_ACTION_ADMIN() != null && getVAL_ST_ACTION_ADMIN() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ADMIN().equals(ACTION_CREATION)) {
				setAdminCourante(new AutreAdministration());
				getAdminCourante().setLibAutreAdmin(getVAL_EF_ADMIN());
				getAdminCourante().creerAutreAdministration(getTransaction());
				if (!getTransaction().isErreur())
					getListeAdmin().add(getAdminCourante());
				else
					return false;
			} else if (getVAL_ST_ACTION_ADMIN().equals(ACTION_SUPPRESSION)) {
				getAdminCourante().supprimerAutreAdministration(getTransaction());
				if (!getTransaction().isErreur())
					getListeAdmin().remove(getAdminCourante());
				else
					return false;
				setAdminCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeAdmin(request);
			addZone(getNOM_ST_ACTION_ADMIN(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une autre administration Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieAdmin(HttpServletRequest request) throws Exception {

		// Verification libellé autre administration not null
		if (getZone(getNOM_EF_ADMIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
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
	private boolean performControlerRegleGestionAdmin(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de diplome utilisé sur un
		// diplome d'agent
		if (getVAL_ST_ACTION_ADMIN().equals(ACTION_SUPPRESSION)
				&& AutreAdministrationAgent.listerAutreAdministrationAgentAvecAutreAdministration(getTransaction(),
						getAdminCourante()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un agent", "cette administration"));
			return false;
		}

		// Vérification des contraintes d'unicité du titre de diplome
		if (getVAL_ST_ACTION_ADMIN().equals(ACTION_CREATION)) {

			for (AutreAdministration admin : getListeAdmin()) {
				if (admin.getLibAutreAdmin().equals(getVAL_EF_ADMIN().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une administration", "ce libellé"));
					return false;
				}
			}
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ADMIN Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_ST_ACTION_ADMIN() {
		return "NOM_ST_ACTION_ADMIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_ADMIN
	 * Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_ST_ACTION_ADMIN() {
		return getZone(getNOM_ST_ACTION_ADMIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ADMIN Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_EF_ADMIN() {
		return "NOM_EF_ADMIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ADMIN Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_EF_ADMIN() {
		return getZone(getNOM_EF_ADMIN());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ADMIN Date de création :
	 * (15/09/11 14:14:43)
	 * 
	 */
	private String[] getLB_ADMIN() {
		if (LB_ADMIN == null)
			LB_ADMIN = initialiseLazyLB();
		return LB_ADMIN;
	}

	/**
	 * Setter de la liste: LB_ADMIN Date de création : (15/09/11 14:14:43)
	 * 
	 */
	private void setLB_ADMIN(String[] newLB_ADMIN) {
		LB_ADMIN = newLB_ADMIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ADMIN Date de création :
	 * (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_LB_ADMIN() {
		return "NOM_LB_ADMIN";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ADMIN_SELECT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_LB_ADMIN_SELECT() {
		return "NOM_LB_ADMIN_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ADMIN Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String[] getVAL_LB_ADMIN() {
		return getLB_ADMIN();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ADMIN Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_LB_ADMIN_SELECT() {
		return getZone(getNOM_LB_ADMIN_SELECT());
	}

	private AutreAdministration getAdminCourante() {
		return adminCourante;
	}

	private void setAdminCourante(AutreAdministration adminCourante) {
		this.adminCourante = adminCourante;
	}

	private ArrayList<AutreAdministration> getListeAdmin() {
		return listeAdmin;
	}

	private void setListeAdmin(ArrayList<AutreAdministration> listeAdmin) {
		this.listeAdmin = listeAdmin;
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
				getTypeDocumentCourant().setModuleTypeDocument("DONNEES PERSONNELLES");
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
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_DIPLOME Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_DIPLOME() {
		return "NOM_PB_MODIFIER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_DIPLOME(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_DIPLOME_SELECT()) ? Integer.parseInt(getVAL_LB_DIPLOME_SELECT())
				: -1);

		if (indice != -1 && indice < getListeDiplome().size()) {
			TitreDiplome diplome = getListeDiplome().get(indice);
			setDiplomeCourant(diplome);
			addZone(getNOM_EF_DIPLOME(), diplome.getLibTitreDiplome());
			addZone(getNOM_EF_NIV_ETUDE(), diplome.getNiveauEtude());
			addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres diplômes"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_TITRE_PERMIS Date
	 * de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_TITRE_PERMIS() {
		return "NOM_PB_MODIFIER_TITRE_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_TITRE_PERMIS(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_TITRE_PERMIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_TITRE_PERMIS_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitrePermis().size()) {
			TitrePermis titre = getListeTitrePermis().get(indice);
			setTitrePermisCourant(titre);
			addZone(getNOM_EF_TITRE_PERMIS(), titre.getLibPermis());
			addZone(getNOM_ST_ACTION_TITRE_PERMIS(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de permis"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_TITRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_TITRE_FORMATION() {
		return "NOM_PB_MODIFIER_TITRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_TITRE_FORMATION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_TITRE_FORMATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_TITRE_FORMATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitreFormation().size()) {
			TitreFormation titre = getListeTitreFormation().get(indice);
			setTitreFormationCourant(titre);
			addZone(getNOM_EF_TITRE_FORMATION(), titre.getLibTitreFormation());
			addZone(getNOM_ST_ACTION_TITRE_FORMATION(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de formation"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_CENTRE_FORMATION
	 * Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_CENTRE_FORMATION() {
		return "NOM_PB_MODIFIER_CENTRE_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_CENTRE_FORMATION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_CENTRE_FORMATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_CENTRE_FORMATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeCentreFormation().size()) {
			CentreFormation centre = getListeCentreFormation().get(indice);
			setCentreFormationCourant(centre);
			addZone(getNOM_EF_CENTRE_FORMATION(), centre.getLibCentreFormation());
			addZone(getNOM_ST_ACTION_CENTRE_FORMATION(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "centres de formation"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<TitrePermis> getListeTitrePermis() {
		return listeTitrePermis;
	}

	public void setListeTitrePermis(ArrayList<TitrePermis> listeTitrePermis) {
		this.listeTitrePermis = listeTitrePermis;
	}

	public TitrePermis getTitrePermisCourant() {
		return titrePermisCourant;
	}

	public void setTitrePermisCourant(TitrePermis titrePermisCourant) {
		this.titrePermisCourant = titrePermisCourant;
	}

	public ArrayList<TitreFormation> getListeTitreFormation() {
		return listeTitreFormation;
	}

	public void setListeTitreFormation(ArrayList<TitreFormation> listeTitreFormation) {
		this.listeTitreFormation = listeTitreFormation;
	}

	public TitreFormation getTitreFormationCourant() {
		return titreFormationCourant;
	}

	public void setTitreFormationCourant(TitreFormation titreFormationCourant) {
		this.titreFormationCourant = titreFormationCourant;
	}

	public ArrayList<CentreFormation> getListeCentreFormation() {
		return listeCentreFormation;
	}

	public void setListeCentreFormation(ArrayList<CentreFormation> listeCentreFormation) {
		this.listeCentreFormation = listeCentreFormation;
	}

	public CentreFormation getCentreFormationCourant() {
		return centreFormationCourant;
	}

	public void setCentreFormationCourant(CentreFormation centreFormationCourant) {
		this.centreFormationCourant = centreFormationCourant;
	}

	public TitreFormationDao getTitreFormationDao() {
		return titreFormationDao;
	}

	public void setTitreFormationDao(TitreFormationDao titreFormationDao) {
		this.titreFormationDao = titreFormationDao;
	}

	public CentreFormationDao getCentreFormationDao() {
		return centreFormationDao;
	}

	public void setCentreFormationDao(CentreFormationDao centreFormationDao) {
		this.centreFormationDao = centreFormationDao;
	}

	public FormationAgentDao getFormationAgentDao() {
		return formationAgentDao;
	}

	public void setFormationAgentDao(FormationAgentDao formationAgentDao) {
		this.formationAgentDao = formationAgentDao;
	}

	public TitrePermisDao getTitrePermisDao() {
		return titrePermisDao;
	}

	public void setTitrePermisDao(TitrePermisDao titrePermisDao) {
		this.titrePermisDao = titrePermisDao;
	}

	public PermisAgentDao getPermisAgentDao() {
		return permisAgentDao;
	}

	public void setPermisAgentDao(PermisAgentDao permisAgentDao) {
		this.permisAgentDao = permisAgentDao;
	}

	public SpecialiteDiplomeDao getSpecialiteDiplomeDao() {
		return specialiteDiplomeDao;
	}

	public void setSpecialiteDiplomeDao(SpecialiteDiplomeDao specialiteDiplomeDao) {
		this.specialiteDiplomeDao = specialiteDiplomeDao;
	}

	public TitreDiplomeDao getTitreDiplomeDao() {
		return titreDiplomeDao;
	}

	public void setTitreDiplomeDao(TitreDiplomeDao titreDiplomeDao) {
		this.titreDiplomeDao = titreDiplomeDao;
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}
}
