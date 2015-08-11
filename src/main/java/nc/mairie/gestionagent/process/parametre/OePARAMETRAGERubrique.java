package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.RubriqueCharge;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.metier.parametrage.RubriqueChargeDao;
import nc.mairie.spring.dao.metier.specificites.RubriqueDao;
import nc.mairie.spring.dao.utils.MairieDao;
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
public class OePARAMETRAGERubrique extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_RUBRIQUE;

	private ArrayList<RubriqueCharge> listeRubriqueCharge;
	private RubriqueCharge rubriqueChargeCourant;
	private RubriqueChargeDao rubriqueChargeDao;
	private RubriqueDao rubriqueDao;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";
	public String ACTION_SUPPRESSION = "0";

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
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		if (getListeRubriqueCharge().size() == 0) {
			initialiseListeRubrique(request);
		}

	}

	private void initialiseListeRubrique(HttpServletRequest request) throws Exception {
		setListeRubriqueCharge((ArrayList<RubriqueCharge>) getRubriqueChargeDao().getListRubriqueCharge());
		if (getListeRubriqueCharge().size() != 0) {
			int tailles[] = { 100 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<RubriqueCharge> list = getListeRubriqueCharge().listIterator(); list.hasNext();) {
				RubriqueCharge ma = (RubriqueCharge) list.next();
				String ligne[] = { ma.getNorubr().toString() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_RUBRIQUE(aFormat.getListeFormatee());
		} else {
			setLB_RUBRIQUE(null);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getRubriqueChargeDao() == null) {
			setRubriqueChargeDao(new RubriqueChargeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRubriqueDao() == null) {
			setRubriqueDao(new RubriqueDao((MairieDao) context.getBean("mairieDao")));
		}
	}

	public OePARAMETRAGERubrique() {
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

			// Si clic sur le bouton PB_ANNULER_RUBRIQUE
			if (testerParametre(request, getNOM_PB_ANNULER_RUBRIQUE())) {
				return performPB_ANNULER_RUBRIQUE(request);
			}

			// Si clic sur le bouton PB_CREER_RUBRIQUE
			if (testerParametre(request, getNOM_PB_CREER_RUBRIQUE())) {
				return performPB_CREER_RUBRIQUE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RUBRIQUE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RUBRIQUE())) {
				return performPB_SUPPRIMER_RUBRIQUE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_RUBRIQUE
			if (testerParametre(request, getNOM_PB_MODIFIER_RUBRIQUE())) {
				return performPB_MODIFIER_RUBRIQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER_RUBRIQUE
			if (testerParametre(request, getNOM_PB_VALIDER_RUBRIQUE())) {
				return performPB_VALIDER_RUBRIQUE(request);
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
		return "OePARAMETRAGERubrique.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-RUBRIQUE";
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
		return getNOM_PB_ANNULER_RUBRIQUE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getNOM_ST_ACTION_RUBRIQUE() {
		return "NOM_ST_ACTION_RUBRIQUE";
	}

	public String getVAL_ST_ACTION_RUBRIQUE() {
		return getZone(getNOM_ST_ACTION_RUBRIQUE());
	}

	public String getNOM_EF_LIB_RUBRIQUE() {
		return "NOM_EF_LIB_RUBRIQUE";
	}

	public String getVAL_EF_LIB_RUBRIQUE() {
		return getZone(getNOM_EF_LIB_RUBRIQUE());
	}

	private String[] getLB_RUBRIQUE() {
		if (LB_RUBRIQUE == null)
			LB_RUBRIQUE = initialiseLazyLB();
		return LB_RUBRIQUE;
	}

	private void setLB_RUBRIQUE(String[] newLB_RUBRIQUE) {
		LB_RUBRIQUE = newLB_RUBRIQUE;
	}

	public String getNOM_LB_RUBRIQUE() {
		return "NOM_LB_RUBRIQUE";
	}

	public String getNOM_LB_RUBRIQUE_SELECT() {
		return "NOM_LB_RUBRIQUE_SELECT";
	}

	public String[] getVAL_LB_RUBRIQUE() {
		return getLB_RUBRIQUE();
	}

	public String getVAL_LB_RUBRIQUE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_SELECT());
	}

	private ArrayList<RubriqueCharge> getListeRubriqueCharge() {
		if (listeRubriqueCharge == null)
			return new ArrayList<RubriqueCharge>();
		return listeRubriqueCharge;
	}

	private void setListeRubriqueCharge(ArrayList<RubriqueCharge> listeRubriqueCharge) {
		this.listeRubriqueCharge = listeRubriqueCharge;
	}

	private RubriqueCharge getRubriqueChargeCourant() {
		return rubriqueChargeCourant;
	}

	private void setRubriqueChargeCourant(RubriqueCharge rubriqueChargeCourant) {
		this.rubriqueChargeCourant = rubriqueChargeCourant;
	}

	public String getNOM_PB_ANNULER_RUBRIQUE() {
		return "NOM_PB_ANNULER_RUBRIQUE";
	}

	public boolean performPB_ANNULER_RUBRIQUE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_RUBRIQUE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_RUBRIQUE());
		return true;
	}

	public String getNOM_PB_CREER_RUBRIQUE() {
		return "NOM_PB_CREER_RUBRIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_RUBRIQUE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_RUBRIQUE(), ACTION_CREATION);
		addZone(getNOM_EF_LIB_RUBRIQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_CK_CODE_CHARGE(), getCHECKED_OFF());
		addZone(getNOM_CK_CREANCIER(), getCHECKED_OFF());
		addZone(getNOM_CK_MATRICULE_CHARGE(), getCHECKED_OFF());
		addZone(getNOM_CK_MONTANT(), getCHECKED_OFF());
		addZone(getNOM_CK_DONNEES_MUTU(), getCHECKED_OFF());

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_RUBRIQUE());
		return true;
	}

	public String getNOM_PB_MODIFIER_RUBRIQUE() {
		return "NOM_PB_MODIFIER_RUBRIQUE";
	}

	public boolean performPB_MODIFIER_RUBRIQUE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_RUBRIQUE_SELECT()) ? Integer
				.parseInt(getVAL_LB_RUBRIQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeRubriqueCharge().size()) {
			RubriqueCharge ma = getListeRubriqueCharge().get(indice);
			setRubriqueChargeCourant(ma);
			addZone(getNOM_EF_LIB_RUBRIQUE(), ma.getNorubr().toString());

			addZone(getNOM_CK_CODE_CHARGE(), getRubriqueChargeCourant().isShowCodeCharge() ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_CK_CREANCIER(), getRubriqueChargeCourant().isShowCreancier() ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_CK_MATRICULE_CHARGE(), getRubriqueChargeCourant().isShowMatriculeCharge() ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_CK_MONTANT(), getRubriqueChargeCourant().isShowMontant() ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_CK_DONNEES_MUTU(), getRubriqueChargeCourant().isShowDonneesMutu() ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_ST_ACTION_RUBRIQUE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques de charge"));
		}

		setFocus(getNOM_PB_ANNULER_RUBRIQUE());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RUBRIQUE() {
		return "NOM_PB_SUPPRIMER_RUBRIQUE";
	}

	public boolean performPB_SUPPRIMER_RUBRIQUE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_RUBRIQUE_SELECT()) ? Integer
				.parseInt(getVAL_LB_RUBRIQUE_SELECT()) : -1);
		if (indice != -1 && indice < getListeRubriqueCharge().size()) {
			RubriqueCharge ma = getListeRubriqueCharge().get(indice);
			setRubriqueChargeCourant(ma);
			addZone(getNOM_EF_LIB_RUBRIQUE(), ma.getNorubr().toString());
			addZone(getNOM_ST_ACTION_RUBRIQUE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques de charge"));
		}

		setFocus(getNOM_PB_ANNULER_RUBRIQUE());
		return true;
	}

	public String getNOM_PB_VALIDER_RUBRIQUE() {
		return "NOM_PB_VALIDER_RUBRIQUE";
	}

	public boolean performPB_VALIDER_RUBRIQUE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieRubrique(request))
			return false;

		if (!performControlerRegleGestionRubrique(request))
			return false;

		if (getVAL_ST_ACTION_RUBRIQUE() != null && getVAL_ST_ACTION_RUBRIQUE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_RUBRIQUE().equals(ACTION_CREATION)) {
				setRubriqueChargeCourant(new RubriqueCharge());
				getRubriqueChargeCourant().setNorubr(new Integer(getVAL_EF_LIB_RUBRIQUE()));
				getRubriqueChargeCourant().setShowCodeCharge(getVAL_CK_CODE_CHARGE().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowCreancier(getVAL_CK_CREANCIER().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowMatriculeCharge(getVAL_CK_MATRICULE_CHARGE().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowMontant(getVAL_CK_MONTANT().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowDonneesMutu(getVAL_CK_DONNEES_MUTU().equals(getCHECKED_ON()));
				getRubriqueChargeDao().creerRubriqueCharge(getRubriqueChargeCourant());
				if (!getTransaction().isErreur())
					getListeRubriqueCharge().add(getRubriqueChargeCourant());
			} else if (getVAL_ST_ACTION_RUBRIQUE().equals(ACTION_MODIFICATION)) {
				getRubriqueChargeCourant().setShowCodeCharge(getVAL_CK_CODE_CHARGE().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowCreancier(getVAL_CK_CREANCIER().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowMatriculeCharge(getVAL_CK_MATRICULE_CHARGE().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowMontant(getVAL_CK_MONTANT().equals(getCHECKED_ON()));
				getRubriqueChargeCourant().setShowDonneesMutu(getVAL_CK_DONNEES_MUTU().equals(getCHECKED_ON()));
				getRubriqueChargeDao().modifierRubriqueCharge(getRubriqueChargeCourant());
			} else if (getVAL_ST_ACTION_RUBRIQUE().equals(ACTION_SUPPRESSION)) {
				getRubriqueChargeDao().supprimerRubriqueCharge(getRubriqueChargeCourant());
				if (!getTransaction().isErreur())
					getListeRubriqueCharge().remove(getRubriqueChargeCourant());
				setRubriqueChargeCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeRubrique(request);
			addZone(getNOM_ST_ACTION_RUBRIQUE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_RUBRIQUE());
		return true;
	}

	private boolean performControlerSaisieRubrique(HttpServletRequest request) throws Exception {
		// Verification numero rubrique not null
		if (getZone(getNOM_EF_LIB_RUBRIQUE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "numero rubrique"));
			return false;
		}
		// Verification numero rubrique numerique
		if (!Services.estNumerique(getZone(getNOM_EF_LIB_RUBRIQUE()))) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "numéro rubrique"));
			return false;
		}
		return true;
	}

	private boolean performControlerRegleGestionRubrique(HttpServletRequest request) throws Exception {

		// verification existance rubrique
		Rubrique rub = getRubriqueDao().chercherRubrique(new Integer(getVAL_EF_LIB_RUBRIQUE()));
		if (rub == null) {
			// "ERR149",
			// "Attention, il n'existe pas de rubrique avec ce numéro."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR149"));
			return false;
		}

		if (getVAL_ST_ACTION_RUBRIQUE().equals(ACTION_CREATION)) {
			// Vérification des contraintes d'unicité de la rubrique
			for (RubriqueCharge rubr : getListeRubriqueCharge()) {
				if (rubr.getNorubr().toString().toUpperCase().equals(getVAL_EF_LIB_RUBRIQUE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une rubrique", "ce numéro"));
					return false;
				}
			}
		}

		return true;
	}

	public RubriqueChargeDao getRubriqueChargeDao() {
		return rubriqueChargeDao;
	}

	public void setRubriqueChargeDao(RubriqueChargeDao rubriqueChargeDao) {
		this.rubriqueChargeDao = rubriqueChargeDao;
	}

	public String getNOM_CK_CODE_CHARGE() {
		return "NOM_CK_CODE_CHARGE";
	}

	public String getVAL_CK_CODE_CHARGE() {
		return getZone(getNOM_CK_CODE_CHARGE());
	}

	public String getNOM_CK_CREANCIER() {
		return "NOM_CK_CREANCIER";
	}

	public String getVAL_CK_CREANCIER() {
		return getZone(getNOM_CK_CREANCIER());
	}

	public String getNOM_CK_MATRICULE_CHARGE() {
		return "NOM_CK_CMATRICULE_CHARGE";
	}

	public String getVAL_CK_MATRICULE_CHARGE() {
		return getZone(getNOM_CK_MATRICULE_CHARGE());
	}

	public String getNOM_CK_MONTANT() {
		return "NOM_CK_MONTANT";
	}

	public String getVAL_CK_MONTANT() {
		return getZone(getNOM_CK_MONTANT());
	}

	public String getNOM_CK_DONNEES_MUTU() {
		return "NOM_CK_DONNEES_MUTU";
	}

	public String getVAL_CK_DONNEES_MUTU() {
		return getZone(getNOM_CK_DONNEES_MUTU());
	}

	public RubriqueDao getRubriqueDao() {
		return rubriqueDao;
	}

	public void setRubriqueDao(RubriqueDao rubriqueDao) {
		this.rubriqueDao = rubriqueDao;
	}
}
