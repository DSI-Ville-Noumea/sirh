package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.parametrage.MotifCarriere;
import nc.mairie.spring.dao.metier.parametrage.MotifCarriereDao;
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
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGECarriere extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_MOTIF;

	private ArrayList<MotifCarriere> listeMotif;
	private MotifCarriere motifCourant;
	private MotifCarriereDao motifCarriereDao;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";
	public String ACTION_SUPPRESSION = "0";

	public String focus = null;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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

		if (getListeMotif().size() == 0) {
			initialiseListeMotif(request);
		}

	}

	/**
	 * Initialisation de la listes des motifs de carriere Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		setListeMotif(getMotifCarriereDao().listerMotifCarriere());
		if (getListeMotif().size() != 0) {
			int tailles[] = { 100 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MotifCarriere> list = getListeMotif().listIterator(); list.hasNext();) {
				MotifCarriere ma = (MotifCarriere) list.next();
				String ligne[] = { ma.getLibMotifCarriere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getMotifCarriereDao() == null) {
			setMotifCarriereDao(new MotifCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAvancement. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGECarriere() {
		super();
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_MOTIF
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF())) {
				return performPB_ANNULER_MOTIF(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF
			if (testerParametre(request, getNOM_PB_CREER_MOTIF())) {
				return performPB_CREER_MOTIF(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MOTIF
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MOTIF())) {
				return performPB_SUPPRIMER_MOTIF(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF())) {
				return performPB_VALIDER_MOTIF(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGECarriere.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-CARRIERE";
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
		return getNOM_PB_ANNULER_MOTIF();
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION_MOTIF
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_LIB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	private ArrayList<MotifCarriere> getListeMotif() {
		if (listeMotif == null)
			return new ArrayList<MotifCarriere>();
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifCarriere> listeMotif) {
		this.listeMotif = listeMotif;
	}

	private MotifCarriere getMotifCourant() {
		return motifCourant;
	}

	private void setMotifCourant(MotifCarriere motifCourant) {
		this.motifCourant = motifCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MOTIF Date de création
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_MOTIF(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF(), ACTION_CREATION);
		addZone(getNOM_EF_LIB_MOTIF(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MOTIF() {
		return "NOM_PB_SUPPRIMER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MOTIF(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotif().size()) {
			MotifCarriere ma = getListeMotif().get(indice);
			setMotifCourant(ma);
			addZone(getNOM_EF_LIB_MOTIF(), ma.getLibMotifCarriere());
			addZone(getNOM_ST_ACTION_MOTIF(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de carrière"));
		}

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_MOTIF(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMotif(request))
			return false;

		if (!performControlerRegleGestionMotif(request))
			return false;

		if (getVAL_ST_ACTION_MOTIF() != null && getVAL_ST_ACTION_MOTIF() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
				setMotifCourant(new MotifCarriere());
				getMotifCourant().setLibMotifCarriere(getVAL_EF_LIB_MOTIF());
				getMotifCarriereDao().creerMotifCarriere(getMotifCourant().getLibMotifCarriere());
				if (!getTransaction().isErreur())
					getListeMotif().add(getMotifCourant());
			} else if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)) {
				getMotifCarriereDao().supprimerMotifCarriere(getMotifCourant().getIdMotifCarriere());
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

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	private boolean performControlerSaisieMotif(HttpServletRequest request) throws Exception {
		// Verification libellé motif not null
		if (getZone(getNOM_EF_LIB_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		return true;
	}

	private boolean performControlerRegleGestionMotif(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un motif de carriere utilisee sur une
		// carriere
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)
				&& Carriere.listerCarriereAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une carrière", "ce motif de carrière"));
			return false;
		}

		// Vérification des contraintes d'unicité du motif de carriere
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {

			for (MotifCarriere motif : getListeMotif()) {
				if (motif.getLibMotifCarriere().toUpperCase().equals(getVAL_EF_LIB_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà  @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un motif de carrière", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	public MotifCarriereDao getMotifCarriereDao() {
		return motifCarriereDao;
	}

	public void setMotifCarriereDao(MotifCarriereDao motifCarriereDao) {
		this.motifCarriereDao = motifCarriereDao;
	}
}
