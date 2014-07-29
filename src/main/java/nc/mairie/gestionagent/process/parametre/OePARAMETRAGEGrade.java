package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Classe;
import nc.mairie.metier.carriere.Echelon;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Process OePARAMETRAGEGrade Date de création : (04/10/11 09:04:41)
 * 
 */
public class OePARAMETRAGEGrade extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_CLASSE;
	private String[] LB_ECHELON;
	private String[] LB_GRADE_GENERIQUE;
	private String[] LB_MOTIF_AVCT;

	private ArrayList<Grade> listeGrade;
	private ArrayList<Grade> listeGrille;
	private ArrayList<GradeGenerique> listeGradeGenerique;
	private ArrayList<Echelon> listeEchelon;
	private ArrayList<MotifAvancement> listeMotifAvct;
	private ArrayList<Classe> listeClasse;
	private ArrayList<Bareme> listeBareme;

	private Hashtable<String, GradeGenerique> hashGradeGenerique;
	private Hashtable<String, Classe> hashClasse;
	private Hashtable<String, Echelon> hashEchelon;
	private Hashtable<String, MotifAvancement> hashMotifAvct;
	private Hashtable<String, Bareme> hashBareme;

	private Grade gradeCourant;
	private Grade grilleCourant;

	public String ACTION_CONSULTATION_GRILLE = "Consultation d'une grille";
	public String ACTION_CONSULTATION_GRADE = "Consultation d'un grade";
	public String ACTION_MODIFICATION_GRILLE = "Modification d'une grille";
	public String ACTION_CREATION_GRILLE = "Création d'une grille.";
	public String ACTION_CREATION_GRADE = "Création d'un grade.";
	public String ACTION_MODIFICATION_GRADE = "Modification d'un grade.";

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName((String) ServletAgent.getMesParametres().get(
			"DTAARA_SCHEMA"), (String) ServletAgent.getMesParametres().get("DTAARA_NAME"), "DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent
			.getMesParametres().get("HOST_SGBD_PAYE"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_ADMIN"),
			(String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")), CALC_PATH.getPath());
	private String calculPaye;

	private MotifAvancementDao motifAvancementDao;

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_GRADE Date de
	 * création : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_EF_GRADE() {
		return "NOM_EF_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_GRADE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getVAL_EF_GRADE() {
		return getZone(getNOM_EF_GRADE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CLASSE Date de création :
	 * (04/10/11 09:04:41)
	 * 
	 */
	private String[] getLB_CLASSE() {
		if (LB_CLASSE == null)
			LB_CLASSE = initialiseLazyLB();
		return LB_CLASSE;
	}

	/**
	 * Setter de la liste: LB_CLASSE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	private void setLB_CLASSE(String[] newLB_CLASSE) {
		LB_CLASSE = newLB_CLASSE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CLASSE Date de création :
	 * (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_CLASSE() {
		return "NOM_LB_CLASSE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CLASSE_SELECT Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_CLASSE_SELECT() {
		return "NOM_LB_CLASSE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CLASSE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String[] getVAL_LB_CLASSE() {
		return getLB_CLASSE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CLASSE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getVAL_LB_CLASSE_SELECT() {
		return getZone(getNOM_LB_CLASSE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ECHELON Date de création
	 * : (04/10/11 09:04:41)
	 * 
	 */
	private String[] getLB_ECHELON() {
		if (LB_ECHELON == null)
			LB_ECHELON = initialiseLazyLB();
		return LB_ECHELON;
	}

	/**
	 * Setter de la liste: LB_ECHELON Date de création : (04/10/11 09:04:41)
	 * 
	 */
	private void setLB_ECHELON(String[] newLB_ECHELON) {
		LB_ECHELON = newLB_ECHELON;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ECHELON Date de création
	 * : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_ECHELON() {
		return "NOM_LB_ECHELON";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ECHELON_SELECT Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_ECHELON_SELECT() {
		return "NOM_LB_ECHELON_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ECHELON Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String[] getVAL_LB_ECHELON() {
		return getLB_ECHELON();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ECHELON Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getVAL_LB_ECHELON_SELECT() {
		return getZone(getNOM_LB_ECHELON_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GRADE_GENERIQUE Date de
	 * création : (04/10/11 09:04:41)
	 * 
	 */
	private String[] getLB_GRADE_GENERIQUE() {
		if (LB_GRADE_GENERIQUE == null)
			LB_GRADE_GENERIQUE = initialiseLazyLB();
		return LB_GRADE_GENERIQUE;
	}

	/**
	 * Setter de la liste: LB_GRADE_GENERIQUE Date de création : (04/10/11
	 * 09:04:41)
	 * 
	 */
	private void setLB_GRADE_GENERIQUE(String[] newLB_GRADE_GENERIQUE) {
		LB_GRADE_GENERIQUE = newLB_GRADE_GENERIQUE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GRADE_GENERIQUE Date de
	 * création : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_GRADE_GENERIQUE() {
		return "NOM_LB_GRADE_GENERIQUE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GRADE_GENERIQUE_SELECT Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getNOM_LB_GRADE_GENERIQUE_SELECT() {
		return "NOM_LB_GRADE_GENERIQUE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GRADE_GENERIQUE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String[] getVAL_LB_GRADE_GENERIQUE() {
		return getLB_GRADE_GENERIQUE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_GRADE_GENERIQUE Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public String getVAL_LB_GRADE_GENERIQUE_SELECT() {
		return getZone(getNOM_LB_GRADE_GENERIQUE_SELECT());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (04/10/11 09:12:33)
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

		// SI CALCUL PAYE EN COURS
		String percou = DTAARA_CALC.read().toString();
		if (!percou.trim().equals(Const.CHAINE_VIDE)) {
			setCalculPaye(percou);
		} else {
			setCalculPaye(Const.CHAINE_VIDE);
		}

		initialiseListeDeroulante();

		if (getListeGrille().size() == 0) {
			initialiseListeGrille();
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		// initialisation de la liste des grades génériques
		if (getHashGradeGenerique().size() == 0) {
			ArrayList<GradeGenerique> listeGradeGenerique = GradeGenerique
					.listerGradeGeneriqueOrderLib(getTransaction());
			setListeGradeGenerique(listeGradeGenerique);

			int[] tailles = { 50 };
			String[] champs = { "libGradeGenerique" };
			setLB_GRADE_GENERIQUE(new FormateListe(tailles, listeGradeGenerique, champs).getListeFormatee(false));

			// remplissage de la hashTable
			for (GradeGenerique g : listeGradeGenerique) {
				getHashGradeGenerique().put(g.getCdgeng(), g);
			}
		}

		// initialisation de la liste des classes
		if (getHashClasse().size() == 0) {
			ArrayList<Classe> listeClasse = Classe.listerClasse(getTransaction());
			setListeClasse(listeClasse);

			int[] tailles = { 60 };
			String[] champs = { "libClasse" };
			setLB_CLASSE(new FormateListe(tailles, listeClasse, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (Classe c : listeClasse) {
				getHashClasse().put(c.getCodClasse(), c);
			}
		}

		// initialisation de la liste des échelons
		if (getHashEchelon().size() == 0) {
			ArrayList<Echelon> listeEchelon = Echelon.listerEchelon(getTransaction());
			setListeEchelon(listeEchelon);

			int[] tailles = { 60 };
			String[] champs = { "libEchelon" };
			setLB_ECHELON(new FormateListe(tailles, listeEchelon, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (Echelon e : listeEchelon) {
				getHashEchelon().put(e.getCodEchelon(), e);
			}
		}

		// initialisation de la liste des baremes
		if (getHashBareme().size() == 0) {
			setListeBareme(Bareme.listerBareme(getTransaction()));

			int[] tailles = { 7 };
			String[] champs = { "iban" };
			setLB_BAREME(new FormateListe(tailles, getListeBareme(), champs).getListeFormatee());

			// remplissage de la hashTable
			for (Bareme b : getListeBareme()) {
				getHashBareme().put(b.getIban(), b);
			}
		}

		// initialisation de la liste des motifs d'avancement
		if (getHashMotifAvct().size() == 0) {
			ArrayList<MotifAvancement> listeMotifAvct = getMotifAvancementDao().listerMotifAvancement();
			setListeMotifAvct(listeMotifAvct);

			if (getListeMotifAvct().size() != 0) {
				int[] tailles = { 60 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<MotifAvancement> list = getListeMotifAvct().listIterator(); list.hasNext();) {
					MotifAvancement de = (MotifAvancement) list.next();
					String ligne[] = { de.getLibMotifAvct() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_MOTIF_AVCT(aFormat.getListeFormatee(true));
			} else {
				setLB_MOTIF_AVCT(null);
			}

			// remplissage de la hashTable
			for (MotifAvancement m : listeMotifAvct) {
				getHashMotifAvct().put(m.getIdMotifAvct().toString(), m);
			}
		}

	}

	/**
	 * fonction qui permet de savoir si une paye est en cours.
	 * 
	 * @return calculPaye
	 */
	public String getCalculPaye() {
		return calculPaye;
	}

	private void setCalculPaye(String calculPaye) {
		this.calculPaye = calculPaye;
	}

	/**
	 * Affiche la liste des grades
	 * 
	 */
	public void afficheListeGrade() {

		int indiceGrade = 0;
		if (getListeGrade() != null) {
			for (int i = 0; i < getListeGrade().size(); i++) {
				Grade grade = (Grade) getListeGrade().get(i);

				addZone(getNOM_ST_CODE_GRADE(indiceGrade), grade.getCodeGrade());
				addZone(getNOM_ST_LIB_GRADE(indiceGrade), grade.getLibGrade());
				addZone(getNOM_ST_IBA_GRADE(indiceGrade),
						Services.estNumerique(grade.getIban()) ? Integer.valueOf(grade.getIban()).toString() : grade
								.getIban());
				addZone(getNOM_ST_GRADE_SUIVANT(indiceGrade),
						grade.getCodeGradeSuivant() == null || grade.getCodeGradeSuivant().equals(Const.CHAINE_VIDE) ? "&nbsp;"
								: grade.getCodeGradeSuivant());
				indiceGrade++;
			}
		}
	}

	/**
	 * Affiche la liste des classe/echelons du grade correspondant
	 */
	private void initialiseListeGrille() throws Exception {

		ArrayList<Grade> liste = Grade.listerTypeGrade(getTransaction());
		setListeGrille(liste);

		int indiceGrille = 0;
		if (getListeGrille() != null) {
			for (int i = 0; i < getListeGrille().size(); i++) {
				Grade grille = (Grade) getListeGrille().get(i);

				addZone(getNOM_ST_LIB_GRADE_GRILLE(indiceGrille), grille.getGrade());
				addZone(getNOM_ST_ACTIF_GRADE_GRILLE(indiceGrille), grille.getCodeActif());
				indiceGrille++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_GRILLE Date de
	 * création : (04/10/11 09:12:40)
	 * 
	 */
	public String getNOM_PB_CREER_GRILLE() {
		return "NOM_PB_CREER_GRILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 09:12:40)
	 * 
	 */
	public boolean performPB_CREER_GRILLE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_GRILLE(), ACTION_CREATION_GRILLE);

		setListeGrade(new ArrayList<Grade>());

		addZone(getNOM_LB_GRADE_GENERIQUE_SELECT(), Const.ZERO);
		initialiseNomGrade();

		// init de la visite courante
		videZonesDeSaisie(request);

		afficheListeGrade();

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_LB_CLASSE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_ECHELON_SELECT(), Const.ZERO);
		addZone(getNOM_LB_BAREME_SELECT(), Const.ZERO);
		addZone(getNOM_LB_MOTIF_AVCT_SELECT(), Const.ZERO);

		addZone(getNOM_EF_CODE_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_GRADE_SUIVANT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MONTANT_FORFAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MONTANT_PRIME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE_MIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE_MOY(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DUREE_MAX(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_ACC(), getNOM_RB_OUI());
		addZone(getNOM_RG_BM(), getNOM_RB_OUI());
		addZone(getNOM_RG_CODE_GRILLE(), getNOM_RB_NC());
	}

	/**
	 * Initialise la grille courante Liste les grades de la grille
	 */
	private boolean initialiseGrilleCourante(HttpServletRequest request) throws Exception {
		String typeGrade = getGrilleCourant().getGrade();
		String codActGrade = getGrilleCourant().getCodeActif();

		ArrayList<Grade> liste = Grade.listerGradeAvecTypeGradeCodAct(getTransaction(), typeGrade, codActGrade);

		setListeGrade(liste);

		afficheListeGrade();

		return true;
	}

	/**
	 * Initialise la classe échelon courante alimente le formulaire avec le
	 * grade sélectioné
	 */
	private boolean initialiseClasseEchelonCourant(HttpServletRequest request) throws Exception {

		GradeGenerique gradeGenerique = (GradeGenerique) getHashGradeGenerique().get(
				getGradeCourant().getCodeGradeGenerique());
		Classe classe = (Classe) getHashClasse().get(getGradeCourant().getCodeClasse());
		Echelon echelon = (Echelon) getHashEchelon().get(getGradeCourant().getCodeEchelon());
		Bareme bareme = (Bareme) getHashBareme().get(Services.lpad(getGradeCourant().getIban(), 7, "0"));
		MotifAvancement motifAvct = (MotifAvancement) getHashMotifAvct().get(getGradeCourant().getCodeTava());

		if (getTransaction().isErreur())
			getTransaction().traiterErreur();

		// Alim zones
		if (gradeGenerique != null)
			addZone(getNOM_LB_GRADE_GENERIQUE_SELECT(),
					String.valueOf(getListeGradeGenerique().indexOf(gradeGenerique)));

		addZone(getNOM_EF_GRADE(), getGradeCourant().getGrade());

		if (classe != null)
			addZone(getNOM_LB_CLASSE_SELECT(), String.valueOf(getListeClasse().indexOf(classe) + 1));

		if (echelon != null)
			addZone(getNOM_LB_ECHELON_SELECT(), String.valueOf(getListeEchelon().indexOf(echelon) + 1));

		if (motifAvct != null)
			addZone(getNOM_LB_MOTIF_AVCT_SELECT(), String.valueOf(getListeMotifAvct().indexOf(motifAvct) + 1));

		addZone(getNOM_EF_CODE_GRADE(), getGradeCourant().getCodeGrade());
		addZone(getNOM_EF_CODE_GRADE_SUIVANT(), getGradeCourant().getCodeGradeSuivant());
		addZone(getNOM_EF_MONTANT_FORFAIT(),
				getGradeCourant().getMontantForfait().indexOf(".") == -1 ? getGradeCourant().getMontantForfait()
						: getGradeCourant().getMontantForfait().substring(0,
								getGradeCourant().getMontantForfait().indexOf(".")));
		addZone(getNOM_EF_MONTANT_PRIME(), getGradeCourant().getMontantPrime());

		if (bareme != null)
			addZone(getNOM_LB_BAREME_SELECT(), String.valueOf(getListeBareme().indexOf(bareme)));

		addZone(getNOM_RG_CODE_GRILLE(), getGradeCourant().getCodeGrille().equals("FR") ? getNOM_RB_FR()
				: getNOM_RB_NC());
		addZone(getNOM_RG_ACC(), getGradeCourant().getAcc().equals("O") ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_BM(), getGradeCourant().getBm().equals("O") ? getNOM_RB_OUI() : getNOM_RB_NON());

		addZone(getNOM_EF_DUREE_MIN(), getGradeCourant().getDureeMin());
		addZone(getNOM_EF_DUREE_MOY(), getGradeCourant().getDureeMoy());
		addZone(getNOM_EF_DUREE_MAX(), getGradeCourant().getDureeMax());

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_GRADE Date de
	 * création : (04/10/11 09:12:40)
	 * 
	 */
	public String getNOM_ST_ACTION_GRADE() {
		return "NOM_ST_ACTION_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_GRADE
	 * Date de création : (04/10/11 09:12:40)
	 * 
	 */
	public String getVAL_ST_ACTION_GRADE() {
		return getZone(getNOM_ST_ACTION_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_GRILLE Date
	 * de création : (04/10/11 09:12:40)
	 * 
	 */
	public String getNOM_ST_ACTION_GRILLE() {
		return "NOM_ST_ACTION_GRILLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_GRILLE
	 * Date de création : (04/10/11 09:12:40)
	 * 
	 */
	public String getVAL_ST_ACTION_GRILLE() {
		return getZone(getNOM_ST_ACTION_GRILLE());
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

	private ArrayList<MotifAvancement> getListeMotifAvct() {
		return listeMotifAvct;
	}

	private void setListeMotifAvct(ArrayList<MotifAvancement> listeMotifAvct) {
		this.listeMotifAvct = listeMotifAvct;
	}

	private ArrayList<GradeGenerique> getListeGradeGenerique() {
		return listeGradeGenerique;
	}

	private void setListeGradeGenerique(ArrayList<GradeGenerique> listeGradeGenerique) {
		this.listeGradeGenerique = listeGradeGenerique;
	}

	public ArrayList<Grade> getListeGrille() {
		if (listeGrille == null)
			return new ArrayList<Grade>();
		return listeGrille;
	}

	private void setListeGrille(ArrayList<Grade> listeGrille) {
		this.listeGrille = listeGrille;
	}

	public ArrayList<Grade> getListeGrade() {
		if (listeGrade == null)
			return new ArrayList<Grade>();
		return listeGrade;
	}

	private void setListeGrade(ArrayList<Grade> listeGrade) {
		this.listeGrade = listeGrade;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 10:26:55)
	 * 
	 */
	public boolean performPB_SELECT_GRADE_GENERIQUE(HttpServletRequest request) throws Exception {
		initialiseNomGrade();
		return true;

	}

	/**
	 * Alimente le champ grade avec le nom du grade générique pour être modifié
	 * 
	 */
	public void initialiseNomGrade() throws Exception {
		int numligne = (Services.estNumerique(getZone(getNOM_LB_GRADE_GENERIQUE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GRADE_GENERIQUE_SELECT())) : -1);

		if (numligne == -1) {
			addZone(getNOM_EF_GRADE(), Const.CHAINE_VIDE);
			return;
		}

		GradeGenerique gradeGenerique = (GradeGenerique) getListeGradeGenerique().get(numligne);
		addZone(getNOM_EF_GRADE(), gradeGenerique.getLibGradeGenerique());

		if (getTransaction().isErreur())
			getTransaction().traiterErreur();

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (04/10/11 10:27:59)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 10:27:59)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_ACTION_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_GRILLE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_GRADE_GENERIQUE Date
	 * de création : (04/10/11 10:27:59)
	 * 
	 */
	public String getNOM_PB_SELECT_GRADE_GENERIQUE() {
		return "NOM_PB_SELECT_GRADE_GENERIQUE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_GRADE Date de
	 * création : (04/10/11 10:27:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_GRADE() {
		return "NOM_PB_VALIDER_GRADE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_GRILLE Date de
	 * création : (04/10/11 10:27:59)
	 * 
	 */
	public String getNOM_PB_VALIDER_GRILLE() {
		return "NOM_PB_VALIDER_GRILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 10:27:59)
	 * 
	 */
	public boolean performPB_VALIDER_GRADE(HttpServletRequest request) throws Exception {

		// validation modification grille
		if (!performControlerChamp())
			return false;

		if (!performControlerRG())
			return false;

		if (!alimenterGrade())
			return false;

		if (getVAL_ST_ACTION_GRADE().equals(ACTION_MODIFICATION_GRADE)) {
			// on verifie que le grade suivant existe
			String cdgrad = getVAL_EF_CODE_GRADE_SUIVANT();
			if (cdgrad != Const.CHAINE_VIDE) {
				@SuppressWarnings("unused")
				Grade gradeSuiv = Grade.chercherGrade(getTransaction(), cdgrad);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "ERR146",
					// "Le code @ du grade suivant ne correspond à aucun grade."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR146", cdgrad));
					return false;
				}
				getGradeCourant().setCodeGradeSuivant(cdgrad);
			}
			getGradeCourant().modifierGrade(getTransaction());
			addZone(getNOM_ST_ACTION_GRILLE(), ACTION_CONSULTATION_GRILLE);
		} else {
			// on met à jour le code grade suivant
			if (getListeGrade().size() != 0) {
				Grade dernierGrade = getListeGrade().get(getListeGrade().size() - 1);
				dernierGrade.setCodeGradeSuivant(getGradeCourant().getCodeGrade());
				dernierGrade.modifierGrade(getTransaction());
			}
			getGradeCourant().creerGrade(getTransaction());
			addZone(getNOM_ST_ACTION_GRILLE(), ACTION_CONSULTATION_GRILLE);
		}

		getTransaction().commitTransaction();

		initialiseListeGrille();
		// initialiseGrilleCourante(request);

		setGradeCourant(null);
		setGrilleCourant(null);

		addZone(getNOM_ST_ACTION_GRADE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 10:27:59)
	 * 
	 */
	public boolean performPB_VALIDER_GRILLE(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_GRILLE().equals(ACTION_CREATION_GRILLE)) {

			if (getVAL_EF_GRADE().equals(Const.CHAINE_VIDE)) {
				// "ERR002","La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "grade"));
				return false;
			}

			if (Grade.listerGradeAvecTypeGrade(getTransaction(), getVAL_EF_GRADE().trim()).size() != 0) {
				// "ERR141","Cette grille existe déjà."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR141"));
				return false;
			}

			addZone(getNOM_EF_GRADE(), getVAL_EF_GRADE().trim());
			addZone(getNOM_ST_ACTION_GRADE(), ACTION_CREATION_GRADE);
			return true;
		}

		return true;
	}

	/**
	 * Controle si les champs du formulaire de création d'un grade sont remplis
	 * correctement
	 * 
	 * @return boolean
	 */
	private boolean performControlerChamp() {

		// champ code grade obligatoire
		if (getVAL_EF_CODE_GRADE().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code grade"));
			return false;
		}

		// champ montant forfait obligatoire
		if (getVAL_EF_MONTANT_FORFAIT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "montant forfait"));
			return false;
		}

		// champ montant forfait numerique
		if (!Services.estNumerique(getVAL_EF_MONTANT_FORFAIT())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "montant forfait"));
			return false;
		}

		// champ montant prime obligatoire
		if (getVAL_EF_MONTANT_PRIME().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "montant prime"));
			return false;
		}

		// champ montant prime numerique
		if (!Services.estNumerique(getVAL_EF_MONTANT_PRIME())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "montant prime"));
			return false;
		}

		// champ duree min obligatoire
		if (getVAL_EF_DUREE_MIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée min"));
			return false;
		}

		// champ duree min numerique
		if (!Services.estNumerique(getVAL_EF_DUREE_MIN())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "durée min"));
			return false;
		}

		// champ duree moy obligatoire
		if (getVAL_EF_DUREE_MOY().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée moyenne"));
			return false;
		}

		// champ duree moy numerique
		if (!Services.estNumerique(getVAL_EF_DUREE_MOY())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "duree moyenne"));
			return false;
		}

		// champ duree max obligatoire
		if (getVAL_EF_DUREE_MAX().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée max"));
			return false;
		}

		// champ duree max numerique
		if (!Services.estNumerique(getVAL_EF_DUREE_MAX())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "duree max"));
			return false;
		}

		return true;
	}

	/**
	 * Controle si les regles de gestion de création d'un grade sont respectées
	 * 
	 * @return boolean
	 */
	private boolean performControlerRG() throws Exception {

		// codeGrade Existant
		if (getVAL_ST_ACTION_GRADE().equals(ACTION_CREATION_GRADE)) {

			@SuppressWarnings("unused")
			Grade gradeExist = Grade.chercherGrade(getTransaction(), getVAL_EF_CODE_GRADE().toUpperCase());
			if (!getTransaction().isErreur()) {
				// listeMessages.put("ERR974",
				// "Attention, il existe déjà @ avec @. Veuillez contrôler.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un grade", "ce code"));
				return false;
			} else {
				getTransaction().traiterErreur();
			}

			ArrayList<Grade> gradesExistants = Grade.listerGradeActif(getTransaction());
			for (Grade g : gradesExistants)
				if (g.getCodeGrade().equals(getVAL_EF_CODE_GRADE().toUpperCase())) {
					// listeMessages.put("ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un grade", "ce code"));
					return false;
				}
		}

		// couple classe/echelon deja existant
		String classeString = getZone(getNOM_LB_CLASSE_SELECT()).equals(Const.CHAINE_VIDE) ? "0"
				: getZone(getNOM_LB_CLASSE_SELECT());
		Classe classe = null;
		if (!classeString.equals("0")) {
			classe = (Classe) getListeClasse().get(Integer.parseInt(classeString) - 1);
		}
		String echelonString = getZone(getNOM_LB_ECHELON_SELECT()).equals(Const.CHAINE_VIDE) ? "0"
				: getZone(getNOM_LB_ECHELON_SELECT());
		Echelon echelon = null;
		if (!echelonString.equals("0")) {
			echelon = (Echelon) getListeEchelon().get(Integer.parseInt(echelonString) - 1);
		}

		if (classe != null && echelon != null) {
			for (Grade g : getListeGrade()) {
				if (!g.getCodeGrade().equals(getVAL_EF_CODE_GRADE()) && g.getCodeClasse().equals(classe.getCodClasse())
						&& g.getCodeEchelon().equals(echelon.getCodEchelon())) {
					// listeMessages.put("ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler.");
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un grade", "cette classe et cet échelon"));
					return false;
				}
			}
		}

		// MOTIF AVCT et DUREE MIN/MOY/MAX
		String motifAvctString = getZone(getNOM_LB_MOTIF_AVCT_SELECT()).equals(Const.CHAINE_VIDE) ? "0"
				: getZone(getNOM_LB_MOTIF_AVCT_SELECT());
		MotifAvancement motifAvct = null;
		if (!motifAvctString.equals("0")) {
			motifAvct = (MotifAvancement) getListeMotifAvct().get(Integer.parseInt(motifAvctString) - 1);
		}

		if (motifAvct != null && motifAvct.getCode().equals("AD")) {
			// durée : min <= moy <= max
			if (Integer.parseInt(getVAL_EF_DUREE_MIN()) > Integer.parseInt(getVAL_EF_DUREE_MOY())) {
				// "ERR968", "La zone @ ne peut être supérieure à la zone @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR968", "durée min", "durée moyenne"));
				return false;
			}
			if (Integer.parseInt(getVAL_EF_DUREE_MOY()) > Integer.parseInt(getVAL_EF_DUREE_MAX())) {
				// "ERR968", "La zone @ ne peut être supérieure à la zone @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR968", "durée moyenne", "durée max"));
				return false;
			}

		} else if (motifAvct != null
				&& (motifAvct.getCode().equals("AUTO") || motifAvct.getCode().equals("PROMO")
						|| motifAvct.getCode().equals("REVA") || motifAvct.getCode().equals("TITU"))) {
			if (Integer.parseInt(getVAL_EF_DUREE_MIN()) > 0) {
				// "ERR187",
				// "Si le motif d'avancement est @. Alors @ doit être égal à 0."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR187", motifAvct.getCode(), "durée minimum"));
				return false;
			}
			if (Integer.parseInt(getVAL_EF_DUREE_MAX()) > 0) {
				// "ERR187",
				// "Si le motif d'avancement est @. Alors @ doit être égal à 0."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR187", motifAvct.getCode(), "durée maximum"));
				return false;
			}
		} else if (motifAvct != null) {
			// "ERR188",
			// "Ce motif d'avancement n'a pas de règle de gestion. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR188"));
			return false;
		}

		// IBAN grade n > IBAN grade n-1
		String baremeString = getZone(getNOM_LB_BAREME_SELECT()).equals(Const.CHAINE_VIDE) ? "0"
				: getZone(getNOM_LB_BAREME_SELECT());
		Bareme bareme = (Bareme) getListeBareme().get(Integer.parseInt(baremeString));

		Grade gradePrecedant = getGradePrecedant();
		Grade gradeSuivant = getGradeSuivant();
		if (gradePrecedant != null && Services.estNumerique(gradePrecedant.getIban())) {
			if (Integer.parseInt(gradePrecedant.getIban()) >= Integer.parseInt(bareme.getIban())) {
				// "ERR142",
				// "L'IBAN doit etre supérieur à l'IBAN du grade précédant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR142"));
				return false;
			}
		}
		if (gradeSuivant != null && Services.estNumerique(gradeSuivant.getIban())) {
			if (Integer.parseInt(gradeSuivant.getIban()) <= Integer.parseInt(bareme.getIban())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR143"));
				return false;
			}
		}

		return true;
	}

	private Grade getGradePrecedant() throws Exception {
		if (!getListeGrade().contains(getGradeCourant())) {
			if (getListeGrade().size() > 0)
				return getListeGrade().get(getListeGrade().size() - 1);

			return null;
		}

		Grade precedant = null;
		for (Grade courant : getListeGrade()) {
			if (courant.equals(getGradeCourant()))
				return precedant;
			precedant = courant;
		}

		return null;
	}

	private Grade getGradeSuivant() throws Exception {
		if (!getListeGrade().contains(getGradeCourant())) {
			return null;
		}

		Grade precedant = null;
		for (Grade courant : getListeGrade()) {
			if (precedant != null && precedant.equals(getGradeCourant()))
				return courant;
			precedant = courant;
		}

		return null;
	}

	private boolean alimenterGrade() {

		// classe
		Classe classe = null;
		int numClasse = (Services.estNumerique(getZone(getNOM_LB_CLASSE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_CLASSE_SELECT())) : -1);
		if (numClasse > 0 && getListeClasse().size() > 0 && numClasse - 1 < getListeClasse().size()) {
			classe = (Classe) getListeClasse().get(numClasse - 1);
		}

		// echelon
		Echelon echelon = null;
		int numEchelon = (Services.estNumerique(getZone(getNOM_LB_ECHELON_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ECHELON_SELECT())) : -1);
		if (numEchelon > 0 && getListeEchelon().size() > 0 && numEchelon - 1 < getListeEchelon().size()) {
			echelon = (Echelon) getListeEchelon().get(numEchelon - 1);
		}

		// motif avct
		MotifAvancement motifAvct = null;
		int numMotifAvct = (Services.estNumerique(getZone(getNOM_LB_MOTIF_AVCT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_MOTIF_AVCT_SELECT())) : -1);
		if (numMotifAvct > 0 && getListeMotifAvct().size() > 0 && numMotifAvct - 1 < getListeMotifAvct().size()) {
			motifAvct = (MotifAvancement) getListeMotifAvct().get(numMotifAvct - 1);
		}

		// grade générique
		int numGradeGenerique = (Services.estNumerique(getZone(getNOM_LB_GRADE_GENERIQUE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GRADE_GENERIQUE_SELECT())) : -1);
		if (numGradeGenerique == -1 || getListeGradeGenerique().size() == 0
				|| numGradeGenerique > getListeGradeGenerique().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "grades génériques"));
			return false;
		}
		GradeGenerique gradeGenerique = (GradeGenerique) getListeGradeGenerique().get(numGradeGenerique);
		if (getGradeCourant() == null)
			setGradeCourant(new Grade());

		if (classe != null)
			getGradeCourant().setCodeClasse(classe.getCodClasse());
		else
			getGradeCourant().setCodeClasse(Const.CHAINE_VIDE);

		if (echelon != null)
			getGradeCourant().setCodeEchelon(echelon.getCodEchelon());
		else
			getGradeCourant().setCodeEchelon(Const.CHAINE_VIDE);

		if (motifAvct != null)
			getGradeCourant().setCodeTava(motifAvct.getIdMotifAvct().toString());
		else
			getGradeCourant().setCodeTava(Const.CHAINE_VIDE);

		getGradeCourant().setAcc(getVAL_RG_ACC().equals(getNOM_RB_OUI()) ? "O" : "N");
		getGradeCourant().setBm(getVAL_RG_BM().equals(getNOM_RB_OUI()) ? "O" : "N");
		getGradeCourant().setCodeGrille(getVAL_RG_CODE_GRILLE().equals(getNOM_RB_NC()) ? "NC" : "FR");

		getGradeCourant().setCodeGradeGenerique(gradeGenerique.getCdgeng());
		getGradeCourant().setCodeCadre(gradeGenerique.getCodCadre());
		getGradeCourant().setCodeGrade(getVAL_EF_CODE_GRADE());

		getGradeCourant().setMontantForfait(getVAL_EF_MONTANT_FORFAIT());
		getGradeCourant().setMontantPrime(getVAL_EF_MONTANT_PRIME());

		Bareme bareme = getListeBareme().get(Integer.parseInt(getVAL_LB_BAREME_SELECT()));
		String iban = bareme.getIban();
		if (Services.estNumerique(iban)) {
			iban = Services.lpad(iban, 7, "0");
		}
		getGradeCourant().setIban(iban);

		getGradeCourant().setDureeMin(getVAL_EF_DUREE_MIN());
		getGradeCourant().setDureeMoy(getVAL_EF_DUREE_MOY());
		getGradeCourant().setDureeMax(getVAL_EF_DUREE_MAX());

		getGradeCourant().setGrade(getVAL_EF_GRADE());
		getGradeCourant().setLibGrade(
				getVAL_EF_GRADE().trim() + (classe != null ? " " + classe.getLibClasse() : Const.CHAINE_VIDE)
						+ (echelon != null ? " " + echelon.getLibEchelon() : Const.CHAINE_VIDE));

		return true;
	}

	private Hashtable<String, Classe> getHashClasse() {
		if (hashClasse == null)
			hashClasse = new Hashtable<String, Classe>();
		return hashClasse;
	}

	private Hashtable<String, Echelon> getHashEchelon() {
		if (hashEchelon == null)
			hashEchelon = new Hashtable<String, Echelon>();
		return hashEchelon;
	}

	private Hashtable<String, MotifAvancement> getHashMotifAvct() {
		if (hashMotifAvct == null)
			hashMotifAvct = new Hashtable<String, MotifAvancement>();
		return hashMotifAvct;
	}

	private Hashtable<String, GradeGenerique> getHashGradeGenerique() {
		if (hashGradeGenerique == null)
			hashGradeGenerique = new Hashtable<String, GradeGenerique>();
		return hashGradeGenerique;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_GRADE_SUIVANT(int i) {
		return "NOM_PB_GRADE_SUIVANT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/10/11 11:34:53)
	 * 
	 */
	public boolean performPB_GRADE_SUIVANT(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		videZonesDeSaisie(request);

		setGradeCourant(new Grade());

		// init de l'échelon courant
		Grade dernierGrade = getListeGrade().get(getListeGrade().size() - 1);
		if (!initialiseClasseEchelonAncien(request, dernierGrade))
			return false;

		addZone(getNOM_ST_ACTION_GRADE(), ACTION_CREATION_GRADE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseClasseEchelonAncien(HttpServletRequest request, Grade dernierGrade) throws Exception {

		GradeGenerique gradeGenerique = (GradeGenerique) getHashGradeGenerique().get(
				dernierGrade.getCodeGradeGenerique());
		Classe classe = (Classe) getHashClasse().get(dernierGrade.getCodeClasse());
		Echelon echelon = (Echelon) getHashEchelon().get(dernierGrade.getCodeEchelon());
		MotifAvancement motifAvct = (MotifAvancement) getHashMotifAvct().get(dernierGrade.getCodeTava());
		Bareme bareme = (Bareme) getHashBareme().get(Services.lpad(dernierGrade.getIban(), 7, "0"));

		if (getTransaction().isErreur())
			getTransaction().traiterErreur();

		// Alim zones
		if (gradeGenerique != null)
			addZone(getNOM_LB_GRADE_GENERIQUE_SELECT(),
					String.valueOf(getListeGradeGenerique().indexOf(gradeGenerique)));

		addZone(getNOM_EF_GRADE(), dernierGrade.getGrade());

		if (classe != null)
			addZone(getNOM_LB_CLASSE_SELECT(), String.valueOf(getListeClasse().indexOf(classe) + 1));

		if (echelon != null)
			addZone(getNOM_LB_ECHELON_SELECT(), String.valueOf(getListeEchelon().indexOf(echelon) + 1));

		if (motifAvct != null)
			addZone(getNOM_LB_MOTIF_AVCT_SELECT(), String.valueOf(getListeMotifAvct().indexOf(motifAvct) + 1));

		addZone(getNOM_EF_CODE_GRADE(), dernierGrade.getCodeGrade());
		addZone(getNOM_EF_MONTANT_FORFAIT(),
				dernierGrade.getMontantForfait().indexOf(".") == -1 ? dernierGrade.getMontantForfait() : dernierGrade
						.getMontantForfait().substring(0, dernierGrade.getMontantForfait().indexOf(".")));
		addZone(getNOM_EF_MONTANT_PRIME(), dernierGrade.getMontantPrime());

		if (bareme != null)
			addZone(getNOM_LB_BAREME_SELECT(), String.valueOf(getListeBareme().indexOf(bareme)));

		addZone(getNOM_RG_CODE_GRILLE(), dernierGrade.getCodeGrille().equals("FR") ? getNOM_RB_FR() : getNOM_RB_NC());
		addZone(getNOM_RG_ACC(), dernierGrade.getAcc().equals("O") ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_BM(), dernierGrade.getBm().equals("O") ? getNOM_RB_OUI() : getNOM_RB_NON());

		addZone(getNOM_EF_DUREE_MIN(), dernierGrade.getDureeMin());
		addZone(getNOM_EF_DUREE_MOY(), dernierGrade.getDureeMoy());
		addZone(getNOM_EF_DUREE_MAX(), dernierGrade.getDureeMax());

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_GRADE Date de
	 * création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_CODE_GRADE() {
		return "NOM_EF_CODE_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_GRADE Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_CODE_GRADE() {
		return getZone(getNOM_EF_CODE_GRADE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_MAX Date de
	 * création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_DUREE_MAX() {
		return "NOM_EF_DUREE_MAX";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_MAX Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_DUREE_MAX() {
		return getZone(getNOM_EF_DUREE_MAX());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_MIN Date de
	 * création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_DUREE_MIN() {
		return "NOM_EF_DUREE_MIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_MIN Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_DUREE_MIN() {
		return getZone(getNOM_EF_DUREE_MIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DUREE_MOY Date de
	 * création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_DUREE_MOY() {
		return "NOM_EF_DUREE_MOY";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DUREE_MOY Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_DUREE_MOY() {
		return getZone(getNOM_EF_DUREE_MOY());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_FORFAIT
	 * Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_MONTANT_FORFAIT() {
		return "NOM_EF_MONTANT_FORFAIT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_FORFAIT Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_MONTANT_FORFAIT() {
		return getZone(getNOM_EF_MONTANT_FORFAIT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_PRIME Date
	 * de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_EF_MONTANT_PRIME() {
		return "NOM_EF_MONTANT_PRIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_PRIME Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_EF_MONTANT_PRIME() {
		return getZone(getNOM_EF_MONTANT_PRIME());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_ACC
	 * Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RG_ACC() {
		return "NOM_RG_ACC";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_ACC Date
	 * de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_RG_ACC() {
		return getZone(getNOM_RG_ACC());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_BM Date
	 * de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RG_BM() {
		return "NOM_RG_BM";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_BM Date
	 * de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_RG_BM() {
		return getZone(getNOM_RG_BM());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_CODE_GRILLE Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RG_CODE_GRILLE() {
		return "NOM_RG_CODE_GRILLE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_CODE_GRILLE Date de création : (04/10/11 11:47:54)
	 * 
	 */
	public String getVAL_RG_CODE_GRILLE() {
		return getZone(getNOM_RG_CODE_GRILLE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_FR Date de création :
	 * (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RB_FR() {
		return "NOM_RB_FR";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NC Date de création :
	 * (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RB_NC() {
		return "NOM_RB_NC";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NON Date de création :
	 * (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_OUI Date de création :
	 * (04/10/11 11:47:54)
	 * 
	 */
	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	public Grade getGradeCourant() {
		return gradeCourant;
	}

	private void setGradeCourant(Grade gradeCourant) {
		this.gradeCourant = gradeCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_GRADE Date de
	 * création : (05/10/11 15:31:07)
	 * 
	 */
	public String getNOM_PB_MODIFIER_GRADE(int i) {
		return "NOM_PB_MODIFIER_GRADE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/10/11 15:31:07)
	 * 
	 */
	public boolean performPB_MODIFIER_GRADE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Récup du grade courant
		Grade gradeCourant = getListeGrade().get(indiceEltAConsulter);
		setGradeCourant(gradeCourant);

		videZonesDeSaisie(request);

		// init de l'échelon courant
		if (!initialiseClasseEchelonCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_GRADE(), ACTION_MODIFICATION_GRADE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_GRADE Date de création
	 * : (06/10/11 10:14:07)
	 * 
	 */
	public String getNOM_PB_CREER_GRADE() {
		return "NOM_PB_CREER_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (06/10/11 10:14:07)
	 * 
	 */
	public boolean performPB_CREER_GRADE(HttpServletRequest request) throws Exception {

		videZonesDeSaisie(request);

		setGradeCourant(new Grade());

		// init de l'échelon courant
		if (!initialiseClasseEchelonCourant(request))
			return false;

		addZone(getNOM_ST_ACTION_GRADE(), ACTION_CREATION_GRADE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-GRADE-GRADES";
	}

	/**
	 * Constructeur du process OePARAMETRAGEGrade. Date de création : (18/10/11
	 * 16:01:01)
	 * 
	 */
	public OePARAMETRAGEGrade() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_GRADE Date de
	 * création : (18/10/11 16:01:01)
	 * 
	 */
	public String getNOM_PB_CONSULTER_GRADE(int i) {
		return "NOM_PB_CONSULTER_GRADE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/10/11 16:01:01)
	 * 
	 */
	public boolean performPB_CONSULTER_GRADE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Récup du grade courant
		Grade gradeCourant = getListeGrade().get(indiceEltAConsulter);
		setGradeCourant(gradeCourant);

		videZonesDeSaisie(request);

		// init de l'échelon courant
		if (!initialiseClasseEchelonCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_GRADE(), ACTION_CONSULTATION_GRADE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_GRILLE Date de
	 * création : (18/10/11 16:01:01)
	 * 
	 */
	public String getNOM_PB_CONSULTER_GRILLE(int i) {
		return "NOM_PB_CONSULTER_GRILLE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/10/11 16:01:01)
	 * 
	 */
	public boolean performPB_CONSULTER_GRILLE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// Récup du grille courant
		Grade grilleGradeCourant = getListeGrille().get(indiceEltAConsulter);
		setGrilleCourant(grilleGradeCourant);

		// init de la grille courante
		if (!initialiseGrilleCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_GRILLE(), ACTION_CONSULTATION_GRILLE);
		addZone(getNOM_ST_ACTION_GRADE(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private String[] LB_BAREME;

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (04/10/11 09:04:41)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CONSULTER_GRADE
			for (int i = 0; i < getListeGrade().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_GRADE(i))) {
					return performPB_CONSULTER_GRADE(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER_GRILLE
			for (int i = 0; i < getListeGrille().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_GRILLE(i))) {
					return performPB_CONSULTER_GRILLE(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_GRADE
			if (testerParametre(request, getNOM_PB_CREER_GRADE())) {
				return performPB_CREER_GRADE(request);
			}

			// Si clic sur le bouton PB_GRADE_SUIVANT
			for (int i = 0; i < getListeGrade().size(); i++) {
				if (testerParametre(request, getNOM_PB_GRADE_SUIVANT(i))) {
					return performPB_GRADE_SUIVANT(request, i);
				}
			}

			// Si clic sur le bouton PB_MODIFIER_GRADE
			for (int i = 0; i < getListeGrade().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_GRADE(i))) {
					return performPB_MODIFIER_GRADE(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER_GRILLE
			if (testerParametre(request, getNOM_PB_VALIDER_GRILLE())) {
				return performPB_VALIDER_GRILLE(request);
			}

			// Si clic sur le bouton PB_VALIDER_GRADE
			if (testerParametre(request, getNOM_PB_VALIDER_GRADE())) {
				return performPB_VALIDER_GRADE(request);
			}

			// Si clic sur le bouton PB_SELECT_GRADE_GENERIQUE
			if (testerParametre(request, getNOM_PB_SELECT_GRADE_GENERIQUE())) {
				return performPB_SELECT_GRADE_GENERIQUE(request);
			}

			// Si clic sur le bouton PB_CREER_GRILLE
			if (testerParametre(request, getNOM_PB_CREER_GRILLE())) {
				return performPB_CREER_GRILLE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (02/11/11 08:44:31)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEGrade.jsp";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BAREME Date de création :
	 * (02/11/11 08:44:31)
	 * 
	 */
	private String[] getLB_BAREME() {
		if (LB_BAREME == null)
			LB_BAREME = initialiseLazyLB();
		return LB_BAREME;
	}

	/**
	 * Setter de la liste: LB_BAREME Date de création : (02/11/11 08:44:31)
	 * 
	 */
	private void setLB_BAREME(String[] newLB_BAREME) {
		LB_BAREME = newLB_BAREME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BAREME Date de création :
	 * (02/11/11 08:44:31)
	 * 
	 */
	public String getNOM_LB_BAREME() {
		return "NOM_LB_BAREME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BAREME_SELECT Date de création : (02/11/11 08:44:31)
	 * 
	 */
	public String getNOM_LB_BAREME_SELECT() {
		return "NOM_LB_BAREME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BAREME Date de création : (02/11/11 08:44:31)
	 * 
	 */
	public String[] getVAL_LB_BAREME() {
		return getLB_BAREME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_BAREME Date de création : (02/11/11 08:44:31)
	 * 
	 */
	public String getVAL_LB_BAREME_SELECT() {
		return getZone(getNOM_LB_BAREME_SELECT());
	}

	public ArrayList<Bareme> getListeBareme() {
		return listeBareme;
	}

	public void setListeBareme(ArrayList<Bareme> listeBareme) {
		this.listeBareme = listeBareme;
	}

	private Hashtable<String, Bareme> getHashBareme() {
		if (hashBareme == null)
			hashBareme = new Hashtable<String, Bareme>();
		return hashBareme;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_GRADE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE_GRADE(int i) {
		return "NOM_ST_CODE_GRADE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_GRADE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_GRADE(int i) {
		return getZone(getNOM_ST_CODE_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_GRADE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_GRADE(int i) {
		return "NOM_ST_LIB_GRADE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_GRADE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_GRADE(int i) {
		return getZone(getNOM_ST_LIB_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_IBA_GRADE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_IBA_GRADE(int i) {
		return "NOM_ST_IBA_GRADE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_IBA_GRADE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_IBA_GRADE(int i) {
		return getZone(getNOM_ST_IBA_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_SUIVANT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_GRADE_SUIVANT(int i) {
		return "NOM_ST_GRADE_SUIVANT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE_SUIVANT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_GRADE_SUIVANT(int i) {
		return getZone(getNOM_ST_GRADE_SUIVANT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_GRADE_GRILLE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_GRADE_GRILLE(int i) {
		return "NOM_ST_LIB_GRADE_GRILLE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_GRADE_GRILLE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_GRADE_GRILLE(int i) {
		return getZone(getNOM_ST_LIB_GRADE_GRILLE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTIF_GRADE_GRILLE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ACTIF_GRADE_GRILLE(int i) {
		return "NOM_ST_ACTIF_GRADE_GRILLE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTIF_GRADE_GRILLE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ACTIF_GRADE_GRILLE(int i) {
		return getZone(getNOM_ST_ACTIF_GRADE_GRILLE(i));
	}

	public Grade getGrilleCourant() {
		return grilleCourant;
	}

	public void setGrilleCourant(Grade grilleCourant) {
		this.grilleCourant = grilleCourant;
	}

	public String getNOM_EF_CODE_GRADE_SUIVANT() {
		return "NOM_EF_CODE_GRADE_SUIVANT";
	}

	public String getVAL_EF_CODE_GRADE_SUIVANT() {
		return getZone(getNOM_EF_CODE_GRADE_SUIVANT());
	}

	private String[] getLB_MOTIF_AVCT() {
		if (LB_MOTIF_AVCT == null)
			LB_MOTIF_AVCT = initialiseLazyLB();
		return LB_MOTIF_AVCT;
	}

	private void setLB_MOTIF_AVCT(String[] newLB_MOTIF_AVCT) {
		LB_MOTIF_AVCT = newLB_MOTIF_AVCT;
	}

	public String getNOM_LB_MOTIF_AVCT() {
		return "NOM_LB_MOTIF_AVCT";
	}

	public String getNOM_LB_MOTIF_AVCT_SELECT() {
		return "NOM_LB_MOTIF_AVCT_SELECT";
	}

	public String[] getVAL_LB_MOTIF_AVCT() {
		return getLB_MOTIF_AVCT();
	}

	public String getVAL_LB_MOTIF_AVCT_SELECT() {
		return getZone(getNOM_LB_MOTIF_AVCT_SELECT());
	}

	public MotifAvancementDao getMotifAvancementDao() {
		return motifAvancementDao;
	}

	public void setMotifAvancementDao(MotifAvancementDao motifAvancementDao) {
		this.motifAvancementDao = motifAvancementDao;
	}
}
