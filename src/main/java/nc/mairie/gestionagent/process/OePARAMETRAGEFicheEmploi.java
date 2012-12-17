package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Categorie;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.CodeRome;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.DomaineEmploi;
import nc.mairie.metier.parametrage.FamilleEmploi;
import nc.mairie.metier.poste.CategorieFE;
import nc.mairie.metier.poste.Ecole;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OePARAMETRAGEFicheEmploi Date de création : (09/09/11 11:54:33)
 * 
 */
public class OePARAMETRAGEFicheEmploi extends nc.mairie.technique.BasicProcess {
	private String[] LB_DOMAINE;
	private String[] LB_CATEGORIE;
	private String[] LB_CADRE_EMPLOI;
	private String[] LB_CODE_ROME;
	private String[] LB_FILIERE;

	private String[] LB_DIPLOME;

	private ArrayList<DomaineEmploi> listeDomaine;
	private DomaineEmploi domaineEmploiCourant;

	private ArrayList<FamilleEmploi> listeFamille;
	private FamilleEmploi familleCourante;

	private ArrayList<CadreEmploi> listeCadreEmploi;
	private CadreEmploi cadreEmploiCourant;

	private ArrayList<FiliereGrade> listeFiliere;
	private Hashtable<String, FiliereGrade> hashFiliere;

	private ArrayList<DiplomeGenerique> listeDiplome;
	private DiplomeGenerique diplomeCourant;

	private ArrayList<Categorie> listeCategorie;
	private Categorie categorieCourante;

	private ArrayList<CodeRome> listeCodeRome;
	private CodeRome codeRomeCourant;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (09/09/11 11:54:33)
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

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//

		if (getListeDomaine() == null) {
			// Recherche des domaines d'activité
			ArrayList liste = DomaineEmploi.listerDomaineEmploi(getTransaction());
			setListeDomaine(liste);
			initialiseListeDomaine(request);
		}

		if (getListeFamille() == null) {
			// Recherche des domaines d'activité
			ArrayList liste = FamilleEmploi.listerFamilleEmploi(getTransaction());
			setListeFamille(liste);
			initialiseListeFamille(request);
		}

		if (getListeCadreEmploi() == null) {
			// Recherche des domaines d'activité
			ArrayList liste = CadreEmploi.listerCadreEmploi(getTransaction());
			setListeCadreEmploi(liste);
			initialiseListeCadreEmploi(request);
		}

		// Si hashtable des écoles vide
		if (getHashFiliere().size() == 0) {
			ArrayList a = FiliereGrade.listerFiliereGrade(getTransaction());
			setListeFiliere(a);
			initialiseListeFiliere(request);
			// remplissage de la hashTable
			for (int i = 0; i < a.size(); i++) {
				FiliereGrade fi = (FiliereGrade) a.get(i);
				getHashFiliere().put(fi.getCodeFiliere(), fi);
			}
		}

		if (getListeDiplome() == null) {
			// Recherche des domaines d'activité
			ArrayList liste = DiplomeGenerique.listerDiplomeGenerique(getTransaction());
			setListeDiplome(liste);
			initialiseListeDiplome(request);
		}

		if (getListeCategorie() == null) {
			// Recherche des categories d'emploi
			ArrayList liste = Categorie.listerCategorie(getTransaction());
			setListeCategorie(liste);
			initialiseListeCategorie(request);
		}

