package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.poste.Activite;
import nc.mairie.spring.dao.metier.poste.ActiviteDao;
import nc.mairie.spring.dao.metier.poste.ActiviteFEDao;
import nc.mairie.spring.dao.metier.poste.ActiviteFPDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFEActivite Date de création : (04/07/11 16:24:13)
 * 
 */
public class OePOSTEFEActivite extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String ACTION_SUPPRESSION = "Supprimer";
	public String ACTION_MODIFICATION = "Modifier";
	public String ACTION_AJOUT = "Ajouter";
	public String ACTION_CONSULTATION = "Consulter";

	private ArrayList<Activite> listeActivite;

	private Activite activiteCourante;

	public String focus = null;

	private ActiviteDao activiteDao;
	private ActiviteFPDao activiteFPDao;
	private ActiviteFEDao activiteFEDao;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (04/07/11 16:24:13)
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
		// Si liste activites vide alors initialisation.
		if (getListeActivite() == null || getListeActivite().size() == 0) {
			setListeActivite(getActiviteDao().listerActivite(true));

			for (int j = 0; j < getListeActivite().size(); j++) {
				Activite activite = (Activite) getListeActivite().get(j);
				Integer i = activite.getIdActivite();
				if (activite != null) {
					addZone(getNOM_ST_ID_ACTI(i), activite.getIdActivite().toString());
					addZone(getNOM_ST_LIB_ACTI(i), activite.getNomActivite());
				}
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getActiviteDao() == null) {
			setActiviteDao(new ActiviteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteFPDao() == null) {
			setActiviteFPDao(new ActiviteFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteFEDao() == null) {
			setActiviteFEDao(new ActiviteFEDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (04/07/11 16:24:13)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/07/11 16:24:13)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ACTIVITE Date de
	 * création : (05/07/11 10:43:04)
	 * 
	 */
	public String getNOM_PB_VALIDER_ACTIVITE() {
		return "NOM_PB_VALIDER_ACTIVITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/07/11 10:43:04)
	 * 
	 */
	public boolean performPB_VALIDER_ACTIVITE(HttpServletRequest request) throws Exception {
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
				setActiviteCourante(new Activite(getVAL_EF_DESC_ACTIVITE()));
				getActiviteDao().creerActivite(getActiviteCourante().getNomActivite());
				messageInf = MessageUtils.getMessage("INF110", "créée", getActiviteCourante().getNomActivite());
			} else if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {
				getActiviteCourante().setNomActivite(getVAL_EF_DESC_ACTIVITE());
				getActiviteDao().modifierActivite(getActiviteCourante().getIdActivite(),
						getActiviteCourante().getNomActivite());
				messageInf = MessageUtils.getMessage("INF110", "modifiée", getActiviteCourante().getNomActivite());
			} else if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
				getActiviteDao().supprimerActivite(getActiviteCourante().getIdActivite());
				messageInf = MessageUtils.getMessage("INF110", "supprimée", getActiviteCourante().getNomActivite());
				setActiviteCourante(null);
			}
			getTransaction().commitTransaction();
			setListeActivite(null);
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			if (!Const.CHAINE_VIDE.equals(messageInf))
				getTransaction().declarerErreur(messageInf);
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ACTIVITE Date de
	 * création : (05/07/11 10:44:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_ACTIVITE() {
		return "NOM_PB_ANNULER_ACTIVITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/07/11 10:44:10)
	 * 
	 */
	public boolean performPB_ANNULER_ACTIVITE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne la liste des activites.
	 * 
	 * @return listeActivite ArrayList(Activite)
	 */
	public ArrayList<Activite> getListeActivite() {
		return listeActivite;
	}

	/**
	 * Met a jour la liste des activités.
	 * 
	 * @param listeActivite
	 *            ArrayList(Activite)
	 */
	private void setListeActivite(ArrayList<Activite> listeActivite) {
		this.listeActivite = listeActivite;
	}

	/**
	 * Retourne l'activite courante.
	 * 
	 * @return activiteCourante Activite
	 */
	private Activite getActiviteCourante() {
		return activiteCourante;
	}

	/**
	 * Met a jour l'activité courante.
	 * 
	 * @param activiteCourante
	 *            Activite
	 */
	private void setActiviteCourante(Activite activiteCourante) {
		this.activiteCourante = activiteCourante;
	}

	/**
	 * Controle les zones saisies Date de création : (04/07/11 11:04:00)
	 * RG_PE_AC_A01
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {
		// RG_PE_AC_A01
		// ************************************
		// Verification lib competence not null
		// ************************************
		if (getZone(getNOM_EF_DESC_ACTIVITE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getNOM_EF_DESC_ACTIVITE());
			return false;
		}
		return true;
	}

	/**
	 * Controle les regles de gestion Date de création : (23/08/11) RG_PE_AC_A02
	 */
	private boolean performControlerRegleGestion(HttpServletRequest request) throws Exception {
		// RG_PE_AC_A02
		// **********************************************************************
		// Verification si suppression d'une activite utilisee sur une fiche de
		// poste
		// **********************************************************************
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)
				&& getActiviteFEDao().listerActiviteFEAvecActivite(getActiviteCourante().getIdActivite()).size() > 0) {
			// "ERR120",
			// "Impossible de supprimer @ actuellement utilisee par @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR120", "cette activite", "une fiche emploi"));
			setFocus(getNOM_EF_DESC_ACTIVITE());
			return false;
		}
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)
				&& getActiviteFPDao().listerActiviteFPAvecActivite(getActiviteCourante().getIdActivite()).size() > 0) {
			// "ERR120",
			// "Impossible de supprimer @ actuellement utilisee par @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR120", "cette activite", "une fiche de poste"));
			setFocus(getNOM_EF_DESC_ACTIVITE());
			return false;
		}
		return true;
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
		return getNOM_EF_DESC_ACTIVITE();
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PE-FE-ACTIVITE";
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (04/07/11 16:24:13)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// Si clic sur le bouton PB_ANNULER_ACTIVITE
			if (testerParametre(request, getNOM_PB_ANNULER_ACTIVITE())) {
				return performPB_ANNULER_ACTIVITE(request);
			}

			// Si clic sur le bouton PB_VALIDER_ACTIVITE
			if (testerParametre(request, getNOM_PB_VALIDER_ACTIVITE())) {
				return performPB_VALIDER_ACTIVITE(request);
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
	 * Constructeur du process OePOSTEFEActivite. Date de création : (21/10/11
	 * 14:52:03)
	 * 
	 */
	public OePOSTEFEActivite() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/10/11 14:52:03)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFEActivite.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_ACTI(int i) {
		return "NOM_ST_ID_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ID_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_ACTI(int i) {
		return getZone(getNOM_ST_ID_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la case
	 * a cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_ACTIVITE Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_EF_DESC_ACTIVITE() {
		return "NOM_EF_DESC_ACTIVITE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DESC_ACTIVITE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_EF_DESC_ACTIVITE() {
		return getZone(getNOM_EF_DESC_ACTIVITE());
	}

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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_DESC_ACTIVITE(), Const.CHAINE_VIDE);
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request) throws Exception {
		boolean auMoinsUneligneSelect = false;
		Activite acti = null;
		for (int j = 0; j < getListeActivite().size(); j++) {
			// on recupere la ligne concernée
			acti = (Activite) getListeActivite().get(j);
			Integer i = acti.getIdActivite();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}
		setActiviteCourante(acti);
		addZone(getNOM_EF_DESC_ACTIVITE(), acti.getNomActivite());
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request) throws Exception {
		boolean auMoinsUneligneSelect = false;
		Activite acti = null;
		for (int j = 0; j < getListeActivite().size(); j++) {
			// on recupere la ligne concernée
			acti = (Activite) getListeActivite().get(j);
			Integer i = acti.getIdActivite();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}
		setActiviteCourante(acti);
		addZone(getNOM_EF_DESC_ACTIVITE(), acti.getNomActivite());
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		return true;
	}

	public ActiviteDao getActiviteDao() {
		return activiteDao;
	}

	public void setActiviteDao(ActiviteDao activiteDao) {
		this.activiteDao = activiteDao;
	}

	public ActiviteFEDao getActiviteFEDao() {
		return activiteFEDao;
	}

	public void setActiviteFEDao(ActiviteFEDao activiteFEDao) {
		this.activiteFEDao = activiteFEDao;
	}

	public ActiviteFPDao getActiviteFPDao() {
		return activiteFPDao;
	}

	public void setActiviteFPDao(ActiviteFPDao activiteFPDao) {
		this.activiteFPDao = activiteFPDao;
	}
}
