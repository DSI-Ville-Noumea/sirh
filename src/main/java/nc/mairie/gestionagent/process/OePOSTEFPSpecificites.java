package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.domain.metier.specificites.PrimePointage;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEFPSpecificites Date de cr�ation : (27/07/11 12:13:47)
 * 
 */
public class OePOSTEFPSpecificites extends nc.mairie.technique.BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String ACTION_AJOUTER = "Ajouter";
	public final String ACTION_SUPPRIMER = "Supprimer";
	public final String SPEC_AVANTAGE_NATURE = "avantage en nature";
	public final String SPEC_DELEGATION = "d�l�gation";
	public final String SPEC_REG_INDEMN = "r�gime indemnitaire";
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
	private ArrayList<PrimePointage> listePrimePointage;
	private ArrayList<PrimePointage> listePrimePointageAAjouter;
	private ArrayList<PrimePointage> listePrimePointageASupprimer;

	private ArrayList<TypeAvantage> listeTypeAvantage;
	private ArrayList<NatureAvantage> listeNatureAvantage;
	private ArrayList<Rubrique> listeRubrique;
	private ArrayList<TypeDelegation> listeTypeDelegation;
	private ArrayList<TypeRegIndemn> listeTypeRegIndemn;

	public String focus = null;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		if (getListeAvantage() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE, getListeAvantage());
		if (getListeAvantageAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT, getListeAvantageAAjouter());
		if (getListeAvantageASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR, getListeAvantageASupprimer());

		if (getListeDelegation() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION, getListeDelegation());
		if (getListeDelegationAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT, getListeDelegationAAjouter());
		if (getListeDelegationASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR, getListeDelegationASupprimer());

		if (getListeRegime() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN, getListeRegime());
		if (getListeRegimeAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT, getListeRegimeAAjouter());
		if (getListeRegimeASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR, getListeRegimeASupprimer());

		if (getListePrimePointage() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE, getListePrimePointage());
		if (getListePrimePointageAAjouter() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_AJOUT, getListePrimePointageAAjouter());
		if (getListePrimePointageASupprimer() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_SUPPR, getListePrimePointageASupprimer());

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Contr�le les zones saisies d'un avantage en nature. Date de cr�ation :
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
		// Verification Montant OU Nature renseign�
		// ****************************************
		if (getVAL_EF_MONTANT_AVANTAGE().length() == 0
				&& ((NatureAvantage) getListeNatureAvantage().get(Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()))).getIdNatureAvantage() == null) {
			// "ERR979","Au moins une des 2 zones suivantes doit �tre renseign�e : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Nature avantage", "Montant"));
			setFocus(getNOM_LB_NATURE_AVANTAGE());
			return false;
		}

		// ********************
		// Verification Montant
		// ********************
		if (getVAL_EF_MONTANT_AVANTAGE().length() != 0 && !Services.estNumerique(getVAL_EF_MONTANT_AVANTAGE())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Montant"));
			setFocus(getNOM_EF_MONTANT_AVANTAGE());
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'une d�l�gation. Date de cr�ation :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieDel(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Contr�le les zones saisies d'un r�gime indemnitaire. Date de cr�ation :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieRegIndemn(HttpServletRequest request) throws Exception {

		// *******************************************
		// Verification Forfait OU Nb points renseign�
		// *******************************************
		if (getVAL_EF_FORFAIT_REGIME().length() == 0 && getVAL_EF_NB_POINTS_REGIME().length() == 0) {
			// "ERR979","Au moins une des 2 zones suivantes doit �tre renseign�e : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Forfait", "Nb points"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// ********************
		// Verification Forfait
		// ********************
		if (getVAL_EF_FORFAIT_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_FORFAIT_REGIME())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Forfait"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// **********************
		// Verification Nb points
		// **********************
		if (getVAL_EF_NB_POINTS_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_NB_POINTS_REGIME())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Nb points"));
			setFocus(getNOM_EF_NB_POINTS_REGIME());
			return false;
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_CODE_RUBRIQUE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_ST_CODE_RUBRIQUE_AVANTAGE() {
		return "NOM_ST_CODE_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_CODE_RUBRIQUE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_ST_CODE_RUBRIQUE_AVANTAGE() {
		return getZone(getNOM_ST_CODE_RUBRIQUE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_RUBRIQUE_REGIME
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_ST_CODE_RUBRIQUE_REGIME() {
		return "NOM_ST_CODE_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_CODE_RUBRIQUE_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_ST_CODE_RUBRIQUE_REGIME() {
		return getZone(getNOM_ST_CODE_RUBRIQUE_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENT_DELEGATION
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_COMMENT_DELEGATION() {
		return "NOM_EF_COMMENT_DELEGATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_COMMENT_DELEGATION Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_COMMENT_DELEGATION() {
		return getZone(getNOM_EF_COMMENT_DELEGATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FORFAIT_REGIME Date
	 * de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_FORFAIT_REGIME() {
		return "NOM_EF_FORFAIT_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_FORFAIT_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_FORFAIT_REGIME() {
		return getZone(getNOM_EF_FORFAIT_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_AVANTAGE
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_MONTANT_AVANTAGE() {
		return "NOM_EF_MONTANT_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_MONTANT_AVANTAGE() {
		return getZone(getNOM_EF_MONTANT_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_POINTS_REGIME
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_EF_NB_POINTS_REGIME() {
		return "NOM_EF_NB_POINTS_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NB_POINTS_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_EF_NB_POINTS_REGIME() {
		return getZone(getNOM_EF_NB_POINTS_REGIME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_AVANTAGE() {
		if (LB_RUBRIQUE_AVANTAGE == null)
			LB_RUBRIQUE_AVANTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_AVANTAGE Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_AVANTAGE(String[] newLB_RUBRIQUE_AVANTAGE) {
		LB_RUBRIQUE_AVANTAGE = newLB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE() {
		return "NOM_LB_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_RUBRIQUE_AVANTAGE_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_AVANTAGE() {
		return getLB_RUBRIQUE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_REGIME Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_REGIME() {
		if (LB_RUBRIQUE_REGIME == null)
			LB_RUBRIQUE_REGIME = initialiseLazyLB();
		return LB_RUBRIQUE_REGIME;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_REGIME Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_REGIME(String[] newLB_RUBRIQUE_REGIME) {
		LB_RUBRIQUE_REGIME = newLB_RUBRIQUE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_REGIME Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME() {
		return "NOM_LB_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_RUBRIQUE_REGIME_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME_SELECT() {
		return "NOM_LB_RUBRIQUE_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_REGIME() {
		return getLB_RUBRIQUE_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_REGIME_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_REGIME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_RUBRIQUE_PRIME_POINTAGE() {
		if (LB_RUBRIQUE_PRIME_POINTAGE == null)
			LB_RUBRIQUE_PRIME_POINTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_PRIME_POINTAGE Date de cr�ation :
	 * (27/07/11 12:13:47)
	 * 
	 */
	private void setLB_RUBRIQUE_PRIME_POINTAGE(String[] newLB_RUBRIQUE_PRIME_POINTAGE) {
		LB_RUBRIQUE_PRIME_POINTAGE = newLB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_PRIME_POINTAGE() {
		return getLB_RUBRIQUE_PRIME_POINTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de cr�ation : (27/07/11
	 * 12:13:47)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de cr�ation : (27/07/11 12:13:47)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de cr�ation : (27/07/11 12:13:47)
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
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AVANTAGE Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AVANTAGE() {
		return "NOM_PB_AJOUTER_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_AVANTAGE_NATURE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DELEGATION Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DELEGATION() {
		return "NOM_PB_AJOUTER_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_DELEGATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REGIME Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_REGIME() {
		return "NOM_PB_AJOUTER_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_REGIME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_PRIME_POINTAGE Date
	 * de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_PRIME_POINTAGE() {
		return "NOM_PB_AJOUTER_PRIME_POINTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_AJOUTER_PRIME_POINTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_PRIME_POINTAGE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AVANTAGE Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AVANTAGE(HttpServletRequest request) throws Exception {
		// R�cup�ration de l'avantage � supprimer
		int indiceAvNat = (Services.estNumerique(getVAL_LB_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_AVANTAGE_SELECT()) : -1);
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
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELEGATION() {
		return "NOM_PB_SUPPRIMER_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELEGATION(HttpServletRequest request) throws Exception {
		// R�cup�ration de la d�l�gation � supprimer
		int indiceDel = (Services.estNumerique(getVAL_LB_DELEGATION_SELECT()) ? Integer.parseInt(getVAL_LB_DELEGATION_SELECT()) : -1);
		if (indiceDel == -1 || getListeDelegation().size() == 0 || indiceDel > getListeDelegation().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "D�l�gations"));
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
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REGIME() {
		return "NOM_PB_SUPPRIMER_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REGIME(HttpServletRequest request) throws Exception {
		// R�cup�ration du r�gime indemnitaire � supprimer
		int indiceRI = (Services.estNumerique(getVAL_LB_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_REGIME_SELECT()) : -1);
		if (indiceRI == -1 || getListeRegime().size() == 0 || indiceRI > getListeRegime().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "R�gimes indemnitaires"));
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
	 * Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_PRIME_POINTAGE() {
		return "NOM_PB_SUPPRIMER_PRIME_POINTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_PRIME_POINTAGE(HttpServletRequest request) throws Exception {
		// R�cup�ration de la prime pointage � supprimer
		int indicePrime = (Services.estNumerique(getVAL_LB_PRIME_POINTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_PRIME_POINTAGE_SELECT()) : -1);
		if (indicePrime == -1 || getListePrimePointage().size() == 0 || indicePrime > getListePrimePointage().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Primes"));
			return false;
		}
		PrimePointage prime = (PrimePointage) getListePrimePointage().get(indicePrime);

		if (prime != null) {
			if (getListePrimePointage() != null) {
				getListePrimePointage().remove(prime);
				if (getListePrimePointageAAjouter().contains(prime)) {
					getListePrimePointageAAjouter().remove(prime);
				} else {
					getListePrimePointageASupprimer().add(prime);
				}
			}
		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVANTAGE Date de cr�ation
	 * : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_AVANTAGE() {
		if (LB_AVANTAGE == null)
			LB_AVANTAGE = initialiseLazyLB();
		return LB_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_AVANTAGE Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_AVANTAGE(String[] newLB_AVANTAGE) {
		LB_AVANTAGE = newLB_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVANTAGE Date de cr�ation
	 * : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_AVANTAGE() {
		return "NOM_LB_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_AVANTAGE_SELECT Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_AVANTAGE_SELECT() {
		return "NOM_LB_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_AVANTAGE Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_AVANTAGE() {
		return getLB_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_AVANTAGE Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DELEGATION Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_DELEGATION() {
		if (LB_DELEGATION == null)
			LB_DELEGATION = initialiseLazyLB();
		return LB_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_DELEGATION Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_DELEGATION(String[] newLB_DELEGATION) {
		LB_DELEGATION = newLB_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DELEGATION Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_DELEGATION() {
		return "NOM_LB_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_DELEGATION_SELECT Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_DELEGATION_SELECT() {
		return "NOM_LB_DELEGATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_DELEGATION Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_DELEGATION() {
		return getLB_DELEGATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_DELEGATION Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_DELEGATION_SELECT() {
		return getZone(getNOM_LB_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_PRIME_POINTAGE Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_PRIME_POINTAGE() {
		if (LB_PRIME_POINTAGE == null)
			LB_PRIME_POINTAGE = initialiseLazyLB();
		return LB_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_PRIME_POINTAGE Date de cr�ation : (27/07/11
	 * 14:09:02)
	 * 
	 */
	private void setLB_PRIME_POINTAGE(String[] newLB_PRIME_POINTAGE) {
		LB_PRIME_POINTAGE = newLB_PRIME_POINTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_PRIME_POINTAGE Date de
	 * cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_PRIME_POINTAGE() {
		return "NOM_LB_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_PRIME_POINTAGE_SELECT Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_PRIME_POINTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_PRIME_POINTAGE Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_PRIME_POINTAGE() {
		return getLB_PRIME_POINTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_PRIME_POINTAGE Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getVAL_LB_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REGIME Date de cr�ation :
	 * (27/07/11 14:09:02)
	 * 
	 */
	private String[] getLB_REGIME() {
		if (LB_REGIME == null)
			LB_REGIME = initialiseLazyLB();
		return LB_REGIME;
	}

	/**
	 * Setter de la liste: LB_REGIME Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	private void setLB_REGIME(String[] newLB_REGIME) {
		LB_REGIME = newLB_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REGIME Date de cr�ation :
	 * (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_REGIME() {
		return "NOM_LB_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_REGIME_SELECT Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String getNOM_LB_REGIME_SELECT() {
		return "NOM_LB_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_REGIME Date de cr�ation : (27/07/11 14:09:02)
	 * 
	 */
	public String[] getVAL_LB_REGIME() {
		return getLB_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_REGIME Date de cr�ation : (27/07/11 14:09:02)
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
	 * Met � jour la liste des AvantageNature.
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
	 * Met � jour la liste des Delegation.
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
	 * Met � jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeRegime
	 */
	private void setListeRegime(ArrayList<RegimeIndemnitaire> listeRegime) {
		this.listeRegime = listeRegime;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	private ArrayList<PrimePointage> getListePrimePointage() {
		return listePrimePointage;
	}

	/**
	 * Met � jour la liste des PrimePointageIndemnitaire.
	 * 
	 * @param listePrimePointage
	 */
	private void setListePrimePointage(ArrayList<PrimePointage> listePrimePointage) {
		this.listePrimePointage = listePrimePointage;
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (27/07/11 14:55:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseListeDeroulante();
		initialiseListeSpecificites();
		if (getVAL_RG_SPECIFICITE() == null || getVAL_RG_SPECIFICITE().length() == 0)
			addZone(getNOM_RG_SPECIFICITE(), getNOM_RB_SPECIFICITE_AN());
	}

	/**
	 * Initialise les listes de sp�cificit�s. Date de cr�ation : (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeSpecificites() throws Exception {
		// Avantages en nature
		if (getListeAvantage() == null)
			setListeAvantage((ArrayList<AvantageNature>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_AV_NATURE));
		if (getListeAvantage() != null && getListeAvantage().size() != 0) {
			int tailles[] = { 52, 10, 52 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage.chercherNatureAvantage(getTransaction(),
							aAvNat.getIdNatureAvantage());
					String ligne[] = { typAv.libTypeAvantage, aAvNat.getMontant(), natAv == null ? Const.CHAINE_VIDE : natAv.getLibNatureAvantage() };
					aFormat.ajouteLigne(ligne);
				}
			}
			setLB_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_AVANTAGE(null);
		}

		// D�l�gations
		if (getListeDelegation() == null)
			setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation() != null && getListeDelegation().size() != 0) {
			int taillesDel[] = { 32, 100 };
			FormateListe aFormatDel = new FormateListe(taillesDel);
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());
					String ligne[] = { typDel.libTypeDelegation, aDel.getLibDelegation() };
					aFormatDel.ajouteLigne(ligne);
				}
			}
			setLB_DELEGATION(aFormatDel.getListeFormatee());
		} else {
			setLB_DELEGATION(null);
		}

		// R�gimes indemnitaires
		if (getListeRegime() == null)
			setListeRegime((ArrayList<RegimeIndemnitaire>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN));
		if (getListeRegime() != null && getListeRegime().size() != 0) {
			int taillesReg[] = { 22, 10, 10 };
			FormateListe aFormatReg = new FormateListe(taillesReg);
			for (ListIterator<RegimeIndemnitaire> list = getListeRegime().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());
					String ligne[] = { typReg.libTypeRegIndemn, aReg.getForfait(), aReg.getNombrePoints() };
					aFormatReg.ajouteLigne(ligne);
				}
			}
			setLB_REGIME(aFormatReg.getListeFormatee());
		} else {
			setLB_REGIME(null);
		}

		// Primes pointage
		if (getListePrimePointage() == null)
			setListePrimePointage((ArrayList<PrimePointage>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE));
		if (getListePrimePointage() != null && getListePrimePointage().size() != 0) {
			int taillesReg[] = { 10, 50 };
			FormateListe aFormatReg = new FormateListe(taillesReg);
			for (ListIterator<PrimePointage> list = getListePrimePointage().listIterator(); list.hasNext();) {
				PrimePointage aReg = (PrimePointage) list.next();
				Rubrique rubr = Rubrique.chercherRubrique(getTransaction(), aReg.getIdRubrique().toString());
				if (aReg != null) {
					String ligne[] = { rubr.getNumRubrique(), rubr.getLibRubrique() };
					aFormatReg.ajouteLigne(ligne);
				}
			}
			setLB_PRIME_POINTAGE(aFormatReg.getListeFormatee());
		} else {
			setLB_PRIME_POINTAGE(null);
		}
	}

	/**
	 * Initialise les listes d�roulantes de l'�cran. Date de cr�ation :
	 * (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste type avantage vide alors affectation
		if (getLB_TYPE_AVANTAGE() == LBVide) {
			ArrayList<TypeAvantage> typeAvantage = TypeAvantage.listerTypeAvantage(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des type avantage non trouv�e");
			}
			setListeTypeAvantage(typeAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libTypeAvantage" };
			setLB_TYPE_AVANTAGE(new FormateListe(tailles, typeAvantage, champs).getListeFormatee());
		}

		// Si liste nature avantage vide alors affectation
		if (getLB_NATURE_AVANTAGE() == LBVide) {
			ArrayList<NatureAvantage> natureAvantage = NatureAvantage.listerNatureAvantage(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des nature avantage non trouv�e");
			}
			NatureAvantage natAvVide = new NatureAvantage();
			natureAvantage.add(0, natAvVide);
			setListeNatureAvantage(natureAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libNatureAvantage" };
			setLB_NATURE_AVANTAGE(new FormateListe(tailles, natureAvantage, champs).getListeFormatee());
		}

		// Si liste rubrique vide alors affectation
		if (getLB_RUBRIQUE_AVANTAGE() == LBVide || getLB_RUBRIQUE_REGIME() == LBVide) {
			ArrayList<Rubrique> rubrique = Rubrique.listerRubrique7000(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des rubriques non trouv�e");
			}
			setListeRubrique(rubrique);

			if (getListeRubrique() != null && getListeRubrique().size() != 0) {
				int taillesRub[] = { 68 };
				FormateListe aFormatRub = new FormateListe(taillesRub);
				for (ListIterator<Rubrique> list = getListeRubrique().listIterator(); list.hasNext();) {
					Rubrique aRub = (Rubrique) list.next();
					if (aRub != null) {
						String ligne[] = { aRub.getNumRubrique() + " - " + aRub.getLibRubrique() };
						aFormatRub.ajouteLigne(ligne);
					}
				}
				setLB_RUBRIQUE_AVANTAGE(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_REGIME(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_PRIME_POINTAGE(aFormatRub.getListeFormatee(true));
			} else {
				setLB_RUBRIQUE_AVANTAGE(null);
				setLB_RUBRIQUE_REGIME(null);
				setLB_RUBRIQUE_PRIME_POINTAGE(null);
			}
		}

		// Si liste type d�l�gation vide alors affectation
		if (getLB_TYPE_DELEGATION() == LBVide) {
			ArrayList<TypeDelegation> typeDelegation = TypeDelegation.listerTypeDelegation(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des types d�l�gation non trouv�e");
			}
			setListeTypeDelegation(typeDelegation);

			int[] tailles = { 30 };
			String[] champs = { "libTypeDelegation" };
			setLB_TYPE_DELEGATION(new FormateListe(tailles, typeDelegation, champs).getListeFormatee());
		}

		// Si liste type r�gime vide alors affectation
		if (getLB_TYPE_REGIME() == LBVide) {
			ArrayList<TypeRegIndemn> typeRegime = TypeRegIndemn.listerTypeRegIndemn(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "Liste des types de r�gimes indemnitaires non trouv�e");
			}
			setListeTypeRegIndemn(typeRegime);
			int[] tailles = { 20 };
			String[] champs = { "libTypeRegIndemn" };
			setLB_TYPE_REGIME(new FormateListe(tailles, typeRegime, champs).getListeFormatee());
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (27/07/11 14:55:25)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (27/07/11 14:55:25)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIFICITE Date de
	 * cr�ation : (28/07/11 12:09:49)
	 * 
	 */
	public String getNOM_ST_SPECIFICITE() {
		return "NOM_ST_SPECIFICITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SPECIFICITE
	 * Date de cr�ation : (28/07/11 12:09:49)
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
	 * Met � jour la liste des types d'avantage en nature.
	 * 
	 * @param listeTypeAvantage
	 */
	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	/**
	 * Retourne la liste des avantages en nature � ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	private ArrayList<AvantageNature> getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList<AvantageNature>();
		return listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des avantages en nature � supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList<AvantageNature> getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList<AvantageNature>();
		return listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des d�l�gations � ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	private ArrayList<Delegation> getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList<Delegation>();
		return listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des d�l�gations � supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList<Delegation> getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList<Delegation>();
		return listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � ajouter.
	 * 
	 * @return listePrimePointageAAjouter
	 */
	private ArrayList<PrimePointage> getListePrimePointageAAjouter() {
		if (listePrimePointageAAjouter == null)
			listePrimePointageAAjouter = new ArrayList<PrimePointage>();
		return listePrimePointageAAjouter;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � supprimer.
	 * 
	 * @return listePrimePointageASupprimer
	 */
	private ArrayList<PrimePointage> getListePrimePointageASupprimer() {
		if (listePrimePointageASupprimer == null)
			listePrimePointageASupprimer = new ArrayList<PrimePointage>();
		return listePrimePointageASupprimer;
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
	 * Met � jour la liste des natures d'avantage en nature.
	 * 
	 * @param listeNatureAvantage
	 */
	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	/**
	 * Met � jour la liste des rubriques.
	 * 
	 * @param listeRubrique
	 */
	private void setListeRubrique(ArrayList<Rubrique> listeRubrique) {
		this.listeRubrique = listeRubrique;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * cr�ation : (29/07/11 11:47:48)
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT() {
		return "NOM_PB_VALIDER_AJOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/07/11 11:47:48)
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT(HttpServletRequest request) throws Exception {
		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE)) {
			// Contr�le des champs
			if (!performControlerSaisieAvNat(request))
				return false;

			// Alimentation de l'objet
			AvantageNature avNat = new AvantageNature();

			avNat.setMontant(getVAL_EF_MONTANT_AVANTAGE());

			int indiceTypeAvantage = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT())
					: -1);
			avNat.setIdTypeAvantage(((TypeAvantage) getListeTypeAvantage().get(indiceTypeAvantage)).getIdTypeAvantage());
			int indiceNatAvantage = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT())
					: -1);
			avNat.setIdNatureAvantage(((NatureAvantage) getListeNatureAvantage().get(indiceNatAvantage)).getIdNatureAvantage());
			int indiceRubAvantage = (Services.estNumerique(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) : -1);
			avNat.setNumRubrique(indiceRubAvantage <= 0 ? null : ((Rubrique) getListeRubrique().get(indiceRubAvantage - 1)).getNumRubrique());

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
			// Contr�le des champs
			if (!performControlerSaisieDel(request))
				return false;

			// Alimentation de l'objet
			Delegation deleg = new Delegation();

			deleg.setLibDelegation(getVAL_EF_COMMENT_DELEGATION());

			int indiceTypeDelegation = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer
					.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);
			deleg.setIdTypeDelegation(((TypeDelegation) getListeTypeDelegation().get(indiceTypeDelegation)).getIdTypeDelegation());

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
			// Contr�le des champs
			if (!performControlerSaisieRegIndemn(request))
				return false;

			// Alimentation de l'objet
			RegimeIndemnitaire regIndemn = new RegimeIndemnitaire();

			regIndemn.setForfait(getVAL_EF_FORFAIT_REGIME());
			regIndemn.setNombrePoints(getVAL_EF_NB_POINTS_REGIME());

			int indiceRegIndemn = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);
			regIndemn.setIdTypeRegIndemn(((TypeRegIndemn) getListeTypeRegIndemn().get(indiceRegIndemn)).getIdTypeRegIndemn());
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_REGIME_SELECT()) : -1);
			regIndemn.setNumRubrique(indiceRub <= 0 ? null : ((Rubrique) getListeRubrique().get(indiceRub - 1)).getNumRubrique());

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
			// Contr�le des champs
			if (!performControlerSaisiePrimePointage(request))
				return false;

			// Alimentation de l'objet
			PrimePointage regIndemn = new PrimePointage();
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
			regIndemn.setIdRubrique(indiceRub <= 0 ? null : Integer.valueOf(((Rubrique) getListeRubrique().get(indiceRub - 1)).getNumRubrique()));

			if (getListePrimePointage() == null)
				setListePrimePointage(new ArrayList<PrimePointage>());

			if (!getListePrimePointage().contains(regIndemn)) {
				getListePrimePointage().add(regIndemn);
				if (getListePrimePointageASupprimer().contains(regIndemn)) {
					getListePrimePointageASupprimer().remove(regIndemn);
				} else {
					getListePrimePointageAAjouter().add(regIndemn);
				}
			}
		}
		return true;
	}

	private boolean performControlerSaisiePrimePointage(HttpServletRequest request) {
		// rubrique obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
		if (indiceRubr < 1) {
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
	 * Met � jour la liste des TypeDelegation.
	 * 
	 * @param listeTypeDelegation
	 */
	private void setListeTypeDelegation(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
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
	 * Met � jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeTypeRegIndemn
	 */
	private void setListeTypeRegIndemn(ArrayList<TypeRegIndemn> listeTypeRegIndemn) {
		this.listeTypeRegIndemn = listeTypeRegIndemn;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_SPECIFICITE Date de cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RG_SPECIFICITE() {
		return "NOM_RG_SPECIFICITE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_SPECIFICITE Date de cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getVAL_RG_SPECIFICITE() {
		return getZone(getNOM_RG_SPECIFICITE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_AN Date de
	 * cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_AN() {
		return "NOM_RB_SPECIFICITE_AN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_D Date de
	 * cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_D() {
		return "NOM_RB_SPECIFICITE_D";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_RI Date de
	 * cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_RI() {
		return "NOM_RB_SPECIFICITE_RI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_PP Date de
	 * cr�ation : (02/08/11 08:59:59)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_PP() {
		return "NOM_RB_SPECIFICITE_PP";
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (27/07/11 12:13:47)
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
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFPSpecificites. Date de cr�ation :
	 * (02/08/11 09:18:22)
	 * 
	 */
	public OePOSTEFPSpecificites() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (02/08/11 09:18:23)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFPSpecificites.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_SPECIFICITE Date de
	 * cr�ation : (02/08/11 09:18:23)
	 * 
	 */
	public String getNOM_PB_CHANGER_SPECIFICITE() {
		return "NOM_PB_CHANGER_SPECIFICITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (02/08/11 09:18:23)
	 * 
	 */
	public boolean performPB_CHANGER_SPECIFICITE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}
}
