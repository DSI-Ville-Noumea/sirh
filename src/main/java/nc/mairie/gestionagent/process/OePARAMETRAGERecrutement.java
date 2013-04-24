package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.MotifNonRecrutement;
import nc.mairie.metier.parametrage.MotifRecrutement;
import nc.mairie.metier.poste.Recrutement;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OePARAMETRAGERecrutement
 * Date de création : (14/09/11 13:52:54)
     *
 */
public class OePARAMETRAGERecrutement extends nc.mairie.technique.BasicProcess {
	private String[] LB_MOTIF;

	private ArrayList<MotifRecrutement> listeMotif;
	private MotifRecrutement motifCourant;

	private ArrayList<MotifNonRecrutement> listeNonRec;
	private MotifNonRecrutement nonRecCourant;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	/**
	 * Initialisation des zones à afficher dans la JSP
	 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	 * Utilisation de la méthode addZone(getNOMxxx, String);
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		//POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		//----------------------------------//
		// Vérification des droits d'accès. //
		//----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			//"ERR190", "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		//---------------------------//
		// Initialisation de la page.//
		//---------------------------//	
		if (getListeMotif() == null) {
			setListeMotif(MotifRecrutement.listerMotifRecrutement(getTransaction()));
			initialiseListeMotif(request);
		}

		if (getListeNonRec() == null) {
			setListeNonRec(MotifNonRecrutement.listerMotifNonRecrutement(getTransaction()));
			initialiseListeNonRec(request);
		}
	}

	/**
	 * Initialisation de la listes des motifs de recrutement
	 * Date de création : (14/09/11)
     *
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		setListeMotif(MotifRecrutement.listerMotifRecrutement(getTransaction()));
		if (getListeMotif().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MotifRecrutement> list = getListeMotif().listIterator(); list.hasNext();) {
				MotifRecrutement mr = (MotifRecrutement) list.next();
				String ligne[] = { mr.getLibMotifRecrut() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	/**
	 * Initialisation de la listes des motifs de non recrutement
	 * Date de création : (14/09/11)
     *
	 */
	private void initialiseListeNonRec(HttpServletRequest request) throws Exception {
		setListeNonRec(MotifNonRecrutement.listerMotifNonRecrutement(getTransaction()));
		if (getListeNonRec().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MotifNonRecrutement> list = getListeNonRec().listIterator(); list.hasNext();) {
				MotifNonRecrutement mr = (MotifNonRecrutement) list.next();
				String ligne[] = { mr.getLibMotifNonRecrut() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_NON_REC(aFormat.getListeFormatee());
		} else {
			setLB_NON_REC(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGERecrutement.
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public OePARAMETRAGERecrutement() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_CREER_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public boolean performPB_CREER_MOTIF(HttpServletRequest request) throws Exception {
		//On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF(), ACTION_CREATION);
		addZone(getNOM_EF_MOTIF(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_PB_SUPPRIMER_MOTIF() {
		return "NOM_PB_SUPPRIMER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public boolean performPB_SUPPRIMER_MOTIF(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);

		if (indice != -1 && indice < getListeMotif().size()) {
			MotifRecrutement mr = getListeMotif().get(indice);
			setMotifCourant(mr);
			addZone(getNOM_EF_MOTIF(), mr.getLibMotifRecrut());
			addZone(getNOM_ST_ACTION_MOTIF(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de recrutement"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_VALIDER_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public boolean performPB_VALIDER_MOTIF(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMotif(request))
			return false;

		if (!performControlerRegleGestionMotif(request))
			return false;

		if (getVAL_ST_ACTION_MOTIF() != null && getVAL_ST_ACTION_MOTIF() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
				setMotifCourant(new MotifRecrutement());
				getMotifCourant().setLibMotifRecrut(getVAL_EF_MOTIF());
				getMotifCourant().creerMotifRecrutement(getTransaction());
				if (!getTransaction().isErreur())
					getListeMotif().add(getMotifCourant());
			} else if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)) {
				getMotifCourant().supprimerMotifRecrutement(getTransaction());
				if (!getTransaction().isErreur())
					getListeMotif().remove(getMotifCourant());
				setMotifCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMotif(request);
			addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un motif de recrutement
	 * Date de création : (14/09/11)
	 */
	private boolean performControlerSaisieMotif(HttpServletRequest request) throws Exception {

		//Verification libellé motif not null
		if (getZone(getNOM_EF_MOTIF()).length() == 0) {
			//"ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une entité géographique
	 * Date de création : (14/09/11)
	 */
	private boolean performControlerRegleGestionMotif(HttpServletRequest request) throws Exception {

		//Verification si suppression d'une entité géographique utilisée sur une fiche de poste
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION) && Recrutement.listerRecrutementAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {

			//"ERR989", "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un recrutement", "ce motif de recrutement"));
			return false;
		}

		//Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {

			for (MotifRecrutement motif : getListeMotif()) {
				if (motif.getLibMotifRecrut().equals(getVAL_EF_MOTIF().toUpperCase())) {
					//"ERR974", "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif de recrutement", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_ACTION_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_EF_MOTIF() {
		return "NOM_EF_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie  :
	 * EF_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getVAL_EF_MOTIF() {
		return getZone(getNOM_EF_MOTIF());
	}

	/**
	 * Getter de la liste avec un lazy initialize :
	 * LB_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste:
	 * LB_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP :
	 * NOM_LB_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser
	 * Retourne la valeur à afficher pour la zone de la JSP :
	 * LB_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser
	 * Retourne l'indice à sélectionner pour la zone de la JSP :
	 * LB_MOTIF
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	private ArrayList<MotifRecrutement> getListeMotif() {
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifRecrutement> listeMotif) {
		this.listeMotif = listeMotif;
	}

	private MotifRecrutement getMotifCourant() {
		return motifCourant;
	}

	private void setMotifCourant(MotifRecrutement motifCourant) {
		this.motifCourant = motifCourant;
	}

	private String[] LB_NON_REC;

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : 
	 * en fonction du bouton de la JSP 
	 * Date de création : (14/09/11 13:52:54)
     *
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		//Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			//Si clic sur le bouton PB_ANNULER_NON_REC
			if (testerParametre(request, getNOM_PB_ANNULER_NON_REC())) {
				return performPB_ANNULER_NON_REC(request);
			}

			//Si clic sur le bouton PB_CREER_NON_REC
			if (testerParametre(request, getNOM_PB_CREER_NON_REC())) {
				return performPB_CREER_NON_REC(request);
			}

			//Si clic sur le bouton PB_SUPPRIMER_NON_REC
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NON_REC())) {
				return performPB_SUPPRIMER_NON_REC(request);
			}

			//Si clic sur le bouton PB_VALIDER_NON_REC
			if (testerParametre(request, getNOM_PB_VALIDER_NON_REC())) {
				return performPB_VALIDER_NON_REC(request);
			}

			//Si clic sur le bouton PB_ANNULER_MOTIF
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF())) {
				return performPB_ANNULER_MOTIF(request);
			}

			//Si clic sur le bouton PB_CREER_MOTIF
			if (testerParametre(request, getNOM_PB_CREER_MOTIF())) {
				return performPB_CREER_MOTIF(request);
			}

			//Si clic sur le bouton PB_SUPPRIMER_MOTIF
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MOTIF())) {
				return performPB_SUPPRIMER_MOTIF(request);
			}

			//Si clic sur le bouton PB_VALIDER_MOTIF
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF())) {
				return performPB_VALIDER_MOTIF(request);
			}

		}
		//Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process
	 * Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getJSP() {
		return "OePARAMETRAGERecrutement.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-PE-RECRUTEMENT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_PB_ANNULER_NON_REC() {
		return "NOM_PB_ANNULER_NON_REC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public boolean performPB_ANNULER_NON_REC(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_NON_REC(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_CREER_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_PB_CREER_NON_REC() {
		return "NOM_PB_CREER_NON_REC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public boolean performPB_CREER_NON_REC(HttpServletRequest request) throws Exception {
		//On nomme l'action
		addZone(getNOM_ST_ACTION_NON_REC(), ACTION_CREATION);
		addZone(getNOM_EF_NON_REC(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_PB_SUPPRIMER_NON_REC() {
		return "NOM_PB_SUPPRIMER_NON_REC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public boolean performPB_SUPPRIMER_NON_REC(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_NON_REC_SELECT()) ? Integer.parseInt(getVAL_LB_NON_REC_SELECT()) : -1);

		if (indice != -1 && indice < getListeNonRec().size()) {
			MotifNonRecrutement mnr = getListeNonRec().get(indice);
			setNonRecCourant(mnr);
			addZone(getNOM_EF_NON_REC(), mnr.getLibMotifNonRecrut());
			addZone(getNOM_ST_ACTION_NON_REC(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de non recrutement"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_VALIDER_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_PB_VALIDER_NON_REC() {
		return "NOM_PB_VALIDER_NON_REC";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public boolean performPB_VALIDER_NON_REC(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieNonRec(request))
			return false;

		if (!performControlerRegleGestionNonRec(request))
			return false;

		if (getVAL_ST_ACTION_NON_REC() != null && getVAL_ST_ACTION_NON_REC() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_NON_REC().equals(ACTION_CREATION)) {
				setNonRecCourant(new MotifNonRecrutement());
				getNonRecCourant().setLibMotifNonRecrut(getVAL_EF_NON_REC());
				getNonRecCourant().creerMotifNonRecrutement(getTransaction());
				if (!getTransaction().isErreur())
					getListeNonRec().add(getNonRecCourant());
			} else if (getVAL_ST_ACTION_NON_REC().equals(ACTION_SUPPRESSION)) {
				getNonRecCourant().supprimerMotifNonRecrutement(getTransaction());
				if (!getTransaction().isErreur())
					getListeNonRec().remove(getNonRecCourant());
				setNonRecCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeNonRec(request);
			addZone(getNOM_ST_ACTION_NON_REC(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un motif de non recrutement
	 * Date de création : (14/09/11)
	 */
	private boolean performControlerSaisieNonRec(HttpServletRequest request) throws Exception {

		//Verification libellé motif not null
		if (getZone(getNOM_EF_NON_REC()).length() == 0) {
			//"ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un motif de non recrutement
	 * Date de création : (14/09/11)
	 */
	private boolean performControlerRegleGestionNonRec(HttpServletRequest request) throws Exception {

		//Verification si suppression d'une entité géographique utilisée sur une fiche de poste
		if (getVAL_ST_ACTION_NON_REC().equals(ACTION_SUPPRESSION) && Recrutement.listerRecrutementAvecMotifNonRec(getTransaction(), getNonRecCourant()).size() > 0) {

			//"ERR989", "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un recrutement", "ce motif de non recrutement"));
			return false;
		}

		//Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_NON_REC().equals(ACTION_CREATION)) {

			for (MotifNonRecrutement motif : getListeNonRec()) {
				if (motif.getLibMotifNonRecrut().equals(getVAL_EF_NON_REC().toUpperCase())) {
					//"ERR974", "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif de non recrutement", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_ST_ACTION_NON_REC() {
		return "NOM_ST_ACTION_NON_REC";
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_ACTION_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getVAL_ST_ACTION_NON_REC() {
		return getZone(getNOM_ST_ACTION_NON_REC());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_EF_NON_REC() {
		return "NOM_EF_NON_REC";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie  :
	 * EF_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getVAL_EF_NON_REC() {
		return getZone(getNOM_EF_NON_REC());
	}

	/**
	 * Getter de la liste avec un lazy initialize :
	 * LB_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	private String[] getLB_NON_REC() {
		if (LB_NON_REC == null)
			LB_NON_REC = initialiseLazyLB();
		return LB_NON_REC;
	}

	/**
	 * Setter de la liste:
	 * LB_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	private void setLB_NON_REC(String[] newLB_NON_REC) {
		LB_NON_REC = newLB_NON_REC;
	}

	/**
	 * Retourne le nom de la zone pour la JSP :
	 * NOM_LB_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_LB_NON_REC() {
		return "NOM_LB_NON_REC";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NON_REC_SELECT
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getNOM_LB_NON_REC_SELECT() {
		return "NOM_LB_NON_REC_SELECT";
	}

	/**
	 * Méthode à personnaliser
	 * Retourne la valeur à afficher pour la zone de la JSP :
	 * LB_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String[] getVAL_LB_NON_REC() {
		return getLB_NON_REC();
	}

	/**
	 * Méthode à personnaliser
	 * Retourne l'indice à sélectionner pour la zone de la JSP :
	 * LB_NON_REC
	 * Date de création : (14/09/11 15:20:21)
     *
	 */
	public String getVAL_LB_NON_REC_SELECT() {
		return getZone(getNOM_LB_NON_REC_SELECT());
	}

	private ArrayList<MotifNonRecrutement> getListeNonRec() {
		return listeNonRec;
	}

	private void setListeNonRec(ArrayList<MotifNonRecrutement> listeNonRec) {
		this.listeNonRec = listeNonRec;
	}

	private MotifNonRecrutement getNonRecCourant() {
		return nonRecCourant;
	}

	private void setNonRecCourant(MotifNonRecrutement nonRecCourant) {
		this.nonRecCourant = nonRecCourant;
	}
}
