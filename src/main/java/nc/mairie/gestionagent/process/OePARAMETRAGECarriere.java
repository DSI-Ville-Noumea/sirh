package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.spring.dao.metier.parametrage.SPBASEDao;
import nc.mairie.spring.domain.metier.parametrage.SPBASE;
import nc.mairie.spring.utils.ApplicationContextProvider;
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
public class OePARAMETRAGECarriere extends nc.mairie.technique.BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_SPBASE;
	private SPBASEDao spbaseDao;
	private ArrayList<SPBASE> listeSpbase;
	private SPBASE spbaseCourant;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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
		if (getListeSpbase().size() == 0) {
			initialiseListeSpbase(request);
		}

	}

	private void initialiseListeSpbase(HttpServletRequest request) throws Exception {
		setListeSpbase(getSpbaseDao().listerSPBASE());
		if (getListeSpbase().size() != 0) {
			int tailles[] = { 6, 30 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<SPBASE> list = getListeSpbase().listIterator(); list.hasNext();) {
				SPBASE base = (SPBASE) list.next();
				String ligne[] = { base.getCdBase(), base.getLiBase() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_SPBASE(aFormat.getListeFormatee());
		} else {
			setLB_SPBASE(null);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getSpbaseDao() == null) {
			setSpbaseDao((SPBASEDao) context.getBean("spbaseDao"));
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
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CREER_SPBASE
			if (testerParametre(request, getNOM_PB_CREER_SPBASE())) {
				return performPB_CREER_SPBASE(request);
			}
			// Si clic sur le bouton PB_MODIFIER_SPBASE
			if (testerParametre(request, getNOM_PB_MODIFIER_SPBASE())) {
				return performPB_MODIFIER_SPBASE(request);
			}
			// Si clic sur le bouton PB_ANNULER_SPBASE
			if (testerParametre(request, getNOM_PB_ANNULER_SPBASE())) {
				return performPB_ANNULER_SPBASE(request);
			}
			// Si clic sur le bouton PB_VALIDER_SPBASE
			if (testerParametre(request, getNOM_PB_VALIDER_SPBASE())) {
				return performPB_VALIDER_SPBASE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
		return getNOM_PB_ANNULER_SPBASE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public SPBASEDao getSpbaseDao() {
		return spbaseDao;
	}

	public void setSpbaseDao(SPBASEDao spbaseDao) {
		this.spbaseDao = spbaseDao;
	}

	public ArrayList<SPBASE> getListeSpbase() {
		return listeSpbase == null ? new ArrayList<SPBASE>() : listeSpbase;
	}

	public void setListeSpbase(ArrayList<SPBASE> listeSpbase) {
		this.listeSpbase = listeSpbase;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SPBASE Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_SPBASE() {
		if (LB_SPBASE == null)
			LB_SPBASE = initialiseLazyLB();
		return LB_SPBASE;
	}

	/**
	 * Setter de la liste: LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_SPBASE(String[] newLB_SPBASE) {
		LB_SPBASE = newLB_SPBASE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SPBASE Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE() {
		return "NOM_LB_SPBASE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_SPBASE_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE_SELECT() {
		return "NOM_LB_SPBASE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_SPBASE() {
		return getLB_SPBASE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_SPBASE_SELECT() {
		return getZone(getNOM_LB_SPBASE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_SPBASE() {
		return "NOM_PB_CREER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_SPBASE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_SPBASE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_SPBASE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_MODIFIER_SPBASE() {
		return "NOM_PB_MODIFIER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_MODIFIER_SPBASE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_SPBASE_SELECT()) ? Integer.parseInt(getVAL_LB_SPBASE_SELECT()) : -1);
		if (indice != -1 && indice < getListeSpbase().size()) {
			SPBASE base = getListeSpbase().get(indice);
			setSpbaseCourant(base);
			addZone(getNOM_EF_LIB_SPBASE(), base.getLiBase());
			addZone(getNOM_EF_CODE_SPBASE(), base.getCdBase());
			addZone(getNOM_ST_ACTION_SPBASE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases horaires"));
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_SPBASE Date
	 * de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_SPBASE() {
		return "NOM_ST_ACTION_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_SPBASE
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_SPBASE() {
		return getZone(getNOM_ST_ACTION_SPBASE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_SPBASE() {
		return "NOM_PB_ANNULER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_SPBASE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_SPBASE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_SPBASE() {
		return "NOM_PB_VALIDER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_SPBASE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieSPBASE(request))
			return false;

		if (!performControlerRegleGestionSPBASE(request))
			return false;

		if (getVAL_ST_ACTION_SPBASE() != null && getVAL_ST_ACTION_SPBASE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_SPBASE().equals(ACTION_CREATION)) {
				setSpbaseCourant(new SPBASE());
				getSpbaseCourant().setCdBase(getVAL_EF_CODE_SPBASE());
				getSpbaseCourant().setLiBase(getVAL_EF_LIB_SPBASE());
				// getSpbaseDao().creerSPBASE(getSpbaseCourant().getCdBase(),
				// getSpbaseCourant().getLiBase());
				// TODO

				getListeSpbase().add(getSpbaseCourant());
			} else if (getVAL_ST_ACTION_SPBASE().equals(ACTION_MODIFICATION)) {
				getSpbaseCourant().setCdBase(getVAL_EF_CODE_SPBASE());
				getSpbaseCourant().setLiBase(getVAL_EF_LIB_SPBASE());
				// getSpbaseDao().modifierSPBASE(getSpbaseCourant().getCdBase(),
				// getSpbaseCourant().getLiBase());
				// TODO
				setSpbaseCourant(null);
			}

			initialiseListeSpbase(request);
			addZone(getNOM_ST_ACTION_SPBASE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	private boolean performControlerRegleGestionSPBASE(HttpServletRequest request) {
		// Vérification des contraintes d'unicité de la base horaire
		if (getVAL_ST_ACTION_SPBASE().equals(ACTION_CREATION)) {

			for (SPBASE motif : getListeSpbase()) {
				if (motif.getLiBase().trim().equals(getVAL_EF_LIB_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce libellé"));
					return false;
				}
				if (motif.getCdBase().trim().equals(getVAL_EF_CODE_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce code"));
					return false;
				}
			}
		}
		return true;
	}

	private boolean performControlerSaisieSPBASE(HttpServletRequest request) {
		// Verification libellé not null
		if (getZone(getNOM_EF_LIB_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification code not null
		if (getZone(getNOM_EF_CODE_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_SPBASE() {
		return "NOM_EF_LIB_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_SPBASE() {
		return getZone(getNOM_EF_LIB_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_SPBASE() {
		return "NOM_EF_CODE_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_SPBASE() {
		return getZone(getNOM_EF_CODE_SPBASE());
	}

	public SPBASE getSpbaseCourant() {
		return spbaseCourant;
	}

	public void setSpbaseCourant(SPBASE spbaseCourant) {
		this.spbaseCourant = spbaseCourant;
	}
}
