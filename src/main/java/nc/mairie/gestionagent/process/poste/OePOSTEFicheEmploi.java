package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Categorie;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.CodeRome;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.DomaineEmploi;
import nc.mairie.metier.parametrage.FamilleEmploi;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFE;
import nc.mairie.metier.poste.AutreAppellationEmploi;
import nc.mairie.metier.poste.CadreEmploiFE;
import nc.mairie.metier.poste.CategorieFE;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.NiveauEtudeFE;
import nc.mairie.metier.referentiel.NiveauEtude;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.spring.dao.metier.carriere.CategorieDao;
import nc.mairie.spring.dao.metier.parametrage.CadreEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.CodeRomeDao;
import nc.mairie.spring.dao.metier.parametrage.DiplomeGeneriqueDao;
import nc.mairie.spring.dao.metier.parametrage.DomaineEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.FamilleEmploiDao;
import nc.mairie.spring.dao.metier.poste.AutreAppellationEmploiDao;
import nc.mairie.spring.dao.metier.poste.CadreEmploiFEDao;
import nc.mairie.spring.dao.metier.poste.CategorieFEDao;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.VariableActivite;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFicheEmploi Date de création : (21/06/11 16:27:37)
 * 
 */
public class OePOSTEFicheEmploi extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_COMPETENCE = 3;
	public static final int STATUT_ACTI_PRINC = 2;
	public static final int STATUT_SUIVI = 1;
	public static final int STATUT_RECHERCHE_AVANCEE = 11;

	private String[] LB_AUTRE_APPELLATION;
	private String[] LB_CADRE_EMPLOI;
	private String[] LB_CADRE_EMPLOI_MULTI;
	private String[] LB_CATEGORIE;
	private String[] LB_DIPLOME;
	private String[] LB_DIPLOME_MULTI;
	private String[] LB_DOMAINE;
	private String[] LB_FAMILLE_EMPLOI;
	private String[] LB_NIVEAU_ETUDE;
	private String[] LB_NIVEAU_ETUDE_MULTI;

	private ArrayList<AutreAppellationEmploi> listeAutreAppellationMulti;
	private ArrayList<AutreAppellationEmploi> listeAutreAppellationASupprimer;
	private ArrayList<AutreAppellationEmploi> listeAutreAppellationAAjouter;
	private ArrayList<DomaineEmploi> listeDomaine;
	private ArrayList<FamilleEmploi> listeFamille;
	private ArrayList<Categorie> listeCategorie;
	private ArrayList<Categorie> listeCategorieMulti;
	private ArrayList<Categorie> listeCategorieASupprimer;
	private ArrayList<Categorie> listeCategorieAAjouter;
	private ArrayList<CadreEmploi> listeCadresEmploi;
	private ArrayList<CadreEmploi> listeCadresEmploiMulti;
	private ArrayList<CadreEmploi> listeCadresEmploiAAjouter;
	private ArrayList<CadreEmploi> listeCadresEmploiASupprimer;
	private ArrayList<NiveauEtude> listeNiveauEtude;
	private ArrayList<NiveauEtude> listeNiveauEtudeMulti;
	private ArrayList<NiveauEtude> listeNiveauEtudeAAjouter;
	private ArrayList<NiveauEtude> listeNiveauEtudeASupprimer;
	private ArrayList<DiplomeGenerique> listeDiplome;
	private ArrayList<DiplomeGenerique> listeDiplomeMulti;
	private ArrayList<DiplomeGenerique> listeDiplomeAAjouter;
	private ArrayList<DiplomeGenerique> listeDiplomeASupprimer;
	private ArrayList<Activite> listeActiPrincMulti;
	private ArrayList<Activite> listeActiPrincAAjouter;
	private ArrayList<Activite> listeActiPrincASupprimer;
	private ArrayList<Competence> listeSavoirMulti;
	private ArrayList<Competence> listeSavoirAAjouter;
	private ArrayList<Competence> listeSavoirASupprimer;
	private ArrayList<Competence> listeSavoirFaireMulti;
	private ArrayList<Competence> listeSavoirFaireAAjouter;
	private ArrayList<Competence> listeSavoirFaireASupprimer;
	private ArrayList<Competence> listeComportementMulti;
	private ArrayList<Competence> listeComportementAAjouter;
	private ArrayList<Competence> listeComportementASupprimer;
	private TypeCompetence typeCompetenceCourant;
	private ArrayList<CodeRome> listeCodeRome;

	private FicheEmploi ficheEmploiCourant;
	private boolean suppression;
	private boolean suppressionLienFE_FP;

	public String focus = null;
	private boolean afficherListeCategorie = false;
	private boolean afficherListeCadre = false;
	private boolean afficherListeNivEt = false;
	private boolean afficherListeDiplome = false;

	private String messageInf = Const.CHAINE_VIDE;

	public String ACTION_RECHERCHE = "Recherche.";
	public String ACTION_CREATION = "Création.";
	public String ACTION_DUPLICATION = "Duplication.";
	public String ACTION_MODIFICATION = "Modification.";

	private CadreEmploiDao cadreEmploiDao;
	private CadreEmploiFEDao cadreEmploiFEDao;
	private CodeRomeDao codeRomeDao;
	private DiplomeGeneriqueDao diplomeGeneriqueDao;
	private DomaineEmploiDao domaineEmploiDao;
	private FamilleEmploiDao familleEmploiDao;
	private TypeCompetenceDao typeCompetenceDao;
	private AutreAppellationEmploiDao autreAppellationEmploiDao;
	private CategorieDao categorieDao;
	private CategorieFEDao categorieFEDao;

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTIVITE_PRINCIPALE
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_ACTIVITE_PRINCIPALE() {
		return "NOM_ST_ACTIVITE_PRINCIPALE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTIVITE_PRINCIPALE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_ACTIVITE_PRINCIPALE() {
		return getZone(getNOM_ST_ACTIVITE_PRINCIPALE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTRE_APPELLATION
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_AUTRE_APPELLATION() {
		return "NOM_ST_AUTRE_APPELLATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_AUTRE_APPELLATION Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_AUTRE_APPELLATION() {
		return getZone(getNOM_ST_AUTRE_APPELLATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI() {
		return "NOM_ST_CADRE_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CADRE_EMPLOI
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI() {
		return getZone(getNOM_ST_CADRE_EMPLOI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_CATEGORIE() {
		return "NOM_ST_CATEGORIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_CATEGORIE() {
		return getZone(getNOM_ST_CATEGORIE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIPLOME Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_DIPLOME() {
		return "NOM_ST_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIPLOME Date
	 * de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_DIPLOME() {
		return getZone(getNOM_ST_DIPLOME());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DOMAINE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_DOMAINE() {
		return "NOM_ST_DOMAINE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DOMAINE Date
	 * de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_DOMAINE() {
		return getZone(getNOM_ST_DOMAINE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FAMILLE_EMPLOI Date
	 * de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_FAMILLE_EMPLOI() {
		return "NOM_ST_FAMILLE_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FAMILLE_EMPLOI
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_FAMILLE_EMPLOI() {
		return getZone(getNOM_ST_FAMILLE_EMPLOI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NIVEAU_ETUDE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_ST_NIVEAU_ETUDE() {
		return "NOM_ST_NIVEAU_ETUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NIVEAU_ETUDE
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_ST_NIVEAU_ETUDE() {
		return getZone(getNOM_ST_NIVEAU_ETUDE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_ROME Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_EF_CODE_ROME() {
		return "NOM_EF_CODE_ROME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_ROME Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_EF_CODE_ROME() {
		return getZone(getNOM_EF_CODE_ROME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DEFINITION_EMPLOI
	 * Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_EF_DEFINITION_EMPLOI() {
		return "NOM_EF_DEFINITION_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DEFINITION_EMPLOI Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_EF_DEFINITION_EMPLOI() {
		return getZone(getNOM_EF_DEFINITION_EMPLOI());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRECISIONS_DIPLOMES
	 * Date de création : (24/06/11 10:48:00)
	 * 
	 */
	public String getNOM_EF_PRECISIONS_DIPLOMES() {
		return "NOM_EF_PRECISIONS_DIPLOMES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRECISIONS_DIPLOMES Date de création : (24/06/11 10:48:00)
	 * 
	 */
	public String getVAL_EF_PRECISIONS_DIPLOMES() {
		return getZone(getNOM_EF_PRECISIONS_DIPLOMES());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_METIER Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_EF_NOM_METIER() {
		return "NOM_EF_NOM_METIER";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_METIER Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_EF_NOM_METIER() {
		return getZone(getNOM_EF_NOM_METIER());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_MAIRIE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_EF_REF_MAIRIE() {
		return "NOM_EF_REF_MAIRIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_MAIRIE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_EF_REF_MAIRIE() {
		return getZone(getNOM_EF_REF_MAIRIE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AUTRE_APPELLATION Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_AUTRE_APPELLATION() {
		if (LB_AUTRE_APPELLATION == null)
			LB_AUTRE_APPELLATION = initialiseLazyLB();
		return LB_AUTRE_APPELLATION;
	}

	/**
	 * Setter de la liste: LB_AUTRE_APPELLATION Date de création : (21/06/11
	 * 16:27:37)
	 * 
	 */
	private void setLB_AUTRE_APPELLATION(String[] newLB_AUTRE_APPELLATION) {
		LB_AUTRE_APPELLATION = newLB_AUTRE_APPELLATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AUTRE_APPELLATION Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_AUTRE_APPELLATION() {
		return "NOM_LB_AUTRE_APPELLATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AUTRE_APPELLATION_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_AUTRE_APPELLATION_SELECT() {
		return "NOM_LB_AUTRE_APPELLATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AUTRE_APPELLATION Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_AUTRE_APPELLATION() {
		return getLB_AUTRE_APPELLATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AUTRE_APPELLATION Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_AUTRE_APPELLATION_SELECT() {
		return getZone(getNOM_LB_AUTRE_APPELLATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CADRE_EMPLOI Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_CADRE_EMPLOI() {
		if (LB_CADRE_EMPLOI == null)
			LB_CADRE_EMPLOI = initialiseLazyLB();
		return LB_CADRE_EMPLOI;
	}

	/**
	 * Setter de la liste: LB_CADRE_EMPLOI Date de création : (21/06/11
	 * 16:27:37)
	 * 
	 */
	private void setLB_CADRE_EMPLOI(String[] newLB_CADRE_EMPLOI) {
		LB_CADRE_EMPLOI = newLB_CADRE_EMPLOI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CADRE_EMPLOI Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI() {
		return "NOM_LB_CADRE_EMPLOI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CADRE_EMPLOI_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_SELECT() {
		return "NOM_LB_CADRE_EMPLOI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CADRE_EMPLOI Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_CADRE_EMPLOI() {
		return getLB_CADRE_EMPLOI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CADRE_EMPLOI Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_CADRE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_CADRE_EMPLOI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CATEGORIE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_CATEGORIE() {
		if (LB_CATEGORIE == null)
			LB_CATEGORIE = initialiseLazyLB();
		return LB_CATEGORIE;
	}

	/**
	 * Setter de la liste: LB_CATEGORIE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	private void setLB_CATEGORIE(String[] newLB_CATEGORIE) {
		LB_CATEGORIE = newLB_CATEGORIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CATEGORIE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_CATEGORIE() {
		return "NOM_LB_CATEGORIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CATEGORIE_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_CATEGORIE_SELECT() {
		return "NOM_LB_CATEGORIE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CATEGORIE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_CATEGORIE() {
		return getLB_CATEGORIE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CATEGORIE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_CATEGORIE_SELECT() {
		return getZone(getNOM_LB_CATEGORIE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME Date de création
	 * : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_DIPLOME() {
		if (LB_DIPLOME == null)
			LB_DIPLOME = initialiseLazyLB();
		return LB_DIPLOME;
	}

	/**
	 * Setter de la liste: LB_DIPLOME Date de création : (21/06/11 16:27:37)
	 * 
	 */
	private void setLB_DIPLOME(String[] newLB_DIPLOME) {
		LB_DIPLOME = newLB_DIPLOME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME Date de création
	 * : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_DIPLOME() {
		return "NOM_LB_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_DIPLOME_SELECT() {
		return "NOM_LB_DIPLOME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_DIPLOME() {
		return getLB_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_DIPLOME_SELECT() {
		return getZone(getNOM_LB_DIPLOME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DOMAINE Date de création
	 * : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_DOMAINE() {
		if (LB_DOMAINE == null)
			LB_DOMAINE = initialiseLazyLB();
		return LB_DOMAINE;
	}

	/**
	 * Setter de la liste: LB_DOMAINE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	private void setLB_DOMAINE(String[] newLB_DOMAINE) {
		LB_DOMAINE = newLB_DOMAINE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DOMAINE Date de création
	 * : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_DOMAINE() {
		return "NOM_LB_DOMAINE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DOMAINE_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_DOMAINE_SELECT() {
		return "NOM_LB_DOMAINE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DOMAINE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_DOMAINE() {
		return getLB_DOMAINE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DOMAINE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_DOMAINE_SELECT() {
		return getZone(getNOM_LB_DOMAINE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_FAMILLE_EMPLOI Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_FAMILLE_EMPLOI() {
		if (LB_FAMILLE_EMPLOI == null)
			LB_FAMILLE_EMPLOI = initialiseLazyLB();
		return LB_FAMILLE_EMPLOI;
	}

	/**
	 * Setter de la liste: LB_FAMILLE_EMPLOI Date de création : (21/06/11
	 * 16:27:37)
	 * 
	 */
	private void setLB_FAMILLE_EMPLOI(String[] newLB_FAMILLE_EMPLOI) {
		LB_FAMILLE_EMPLOI = newLB_FAMILLE_EMPLOI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FAMILLE_EMPLOI Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_FAMILLE_EMPLOI() {
		return "NOM_LB_FAMILLE_EMPLOI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FAMILLE_EMPLOI_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_FAMILLE_EMPLOI_SELECT() {
		return "NOM_LB_FAMILLE_EMPLOI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_FAMILLE_EMPLOI Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_FAMILLE_EMPLOI() {
		return getLB_FAMILLE_EMPLOI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_FAMILLE_EMPLOI Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_FAMILLE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_FAMILLE_EMPLOI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NIVEAU_ETUDE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	private String[] getLB_NIVEAU_ETUDE() {
		if (LB_NIVEAU_ETUDE == null)
			LB_NIVEAU_ETUDE = initialiseLazyLB();
		return LB_NIVEAU_ETUDE;
	}

	/**
	 * Setter de la liste: LB_NIVEAU_ETUDE Date de création : (21/06/11
	 * 16:27:37)
	 * 
	 */
	private void setLB_NIVEAU_ETUDE(String[] newLB_NIVEAU_ETUDE) {
		LB_NIVEAU_ETUDE = newLB_NIVEAU_ETUDE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NIVEAU_ETUDE Date de
	 * création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE() {
		return "NOM_LB_NIVEAU_ETUDE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NIVEAU_ETUDE_SELECT Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE_SELECT() {
		return "NOM_LB_NIVEAU_ETUDE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NIVEAU_ETUDE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String[] getVAL_LB_NIVEAU_ETUDE() {
		return getLB_NIVEAU_ETUDE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NIVEAU_ETUDE Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public String getVAL_LB_NIVEAU_ETUDE_SELECT() {
		return getZone(getNOM_LB_NIVEAU_ETUDE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AUTRE_APPELLATION
	 * Date de création : (22/06/11 10:11:21)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AUTRE_APPELLATION() {
		return "NOM_PB_AJOUTER_AUTRE_APPELLATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 10:11:21)
	 * 
	 */
	public boolean performPB_AJOUTER_AUTRE_APPELLATION(HttpServletRequest request) throws Exception {

		if (Const.CHAINE_VIDE.equals(getVAL_EF_AUTRE_APPELLATION()))
			return true;

		if (getListeAutreAppellationMulti() == null)
			setListeAutreAppellationMulti(new ArrayList<AutreAppellationEmploi>());
		AutreAppellationEmploi aae = new AutreAppellationEmploi(Integer.valueOf(getFicheEmploiCourant()
				.getIdFicheEmploi()), getVAL_EF_AUTRE_APPELLATION());
		if (!getListeAutreAppellationMulti().contains(aae)) {
			getListeAutreAppellationMulti().add(aae);

			if (getListeAutreAppellationASupprimer().contains(aae)) {
				getListeAutreAppellationASupprimer().remove(aae);
			} else {
				getListeAutreAppellationAAjouter().add(aae);
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (22/06/11 10:11:21)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 10:11:21)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);

		setFicheEmploiCourant(null);
		setListeAutreAppellationMulti(null);
		setListeActiPrincMulti(null);
		setListeSavoirMulti(null);
		setListeSavoirFaireMulti(null);
		setListeComportementMulti(null);
		setListeCategorieMulti(null);
		setListeCadresEmploiMulti(null);
		setListeNiveauEtudeMulti(null);
		setListeDiplomeMulti(null);

		setAfficherListeCadre(false);
		setAfficherListeCategorie(false);
		setAfficherListeDiplome(false);
		setAfficherListeNivEt(false);

		viderFicheEmploi();
		setSuppression(false);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// dans le cas ou l on vient de la fiche de poste, on retourne sur la
		// fiche de poste
		if (null != getProcessAppelant() && OePOSTEFichePoste.class.equals(getProcessAppelant().getClass())) {
			setStatut(STATUT_PROCESS_APPELANT);
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (22/06/11 10:11:21)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 10:11:21)
	 * 
	 * RG_PE_FE_A04
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si suppression
		if (isSuppression()) {
			if (isSuppressionLienFE_FP()) {
				ArrayList<FEFP> lienFE_FP = FEFP.listerFEFPAvecFE(getTransaction(), getFicheEmploiCourant());
				for (int i = 0; i < lienFE_FP.size(); i++) {
					FEFP feFp = (FEFP) lienFE_FP.get(i);
					feFp.supprimerFEFP(getTransaction());
				}
			}
			if (suppressionFicheEmploiEtLien(getFicheEmploiCourant(), getTransaction())) {
				messageInf = MessageUtils
						.getMessage("INF101", "Fiche emploi " + getFicheEmploiCourant().getRefMairie());
				setFicheEmploiCourant(null);
				setSuppression(false);
				viderFicheEmploi();
				viderObjetsFicheEmploi();
			} else {
				return false;
			}
		} else {

			// Contrôle des champs
			if (!performControlerSaisie(request))
				return false;

			// Alimentation de la fiche emploi
			alimenterFicheEmploi(request);

			if (getFicheEmploiCourant().getIdFicheEmploi() == null) {
				// Création de la Ref Mairie
				int indiceDomaine = (Services.estNumerique(getVAL_LB_DOMAINE_SELECT()) ? Integer
						.parseInt(getVAL_LB_DOMAINE_SELECT()) : -1);
				int indiceFamille = (Services.estNumerique(getVAL_LB_FAMILLE_EMPLOI_SELECT()) ? Integer
						.parseInt(getVAL_LB_FAMILLE_EMPLOI_SELECT()) : -1);
				String codeDomaineEtCodeFamille = ((DomaineEmploi) getListeDomaine().get(indiceDomaine))
						.getCodeDomaineEmploi()
						+ ((FamilleEmploi) getListeFamille().get(indiceFamille)).getCodeFamilleEmploi();
				// RG_PE_FE_A04
				getFicheEmploiCourant().setRefMairie(
						codeDomaineEtCodeFamille
								+ Services.lpad(String.valueOf(FicheEmploi.genererNumChrono(getTransaction(),
										codeDomaineEtCodeFamille)), 3, "0"));

				// Création de la fiche emploi
				getFicheEmploiCourant().creerFicheEmploi(getTransaction());

				if (getTransaction().isErreur()) {
					getTransaction()
							.declarerErreur(
									MessageUtils.getMessage("ERR976", "Fiche emploi "
											+ getFicheEmploiCourant().getRefMairie()));
					return false;
				}

				messageInf = MessageUtils.getMessage("INF100", getFicheEmploiCourant().getRefMairie());
			} else {
				// Modification de la fiche emploi
				getFicheEmploiCourant().modifierFicheEmploi(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction()
							.declarerErreur(
									MessageUtils.getMessage("ERR978", "Fiche emploi "
											+ getFicheEmploiCourant().getRefMairie()));
					return false;
				}

				messageInf = MessageUtils.getMessage("INF108", getFicheEmploiCourant().getRefMairie());
			}

			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, getFicheEmploiCourant());

			// Sauvegarde des nouvelles autres appellations et suppression des
			// anciennes
			for (int i = 0; i < getListeAutreAppellationAAjouter().size(); i++) {
				AutreAppellationEmploi aae = (AutreAppellationEmploi) getListeAutreAppellationAAjouter().get(i);
				aae.setIdFicheEmploi(Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()));
				getAutreAppellationEmploiDao().creerAutreAppellationEmploi(aae.getIdFicheEmploi(),
						aae.getLibAutreAppellationEmploi());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "AutreAppellation '" + aae.getLibAutreAppellationEmploi()
									+ "'"));
					return false;
				}
			}
			getListeAutreAppellationAAjouter().clear();

			for (int i = 0; i < getListeAutreAppellationASupprimer().size(); i++) {
				AutreAppellationEmploi aae = (AutreAppellationEmploi) getListeAutreAppellationASupprimer().get(i);
				getAutreAppellationEmploiDao().supprimerAutreAppellationEmploi(aae.getIdAutreAppellationEmploi());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "AutreAppellation '" + aae.getLibAutreAppellationEmploi()
									+ "'"));
					return false;
				}
			}
			getListeAutreAppellationASupprimer().clear();

			// Sauvegarde des nouvelles catégories et suppression des anciennes
			for (int i = 0; i < getListeCategorieAAjouter().size(); i++) {
				Categorie cat = (Categorie) getListeCategorieAAjouter().get(i);
				CategorieFE catFE = new CategorieFE(Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()),
						cat.getIdCategorieStatut());
				getCategorieFEDao().creerCategorieFE(catFE.getIdFicheEmploi(), catFE.getIdCategorieStatut());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "Categorie '" + cat.getLibCategorieStatut() + "'"));
					return false;
				}
			}
			getListeCategorieAAjouter().clear();

			for (int i = 0; i < getListeCategorieASupprimer().size(); i++) {
				Categorie cat = (Categorie) getListeCategorieASupprimer().get(i);
				CategorieFE catFE = getCategorieFEDao().chercherCategorieFE(
						Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()), cat.getIdCategorieStatut());
				getCategorieFEDao().supprimerCategorieFE(catFE.getIdFicheEmploi(), catFE.getIdCategorieStatut());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "Categorie '" + cat.getLibCategorieStatut() + "'"));
					return false;
				}
			}
			getListeCategorieASupprimer().clear();

			// Sauvegarde des nouveaux cadres emploi et suppression des anciens
			for (int i = 0; i < getListeCadresEmploiAAjouter().size(); i++) {
				CadreEmploi cadre = (CadreEmploi) getListeCadresEmploiAAjouter().get(i);
				CadreEmploiFE cadreFE = new CadreEmploiFE(Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()),
						cadre.getIdCadreEmploi());
				getCadreEmploiFEDao().creerCadreEmploiFE(cadreFE.getIdFicheEmploi(), cadreFE.getIdCadreEmploi());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "CadreEmploi '" + cadre.getLibCadreEmploi() + "'"));
					return false;
				}
			}
			getListeCadresEmploiAAjouter().clear();

			for (int i = 0; i < getListeCadresEmploiASupprimer().size(); i++) {
				CadreEmploi cadre = (CadreEmploi) getListeCadresEmploiASupprimer().get(i);
				CadreEmploiFE cadreFE = getCadreEmploiFEDao().chercherCadreEmploiFE(
						Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()), cadre.getIdCadreEmploi());
				getCadreEmploiFEDao().supprimerCadreEmploiFE(cadreFE.getIdFicheEmploi(), cadreFE.getIdCadreEmploi());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "CadreEmploi '" + cadre.getLibCadreEmploi() + "'"));
					return false;
				}
			}
			getListeCadresEmploiASupprimer().clear();

			// Sauvegarde des nouveaux niveaux d'étude et suppression des
			// anciens
			for (int i = 0; i < getListeNiveauEtudeAAjouter().size(); i++) {
				NiveauEtude niv = (NiveauEtude) getListeNiveauEtudeAAjouter().get(i);
				NiveauEtudeFE nivEtudeFE = new NiveauEtudeFE(getFicheEmploiCourant().getIdFicheEmploi(),
						niv.getIdNiveauEtude());
				nivEtudeFE.creerNiveauEtudeFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "NiveauEtude '" + niv.getLibNiveauEtude() + "'"));
					return false;
				}
			}
			getListeNiveauEtudeAAjouter().clear();

			for (int i = 0; i < getListeNiveauEtudeASupprimer().size(); i++) {
				NiveauEtude niv = (NiveauEtude) getListeNiveauEtudeASupprimer().get(i);
				NiveauEtudeFE nivEtudeFE = NiveauEtudeFE.chercherNiveauEtudeFE(getTransaction(),
						niv.getIdNiveauEtude(), getFicheEmploiCourant().getIdFicheEmploi());
				nivEtudeFE.supprimerNiveauEtudeFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "NiveauEtude '" + niv.getLibNiveauEtude() + "'"));
					return false;
				}
			}
			getListeNiveauEtudeASupprimer().clear();

			// Sauvegarde des nouveaux diplômes et suppression des anciens
			for (int i = 0; i < getListeDiplomeAAjouter().size(); i++) {
				DiplomeGenerique dipl = (DiplomeGenerique) getListeDiplomeAAjouter().get(i);
				DiplomeFE diplFE = new DiplomeFE(getFicheEmploiCourant().getIdFicheEmploi(), dipl
						.getIdDiplomeGenerique().toString());
				diplFE.creerDiplomeFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "Diplome '" + dipl.getLibDiplomeGenerique() + "'"));
					return false;
				}
			}
			getListeDiplomeAAjouter().clear();

			for (int i = 0; i < getListeDiplomeASupprimer().size(); i++) {
				DiplomeGenerique dipl = (DiplomeGenerique) getListeDiplomeASupprimer().get(i);
				DiplomeFE diplFE = DiplomeFE.chercherDiplomeFE(getTransaction(), getFicheEmploiCourant()
						.getIdFicheEmploi(), dipl.getIdDiplomeGenerique().toString());
				diplFE.supprimerDiplomeFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "Diplome '" + dipl.getLibDiplomeGenerique() + "'"));
					return false;
				}
			}
			getListeDiplomeASupprimer().clear();

			// Sauvegarde des nouvelles activites principales et suppression des
			// anciennes
			for (int i = 0; i < getListeActiPrincAAjouter().size(); i++) {
				Activite acti = (Activite) getListeActiPrincAAjouter().get(i);
				ActiviteFE actiFE = new ActiviteFE(getFicheEmploiCourant().getIdFicheEmploi(), acti.getIdActivite());
				actiFE.creerActiviteFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "Activité principale '" + acti.getNomActivite() + "'"));
					return false;
				}
			}
			getListeActiPrincAAjouter().clear();

			for (int i = 0; i < getListeActiPrincASupprimer().size(); i++) {
				Activite acti = (Activite) getListeActiPrincASupprimer().get(i);
				ActiviteFE actiFE = ActiviteFE.chercherActiviteFE(getTransaction(), getFicheEmploiCourant()
						.getIdFicheEmploi(), acti.getIdActivite());
				actiFE.supprimerActiviteFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "Activité principale '" + acti.getNomActivite() + "'"));
					return false;
				}
			}
			getListeActiPrincASupprimer().clear();

			// Sauvegarde des nouvelles compétences savoir et suppression des
			// anciennes
			for (int i = 0; i < getListeSavoirAAjouter().size(); i++) {
				Competence comp = (Competence) getListeSavoirAAjouter().get(i);
				CompetenceFE compFE = new CompetenceFE(getFicheEmploiCourant().getIdFicheEmploi(),
						comp.getIdCompetence());
				compFE.creerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeSavoirAAjouter().clear();

			for (int i = 0; i < getListeSavoirASupprimer().size(); i++) {
				Competence comp = (Competence) getListeSavoirASupprimer().get(i);
				CompetenceFE compFE = CompetenceFE.chercherCompetenceFE(getTransaction(), getFicheEmploiCourant()
						.getIdFicheEmploi(), comp.getIdCompetence());
				compFE.supprimerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeSavoirASupprimer().clear();

			// Sauvegarde des nouvelles compétences savoir faire et suppression
			// des anciennes
			for (int i = 0; i < getListeSavoirFaireAAjouter().size(); i++) {
				Competence comp = (Competence) getListeSavoirFaireAAjouter().get(i);
				CompetenceFE compFE = new CompetenceFE(getFicheEmploiCourant().getIdFicheEmploi(),
						comp.getIdCompetence());
				compFE.creerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeSavoirFaireAAjouter().clear();

			for (int i = 0; i < getListeSavoirFaireASupprimer().size(); i++) {
				Competence comp = (Competence) getListeSavoirFaireASupprimer().get(i);
				CompetenceFE compFE = CompetenceFE.chercherCompetenceFE(getTransaction(), getFicheEmploiCourant()
						.getIdFicheEmploi(), comp.getIdCompetence());
				compFE.supprimerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeSavoirFaireASupprimer().clear();

			// Sauvegarde des nouvelles compétences savoir faire et suppression
			// des anciennes
			for (int i = 0; i < getListeComportementAAjouter().size(); i++) {
				Competence comp = (Competence) getListeComportementAAjouter().get(i);
				CompetenceFE compFE = new CompetenceFE(getFicheEmploiCourant().getIdFicheEmploi(),
						comp.getIdCompetence());
				compFE.creerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeComportementAAjouter().clear();

			for (int i = 0; i < getListeComportementASupprimer().size(); i++) {
				Competence comp = (Competence) getListeComportementASupprimer().get(i);
				CompetenceFE compFE = CompetenceFE.chercherCompetenceFE(getTransaction(), getFicheEmploiCourant()
						.getIdFicheEmploi(), comp.getIdCompetence());
				compFE.supprimerCompetenceFE(getTransaction());
				if (getTransaction().isErreur() && getTransaction().getMessageErreur().startsWith("ERR")) {
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR975", "compétence '" + comp.getNomCompetence() + "'"));
					return false;
				}
			}
			getListeComportementASupprimer().clear();
		}

		if (getTransaction().isErreur())
			return false;

		// COMMIT
		commitTransaction();

		if (!Const.CHAINE_VIDE.equals(messageInf)) {
			setFocus(null);
			getTransaction().declarerErreur(messageInf);
		}

		// dans le cas ou l on vient de la fiche de poste, on retourne sur la
		// fiche de poste
		if (null != getProcessAppelant() && OePOSTEFichePoste.class.equals(getProcessAppelant().getClass())) {
			setStatut(STATUT_PROCESS_APPELANT);
		}

		return true;
	}

	private boolean suppressionFicheEmploiEtLien(FicheEmploi ficheEmploiCourant, Transaction aTransaction)
			throws Exception {
		boolean result = true;
		// Suppression des Activite
		result = result & ActiviteFE.supprimerActiviteFEAvecFE(aTransaction, ficheEmploiCourant);

		// Suppression des Autres appellations
		getAutreAppellationEmploiDao().supprimerAutreAppellationEmploiAvecFE(
				Integer.valueOf(ficheEmploiCourant.getIdFicheEmploi()));

		// Suppression des Categorie
		getCategorieFEDao().supprimerCategorieFEAvecFE(Integer.valueOf(ficheEmploiCourant.getIdFicheEmploi()));

		// Suppression des CadreEmploi
		getCadreEmploiFEDao().supprimerCadreEmploiFEAvecFE(Integer.valueOf(ficheEmploiCourant.getIdFicheEmploi()));

		// Suppression des NiveauEtude
		result = result & NiveauEtudeFE.supprimerNiveauEtudeFEAvecFE(aTransaction, ficheEmploiCourant);

		// Suppression des Diplome
		result = result & DiplomeFE.supprimerDiplomeFEAvecFE(aTransaction, ficheEmploiCourant);

		// suppression de la FicheEmploi
		result = result & ficheEmploiCourant.supprimerFicheEmploi(getTransaction());
		return result;
	}

	/**
	 * Alimente l'objet FicheEmploi avec les champs de saisie du formulaire.
	 * Retourne true ou false Date de création : (27/06/11 15:34:00)
	 */
	private boolean alimenterFicheEmploi(HttpServletRequest request) throws Exception {

		if (getFicheEmploiCourant() == null)
			setFicheEmploiCourant(new FicheEmploi());

		// récupération du code rome et vérification de son existence.
		String idCodeRome = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeCodeRome().size(); i++) {
			CodeRome codeRome = (CodeRome) getListeCodeRome().get(i);
			if (codeRome.getLibCodeRome().equals(getVAL_EF_CODE_ROME())) {
				idCodeRome = codeRome.getIdCodeRome().toString();
				break;
			}
		}

		getFicheEmploiCourant().setRefMairie(getVAL_EF_REF_MAIRIE());
		getFicheEmploiCourant().setIdCodeRome(idCodeRome);
		getFicheEmploiCourant().setNomMetierEmploi(getVAL_EF_NOM_METIER());
		getFicheEmploiCourant().setPrecisionsDiplomes(getVAL_EF_PRECISIONS_DIPLOMES());
		getFicheEmploiCourant().setLienHierarchique(Const.CHAINE_VIDE);
		getFicheEmploiCourant().setDefinitionEmploi(getVAL_EF_DEFINITION_EMPLOI());

		if (getFicheEmploiCourant().getIdFicheEmploi() == null) {
			int indiceDomaine = (Services.estNumerique(getVAL_LB_DOMAINE_SELECT()) ? Integer
					.parseInt(getVAL_LB_DOMAINE_SELECT()) : -1);
			getFicheEmploiCourant().setIdDomaineFE(
					((DomaineEmploi) getListeDomaine().get(indiceDomaine)).getIdDomaineEmploi().toString());
		}

		if (getFicheEmploiCourant().getIdFicheEmploi() == null) {
			int indiceFamille = (Services.estNumerique(getVAL_LB_FAMILLE_EMPLOI_SELECT()) ? Integer
					.parseInt(getVAL_LB_FAMILLE_EMPLOI_SELECT()) : -1);
			getFicheEmploiCourant().setIdFamilleEmploi(
					((FamilleEmploi) getListeFamille().get(indiceFamille)).getIdFamilleEmploi().toString());
		}

		return true;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (22/06/11 13:59:14)
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

		// Récupération de la fiche emploi en session
		FicheEmploi feRechAvancee = (FicheEmploi) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_FICHE_EMPLOI);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);

		if (feRechAvancee != null) {
			viderFicheEmploi();
			viderObjetsFicheEmploi();
			setFicheEmploiCourant(feRechAvancee);
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		} else if (getFicheEmploiCourant() == null || getFicheEmploiCourant().getIdFicheEmploi() == null) {
			setFicheEmploiCourant(new FicheEmploi());
		}

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		// Mise à jour de la liste des compétences
		if (getTypeCompetenceCourant() == null || getTypeCompetenceCourant().getIdTypeCompetence() == null) {
			setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetence(1));
			addZone(getNOM_RG_TYPE_COMPETENCE(), getNOM_RB_TYPE_COMPETENCE_S());
		} else {
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_S()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_SF()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR_FAIRE.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_C()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.COMPORTEMENT.getValue()));
		}

		// Initialise les listes fixes
		initialiseListeDeroulante();

		// Si ficheEmploi vide
		if (getFicheEmploiCourant() == null) {
			setFicheEmploiCourant(new FicheEmploi());
		} else if (getFicheEmploiCourant().getIdFicheEmploi() != null) {
			initialiseZonesFicheCourante();
		}

		// Initialise les listes renseignées par l'utilisateur
		initialiseAutreAppellationMulti();
		initialiseCategorieMulti();
		initialiseCadresEmploiMulti();
		initialiseNiveauEtudeMulti();
		initialiseDiplomeMulti();
		initialiseActivitePrinc();
		initialiserCompetence();

		// Mise à jour de l'action
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION()))
			addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getCadreEmploiDao() == null) {
			setCadreEmploiDao(new CadreEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCodeRomeDao() == null) {
			setCodeRomeDao(new CodeRomeDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getDiplomeGeneriqueDao() == null) {
			setDiplomeGeneriqueDao(new DiplomeGeneriqueDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getDomaineEmploiDao() == null) {
			setDomaineEmploiDao(new DomaineEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getFamilleEmploiDao() == null) {
			setFamilleEmploiDao(new FamilleEmploiDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAppellationEmploiDao() == null) {
			setAutreAppellationEmploiDao(new AutreAppellationEmploiDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCadreEmploiFEDao() == null) {
			setCadreEmploiFEDao(new CadreEmploiFEDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCategorieDao() == null) {
			setCategorieDao(new CategorieDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCategorieFEDao() == null) {
			setCategorieFEDao(new CategorieFEDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialise les zones de la fiche emploi courante.
	 * 
	 * @throws Exception
	 */
	private void initialiseZonesFicheCourante() throws Exception {
		addZone(getNOM_EF_REF_MAIRIE(), getFicheEmploiCourant().getRefMairie());
		addZone(getNOM_EF_NOM_METIER(), getFicheEmploiCourant().getNomMetierEmploi());
		addZone(getNOM_EF_PRECISIONS_DIPLOMES(), getFicheEmploiCourant().getPrecisionsDiplomes());
		addZone(getNOM_EF_DEFINITION_EMPLOI(), getFicheEmploiCourant().getDefinitionEmploi());

		if (getFicheEmploiCourant().getIdFicheEmploi() != null) {
			DomaineEmploi domaine = getDomaineEmploiDao().chercherDomaineEmploi(
					Integer.valueOf(getFicheEmploiCourant().getIdDomaineFE()));
			addZone(getNOM_LB_DOMAINE_SELECT(), String.valueOf(getListeDomaine().indexOf(domaine)));

			FamilleEmploi famille = getFamilleEmploiDao().chercherFamilleEmploi(
					Integer.valueOf(getFicheEmploiCourant().getIdFamilleEmploi()));
			addZone(getNOM_LB_FAMILLE_EMPLOI_SELECT(), String.valueOf(getListeFamille().indexOf(famille)));

		}

		if (getListeCodeRome() != null)
			for (int i = 0; i < getListeCodeRome().size(); i++) {
				CodeRome cr = (CodeRome) getListeCodeRome().get(i);
				if (cr.getIdCodeRome().toString().equals(getFicheEmploiCourant().getIdCodeRome())) {
					addZone(getNOM_EF_CODE_ROME(), cr.getLibCodeRome());
					break;
				}
			}
	}

	/**
	 * Récupère les compétences des activités choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserCompetence() throws Exception {

		// Recherche des types de compétence
		TypeCompetence savoir = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.SAVOIR.getValue());

		TypeCompetence savoirFaire = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.SAVOIR_FAIRE.getValue());

		TypeCompetence comportement = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.COMPORTEMENT.getValue());

		if (getListeSavoirMulti().size() == 0 && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeSavoirMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(), getFicheEmploiCourant(),
					EnumTypeCompetence.SAVOIR.getCode().toString()));
		}
		if (getListeSavoirFaireMulti().size() == 0 && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeSavoirFaireMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(),
					getFicheEmploiCourant(), EnumTypeCompetence.SAVOIR_FAIRE.getCode().toString()));
		}
		if (getListeComportementMulti().size() == 0 && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeComportementMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(),
					getFicheEmploiCourant(), EnumTypeCompetence.COMPORTEMENT.getCode().toString()));
		}

		// on recupere les activites selectionnées dans l'ecran de selection
		@SuppressWarnings("unchecked")
		ArrayList<Competence> listeCompSelect = (ArrayList<Competence>) VariablesActivite.recuperer(this, "COMPETENCE");
		if (listeCompSelect != null && listeCompSelect.size() != 0) {
			for (int i = 0; i < listeCompSelect.size(); i++) {
				Competence comp = (Competence) listeCompSelect.get(i);
				if (comp != null) {
					if (comp.getIdTypeCompetence().equals(savoir.getIdTypeCompetence().toString())) {
						// si c'est un savoir
						if (getListeSavoirMulti() == null)
							setListeSavoirMulti(new ArrayList<Competence>());
						if (!getListeSavoirMulti().contains(comp)) {
							getListeSavoirMulti().add(comp);
							if (getListeSavoirASupprimer().contains(comp)) {
								getListeSavoirASupprimer().remove(comp);
							} else {
								getListeSavoirAAjouter().add(comp);
							}
						}

					} else if (comp.getIdTypeCompetence().equals(savoirFaire.getIdTypeCompetence().toString())) {
						// si c'est un savoir faire
						if (getListeSavoirFaireMulti() == null)
							setListeSavoirFaireMulti(new ArrayList<Competence>());
						if (!getListeSavoirFaireMulti().contains(comp)) {
							getListeSavoirFaireMulti().add(comp);
							if (getListeSavoirFaireASupprimer().contains(comp)) {
								getListeSavoirFaireASupprimer().remove(comp);
							} else {
								getListeSavoirFaireAAjouter().add(comp);
							}
						}

					} else if (comp.getIdTypeCompetence().equals(comportement.getIdTypeCompetence().toString())) {
						// si c'est un comportement
						if (getListeComportementMulti() == null)
							setListeComportementMulti(new ArrayList<Competence>());
						if (!getListeComportementMulti().contains(comp)) {
							getListeComportementMulti().add(comp);
							if (getListeComportementASupprimer().contains(comp)) {
								getListeComportementASupprimer().remove(comp);
							} else {
								getListeComportementAAjouter().add(comp);
							}
						}

					}
				}
			}

		}
		VariablesActivite.enlever(this, "COMPETENCE");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIR");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIRFAIRE");
		VariablesActivite.enlever(this, "LISTECOMPETENCECOMPORTEMENT");

		// Affichage des listes obtenues
		int indiceCompS = 0;
		if (getListeSavoirMulti() != null) {
			for (int i = 0; i < getListeSavoirMulti().size(); i++) {
				Competence co = (Competence) getListeSavoirMulti().get(i);

				addZone(getNOM_ST_LIB_COMP_S(indiceCompS), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: co.getNomCompetence());

				indiceCompS++;
			}
		}

		int indiceCompSF = 0;
		if (getListeSavoirFaireMulti() != null) {
			for (int i = 0; i < getListeSavoirFaireMulti().size(); i++) {
				Competence co = (Competence) getListeSavoirFaireMulti().get(i);

				addZone(getNOM_ST_LIB_COMP_SF(indiceCompSF), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: co.getNomCompetence());

				indiceCompSF++;
			}
		}

		int indiceCompPro = 0;
		if (getListeComportementMulti() != null) {
			for (int i = 0; i < getListeComportementMulti().size(); i++) {
				Competence co = (Competence) getListeComportementMulti().get(i);

				addZone(getNOM_ST_LIB_COMP_PRO(indiceCompPro),
						co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());

				indiceCompPro++;
			}
		}
	}

	/**
	 * Vide les champs du formulaire.
	 * 
	 * @throws Exception
	 */
	private void viderFicheEmploi() throws Exception {

		addZone(getNOM_EF_REF_MAIRIE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_ROME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_METIER(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_AUTRE_APPELLATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PRECISIONS_DIPLOMES(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DEFINITION_EMPLOI(), Const.CHAINE_VIDE);

		addZone(getNOM_EF_CATEGORIE_MULTI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_DOMAINE_SELECT(), "0");
		addZone(getNOM_LB_FAMILLE_EMPLOI_SELECT(), "0");

		setAfficherListeCategorie(false);
		addZone(getNOM_LB_CATEGORIE_SELECT(), "0");
		setAfficherListeCadre(false);
		addZone(getNOM_LB_CADRE_EMPLOI_SELECT(), "0");
		setAfficherListeNivEt(false);
		addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), "0");
		setAfficherListeDiplome(false);
		addZone(getNOM_LB_DIPLOME_SELECT(), "0");

		setLB_AUTRE_APPELLATION(null);
		setLB_CADRE_EMPLOI_MULTI(null);
		setLB_DIPLOME_MULTI(null);
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste domaine vide alors affectation
		if (getLB_DOMAINE() == LBVide) {
			ArrayList<DomaineEmploi> dom = getDomaineEmploiDao().listerDomaineEmploi();
			setListeDomaine(dom);

			if (getListeDomaine().size() != 0) {
				int[] tailles = { 4, 100 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<DomaineEmploi> list = getListeDomaine().listIterator(); list.hasNext();) {
					DomaineEmploi de = (DomaineEmploi) list.next();
					String ligne[] = { de.getCodeDomaineEmploi(), de.getLibDomaineEmploi() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_DOMAINE(aFormat.getListeFormatee());
			} else {
				setLB_DOMAINE(null);
			}
		}

		// Si liste famille vide alors affectation
		if (getLB_FAMILLE_EMPLOI() == LBVide) {
			ArrayList<FamilleEmploi> fam = getFamilleEmploiDao().listerFamilleEmploi();
			setListeFamille(fam);

			if (getListeFamille().size() != 0) {
				int[] tailles = { 4, 100 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<FamilleEmploi> list = getListeFamille().listIterator(); list.hasNext();) {
					FamilleEmploi de = (FamilleEmploi) list.next();
					String ligne[] = { de.getCodeFamilleEmploi(), de.getLibFamilleEmploi() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_FAMILLE_EMPLOI(aFormat.getListeFormatee());
			} else {
				setLB_FAMILLE_EMPLOI(null);
			}
		}

		// Si liste categorie vide alors affectation
		if (getLB_CATEGORIE() == LBVide) {
			ArrayList<Categorie> cat = getCategorieDao().listerCategorie();
			setListeCategorie(cat);

			if (getListeCategorie().size() != 0) {
				int[] tailles = { 2 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<Categorie> list = getListeCategorie().listIterator(); list.hasNext();) {
					Categorie de = (Categorie) list.next();
					String ligne[] = { de.getLibCategorieStatut() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_CATEGORIE(aFormat.getListeFormatee(true));
			} else {
				setLB_CATEGORIE(null);
			}
		}

		// Si liste cadres emploi vide alors affectation
		if (getLB_CADRE_EMPLOI() == LBVide) {
			int[] tailles = { 100 };

			ArrayList<CadreEmploi> cadresE = getCadreEmploiDao().listerCadreEmploi();
			setListeCadresEmploi(cadresE);
			if (getListeCadresEmploi().size() != 0) {
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<CadreEmploi> list = getListeCadresEmploi().listIterator(); list.hasNext();) {
					CadreEmploi cadre = (CadreEmploi) list.next();
					String ligne[] = { cadre.getLibCadreEmploi() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_CADRE_EMPLOI(aFormat.getListeFormatee(true));
			} else {
				setLB_CADRE_EMPLOI(null);
			}
		}

		// Si liste diplomes vide alors affectation
		if (getLB_NIVEAU_ETUDE() == LBVide) {
			ArrayList<NiveauEtude> niveau = NiveauEtude.listerNiveauEtude(getTransaction());
			setListeNiveauEtude(niveau);

			int[] tailles = { 10 };
			String[] champs = { "libNiveauEtude" };
			setLB_NIVEAU_ETUDE(new FormateListe(tailles, niveau, champs).getListeFormatee(true));
		}

		// Si liste diplomes vide alors affectation
		if (getLB_DIPLOME() == LBVide) {
			int[] tailles = { 100 };
			ArrayList<DiplomeGenerique> dipl = getDiplomeGeneriqueDao().listerDiplomeGenerique();
			setListeDiplome(dipl);
			if (getListeDiplome().size() != 0) {
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<DiplomeGenerique> list = getListeDiplome().listIterator(); list.hasNext();) {
					DiplomeGenerique cadre = (DiplomeGenerique) list.next();
					String ligne[] = { cadre.getLibDiplomeGenerique() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_DIPLOME(aFormat.getListeFormatee(true));
			} else {
				setLB_DIPLOME(null);
			}
		}

		// Si liste code rome vide alors affectation
		if (getListeCodeRome().size() == 0) {
			ArrayList<CodeRome> codeRome = (ArrayList<CodeRome>) getCodeRomeDao().listerCodeRome();
			setListeCodeRome(codeRome);
		}
	}

	/**
	 * Initialise la liste des Autres appellations sélectionnées par
	 * l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseAutreAppellationMulti() throws Exception {
		if (getListeAutreAppellationMulti() == null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeAutreAppellationMulti(getAutreAppellationEmploiDao().listerAutreAppellationEmploiAvecFE(
					Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi())));
		}

		if (getListeAutreAppellationMulti() != null) {
			int tailles[] = { 100 };
			FormateListe aListeFormatee = new FormateListe(tailles);
			for (int i = 0; i < getListeAutreAppellationMulti().size(); i++) {
				AutreAppellationEmploi autreAppellEmpl = (AutreAppellationEmploi) getListeAutreAppellationMulti()
						.get(i);
				String colonnes[] = { autreAppellEmpl.getLibAutreAppellationEmploi() };
				aListeFormatee.ajouteLigne(colonnes);
			}

			setLB_AUTRE_APPELLATION(aListeFormatee.getListeFormatee());
		}
	}

	/**
	 * Initialise la liste des Catégories sélectionnées par l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseCategorieMulti() throws Exception {
		if (getListeCategorieMulti() == null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeCategorieMulti(getCategorieDao().listerCategorieAvecFE(
					Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()), getCategorieFEDao()));
		}

		String catMulti = Const.CHAINE_VIDE;
		if (getListeCategorieMulti() != null) {
			for (int i = 0; i < getListeCategorieMulti().size(); i++) {
				Categorie categorie = (Categorie) getListeCategorieMulti().get(i);
				catMulti += categorie.getLibCategorieStatut() + ", ";
			}
		}
		addZone(getNOM_EF_CATEGORIE_MULTI(), catMulti.length() > 0 ? catMulti.substring(0, catMulti.length() - 2)
				: catMulti);
	}

	/**
	 * Initialise la liste des Cadres emploi sélectionnés par l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseCadresEmploiMulti() throws Exception {
		if (getListeCadresEmploiMulti() == null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeCadresEmploiMulti(getCadreEmploiDao().listerCadreEmploiAvecFicheEmploi(getCadreEmploiFEDao(),
					Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi())));
		}

		int tailles[] = { 100 };
		FormateListe aListeFormatee = new FormateListe(tailles);
		if (getListeCadresEmploiMulti() != null) {
			for (int i = 0; i < getListeCadresEmploiMulti().size(); i++) {
				CadreEmploi cadreE = (CadreEmploi) getListeCadresEmploiMulti().get(i);
				String colonnes[] = { cadreE.getLibCadreEmploi() };
				aListeFormatee.ajouteLigne(colonnes);
			}
		}
		setLB_CADRE_EMPLOI_MULTI(aListeFormatee.getListeFormatee());
	}

	/**
	 * Initialise la liste des NiveauEtude sélectionnés par l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseNiveauEtudeMulti() throws Exception {
		if (getListeNiveauEtudeMulti() == null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeNiveauEtudeMulti(NiveauEtude.listerNiveauEtudeAvecFE(getTransaction(), getFicheEmploiCourant()));
		}

		String nivEtMulti = Const.CHAINE_VIDE;
		if (getListeNiveauEtudeMulti() != null) {
			for (int i = 0; i < getListeNiveauEtudeMulti().size(); i++) {
				NiveauEtude nivEt = (NiveauEtude) getListeNiveauEtudeMulti().get(i);
				nivEtMulti += nivEt.getLibNiveauEtude() + ", ";
			}
		}
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(),
				nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);
	}

	/**
	 * Initialise la liste des Diplome sélectionnés par l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseDiplomeMulti() throws Exception {
		if (getListeDiplomeMulti() == null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeDiplomeMulti(getDiplomeGeneriqueDao().listerDiplomeGeneriqueAvecFE(getTransaction(),
					getFicheEmploiCourant()));
		}

		int tailles[] = { 100 };
		FormateListe aListeFormatee = new FormateListe(tailles);
		if (getListeDiplomeMulti() != null) {
			for (int i = 0; i < getListeDiplomeMulti().size(); i++) {
				DiplomeGenerique dipl = (DiplomeGenerique) getListeDiplomeMulti().get(i);
				String colonnes[] = { dipl.getLibDiplomeGenerique() };
				aListeFormatee.ajouteLigne(colonnes);
			}
		}
		setLB_DIPLOME_MULTI(aListeFormatee.getListeFormatee());
	}

	/**
	 * Initialise la liste des activités principales sélectionnées par
	 * l'utilisateur.
	 * 
	 * @throws Exception
	 */
	private void initialiseActivitePrinc() throws Exception {
		if (getListeActiPrincMulti().size() == 0 && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setListeActiPrincMulti(Activite.listerActiviteAvecFE(getTransaction(), getFicheEmploiCourant()));
		}
		// on recupere les activites selectionnées dans l'ecran de selection
		@SuppressWarnings("unchecked")
		ArrayList<Activite> listeActiSelect = (ArrayList<Activite>) VariablesActivite.recuperer(this, "ACTIVITE_PRINC");
		if (listeActiSelect != null && listeActiSelect.size() != 0) {
			for (int i = 0; i < listeActiSelect.size(); i++) {
				Activite a = (Activite) listeActiSelect.get(i);
				if (a != null) {
					if (!getListeActiPrincMulti().contains(a)) {
						getListeActiPrincMulti().add(a);
						if (getListeActiPrincASupprimer().contains(a)) {
							getListeActiPrincASupprimer().remove(a);
						} else {
							getListeActiPrincAAjouter().add(a);
						}
					}
				}
			}

		}
		VariablesActivite.enlever(this, "ACTIVITE_PRINC");

		int indiceActi = 0;
		if (getListeActiPrincMulti() != null) {
			for (int i = 0; i < getListeActiPrincMulti().size(); i++) {
				Activite acti = (Activite) getListeActiPrincMulti().get(i);

				addZone(getNOM_ST_LIB_ACTI(indiceActi), acti.getNomActivite().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: acti.getNomActivite());

				indiceActi++;
			}
		}
	}

	/**
	 * Contrôle les zones saisies Date de création : (27/06/11 14:50:00)
	 * RG_PE_FE_A01
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A01
		// ***********************
		// Verification Nom métier
		// ***********************
		if (getVAL_EF_NOM_METIER().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Nom du métier"));
			return false;
		}

		// **********************************
		// Verification Activités principales
		// **********************************
		if (getListeActiPrincMulti() == null || getListeActiPrincMulti().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Activités principales"));
			return false;
		}

		// *************************************
		// Verification Définition de l'emploi
		// *************************************
		if (getVAL_EF_DEFINITION_EMPLOI().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Définition de l'emploi"));
			return false;
		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CADRE_EMPLOI_MULTI Date
	 * de création : (22/06/11 13:59:15)
	 * 
	 */
	private String[] getLB_CADRE_EMPLOI_MULTI() {
		if (LB_CADRE_EMPLOI_MULTI == null)
			LB_CADRE_EMPLOI_MULTI = initialiseLazyLB();
		return LB_CADRE_EMPLOI_MULTI;
	}

	/**
	 * Setter de la liste: LB_CADRE_EMPLOI_MULTI Date de création : (22/06/11
	 * 13:59:15)
	 * 
	 */
	private void setLB_CADRE_EMPLOI_MULTI(String[] newLB_CADRE_EMPLOI_MULTI) {
		LB_CADRE_EMPLOI_MULTI = newLB_CADRE_EMPLOI_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CADRE_EMPLOI_MULTI Date
	 * de création : (22/06/11 13:59:15)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_MULTI() {
		return "NOM_LB_CADRE_EMPLOI_MULTI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CADRE_EMPLOI_MULTI_SELECT Date de création : (22/06/11 13:59:15)
	 * 
	 */
	public String getNOM_LB_CADRE_EMPLOI_MULTI_SELECT() {
		return "NOM_LB_CADRE_EMPLOI_MULTI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CADRE_EMPLOI_MULTI Date de création : (22/06/11 13:59:15)
	 * 
	 */
	public String[] getVAL_LB_CADRE_EMPLOI_MULTI() {
		return getLB_CADRE_EMPLOI_MULTI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CADRE_EMPLOI_MULTI Date de création : (22/06/11 13:59:15)
	 * 
	 */
	public String getVAL_LB_CADRE_EMPLOI_MULTI_SELECT() {
		return getZone(getNOM_LB_CADRE_EMPLOI_MULTI_SELECT());
	}

	/**
	 * Retourne la liste des cadres emploi.
	 * 
	 * @return listeCadresEmploi ArrayList(CadreEmploi)
	 */
	private ArrayList<CadreEmploi> getListeCadresEmploi() {
		return listeCadresEmploi;
	}

	/**
	 * Met à jour la liste des cadres emploi.
	 * 
	 * @param listeCadresEmploi
	 *            ArrayList(CadreEmploi)
	 */
	private void setListeCadresEmploi(ArrayList<CadreEmploi> listeCadresEmploi) {
		this.listeCadresEmploi = listeCadresEmploi;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_CADRE_EMPLOI Date de
	 * création : (22/06/11 14:44:25)
	 * 
	 */
	public String getNOM_PB_AJOUTER_CADRE_EMPLOI() {
		return "NOM_PB_AJOUTER_CADRE_EMPLOI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/06/11 14:44:25)
	 * 
	 */
	public boolean performPB_AJOUTER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {
		// Récupération du cadre emploi à ajouter
		int indiceCadre = (Services.estNumerique(getVAL_LB_CADRE_EMPLOI_SELECT()) ? Integer
				.parseInt(getVAL_LB_CADRE_EMPLOI_SELECT()) : -1);
		if (indiceCadre == -1 || getListeCadresEmploi().size() == 0 || indiceCadre > getListeCadresEmploi().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Cadres emploi"));
			return false;
		}

		if (indiceCadre > 0) {
			CadreEmploi c = (CadreEmploi) getListeCadresEmploi().get(indiceCadre - 1);

			if (c != null) {
				if (getListeCadresEmploiMulti() == null)
					setListeCadresEmploiMulti(new ArrayList<CadreEmploi>());

				if (!getListeCadresEmploiMulti().contains(c)) {
					getListeCadresEmploiMulti().add(c);
					if (getListeCadresEmploiASupprimer().contains(c)) {
						getListeCadresEmploiASupprimer().remove(c);
					} else {
						getListeCadresEmploiAAjouter().add(c);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Retourne la liste des cadres emploi de la fiche.
	 * 
	 * @return listeCadresEmploiMulti ArrayList(CadreEmploi)
	 */
	private ArrayList<CadreEmploi> getListeCadresEmploiMulti() {
		return listeCadresEmploiMulti;
	}

	/**
	 * Met à jour la liste des cadres emploi de la fiche.
	 * 
	 * @param listeCadresEmploiMulti
	 *            ArrayList(CadreEmploi)
	 */
	private void setListeCadresEmploiMulti(ArrayList<CadreEmploi> listeCadresEmploiMulti) {
		this.listeCadresEmploiMulti = listeCadresEmploiMulti;
	}

	/**
	 * Retourne la fiche emploi sourante.
	 * 
	 * @return ficheEmploiCourant FicheEmploi
	 */
	private FicheEmploi getFicheEmploiCourant() {
		return ficheEmploiCourant;
	}

	/**
	 * Met à jour la fiche emploi courante.
	 * 
	 * @param ficheEmploiCourant
	 *            FicheEmploi
	 */
	private void setFicheEmploiCourant(FicheEmploi ficheEmploiCourant) {
		this.ficheEmploiCourant = ficheEmploiCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_CATEGORIE Date de
	 * création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_AJOUTER_CATEGORIE() {
		return "NOM_PB_AJOUTER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_AJOUTER_CATEGORIE(HttpServletRequest request) throws Exception {
		// Récupération de la catégorie à ajouter
		int indiceCat = (Services.estNumerique(getVAL_LB_CATEGORIE_SELECT()) ? Integer
				.parseInt(getVAL_LB_CATEGORIE_SELECT()) : -1);
		if (indiceCat == -1 || getListeCategorie().size() == 0 || indiceCat > getListeCategorie().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Catégories"));
			return false;
		}
		if (indiceCat > 0) {
			Categorie c = (Categorie) getListeCategorie().get(indiceCat - 1);

			if (c != null) {
				if (getListeCategorieMulti() == null)
					setListeCategorieMulti(new ArrayList<Categorie>());

				if (!getListeCategorieMulti().contains(c)) {
					getListeCategorieMulti().add(c);
					if (getListeCategorieASupprimer().contains(c)) {
						getListeCategorieASupprimer().remove(c);
					} else {
						getListeCategorieAAjouter().add(c);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DIPLOME Date de
	 * création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DIPLOME() {
		return "NOM_PB_AJOUTER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_AJOUTER_DIPLOME(HttpServletRequest request) throws Exception {
		// Récupération du diplome à ajouter
		int indiceDipl = (Services.estNumerique(getVAL_LB_DIPLOME_SELECT()) ? Integer
				.parseInt(getVAL_LB_DIPLOME_SELECT()) : -1);
		if (indiceDipl == -1 || getListeDiplome().size() == 0 || indiceDipl > getListeDiplome().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Diplomes"));
			return false;
		}

		if (indiceDipl > 0) {
			DiplomeGenerique d = (DiplomeGenerique) getListeDiplome().get(indiceDipl - 1);

			if (d != null) {
				if (getListeDiplomeMulti() == null)
					setListeDiplomeMulti(new ArrayList<DiplomeGenerique>());

				if (!getListeDiplomeMulti().contains(d)) {
					getListeDiplomeMulti().add(d);
					if (getListeDiplomeASupprimer().contains(d)) {
						getListeDiplomeASupprimer().remove(d);
					} else {
						getListeDiplomeAAjouter().add(d);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_NIVEAU_ETUDE Date de
	 * création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_AJOUTER_NIVEAU_ETUDE() {
		return "NOM_PB_AJOUTER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_AJOUTER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {
		// Récupération du niveau d'étude à ajouter
		int indiceNiv = (Services.estNumerique(getVAL_LB_NIVEAU_ETUDE_SELECT()) ? Integer
				.parseInt(getVAL_LB_NIVEAU_ETUDE_SELECT()) : -1);
		if (indiceNiv == -1 || getListeNiveauEtude().size() == 0 || indiceNiv > getListeNiveauEtude().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Niveaux d'étude"));
			return false;
		}

		if (indiceNiv > 0) {
			NiveauEtude n = (NiveauEtude) getListeNiveauEtude().get(indiceNiv - 1);

			if (n != null) {
				if (getListeNiveauEtudeMulti() == null)
					setListeNiveauEtudeMulti(new ArrayList<NiveauEtude>());

				if (!getListeNiveauEtudeMulti().contains(n)) {
					getListeNiveauEtudeMulti().add(n);
					if (getListeNiveauEtudeASupprimer().contains(n)) {
						getListeNiveauEtudeASupprimer().remove(n);
					} else {
						getListeNiveauEtudeAAjouter().add(n);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CADRE_EMPLOI Date
	 * de création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CADRE_EMPLOI() {
		return "NOM_PB_SUPPRIMER_CADRE_EMPLOI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CADRE_EMPLOI(HttpServletRequest request) throws Exception {

		// Suppression du dernier cadre emploi de la liste
		if (getListeCadresEmploiMulti() != null && getListeCadresEmploiMulti().size() != 0) {
			CadreEmploi ce = (CadreEmploi) getListeCadresEmploiMulti().get(getListeCadresEmploiMulti().size() - 1);
			getListeCadresEmploiMulti().remove(ce);

			if (getListeCadresEmploiAAjouter().contains(ce)) {
				getListeCadresEmploiAAjouter().remove(ce);
			} else {
				getListeCadresEmploiASupprimer().add(ce);
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR967", "cadres emplois"));
			return false;
		}

		// Positionnement sur le dernier élément de la liste
		if (getListeCadresEmploiMulti() != null && getListeCadresEmploiMulti().size() > 0) {
			CadreEmploi dernierCadreEmploiMulti = (CadreEmploi) getListeCadresEmploiMulti().get(
					getListeCadresEmploiMulti().size() - 1);
			int i = getListeCadresEmploi().indexOf(dernierCadreEmploiMulti) + 1;
			addZone(getNOM_LB_CADRE_EMPLOI_SELECT(), String.valueOf(i));
		} else {
			addZone(getNOM_LB_CADRE_EMPLOI_SELECT(), String.valueOf(0));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CATEGORIE Date de
	 * création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CATEGORIE() {
		return "NOM_PB_SUPPRIMER_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CATEGORIE(HttpServletRequest request) throws Exception {
		// Suppression de la dernière catégorie de la liste
		if (getListeCategorieMulti() != null && getListeCategorieMulti().size() != 0) {
			Categorie cat = (Categorie) getListeCategorieMulti().get(getListeCategorieMulti().size() - 1);
			getListeCategorieMulti().remove(cat);

			if (getListeCategorieAAjouter().contains(cat)) {
				getListeCategorieAAjouter().remove(cat);
			} else {
				getListeCategorieASupprimer().add(cat);
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR967", "catégories"));
			return false;
		}

		// Positionnement sur le dernier élément de la liste
		if (getListeCategorieMulti() != null && getListeCategorieMulti().size() > 0) {
			Categorie derniereCategorieMulti = (Categorie) getListeCategorieMulti().get(
					getListeCategorieMulti().size() - 1);
			int i = getListeCategorie().indexOf(derniereCategorieMulti) + 1;
			addZone(getNOM_LB_CATEGORIE_SELECT(), String.valueOf(i));
		} else {
			addZone(getNOM_LB_CATEGORIE_SELECT(), String.valueOf(0));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DIPLOME Date de
	 * création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DIPLOME() {
		return "NOM_PB_SUPPRIMER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DIPLOME(HttpServletRequest request) throws Exception {

		// Suppression du dernier diplome de la liste
		if (getListeDiplomeMulti() != null && getListeDiplomeMulti().size() != 0) {
			DiplomeGenerique dip = (DiplomeGenerique) getListeDiplomeMulti().get(getListeDiplomeMulti().size() - 1);
			getListeDiplomeMulti().remove(dip);

			if (getListeDiplomeAAjouter().contains(dip)) {
				getListeDiplomeAAjouter().remove(dip);
			} else {
				getListeDiplomeASupprimer().add(dip);
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR967", "diplômes"));
			return false;
		}

		// Positionnement sur le dernier élément de la liste
		if (getListeDiplomeMulti() != null && getListeDiplomeMulti().size() > 0) {
			DiplomeGenerique dernierDipMulti = (DiplomeGenerique) getListeDiplomeMulti().get(
					getListeDiplomeMulti().size() - 1);
			int i = getListeDiplome().indexOf(dernierDipMulti) + 1;
			addZone(getNOM_LB_DIPLOME_SELECT(), String.valueOf(i));
		} else {
			addZone(getNOM_LB_DIPLOME_SELECT(), String.valueOf(0));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NIVEAU_ETUDE Date
	 * de création : (23/06/11 09:14:45)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NIVEAU_ETUDE() {
		return "NOM_PB_SUPPRIMER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/06/11 09:14:45)
	 * 
	 */
	public boolean performPB_SUPPRIMER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {
		// Suppression du dernier niveau d'étude de la liste
		if (getListeNiveauEtudeMulti() != null && getListeNiveauEtudeMulti().size() != 0) {
			NiveauEtude niv = (NiveauEtude) getListeNiveauEtudeMulti().get(getListeNiveauEtudeMulti().size() - 1);
			getListeNiveauEtudeMulti().remove(getListeNiveauEtudeMulti().size() - 1);

			if (getListeNiveauEtudeAAjouter().contains(niv)) {
				getListeNiveauEtudeAAjouter().remove(niv);
			} else {
				getListeNiveauEtudeASupprimer().add(niv);
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR967", "niveaux d'étude"));
			return false;
		}

		// Positionnement sur le dernier élément de la liste
		if (getListeNiveauEtudeMulti() != null && getListeNiveauEtudeMulti().size() > 0) {
			NiveauEtude dernierNivMulti = (NiveauEtude) getListeNiveauEtudeMulti().get(
					getListeNiveauEtudeMulti().size() - 1);
			int i = getListeNiveauEtude().indexOf(dernierNivMulti) + 1;
			addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), String.valueOf(i));
		} else {
			addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), String.valueOf(0));
		}

		return true;
	}

	/**
	 * Retourne la liste des actégories.
	 * 
	 * @return listeCategorie ArrayList
	 */
	private ArrayList<Categorie> getListeCategorie() {
		return listeCategorie;
	}

	/**
	 * Met à jour la liste des catégories.
	 * 
	 * @param listeCategorie
	 *            ArrayList(Categorie)
	 */
	private void setListeCategorie(ArrayList<Categorie> listeCategorie) {
		this.listeCategorie = listeCategorie;
	}

	/**
	 * Retourne la liste des catégories de la fiche emploi.
	 * 
	 * @return listeCategorieMulti ArrayList
	 */
	private ArrayList<Categorie> getListeCategorieMulti() {
		return listeCategorieMulti;
	}

	/**
	 * Met à jour la liste des catégories de la fiche emploi.
	 * 
	 * @param listeCategorieMulti
	 *            ArrayList(Categorie)
	 */
	private void setListeCategorieMulti(ArrayList<Categorie> listeCategorieMulti) {
		this.listeCategorieMulti = listeCategorieMulti;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME_MULTI Date de
	 * création : (23/06/11 17:14:10)
	 * 
	 */
	private String[] getLB_DIPLOME_MULTI() {
		if (LB_DIPLOME_MULTI == null)
			LB_DIPLOME_MULTI = initialiseLazyLB();
		return LB_DIPLOME_MULTI;
	}

	/**
	 * Setter de la liste: LB_DIPLOME_MULTI Date de création : (23/06/11
	 * 17:14:10)
	 * 
	 */
	private void setLB_DIPLOME_MULTI(String[] newLB_DIPLOME_MULTI) {
		LB_DIPLOME_MULTI = newLB_DIPLOME_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME_MULTI Date de
	 * création : (23/06/11 17:14:10)
	 * 
	 */
	public String getNOM_LB_DIPLOME_MULTI() {
		return "NOM_LB_DIPLOME_MULTI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_MULTI_SELECT Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String getNOM_LB_DIPLOME_MULTI_SELECT() {
		return "NOM_LB_DIPLOME_MULTI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME_MULTI Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String[] getVAL_LB_DIPLOME_MULTI() {
		return getLB_DIPLOME_MULTI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME_MULTI Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String getVAL_LB_DIPLOME_MULTI_SELECT() {
		return getZone(getNOM_LB_DIPLOME_MULTI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NIVEAU_ETUDE_MULTI Date
	 * de création : (23/06/11 17:14:10)
	 * 
	 */
	private String[] getLB_NIVEAU_ETUDE_MULTI() {
		if (LB_NIVEAU_ETUDE_MULTI == null)
			LB_NIVEAU_ETUDE_MULTI = initialiseLazyLB();
		return LB_NIVEAU_ETUDE_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NIVEAU_ETUDE_MULTI Date
	 * de création : (23/06/11 17:14:10)
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE_MULTI() {
		return "NOM_LB_NIVEAU_ETUDE_MULTI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NIVEAU_ETUDE_MULTI_SELECT Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE_MULTI_SELECT() {
		return "NOM_LB_NIVEAU_ETUDE_MULTI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NIVEAU_ETUDE_MULTI Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String[] getVAL_LB_NIVEAU_ETUDE_MULTI() {
		return getLB_NIVEAU_ETUDE_MULTI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NIVEAU_ETUDE_MULTI Date de création : (23/06/11 17:14:10)
	 * 
	 */
	public String getVAL_LB_NIVEAU_ETUDE_MULTI_SELECT() {
		return getZone(getNOM_LB_NIVEAU_ETUDE_MULTI_SELECT());
	}

	/**
	 * Retourne la liste des Diplome.
	 * 
	 * @return listeDiplome ArrayList(Diplome)
	 */
	private ArrayList<DiplomeGenerique> getListeDiplome() {
		return listeDiplome;
	}

	/**
	 * Met à jour la liste des Diplome.
	 * 
	 * @param listeDiplome
	 *            ArrayList(Diplome)
	 */
	private void setListeDiplome(ArrayList<DiplomeGenerique> listeDiplome) {
		this.listeDiplome = listeDiplome;
	}

	/**
	 * Retourne la liste des Diplomes de la fiche emploi.
	 * 
	 * @return listeDiplomeMulti ArrayList(Diplome)
	 */
	private ArrayList<DiplomeGenerique> getListeDiplomeMulti() {
		return listeDiplomeMulti;
	}

	/**
	 * Met à jour la liste des Diplomes de la fiche emploi.
	 * 
	 * @param listeDiplomeMulti
	 *            ArrayList(Diplome)
	 */
	private void setListeDiplomeMulti(ArrayList<DiplomeGenerique> listeDiplomeMulti) {
		this.listeDiplomeMulti = listeDiplomeMulti;
	}

	/**
	 * Retourne la liste des NiveauEtude
	 * 
	 * @return listeNiveauEtude ArrayList(NiveauEtude)
	 */
	private ArrayList<NiveauEtude> getListeNiveauEtude() {
		return listeNiveauEtude;
	}

	/**
	 * Met à jour la liste des NiveauEtude.
	 * 
	 * @param listeNiveauEtude
	 *            ArrayList(NiveauEtude)
	 */
	private void setListeNiveauEtude(ArrayList<NiveauEtude> listeNiveauEtude) {
		this.listeNiveauEtude = listeNiveauEtude;
	}

	/**
	 * Retourne la liste des NiveauEtude de la fiche emploi.
	 * 
	 * @return listeNiveauEtudeMulti ArrayList(NiveauEtude)
	 */
	private ArrayList<NiveauEtude> getListeNiveauEtudeMulti() {
		return listeNiveauEtudeMulti;
	}

	/**
	 * Met à jour la liste des NiveauEtude de la fiche emploi.
	 * 
	 * @param listeNiveauEtudeMulti
	 *            ArrayList(NiveauEtude)
	 */
	private void setListeNiveauEtudeMulti(ArrayList<NiveauEtude> listeNiveauEtudeMulti) {
		this.listeNiveauEtudeMulti = listeNiveauEtudeMulti;
	}

	/**
	 * Retourne la liste des domaines.
	 * 
	 * @return listeDomaine ArrayList(DomaineEmploi)
	 */
	private ArrayList<DomaineEmploi> getListeDomaine() {
		return listeDomaine;
	}

	/**
	 * Met à jour la liste des domaines.
	 * 
	 * @param listeDomaine
	 *            ArrayList(DomaineEmploi)
	 */
	private void setListeDomaine(ArrayList<DomaineEmploi> listeDomaine) {
		this.listeDomaine = listeDomaine;
	}

	/**
	 * Retourne la liste des familles.
	 * 
	 * @return listeFamille ArrayList(FamilleEmploi)
	 */
	private ArrayList<FamilleEmploi> getListeFamille() {
		return listeFamille;
	}

	/**
	 * Met à jour la liste des familles.
	 * 
	 * @param listeFamille
	 *            ArrayList(FamilleEmploi)
	 */
	private void setListeFamille(ArrayList<FamilleEmploi> listeFamille) {
		this.listeFamille = listeFamille;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : EF_CATEGORIE_MULTI Date
	 * de création : (27/06/11 09:09:10)
	 * 
	 */
	public String getNOM_EF_CATEGORIE_MULTI() {
		return "NOM_EF_CATEGORIE_MULTI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * EF_CATEGORIE_MULTI Date de création : (27/06/11 09:09:10)
	 * 
	 */
	public String getVAL_EF_CATEGORIE_MULTI() {
		return getZone(getNOM_EF_CATEGORIE_MULTI());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NIVEAU_ETUDE_MULTI
	 * Date de création : (27/06/11 14:22:21)
	 * 
	 */
	public String getNOM_EF_NIVEAU_ETUDE_MULTI() {
		return "NOM_EF_NIVEAU_ETUDE_MULTI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NIVEAU_ETUDE_MULTI Date de création : (27/06/11 14:22:21)
	 * 
	 */
	public String getVAL_EF_NIVEAU_ETUDE_MULTI() {
		return getZone(getNOM_EF_NIVEAU_ETUDE_MULTI());
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_EF_RECHERCHE_REF_MAIRIE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_AUTRE_APPELLATION
	 * Date de création : (28/06/11 11:33:58)
	 * 
	 */
	public String getNOM_EF_AUTRE_APPELLATION() {
		return "NOM_EF_AUTRE_APPELLATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_AUTRE_APPELLATION Date de création : (28/06/11 11:33:58)
	 * 
	 */
	public String getVAL_EF_AUTRE_APPELLATION() {
		return getZone(getNOM_EF_AUTRE_APPELLATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_RECHERCHE_REF_MAIRIE Date de création : (28/06/11 11:33:58)
	 * 
	 */
	public String getNOM_EF_RECHERCHE_REF_MAIRIE() {
		return "NOM_EF_RECHERCHE_REF_MAIRIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RECHERCHE_REF_MAIRIE Date de création : (28/06/11 11:33:58)
	 * 
	 */
	public String getVAL_EF_RECHERCHE_REF_MAIRIE() {
		return getZone(getNOM_EF_RECHERCHE_REF_MAIRIE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FE Date de
	 * création : (28/06/11 13:51:13)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FE() {
		return "NOM_PB_RECHERCHER_FE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/06/11 13:51:13)
	 * 
	 */
	public boolean performPB_RECHERCHER_FE(HttpServletRequest request) throws Exception {
		// Recherche de la fiche emploi
		if (getVAL_EF_RECHERCHE_REF_MAIRIE() != null && getVAL_EF_RECHERCHE_REF_MAIRIE() != Const.CHAINE_VIDE) {
			FicheEmploi fiche = FicheEmploi.chercherFicheEmploiAvecRefMairie(getTransaction(),
					getVAL_EF_RECHERCHE_REF_MAIRIE());
			if (getTransaction().isErreur()) {
				return false;
			}

			// Mise à jour de l'action menée
			addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

			if (fiche != null && !fiche.getRefMairie().equals(getFicheEmploiCourant().getRefMairie())) {
				viderFicheEmploi();
				setFicheEmploiCourant(fiche);
				setListeAutreAppellationMulti(getAutreAppellationEmploiDao().listerAutreAppellationEmploiAvecFE(
						Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi())));
				setListeActiPrincMulti(Activite.listerActiviteAvecFE(getTransaction(), getFicheEmploiCourant()));
				setListeSavoirMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(),
						getFicheEmploiCourant(), EnumTypeCompetence.SAVOIR.getCode().toString()));
				setListeSavoirFaireMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(),
						getFicheEmploiCourant(), EnumTypeCompetence.SAVOIR_FAIRE.getCode().toString()));
				setListeComportementMulti(Competence.listerCompetenceAvecFEEtTypeComp(getTransaction(),
						getFicheEmploiCourant(), EnumTypeCompetence.COMPORTEMENT.getCode().toString()));
				setListeCategorieMulti(getCategorieDao().listerCategorieAvecFE(
						Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi()), getCategorieFEDao()));
				setListeCadresEmploiMulti(getCadreEmploiDao().listerCadreEmploiAvecFicheEmploi(getCadreEmploiFEDao(),
						Integer.valueOf(getFicheEmploiCourant().getIdFicheEmploi())));
				setListeNiveauEtudeMulti(NiveauEtude.listerNiveauEtudeAvecFE(getTransaction(), getFicheEmploiCourant()));
				setListeDiplomeMulti(getDiplomeGeneriqueDao().listerDiplomeGeneriqueAvecFE(getTransaction(),
						getFicheEmploiCourant()));
			}
		} else {
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR982"));
			return false;
		}

		// On alimente une var d'activité
		if (getFicheEmploiCourant() != null)
			VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, getFicheEmploiCourant());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne la liste des AutreAppelation de la fiche emploi.
	 * 
	 * @return listeAutreAppellationMulti ArrayList(AutreAppellation)
	 */
	private ArrayList<AutreAppellationEmploi> getListeAutreAppellationMulti() {
		return listeAutreAppellationMulti;
	}

	/**
	 * Met à jour la liste des AutreAppellation de la fiche emploi.
	 * 
	 * @param listeAutreAppellationMulti
	 *            ArrayList(AutreAppellation)
	 */
	private void setListeAutreAppellationMulti(ArrayList<AutreAppellationEmploi> listeAutreAppellationMulti) {
		this.listeAutreAppellationMulti = listeAutreAppellationMulti;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AUTRE_APPELLATION
	 * Date de création : (28/06/11 15:39:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AUTRE_APPELLATION() {
		return "NOM_PB_SUPPRIMER_AUTRE_APPELLATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/06/11 15:39:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AUTRE_APPELLATION(HttpServletRequest request) throws Exception {
		// Suppression de la dernière appellation de la liste
		if (getListeAutreAppellationMulti() != null && getListeAutreAppellationMulti().size() != 0) {
			AutreAppellationEmploi aae = (AutreAppellationEmploi) getListeAutreAppellationMulti().get(
					getListeAutreAppellationMulti().size() - 1);
			getListeAutreAppellationMulti().remove(aae);

			if (getListeAutreAppellationAAjouter().contains(aae)) {
				getListeAutreAppellationAAjouter().remove(aae);
			} else {
				getListeAutreAppellationASupprimer().add(aae);
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR967", "autres appellations"));
			return false;
		}

		// Positionnement sur le dernier élément de la liste
		if (getListeAutreAppellationMulti() != null && getListeAutreAppellationMulti().size() > 0) {
			AutreAppellationEmploi dernierAutreAppelMulti = (AutreAppellationEmploi) getListeAutreAppellationMulti()
					.get(getListeAutreAppellationMulti().size() - 1);
			addZone(getNOM_EF_AUTRE_APPELLATION(), dernierAutreAppelMulti.getLibAutreAppellationEmploi());
		}

		return true;
	}

	/**
	 * Retourne vrai si l'utilisateur est en mode modification. Faux sinon (mode
	 * création)
	 * 
	 * @return boolean
	 */
	public boolean isModification() {
		return (getFicheEmploiCourant() != null && getFicheEmploiCourant().getIdFicheEmploi() != null);
	}

	/**
	 * Retourne vrai si l'utilisateur est en mode suppression. Faux sinon (mode
	 * création/modification)
	 * 
	 * @return boolean
	 */
	public boolean isSuppression() {
		return suppression;
	}

	/**
	 * Met à jour le mode suppression.
	 * 
	 * @param newSuppression
	 */
	private void setSuppression(boolean newSuppression) {
		this.suppression = newSuppression;
	}

	/**
	 * Retourne la liste des AutreAppellationEmploi à supprimer.
	 * 
	 * @return listeAutreAppellationASupprimer ArrayList(AutreAppellationEmploi)
	 */
	private ArrayList<AutreAppellationEmploi> getListeAutreAppellationASupprimer() {
		if (listeAutreAppellationASupprimer == null)
			listeAutreAppellationASupprimer = new ArrayList<AutreAppellationEmploi>();
		return listeAutreAppellationASupprimer;
	}

	/**
	 * Retourne la liste des AutreAppellationEmploi à ajouter.
	 * 
	 * @return listeAutreAppellationAAjouter
	 */
	private ArrayList<AutreAppellationEmploi> getListeAutreAppellationAAjouter() {
		if (listeAutreAppellationAAjouter == null)
			listeAutreAppellationAAjouter = new ArrayList<AutreAppellationEmploi>();
		return listeAutreAppellationAAjouter;
	}

	/**
	 * Retourne la liste des CadreEmploi à ajouter.
	 * 
	 * @return listeCadresEmploiAAjouter
	 */
	private ArrayList<CadreEmploi> getListeCadresEmploiAAjouter() {
		if (listeCadresEmploiAAjouter == null)
			listeCadresEmploiAAjouter = new ArrayList<CadreEmploi>();
		return listeCadresEmploiAAjouter;
	}

	/**
	 * Retourne la liste des CadreEmploi à supprimer.
	 * 
	 * @return listeCadresEmploiASupprimer
	 */
	private ArrayList<CadreEmploi> getListeCadresEmploiASupprimer() {
		if (listeCadresEmploiASupprimer == null)
			listeCadresEmploiASupprimer = new ArrayList<CadreEmploi>();
		return listeCadresEmploiASupprimer;
	}

	/**
	 * Retourne la liste des Categorie à ajouter.
	 * 
	 * @return listeCategorieAAjouter
	 */
	private ArrayList<Categorie> getListeCategorieAAjouter() {
		if (listeCategorieAAjouter == null)
			listeCategorieAAjouter = new ArrayList<Categorie>();
		return listeCategorieAAjouter;
	}

	/**
	 * Retourne la liste des Categorie à supprimer.
	 * 
	 * @return listeCategorieASupprimer
	 */
	private ArrayList<Categorie> getListeCategorieASupprimer() {
		if (listeCategorieASupprimer == null)
			listeCategorieASupprimer = new ArrayList<Categorie>();
		return listeCategorieASupprimer;
	}

	/**
	 * Retourne la liste des Diplome à ajouter.
	 * 
	 * @return listeDiplomeAAjouter
	 */
	private ArrayList<DiplomeGenerique> getListeDiplomeAAjouter() {
		if (listeDiplomeAAjouter == null)
			listeDiplomeAAjouter = new ArrayList<DiplomeGenerique>();
		return listeDiplomeAAjouter;
	}

	/**
	 * Retourne la liste des Diplome à supprimer.
	 * 
	 * @return listeDiplomeASupprimer
	 */
	private ArrayList<DiplomeGenerique> getListeDiplomeASupprimer() {
		if (listeDiplomeASupprimer == null)
			listeDiplomeASupprimer = new ArrayList<DiplomeGenerique>();
		return listeDiplomeASupprimer;
	}

	/**
	 * Retourne la liste des NiveauEtude à ajouter.
	 * 
	 * @return listeNiveauEtudeAAjouter
	 */
	private ArrayList<NiveauEtude> getListeNiveauEtudeAAjouter() {
		if (listeNiveauEtudeAAjouter == null)
			listeNiveauEtudeAAjouter = new ArrayList<NiveauEtude>();
		return listeNiveauEtudeAAjouter;
	}

	/**
	 * Retourne la liste des NiveauEtude à supprimer.
	 * 
	 * @return listeNiveauEtudeASupprimer
	 */
	private ArrayList<NiveauEtude> getListeNiveauEtudeASupprimer() {
		if (listeNiveauEtudeASupprimer == null)
			listeNiveauEtudeASupprimer = new ArrayList<NiveauEtude>();
		return listeNiveauEtudeASupprimer;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_CADRE Date de
	 * création : (18/07/11 13:43:51)
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_CADRE() {
		return "NOM_PB_AFFICHER_LISTE_CADRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 13:43:51)
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_CADRE(HttpServletRequest request) throws Exception {
		setAfficherListeCadre(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_CATEGORIE
	 * Date de création : (18/07/11 13:43:51)
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_CATEGORIE() {
		return "NOM_PB_AFFICHER_LISTE_CATEGORIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 13:43:51)
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_CATEGORIE(HttpServletRequest request) throws Exception {
		setAfficherListeCategorie(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_DIPLOME Date
	 * de création : (18/07/11 13:43:51)
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_DIPLOME() {
		return "NOM_PB_AFFICHER_LISTE_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 13:43:51)
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_DIPLOME(HttpServletRequest request) throws Exception {
		setAfficherListeDiplome(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_NIVEAU Date
	 * de création : (18/07/11 13:43:51)
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_NIVEAU() {
		return "NOM_PB_AFFICHER_LISTE_NIVEAU";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 13:43:51)
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_NIVEAU(HttpServletRequest request) throws Exception {
		setAfficherListeNivEt(true);
		return true;
	}

	/**
	 * Retourne vrai si la liste des cadres doit être affichée.
	 * 
	 * @return afficherListeCadre boolean
	 */
	public boolean isAfficherListeCadre() {
		return afficherListeCadre;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des cadres.
	 * 
	 * @param afficherListeCadre
	 *            boolean
	 */
	private void setAfficherListeCadre(boolean afficherListeCadre) {
		this.afficherListeCadre = afficherListeCadre;
	}

	/**
	 * Retourne vrai si la liste des categories doit être affichée.
	 * 
	 * @return afficherListeCategorie boolean
	 */
	public boolean isAfficherListeCategorie() {
		return afficherListeCategorie;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des categories.
	 * 
	 * @param afficherListeCategorie
	 *            boolean
	 */
	private void setAfficherListeCategorie(boolean afficherListeCategorie) {
		this.afficherListeCategorie = afficherListeCategorie;
	}

	/**
	 * Retourne vrai si la liste des diplomes doit être affichée.
	 * 
	 * @return afficherListeDiplome boolean
	 */
	public boolean isAfficherListeDiplome() {
		return afficherListeDiplome;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des diplomes.
	 * 
	 * @param afficherListeDiplome
	 *            boolean
	 */
	private void setAfficherListeDiplome(boolean afficherListeDiplome) {
		this.afficherListeDiplome = afficherListeDiplome;
	}

	/**
	 * Retourne vrai si la liste des niveaux d'étude doit être affichée.
	 * 
	 * @return afficherListeNivEt boolean
	 */
	public boolean isAfficherListeNivEt() {
		return afficherListeNivEt;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des niveaux d'étude.
	 * 
	 * @param afficherListeNivEt
	 *            boolean
	 */
	private void setAfficherListeNivEt(boolean afficherListeNivEt) {
		this.afficherListeNivEt = afficherListeNivEt;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_ACTIVITE_PRINC Date
	 * de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_ACTIVITE_PRINC() {
		return "NOM_PB_AJOUTER_ACTIVITE_PRINC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_ACTIVITE_PRINC(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A05
		ArrayList<Activite> listeToutesActi = new ArrayList<Activite>();
		if (getListeActiPrincMulti() != null) {
			listeToutesActi.addAll(getListeActiPrincMulti());
		}
		VariablesActivite.ajouter(this, "LISTEACTIVITE", listeToutesActi);
		setStatut(STATUT_ACTI_PRINC, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ACTIVITE_PRINC
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ACTIVITE_PRINC(int i) {
		return "NOM_PB_SUPPRIMER_ACTIVITE_PRINC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ACTIVITE_PRINC(HttpServletRequest request, int elemASupprimer) throws Exception {
		Activite a = (Activite) getListeActiPrincMulti().get(elemASupprimer);

		if (a != null) {
			if (getListeActiPrincMulti() != null) {
				getListeActiPrincMulti().remove(a);
				if (getListeActiPrincAAjouter().contains(a)) {
					getListeActiPrincAAjouter().remove(a);
				} else {
					getListeActiPrincASupprimer().add(a);
				}
			}
		}

		return true;
	}

	/**
	 * Retourne la liste des activités principales à ajouter.
	 * 
	 * @return listeActiPrincAAjouter ArrayList
	 */
	private ArrayList<Activite> getListeActiPrincAAjouter() {
		if (listeActiPrincAAjouter == null)
			listeActiPrincAAjouter = new ArrayList<Activite>();
		return listeActiPrincAAjouter;
	}

	/**
	 * Retourne la liste des activités principales à supprimer.
	 * 
	 * @return listeActiPrincASupprimer ArrayList
	 */
	private ArrayList<Activite> getListeActiPrincASupprimer() {
		if (listeActiPrincASupprimer == null)
			listeActiPrincASupprimer = new ArrayList<Activite>();
		return listeActiPrincASupprimer;
	}

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeActiPrincMulti ArrayList
	 */
	public ArrayList<Activite> getListeActiPrincMulti() {
		if (listeActiPrincMulti == null)
			listeActiPrincMulti = new ArrayList<Activite>();
		return listeActiPrincMulti;
	}

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeActiPrincMulti
	 *            ArrayList
	 */
	private void setListeActiPrincMulti(ArrayList<Activite> listeActiPrincMulti) {
		this.listeActiPrincMulti = listeActiPrincMulti;
	}

	/**
	 * Retourne la liste des activités principales à ajouter.
	 * 
	 * @return listeSavoirAAjouter ArrayList
	 */
	private ArrayList<Competence> getListeSavoirAAjouter() {
		if (listeSavoirAAjouter == null)
			listeSavoirAAjouter = new ArrayList<Competence>();
		return listeSavoirAAjouter;
	}

	/**
	 * Retourne la liste des activités principales à supprimer.
	 * 
	 * @return listeSavoirASupprimer ArrayList
	 */
	private ArrayList<Competence> getListeSavoirASupprimer() {
		if (listeSavoirASupprimer == null)
			listeSavoirASupprimer = new ArrayList<Competence>();
		return listeSavoirASupprimer;
	}

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeSavoirMulti ArrayList
	 */
	public ArrayList<Competence> getListeSavoirMulti() {
		if (listeSavoirMulti == null)
			listeSavoirMulti = new ArrayList<Competence>();
		return listeSavoirMulti;
	}

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeSavoirMulti
	 *            ArrayList
	 */
	private void setListeSavoirMulti(ArrayList<Competence> listeSavoirMulti) {
		this.listeSavoirMulti = listeSavoirMulti;
	}

	/**
	 * Retourne la liste des activités principales à ajouter.
	 * 
	 * @return listeSavoirFaireAAjouter ArrayList
	 */
	private ArrayList<Competence> getListeSavoirFaireAAjouter() {
		if (listeSavoirFaireAAjouter == null)
			listeSavoirFaireAAjouter = new ArrayList<Competence>();
		return listeSavoirFaireAAjouter;
	}

	/**
	 * Retourne la liste des activités principales à supprimer.
	 * 
	 * @return listeSavoirFaireASupprimer ArrayList
	 */
	private ArrayList<Competence> getListeSavoirFaireASupprimer() {
		if (listeSavoirFaireASupprimer == null)
			listeSavoirFaireASupprimer = new ArrayList<Competence>();
		return listeSavoirFaireASupprimer;
	}

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeSavoirFaireMulti ArrayList
	 */
	public ArrayList<Competence> getListeSavoirFaireMulti() {
		if (listeSavoirFaireMulti == null)
			listeSavoirFaireMulti = new ArrayList<Competence>();
		return listeSavoirFaireMulti;
	}

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeSavoirFaireMulti
	 *            ArrayList
	 */
	private void setListeSavoirFaireMulti(ArrayList<Competence> listeSavoirFaireMulti) {
		this.listeSavoirFaireMulti = listeSavoirFaireMulti;
	}

	/**
	 * Retourne la liste des activités principales à ajouter.
	 * 
	 * @return listeComportementAAjouter ArrayList
	 */
	private ArrayList<Competence> getListeComportementAAjouter() {
		if (listeComportementAAjouter == null)
			listeComportementAAjouter = new ArrayList<Competence>();
		return listeComportementAAjouter;
	}

	/**
	 * Retourne la liste des activités principales à supprimer.
	 * 
	 * @return listeComportementASupprimer ArrayList
	 */
	private ArrayList<Competence> getListeComportementASupprimer() {
		if (listeComportementASupprimer == null)
			listeComportementASupprimer = new ArrayList<Competence>();
		return listeComportementASupprimer;
	}

	/**
	 * Retourne la liste des activités principales.
	 * 
	 * @return listeComportementMulti ArrayList
	 */
	public ArrayList<Competence> getListeComportementMulti() {
		if (listeComportementMulti == null)
			listeComportementMulti = new ArrayList<Competence>();
		return listeComportementMulti;
	}

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeComportementMulti
	 *            ArrayList
	 */
	private void setListeComportementMulti(ArrayList<Competence> listeComportementMulti) {
		this.listeComportementMulti = listeComportementMulti;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DUPLIQUER_FE Date de
	 * création : (05/09/11 10:08:11)
	 * 
	 */
	public String getNOM_PB_DUPLIQUER_FE() {
		return "NOM_PB_DUPLIQUER_FE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 10:08:11)
	 * 
	 * RG_PE_FE_A02
	 */
	public boolean performPB_DUPLIQUER_FE(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A02
		if (getFicheEmploiCourant().getIdFicheEmploi() != null) {
			FicheEmploi ficheDupliquee = (FicheEmploi) getFicheEmploiCourant().clone();
			ficheDupliquee.setRefMairie(ficheDupliquee.getRefMairie().substring(0, 5)
					+ Services.lpad(String.valueOf(FicheEmploi.genererNumChrono(getTransaction(), ficheDupliquee
							.getRefMairie().substring(0, 5))), 3, "0"));
			ficheDupliquee.creerFicheEmploi(getTransaction());

			// Duplique les AutreAppellation
			for (int i = 0; i < getListeAutreAppellationMulti().size(); i++) {
				AutreAppellationEmploi aa = (AutreAppellationEmploi) getListeAutreAppellationMulti().get(i);
				AutreAppellationEmploi newAA = new AutreAppellationEmploi(Integer.valueOf(ficheDupliquee
						.getIdFicheEmploi()), aa.getLibAutreAppellationEmploi());
				getAutreAppellationEmploiDao().creerAutreAppellationEmploi(newAA.getIdFicheEmploi(),
						newAA.getLibAutreAppellationEmploi());
			}
			// Duplique les Activites principales
			for (int i = 0; i < getListeActiPrincMulti().size(); i++) {
				Activite actiP = (Activite) getListeActiPrincMulti().get(i);
				ActiviteFE newActiFEPrinc = new ActiviteFE(ficheDupliquee.getIdFicheEmploi(), actiP.getIdActivite());
				newActiFEPrinc.creerActiviteFE(getTransaction());
			}
			// Duplique les Savoir
			for (int i = 0; i < getListeSavoirMulti().size(); i++) {
				Competence compSavoir = (Competence) getListeSavoirMulti().get(i);
				CompetenceFE newCompSavoirFE = new CompetenceFE(ficheDupliquee.getIdFicheEmploi(),
						compSavoir.getIdCompetence());
				newCompSavoirFE.creerCompetenceFE(getTransaction());
			}
			// Duplique les SavoirFaire
			for (int i = 0; i < getListeSavoirFaireMulti().size(); i++) {
				Competence compSavoirFaire = (Competence) getListeSavoirFaireMulti().get(i);
				CompetenceFE newCompSavoirFaireFE = new CompetenceFE(ficheDupliquee.getIdFicheEmploi(),
						compSavoirFaire.getIdCompetence());
				newCompSavoirFaireFE.creerCompetenceFE(getTransaction());
			}
			// Duplique les Comportement
			for (int i = 0; i < getListeComportementMulti().size(); i++) {
				Competence compComportement = (Competence) getListeComportementMulti().get(i);
				CompetenceFE newCompComportementFE = new CompetenceFE(ficheDupliquee.getIdFicheEmploi(),
						compComportement.getIdCompetence());
				newCompComportementFE.creerCompetenceFE(getTransaction());
			}
			// Duplique les Categorie
			for (int i = 0; i < getListeCategorieMulti().size(); i++) {
				Categorie cat = (Categorie) getListeCategorieMulti().get(i);
				CategorieFE newCatFE = new CategorieFE(Integer.valueOf(ficheDupliquee.getIdFicheEmploi()),
						cat.getIdCategorieStatut());
				getCategorieFEDao().creerCategorieFE(newCatFE.getIdFicheEmploi(), newCatFE.getIdCategorieStatut());
			}
			// Duplique les CadreEmploi
			for (int i = 0; i < getListeCadresEmploiMulti().size(); i++) {
				CadreEmploi cadre = (CadreEmploi) getListeCadresEmploiMulti().get(i);
				CadreEmploiFE newCadreFE = new CadreEmploiFE(Integer.valueOf(ficheDupliquee.getIdFicheEmploi()),
						cadre.getIdCadreEmploi());
				getCadreEmploiFEDao().creerCadreEmploiFE(newCadreFE.getIdFicheEmploi(), newCadreFE.getIdCadreEmploi());
			}
			// Duplique les NiveauEtude
			for (int i = 0; i < getListeNiveauEtudeMulti().size(); i++) {
				NiveauEtude niv = (NiveauEtude) getListeNiveauEtudeMulti().get(i);
				NiveauEtudeFE newNivFE = new NiveauEtudeFE(ficheDupliquee.getIdFicheEmploi(), niv.getIdNiveauEtude());
				newNivFE.creerNiveauEtudeFE(getTransaction());
			}
			// Duplique les Diplome
			for (int i = 0; i < getListeDiplomeMulti().size(); i++) {
				DiplomeGenerique dipl = (DiplomeGenerique) getListeDiplomeMulti().get(i);
				DiplomeFE newDiplFE = new DiplomeFE(ficheDupliquee.getIdFicheEmploi(), dipl.getIdDiplomeGenerique()
						.toString());
				newDiplFE.creerDiplomeFE(getTransaction());
			}
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR976", "FicheEmploi " + ficheDupliquee.getRefMairie()));
				return false;
			}
			setFicheEmploiCourant(ficheDupliquee);
			addZone(getNOM_ST_ACTION(), ACTION_DUPLICATION);
			messageInf = MessageUtils.getMessage("INF100", ficheDupliquee.getRefMairie());
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("INF107"));
			return false;
		}

		if (getTransaction().isErreur())
			return false;

		// COMMIT
		commitTransaction();

		if (!Const.CHAINE_VIDE.equals(messageInf))
			getTransaction().declarerErreur(messageInf);

		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CHANGER_TYPE
			if (testerParametre(request, getNOM_PB_CHANGER_TYPE())) {
				return performPB_CHANGER_TYPE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_FE
			if (testerParametre(request, getNOM_PB_AJOUTER_FE())) {
				return performPB_AJOUTER_FE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_FE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_FE())) {
				return performPB_SUPPRIMER_FE(request);
			}

			// Si clic sur le bouton PB_DUPLIQUER_FE
			if (testerParametre(request, getNOM_PB_DUPLIQUER_FE())) {
				return performPB_DUPLIQUER_FE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ACTIVITE_PRINC
			for (int i = 0; i < getListeActiPrincMulti().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_ACTIVITE_PRINC(i))) {
					return performPB_SUPPRIMER_ACTIVITE_PRINC(request, i);
				}
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_CADRE
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_CADRE())) {
				return performPB_AFFICHER_LISTE_CADRE(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_CATEGORIE
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_CATEGORIE())) {
				return performPB_AFFICHER_LISTE_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_DIPLOME
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_DIPLOME())) {
				return performPB_AFFICHER_LISTE_DIPLOME(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_NIVEAU
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_NIVEAU())) {
				return performPB_AFFICHER_LISTE_NIVEAU(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AUTRE_APPELLATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_AUTRE_APPELLATION())) {
				return performPB_SUPPRIMER_AUTRE_APPELLATION(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_FE
			if (testerParametre(request, getNOM_PB_RECHERCHER_FE())) {
				return performPB_RECHERCHER_FE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_CATEGORIE
			if (testerParametre(request, getNOM_PB_AJOUTER_CATEGORIE())) {
				return performPB_AJOUTER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DIPLOME
			if (testerParametre(request, getNOM_PB_AJOUTER_DIPLOME())) {
				return performPB_AJOUTER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_AJOUTER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_AJOUTER_NIVEAU_ETUDE())) {
				return performPB_AJOUTER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CADRE_EMPLOI
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CADRE_EMPLOI())) {
				return performPB_SUPPRIMER_CADRE_EMPLOI(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CATEGORIE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CATEGORIE())) {
				return performPB_SUPPRIMER_CATEGORIE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DIPLOME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DIPLOME())) {
				return performPB_SUPPRIMER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NIVEAU_ETUDE())) {
				return performPB_SUPPRIMER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_CADRE_EMPLOI
			if (testerParametre(request, getNOM_PB_AJOUTER_CADRE_EMPLOI())) {
				return performPB_AJOUTER_CADRE_EMPLOI(request);
			}

			// Si clic sur le bouton PB_AJOUTER_ACTIVITE_PRINC
			if (testerParametre(request, getNOM_PB_AJOUTER_ACTIVITE_PRINC())) {
				return performPB_AJOUTER_ACTIVITE_PRINC(request);
			}

			// Si clic sur le bouton PB_AJOUTER_AUTRE_APPELLATION
			if (testerParametre(request, getNOM_PB_AJOUTER_AUTRE_APPELLATION())) {
				return performPB_AJOUTER_AUTRE_APPELLATION(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}
			// Si clic sur le bouton PB_RECHERCHE_AVANCEE
			if (testerParametre(request, getNOM_PB_RECHERCHE_AVANCEE())) {
				return performPB_RECHERCHE_AVANCEE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE
			for (int i = 0; i < getListeSavoirFaireMulti().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE(i))) {
					return performPB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_COMPETENCE_SAVOIR
			for (int i = 0; i < getListeSavoirMulti().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR(i))) {
					return performPB_SUPPRIMER_COMPETENCE_SAVOIR(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_COMPETENCE_COMPORTEMENT
			for (int i = 0; i < getListeComportementMulti().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_COMPETENCE_COMPORTEMENT(i))) {
					return performPB_SUPPRIMER_COMPETENCE_COMPORTEMENT(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_COMPORTEMENT
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT())) {
				return performPB_AJOUTER_COMPETENCE_COMPORTEMENT(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFicheEmploi. Date de création : (05/09/11
	 * 14:10:47)
	 * 
	 */
	public OePOSTEFicheEmploi() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (05/09/11 14:10:47)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFicheEmploi.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PE-FE-FICHEEMPLOI";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_FE Date de création
	 * : (05/09/11 14:10:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_FE() {
		return "NOM_PB_AJOUTER_FE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 14:10:47)
	 * 
	 */
	public boolean performPB_AJOUTER_FE(HttpServletRequest request) throws Exception {

		viderFicheEmploi();
		viderObjetsFicheEmploi();

		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Efface tous les objets liés à la fiche emploi courante.
	 */
	private void viderObjetsFicheEmploi() {
		setFicheEmploiCourant(null);

		setListeAutreAppellationMulti(null);
		setListeActiPrincMulti(null);
		getListeActiPrincASupprimer().clear();
		getListeActiPrincAAjouter().clear();
		setListeSavoirMulti(null);
		getListeSavoirASupprimer().clear();
		getListeSavoirAAjouter().clear();
		setListeSavoirFaireMulti(null);
		getListeSavoirFaireASupprimer().clear();
		getListeSavoirFaireAAjouter().clear();
		setListeComportementMulti(null);
		getListeComportementASupprimer().clear();
		getListeComportementASupprimer().clear();
		setListeCategorieMulti(null);
		setListeCadresEmploiMulti(null);
		setListeNiveauEtudeMulti(null);
		setListeDiplomeMulti(null);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_FE Date de
	 * création : (05/09/11 14:10:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_FE() {
		return "NOM_PB_SUPPRIMER_FE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 14:10:47)
	 * 
	 * RG_PE_FE_A03
	 */
	public boolean performPB_SUPPRIMER_FE(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A03
		if (getFicheEmploiCourant() != null && getFicheEmploiCourant().getIdFicheEmploi() != null) {
			setSuppression(true);
			ArrayList<FEFP> lienFE_FP = FEFP.listerFEFPAvecFE(getTransaction(), getFicheEmploiCourant());
			if (lienFE_FP.size() > 0) {
				getTransaction().declarerErreur(
						MessageUtils.getMessage("INF105", getFicheEmploiCourant().getRefMairie()));
				setSuppressionLienFE_FP(true);
			} else {
				getTransaction().declarerErreur(
						MessageUtils.getMessage("INF102", getFicheEmploiCourant().getRefMairie()));
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR122"));
		}
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne vrai si l'utilisateur est en mode suppression. Faux sinon (mode
	 * création/modification)
	 * 
	 * @return boolean
	 */
	public boolean isSuppressionLienFE_FP() {
		return suppressionLienFE_FP;
	}

	private void setSuppressionLienFE_FP(boolean newSuppressionLienFE_FP) {
		this.suppressionLienFE_FP = newSuppressionLienFE_FP;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_AVANCEE Date de
	 * création : (13/09/11 08:35:27)
	 * 
	 */
	public String getNOM_PB_RECHERCHE_AVANCEE() {
		return "NOM_PB_RECHERCHE_AVANCEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:35:27)
	 * 
	 */
	public boolean performPB_RECHERCHE_AVANCEE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHE_AVANCEE, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_COMPETENCE_SAVOIR
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A05
		ArrayList<Competence> listeToutesCompSavoir = new ArrayList<Competence>();
		if (getListeSavoirMulti() != null) {
			listeToutesCompSavoir.addAll(getListeSavoirMulti());
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIR", listeToutesCompSavoir);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A05
		ArrayList<Competence> listeToutesCompSavoirFaire = new ArrayList<Competence>();
		if (getListeSavoirFaireMulti() != null) {
			listeToutesCompSavoirFaire.addAll(getListeSavoirFaireMulti());
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIRFAIRE", listeToutesCompSavoirFaire);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT() {
		return "NOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * RG_PE_FE_A05
	 */
	public boolean performPB_AJOUTER_COMPETENCE_COMPORTEMENT(HttpServletRequest request) throws Exception {
		// RG_PE_FE_A05
		ArrayList<Competence> listeToutesCompCOmportement = new ArrayList<Competence>();
		if (getListeComportementMulti() != null) {
			listeToutesCompCOmportement.addAll(getListeComportementMulti());
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCECOMPORTEMENT", listeToutesCompCOmportement);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_COMPETENCE_SAVOIR
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR(int i) {
		return "NOM_PB_SUPPRIMER_COMPETENCE_SAVOIR" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_COMPETENCE_SAVOIR(HttpServletRequest request, int elemASupp) throws Exception {
		Competence c = (Competence) getListeSavoirMulti().get(elemASupp);

		if (c != null) {
			if (getListeSavoirMulti() != null) {
				getListeSavoirMulti().remove(c);
				if (getListeSavoirAAjouter().contains(c)) {
					getListeSavoirAAjouter().remove(c);
				} else {
					getListeSavoirASupprimer().add(c);
				}
			}
		}
		setFocus(getNOM_PB_VALIDER());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE Date de création : (18/07/11
	 * 16:08:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE(int i) {
		return "NOM_PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE(HttpServletRequest request, int elemASupprimer)
			throws Exception {
		Competence c = (Competence) getListeSavoirFaireMulti().get(elemASupprimer);

		if (c != null) {
			if (getListeSavoirFaireMulti() != null) {
				getListeSavoirFaireMulti().remove(c);
				if (getListeSavoirFaireAAjouter().contains(c)) {
					getListeSavoirFaireAAjouter().remove(c);
				} else {
					getListeSavoirFaireASupprimer().add(c);
				}
			}
		}
		setFocus(getNOM_PB_VALIDER());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11
	 * 16:08:47)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_COMPETENCE_COMPORTEMENT(int i) {
		return "NOM_PB_SUPPRIMER_COMPETENCE_COMPORTEMENT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 */
	public boolean performPB_SUPPRIMER_COMPETENCE_COMPORTEMENT(HttpServletRequest request, int elemASupp)
			throws Exception {
		Competence c = (Competence) getListeComportementMulti().get(elemASupp);

		if (c != null) {
			if (getListeComportementMulti() != null) {
				getListeComportementMulti().remove(c);
				if (getListeComportementAAjouter().contains(c)) {
					getListeComportementAAjouter().remove(c);
				} else {
					getListeComportementASupprimer().add(c);
				}
			}
		}
		setFocus(getNOM_PB_VALIDER());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_TYPE Date de
	 * création : (04/07/11 13:57:35)
	 * 
	 */
	public String getNOM_PB_CHANGER_TYPE() {
		return "NOM_PB_CHANGER_TYPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/07/11 13:57:35)
	 * 
	 */
	public boolean performPB_CHANGER_TYPE(HttpServletRequest request) throws Exception {
		/*
		 * setListeSavoirMulti(null); setListeSavoirFaireMulti(null);
		 * setListeComportementMulti(null);
		 */
		setFocus(getNOM_PB_VALIDER());
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RG_TYPE_COMPETENCE() {
		return "NOM_RG_TYPE_COMPETENCE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_RG_TYPE_COMPETENCE() {
		return getZone(getNOM_RG_TYPE_COMPETENCE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_C Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_C() {
		return "NOM_RB_TYPE_COMPETENCE_C";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_S Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_S() {
		return "NOM_RB_TYPE_COMPETENCE_S";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_SF Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_SF() {
		return "NOM_RB_TYPE_COMPETENCE_SF";
	}

	public TypeCompetence getTypeCompetenceCourant() {
		return typeCompetenceCourant;
	}

	private void setTypeCompetenceCourant(TypeCompetence typeCompetenceCourant) {
		this.typeCompetenceCourant = typeCompetenceCourant;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ACTI Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP_SF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_SF(int i) {
		return "NOM_ST_LIB_COMP_SF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP_SF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_SF(int i) {
		return getZone(getNOM_ST_LIB_COMP_SF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP_S Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_S(int i) {
		return "NOM_ST_LIB_COMP_S" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP_S
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_S(int i) {
		return getZone(getNOM_ST_LIB_COMP_S(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP_PRO Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_PRO(int i) {
		return "NOM_ST_LIB_COMP_PRO" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP_PRO
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_PRO(int i) {
		return getZone(getNOM_ST_LIB_COMP_PRO(i));
	}

	public ArrayList<CodeRome> getListeCodeRome() {
		if (listeCodeRome == null)
			listeCodeRome = new ArrayList<CodeRome>();
		return listeCodeRome;
	}

	private void setListeCodeRome(ArrayList<CodeRome> listeCodeRome) {
		this.listeCodeRome = listeCodeRome;
	}

	public CadreEmploiDao getCadreEmploiDao() {
		return cadreEmploiDao;
	}

	public void setCadreEmploiDao(CadreEmploiDao cadreEmploiDao) {
		this.cadreEmploiDao = cadreEmploiDao;
	}

	public CodeRomeDao getCodeRomeDao() {
		return codeRomeDao;
	}

	public void setCodeRomeDao(CodeRomeDao codeRomeDao) {
		this.codeRomeDao = codeRomeDao;
	}

	public DiplomeGeneriqueDao getDiplomeGeneriqueDao() {
		return diplomeGeneriqueDao;
	}

	public void setDiplomeGeneriqueDao(DiplomeGeneriqueDao diplomeGeneriqueDao) {
		this.diplomeGeneriqueDao = diplomeGeneriqueDao;
	}

	public DomaineEmploiDao getDomaineEmploiDao() {
		return domaineEmploiDao;
	}

	public void setDomaineEmploiDao(DomaineEmploiDao domaineEmploiDao) {
		this.domaineEmploiDao = domaineEmploiDao;
	}

	public FamilleEmploiDao getFamilleEmploiDao() {
		return familleEmploiDao;
	}

	public void setFamilleEmploiDao(FamilleEmploiDao familleEmploiDao) {
		this.familleEmploiDao = familleEmploiDao;
	}

	public TypeCompetenceDao getTypeCompetenceDao() {
		return typeCompetenceDao;
	}

	public void setTypeCompetenceDao(TypeCompetenceDao typeCompetenceDao) {
		this.typeCompetenceDao = typeCompetenceDao;
	}

	public AutreAppellationEmploiDao getAutreAppellationEmploiDao() {
		return autreAppellationEmploiDao;
	}

	public void setAutreAppellationEmploiDao(AutreAppellationEmploiDao autreAppellationEmploiDao) {
		this.autreAppellationEmploiDao = autreAppellationEmploiDao;
	}

	public CadreEmploiFEDao getCadreEmploiFEDao() {
		return cadreEmploiFEDao;
	}

	public void setCadreEmploiFEDao(CadreEmploiFEDao cadreEmploiFEDao) {
		this.cadreEmploiFEDao = cadreEmploiFEDao;
	}

	public CategorieDao getCategorieDao() {
		return categorieDao;
	}

	public void setCategorieDao(CategorieDao categorieDao) {
		this.categorieDao = categorieDao;
	}

	public CategorieFEDao getCategorieFEDao() {
		return categorieFEDao;
	}

	public void setCategorieFEDao(CategorieFEDao categorieFEDao) {
		this.categorieFEDao = categorieFEDao;
	}
}
