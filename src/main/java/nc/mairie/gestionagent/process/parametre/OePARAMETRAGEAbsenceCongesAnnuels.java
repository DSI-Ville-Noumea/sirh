package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.RadiWSConsumer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAbsenceCongesAnnuels extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private ArrayList<RefTypeSaisiCongeAnnuelDto> listeTypeAbsence;
	private RefTypeSaisiCongeAnnuelDto typeAbsenceCourant;

	public String ACTION_MODIFICATION = "Modification d'un congé annuel :";
	public String ACTION_VISUALISATION = "Visualisation d'un congé annuel :";

	private AgentDao agentDao;

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

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		List<TypeAbsenceDto> listeTypeAbsence = consuAbs.getListeRefTypeAbsenceDto(EnumTypeGroupeAbsence.CONGES_ANNUELS
				.getValue());

		for (TypeAbsenceDto abs : listeTypeAbsence) {
			getListeTypeAbsence().add(abs.getTypeSaisiCongeAnnuelDto());
		}
		for (RefTypeSaisiCongeAnnuelDto type : getListeTypeAbsence()) {
			Integer i = type.getIdRefTypeSaisiCongeAnnuel();
			addZone(getNOM_ST_CODE_CONGE(i), type.getCodeBaseHoraireAbsence());
			addZone(getNOM_ST_DESCRIPTION(i), type.getDescription());
			addZone(getNOM_ST_QUOTA_MULTIPLE(i), type.getQuotaMultiple() == null ? Const.CHAINE_VIDE : type
					.getQuotaMultiple().toString());

		}

	}

	/**
	 * Constructeur du process OePARAMETRAGEAbsence. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAbsenceCongesAnnuels() {
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

			// Si clic sur le bouton PB_VALIDER_CONGES
			if (testerParametre(request, getNOM_PB_VALIDER_CONGES())) {
				return performPB_VALIDER_CONGES(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur les boutons du tableau
			for (RefTypeSaisiCongeAnnuelDto abs : getListeTypeAbsence()) {
				int indiceAbs = abs.getIdRefTypeSaisiCongeAnnuel();
				// Si clic sur le bouton PB_MODIFIER_CONGES
				if (testerParametre(request, getNOM_PB_MODIFIER_CONGES(indiceAbs))) {
					return performPB_MODIFIER_CONGES(request, indiceAbs);
				}
				// Si clic sur le bouton PB_VISUALISATION
				if (testerParametre(request, getNOM_PB_VISUALISATION(indiceAbs))) {
					return performPB_VISUALISATION(request, indiceAbs);
				}
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
		return "OePARAMETRAGEAbsenceCongesAnnuels.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-ABS-CONG";
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
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public ArrayList<RefTypeSaisiCongeAnnuelDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<RefTypeSaisiCongeAnnuelDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<RefTypeSaisiCongeAnnuelDto> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_ST_CODE_CONGE(int i) {
		return "NOM_ST_CODE_CONGE_" + i;
	}

	public String getVAL_ST_CODE_CONGE(int i) {
		return getZone(getNOM_ST_CODE_CONGE(i));
	}

	public String getNOM_ST_DESCRIPTION(int i) {
		return "NOM_ST_DESCRIPTION_" + i;
	}

	public String getVAL_ST_DESCRIPTION(int i) {
		return getZone(getNOM_ST_DESCRIPTION(i));
	}

	public String getNOM_ST_QUOTA_MULTIPLE(int i) {
		return "NOM_ST_QUOTA_MULTIPLE_" + i;
	}

	public String getVAL_ST_QUOTA_MULTIPLE(int i) {
		return getZone(getNOM_ST_QUOTA_MULTIPLE(i));
	}

	public String getNOM_PB_MODIFIER_CONGES(int i) {
		return "NOM_PB_MODIFIER_CONGES_" + i;
	}

	public boolean performPB_MODIFIER_CONGES(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		// on recupere la demande
		RefTypeSaisiCongeAnnuelDto aChercher = new RefTypeSaisiCongeAnnuelDto();
		aChercher.setIdRefTypeSaisiCongeAnnuel(idDemande);
		RefTypeSaisiCongeAnnuelDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeAbsenceCourant(type);

		addZone(getNOM_ST_DESCRIPTION(), type.getDescription() == null ? Const.CHAINE_VIDE : type.getDescription());
		addZone(getNOM_ST_QUOTA_MULTIPLE(), type.getQuotaMultiple() == null ? Const.CHAINE_VIDE : type
				.getQuotaMultiple().toString());
		addZone(getNOM_RG_DECOMPTE_SAMEDI(), type.isDecompteSamedi() ? getNOM_RB_DECOMPTE_SAMEDI_OUI()
				: getNOM_RB_DECOMPTE_SAMEDI_NON());
		addZone(getNOM_RG_CONSECUTIF(), type.isConsecutif() ? getNOM_RB_CONSECUTIF_OUI() : getNOM_RB_CONSECUTIF_NON());
		addZone(getNOM_RG_DATE_DEBUT(), getNOM_RB_DATE_DEBUT_OUI());
		addZone(getNOM_RG_DATE_FIN(), type.isCalendarDateFin() ? getNOM_RB_DATE_FIN_OUI() : getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_DATE_REPRISE(), type.isCalendarDateReprise() ? getNOM_RB_DATE_REPRISE_OUI()
				: getNOM_RB_DATE_REPRISE_NON());
		addZone(getNOM_RG_AM_PM(), type.isChkDateDebut() ? getNOM_RB_AM_PM_OUI() : getNOM_RB_AM_PM_NON());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_DESCRIPTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_QUOTA_MULTIPLE(), Const.CHAINE_VIDE);

		addZone(getNOM_RG_DECOMPTE_SAMEDI(), getNOM_RB_DECOMPTE_SAMEDI_NON());
		addZone(getNOM_RG_CONSECUTIF(), getNOM_RB_CONSECUTIF_NON());
		addZone(getNOM_RG_DATE_DEBUT(), getNOM_RB_DATE_DEBUT_OUI());
		addZone(getNOM_RG_DATE_FIN(), getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_DATE_REPRISE(), getNOM_RB_DATE_REPRISE_NON());
		addZone(getNOM_RG_AM_PM(), getNOM_RB_AM_PM_NON());

		setTypeAbsenceCourant(null);
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);

		// on recupere la demande
		RefTypeSaisiCongeAnnuelDto aChercher = new RefTypeSaisiCongeAnnuelDto();
		aChercher.setIdRefTypeSaisiCongeAnnuel(indiceEltAConsulter);
		RefTypeSaisiCongeAnnuelDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeAbsenceCourant(type);

		addZone(getNOM_ST_DESCRIPTION(), type.getDescription() == null ? Const.CHAINE_VIDE : type.getDescription());
		addZone(getNOM_ST_QUOTA_MULTIPLE(), type.getQuotaMultiple() == null ? Const.CHAINE_VIDE : type
				.getQuotaMultiple().toString());
		addZone(getNOM_RG_DECOMPTE_SAMEDI(), type.isDecompteSamedi() ? getNOM_RB_DECOMPTE_SAMEDI_OUI()
				: getNOM_RB_DECOMPTE_SAMEDI_NON());
		addZone(getNOM_RG_CONSECUTIF(), type.isConsecutif() ? getNOM_RB_CONSECUTIF_OUI() : getNOM_RB_CONSECUTIF_NON());
		addZone(getNOM_RG_DATE_DEBUT(), getNOM_RB_DATE_DEBUT_OUI());
		addZone(getNOM_RG_DATE_FIN(), type.isCalendarDateFin() ? getNOM_RB_DATE_FIN_OUI() : getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_DATE_REPRISE(), type.isCalendarDateReprise() ? getNOM_RB_DATE_REPRISE_OUI()
				: getNOM_RB_DATE_REPRISE_NON());
		addZone(getNOM_RG_AM_PM(), type.isChkDateDebut() ? getNOM_RB_AM_PM_OUI() : getNOM_RB_AM_PM_NON());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		return true;
	}

	public String getNOM_RG_DATE_FIN() {
		return "NOM_RG_DATE_FIN";
	}

	public String getVAL_RG_DATE_FIN() {
		return getZone(getNOM_RG_DATE_FIN());
	}

	public String getNOM_RB_DATE_FIN_OUI() {
		return "NOM_RB_DATE_FIN_OUI";
	}

	public String getNOM_RB_DATE_FIN_NON() {
		return "NOM_RB_DATE_FIN_NON";
	}

	public String getNOM_PB_VALIDER_CONGES() {
		return "NOM_PB_VALIDER_CONGES";
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		RadiWSConsumer radiConsu = new RadiWSConsumer();
		LightUserDto user = radiConsu.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(
							radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	public boolean performPB_VALIDER_CONGES(HttpServletRequest request) throws Exception {
		if (getTypeAbsenceCourant() == null) {
			// "ERR804",
			// "Une erreur est survenue dans la sauvegarde d'un type d'absence. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR804"));
			return false;
		}

		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {

			// vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			getTypeAbsenceCourant().setCalendarDateDebut(true);
			getTypeAbsenceCourant().setCalendarDateFin(
					getVAL_RG_DATE_FIN().equals(getNOM_RB_DATE_FIN_OUI()) ? true : false);
			getTypeAbsenceCourant().setCalendarDateReprise(
					getVAL_RG_DATE_REPRISE().equals(getNOM_RB_DATE_REPRISE_OUI()) ? true : false);
			getTypeAbsenceCourant().setChkDateDebut(getVAL_RG_AM_PM().equals(getNOM_RB_AM_PM_OUI()) ? true : false);
			getTypeAbsenceCourant().setChkDateFin(getVAL_RG_AM_PM().equals(getNOM_RB_AM_PM_OUI()) ? true : false);
			getTypeAbsenceCourant().setConsecutif(
					getVAL_RG_CONSECUTIF().equals(getNOM_RB_CONSECUTIF_OUI()) ? true : false);
			getTypeAbsenceCourant().setDecompteSamedi(
					getVAL_RG_DECOMPTE_SAMEDI().equals(getNOM_RB_DECOMPTE_SAMEDI_OUI()) ? true : false);
			getTypeAbsenceCourant().setDescription(
					getVAL_ST_DESCRIPTION().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_DESCRIPTION());
			getTypeAbsenceCourant().setQuotaMultiple(
					getVAL_ST_QUOTA_MULTIPLE().equals(Const.CHAINE_VIDE) ? null : Integer
							.valueOf(getVAL_ST_QUOTA_MULTIPLE()));

			// envoyer l'info aux WS
			TypeAbsenceDto envoi = new TypeAbsenceDto();
			envoi.setTypeSaisiCongeAnnuelDto(getTypeAbsenceCourant());
			RefGroupeAbsenceDto groupe = new RefGroupeAbsenceDto();
			groupe.setIdRefGroupeAbsence(EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue());
			envoi.setGroupeAbsence(groupe);
			envoi.setIdRefTypeAbsence(EnumTypeAbsence.CONGE.getCode());
			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
					.deepSerialize(envoi);

			SirhAbsWSConsumer t = new SirhAbsWSConsumer();
			ReturnMessageDto srm = t.saveTypeAbsence(agentConnecte.getIdAgent(), json);

			if (srm.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur(err);
				return false;
			}
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		// on vide la liste afin qu'elle soit re-affichée
		getListeTypeAbsence().clear();
		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		// format QUOTA
		if (!(Const.CHAINE_VIDE).equals(getVAL_ST_QUOTA_MULTIPLE())
				&& !Services.estNumerique(getVAL_ST_QUOTA_MULTIPLE())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "quota multiple"));
			return false;
		}
		return true;
	}

	public String getNOM_RG_AM_PM() {
		return "NOM_RG_AM_PM";
	}

	public String getVAL_RG_AM_PM() {
		return getZone(getNOM_RG_AM_PM());
	}

	public String getNOM_RB_AM_PM_OUI() {
		return "NOM_RB_AM_PM_OUI";
	}

	public String getNOM_RB_AM_PM_NON() {
		return "NOM_RB_AM_PM_NON";
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public RefTypeSaisiCongeAnnuelDto getTypeAbsenceCourant() {
		return typeAbsenceCourant;
	}

	public void setTypeAbsenceCourant(RefTypeSaisiCongeAnnuelDto typeAbsenceCourant) {
		this.typeAbsenceCourant = typeAbsenceCourant;
	}

	public String getNOM_ST_DESCRIPTION() {
		return "NOM_ST_DESCRIPTION";
	}

	public String getVAL_ST_DESCRIPTION() {
		return getZone(getNOM_ST_DESCRIPTION());
	}

	public String getNOM_ST_QUOTA_MULTIPLE() {
		return "NOM_ST_QUOTA_MULTIPLE";
	}

	public String getVAL_ST_QUOTA_MULTIPLE() {
		return getZone(getNOM_ST_QUOTA_MULTIPLE());
	}

	public String getNOM_RG_DECOMPTE_SAMEDI() {
		return "NOM_RG_DECOMPTE_SAMEDI";
	}

	public String getVAL_RG_DECOMPTE_SAMEDI() {
		return getZone(getNOM_RG_DECOMPTE_SAMEDI());
	}

	public String getNOM_RB_DECOMPTE_SAMEDI_OUI() {
		return "NOM_RB_DECOMPTE_SAMEDI_OUI";
	}

	public String getNOM_RB_DECOMPTE_SAMEDI_NON() {
		return "NOM_RB_DECOMPTE_SAMEDI_NON";
	}

	public String getNOM_RG_CONSECUTIF() {
		return "NOM_RG_CONSECUTIF";
	}

	public String getVAL_RG_CONSECUTIF() {
		return getZone(getNOM_RG_CONSECUTIF());
	}

	public String getNOM_RB_CONSECUTIF_OUI() {
		return "NOM_RB_CONSECUTIF_OUI";
	}

	public String getNOM_RB_CONSECUTIF_NON() {
		return "NOM_RB_CONSECUTIF_NON";
	}

	public String getNOM_RG_DATE_DEBUT() {
		return "NOM_RG_DATE_DEBUT";
	}

	public String getVAL_RG_DATE_DEBUT() {
		return getZone(getNOM_RG_DATE_DEBUT());
	}

	public String getNOM_RB_DATE_DEBUT_OUI() {
		return "NOM_RB_DATE_DEBUT_OUI";
	}

	public String getNOM_RG_DATE_REPRISE() {
		return "NOM_RG_DATE_REPRISE";
	}

	public String getVAL_RG_DATE_REPRISE() {
		return getZone(getNOM_RG_DATE_REPRISE());
	}

	public String getNOM_RB_DATE_REPRISE_OUI() {
		return "NOM_RB_DATE_REPRISE_OUI";
	}

	public String getNOM_RB_DATE_REPRISE_NON() {
		return "NOM_RB_DATE_REPRISE_NON";
	}

}
