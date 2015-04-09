package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.PrimePointageFP;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.specificites.RubriqueDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFPSpecificites Date de création : (27/07/11 12:13:47)
 * 
 */
public class OePOSTEFPSpecificites extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String ACTION_AJOUTER = "Ajouter";
	public final String ACTION_SUPPRIMER = "Supprimer";
	public final String SPEC_AVANTAGE_NATURE = "avantage en nature";
	public final String SPEC_DELEGATION = "délégation";
	public final String SPEC_REG_INDEMN = "régime indemnitaire";
	public final String SPEC_PRIME_POINTAGE = "prime pointage";

	private String[] LB_AVANTAGE;
	private String[] LB_DELEGATION;
	private String[] LB_NATURE_AVANTAGE;
	private String[] LB_REGIME;
	private String[] LB_PRIME_POINTAGE;
	private String[] LB_RUBRIQUE_AVANTAGE;
	private String[] LB_RUBRIQUE_REGIME;
	private String[] LB_RUBRIQUE_PRIME_POINTAGE;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;
	private String[] LB_TYPE_REGIME;

	private ArrayList<AvantageNature> listeAvantage;
	private ArrayList<AvantageNature> listeAvantageAAjouter;
	private ArrayList<AvantageNature> listeAvantageASupprimer;
	private ArrayList<Delegation> listeDelegation;
	private ArrayList<Delegation> listeDelegationAAjouter;
	private ArrayList<Delegation> listeDelegationASupprimer;
	private ArrayList<RegimeIndemnitaire> listeRegime;
	private ArrayList<RegimeIndemnitaire> listeRegimeAAjouter;
	private ArrayList<RegimeIndemnitaire> listeRegimeASupprimer;

	private ArrayList<TypeAvantage> listeTypeAvantage;
	private ArrayList<NatureAvantage> listeNatureAvantage;
	private ArrayList<Rubrique> listeRubrique;
	private List<RefPrimeDto> listePrimes = new ArrayList<>();
	private ArrayList<PrimePointageFP> listePrimePointageFP = new ArrayList<>();
	private ArrayList<PrimePointageFP> listePrimePointageFPAAjouter = new ArrayList<>();
	private ArrayList<PrimePointageFP> listePrimePointageFPASupprimer = new ArrayList<>();
	private ArrayList<TypeDelegation> listeTypeDelegation;
	private ArrayList<TypeRegIndemn> listeTypeRegIndemn;

	public String focus = null;

	private NatureAvantageDao natureAvantageDao;
	private TypeAvantageDao typeAvantageDao;
	private TypeDelegationDao typeDelegationDao;
	private TypeRegIndemnDao typeRegIndemnDao;
	private RubriqueDao rubriqueDao;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		if (getListeAvantage() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE, getListeAvantage());
		if (getListeAvantageAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT,
					getListeAvantageAAjouter());
		if (getListeAvantageASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR,
					getListeAvantageASupprimer());

		if (getListeDelegation() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION, getListeDelegation());
		if (getListeDelegationAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT,
					getListeDelegationAAjouter());
		if (getListeDelegationASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR,
					getListeDelegationASupprimer());

		if (getListeRegime() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN, getListeRegime());
		if (getListeRegimeAAjouter() != null)
			VariablesActivite
					.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT, getListeRegimeAAjouter());
		if (getListeRegimeASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR,
					getListeRegimeASupprimer());

		if (getListePrimes() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE, getListePrimePointageFP());
		if (getListePrimePointageFPAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_AJOUT,
					getListePrimePointageFPAAjouter());
		if (getListePrimePointageFPASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_SUPPR,
					getListePrimePointageFPASupprimer());

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Controle les zones saisies d'un avantage en nature. Date de création :
	 * (28/07/11)
	 */
	private boolean performControlerSaisieAvNat(HttpServletRequest request) throws Exception {

		// **************************
		// Verification Type avantage
		// **************************
		if (getVAL_LB_TYPE_AVANTAGE_SELECT().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type avantage"));
			setFocus(getNOM_LB_TYPE_AVANTAGE());
			return false;
		}

		// ****************************************
		// Verification Montant OU Nature renseigné
		// ****************************************
		if (getVAL_EF_MONTANT_AVANTAGE().length() == 0
				&& ((NatureAvantage) getListeNatureAvantage().get(Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT())))
						.getIdNatureAvantage() == null) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Nature avantage", "Montant"));
			setFocus(getNOM_LB_NATURE_AVANTAGE());
			return false;
		}

		// ********************
		// Verification Montant
		// ********************
		if (getVAL_EF_MONTANT_AVANTAGE().length() != 0 && !Services.estNumerique(getVAL_EF_MONTANT_AVANTAGE())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Montant"));
			setFocus(getNOM_EF_MONTANT_AVANTAGE());
			return false;
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'une délégation. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieDel(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Controle les zones saisies d'un régime indemnitaire. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieRegIndemn(HttpServletRequest request) throws Exception {

		// *******************************************
		// Verification Forfait OU Nb points renseigné
		// *******************************************
		if (getVAL_EF_FORFAIT_REGIME().length() == 0 && getVAL_EF_NB_POINTS_REGIME().length() == 0) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Forfait", "Nb points"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// ********************
		// Verification Forfait
		// ********************
		if (getVAL_EF_FORFAIT_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_FORFAIT_REGIME())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Forfait"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// **********************
		// Verification Nb points
		// **********************
		if (getVAL_EF_NB_POINTS_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_NB_POINTS_REGIME())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Nb points"));
			setFocus(getNOM_EF_NB_POINTS_REGIME());
			return false;
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_CODE_RUBRIQUE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_ST_CODE_RUBRIQUE_AVANTAGE() {
		return "NOM_ST_CODE_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_CODE_RUBRIQUE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_ST_CODE_RUBRIQUE_AVANTAGE() {
		return getZone(getNOM_ST_CODE_RUBRIQUE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_RUBRIQUE_REGIME
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_ST_CODE_RUBRIQUE_REGIME() {
		return "NOM_ST_CODE_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_CODE_RUBRIQUE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_ST_CODE_RUBRIQUE_REGIME() {
		return getZone(getNOM_ST_CODE_RUBRIQUE_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENT_DELEGATION
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_COMMENT_DELEGATION() {
		return "NOM_EF_COMMENT_DELEGATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENT_DELEGATION Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_COMMENT_DELEGATION() {
		return getZone(getNOM_EF_COMMENT_DELEGATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FORFAIT_REGIME Date
	 * de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_FORFAIT_REGIME() {
		return "NOM_EF_FORFAIT_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_FORFAIT_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_FORFAIT_REGIME() {
		return getZone(getNOM_EF_FORFAIT_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_AVANTAGE
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_MONTANT_AVANTAGE() {
		return "NOM_EF_MONTANT_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_MONTANT_AVANTAGE() {
		return getZone(getNOM_EF_MONTANT_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_POINTS_REGIME
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_NB_POINTS_REGIME() {
		return "NOM_EF_NB_POINTS_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NB_POINTS_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_NB_POINTS_REGIME() {
		return getZone(getNOM_EF_NB_POINTS_REGIME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_AVANTAGE() {
		if (LB_RUBRIQUE_AVANTAGE == null)
			LB_RUBRIQUE_AVANTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_AVANTAGE Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_AVANTAGE(String[] newLB_RUBRIQUE_AVANTAGE) {
		LB_RUBRIQUE_AVANTAGE = newLB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE() {
		return "NOM_LB_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_AVANTAGE_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_AVANTAGE() {
		return getLB_RUBRIQUE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_REGIME Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_REGIME() {
		if (LB_RUBRIQUE_REGIME == null)
			LB_RUBRIQUE_REGIME = initialiseLazyLB();
		return LB_RUBRIQUE_REGIME;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_REGIME Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_REGIME(String[] newLB_RUBRIQUE_REGIME) {
		LB_RUBRIQUE_REGIME = newLB_RUBRIQUE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_REGIME Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME() {
		return "NOM_LB_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_REGIME_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME_SELECT() {
		return "NOM_LB_RUBRIQUE_REGIME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_REGIME() {
		return getLB_RUBRIQUE_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_REGIME_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_REGIME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_PRIME_POINTAGE() {
		if (LB_RUBRIQUE_PRIME_POINTAGE == null)
			LB_RUBRIQUE_PRIME_POINTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_PRIME_POINTAGE Date de création :
	 * (27/07/11 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_PRIME_POINTAGE(String[] newLB_RUBRIQUE_PRIME_POINTAGE) {
		LB_RUBRIQUE_PRIME_POINTAGE = newLB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_PRIME_POINTAGE() {
		return getLB_RUBRIQUE_PRIME_POINTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de création : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT());
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
		return getNOM_PB_VALIDER();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AVANTAGE Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AVANTAGE() {
		return "NOM_PB_AJOUTER_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_AVANTAGE_NATURE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DELEGATION Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DELEGATION() {
		return "NOM_PB_AJOUTER_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_DELEGATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REGIME Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_REGIME() {
		return "NOM_PB_AJOUTER_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_REGIME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_PRIME_POINTAGE Date
	 * de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_PRIME_POINTAGE() {
		return "NOM_PB_AJOUTER_PRIME_POINTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_PRIME_POINTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_PRIME_POINTAGE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AVANTAGE Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AVANTAGE(HttpServletRequest request) throws Exception {
		// Récupération de l'avantage a supprimer
		int indiceAvNat = (Services.estNumerique(getVAL_LB_AVANTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_AVANTAGE_SELECT()) : -1);
		if (indiceAvNat == -1 || getListeAvantage().size() == 0 || indiceAvNat > getListeAvantage().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Avantages en nature"));
			return false;
		}
		AvantageNature an = (AvantageNature) getListeAvantage().get(indiceAvNat);

		if (an != null) {
			if (getListeAvantage() != null) {
				getListeAvantage().remove(an);
				if (getListeAvantageAAjouter().contains(an)) {
					getListeAvantageAAjouter().remove(an);
				} else {
					getListeAvantageASupprimer().add(an);
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELEGATION Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELEGATION() {
		return "NOM_PB_SUPPRIMER_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELEGATION(HttpServletRequest request) throws Exception {
		// Récupération de la délégation a supprimer
		int indiceDel = (Services.estNumerique(getVAL_LB_DELEGATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_DELEGATION_SELECT()) : -1);
		if (indiceDel == -1 || getListeDelegation().size() == 0 || indiceDel > getListeDelegation().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "délégations"));
			return false;
		}
		Delegation del = (Delegation) getListeDelegation().get(indiceDel);

		if (del != null) {
			if (getListeDelegation() != null) {
				getListeDelegation().remove(del);
				if (getListeDelegationAAjouter().contains(del)) {
					getListeDelegationAAjouter().remove(del);
				} else {
					getListeDelegationASupprimer().add(del);
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REGIME Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REGIME() {
		return "NOM_PB_SUPPRIMER_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REGIME(HttpServletRequest request) throws Exception {
		// Récupération du régime indemnitaire a supprimer
		int indiceRI = (Services.estNumerique(getVAL_LB_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_REGIME_SELECT())
				: -1);
		if (indiceRI == -1 || getListeRegime().size() == 0 || indiceRI > getListeRegime().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Régimes indemnitaires"));
			return false;
		}
		RegimeIndemnitaire ri = (RegimeIndemnitaire) getListeRegime().get(indiceRI);

		if (ri != null) {
			if (getListeRegime() != null) {
				getListeRegime().remove(ri);
				if (getListeRegimeAAjouter().contains(ri)) {
					getListeRegimeAAjouter().remove(ri);
				} else {
					getListeRegimeASupprimer().add(ri);
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_PRIME_POINTAGE
	 * Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_PRIME_POINTAGE() {
		return "NOM_PB_SUPPRIMER_PRIME_POINTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_PRIME_POINTAGE(HttpServletRequest request) throws Exception {
		// Récupération de la prime pointage a supprimer
		int indicePrime = (Services.estNumerique(getVAL_LB_PRIME_POINTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_PRIME_POINTAGE_SELECT()) : -1);
		if (indicePrime == -1 || getListePrimePointageFP().isEmpty()
				|| indicePrime > getListePrimePointageFP().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Primes"));
			return false;
		}
		PrimePointageFP prime = getListePrimePointageFP().get(indicePrime);

		if (prime != null) {
			if (getListePrimePointageFP() != null) {
				getListePrimePointageFP().remove(prime);
				if (getListePrimePointageFPAAjouter().contains(prime)) {
					getListePrimePointageFPAAjouter().remove(prime);
				} else {
					getListePrimePointageFPASupprimer().add(prime);
				}
			}
		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVANTAGE Date de création
	 * : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_AVANTAGE() {
		if (LB_AVANTAGE == null)
			LB_AVANTAGE = initialiseLazyLB();
		return LB_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_AVANTAGE Date de création : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_AVANTAGE(String[] newLB_AVANTAGE) {
		LB_AVANTAGE = newLB_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVANTAGE Date de création
	 * : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_AVANTAGE() {
		return "NOM_LB_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVANTAGE_SELECT Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_AVANTAGE_SELECT() {
		return "NOM_LB_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVANTAGE Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_AVANTAGE() {
		return getLB_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_AVANTAGE Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DELEGATION Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_DELEGATION() {
		if (LB_DELEGATION == null)
			LB_DELEGATION = initialiseLazyLB();
		return LB_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_DELEGATION Date de création : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_DELEGATION(String[] newLB_DELEGATION) {
		LB_DELEGATION = newLB_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DELEGATION Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_DELEGATION() {
		return "NOM_LB_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DELEGATION_SELECT Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_DELEGATION_SELECT() {
		return "NOM_LB_DELEGATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DELEGATION Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_DELEGATION() {
		return getLB_DELEGATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_DELEGATION Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_DELEGATION_SELECT() {
		return getZone(getNOM_LB_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_PRIME_POINTAGE Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_PRIME_POINTAGE() {
		if (LB_PRIME_POINTAGE == null)
			LB_PRIME_POINTAGE = initialiseLazyLB();
		return LB_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_PRIME_POINTAGE Date de création : (27/07/11
	 * 14:09:02)
	 * 
	 */
	private void setLB_PRIME_POINTAGE(String[] newLB_PRIME_POINTAGE) {
		LB_PRIME_POINTAGE = newLB_PRIME_POINTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_PRIME_POINTAGE Date de
	 * création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_PRIME_POINTAGE() {
		return "NOM_LB_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_PRIME_POINTAGE_SELECT Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_PRIME_POINTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_PRIME_POINTAGE Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_PRIME_POINTAGE() {
		return getLB_PRIME_POINTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_PRIME_POINTAGE Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REGIME Date de création :
	 * (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_REGIME() {
		if (LB_REGIME == null)
			LB_REGIME = initialiseLazyLB();
		return LB_REGIME;
	}

	/**
	 * Setter de la liste: LB_REGIME Date de création : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_REGIME(String[] newLB_REGIME) {
		LB_REGIME = newLB_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REGIME Date de création :
	 * (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_REGIME() {
		return "NOM_LB_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_REGIME_SELECT Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_REGIME_SELECT() {
		return "NOM_LB_REGIME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_REGIME Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_REGIME() {
		return getLB_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_REGIME Date de création : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_REGIME_SELECT() {
		return getZone(getNOM_LB_REGIME_SELECT());
	}

	/**
	 * Retourne la liste des AvantageNature.
	 * 
	 * @return listeAvantage
	 */
	private ArrayList<AvantageNature> getListeAvantage() {
		return listeAvantage;
	}

	/**
	 * Met a jour la liste des AvantageNature.
	 * 
	 * @param listeAvantage
	 */
	private void setListeAvantage(ArrayList<AvantageNature> listeAvantage) {
		this.listeAvantage = listeAvantage;
	}

	/**
	 * Retourne la liste des Delegation.
	 * 
	 * @return listeDelegation
	 */
	private ArrayList<Delegation> getListeDelegation() {
		return listeDelegation;
	}

	/**
	 * Met a jour la liste des Delegation.
	 * 
	 * @param listeDelegation
	 */
	private void setListeDelegation(ArrayList<Delegation> listeDelegation) {
		this.listeDelegation = listeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeRegime
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegime() {
		return listeRegime;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeRegime
	 */
	private void setListeRegime(ArrayList<RegimeIndemnitaire> listeRegime) {
		this.listeRegime = listeRegime;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (27/07/11 14:55:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();
		initialiseListeDeroulante();
		initialiseListeSpecificites();
		if (getVAL_RG_SPECIFICITE() == null || getVAL_RG_SPECIFICITE().length() == 0)
			addZone(getNOM_RG_SPECIFICITE(), getNOM_RB_SPECIFICITE_PP());
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getNatureAvantageDao() == null) {
			setNatureAvantageDao(new NatureAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeAvantageDao() == null) {
			setTypeAvantageDao(new TypeAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeDelegationDao() == null) {
			setTypeDelegationDao(new TypeDelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeRegIndemnDao() == null) {
			setTypeRegIndemnDao(new TypeRegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRubriqueDao() == null) {
			setRubriqueDao(new RubriqueDao((MairieDao) context.getBean("mairieDao")));
		}

	}

	/**
	 * Initialise les listes de spécificités. Date de création : (28/07/11)
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initialiseListeSpecificites() throws Exception {
		// Avantages en nature
		if (getListeAvantage() == null)
			setListeAvantage((ArrayList<AvantageNature>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_AV_NATURE));
		if (getListeAvantage() != null && getListeAvantage().size() != 0) {
			int tailles[] = { 52, 10, 52 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : getNatureAvantageDao()
							.chercherNatureAvantage(aAvNat.getIdNatureAvantage());
					String ligne[] = { typAv.getLibTypeAvantage(), aAvNat.getMontant().toString(),
							natAv == null ? Const.CHAINE_VIDE : natAv.getLibNatureAvantage() };
					aFormat.ajouteLigne(ligne);
				}
			}
			setLB_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_AVANTAGE(null);
		}

		// délégations
		if (getListeDelegation() == null)
			setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation() != null && getListeDelegation().size() != 0) {
			int taillesDel[] = { 32, 100 };
			FormateListe aFormatDel = new FormateListe(taillesDel);
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());
					String ligne[] = { typDel.libTypeDelegation, aDel.getLibDelegation() };
					aFormatDel.ajouteLigne(ligne);
				}
			}
			setLB_DELEGATION(aFormatDel.getListeFormatee());
		} else {
			setLB_DELEGATION(null);
		}

		// Régimes indemnitaires
		if (getListeRegime() == null)
			setListeRegime((ArrayList<RegimeIndemnitaire>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_REG_INDEMN));
		if (getListeRegime() != null && getListeRegime().size() != 0) {
			int taillesReg[] = { 22, 10, 10 };
			FormateListe aFormatReg = new FormateListe(taillesReg);
			for (ListIterator<RegimeIndemnitaire> list = getListeRegime().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());
					String ligne[] = { typReg.getLibTypeRegIndemn(), aReg.getForfait().toString(),
							aReg.getNombrePoints().toString() };
					aFormatReg.ajouteLigne(ligne);
				}
			}
			setLB_REGIME(aFormatReg.getListeFormatee());
		} else {
			setLB_REGIME(null);
		}

		// Primes pointage

		SirhPtgWSConsumer t = new SirhPtgWSConsumer();

		if (getListePrimePointageFP() == null || getListePrimePointageFP().size() == 0)
			setListePrimePointageFP((ArrayList<PrimePointageFP>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE));
		if (getListePrimePointageFP() != null && getListePrimePointageFP().size() > 0) {
			int taillesReg[] = { 10, 50 };
			FormateListe aFormatReg = new FormateListe(taillesReg);
			for (ListIterator<PrimePointageFP> list = getListePrimePointageFP().listIterator(); list.hasNext();) {
				PrimePointageFP aReg = (PrimePointageFP) list.next();
				RefPrimeDto rubr = t.getPrimeDetail(aReg.getNumRubrique());
				if (aReg != null) {
					String ligne[] = { rubr.getNumRubrique().toString(), rubr.getLibelle() };
					aFormatReg.ajouteLigne(ligne);
				}
			}
			setLB_PRIME_POINTAGE(aFormatReg.getListeFormatee());
		} else {
			setLB_PRIME_POINTAGE(null);
		}
	}

	/**
	 * #3264 Initialisation de la liste deroulantes des primes.
	 */
	private List<RefPrimeDto> initialiseListeDeroulantePrimes() {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		// Si liste etat vide alors affectation
		List<RefPrimeDto> primes = new ArrayList<RefPrimeDto>();
		try {
			primes = t.getPrimes();
		} catch (Exception e) {
			// TODO a supprimer quand les pointages seront en prod
		}
		return primes;
	}

	/**
	 * fin CLV #3264
	 */

	/**
	 * Initialise les listes deroulantes de l'écran. Date de création :
	 * (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste type avantage vide alors affectation
		if (getLB_TYPE_AVANTAGE() == LBVide) {
			ArrayList<TypeAvantage> typeAvantage = getTypeAvantageDao().listerTypeAvantage();
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "Liste des type avantage non trouvée");
			}
			setListeTypeAvantage(typeAvantage);
			if (getListeTypeAvantage().size() != 0) {
				int tailles[] = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeAvantage> list = getListeTypeAvantage().listIterator(); list.hasNext();) {
					TypeAvantage na = (TypeAvantage) list.next();
					String ligne[] = { na.getLibTypeAvantage() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_AVANTAGE(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_AVANTAGE(null);
			}
		}

		// Si liste nature avantage vide alors affectation
		if (getLB_NATURE_AVANTAGE() == LBVide) {
			ArrayList<NatureAvantage> natureAvantage = getNatureAvantageDao().listerNatureAvantage();
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "Liste des nature avantage non trouvée");
			}
			NatureAvantage natAvVide = new NatureAvantage();
			natureAvantage.add(0, natAvVide);
			setListeNatureAvantage(natureAvantage);
			if (getListeNatureAvantage().size() != 0) {
				int tailles[] = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<NatureAvantage> list = getListeNatureAvantage().listIterator(); list.hasNext();) {
					NatureAvantage na = (NatureAvantage) list.next();
					String ligne[] = { na.getLibNatureAvantage() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_NATURE_AVANTAGE(aFormat.getListeFormatee());
			} else {
				setLB_NATURE_AVANTAGE(null);
			}
		}

		// Si liste rubrique vide alors affectation
		if (getLB_RUBRIQUE_AVANTAGE() == LBVide || getLB_RUBRIQUE_REGIME() == LBVide) {
			ArrayList<Rubrique> rubrique = getRubriqueDao().listerRubrique7000();
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des rubriques non trouvée");
			}
			setListeRubrique(rubrique);

			if (getListeRubrique() != null && getListeRubrique().size() != 0) {
				int taillesRub[] = { 68 };
				FormateListe aFormatRub = new FormateListe(taillesRub);
				for (ListIterator<Rubrique> list = getListeRubrique().listIterator(); list.hasNext();) {
					Rubrique aRub = (Rubrique) list.next();
					if (aRub != null) {
						String ligne[] = { aRub.getNorubr() + " - " + aRub.getLirubr() };
						aFormatRub.ajouteLigne(ligne);
					}
				}
				setLB_RUBRIQUE_AVANTAGE(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_REGIME(aFormatRub.getListeFormatee(true));
			} else {
				setLB_RUBRIQUE_AVANTAGE(null);
				setLB_RUBRIQUE_REGIME(null);
			}
		}

		/***
		 * CLV #3264
		 */
		if (getLB_RUBRIQUE_PRIME_POINTAGE() == LBVide) {
			setListePrimes(initialiseListeDeroulantePrimes());
			if (getListePrimes() != null) {
				String[] content = new String[getListePrimes().size()];
				for (int i = 0; i < getListePrimes().size(); i++) {
					content[i] = getListePrimes().get(i).getNumRubrique() + " - "
							+ getListePrimes().get(i).getLibelle();
				}
				setLB_RUBRIQUE_PRIME_POINTAGE(content);
			}
			/***
			 * fin CLV #3264
			 */
		}

		// Si liste type délégation vide alors affectation
		if (getLB_TYPE_DELEGATION() == LBVide) {
			ArrayList<TypeDelegation> typeDelegation = getTypeDelegationDao().listerTypeDelegation();
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "Liste des types délégation non trouvée");
			}
			setListeTypeDelegation(typeDelegation);
			if (getListeTypeDelegation().size() != 0) {
				int tailles[] = { 30 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeDelegation> list = getListeTypeDelegation().listIterator(); list.hasNext();) {
					TypeDelegation na = (TypeDelegation) list.next();
					String ligne[] = { na.getLibTypeDelegation() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_DELEGATION(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_DELEGATION(null);
			}
		}

		// Si liste type régime vide alors affectation
		if (getLB_TYPE_REGIME() == LBVide) {
			ArrayList<TypeRegIndemn> typeRegime = getTypeRegIndemnDao().listerTypeRegIndemn();
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "Liste des types de régimes indemnitaires non trouvée");
			}
			setListeTypeRegIndemn(typeRegime);
			if (getListeTypeRegIndemn().size() != 0) {
				int tailles[] = { 20 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeRegIndemn> list = getListeTypeRegIndemn().listIterator(); list.hasNext();) {
					TypeRegIndemn na = (TypeRegIndemn) list.next();
					String ligne[] = { na.getLibTypeRegIndemn() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_REGIME(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_REGIME(null);
			}
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (27/07/11 14:55:25)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (27/07/11 14:55:25)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIFICITE Date de
	 * création : (28/07/11 12:09:49)
	 * 
	 */
	public String getNOM_ST_SPECIFICITE() {
		return "NOM_ST_SPECIFICITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SPECIFICITE
	 * Date de création : (28/07/11 12:09:49)
	 * 
	 */
	public String getVAL_ST_SPECIFICITE() {
		return getZone(getNOM_ST_SPECIFICITE());
	}

	/**
	 * Retourne la liste des types d'avantage en nature.
	 * 
	 * @return listeTypeAvantage
	 */
	private ArrayList<TypeAvantage> getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	/**
	 * Met a jour la liste des types d'avantage en nature.
	 * 
	 * @param listeTypeAvantage
	 */
	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	/**
	 * Retourne la liste des avantages en nature a ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	private ArrayList<AvantageNature> getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList<AvantageNature>();
		return listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des avantages en nature a supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList<AvantageNature> getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList<AvantageNature>();
		return listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des délégations a ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	private ArrayList<Delegation> getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList<Delegation>();
		return listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des délégations a supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList<Delegation> getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList<Delegation>();
		return listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a ajouter.
	 * 
	 * @return listePrimePointageAAjouter
	 */
	private ArrayList<PrimePointageFP> getListePrimePointageFPAAjouter() {
		if (listePrimePointageFPAAjouter == null)
			listePrimePointageFPAAjouter = new ArrayList<>();
		return listePrimePointageFPAAjouter;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a supprimer.
	 * 
	 * @return listePrimePointageASupprimer
	 */
	private ArrayList<PrimePointageFP> getListePrimePointageFPASupprimer() {
		if (listePrimePointageFPASupprimer == null)
			listePrimePointageFPASupprimer = new ArrayList<>();
		return listePrimePointageFPASupprimer;
	}

	/**
	 * Retourne la liste des natures d'avantage en nature.
	 * 
	 * @return listeNatureAvantage
	 */
	private ArrayList<NatureAvantage> getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	/**
	 * Retourne la liste des rubriques.
	 * 
	 * @return listeRubrique
	 */
	private ArrayList<Rubrique> getListeRubrique() {
		return listeRubrique;
	}

	/**
	 * Met a jour la liste des natures d'avantage en nature.
	 * 
	 * @param listeNatureAvantage
	 */
	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	/**
	 * Met a jour la liste des rubriques.
	 * 
	 * @param listeRubrique
	 */
	private void setListeRubrique(ArrayList<Rubrique> listeRubrique) {
		this.listeRubrique = listeRubrique;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * création : (29/07/11 11:47:48)
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT() {
		return "NOM_PB_VALIDER_AJOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/07/11 11:47:48)
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT(HttpServletRequest request) throws Exception {
		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE)) {
			// Controle des champs
			if (!performControlerSaisieAvNat(request))
				return false;

			// Alimentation de l'objet
			AvantageNature avNat = new AvantageNature();

			avNat.setMontant(Double.valueOf(getVAL_EF_MONTANT_AVANTAGE()));

			int indiceTypeAvantage = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT()) : -1);
			avNat.setIdTypeAvantage(((TypeAvantage) getListeTypeAvantage().get(indiceTypeAvantage)).getIdTypeAvantage());
			int indiceNatAvantage = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()) : -1);
			avNat.setIdNatureAvantage(((NatureAvantage) getListeNatureAvantage().get(indiceNatAvantage))
					.getIdNatureAvantage());
			int indiceRubAvantage = (Services.estNumerique(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) : -1);
			avNat.setNumRubrique(indiceRubAvantage <= 0 ? null : ((Rubrique) getListeRubrique().get(
					indiceRubAvantage - 1)).getNorubr());

			if (getListeAvantage() == null)
				setListeAvantage(new ArrayList<AvantageNature>());

			if (!getListeAvantage().contains(avNat)) {
				getListeAvantage().add(avNat);
				if (getListeAvantageASupprimer().contains(avNat)) {
					getListeAvantageASupprimer().remove(avNat);
				} else {
					getListeAvantageAAjouter().add(avNat);
				}
			}
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_DELEGATION)) {
			// Controle des champs
			if (!performControlerSaisieDel(request))
				return false;

			// Alimentation de l'objet
			Delegation deleg = new Delegation();

			deleg.setLibDelegation(getVAL_EF_COMMENT_DELEGATION());

			int indiceTypeDelegation = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer
					.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);
			deleg.setIdTypeDelegation(((TypeDelegation) getListeTypeDelegation().get(indiceTypeDelegation))
					.getIdTypeDelegation());

			if (getListeDelegation() == null)
				setListeDelegation(new ArrayList<Delegation>());

			if (!getListeDelegation().contains(deleg)) {
				getListeDelegation().add(deleg);
				if (getListeDelegationASupprimer().contains(deleg)) {
					getListeDelegationASupprimer().remove(deleg);
				} else {
					getListeDelegationAAjouter().add(deleg);
				}
			}
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_REG_INDEMN)) {
			// Controle des champs
			if (!performControlerSaisieRegIndemn(request))
				return false;

			// Alimentation de l'objet
			RegimeIndemnitaire regIndemn = new RegimeIndemnitaire();

			regIndemn.setForfait(Double.valueOf(getVAL_EF_FORFAIT_REGIME()));
			regIndemn.setNombrePoints(Integer.valueOf(getVAL_EF_NB_POINTS_REGIME()));

			int indiceRegIndemn = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer
					.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);
			regIndemn.setIdTypeRegIndemn(((TypeRegIndemn) getListeTypeRegIndemn().get(indiceRegIndemn))
					.getIdTypeRegIndemn());
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_REGIME_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_REGIME_SELECT()) : -1);
			regIndemn.setNumRubrique(indiceRub <= 0 ? null : getListeRubrique().get(indiceRub - 1).getNorubr());

			if (getListeRegime() == null)
				setListeRegime(new ArrayList<RegimeIndemnitaire>());

			if (!getListeRegime().contains(regIndemn)) {
				getListeRegime().add(regIndemn);
				if (getListeRegimeASupprimer().contains(regIndemn)) {
					getListeRegimeASupprimer().remove(regIndemn);
				} else {
					getListeRegimeAAjouter().add(regIndemn);
				}
			}
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_PRIME_POINTAGE)) {
			// Controle des champs
			if (!performControlerSaisiePrimePointage(request))
				return false;

			// Alimentation de l'objet
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
			if (indiceRub != -1) {
				Integer numRubAjout = getListePrimes().get(indiceRub).getNumRubrique();

				// Alimentation de l'objet
				if (!getListeRubs().contains(numRubAjout)) {
					PrimePointageFP prime = new PrimePointageFP();
					prime.setNumRubrique(numRubAjout);

					if (getListePrimePointageFP() == null)
						setListePrimePointageFP(new ArrayList<PrimePointageFP>());

					if (getListePrimePointageFPASupprimer().contains(prime)) {
						getListePrimePointageFPASupprimer().remove(prime);
						getListePrimePointageFP().remove(prime);
					} else {
						getListePrimePointageFPAAjouter().add(prime);
						getListePrimePointageFP().add(prime);
					}
				} else {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR088"));
					return false;
				}
			} else {
				return false;
			}

		}
		return true;
	}

	public ArrayList<Integer> getListeRubs() {
		ArrayList<Integer> ret = new ArrayList<>();

		if (getListePrimePointageFP() != null) {
			for (PrimePointageFP p : getListePrimePointageFP()) {
				ret.add(p.getNumRubrique());
			}
		}
		if (getListePrimePointageFPAAjouter() != null) {
			for (PrimePointageFP p : getListePrimePointageFPAAjouter()) {
				ret.add(p.getNumRubrique());

			}
		}
		return ret;
	}

	private boolean performControlerSaisiePrimePointage(HttpServletRequest request) {
		// rubrique obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "rubrique"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne la liste des TypeDelegation.
	 * 
	 * @return listeTypeDelegation
	 */
	private ArrayList<TypeDelegation> getListeTypeDelegation() {
		return listeTypeDelegation;
	}

	/**
	 * Met a jour la liste des TypeDelegation.
	 * 
	 * @param listeTypeDelegation
	 */
	private void setListeTypeDelegation(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire que l'on peut ajouter.
	 * 
	 * @return listePrimePointage
	 */
	public List<RefPrimeDto> getListePrimes() {
		return listePrimes;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire que l'on peut ajouter.
	 * 
	 * @param listePrimes
	 */
	public void setListePrimes(List<RefPrimeDto> listePrimes) {
		this.listePrimes = listePrimes;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire.
	 * 
	 * @return listePrimePointageFP
	 */

	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		return listePrimePointageFP;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire.
	 * 
	 * @param listePrimePointageFP
	 */
	public void setListePrimePointageFP(ArrayList<PrimePointageFP> listePrimePointageFP) {
		this.listePrimePointageFP = listePrimePointageFP;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeTypeRegIndemn
	 */
	private ArrayList<TypeRegIndemn> getListeTypeRegIndemn() {
		return listeTypeRegIndemn;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeTypeRegIndemn
	 */
	private void setListeTypeRegIndemn(ArrayList<TypeRegIndemn> listeTypeRegIndemn) {
		this.listeTypeRegIndemn = listeTypeRegIndemn;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_SPECIFICITE Date de création : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RG_SPECIFICITE() {
		return "NOM_RG_SPECIFICITE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_SPECIFICITE Date de création : (02/08/11 08:59:59)
	 * 
	 */
	public String getVAL_RG_SPECIFICITE() {
		return getZone(getNOM_RG_SPECIFICITE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_AN Date de
	 * création : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_AN() {
		return "NOM_RB_SPECIFICITE_AN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_D Date de
	 * création : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_D() {
		return "NOM_RB_SPECIFICITE_D";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_RI Date de
	 * création : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_RI() {
		return "NOM_RB_SPECIFICITE_RI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_PP Date de
	 * création : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_PP() {
		return "NOM_RB_SPECIFICITE_PP";
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (27/07/11 12:13:47)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CHANGER_SPECIFICITE
			if (testerParametre(request, getNOM_PB_CHANGER_SPECIFICITE())) {
				return performPB_CHANGER_SPECIFICITE(request);
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT
			if (testerParametre(request, getNOM_PB_VALIDER_AJOUT())) {
				return performPB_VALIDER_AJOUT(request);
			}

			// Si clic sur le bouton PB_AJOUTER_AVANTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_AVANTAGE())) {
				return performPB_AJOUTER_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DELEGATION
			if (testerParametre(request, getNOM_PB_AJOUTER_DELEGATION())) {
				return performPB_AJOUTER_DELEGATION(request);
			}

			// Si clic sur le bouton PB_AJOUTER_REGIME
			if (testerParametre(request, getNOM_PB_AJOUTER_REGIME())) {
				return performPB_AJOUTER_REGIME(request);
			}

			// Si clic sur le bouton PB_AJOUTER_PRIME_POINTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_PRIME_POINTAGE())) {
				return performPB_AJOUTER_PRIME_POINTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AVANTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_AVANTAGE())) {
				return performPB_SUPPRIMER_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DELEGATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATION())) {
				return performPB_SUPPRIMER_DELEGATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REGIME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REGIME())) {
				return performPB_SUPPRIMER_REGIME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_PRIME_POINTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_PRIME_POINTAGE())) {
				return performPB_SUPPRIMER_PRIME_POINTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFPSpecificites. Date de création :
	 * (02/08/11 09:18:22)
	 * 
	 */
	public OePOSTEFPSpecificites() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (02/08/11 09:18:23)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFPSpecificites.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_SPECIFICITE Date de
	 * création : (02/08/11 09:18:23)
	 * 
	 */
	public String getNOM_PB_CHANGER_SPECIFICITE() {
		return "NOM_PB_CHANGER_SPECIFICITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:18:23)
	 * 
	 */
	public boolean performPB_CHANGER_SPECIFICITE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public NatureAvantageDao getNatureAvantageDao() {
		return natureAvantageDao;
	}

	public void setNatureAvantageDao(NatureAvantageDao natureAvantageDao) {
		this.natureAvantageDao = natureAvantageDao;
	}

	public TypeAvantageDao getTypeAvantageDao() {
		return typeAvantageDao;
	}

	public void setTypeAvantageDao(TypeAvantageDao typeAvantageDao) {
		this.typeAvantageDao = typeAvantageDao;
	}

	public TypeDelegationDao getTypeDelegationDao() {
		return typeDelegationDao;
	}

	public void setTypeDelegationDao(TypeDelegationDao typeDelegationDao) {
		this.typeDelegationDao = typeDelegationDao;
	}

	public TypeRegIndemnDao getTypeRegIndemnDao() {
		return typeRegIndemnDao;
	}

	public void setTypeRegIndemnDao(TypeRegIndemnDao typeRegIndemnDao) {
		this.typeRegIndemnDao = typeRegIndemnDao;
	}

	public RubriqueDao getRubriqueDao() {
		return rubriqueDao;
	}

	public void setRubriqueDao(RubriqueDao rubriqueDao) {
		this.rubriqueDao = rubriqueDao;
	}
}
