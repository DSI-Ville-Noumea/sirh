package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.metier.Const;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFECompetence Date de création : (01/07/11 10:39:25)
 * 
 */
public class OePOSTEFECompetence extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String ACTION_SUPPRESSION = "Supprimer";
	public String ACTION_MODIFICATION = "Modifier";
	public String ACTION_AJOUT = "Ajouter";

	private ArrayList<Competence> listeCompetence;
	private Competence competenceCourant;
	private TypeCompetence typeCompetenceCourant;

	public String focus = null;

	private TypeCompetenceDao typeCompetenceDao;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_DESC_COMPETENCE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), ACTION_AJOUT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER Date de création :
	 * (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_PB_MODIFIER() {
		return "NOM_PB_MODIFIER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request) throws Exception {
		boolean auMoinsUneligneSelect = false;
		Competence comp = null;
		for (int j = 0; j < getListeCompetence().size(); j++) {
			// on recupère la ligne concernée
			comp = (Competence) getListeCompetence().get(j);
			Integer i = Integer.valueOf(comp.getIdCompetence());
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "compétences"));
			return false;
		}
		setCompetenceCourant(comp);
		addZone(getNOM_EF_DESC_COMPETENCE(), comp.getNomCompetence());
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER Date de création :
	 * (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER() {
		return "NOM_PB_SUPPRIMER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request) throws Exception {
		boolean auMoinsUneligneSelect = false;
		Competence comp = null;
		for (int j = 0; j < getListeCompetence().size(); j++) {
			// on recupère la ligne concernée
			comp = (Competence) getListeCompetence().get(j);
			Integer i = Integer.valueOf(comp.getIdCompetence());
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "compétences"));
			return false;
		}
		setCompetenceCourant(comp);
		addZone(getNOM_EF_DESC_COMPETENCE(), comp.getNomCompetence());
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_COMPETENCE
	 * Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_EF_DESC_COMPETENCE() {
		return "NOM_EF_DESC_COMPETENCE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DESC_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_EF_DESC_COMPETENCE() {
		return getZone(getNOM_EF_DESC_COMPETENCE());
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

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (01/07/11 13:52:39)
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

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		// Mise à jour de la liste des compétences
		if (getTypeCompetenceCourant() == null || getTypeCompetenceCourant().getIdTypeCompetence() == null) {
			setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetence(1));
			addZone(getNOM_RG_TYPE_COMPETENCE(), getNOM_RB_TYPE_COMPETENCE_S());
		}
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
		}

		// Si liste competences vide alors initialisation.
		if (getListeCompetence() == null || getListeCompetence().size() == 0) {
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_S()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_SF()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR_FAIRE.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_C()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.COMPORTEMENT.getValue()));

			setListeCompetence(Competence.listerCompetenceAvecType(getTransaction(), getTypeCompetenceCourant()
					.getIdTypeCompetence().toString()));

			for (int j = 0; j < getListeCompetence().size(); j++) {
				Competence competence = (Competence) getListeCompetence().get(j);
				Integer i = Integer.valueOf(competence.getIdCompetence());
				if (competence != null) {
					addZone(getNOM_ST_ID_COMP(i), competence.getIdCompetence());
					addZone(getNOM_ST_LIB_COMP(i), competence.getNomCompetence());
				}
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_COMPETENCE Date de
	 * création : (01/07/11 13:52:40)
	 * 
	 */
	public String getNOM_PB_ANNULER_COMPETENCE() {
		return "NOM_PB_ANNULER_COMPETENCE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 13:52:40)
	 * 
	 */
	public boolean performPB_ANNULER_COMPETENCE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_COMPETENCE Date de
	 * création : (01/07/11 13:52:40)
	 * 
	 */
	public String getNOM_PB_VALIDER_COMPETENCE() {
		return "NOM_PB_VALIDER_COMPETENCE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 13:52:40)
	 * 
	 */
	public boolean performPB_VALIDER_COMPETENCE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisie(request))
			return false;

		// si on est dans le cas d'une suppression
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
			if (!performControlerRegleGestion(request))
				return false;
		}
		String messageInf = Const.CHAINE_VIDE;
		if (getVAL_ST_ACTION() != null && getVAL_ST_ACTION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION().equals(ACTION_AJOUT)) {
				setCompetenceCourant(new Competence(getTypeCompetenceCourant().getIdTypeCompetence().toString(),
						getVAL_EF_DESC_COMPETENCE()));
				getCompetenceCourant().creerCompetence(getTransaction());
				messageInf = MessageUtils.getMessage("INF109", "créée", getCompetenceCourant().getNomCompetence());
			} else if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {
				getCompetenceCourant().setNomCompetence(getVAL_EF_DESC_COMPETENCE());
				getCompetenceCourant().modifierCompetence(getTransaction());
				messageInf = MessageUtils.getMessage("INF109", "modifiée", getCompetenceCourant().getNomCompetence());
			} else if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
				getCompetenceCourant().supprimerCompetence(getTransaction());
				messageInf = MessageUtils.getMessage("INF109", "supprimée", getCompetenceCourant().getNomCompetence());
				setCompetenceCourant(null);
			}
			getTransaction().commitTransaction();
			setListeCompetence(null);
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			if (!Const.CHAINE_VIDE.equals(messageInf))
				getTransaction().declarerErreur(messageInf);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies Date de création : (04/07/11 11:04:00)
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {

		// ************************************
		// Verification lib competence not null
		// ************************************
		if (getZone(getNOM_EF_DESC_COMPETENCE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getNOM_EF_DESC_COMPETENCE());
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion Date de création : (04/07/11 11:04:00)
	 * RG_PE_CO_A01
	 */
	private boolean performControlerRegleGestion(HttpServletRequest request) throws Exception {
		// RG_PE_CO_A01
		// **********************************************************************
		// Verification si suppression d'une activite utilisée sur une fiche de
		// poste
		// **********************************************************************
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)
				&& CompetenceFE.listerCompetenceFEAvecCompetence(getTransaction(), getCompetenceCourant()).size() > 0) {
			// "ERR120",
			// "Impossible de supprimer @ actuellement utilisée par @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR120", "cette compétence", "une fiche emploi"));
			setFocus(getNOM_EF_DESC_COMPETENCE());
			return false;
		}
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)
				&& CompetenceFP.listerCompetenceFPAvecCompetence(getTransaction(), getCompetenceCourant()).size() > 0) {
			// "ERR120",
			// "Impossible de supprimer @ actuellement utilisée par @."
			getTransaction()
					.declarerErreur(MessageUtils.getMessage("ERR120", "cette compétence", "une fiche de poste"));
			setFocus(getNOM_EF_DESC_COMPETENCE());
			return false;
		}
		return true;
	}

	/**
	 * Retourne la liste des compétences.
	 * 
	 * @return listeCompetence ArrayList(Competence)
	 */
	public ArrayList<Competence> getListeCompetence() {
		return listeCompetence;
	}

	/**
	 * Met à jour la liste des compétences
	 * 
	 * @param listeCompetence
	 *            ArrayList(Competence)
	 */
	private void setListeCompetence(ArrayList<Competence> listeCompetence) {
		this.listeCompetence = listeCompetence;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (04/07/11 10:52:37)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/07/11 10:52:37)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
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
		return getNOM_EF_DESC_COMPETENCE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne la competence courante.
	 * 
	 * @return competenceCourant Competence
	 */
	private Competence getCompetenceCourant() {
		return competenceCourant;
	}

	/**
	 * Met à jour la competence courante.
	 * 
	 * @param competenceCourant
	 *            Competence
	 */
	private void setCompetenceCourant(Competence competenceCourant) {
		this.competenceCourant = competenceCourant;
	}

	/**
	 * Retourne le TypeCompétence courant.
	 * 
	 * @return typeCompetenceCourant TypeCompetence
	 */
	private TypeCompetence getTypeCompetenceCourant() {
		return typeCompetenceCourant;
	}

	/**
	 * Met à jour le TypeCompetence courant.
	 * 
	 * @param typeCompetenceCourant
	 *            TypeCompetence
	 */
	private void setTypeCompetenceCourant(TypeCompetence typeCompetenceCourant) {
		this.typeCompetenceCourant = typeCompetenceCourant;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CHANGER_TYPE
			if (testerParametre(request, getNOM_PB_CHANGER_TYPE())) {
				return performPB_CHANGER_TYPE(request);
			}

			// Si clic sur le bouton PB_ANNULER_COMPETENCE
			if (testerParametre(request, getNOM_PB_ANNULER_COMPETENCE())) {
				return performPB_ANNULER_COMPETENCE(request);
			}

			// Si clic sur le bouton PB_VALIDER_COMPETENCE
			if (testerParametre(request, getNOM_PB_VALIDER_COMPETENCE())) {
				return performPB_VALIDER_COMPETENCE(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			if (testerParametre(request, getNOM_PB_MODIFIER())) {
				return performPB_MODIFIER(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER
			if (testerParametre(request, getNOM_PB_SUPPRIMER())) {
				return performPB_SUPPRIMER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFECompetence. Date de création : (04/07/11
	 * 13:57:35)
	 * 
	 */
	public OePOSTEFECompetence() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (04/07/11 13:57:35)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFECompetence.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PE-FE-COMPETENCE";
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
		setListeCompetence(null);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_COMP(int i) {
		return "NOM_ST_ID_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_COMP(int i) {
		return getZone(getNOM_ST_ID_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_COMP(int i) {
		return "NOM_ST_LIB_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_COMP(int i) {
		return getZone(getNOM_ST_LIB_COMP(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	public TypeCompetenceDao getTypeCompetenceDao() {
		return typeCompetenceDao;
	}

	public void setTypeCompetenceDao(TypeCompetenceDao typeCompetenceDao) {
		this.typeCompetenceDao = typeCompetenceDao;
	}
}