		if (getListeCodeRome() == null) {
			// Recherche des codes rome d'emploi
			ArrayList liste = CodeRome.listerCodeRome(getTransaction());
			setListeCodeRome(liste);
			initialiseListeCodeRome(request);
		}

	}

	private void initialiseListeFiliere(HttpServletRequest request) {
		if (getListeFiliere().size() != 0) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeFiliere().listIterator(); list.hasNext();) {
				FiliereGrade fi = (FiliereGrade) list.next();
				String ligne[] = { fi.getLibFiliere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FILIERE(aFormat.getListeFormatee(true));
		} else {
			setLB_FILIERE(null);
		}
	}

	/**
	 * @param request
	 * @throws Exception
	 */
	private void initialiseListeCodeRome(HttpServletRequest request) throws Exception {
		setListeCodeRome(CodeRome.listerCodeRome(getTransaction()));
		if (getListeCodeRome().size() != 0) {
			int tailles[] = { 6, 100 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCodeRome().listIterator(); list.hasNext();) {
				CodeRome cr = (CodeRome) list.next();
				String ligne[] = { cr.getLibCodeRome(), cr.getDescCodeRome() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CODE_ROME(aFormat.getListeFormatee());
		} else {
			setLB_CODE_ROME(null);
		}
	}

	/**
	 * Initialisation de la liste des domaines emploi Date de création :
	 * (09/09/11)
	 * 
	 */
	private void initialiseListeDomaine(HttpServletRequest request) throws Exception {
		setListeDomaine(DomaineEmploi.listerDomaineEmploi(getTransaction()));
		if (getListeDomaine().size() != 0) {
			int tailles[] = { 2, 70 };
			String padding[] = { "C", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeDomaine().listIterator(); list.hasNext();) {
				DomaineEmploi de = (DomaineEmploi) list.next();
				String ligne[] = { de.getCodeDomaineEmploi(), de.getLibDomaineEmploi() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_DOMAINE(aFormat.getListeFormatee());
		} else {
			setLB_DOMAINE(null);
		}
	}

	/**
	 * Initialisation de la liste des domaine d'activité Date de création :
	 * (09/09/11)
	 * 
	 */
	private void initialiseListeFamille(HttpServletRequest request) throws Exception {
		setListeFamille(FamilleEmploi.listerFamilleEmploi(getTransaction()));
		if (getListeFamille().size() != 0) {
			int tailles[] = { 3, 70 };
			String padding[] = { "C", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeFamille().listIterator(); list.hasNext();) {
				FamilleEmploi f = (FamilleEmploi) list.next();
				String ligne[] = { f.getCodeFamilleEmploi(), f.getLibFamilleEmploi() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FAMILLE(aFormat.getListeFormatee());
		} else {
			setLB_FAMILLE(null);
		}
	}

	/**
	 * Initialisation de la liste des domaine d'activité Date de création :
	 * (09/09/11)
	 * 
	 */
	private void initialiseListeCadreEmploi(HttpServletRequest request) throws Exception {
		setListeCadreEmploi(CadreEmploi.listerCadreEmploi(getTransaction()));
		if (getListeCadreEmploi().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCadreEmploi().listIterator(); list.hasNext();) {
				CadreEmploi ce = (CadreEmploi) list.next();
				String ligne[] = { ce.libCadreEmploi };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CADRE_EMPLOI(aFormat.getListeFormatee());
		} else {
			setLB_CADRE_EMPLOI(null);
		}
	}

	/**
	 * Initialisation de la liste des domaine d'activité Date de création :
	 * (09/09/11)
	 * 
	 */
	private void initialiseListeDiplome(HttpServletRequest request) throws Exception {
		setListeDiplome(DiplomeGenerique.listerDiplomeGenerique(getTransaction()));
		if (getListeDiplome().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeDiplome().listIterator(); list.hasNext();) {
				DiplomeGenerique dg = (DiplomeGenerique) list.next();
				String ligne[] = { dg.libDiplomeGenerique };

				aFormat.ajouteLigne(ligne);
			}
			setLB_DIPLOME(aFormat.getListeFormatee());
		} else {
			setLB_DIPLOME(null);
		}
	}

	/**
	 * Initialisation de la liste des categories emploi Date de création :
	 * (09/09/11)
	 */
	private void initialiseListeCategorie(HttpServletRequest request) throws Exception {
		setListeCategorie(Categorie.listerCategorie(getTransaction()));
		if (getListeCategorie().size() != 0) {
			int tailles[] = { 20 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCategorie().listIterator(); list.hasNext();) {
				Categorie cat = (Categorie) list.next();
				String ligne[] = { cat.getLibCategorie() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CATEGORIE(aFormat.getListeFormatee());
		} else {
			setLB_CATEGORIE(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEFicheEmploi. Date de création :
	 * (09/09/11 11:54:33)
	 * 
	 */
	public OePARAMETRAGEFicheEmploi() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DOMAINE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_ANNULER_DOMAINE() {
		return "NOM_PB_ANNULER_DOMAINE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_ANNULER_DOMAINE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DOMAINE(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOMAINE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_CREER_DOMAINE() {
		return "NOM_PB_CREER_DOMAINE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_CREER_DOMAINE(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOMAINE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_DOMAINE(), "");
		addZone(getNOM_EF_LIB_DOMAINE(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DOMAINE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DOMAINE() {
		return "NOM_PB_SUPPRIMER_DOMAINE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DOMAINE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_DOMAINE_SELECT()) ? Integer.parseInt(getVAL_LB_DOMAINE_SELECT()) : -1);

		if (indice != -1 && indice < getListeDomaine().size()) {
			DomaineEmploi de = getListeDomaine().get(indice);
			setDomaineEmploiCourant(de);
			addZone(getNOM_EF_LIB_DOMAINE(), de.getLibDomaineEmploi());
			addZone(getNOM_EF_CODE_DOMAINE(), de.getCodeDomaineEmploi());
			addZone(getNOM_ST_ACTION_DOMAINE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "domaines d'activité"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DOMAINE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_VALIDER_DOMAINE() {
		return "NOM_PB_VALIDER_DOMAINE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_VALIDER_DOMAINE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieDomaine(request))
			return false;

		if (!performControlerRegleGestionDomaine(request))
			return false;

		if (getVAL_ST_ACTION_DOMAINE() != null && getVAL_ST_ACTION_DOMAINE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_DOMAINE().equals(ACTION_CREATION)) {
				setDomaineEmploiCourant(new DomaineEmploi());
				getDomaineEmploiCourant().setCodeDomaineEmploi(getVAL_EF_CODE_DOMAINE());
				getDomaineEmploiCourant().setLibDomaineEmploi(getVAL_EF_LIB_DOMAINE());
				getDomaineEmploiCourant().creerDomaineEmploi(getTransaction());
				if (!getTransaction().isErreur())
					getListeDomaine().add(getDomaineEmploiCourant());
			} else if (getVAL_ST_ACTION_DOMAINE().equals(ACTION_SUPPRESSION)) {
				getDomaineEmploiCourant().supprimerDomaineEmploi(getTransaction());
				if (!getTransaction().isErreur())
					getListeDomaine().remove(getDomaineEmploiCourant());
				setDomaineEmploiCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeDomaine(request);
			addZone(getNOM_ST_ACTION_DOMAINE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un domaine Date de création : (09/09/11)
	 */
	private boolean performControlerSaisieDomaine(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_LIB_DOMAINE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code domaine d'activite not null
		if (getZone(getNOM_EF_CODE_DOMAINE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un domaine emploi Date de création :
	 * (09/09/11 11:04:00)
	 */
	private boolean performControlerRegleGestionDomaine(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un domaine utilisé sur une fiche emploi
		if (getVAL_ST_ACTION_DOMAINE().equals(ACTION_SUPPRESSION)
				&& FicheEmploi.listerFicheEmploiAvecDomaineEmploi(getTransaction(), getDomaineEmploiCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche emploi", "ce domaine"));
			return false;
		}

		// Vérification des contraintes d'unicité du domaine d'activité
		if (getVAL_ST_ACTION_DOMAINE().equals(ACTION_CREATION)) {

			for (DomaineEmploi domaine : getListeDomaine()) {
				if (domaine.getCodeDomaineEmploi().equals(getVAL_EF_CODE_DOMAINE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un domaine d'activité", "ce code"));
					return false;
				}
				if (domaine.getLibDomaineEmploi().equals(getVAL_EF_LIB_DOMAINE().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un domaine d'activité", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOMAINE Date
	 * de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_ST_ACTION_DOMAINE() {
		return "NOM_ST_ACTION_DOMAINE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_DOMAINE
	 * Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_ST_ACTION_DOMAINE() {
		return getZone(getNOM_ST_ACTION_DOMAINE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_DOMAINE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_EF_LIB_DOMAINE() {
		return "NOM_EF_LIB_DOMAINE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_DOMAINE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_EF_LIB_DOMAINE() {
		return getZone(getNOM_EF_LIB_DOMAINE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DOMAINE Date de création
	 * : (09/09/11 11:54:33)
	 * 
	 */
	private String[] getLB_DOMAINE() {
		if (LB_DOMAINE == null)
			LB_DOMAINE = initialiseLazyLB();
		return LB_DOMAINE;
	}

	/**
	 * Setter de la liste: LB_DOMAINE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	private void setLB_DOMAINE(String[] newLB_DOMAINE) {
		LB_DOMAINE = newLB_DOMAINE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DOMAINE Date de création
	 * : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_LB_DOMAINE() {
		return "NOM_LB_DOMAINE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DOMAINE_SELECT Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_LB_DOMAINE_SELECT() {
		return "NOM_LB_DOMAINE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DOMAINE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String[] getVAL_LB_DOMAINE() {
		return getLB_DOMAINE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DOMAINE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_LB_DOMAINE_SELECT() {
		return getZone(getNOM_LB_DOMAINE_SELECT());
	}

	private String[] LB_FAMILLE;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_FAMILLE Date de
	 * création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_PB_ANNULER_FAMILLE() {
		return "NOM_PB_ANNULER_FAMILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public boolean performPB_ANNULER_FAMILLE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_FAMILLE(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_FAMILLE Date de
	 * création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_PB_CREER_FAMILLE() {
		return "NOM_PB_CREER_FAMILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public boolean performPB_CREER_FAMILLE(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_ACTION_FAMILLE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_FAMILLE(), "");
		addZone(getNOM_EF_FAMILLE(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_FAMILLE Date de
	 * création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_FAMILLE() {
		return "NOM_PB_SUPPRIMER_FAMILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public boolean performPB_SUPPRIMER_FAMILLE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_FAMILLE_SELECT()) ? Integer.parseInt(getVAL_LB_FAMILLE_SELECT()) : -1);

		if (indice != -1 && indice < getListeFamille().size()) {
			FamilleEmploi f = getListeFamille().get(indice);
			setFamilleCourante(f);
			addZone(getNOM_EF_FAMILLE(), f.getLibFamilleEmploi());
			addZone(getNOM_EF_CODE_FAMILLE(), f.getCodeFamilleEmploi());
			addZone(getNOM_ST_ACTION_FAMILLE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "familles d'emploi"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_FAMILLE Date de
	 * création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_PB_VALIDER_FAMILLE() {
		return "NOM_PB_VALIDER_FAMILLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public boolean performPB_VALIDER_FAMILLE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieFamille(request))
			return false;

		if (!performControlerRegleGestionFamille(request))
			return false;

		if (getVAL_ST_ACTION_FAMILLE() != null && getVAL_ST_ACTION_FAMILLE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_FAMILLE().equals(ACTION_CREATION)) {
				setFamilleCourante(new FamilleEmploi());
				getFamilleCourante().setLibFamilleEmploi(getVAL_EF_FAMILLE());
				getFamilleCourante().setCodeFamilleEmploi(getVAL_EF_CODE_FAMILLE());
				getFamilleCourante().creerFamilleEmploi(getTransaction());
				if (!getTransaction().isErreur())
					getListeFamille().add(getFamilleCourante());
			} else if (getVAL_ST_ACTION_FAMILLE().equals(ACTION_SUPPRESSION)) {
				getFamilleCourante().supprimerFamilleEmploi(getTransaction());
				if (!getTransaction().isErreur())
					getListeFamille().remove(getFamilleCourante());
				setFamilleCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeFamille(request);
			addZone(getNOM_ST_ACTION_FAMILLE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une famille d'emploi Date de création :
	 * (12/09/11)
	 */
	private boolean performControlerSaisieFamille(HttpServletRequest request) throws Exception {

		// Verification libellé famille not null
		if (getZone(getNOM_EF_FAMILLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		// Verification code famille not null
		if (getZone(getNOM_EF_CODE_FAMILLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une famille d'emploi Date de création :
	 * (12/09/11 11:04:00)
	 */
	private boolean performControlerRegleGestionFamille(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une famille d'emploi utilisée sur une
		// fiche emploi
		if (getVAL_ST_ACTION_FAMILLE().equals(ACTION_SUPPRESSION)
				&& FicheEmploi.listerFicheEmploiAvecFamilleEmploi(getTransaction(), getFamilleCourante()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche emploi", "cette famille d'emploi"));
			return false;
		}

		// Vérification des contraintes d'unicité de la famille d'emploi
		if (getVAL_ST_ACTION_FAMILLE().equals(ACTION_CREATION)) {

			for (FamilleEmploi famille : getListeFamille()) {
				if (famille.getCodeFamilleEmploi().equals(getVAL_EF_CODE_FAMILLE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @ – Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une famille d'emploi", "ce code"));
					return false;
				}
				if (famille.getLibFamilleEmploi().equals(getVAL_EF_FAMILLE().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une famille d'emploi", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_FAMILLE Date
	 * de création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_ST_ACTION_FAMILLE() {
		return "NOM_ST_ACTION_FAMILLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_FAMILLE
	 * Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public String getVAL_ST_ACTION_FAMILLE() {
		return getZone(getNOM_ST_ACTION_FAMILLE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FAMILLE Date de
	 * création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_EF_FAMILLE() {
		return "NOM_EF_FAMILLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_FAMILLE Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public String getVAL_EF_FAMILLE() {
		return getZone(getNOM_EF_FAMILLE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_FAMILLE Date de création
	 * : (09/09/11 13:30:20)
	 * 
	 */
	private String[] getLB_FAMILLE() {
		if (LB_FAMILLE == null)
			LB_FAMILLE = initialiseLazyLB();
		return LB_FAMILLE;
	}

	/**
	 * Setter de la liste: LB_FAMILLE Date de création : (09/09/11 13:30:20)
	 * 
	 */
	private void setLB_FAMILLE(String[] newLB_FAMILLE) {
		LB_FAMILLE = newLB_FAMILLE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FAMILLE Date de création
	 * : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_LB_FAMILLE() {
		return "NOM_LB_FAMILLE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FAMILLE_SELECT Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public String getNOM_LB_FAMILLE_SELECT() {
		return "NOM_LB_FAMILLE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_FAMILLE Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public String[] getVAL_LB_FAMILLE() {
		return getLB_FAMILLE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_FAMILLE Date de création : (09/09/11 13:30:20)
	 * 
	 */
	public String getVAL_LB_FAMILLE_SELECT() {
		return getZone(getNOM_LB_FAMILLE_SELECT());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_ANNULER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), "");
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_CREER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {

		addZone(getNOM_EF_ACTION_CADRE_EMPLOI(), ACTION_CREATION);
		addZone(getNOM_EF_CADRE_EMPLOI(), "");
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_CADRE_EMPLOI_SELECT()) ? Integer.parseInt(getVAL_LB_CADRE_EMPLOI_SELECT()) : -1);

		if (indice != -1 && indice < getListeCadreEmploi().size()) {
			CadreEmploi c = getListeCadreEmploi().get(indice);
			setCadreEmploiCourant(c);

			FiliereGrade fi = (FiliereGrade) getHashFiliere().get(c.getCdfili());
			int ligneFiliere = getListeFiliere().indexOf(fi);
			addZone(getNOM_LB_FILIERE_SELECT(), String.valueOf(ligneFiliere + 1));

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
				// on recupere la filiere
				int indice = (Services.estNumerique(getVAL_LB_FILIERE_SELECT()) ? Integer.parseInt(getVAL_LB_FILIERE_SELECT()) : -1);
				String codFiliere = ((FiliereGrade) getListeFiliere().get(indice - 1)).getCodeFiliere();
				setCadreEmploiCourant(new CadreEmploi());
				getCadreEmploiCourant().setLibCadreEmploi(getVAL_EF_CADRE_EMPLOI());
				getCadreEmploiCourant().setCdfili(codFiliere);
				getCadreEmploiCourant().creerCadreEmploi(getTransaction());
				if (!getTransaction().isErreur())
					getListeCadreEmploi().add(getCadreEmploiCourant());
			} else if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_SUPPRESSION)) {
				getCadreEmploiCourant().supprimerCadreEmploi(getTransaction());
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
	 * Contrôle les zones saisies d'un cadre emploi Date de création :
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
		// la filiere est obligatoire
		if (getVAL_LB_FILIERE_SELECT().equals("0")) {
			// ERR002:La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "filière"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une filiere Date de création : (12/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionCadreEmploi(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un cadre emploi utilisée sur
		// un grade generique
		if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_SUPPRESSION)
				&& GradeGenerique.listerGradeGeneriqueAvecCadreEmploi(getTransaction(), getCadreEmploiCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un grade générique", "ce cadre emploi"));
			return false;
		}

		// Vérification des contraintes d'unicité du cadre emploi
		if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_CREATION)) {

			// Vérification des contraintes d'unicité du cadre emploi
			if (getVAL_EF_ACTION_CADRE_EMPLOI().equals(ACTION_CREATION)) {
				for (CadreEmploi cadre : getListeCadreEmploi()) {
					if (cadre.getLibCadreEmploi().equals(getVAL_EF_CADRE_EMPLOI().toUpperCase())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un cadre emploi", "ce libellé"));
						return false;
					}
				}
			}
		}

		return true;
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CADRE Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_LB_CADRE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_CADRE_EMPLOI_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DIPLOME Date de
	 * création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_PB_ANNULER_DIPLOME() {
		return "NOM_PB_ANNULER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public boolean performPB_ANNULER_DIPLOME(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_ACTION_DIPLOME(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DIPLOME Date de
	 * création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_PB_CREER_DIPLOME() {
		return "NOM_PB_CREER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public boolean performPB_CREER_DIPLOME(HttpServletRequest request) throws Exception {

		addZone(getNOM_EF_ACTION_DIPLOME(), ACTION_CREATION);
		addZone(getNOM_EF_DIPLOME(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DIPLOME Date de
	 * création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DIPLOME() {
		return "NOM_PB_SUPPRIMER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DIPLOME(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_DIPLOME_SELECT()) ? Integer.parseInt(getVAL_LB_DIPLOME_SELECT()) : -1);

		if (indice != -1 && indice < getListeDiplome().size()) {
			DiplomeGenerique d = getListeDiplome().get(indice);
			setDiplomeCourant(d);
			addZone(getNOM_EF_DIPLOME(), d.getLibDiplomeGenerique());
			addZone(getNOM_EF_ACTION_DIPLOME(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "diplômes générique"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DIPLOME Date de
	 * création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_PB_VALIDER_DIPLOME() {
		return "NOM_PB_VALIDER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public boolean performPB_VALIDER_DIPLOME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieDiplome(request))
			return false;

		if (!performControlerRegleGestionDiplome(request))
			return false;

		if (getVAL_EF_ACTION_DIPLOME() != null && getVAL_EF_ACTION_DIPLOME() != Const.CHAINE_VIDE) {
			if (getVAL_EF_ACTION_DIPLOME().equals(ACTION_CREATION)) {
				setDiplomeCourant(new DiplomeGenerique());
				getDiplomeCourant().setLibDiplomeGenerique(getVAL_EF_DIPLOME());
				getDiplomeCourant().creerDiplomeGenerique(getTransaction());
				if (!getTransaction().isErreur())
					getListeDiplome().add(getDiplomeCourant());
			} else if (getVAL_EF_ACTION_DIPLOME().equals(ACTION_SUPPRESSION)) {
				getDiplomeCourant().supprimerDiplomeGenerique(getTransaction());
				if (!getTransaction().isErreur())
					getListeDiplome().remove(getDiplomeCourant());
				setDiplomeCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeDiplome(request);
			addZone(getNOM_EF_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un diplome Date de création : (12/09/11)
	 */
	private boolean performControlerSaisieDiplome(HttpServletRequest request) throws Exception {

		// ************************************
		// Verification lib competence not null
		// ************************************
		if (getZone(getNOM_EF_DIPLOME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un diplome Date de création : (12/09/11
	 * 11:04:00)
	 */
	private boolean performControlerRegleGestionDiplome(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un diplome utilisée sur une fiche
		// emploi
		if (getVAL_EF_ACTION_DIPLOME().equals(ACTION_SUPPRESSION)
				&& FicheEmploi.listerFicheEmploiAvecDiplome(getTransaction(), getDiplomeCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche emploi", "ce diplome"));
			return false;
		}

		// Verification si suppression d'un diplome utilisée sur une fiche poste
		if (getVAL_EF_ACTION_DIPLOME().equals(ACTION_SUPPRESSION)
				&& FichePoste.listerFichePosteAvecDiplome(getTransaction(), getDiplomeCourant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche de poste", "ce diplome"));
			return false;
		}

		// Vérification des contraintes d'unicité du diplome
		if (getVAL_EF_ACTION_DIPLOME().equals(ACTION_CREATION)) {
			for (DiplomeGenerique diplome : getListeDiplome()) {
				if (diplome.getLibDiplomeGenerique().equals(getVAL_EF_DIPLOME().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un diplome", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACTION_DIPLOME Date
	 * de création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_EF_ACTION_DIPLOME() {
		return "NOM_EF_ACTION_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACTION_DIPLOME Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public String getVAL_EF_ACTION_DIPLOME() {
		return getZone(getNOM_EF_ACTION_DIPLOME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DIPLOME Date de
	 * création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_EF_DIPLOME() {
		return "NOM_EF_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DIPLOME Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public String getVAL_EF_DIPLOME() {
		return getZone(getNOM_EF_DIPLOME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME Date de création
	 * : (09/09/11 13:37:43)
	 * 
	 */
	private String[] getLB_DIPLOME() {
		if (LB_DIPLOME == null)
			LB_DIPLOME = initialiseLazyLB();
		return LB_DIPLOME;
	}

	/**
	 * Setter de la liste: LB_DIPLOME Date de création : (09/09/11 13:37:43)
	 * 
	 */
	private void setLB_DIPLOME(String[] newLB_DIPLOME) {
		LB_DIPLOME = newLB_DIPLOME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME Date de création
	 * : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_LB_DIPLOME() {
		return "NOM_LB_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_SELECT Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public String getNOM_LB_DIPLOME_SELECT() {
		return "NOM_LB_DIPLOME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public String[] getVAL_LB_DIPLOME() {
		return getLB_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME Date de création : (09/09/11 13:37:43)
	 * 
	 */
	public String getVAL_LB_DIPLOME_SELECT() {
		return getZone(getNOM_LB_DIPLOME_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_ANNULER_CATEGORIE() {
		return "NOM_PB_ANNULER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_ANNULER_CATEGORIE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CATEGORIE(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_CREER_CATEGORIE() {
		return "NOM_PB_CREER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_CREER_CATEGORIE(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_CATEGORIE(), ACTION_CREATION);
		addZone(getNOM_EF_LIB_CATEGORIE(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CATEGORIE() {
		return "NOM_PB_SUPPRIMER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CATEGORIE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_CATEGORIE_SELECT()) ? Integer.parseInt(getVAL_LB_CATEGORIE_SELECT()) : -1);

		if (indice != -1 && indice < getListeCategorie().size()) {
			Categorie cat = getListeCategorie().get(indice);
			setCategorieCourante(cat);
			addZone(getNOM_EF_LIB_CATEGORIE(), cat.getLibCategorie());
			addZone(getNOM_ST_ACTION_CATEGORIE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "catégorie"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_PB_VALIDER_CATEGORIE() {
		return "NOM_PB_VALIDER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean performPB_VALIDER_CATEGORIE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieCategorie(request))
			return false;

		if (!performControlerRegleGestionCategorie(request))
			return false;

		if (getVAL_ST_ACTION_CATEGORIE() != null && getVAL_ST_ACTION_CATEGORIE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_CATEGORIE().equals(ACTION_CREATION)) {
				setCategorieCourante(new Categorie());
				getCategorieCourante().setLibCategorie(getVAL_EF_LIB_CATEGORIE());
				getCategorieCourante().creerCategorie(getTransaction());
				if (!getTransaction().isErreur())
					getListeCategorie().add(getCategorieCourante());
			} else if (getVAL_ST_ACTION_CATEGORIE().equals(ACTION_SUPPRESSION)) {
				getCategorieCourante().supprimerCategorie(getTransaction());
				if (!getTransaction().isErreur())
					getListeCategorie().remove(getCategorieCourante());
				setCategorieCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeCategorie(request);
			addZone(getNOM_ST_ACTION_CATEGORIE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un domaine Date de création : (09/09/11)
	 */
	private boolean performControlerSaisieCategorie(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_LIB_CATEGORIE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une categorie emploi Date de création :
	 * (09/09/11 11:04:00)
	 */
	private boolean performControlerRegleGestionCategorie(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une categorie utilisée sur une fiche
		// emploi
		if (getVAL_ST_ACTION_CATEGORIE().equals(ACTION_SUPPRESSION)
				&& CategorieFE.listerCategorieFEAvecCategorie(getTransaction(), getCategorieCourante()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche emploi", "cette catégorie"));
			return false;
		}

		// Vérification des contraintes d'unicité de la categorie d'emploi
		if (getVAL_ST_ACTION_CATEGORIE().equals(ACTION_CREATION)) {
			for (Categorie categorie : getListeCategorie()) {
				if (categorie.getLibCategorie().equals(getVAL_EF_LIB_CATEGORIE().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une catégorie", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_CATEGORIE
	 * Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_ST_ACTION_CATEGORIE() {
		return "NOM_ST_ACTION_CATEGORIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_CATEGORIE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_ST_ACTION_CATEGORIE() {
		return getZone(getNOM_ST_ACTION_CATEGORIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_CATEGORIE Date
	 * de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_EF_LIB_CATEGORIE() {
		return "NOM_EF_LIB_CATEGORIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_CATEGORIE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_EF_LIB_CATEGORIE() {
		return getZone(getNOM_EF_LIB_CATEGORIE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	private String[] getLB_CATEGORIE() {
		if (LB_CATEGORIE == null)
			LB_CATEGORIE = initialiseLazyLB();
		return LB_CATEGORIE;
	}

	/**
	 * Setter de la liste: LB_CATEGORIE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	private void setLB_CATEGORIE(String[] newLB_CATEGORIE) {
		LB_CATEGORIE = newLB_CATEGORIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CATEGORIE Date de
	 * création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_LB_CATEGORIE() {
		return "NOM_LB_CATEGORIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CATEGORIE_SELECT Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getNOM_LB_CATEGORIE_SELECT() {
		return "NOM_LB_CATEGORIE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CATEGORIE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String[] getVAL_LB_CATEGORIE() {
		return getLB_CATEGORIE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CATEGORIE Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public String getVAL_LB_CATEGORIE_SELECT() {
		return getZone(getNOM_LB_CATEGORIE_SELECT());
	}

	private DomaineEmploi getDomaineEmploiCourant() {
		return domaineEmploiCourant;
	}

	private void setDomaineEmploiCourant(DomaineEmploi domaineEmploiCourant) {
		this.domaineEmploiCourant = domaineEmploiCourant;
	}

	private ArrayList<DomaineEmploi> getListeDomaine() {
		return listeDomaine;
	}

	private void setListeDomaine(ArrayList<DomaineEmploi> listeDomaine) {
		this.listeDomaine = listeDomaine;
	}

	private FamilleEmploi getFamilleCourante() {
		return familleCourante;
	}

	private void setFamilleCourante(FamilleEmploi familleCourante) {
		this.familleCourante = familleCourante;
	}

	private CadreEmploi getCadreEmploiCourant() {
		return cadreEmploiCourant;
	}

	private void setCadreEmploiCourant(CadreEmploi cadreEmploiCourant) {
		this.cadreEmploiCourant = cadreEmploiCourant;
	}

	private CodeRome getCodeRomeCourant() {
		return codeRomeCourant;
	}

	private void setCodeRomeCourant(CodeRome codeRomeCourant) {
		this.codeRomeCourant = codeRomeCourant;
	}

	private DiplomeGenerique getDiplomeCourant() {
		return diplomeCourant;
	}

	private void setDiplomeCourant(DiplomeGenerique diplomeCourant) {
		this.diplomeCourant = diplomeCourant;
	}

	private void setListeCadreEmploi(ArrayList<CadreEmploi> listeCadreEmploi) {
		this.listeCadreEmploi = listeCadreEmploi;
	}

	private void setListeCodeRome(ArrayList<CodeRome> listeCodeRome) {
		this.listeCodeRome = listeCodeRome;
	}

	private void setListeDiplome(ArrayList<DiplomeGenerique> listeDiplome) {
		this.listeDiplome = listeDiplome;
	}

	private ArrayList<FamilleEmploi> getListeFamille() {
		return listeFamille;
	}

	private void setListeFamille(ArrayList<FamilleEmploi> listeFamille) {
		this.listeFamille = listeFamille;
	}

	private Categorie getCategorieCourante() {
		return categorieCourante;
	}

	private void setCategorieCourante(Categorie categorieCourante) {
		this.categorieCourante = categorieCourante;
	}

	private ArrayList<Categorie> getListeCategorie() {
		return listeCategorie;
	}

	private void setListeCategorie(ArrayList<Categorie> listeCategorie) {
		this.listeCategorie = listeCategorie;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_DOMAINE Date
	 * de création : (12/09/11 16:28:59)
	 * 
	 */
	public String getNOM_EF_CODE_DOMAINE() {
		return "NOM_EF_CODE_DOMAINE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_DOMAINE Date de création : (12/09/11 16:28:59)
	 * 
	 */
	public String getVAL_EF_CODE_DOMAINE() {
		return getZone(getNOM_EF_CODE_DOMAINE());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (09/09/11 11:54:33)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_DIPLOME
			if (testerParametre(request, getNOM_PB_ANNULER_DIPLOME())) {
				return performPB_ANNULER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_CREER_DIPLOME
			if (testerParametre(request, getNOM_PB_CREER_DIPLOME())) {
				return performPB_CREER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DIPLOME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DIPLOME())) {
				return performPB_SUPPRIMER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_VALIDER_DIPLOME
			if (testerParametre(request, getNOM_PB_VALIDER_DIPLOME())) {
				return performPB_VALIDER_DIPLOME(request);
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

			// Si clic sur le bouton PB_ANNULER_FAMILLE
			if (testerParametre(request, getNOM_PB_ANNULER_FAMILLE())) {
				return performPB_ANNULER_FAMILLE(request);
			}

			// Si clic sur le bouton PB_CREER_FAMILLE
			if (testerParametre(request, getNOM_PB_CREER_FAMILLE())) {
				return performPB_CREER_FAMILLE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_FAMILLE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_FAMILLE())) {
				return performPB_SUPPRIMER_FAMILLE(request);
			}

			// Si clic sur le bouton PB_VALIDER_FAMILLE
			if (testerParametre(request, getNOM_PB_VALIDER_FAMILLE())) {
				return performPB_VALIDER_FAMILLE(request);
			}

			// Si clic sur le bouton PB_ANNULER_DOMAINE
			if (testerParametre(request, getNOM_PB_ANNULER_DOMAINE())) {
				return performPB_ANNULER_DOMAINE(request);
			}

			// Si clic sur le bouton PB_CREER_DOMAINE
			if (testerParametre(request, getNOM_PB_CREER_DOMAINE())) {
				return performPB_CREER_DOMAINE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOMAINE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DOMAINE())) {
				return performPB_SUPPRIMER_DOMAINE(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOMAINE
			if (testerParametre(request, getNOM_PB_VALIDER_DOMAINE())) {
				return performPB_VALIDER_DOMAINE(request);
			}

			// Si clic sur le bouton PB_ANNULER_CATEGORIE
			if (testerParametre(request, getNOM_PB_ANNULER_CATEGORIE())) {
				return performPB_ANNULER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_CREER_CATEGORIE
			if (testerParametre(request, getNOM_PB_CREER_CATEGORIE())) {
				return performPB_CREER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CATEGORIE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CATEGORIE())) {
				return performPB_SUPPRIMER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_VALIDER_CATEGORIE
			if (testerParametre(request, getNOM_PB_VALIDER_CATEGORIE())) {
				return performPB_VALIDER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_ANNULER_CODE_ROME
			if (testerParametre(request, getNOM_PB_ANNULER_CODE_ROME())) {
				return performPB_ANNULER_CODE_ROME(request);
			}

			// Si clic sur le bouton PB_CREER_CODE_ROME
			if (testerParametre(request, getNOM_PB_CREER_CODE_ROME())) {
				return performPB_CREER_CODE_ROME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CODE_ROME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CODE_ROME())) {
				return performPB_SUPPRIMER_CODE_ROME(request);
			}

			// Si clic sur le bouton PB_VALIDER_CODE_ROME
			if (testerParametre(request, getNOM_PB_VALIDER_CODE_ROME())) {
				return performPB_VALIDER_CODE_ROME(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (12/09/11 16:36:11)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEFicheEmploi.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-PE-FICHEEMPLOI";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_FAMILLE Date
	 * de création : (12/09/11 16:36:11)
	 * 
	 */
	public String getNOM_EF_CODE_FAMILLE() {
		return "NOM_EF_CODE_FAMILLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_FAMILLE Date de création : (12/09/11 16:36:11)
	 * 
	 */
	public String getVAL_EF_CODE_FAMILLE() {
		return getZone(getNOM_EF_CODE_FAMILLE());
	}

	private ArrayList<CadreEmploi> getListeCadreEmploi() {
		return listeCadreEmploi;
	}

	private ArrayList<DiplomeGenerique> getListeDiplome() {
		return listeDiplome;
	}

	private ArrayList<CodeRome> getListeCodeRome() {
		return listeCodeRome;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_ANNULER_CODE_ROME() {
		return "NOM_PB_ANNULER_CODE_ROME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_ANNULER_CODE_ROME(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_ACTION_CODE_ROME(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_CREER_CODE_ROME() {
		return "NOM_PB_CREER_CODE_ROME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_CREER_CODE_ROME(HttpServletRequest request) throws Exception {

		addZone(getNOM_EF_ACTION_CODE_ROME(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_ROME(), "");
		addZone(getNOM_EF_DESC_CODE_ROME(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CODE_ROME() {
		return "NOM_PB_SUPPRIMER_CODE_ROME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CODE_ROME(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_CODE_ROME_SELECT()) ? Integer.parseInt(getVAL_LB_CODE_ROME_SELECT()) : -1);

		if (indice != -1 && indice < getListeCodeRome().size()) {
			CodeRome cr = getListeCodeRome().get(indice);
			setCodeRomeCourant(cr);
			addZone(getNOM_EF_CODE_ROME(), cr.getLibCodeRome());
			addZone(getNOM_EF_DESC_CODE_ROME(), cr.getDescCodeRome());
			addZone(getNOM_EF_ACTION_CODE_ROME(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "code rome"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_PB_VALIDER_CODE_ROME() {
		return "NOM_PB_VALIDER_CODE_ROME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public boolean performPB_VALIDER_CODE_ROME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieCodeRome(request))
			return false;

		if (!performControlerRegleGestionCodeRome(request))
			return false;

		if (getVAL_EF_ACTION_CODE_ROME() != null && getVAL_EF_ACTION_CODE_ROME() != Const.CHAINE_VIDE) {
			if (getVAL_EF_ACTION_CODE_ROME().equals(ACTION_CREATION)) {
				setCodeRomeCourant(new CodeRome());
				getCodeRomeCourant().setLibCodeRome(getVAL_EF_CODE_ROME());
				getCodeRomeCourant().setDescCodeRome(getVAL_EF_DESC_CODE_ROME());
				getCodeRomeCourant().creerCodeRome(getTransaction());
				if (!getTransaction().isErreur())
					getListeCodeRome().add(getCodeRomeCourant());
			} else if (getVAL_EF_ACTION_CODE_ROME().equals(ACTION_SUPPRESSION)) {
				getCodeRomeCourant().supprimerCodeRome(getTransaction());
				if (!getTransaction().isErreur())
					getListeCodeRome().remove(getCodeRomeCourant());
				setCodeRomeCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeCodeRome(request);
			addZone(getNOM_EF_ACTION_CODE_ROME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un code rome Date de création : (12/09/11)
	 */
	private boolean performControlerSaisieCodeRome(HttpServletRequest request) throws Exception {

		// ************************************
		// Verification lib code rome not null
		// ************************************
		if (getZone(getNOM_EF_CODE_ROME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// ************************************
		// Verification desc code rome not null
		// ************************************
		if (getZone(getNOM_EF_DESC_CODE_ROME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un code rome Date de création :
	 * (12/09/11 11:04:00)
	 */
	private boolean performControlerRegleGestionCodeRome(HttpServletRequest request) throws Exception {

		// **********************************************************************
		// Verification si suppression d'un code rome utilisé sur une fiche
		// emploi
		// **********************************************************************
		if (getVAL_EF_ACTION_CODE_ROME().equals(ACTION_SUPPRESSION)
				&& FicheEmploi.listerFicheEmploiAvecCodeRome(getTransaction(), getCodeRomeCourant().getIdCodeRome()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche emploi", "ce code rome"));
			return false;
		}

		// Vérification des contraintes d'unicité du code rome
		if (getVAL_EF_ACTION_CODE_ROME().equals(ACTION_CREATION)) {
			for (CodeRome codeRome : getListeCodeRome()) {
				if (codeRome.getLibCodeRome().equals(getVAL_EF_CODE_ROME().toUpperCase())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un code rome", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACTION_CODE_ROME
	 * Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_EF_ACTION_CODE_ROME() {
		return "NOM_EF_ACTION_CODE_ROME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACTION_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_EF_ACTION_CODE_ROME() {
		return getZone(getNOM_EF_ACTION_CODE_ROME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_EF_CODE_ROME() {
		return "NOM_EF_CODE_ROME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_EF_CODE_ROME() {
		return getZone(getNOM_EF_CODE_ROME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_CODE_ROME Date
	 * de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_EF_DESC_CODE_ROME() {
		return "NOM_EF_DESC_CODE_ROME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_EF_DESC_CODE_ROME() {
		return getZone(getNOM_EF_DESC_CODE_ROME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	private String[] getLB_CODE_ROME() {
		if (LB_CODE_ROME == null)
			LB_CODE_ROME = initialiseLazyLB();
		return LB_CODE_ROME;
	}

	/**
	 * Setter de la liste: LB_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	private void setLB_CODE_ROME(String[] newLB_CODE_ROME) {
		LB_CODE_ROME = newLB_CODE_ROME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CODE_ROME Date de
	 * création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_LB_CODE_ROME() {
		return "NOM_LB_CODE_ROME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CODE_ROME_SELECT Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getNOM_LB_CODE_ROME_SELECT() {
		return "NOM_LB_CODE_ROME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String[] getVAL_LB_CODE_ROME() {
		return getLB_CODE_ROME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CODE_ROME Date de création : (09/09/11 13:36:47)
	 * 
	 */
	public String getVAL_LB_CODE_ROME_SELECT() {
		return getZone(getNOM_LB_CODE_ROME_SELECT());
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
}
