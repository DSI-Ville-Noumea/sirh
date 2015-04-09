package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Categorie;
import nc.mairie.metier.carriere.Classe;
import nc.mairie.metier.carriere.Echelon;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.Deliberation;
import nc.mairie.spring.dao.metier.carriere.CategorieDao;
import nc.mairie.spring.dao.metier.parametrage.CadreEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.DeliberationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGEGradeRef Date de création : (29/09/11 15:07:35)
 * 
 */
public class OePARAMETRAGEGradeRef extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_BAREME;
	private String[] LB_CLASSE;
	private String[] LB_ECHELON;
	private String[] LB_GRADE_GENERIQUE;
	private String[] LB_CATEGORIE;
	private String[] LB_CADRE_EMPLOI_GRADE;
	private String[] LB_CADRE_EMPLOI;
	private String[] LB_FILIERE;
	private String[] LB_DELIB_TERR_GRADE;
	private String[] LB_DELIB_COMM_GRADE;

	private ArrayList<Classe> listeClasse;
	private Classe classeCourante;

	private ArrayList<Echelon> listeEchelon;
	private Echelon echelonCourant;

	private ArrayList<Bareme> listeBareme;
	private Bareme baremeCourant;

	private ArrayList<GradeGenerique> listeGradeGenerique;
	private GradeGenerique gradeGeneriqueCourant;

	private ArrayList<Categorie> listeCategorie;
	private Hashtable<String, Categorie> hashCategorie;

	private ArrayList<CadreEmploi> listeCadreEmploi;
	private Hashtable<String, CadreEmploi> hashCadreEmploi;
	private CadreEmploi cadreEmploiCourant;

	private ArrayList<FiliereGrade> listeFiliere;
	private Hashtable<String, FiliereGrade> hashFiliere;

	private DeliberationDao deliberationDao;
	private CadreEmploiDao cadreEmploiDao;

	private ArrayList<Deliberation> listeDeliberationTerr;
	private ArrayList<Deliberation> listeDeliberationComm;
	private Hashtable<String, Deliberation> hashDeliberationComm;
	private Hashtable<String, Deliberation> hashDeliberationTerr;

	public String ACTION_CREATION = "0";
	public String ACTION_MODIFICATION = "1";
	public String ACTION_SUPPRESSION = "2";

	private CategorieDao categorieDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (29/09/11 15:07:35)
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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		if (getListeBareme() == null) {
			initialiseListeBareme(request);
		}

		if (getListeClasse() == null) {
			initialiseListeClasse(request);
		}

		if (getListeEchelon() == null) {
			initialiseListeEchelon(request);
		}

		if (getListeGradeGenerique() == null) {
			initialiseListeGradeGenerique(request);
		}

		if (getListeCategorie() == null)
			initialiseListeCategorie(request);

		if (getListeFiliere() == null)
			initialiseListeFiliere(request);

		if (getListeCadreEmploi() == null)
			initialiseListeCadreEmploi(request);

		if (getListeDeliberationComm() == null)
			initialiseListeDeliberationComm(request);

		if (getListeDeliberationTerr() == null)
			initialiseListeDeliberationTerr(request);

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getDeliberationDao() == null) {
			setDeliberationDao(new DeliberationDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCadreEmploiDao() == null) {
			setCadreEmploiDao(new CadreEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCategorieDao() == null) {
			setCategorieDao(new CategorieDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeDeliberationComm(HttpServletRequest request) throws Exception {

		int tailles[] = { 10 };
		String padding[] = { "G" };

		ArrayList<Deliberation> listeDelibComm = getDeliberationDao().listerDeliberationCommunale();
		setListeDeliberationComm(listeDelibComm);
		if (getListeDeliberationComm().size() != 0) {
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Deliberation> list = getListeDeliberationComm().listIterator(); list.hasNext();) {
				Deliberation delibComm = (Deliberation) list.next();

				String ligne[] = { delibComm.getCodeDeliberation() };

				aFormat.ajouteLigne(ligne);
			}

			setLB_DELIB_COMM_GRADE(aFormat.getListeFormatee(true));

		} else {
			setLB_DELIB_COMM_GRADE(null);
		}

		// remplissage de la hashTable
		for (Deliberation delib : getListeDeliberationComm())
			getHashDeliberationComm().put(delib.getIdDeliberation().toString(), delib);

	}

	private void initialiseListeDeliberationTerr(HttpServletRequest request) throws Exception {

		int tailles[] = { 10 };
		String padding[] = { "G" };

		ArrayList<Deliberation> listeDelibTerr = getDeliberationDao().listerDeliberationTerritoriale();
		setListeDeliberationTerr(listeDelibTerr);
		if (getListeDeliberationTerr().size() != 0) {
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Deliberation> list = getListeDeliberationTerr().listIterator(); list.hasNext();) {
				Deliberation delibTerr = (Deliberation) list.next();

				String ligne[] = { delibTerr.getCodeDeliberation() };

				aFormat.ajouteLigne(ligne);
			}

			setLB_DELIB_TERR_GRADE(aFormat.getListeFormatee(true));

		} else {
			setLB_DELIB_TERR_GRADE(null);
		}

		// remplissage de la hashTable
		for (Deliberation delib : getListeDeliberationTerr())
			getHashDeliberationTerr().put(delib.getIdDeliberation().toString(), delib);

	}

	/**
	 * Initialisation de la liste des classe Date de création : (29/09/11)
	 * 
	 */
	private void initialiseListeClasse(HttpServletRequest request) throws Exception {
		setListeClasse(Classe.listerClasse(getTransaction()));
		if (getListeClasse().size() != 0) {
			int tailles[] = { 2, 60 };
			String padding[] = { "C", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (Classe classe : getListeClasse()) {
				String ligne[] = { classe.getCodClasse(), classe.getLibClasse() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_CLASSE(aFormat.getListeFormatee());
		} else {
			setLB_CLASSE(null);
		}
	}

	/**
	 * Initialisation de la liste des échelons Date de création : (29/09/11)
	 * 
	 */
	private void initialiseListeEchelon(HttpServletRequest request) throws Exception {
		setListeEchelon(Echelon.listerEchelon(getTransaction()));
		if (getListeEchelon().size() != 0) {
			int tailles[] = { 3, 60 };
			String padding[] = { "C", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (Echelon echelon : getListeEchelon()) {
				String ligne[] = { echelon.getCodEchelon(), echelon.getLibEchelon() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ECHELON(aFormat.getListeFormatee());
		} else {
			setLB_ECHELON(null);
		}
	}

	/**
	 * Initialisation de la liste des barêmes Date de création : (29/09/11)
	 * 
	 */
	private void initialiseListeBareme(HttpServletRequest request) throws Exception {
		setListeBareme(Bareme.listerBareme(getTransaction()));
		if (getListeBareme().size() != 0) {
			int tailles[] = { 13, 10, 10 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (Bareme bareme : getListeBareme()) {
				String ligne[] = { bareme.getIban(), bareme.getIna(), bareme.getInm() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_BAREME(aFormat.getListeFormatee());
		} else {
			setLB_BAREME(null);
		}
	}

	/**
	 * Initialisation de la liste des grades génériques Date de création :
	 * (29/09/11)
	 * 
	 */
	private void initialiseListeGradeGenerique(HttpServletRequest request) throws Exception {
		setListeGradeGenerique(GradeGenerique.listerGradeGenerique(getTransaction()));
		if (getListeGradeGenerique().size() != 0) {
			int tailles[] = { 5, 6, 5, 50 };
			String padding[] = { "G", "C", "C", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (GradeGenerique gradeGenerique : getListeGradeGenerique()) {
				String ligne[] = { gradeGenerique.getCdgeng(), gradeGenerique.getCodCadre(),
						gradeGenerique.getCodeInactif(), gradeGenerique.getLibGradeGenerique() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_GRADE_GENERIQUE(aFormat.getListeFormatee());
		} else {
			setLB_GRADE_GENERIQUE(null);
		}
	}

	/**
	 * Initialisation de la liste des categories
	 * 
	 */
	private void initialiseListeCategorie(HttpServletRequest request) throws Exception {
		ArrayList<Categorie> liste = getCategorieDao().listerCategorie();
		setListeCategorie(liste);
		if (getListeCategorie().size() != 0) {
			int tailles[] = { 2 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Categorie> list = getListeCategorie().listIterator(); list.hasNext();) {
				Categorie cat = (Categorie) list.next();
				String ligne[] = { cat.getLibCategorieStatut() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CATEGORIE(aFormat.getListeFormatee(true));
		} else {
			setLB_CATEGORIE(null);
		}

		// remplissage de la hashTable
		for (Categorie categorie : liste)
			getHashCategorie().put(categorie.getLibCategorieStatut(), categorie);
	}

	private void initialiseListeFiliere(HttpServletRequest request) throws Exception {
		ArrayList<FiliereGrade> liste = FiliereGrade.listerFiliereGrade(getTransaction());
		setListeFiliere(liste);

		int[] tailles = { 40 };
		String[] champs = { "libFiliere" };
		setLB_FILIERE(new FormateListe(tailles, liste, champs).getListeFormatee(true));

		// remplissage de la hashTable
		for (FiliereGrade filiere : liste)
			getHashFiliere().put(filiere.getCodeFiliere(), filiere);
	}

	/**
	 * Initialisation de la liste des cadres emploi Date de création :
	 * (09/09/11)
	 * 
	 */
	private void initialiseListeCadreEmploi(HttpServletRequest request) throws Exception {
		setListeCadreEmploi(getCadreEmploiDao().listerCadreEmploi());
		if (getListeCadreEmploi().size() != 0) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<CadreEmploi> list = getListeCadreEmploi().listIterator(); list.hasNext();) {
				CadreEmploi ce = (CadreEmploi) list.next();

				String ligne[] = { ce.getLibCadreEmploi() };

				aFormat.ajouteLigne(ligne);
			}

			// remplissage de la hashTable
			for (CadreEmploi cadreEmp : getListeCadreEmploi())
				getHashCadreEmploi().put(cadreEmp.getIdCadreEmploi().toString(), cadreEmp);

			setLB_CADRE_EMPLOI(aFormat.getListeFormatee());

			// pour l'affichage du cadre emploi dans la gestion des grades
			// génériques

			int taillesCadre[] = { 40 };
			String paddingCadre[] = { "G" };
			FormateListe aFormatCadre = new FormateListe(taillesCadre, paddingCadre, false);
			for (ListIterator<CadreEmploi> list = getListeCadreEmploi().listIterator(); list.hasNext();) {
				CadreEmploi ce = (CadreEmploi) list.next();

				String ligne[] = { ce.getLibCadreEmploi() };

				aFormatCadre.ajouteLigne(ligne);
			}
			setLB_CADRE_EMPLOI_GRADE(aFormatCadre.getListeFormatee(true));
		} else {
			setLB_CADRE_EMPLOI(null);
			setLB_CADRE_EMPLOI_GRADE(null);
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_ANNULER_BAREME() {
		return "NOM_PB_ANNULER_BAREME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_ANNULER_BAREME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_BAREME(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CLASSE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_ANNULER_CLASSE() {
		return "NOM_PB_ANNULER_CLASSE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_ANNULER_CLASSE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CLASSE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ECHELON Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_ANNULER_ECHELON() {
		return "NOM_PB_ANNULER_ECHELON";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_ANNULER_ECHELON(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ECHELON(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_GRADE_GENERIQUE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_ANNULER_GRADE_GENERIQUE() {
		return "NOM_PB_ANNULER_GRADE_GENERIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_ANNULER_GRADE_GENERIQUE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_GRADE_GENERIQUE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_CREER_BAREME() {
		return "NOM_PB_CREER_BAREME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_CREER_BAREME(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_BAREME(), ACTION_CREATION);
		addZone(getNOM_EF_IBAN_BAREME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_INA_BAREME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_INM_BAREME(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CLASSE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_CREER_CLASSE() {
		return "NOM_PB_CREER_CLASSE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_CREER_CLASSE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_CLASSE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_CLASSE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIBELLE_CLASSE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ECHELON Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_CREER_ECHELON() {
		return "NOM_PB_CREER_ECHELON";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_CREER_ECHELON(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_ECHELON(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_ECHELON(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIBELLE_ECHELON(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_GRADE_GENERIQUE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_CREER_GRADE_GENERIQUE() {
		return "NOM_PB_CREER_GRADE_GENERIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_CREER_GRADE_GENERIQUE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_GRADE_GENERIQUE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_GRADE_GENERIQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_CATEGORIE_SELECT(), Const.ZERO);
		addZone(getNOM_EF_LIBELLE_GRADE_GENERIQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_PTS_CATEGORIE(), Const.ZERO);
		addZone(getNOM_RG_INACTIF(), getNOM_RB_NON());
		addZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_FILIERE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_DELIB_COMM_GRADE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_DELIB_TERR_GRADE_SELECT(), Const.ZERO);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_VALIDER_BAREME() {
		return "NOM_PB_VALIDER_BAREME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_VALIDER_BAREME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieBareme(request))
			return false;

		if (getVAL_ST_ACTION_BAREME().equals(ACTION_CREATION)) {

			String iban = getVAL_EF_IBAN_BAREME();
			if (Services.estNumerique(iban.substring(0, 1)))
				iban = Services.lpad(iban, 7, "0");

			setBaremeCourant(new Bareme());
			getBaremeCourant().setIban(iban);
			getBaremeCourant().setIna(getVAL_EF_INA_BAREME());
			getBaremeCourant().setInm(getVAL_EF_INM_BAREME());
		} else if (getVAL_ST_ACTION_BAREME().equals(ACTION_MODIFICATION)) {
			getBaremeCourant().setIna(getVAL_EF_INA_BAREME());
			getBaremeCourant().setInm(getVAL_EF_INM_BAREME());
		}

		if (!performControlerRegleGestionBareme(request))
			return false;

		if (getVAL_ST_ACTION_BAREME() != null && getVAL_ST_ACTION_BAREME() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_BAREME().equals(ACTION_CREATION)) {
				getBaremeCourant().creerBareme(getTransaction());
				if (!getTransaction().isErreur())
					getListeBareme().add(getBaremeCourant());
			} else if (getVAL_ST_ACTION_BAREME().equals(ACTION_MODIFICATION)) {
				getBaremeCourant().modifierBareme(getTransaction());
				if (!getTransaction().isErreur())
					getListeBareme().remove(getBaremeCourant());
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeBareme(request);
			setBaremeCourant(null);
			addZone(getNOM_ST_ACTION_BAREME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un Bareme Date de création : (09/09/11)
	 */
	private boolean performControlerSaisieBareme(HttpServletRequest request) throws Exception {

		// Verification iba not null
		if (getZone(getNOM_EF_IBAN_BAREME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "IBAN"));
			return false;
		}

		// Verification ina not null
		if (getZone(getNOM_EF_INA_BAREME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "INA"));
			return false;
		}
		// Verification ina numerique
		if (!Services.estNumerique(getVAL_EF_INA_BAREME())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "INA"));
			return false;
		}

		// Verification inm not null
		if (getZone(getNOM_EF_INM_BAREME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "INM"));
			return false;
		}
		// Verification inm numerique
		if (!Services.estNumerique(getVAL_EF_INM_BAREME())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "INM"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un Bareme Date de création : (09/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionBareme(HttpServletRequest request) throws Exception {

		// Vérification des contraintes d'unicité du bareme
		if (getVAL_ST_ACTION_BAREME().equals(ACTION_CREATION)) {

			for (Bareme bareme : getListeBareme()) {
				if (bareme.getIban().equals(getBaremeCourant().getIban())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un barême", "ce code IBA"));
					return false;
				}
			}
		}

		// coherence déinferiorité et/ou de supériorité avec les indices
		// suivants ou précédents
		if (Services.estNumerique(getVAL_EF_IBAN_BAREME())) {

			Bareme baremePrev = null;
			Bareme baremeNext = null;
			int i = 0;

			for (; i < getListeBareme().size(); i++) {

				baremePrev = baremeNext;
				baremeNext = getListeBareme().get(i);

				if (Services.estNumerique(baremeNext.getIban())
						&& Integer.parseInt(baremeNext.getIban()) > Integer.parseInt(getVAL_EF_IBAN_BAREME())) {
					if (baremePrev.getIban().equals(getVAL_EF_IBAN_BAREME())) {
						if (i - 2 >= 0)
							baremePrev = getListeBareme().get(i - 2);
						else
							baremePrev = null;
					}

					break;
				}
			}

			if (i == getListeBareme().size()) {
				baremeNext = null;
			}

			if (baremePrev != null && Services.estNumerique(baremePrev.getIban())) {

				if (Integer.parseInt(baremePrev.getIna()) > Integer.parseInt(getVAL_EF_INA_BAREME())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR140"));
					return false;
				}
				if (Integer.parseInt(baremePrev.getInm()) > Integer.parseInt(getVAL_EF_INM_BAREME())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR140"));
					return false;
				}
			}

			if (baremeNext != null && Services.estNumerique(baremeNext.getIban())) {
				if (Integer.parseInt(baremeNext.getIna()) < Integer.parseInt(getVAL_EF_INA_BAREME())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR140"));
					return false;
				}
				if (Integer.parseInt(baremeNext.getInm()) < Integer.parseInt(getVAL_EF_INM_BAREME())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR140"));
					return false;
				}
			}

		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CLASSE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_VALIDER_CLASSE() {
		return "NOM_PB_VALIDER_CLASSE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_VALIDER_CLASSE(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_CLASSE() != null && getVAL_ST_ACTION_CLASSE() != Const.CHAINE_VIDE) {

			if (!performControlerSaisieClasse(request))
				return false;

			if (getVAL_ST_ACTION_CLASSE().equals(ACTION_CREATION))
				setClasseCourante(new Classe());
			getClasseCourante().setCodClasse(getVAL_EF_CODE_CLASSE());
			getClasseCourante().setLibClasse(getVAL_EF_LIBELLE_CLASSE());

			if (!performControlerRegleGestionClasse(request))
				return false;

			if (getVAL_ST_ACTION_CLASSE().equals(ACTION_CREATION))
				getClasseCourante().creerClasse(getTransaction());
			else
				getClasseCourante().modifierClasse(getTransaction());

			if (!getTransaction().isErreur()) {
				if (getVAL_ST_ACTION_CLASSE().equals(ACTION_CREATION))
					getListeClasse().add(getClasseCourante());
			} else
				return false;

			commitTransaction();
			initialiseListeClasse(request);
			addZone(getNOM_ST_ACTION_CLASSE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un Classe Date de création : (09/09/11)
	 */
	private boolean performControlerSaisieClasse(HttpServletRequest request) throws Exception {

		// Verification lib not null
		if (getZone(getNOM_EF_LIBELLE_CLASSE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code not null
		if (getZone(getNOM_EF_CODE_CLASSE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un Classe Date de création : (09/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionClasse(HttpServletRequest request) throws Exception {

		// verif conrainte unicité classe
		if (getVAL_ST_ACTION_CLASSE().equals(ACTION_CREATION) || getVAL_ST_ACTION_CLASSE().equals(ACTION_MODIFICATION)) {

			for (Classe classe : getListeClasse()) {

				if (!classe.equals(getClasseCourante())) {
					if (classe.getLibClasse().equals(getVAL_EF_LIBELLE_CLASSE().toUpperCase())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une classe", "ce libellé"));
						return false;
					}
					if (classe.getCodClasse().equals(getVAL_EF_CODE_CLASSE().toUpperCase())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une classe", "ce code"));
						return false;
					}
				}

			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ECHELON Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_VALIDER_ECHELON() {
		return "NOM_PB_VALIDER_ECHELON";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_VALIDER_ECHELON(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_ECHELON() != null && getVAL_ST_ACTION_ECHELON() != Const.CHAINE_VIDE) {

			if (!performControlerSaisieEchelon(request))
				return false;

			if (getVAL_ST_ACTION_ECHELON().equals(ACTION_CREATION))
				setEchelonCourant(new Echelon());

			getEchelonCourant().setCodEchelon(getVAL_EF_CODE_ECHELON());
			getEchelonCourant().setLibEchelon(getVAL_EF_LIBELLE_ECHELON());

			if (!performControlerRegleGestionEchelon(request))
				return false;

			if (getVAL_ST_ACTION_ECHELON().equals(ACTION_CREATION)) {
				getEchelonCourant().creerEchelon(getTransaction());
			} else {
				getEchelonCourant().modifierEchelon(getTransaction());
			}

			if (!getTransaction().isErreur()) {
				if (getVAL_ST_ACTION_ECHELON().equals(ACTION_CREATION))
					getListeEchelon().add(getEchelonCourant());
			} else
				return false;

			commitTransaction();
			initialiseListeEchelon(request);
			addZone(getNOM_ST_ACTION_ECHELON(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un Echelon Date de création : (09/09/11)
	 */
	private boolean performControlerSaisieEchelon(HttpServletRequest request) throws Exception {

		// Verification lib not null
		if (getZone(getNOM_EF_LIBELLE_ECHELON()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code not null
		if (getZone(getNOM_EF_CODE_ECHELON()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un Echelon Date de création : (09/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionEchelon(HttpServletRequest request) throws Exception {

		// verif conrainte unicité échelon
		if (getVAL_ST_ACTION_ECHELON().equals(ACTION_CREATION)
				|| getVAL_ST_ACTION_ECHELON().equals(ACTION_MODIFICATION)) {

			for (Echelon echelon : getListeEchelon()) {

				if (!echelon.equals(getEchelonCourant())) {
					if (echelon.getLibEchelon().equals(getVAL_EF_LIBELLE_ECHELON().toUpperCase())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un echelon", "ce libellé"));
						return false;
					}

					if (echelon.getCodEchelon().equals(getVAL_EF_CODE_ECHELON().toUpperCase())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un echelon", "ce code"));
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_GRADE_GENERIQUE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_PB_VALIDER_GRADE_GENERIQUE() {
		return "NOM_PB_VALIDER_GRADE_GENERIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean performPB_VALIDER_GRADE_GENERIQUE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieGradeGenerique(request))
			return false;

		if (!performControlerRegleGestionGradeGenerique(request))
			return false;

		int numCategorie = (Services.estNumerique(getZone(getNOM_LB_CATEGORIE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_CATEGORIE_SELECT())) : -1);
		Categorie categorie = numCategorie > 0 ? (Categorie) getListeCategorie().get(numCategorie - 1) : null;

		int numFiliere = (Services.estNumerique(getZone(getNOM_LB_FILIERE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FILIERE_SELECT())) : -1);
		FiliereGrade filiere = numFiliere > 0 ? (FiliereGrade) getListeFiliere().get(numFiliere - 1) : null;

		int numCadreEmp = (Services.estNumerique(getZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT())) : -1);
		CadreEmploi cadreEmp = numCadreEmp > 0 ? (CadreEmploi) getListeCadreEmploi().get(numCadreEmp - 1) : null;

		int numDelibTerr = (Services.estNumerique(getZone(getNOM_LB_DELIB_TERR_GRADE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_DELIB_TERR_GRADE_SELECT())) : -1);
		Deliberation delibTerr = numDelibTerr > 0 ? (Deliberation) getListeDeliberationTerr().get(numDelibTerr - 1)
				: null;

		int numDelibComm = (Services.estNumerique(getZone(getNOM_LB_DELIB_COMM_GRADE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_DELIB_COMM_GRADE_SELECT())) : -1);
		Deliberation delibComm = numDelibComm > 0 ? (Deliberation) getListeDeliberationComm().get(numDelibComm - 1)
				: null;

		if (getVAL_ST_ACTION_GRADE_GENERIQUE() != null && getVAL_ST_ACTION_GRADE_GENERIQUE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_GRADE_GENERIQUE().equals(ACTION_CREATION)) {

				Boolean inactif = getZone(getNOM_RG_INACTIF()).equals(getNOM_RB_OUI());
				setGradeGeneriqueCourant(new GradeGenerique());
				getGradeGeneriqueCourant().setCdgeng(getVAL_EF_CODE_GRADE_GENERIQUE());
				getGradeGeneriqueCourant().setCodCadre(
						categorie != null ? categorie.getLibCategorieStatut() : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setLibGradeGenerique(getVAL_EF_LIBELLE_GRADE_GENERIQUE());
				getGradeGeneriqueCourant().setCodeInactif(inactif ? "I" : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setNbPointsAvct(getVAL_EF_NB_PTS_CATEGORIE());
				getGradeGeneriqueCourant().setIdCadreEmploi(
						cadreEmp != null ? cadreEmp.getIdCadreEmploi().toString() : null);
				getGradeGeneriqueCourant().setCdfili(filiere != null ? filiere.getCodeFiliere() : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setTexteCapCadreEmploi(getVAL_EF_TEXTE_CAP_GRADE_GENERIQUE());
				getGradeGeneriqueCourant().setIdDeliberationTerritoriale(
						delibTerr == null ? null : delibTerr.getIdDeliberation().toString());
				getGradeGeneriqueCourant().setIdDeliberationCommunale(
						delibComm == null ? null : delibComm.getIdDeliberation().toString());
				getGradeGeneriqueCourant().creerGradeGenerique(getTransaction());
				if (!getTransaction().isErreur())
					getListeGradeGenerique().add(getGradeGeneriqueCourant());
			} else if (getVAL_ST_ACTION_GRADE_GENERIQUE().equals(ACTION_MODIFICATION)) {
				Boolean inactif = getZone(getNOM_RG_INACTIF()).equals(getNOM_RB_OUI());
				getGradeGeneriqueCourant().setCodCadre(
						categorie != null ? categorie.getLibCategorieStatut() : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setLibGradeGenerique(getVAL_EF_LIBELLE_GRADE_GENERIQUE());
				getGradeGeneriqueCourant().setCodeInactif(inactif ? "I" : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setNbPointsAvct(getVAL_EF_NB_PTS_CATEGORIE());
				getGradeGeneriqueCourant().setIdCadreEmploi(
						cadreEmp != null ? cadreEmp.getIdCadreEmploi().toString() : null);
				getGradeGeneriqueCourant().setCdfili(filiere != null ? filiere.getCodeFiliere() : Const.CHAINE_VIDE);
				getGradeGeneriqueCourant().setTexteCapCadreEmploi(getVAL_EF_TEXTE_CAP_GRADE_GENERIQUE());
				getGradeGeneriqueCourant().setIdDeliberationTerritoriale(
						delibTerr == null ? null : delibTerr.getIdDeliberation().toString());
				getGradeGeneriqueCourant().setIdDeliberationCommunale(
						delibComm == null ? null : delibComm.getIdDeliberation().toString());
				// si le gradeGenerique est inactif/actif alors on
				// désactive/active tous les sous grades lies.
				if (!mettreAjourActiveGrille(getGradeGeneriqueCourant(), inactif)) {
					// "ERR145",
					// "Impossible d'activer/désactiver des grilles associées au grade @. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR145", getGradeGeneriqueCourant().getCdgeng()));

					return false;

				}
				getGradeGeneriqueCourant().modifierGradeGenerique(getTransaction());
				if (!getTransaction().isErreur())
					getListeGradeGenerique().remove(getGradeGeneriqueCourant());
				setGradeGeneriqueCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeGradeGenerique(request);
			addZone(getNOM_ST_ACTION_GRADE_GENERIQUE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	private boolean mettreAjourActiveGrille(GradeGenerique gradeGenerique, Boolean inactif) throws Exception {
		// on cherche tous les grades associés
		ArrayList<Grade> listeGrade = Grade.listerGradeAvecGradeGeneriqueEtGrade(getTransaction(),
				gradeGenerique.getCdgeng(), gradeGenerique.getLibGradeGenerique());
		// si la liste est vide alors message d'erreur
		if (listeGrade.size() == 0 || getTransaction().isErreur()) {
			return false;
		}
		for (Grade gr : listeGrade) {
			gr.setCodeActif(inactif ? "I" : "A");
			gr.modifierGrade(getTransaction());

		}
		return true;
	}

	/**
	 * Controle les zones saisies d'un GradeGenerique Date de création :
	 * (09/09/11)
	 */
	private boolean performControlerSaisieGradeGenerique(HttpServletRequest request) throws Exception {

		// Verification lib not null
		if (getZone(getNOM_EF_LIBELLE_GRADE_GENERIQUE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code not null
		if (getZone(getNOM_EF_CODE_GRADE_GENERIQUE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		// Verification nb points avancement not null
		if (getZone(getNOM_EF_NB_PTS_CATEGORIE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nombre de points"));
			return false;
		}

		// Contrôle format nb points avancements
		if (!Services.estNumerique(getZone(getNOM_EF_NB_PTS_CATEGORIE()))) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nombre de points"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un GradeGenerique Date de création :
	 * (09/09/11 11:04:00)
	 */
	private boolean performControlerRegleGestionGradeGenerique(HttpServletRequest request) throws Exception {

		// verif conrainte unicité grade générique
		if (getVAL_ST_ACTION_GRADE_GENERIQUE().equals(ACTION_CREATION)) {
			// seulement pour la création car ces champs ne sont pas modifiable
			// en modification

			for (GradeGenerique gradeGenerique : getListeGradeGenerique()) {

				if (gradeGenerique.getLibGradeGenerique().equals(getVAL_EF_LIBELLE_GRADE_GENERIQUE().toUpperCase())) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un grade générique", "ce libellé"));
					return false;
				}

				if (gradeGenerique.getCdgeng().equals(getVAL_EF_CODE_GRADE_GENERIQUE().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un grade générique", "ce code"));
					return false;
				}
			}
		} else if (getVAL_ST_ACTION_GRADE_GENERIQUE().equals(ACTION_MODIFICATION)) {
			for (GradeGenerique gradeGenerique : getListeGradeGenerique()) {

				if (!gradeGenerique.getCdgeng().equals(getVAL_EF_CODE_GRADE_GENERIQUE().toUpperCase())) {
					if (gradeGenerique.getLibGradeGenerique().equals(getVAL_EF_LIBELLE_GRADE_GENERIQUE().toUpperCase())) {
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR974", "un grade générique", "ce libellé"));
						return false;
					}
				}

			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_BAREME Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_ST_ACTION_BAREME() {
		return "NOM_ST_ACTION_BAREME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_BAREME
	 * Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_ST_ACTION_BAREME() {
		return getZone(getNOM_ST_ACTION_BAREME());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_CLASSE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_ST_ACTION_CLASSE() {
		return "NOM_ST_ACTION_CLASSE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_CLASSE
	 * Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_ST_ACTION_CLASSE() {
		return getZone(getNOM_ST_ACTION_CLASSE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ECHELON Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_ST_ACTION_ECHELON() {
		return "NOM_ST_ACTION_ECHELON";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_ECHELON
	 * Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_ST_ACTION_ECHELON() {
		return getZone(getNOM_ST_ACTION_ECHELON());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_ST_ACTION_GRADE_GENERIQUE() {
		return "NOM_ST_ACTION_GRADE_GENERIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_ST_ACTION_GRADE_GENERIQUE() {
		return getZone(getNOM_ST_ACTION_GRADE_GENERIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_CADRE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_CODE_CADRE() {
		return "NOM_EF_CODE_CADRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_CADRE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_CODE_CADRE() {
		return getZone(getNOM_EF_CODE_CADRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_CLASSE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_CODE_CLASSE() {
		return "NOM_EF_CODE_CLASSE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_CLASSE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_CODE_CLASSE() {
		return getZone(getNOM_EF_CODE_CLASSE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_ECHELON Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_CODE_ECHELON() {
		return "NOM_EF_CODE_ECHELON";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_ECHELON Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_CODE_ECHELON() {
		return getZone(getNOM_EF_CODE_ECHELON());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_FILIERE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_CODE_FILIERE() {
		return "NOM_EF_CODE_FILIERE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_FILIERE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_CODE_FILIERE() {
		return getZone(getNOM_EF_CODE_FILIERE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_CODE_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_CODE_GRADE_GENERIQUE() {
		return "NOM_EF_CODE_GRADE_GENERIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_CODE_GRADE_GENERIQUE() {
		return getZone(getNOM_EF_CODE_GRADE_GENERIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_TEXTE_CAP_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_TEXTE_CAP_GRADE_GENERIQUE() {
		return "NOM_EF_TEXTE_CAP_GRADE_GENERIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TEXTE_CAP_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_TEXTE_CAP_GRADE_GENERIQUE() {
		return getZone(getNOM_EF_TEXTE_CAP_GRADE_GENERIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_IBAN_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_IBAN_BAREME() {
		return "NOM_EF_IBAN_BAREME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_IBAN_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_IBAN_BAREME() {
		return getZone(getNOM_EF_IBAN_BAREME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_INA_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_INA_BAREME() {
		return "NOM_EF_INA_BAREME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_INA_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_INA_BAREME() {
		return getZone(getNOM_EF_INA_BAREME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_INM_BAREME Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_INM_BAREME() {
		return "NOM_EF_INM_BAREME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_INM_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_INM_BAREME() {
		return getZone(getNOM_EF_INM_BAREME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIBELLE_CLASSE Date
	 * de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_LIBELLE_CLASSE() {
		return "NOM_EF_LIBELLE_CLASSE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_CLASSE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_LIBELLE_CLASSE() {
		return getZone(getNOM_EF_LIBELLE_CLASSE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIBELLE_ECHELON
	 * Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_LIBELLE_ECHELON() {
		return "NOM_EF_LIBELLE_ECHELON";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_ECHELON Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_LIBELLE_ECHELON() {
		return getZone(getNOM_EF_LIBELLE_ECHELON());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_LIBELLE_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_EF_LIBELLE_GRADE_GENERIQUE() {
		return "NOM_EF_LIBELLE_GRADE_GENERIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_EF_LIBELLE_GRADE_GENERIQUE() {
		return getZone(getNOM_EF_LIBELLE_GRADE_GENERIQUE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BAREME Date de création :
	 * (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_BAREME() {
		if (LB_BAREME == null)
			LB_BAREME = initialiseLazyLB();
		return LB_BAREME;
	}

	/**
	 * Setter de la liste: LB_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	private void setLB_BAREME(String[] newLB_BAREME) {
		LB_BAREME = newLB_BAREME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BAREME Date de création :
	 * (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_BAREME() {
		return "NOM_LB_BAREME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BAREME_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_BAREME_SELECT() {
		return "NOM_LB_BAREME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_BAREME() {
		return getLB_BAREME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BAREME Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_BAREME_SELECT() {
		return getZone(getNOM_LB_BAREME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CLASSE Date de création :
	 * (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_CLASSE() {
		if (LB_CLASSE == null)
			LB_CLASSE = initialiseLazyLB();
		return LB_CLASSE;
	}

	/**
	 * Setter de la liste: LB_CLASSE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	private void setLB_CLASSE(String[] newLB_CLASSE) {
		LB_CLASSE = newLB_CLASSE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CLASSE Date de création :
	 * (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_CLASSE() {
		return "NOM_LB_CLASSE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CLASSE_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_CLASSE_SELECT() {
		return "NOM_LB_CLASSE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CLASSE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_CLASSE() {
		return getLB_CLASSE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_CLASSE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_CLASSE_SELECT() {
		return getZone(getNOM_LB_CLASSE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ECHELON Date de création
	 * : (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_ECHELON() {
		if (LB_ECHELON == null)
			LB_ECHELON = initialiseLazyLB();
		return LB_ECHELON;
	}

	/**
	 * Setter de la liste: LB_ECHELON Date de création : (29/09/11 15:07:35)
	 * 
	 */
	private void setLB_ECHELON(String[] newLB_ECHELON) {
		LB_ECHELON = newLB_ECHELON;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ECHELON Date de création
	 * : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_ECHELON() {
		return "NOM_LB_ECHELON";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ECHELON_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_ECHELON_SELECT() {
		return "NOM_LB_ECHELON_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ECHELON Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_ECHELON() {
		return getLB_ECHELON();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ECHELON Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_ECHELON_SELECT() {
		return getZone(getNOM_LB_ECHELON_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GRADE_GENERIQUE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_GRADE_GENERIQUE() {
		if (LB_GRADE_GENERIQUE == null)
			LB_GRADE_GENERIQUE = initialiseLazyLB();
		return LB_GRADE_GENERIQUE;
	}

	/**
	 * Setter de la liste: LB_GRADE_GENERIQUE Date de création : (29/09/11
	 * 15:07:35)
	 * 
	 */
	private void setLB_GRADE_GENERIQUE(String[] newLB_GRADE_GENERIQUE) {
		LB_GRADE_GENERIQUE = newLB_GRADE_GENERIQUE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GRADE_GENERIQUE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_GRADE_GENERIQUE() {
		return "NOM_LB_GRADE_GENERIQUE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GRADE_GENERIQUE_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_GRADE_GENERIQUE_SELECT() {
		return "NOM_LB_GRADE_GENERIQUE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_GRADE_GENERIQUE() {
		return getLB_GRADE_GENERIQUE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_GRADE_GENERIQUE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_GRADE_GENERIQUE_SELECT() {
		return getZone(getNOM_LB_GRADE_GENERIQUE_SELECT());
	}

	private Bareme getBaremeCourant() {
		return baremeCourant;
	}

	private void setBaremeCourant(Bareme baremeCourant) {
		this.baremeCourant = baremeCourant;
	}

	private Classe getClasseCourante() {
		return classeCourante;
	}

	private void setClasseCourante(Classe classeCourante) {
		this.classeCourante = classeCourante;
	}

	private Echelon getEchelonCourant() {
		return echelonCourant;
	}

	private void setEchelonCourant(Echelon echelonCourant) {
		this.echelonCourant = echelonCourant;
	}

	private GradeGenerique getGradeGeneriqueCourant() {
		return gradeGeneriqueCourant;
	}

	private void setGradeGeneriqueCourant(GradeGenerique gradeGeneriqueCourant) {
		this.gradeGeneriqueCourant = gradeGeneriqueCourant;
	}

	private ArrayList<Bareme> getListeBareme() {
		return listeBareme;
	}

	private void setListeBareme(ArrayList<Bareme> listeBareme) {
		this.listeBareme = listeBareme;
	}

	private ArrayList<Classe> getListeClasse() {
		return listeClasse;
	}

	private void setListeClasse(ArrayList<Classe> listeClasse) {
		this.listeClasse = listeClasse;
	}

	private ArrayList<Echelon> getListeEchelon() {
		return listeEchelon;
	}

	private void setListeEchelon(ArrayList<Echelon> listeEchelon) {
		this.listeEchelon = listeEchelon;
	}

	private ArrayList<GradeGenerique> getListeGradeGenerique() {
		return listeGradeGenerique;
	}

	private void setListeGradeGenerique(ArrayList<GradeGenerique> listeGradeGenerique) {
		this.listeGradeGenerique = listeGradeGenerique;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_BAREME Date de
	 * création : (30/09/11 08:47:54)
	 * 
	 */
	public String getNOM_PB_MODIFIER_BAREME() {
		return "NOM_PB_MODIFIER_BAREME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (30/09/11 08:47:54)
	 * 
	 */
	public boolean performPB_MODIFIER_BAREME(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_BAREME_SELECT()) ? Integer.parseInt(getVAL_LB_BAREME_SELECT())
				: -1);

		if (indice != -1 && indice < getListeBareme().size()) {
			Bareme bareme = getListeBareme().get(indice);
			setBaremeCourant(bareme);
			addZone(getNOM_EF_IBAN_BAREME(), bareme.getIban());
			addZone(getNOM_EF_INA_BAREME(), bareme.getIna());
			addZone(getNOM_EF_INM_BAREME(), bareme.getInm());
			addZone(getNOM_ST_ACTION_BAREME(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "barêmes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_GRADE_GENERIQUE
	 * Date de création : (30/09/11 08:47:54)
	 * 
	 */
	public String getNOM_PB_MODIFIER_GRADE_GENERIQUE() {
		return "NOM_PB_MODIFIER_GRADE_GENERIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (30/09/11 08:47:54)
	 * 
	 */
	public boolean performPB_MODIFIER_GRADE_GENERIQUE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_GRADE_GENERIQUE_SELECT()) ? Integer
				.parseInt(getVAL_LB_GRADE_GENERIQUE_SELECT()) : -1);

		if (indice != -1 && indice < getListeGradeGenerique().size()) {
			GradeGenerique grade = getListeGradeGenerique().get(indice);

			Categorie categorie = (Categorie) getHashCategorie().get(grade.getCodCadre());
			int ligneCategorie = getListeCategorie().indexOf(categorie);
			addZone(getNOM_LB_CATEGORIE_SELECT(), String.valueOf(ligneCategorie + 1));

			// filiere
			if (grade.getCdfili() != null) {
				FiliereGrade filiere = (FiliereGrade) getHashFiliere().get(grade.getCdfili());
				int ligneFiliere = getListeFiliere().indexOf(filiere);
				addZone(getNOM_LB_FILIERE_SELECT(), String.valueOf(ligneFiliere + 1));
			} else {
				addZone(getNOM_LB_FILIERE_SELECT(), Const.ZERO);
			}

			if (grade.getIdCadreEmploi() != null) {
				CadreEmploi cadreEmp = (CadreEmploi) getHashCadreEmploi().get(grade.getIdCadreEmploi());
				int ligneCadreEmp = getListeCadreEmploi().indexOf(cadreEmp);
				addZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT(), String.valueOf(ligneCadreEmp + 1));
			} else {
				addZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT(), Const.ZERO);
			}

			// delib communale
			if (grade.getIdDeliberationCommunale() != null) {
				Deliberation delibComm = (Deliberation) getHashDeliberationComm().get(
						grade.getIdDeliberationCommunale());
				int ligneDelibComm = getListeDeliberationComm().indexOf(delibComm);
				addZone(getNOM_LB_DELIB_COMM_GRADE_SELECT(), String.valueOf(ligneDelibComm + 1));
			} else {
				addZone(getNOM_LB_DELIB_COMM_GRADE_SELECT(), Const.ZERO);
			}

			// delib territoriale
			if (grade.getIdDeliberationTerritoriale() != null) {
				Deliberation delibTerr = (Deliberation) getHashDeliberationTerr().get(
						grade.getIdDeliberationTerritoriale());
				int ligneDelibTerr = getListeDeliberationTerr().indexOf(delibTerr);
				addZone(getNOM_LB_DELIB_TERR_GRADE_SELECT(), String.valueOf(ligneDelibTerr + 1));
			} else {
				addZone(getNOM_LB_DELIB_TERR_GRADE_SELECT(), Const.ZERO);
			}

			setGradeGeneriqueCourant(grade);
			addZone(getNOM_EF_LIBELLE_GRADE_GENERIQUE(), grade.getLibGradeGenerique());
			addZone(getNOM_EF_CODE_GRADE_GENERIQUE(), grade.getCdgeng());
			addZone(getNOM_EF_NB_PTS_CATEGORIE(), grade.getNbPointsAvct());
			addZone(getNOM_EF_TEXTE_CAP_GRADE_GENERIQUE(), grade.getTexteCapCadreEmploi());
			addZone(getNOM_ST_ACTION_GRADE_GENERIQUE(), ACTION_MODIFICATION);
			if (grade.getCodeInactif().equals("I"))
				addZone(getNOM_RG_INACTIF(), getNOM_RB_OUI());
			else
				addZone(getNOM_RG_INACTIF(), getNOM_RB_NON());
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "grades génériques"));
		}

		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_INACTIF
	 * Date de création : (30/09/11 09:03:07)
	 * 
	 */
	public String getNOM_RG_INACTIF() {
		return "NOM_RG_INACTIF";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_INACTIF
	 * Date de création : (30/09/11 09:03:07)
	 * 
	 */
	public String getVAL_RG_INACTIF() {
		return getZone(getNOM_RG_INACTIF());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NON Date de création :
	 * (30/09/11 09:03:07)
	 * 
	 */
	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_OUI Date de création :
	 * (30/09/11 09:03:07)
	 * 
	 */
	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	/**
	 * Constructeur du process OePARAMETRAGEGradeRef. Date de création :
	 * (30/09/11 09:50:04)
	 * 
	 */
	public OePARAMETRAGEGradeRef() {
		super();
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-GRADE-REFERENCE";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CADRE Date de création :
	 * (30/09/11 09:50:04)
	 * 
	 */
	private String[] getLB_CATEGORIE() {
		if (LB_CATEGORIE == null)
			LB_CATEGORIE = initialiseLazyLB();
		return LB_CATEGORIE;
	}

	/**
	 * Setter de la liste: LB_CATEGORIE Date de création : (30/09/11 09:50:04)
	 * 
	 */
	private void setLB_CATEGORIE(String[] newLB_CATEGORIE) {
		LB_CATEGORIE = newLB_CATEGORIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CATEGORIE Date de
	 * création : (30/09/11 09:50:04)
	 * 
	 */
	public String getNOM_LB_CATEGORIE() {
		return "NOM_LB_CATEGORIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CATEGORIE_SELECT Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String getNOM_LB_CATEGORIE_SELECT() {
		return "NOM_LB_CATEGORIE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CATEGORIE Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String[] getVAL_LB_CATEGORIE() {
		return getLB_CATEGORIE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_CATEGORIE Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String getVAL_LB_CATEGORIE_SELECT() {
		return getZone(getNOM_LB_CATEGORIE_SELECT());
	}

	private ArrayList<Categorie> getListeCategorie() {
		return listeCategorie;
	}

	private void setListeCategorie(ArrayList<Categorie> listeCategorie) {
		this.listeCategorie = listeCategorie;
	}

	private Hashtable<String, Categorie> getHashCategorie() {
		if (hashCategorie == null)
			hashCategorie = new Hashtable<String, Categorie>();
		return hashCategorie;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_MODIFIER_CLASSE
			if (testerParametre(request, getNOM_PB_MODIFIER_CLASSE())) {
				return performPB_MODIFIER_CLASSE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ECHELON
			if (testerParametre(request, getNOM_PB_MODIFIER_ECHELON())) {
				return performPB_MODIFIER_ECHELON(request);
			}

			// Si clic sur le bouton PB_MODIFIER_BAREME
			if (testerParametre(request, getNOM_PB_MODIFIER_BAREME())) {
				return performPB_MODIFIER_BAREME(request);
			}

			// Si clic sur le bouton PB_MODIFIER_GRADE_GENERIQUE
			if (testerParametre(request, getNOM_PB_MODIFIER_GRADE_GENERIQUE())) {
				return performPB_MODIFIER_GRADE_GENERIQUE(request);
			}

			// Si clic sur le bouton PB_ANNULER_BAREME
			if (testerParametre(request, getNOM_PB_ANNULER_BAREME())) {
				return performPB_ANNULER_BAREME(request);
			}

			// Si clic sur le bouton PB_ANNULER_CLASSE
			if (testerParametre(request, getNOM_PB_ANNULER_CLASSE())) {
				return performPB_ANNULER_CLASSE(request);
			}

			// Si clic sur le bouton PB_ANNULER_ECHELON
			if (testerParametre(request, getNOM_PB_ANNULER_ECHELON())) {
				return performPB_ANNULER_ECHELON(request);
			}

			// Si clic sur le bouton PB_ANNULER_GRADE_GENERIQUE
			if (testerParametre(request, getNOM_PB_ANNULER_GRADE_GENERIQUE())) {
				return performPB_ANNULER_GRADE_GENERIQUE(request);
			}

			// Si clic sur le bouton PB_CREER_BAREME
			if (testerParametre(request, getNOM_PB_CREER_BAREME())) {
				return performPB_CREER_BAREME(request);
			}

			// Si clic sur le bouton PB_CREER_CLASSE
			if (testerParametre(request, getNOM_PB_CREER_CLASSE())) {
				return performPB_CREER_CLASSE(request);
			}

			// Si clic sur le bouton PB_CREER_ECHELON
			if (testerParametre(request, getNOM_PB_CREER_ECHELON())) {
				return performPB_CREER_ECHELON(request);
			}

			// Si clic sur le bouton PB_CREER_GRADE_GENERIQUE
			if (testerParametre(request, getNOM_PB_CREER_GRADE_GENERIQUE())) {
				return performPB_CREER_GRADE_GENERIQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER_BAREME
			if (testerParametre(request, getNOM_PB_VALIDER_BAREME())) {
				return performPB_VALIDER_BAREME(request);
			}

			// Si clic sur le bouton PB_VALIDER_CLASSE
			if (testerParametre(request, getNOM_PB_VALIDER_CLASSE())) {
				return performPB_VALIDER_CLASSE(request);
			}

			// Si clic sur le bouton PB_VALIDER_ECHELON
			if (testerParametre(request, getNOM_PB_VALIDER_ECHELON())) {
				return performPB_VALIDER_ECHELON(request);
			}

			// Si clic sur le bouton PB_VALIDER_GRADE_GENERIQUE
			if (testerParametre(request, getNOM_PB_VALIDER_GRADE_GENERIQUE())) {
				return performPB_VALIDER_GRADE_GENERIQUE(request);
			}

			// Si clic sur le bouton PB_ANNULER_CADRE
			if (testerParametre(request, getNOM_PB_ANNULER_CADRE_EMPLOI())) {
				return performPB_ANNULER_CADRE_EMPLOI(request);
			}

			// Si clic sur le bouton PB_CREER_CADRE
			if (testerParametre(request, getNOM_PB_CREER_CADRE_EMPLOI())) {
				return performPB_CREER_CADRE_EMPLOI(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CADRE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CADRE_EMPLOI())) {
				return performPB_SUPPRIMER_CADRE_EMPLOI(request);
			}

			// Si clic sur le bouton PB_VALIDER_CADRE
			if (testerParametre(request, getNOM_PB_VALIDER_CADRE_EMPLOI())) {
				return performPB_VALIDER_CADRE_EMPLOI(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (02/11/11 10:13:39)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEGradeRef.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_CLASSE Date de
	 * création : (02/11/11 10:13:39)
	 * 
	 */
	public String getNOM_PB_MODIFIER_CLASSE() {
		return "NOM_PB_MODIFIER_CLASSE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/11/11 10:13:39)
	 * 
	 */
	public boolean performPB_MODIFIER_CLASSE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_CLASSE_SELECT()) ? Integer.parseInt(getVAL_LB_CLASSE_SELECT())
				: -1);

		if (indice != -1 && indice < getListeClasse().size()) {
			Classe classe = getListeClasse().get(indice);
			setClasseCourante(classe);
			addZone(getNOM_EF_CODE_CLASSE(), classe.getCodClasse());
			addZone(getNOM_EF_LIBELLE_CLASSE(), classe.getLibClasse());
			addZone(getNOM_ST_ACTION_CLASSE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "classes"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_ECHELON Date de
	 * création : (02/11/11 10:13:39)
	 * 
	 */
	public String getNOM_PB_MODIFIER_ECHELON() {
		return "NOM_PB_MODIFIER_ECHELON";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/11/11 10:13:39)
	 * 
	 */
	public boolean performPB_MODIFIER_ECHELON(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ECHELON_SELECT()) ? Integer.parseInt(getVAL_LB_ECHELON_SELECT())
				: -1);

		if (indice != -1 && indice < getListeEchelon().size()) {
			Echelon echelon = getListeEchelon().get(indice);
			setEchelonCourant(echelon);
			addZone(getNOM_EF_CODE_ECHELON(), echelon.getCodEchelon());
			addZone(getNOM_EF_LIBELLE_ECHELON(), echelon.getLibEchelon());
			addZone(getNOM_ST_ACTION_ECHELON(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "échelons"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_PTS_CATEGORIE
	 * Date de création : (12/09/11 16:28:59)
	 * 
	 */
	public String getNOM_EF_NB_PTS_CATEGORIE() {
		return "NOM_EF_NB_PTS_CATEGORIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NB_PTS_CATEGORIE Date de création : (12/09/11 16:28:59)
	 * 
	 */
	public String getVAL_EF_NB_PTS_CATEGORIE() {
		return getZone(getNOM_EF_NB_PTS_CATEGORIE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CADRE_EMPLOI Date de
	 * création : (30/09/11 09:50:04)
	 * 
	 */
	private String[] getLB_CADRE_EMPLOI_GRADE() {
		if (LB_CADRE_EMPLOI_GRADE == null)
			LB_CADRE_EMPLOI_GRADE = initialiseLazyLB();
		return LB_CADRE_EMPLOI_GRADE;
	}

	/**
	 * Setter de la liste: LB_CADRE_EMPLOI Date de création : (30/09/11
	 * 09:50:04)
	 * 
	 */
	private void setLB_CADRE_EMPLOI_GRADE(String[] newLB_CADRE_EMPLOI_GRADE) {
		LB_CADRE_EMPLOI_GRADE = newLB_CADRE_EMPLOI_GRADE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CADRE_EMPLOI Date de
	 * création : (30/09/11 09:50:04)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_GRADE() {
		return "NOM_LB_CADRE_EMPLOI_GRADE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CADRE_EMPLOI_SELECT Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_GRADE_SELECT() {
		return "NOM_LB_CADRE_EMPLOI_GRADE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CADRE_EMPLOI Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String[] getVAL_LB_CADRE_EMPLOI_GRADE() {
		return getLB_CADRE_EMPLOI_GRADE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_CADRE_EMPLOI Date de création : (30/09/11 09:50:04)
	 * 
	 */
	public String getVAL_LB_CADRE_EMPLOI_GRADE_SELECT() {
		return getZone(getNOM_LB_CADRE_EMPLOI_GRADE_SELECT());
	}

	private Hashtable<String, CadreEmploi> getHashCadreEmploi() {
		if (hashCadreEmploi == null)
			hashCadreEmploi = new Hashtable<String, CadreEmploi>();
		return hashCadreEmploi;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACTION_CADRE Date
	 * de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_EF_ACTION_CADRE_EMPLOI() {
		return "NOM_EF_ACTION_CADRE_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACTION_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_EF_ACTION_CADRE_EMPLOI() {
		return getZone(getNOM_EF_ACTION_CADRE_EMPLOI());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CADRE Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_EF_CADRE_EMPLOI() {
		return "NOM_EF_CADRE_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_EF_CADRE_EMPLOI() {
		return getZone(getNOM_EF_CADRE_EMPLOI());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CADRE Date de création :
	 * (09/09/11 13:36:47)
	 * 
	 */
	private String[] getLB_CADRE_EMPLOI() {
		if (LB_CADRE_EMPLOI == null)
			LB_CADRE_EMPLOI = initialiseLazyLB();
		return LB_CADRE_EMPLOI;
	}

	/**
	 * Setter de la liste: LB_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	private void setLB_CADRE_EMPLOI(String[] newLB_CADRE_EMPLOI) {
		LB_CADRE_EMPLOI = newLB_CADRE_EMPLOI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CADRE Date de création :
	 * (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI() {
		return "NOM_LB_CADRE_EMPLOI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CADRE_SELECT Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_SELECT() {
		return "NOM_LB_CADRE_EMPLOI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String[] getVAL_LB_CADRE_EMPLOI() {
		return getLB_CADRE_EMPLOI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_LB_CADRE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_CADRE_EMPLOI_SELECT());
	}

	private ArrayList<CadreEmploi> getListeCadreEmploi() {
		return listeCadreEmploi;
	}

	private void setListeCadreEmploi(ArrayList<CadreEmploi> listeCadreEmploi) {
		this.listeCadreEmploi = listeCadreEmploi;
	}

	private CadreEmploi getCadreEmploiCourant() {
		return cadreEmploiCourant;
	}

	private void setCadreEmploiCourant(CadreEmploi cadreEmploiCourant) {
		this.cadreEmploiCourant = cadreEmploiCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CADRE Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_ANNULER_CADRE_EMPLOI() {
		return "NOM_PB_ANNULER_CADRE_EMPLOI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_ANNULER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CADRE Date de création
	 * : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_CREER_CADRE_EMPLOI() {
		return "NOM_PB_CREER_CADRE_EMPLOI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_CREER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {

		addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), ACTION_CREATION);
		addZone(getNOM_EF_CADRE_EMPLOI(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_FILIERE_SELECT(), "0");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CADRE Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CADRE_EMPLOI() {
		return "NOM_PB_SUPPRIMER_CADRE_EMPLOI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_CADRE_EMPLOI_SELECT()) ? Integer
				.parseInt(getVAL_LB_CADRE_EMPLOI_SELECT()) : -1);

		if (indice != -1 && indice < getListeCadreEmploi().size()) {
			CadreEmploi c = getListeCadreEmploi().get(indice);
			setCadreEmploiCourant(c);

			addZone(getNOM_EF_CADRE_EMPLOI(), c.getLibCadreEmploi());
			addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "cadres emploi"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CADRE Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_VALIDER_CADRE_EMPLOI() {
		return "NOM_PB_VALIDER_CADRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_VALIDER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieCadreEmploi(request))
			return false;

		if (!performControlerRegleGestionCadreEmploi(request))
			return false;

		if (getVAL_EF_ACTION_CADRE_EMPLOI() != null && getVAL_EF_ACTION_CADRE_EMPLOI() != Const.CHAINE_VIDE) {
			if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_CREATION)) {
				setCadreEmploiCourant(new CadreEmploi());
				getCadreEmploiCourant().setLibCadreEmploi(getVAL_EF_CADRE_EMPLOI());
				getCadreEmploiDao().creerCadreEmploi(getCadreEmploiCourant().getLibCadreEmploi());
				if (!getTransaction().isErreur())
					getListeCadreEmploi().add(getCadreEmploiCourant());
			} else if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_SUPPRESSION)) {
				getCadreEmploiDao().supprimerCadreEmploi(getCadreEmploiCourant().getIdCadreEmploi());
				if (!getTransaction().isErreur())
					getListeCadreEmploi().remove(getCadreEmploiCourant());
				setCadreEmploiCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeCadreEmploi(request);
			addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un cadre emploi Date de création :
	 * (12/09/11)
	 */
	private boolean performControlerSaisieCadreEmploi(HttpServletRequest request) throws Exception {

		// ************************************
		// Verification lib cadre emploi not null
		// ************************************
		if (getZone(getNOM_EF_CADRE_EMPLOI()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'une filiere Date de création : (12/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionCadreEmploi(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un cadre emploi utilisee sur
		// un grade generique
		if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_SUPPRESSION)
				&& GradeGenerique.listerGradeGeneriqueAvecCadreEmploi(getTransaction(), getCadreEmploiCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un grade générique", "ce cadre emploi"));
			return false;
		}

		// Vérification des contraintes d'unicité du cadre emploi
		if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_CREATION)) {

			// Vérification des contraintes d'unicité du cadre emploi
			if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_CREATION)) {
				for (CadreEmploi cadre : getListeCadreEmploi()) {
					if (cadre.getLibCadreEmploi().equals(getVAL_EF_CADRE_EMPLOI().toUpperCase())) {
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR974", "un cadre emploi", "ce libellé"));
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FILIERE Date de création
	 * : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_FILIERE() {
		return "NOM_LB_FILIERE";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_FILIERE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_FILIERE() {
		return getLB_FILIERE();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_FILIERE Date de création
	 * : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_FILIERE() {
		if (LB_FILIERE == null)
			LB_FILIERE = initialiseLazyLB();
		return LB_FILIERE;
	}

	/**
	 * Setter de la liste: LB_FILIERE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_FILIERE(String[] newLB_FILIERE) {
		LB_FILIERE = newLB_FILIERE;
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_FILIERE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_FILIERE_SELECT() {
		return getZone(getNOM_LB_FILIERE_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FILIERE_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_FILIERE_SELECT() {
		return "NOM_LB_FILIERE_SELECT";
	}

	/**
	 * Retourne les ecoles dans une table de hashage Date de création :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, FiliereGrade> getHashFiliere() {
		if (hashFiliere == null) {
			hashFiliere = new Hashtable<String, FiliereGrade>();
		}
		return hashFiliere;
	}

	private ArrayList<FiliereGrade> getListeFiliere() {
		return listeFiliere;
	}

	private void setListeFiliere(ArrayList<FiliereGrade> listeFiliere) {
		this.listeFiliere = listeFiliere;
	}

	public ArrayList<Deliberation> getListeDeliberationTerr() {
		return listeDeliberationTerr;
	}

	public void setListeDeliberationTerr(ArrayList<Deliberation> listeDeliberationTerr) {
		this.listeDeliberationTerr = listeDeliberationTerr;
	}

	public ArrayList<Deliberation> getListeDeliberationComm() {
		return listeDeliberationComm;
	}

	public void setListeDeliberationComm(ArrayList<Deliberation> listeDeliberationComm) {
		this.listeDeliberationComm = listeDeliberationComm;
	}

	public DeliberationDao getDeliberationDao() {
		return deliberationDao;
	}

	public void setDeliberationDao(DeliberationDao deliberationDao) {
		this.deliberationDao = deliberationDao;
	}

	/**
	 * Retourne les déliberations dans une table de hashage Date de création :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, Deliberation> getHashDeliberationComm() {
		if (hashDeliberationComm == null) {
			hashDeliberationComm = new Hashtable<String, Deliberation>();
		}
		return hashDeliberationComm;
	}

	/**
	 * Retourne les déliberations dans une table de hashage Date de création :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, Deliberation> getHashDeliberationTerr() {
		if (hashDeliberationTerr == null) {
			hashDeliberationTerr = new Hashtable<String, Deliberation>();
		}
		return hashDeliberationTerr;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DELIB_TERR_GRADE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_DELIB_TERR_GRADE() {
		if (LB_DELIB_TERR_GRADE == null)
			LB_DELIB_TERR_GRADE = initialiseLazyLB();
		return LB_DELIB_TERR_GRADE;
	}

	/**
	 * Setter de la liste: LB_DELIB_TERR_GRADE Date de création : (29/09/11
	 * 15:07:35)
	 * 
	 */
	private void setLB_DELIB_TERR_GRADE(String[] newLB_DELIB_TERR_GRADE) {
		LB_DELIB_TERR_GRADE = newLB_DELIB_TERR_GRADE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DELIB_TERR_GRADE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_DELIB_TERR_GRADE() {
		return "NOM_LB_DELIB_TERR_GRADE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DELIB_TERR_GRADE_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_DELIB_TERR_GRADE_SELECT() {
		return "NOM_LB_DELIB_TERR_GRADE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DELIB_TERR_GRADE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_DELIB_TERR_GRADE() {
		return getLB_DELIB_TERR_GRADE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_DELIB_TERR_GRADE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_DELIB_TERR_GRADE_SELECT() {
		return getZone(getNOM_LB_DELIB_TERR_GRADE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DELIB_COMM_GRADE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	private String[] getLB_DELIB_COMM_GRADE() {
		if (LB_DELIB_COMM_GRADE == null)
			LB_DELIB_COMM_GRADE = initialiseLazyLB();
		return LB_DELIB_COMM_GRADE;
	}

	/**
	 * Setter de la liste: LB_DELIB_COMM_GRADE Date de création : (29/09/11
	 * 15:07:35)
	 * 
	 */
	private void setLB_DELIB_COMM_GRADE(String[] newLB_DELIB_COMM_GRADE) {
		LB_DELIB_COMM_GRADE = newLB_DELIB_COMM_GRADE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DELIB_COMM_GRADE Date de
	 * création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_DELIB_COMM_GRADE() {
		return "NOM_LB_DELIB_COMM_GRADE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DELIB_COMM_GRADE_SELECT Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getNOM_LB_DELIB_COMM_GRADE_SELECT() {
		return "NOM_LB_DELIB_COMM_GRADE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DELIB_COMM_GRADE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String[] getVAL_LB_DELIB_COMM_GRADE() {
		return getLB_DELIB_COMM_GRADE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_DELIB_COMM_GRADE Date de création : (29/09/11 15:07:35)
	 * 
	 */
	public String getVAL_LB_DELIB_COMM_GRADE_SELECT() {
		return getZone(getNOM_LB_DELIB_COMM_GRADE_SELECT());
	}

	public CadreEmploiDao getCadreEmploiDao() {
		return cadreEmploiDao;
	}

	public void setCadreEmploiDao(CadreEmploiDao cadreEmploiDao) {
		this.cadreEmploiDao = cadreEmploiDao;
	}

	public CategorieDao getCategorieDao() {
		return categorieDao;
	}

	public void setCategorieDao(CategorieDao categorieDao) {
		this.categorieDao = categorieDao;
	}
}
