package nc.mairie.gestionagent.process;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.parametrage.MotifNonRecrutement;
import nc.mairie.metier.parametrage.MotifRecrutement;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Recrutement;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.technique.FormateListe;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTESuiviRecrutement Date de création : (22/07/11 10:17:45)
 * 
 */
public class OePOSTESuiviRecrutement extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHE_FP = 2;
	public static final int STATUT_RECHERCHE_AGENT = 1;

	public String ACTION_SUPPRESSION = "Suppression d'un recrutement.";
	private String ACTION_CREATION = "Création d'un recrutement.";

	private String[] LB_MOTIF_NON_RECRUTEMENT;
	private String[] LB_MOTIF_RECRUTEMENT;
	private ArrayList listeMotifRecrutement;
	private ArrayList listeMotifNonRecrutement;

	private Recrutement recrutementCourant;
	public String focus = null;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP() {
		return "NOM_PB_RECHERCHER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public boolean performPB_RECHERCHER_FP(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHE_FP, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_DIRECTION() {
		return "NOM_ST_DIRECTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_GRADE() {
		return "NOM_ST_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_GRADE() {
		return getZone(getNOM_ST_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_AGENT_REMP Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_NOM_AGENT_REMP() {
		return "NOM_ST_NOM_AGENT_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_AGENT_REMP
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_NOM_AGENT_REMP() {
		return getZone(getNOM_ST_NOM_AGENT_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_MATR_AGENT_REMP
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_NUM_MATR_AGENT_REMP() {
		return "NOM_ST_NUM_MATR_AGENT_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_MATR_AGENT_REMP Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_NUM_MATR_AGENT_REMP() {
		return getZone(getNOM_ST_NUM_MATR_AGENT_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRENOM_AGENT_REMP
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_PRENOM_AGENT_REMP() {
		return "NOM_ST_PRENOM_AGENT_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_PRENOM_AGENT_REMP Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_PRENOM_AGENT_REMP() {
		return getZone(getNOM_ST_PRENOM_AGENT_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_FP Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_REF_FP() {
		return "NOM_ST_REF_FP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_FP Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_REF_FP() {
		return getZone(getNOM_ST_REF_FP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_SES Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_REF_SES() {
		return "NOM_ST_REF_SES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_SES Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_REF_SES() {
		return getZone(getNOM_ST_REF_SES());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SECTION Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_SECTION() {
		return "NOM_ST_SECTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SECTION Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_SECTION() {
		return getZone(getNOM_ST_SECTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SUBDIVISION Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION() {
		return "NOM_ST_SUBDIVISION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SUBDIVISION
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION() {
		return getZone(getNOM_ST_SUBDIVISION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_POSTE Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_ST_TITRE_POSTE() {
		return "NOM_ST_TITRE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE_POSTE
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_ST_TITRE_POSTE() {
		return getZone(getNOM_ST_TITRE_POSTE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_CLOTURE Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_DATE_CLOTURE() {
		return "NOM_EF_DATE_CLOTURE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_CLOTURE Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_DATE_CLOTURE() {
		return getZone(getNOM_EF_DATE_CLOTURE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_OUVERTURE Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_DATE_OUVERTURE() {
		return "NOM_EF_DATE_OUVERTURE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_OUVERTURE Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_DATE_OUVERTURE() {
		return getZone(getNOM_EF_DATE_OUVERTURE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_REPONSE_CAND
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_DATE_REPONSE_CAND() {
		return "NOM_EF_DATE_REPONSE_CAND";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_REPONSE_CAND Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_DATE_REPONSE_CAND() {
		return getZone(getNOM_EF_DATE_REPONSE_CAND());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_TRANSMISSION
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_DATE_TRANSMISSION() {
		return "NOM_EF_DATE_TRANSMISSION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_TRANSMISSION Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_DATE_TRANSMISSION() {
		return getZone(getNOM_EF_DATE_TRANSMISSION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_VALIDATION
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_DATE_VALIDATION() {
		return "NOM_EF_DATE_VALIDATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_VALIDATION Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_DATE_VALIDATION() {
		return getZone(getNOM_EF_DATE_VALIDATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_CAND_RECUES Date
	 * de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_NB_CAND_RECUES() {
		return "NOM_EF_NB_CAND_RECUES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NB_CAND_RECUES Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_NB_CAND_RECUES() {
		return getZone(getNOM_EF_NB_CAND_RECUES());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_AGENT_RECRUT
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_NOM_AGENT_RECRUT() {
		return "NOM_EF_NOM_AGENT_RECRUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_AGENT_RECRUT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_NOM_AGENT_RECRUT() {
		return getZone(getNOM_EF_NOM_AGENT_RECRUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_DRHFPNC Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_REF_DRHFPNC() {
		return "NOM_EF_REF_DRHFPNC";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_DRHFPNC Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_REF_DRHFPNC() {
		return getZone(getNOM_EF_REF_DRHFPNC());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_MAIRIE Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_EF_REF_MAIRIE() {
		return "NOM_EF_REF_MAIRIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_MAIRIE Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_EF_REF_MAIRIE() {
		return getZone(getNOM_EF_REF_MAIRIE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF_NON_RECRUTEMENT
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	private String[] getLB_MOTIF_NON_RECRUTEMENT() {
		if (LB_MOTIF_NON_RECRUTEMENT == null)
			LB_MOTIF_NON_RECRUTEMENT = initialiseLazyLB();
		return LB_MOTIF_NON_RECRUTEMENT;
	}

	/**
	 * Setter de la liste: LB_MOTIF_NON_RECRUTEMENT Date de création : (22/07/11
	 * 10:17:45)
	 * 
	 */
	private void setLB_MOTIF_NON_RECRUTEMENT(String[] newLB_MOTIF_NON_RECRUTEMENT) {
		LB_MOTIF_NON_RECRUTEMENT = newLB_MOTIF_NON_RECRUTEMENT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF_NON_RECRUTEMENT
	 * Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_LB_MOTIF_NON_RECRUTEMENT() {
		return "NOM_LB_MOTIF_NON_RECRUTEMENT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_NON_RECRUTEMENT_SELECT Date de création : (22/07/11
	 * 10:17:45)
	 * 
	 */
	public String getNOM_LB_MOTIF_NON_RECRUTEMENT_SELECT() {
		return "NOM_LB_MOTIF_NON_RECRUTEMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF_NON_RECRUTEMENT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String[] getVAL_LB_MOTIF_NON_RECRUTEMENT() {
		return getLB_MOTIF_NON_RECRUTEMENT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF_NON_RECRUTEMENT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_LB_MOTIF_NON_RECRUTEMENT_SELECT() {
		return getZone(getNOM_LB_MOTIF_NON_RECRUTEMENT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF_RECRUTEMENT Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	private String[] getLB_MOTIF_RECRUTEMENT() {
		if (LB_MOTIF_RECRUTEMENT == null)
			LB_MOTIF_RECRUTEMENT = initialiseLazyLB();
		return LB_MOTIF_RECRUTEMENT;
	}

	/**
	 * Setter de la liste: LB_MOTIF_RECRUTEMENT Date de création : (22/07/11
	 * 10:17:45)
	 * 
	 */
	private void setLB_MOTIF_RECRUTEMENT(String[] newLB_MOTIF_RECRUTEMENT) {
		LB_MOTIF_RECRUTEMENT = newLB_MOTIF_RECRUTEMENT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF_RECRUTEMENT Date de
	 * création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_LB_MOTIF_RECRUTEMENT() {
		return "NOM_LB_MOTIF_RECRUTEMENT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_RECRUTEMENT_SELECT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getNOM_LB_MOTIF_RECRUTEMENT_SELECT() {
		return "NOM_LB_MOTIF_RECRUTEMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF_RECRUTEMENT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String[] getVAL_LB_MOTIF_RECRUTEMENT() {
		return getLB_MOTIF_RECRUTEMENT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF_RECRUTEMENT Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public String getVAL_LB_MOTIF_RECRUTEMENT_SELECT() {
		return getZone(getNOM_LB_MOTIF_RECRUTEMENT_SELECT());
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
		return getNOM_EF_REF_MAIRIE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	private String[] LB_RECRUTEMENT;

	/**
	 * Getter de la liste avec un lazy initialize : LB_RECRUTEMENT Date de
	 * création : (22/07/11 10:42:51)
	 * 
	 */
	private String[] getLB_RECRUTEMENT() {
		if (LB_RECRUTEMENT == null)
			LB_RECRUTEMENT = initialiseLazyLB();
		return LB_RECRUTEMENT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RECRUTEMENT Date de
	 * création : (22/07/11 10:42:51)
	 * 
	 */
	public String getNOM_LB_RECRUTEMENT() {
		return "NOM_LB_RECRUTEMENT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RECRUTEMENT_SELECT Date de création : (22/07/11 10:42:51)
	 * 
	 */
	public String getNOM_LB_RECRUTEMENT_SELECT() {
		return "NOM_LB_RECRUTEMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RECRUTEMENT Date de création : (22/07/11 10:42:51)
	 * 
	 */
	public String[] getVAL_LB_RECRUTEMENT() {
		return getLB_RECRUTEMENT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RECRUTEMENT Date de création : (22/07/11 10:42:51)
	 * 
	 */
	public String getVAL_LB_RECRUTEMENT_SELECT() {
		return getZone(getNOM_LB_RECRUTEMENT_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (22/07/11 10:46:16)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:46:16)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_RECRUT Date de
	 * création : (22/07/11 10:46:16)
	 * 
	 */
	public String getNOM_PB_CREER_RECRUT() {
		return "NOM_PB_CREER_RECRUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:46:16)
	 * 
	 */
	public boolean performPB_CREER_RECRUT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On vide la zone de saisie
		viderFormulaire();

		// init du recrutement courant
		setRecrutementCourant(new Recrutement());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Vide le formulaire.
	 */
	private void viderFormulaire() {
		addZone(getNOM_ST_REF_FP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_REF_SES(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_MAIRIE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_OUVERTURE(), Const.CHAINE_VIDE);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_RECRUT Date de
	 * création : (22/07/11 10:46:16)
	 * 
	 */
	public String getNOM_PB_MODIFIER_RECRUT() {
		return "NOM_PB_MODIFIER_RECRUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:46:16)
	 * 
	 */
	public boolean performPB_MODIFIER_RECRUT(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECRUT Date de
	 * création : (22/07/11 10:46:16)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECRUT() {
		return "NOM_PB_SUPPRIMER_RECRUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:46:16)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECRUT(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (22/07/11 10:46:16)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 10:46:16)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (22/07/11 10:48:44)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {

		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Initialise les listes fixes
		initialiseListeDeroulante();

		FichePoste ficheP = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
		if (ficheP != null) {
			addZone(getNOM_ST_DIRECTION(), "TODO");
			addZone(getNOM_ST_SERVICE(), "TODO");
			addZone(getNOM_ST_SECTION(), "TODO");
			addZone(getNOM_ST_SUBDIVISION(), "TODO");
			TitrePoste titreP = TitrePoste.chercherTitrePoste(getTransaction(), ficheP.getIdTitrePoste());
			addZone(getNOM_ST_TITRE_POSTE(), titreP == null ? Const.CHAINE_VIDE : titreP.getLibTitrePoste());
			Grade grade = Grade.chercherGrade(getTransaction(), ficheP.getCodeGradeGenerique());
			addZone(getNOM_ST_GRADE(), grade == null ? Const.CHAINE_VIDE : grade.getLibGrade());
		}
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste motif recrutement vide alors affectation
		if (getLB_MOTIF_RECRUTEMENT() == LBVide) {
			ArrayList rec = MotifRecrutement.listerMotifRecrutement(getTransaction());
			setListeMotifRecrutement(rec);

			int[] tailles = { 50 };
			String[] champs = { "libMotifRecrut" };
			setLB_MOTIF_RECRUTEMENT(new FormateListe(tailles, rec, champs).getListeFormatee());
		}

		// Si liste motif non recrutement vide alors affectation
		if (getLB_MOTIF_NON_RECRUTEMENT() == LBVide) {
			ArrayList nrec = MotifNonRecrutement.listerMotifNonRecrutement(getTransaction());
			setListeMotifNonRecrutement(nrec);

			int[] tailles = { 50 };
			String[] champs = { "libMotifNonRecrut" };
			setLB_MOTIF_NON_RECRUTEMENT(new FormateListe(tailles, nrec, champs).getListeFormatee());
		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (22/07/11 10:17:45)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER_RECRUT
			if (testerParametre(request, getNOM_PB_CREER_RECRUT())) {
				return performPB_CREER_RECRUT(request);
			}

			// Si clic sur le bouton PB_MODIFIER_RECRUT
			if (testerParametre(request, getNOM_PB_MODIFIER_RECRUT())) {
				return performPB_MODIFIER_RECRUT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECRUT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECRUT())) {
				return performPB_SUPPRIMER_RECRUT(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_FP
			if (testerParametre(request, getNOM_PB_RECHERCHER_FP())) {
				return performPB_RECHERCHER_FP(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTESuiviRecrutement. Date de création :
	 * (22/07/11 10:48:44)
	 * 
	 */
	public OePOSTESuiviRecrutement() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (22/07/11 10:48:44)
	 * 
	 */
	public String getJSP() {
		return "OePOSTESuiviRecrutement.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 * 
	 * @return le nom de l'ecran
	 */
	public String getNomEcran() {
		return "ECR-PE-RECRUTEMENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (22/07/11 10:48:44)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (22/07/11 10:48:44)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le recrutement courant.
	 * 
	 * @return recrutementCourant
	 */
	public Recrutement getRecrutementCourant() {
		return recrutementCourant;
	}

	/**
	 * Met à jour le recrutement courant
	 * 
	 * @param recrutementCourant
	 *            Recrutement
	 */
	private void setRecrutementCourant(Recrutement recrutementCourant) {
		this.recrutementCourant = recrutementCourant;
	}

	/**
	 * Retourne la liste des motifs de non recrutement.
	 * 
	 * @return listeMotifNonRecrutement
	 */
	public ArrayList getListeMotifNonRecrutement() {
		return listeMotifNonRecrutement;
	}

	/**
	 * Met à jour la liste des motifs de non recrutement.
	 * 
	 * @param listeMotifNonRecrutement
	 */
	private void setListeMotifNonRecrutement(ArrayList listeMotifNonRecrutement) {
		this.listeMotifNonRecrutement = listeMotifNonRecrutement;
	}

	/**
	 * Retourne la liste des motifs de recrutement.
	 * 
	 * @return listeMotifRecrutement
	 */
	public ArrayList getListeMotifRecrutement() {
		return listeMotifRecrutement;
	}

	/**
	 * Met à jour la liste des motifs de recrutement.
	 * 
	 * @param listeMotifRecrutement
	 */
	private void setListeMotifRecrutement(ArrayList listeMotifRecrutement) {
		this.listeMotifRecrutement = listeMotifRecrutement;
	}
}
