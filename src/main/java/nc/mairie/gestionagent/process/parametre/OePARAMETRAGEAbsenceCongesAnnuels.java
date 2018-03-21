package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.RefAlimCongesAnnuelsDto;
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
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;

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
	private ArrayList<RefAlimCongesAnnuelsDto> listeAlimMensuelle;
	private RefAlimCongesAnnuelsDto alimCongesAnnuelsCourant;

	public String ACTION_MODIFICATION = "Modification d'un congé annuel :";
	public String ACTION_VISUALISATION = "Visualisation d'un congé annuel :";
	public String ACTION_ALIM_MENSUELLE = "Visualisation des alimentations mensuelles d'un congé annuel";
	public String ACTION_MODIF_ALIM_MENSUELLE = "Modification des alimentations mensuelles d'un congé annuel";
	public String ACTION_CREATION_ALIM_MENSUELLE = "Création d'une alimentation mensuelle d'un congé annuel";

	private AgentDao agentDao;
	
	private IRadiService radiService;

	private IAbsService absService;

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
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {
		List<TypeAbsenceDto> listeTypeAbsence = absService.getListeRefTypeAbsenceDto(EnumTypeGroupeAbsence.CONGES_ANNUELS
				.getValue());

		ArrayList<RefTypeSaisiCongeAnnuelDto> liste = new ArrayList<RefTypeSaisiCongeAnnuelDto>();

		for (TypeAbsenceDto abs : listeTypeAbsence) {
			liste.add(abs.getTypeSaisiCongeAnnuelDto());
		}
		setListeTypeAbsence(liste);
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
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
				// Si clic sur le bouton PB_ALIM_MENSUELLE
				if (testerParametre(request, getNOM_PB_ALIM_MENSUELLE(indiceAbs))) {
					return performPB_ALIM_MENSUELLE(request, indiceAbs);
				}
			}

			// Si clic sur les boutons du tableau des alim mensuelles
			for (RefAlimCongesAnnuelsDto alim : getListeAlimMensuelle()) {
				int indiceAnnee = alim.getAnnee();
				// Si clic sur le bouton PB_MODIFIER_ALIM_MENSUELLE
				if (testerParametre(request, getNOM_PB_MODIFIER_ALIM_MENSUELLE(indiceAnnee))) {
					return performPB_MODIFIER_ALIM_MENSUELLE(request, indiceAnnee);
				}

			}

			// Si clic sur le bouton PB_CREER_ALIM_MENSUELLE
			if (testerParametre(request, getNOM_PB_CREER_ALIM_MENSUELLE())) {
				return performPB_CREER_ALIM_MENSUELLE(request);
			}

			// Si clic sur le bouton PB_VALIDER_ALIM_MENSUELLE
			if (testerParametre(request, getNOM_PB_VALIDER_ALIM_MENSUELLE())) {
				return performPB_VALIDER_ALIM_MENSUELLE(request);
			}

			// Si clic sur le bouton PB_ANNULER_ALIM_MENSUELLE
			if (testerParametre(request, getNOM_PB_ANNULER_ALIM_MENSUELLE())) {
				return performPB_ANNULER_ALIM_MENSUELLE(request);
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
	 *            focus à  définir.
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(
							radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
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

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			getTypeAbsenceCourant().setCalendarDateDebut(true);
			getTypeAbsenceCourant().setCalendarDateFin(
					getVAL_RG_DATE_FIN().equals(getNOM_RB_DATE_FIN_OUI()) ? true : false);
			getTypeAbsenceCourant().setCalendarDateReprise(
					getVAL_RG_DATE_REPRISE().equals(getNOM_RB_DATE_REPRISE_OUI()) ? true : false);
			getTypeAbsenceCourant().setChkDateDebut(getVAL_RG_AM_PM().equals(getNOM_RB_AM_PM_OUI()) ? true : false);
			getTypeAbsenceCourant().setChkDateFin(getVAL_RG_AM_PM().equals(getNOM_RB_AM_PM_OUI()) ? true : false);
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

			ReturnMessageDto srm = absService.saveTypeAbsence(agentConnecte.getIdAgent(), json);

			if (srm.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
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

	public String getNOM_PB_ALIM_MENSUELLE(int i) {
		return "NOM_PB_ALIM_MENSUELLE" + i;
	}

	public boolean performPB_ALIM_MENSUELLE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);

		// on recupere l'alim
		RefTypeSaisiCongeAnnuelDto aChercher = new RefTypeSaisiCongeAnnuelDto();
		aChercher.setIdRefTypeSaisiCongeAnnuel(indiceEltAConsulter);
		RefTypeSaisiCongeAnnuelDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeAbsenceCourant(type);

		initialiseListeAlimMensuelle(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_ALIM_MENSUELLE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseListeAlimMensuelle(HttpServletRequest request) {
		List<RefAlimCongesAnnuelsDto> listeAlimMensuelle = absService
				.getListeRefAlimCongesAnnuels(getTypeAbsenceCourant().getIdRefTypeSaisiCongeAnnuel());
		setListeAlimMensuelle((ArrayList<RefAlimCongesAnnuelsDto>) listeAlimMensuelle);

		for (RefAlimCongesAnnuelsDto type : getListeAlimMensuelle()) {
			Integer i = type.getAnnee();
			addZone(getNOM_ST_ANNEE_ALIM(i), type.getAnnee().toString());
			addZone(getNOM_ST_JANVIER_ALIM(i), type.getJanvier().toString());
			addZone(getNOM_ST_FEVRIER_ALIM(i), type.getFevrier().toString());
			addZone(getNOM_ST_MARS_ALIM(i), type.getMars().toString());
			addZone(getNOM_ST_AVRIL_ALIM(i), type.getAvril().toString());
			addZone(getNOM_ST_MAI_ALIM(i), type.getMai().toString());
			addZone(getNOM_ST_JUIN_ALIM(i), type.getJuin().toString());
			addZone(getNOM_ST_JUILLET_ALIM(i), type.getJuillet().toString());
			addZone(getNOM_ST_AOUT_ALIM(i), type.getAout().toString());
			addZone(getNOM_ST_SEPTEMBRE_ALIM(i), type.getSeptembre().toString());
			addZone(getNOM_ST_OCTOBRE_ALIM(i), type.getOctobre().toString());
			addZone(getNOM_ST_NOVEMBRE_ALIM(i), type.getNovembre().toString());
			addZone(getNOM_ST_DECEMBRE_ALIM(i), type.getDecembre().toString());
		}
	}

	public ArrayList<RefAlimCongesAnnuelsDto> getListeAlimMensuelle() {
		return listeAlimMensuelle;
	}

	public void setListeAlimMensuelle(ArrayList<RefAlimCongesAnnuelsDto> listeAlimMensuelle) {
		this.listeAlimMensuelle = listeAlimMensuelle;
	}

	public String getNOM_ST_ANNEE_ALIM(int i) {
		return "NOM_ST_ANNEE_ALIM_" + i;
	}

	public String getVAL_ST_ANNEE_ALIM(int i) {
		return getZone(getNOM_ST_ANNEE_ALIM(i));
	}

	public String getNOM_ST_JANVIER_ALIM(int i) {
		return "NOM_ST_JANVIER_ALIM_" + i;
	}

	public String getVAL_ST_JANVIER_ALIM(int i) {
		return getZone(getNOM_ST_JANVIER_ALIM(i));
	}

	public String getNOM_ST_FEVRIER_ALIM(int i) {
		return "NOM_ST_FEVRIER_ALIM_" + i;
	}

	public String getVAL_ST_FEVRIER_ALIM(int i) {
		return getZone(getNOM_ST_FEVRIER_ALIM(i));
	}

	public String getNOM_ST_MARS_ALIM(int i) {
		return "NOM_ST_MARS_ALIM_" + i;
	}

	public String getVAL_ST_MARS_ALIM(int i) {
		return getZone(getNOM_ST_MARS_ALIM(i));
	}

	public String getNOM_ST_AVRIL_ALIM(int i) {
		return "NOM_ST_AVRIL_ALIM_" + i;
	}

	public String getVAL_ST_AVRIL_ALIM(int i) {
		return getZone(getNOM_ST_AVRIL_ALIM(i));
	}

	public String getNOM_ST_MAI_ALIM(int i) {
		return "NOM_ST_MAI_ALIM_" + i;
	}

	public String getVAL_ST_MAI_ALIM(int i) {
		return getZone(getNOM_ST_MAI_ALIM(i));
	}

	public String getNOM_ST_JUIN_ALIM(int i) {
		return "NOM_ST_JUIN_ALIM_" + i;
	}

	public String getVAL_ST_JUIN_ALIM(int i) {
		return getZone(getNOM_ST_JUIN_ALIM(i));
	}

	public String getNOM_ST_JUILLET_ALIM(int i) {
		return "NOM_ST_JUILLET_ALIM_" + i;
	}

	public String getVAL_ST_JUILLET_ALIM(int i) {
		return getZone(getNOM_ST_JUILLET_ALIM(i));
	}

	public String getNOM_ST_AOUT_ALIM(int i) {
		return "NOM_ST_AOUT_ALIM_" + i;
	}

	public String getVAL_ST_AOUT_ALIM(int i) {
		return getZone(getNOM_ST_AOUT_ALIM(i));
	}

	public String getNOM_ST_SEPTEMBRE_ALIM(int i) {
		return "NOM_ST_SEPTEMBRE_ALIM_" + i;
	}

	public String getVAL_ST_SEPTEMBRE_ALIM(int i) {
		return getZone(getNOM_ST_SEPTEMBRE_ALIM(i));
	}

	public String getNOM_ST_OCTOBRE_ALIM(int i) {
		return "NOM_ST_OCTOBRE_ALIM_" + i;
	}

	public String getVAL_ST_OCTOBRE_ALIM(int i) {
		return getZone(getNOM_ST_OCTOBRE_ALIM(i));
	}

	public String getNOM_ST_NOVEMBRE_ALIM(int i) {
		return "NOM_ST_NOVEMBRE_ALIM_" + i;
	}

	public String getVAL_ST_NOVEMBRE_ALIM(int i) {
		return getZone(getNOM_ST_NOVEMBRE_ALIM(i));
	}

	public String getNOM_ST_DECEMBRE_ALIM(int i) {
		return "NOM_ST_DECEMBRE_ALIM_" + i;
	}

	public String getVAL_ST_DECEMBRE_ALIM(int i) {
		return getZone(getNOM_ST_DECEMBRE_ALIM(i));
	}

	public String getNOM_PB_MODIFIER_ALIM_MENSUELLE(int i) {
		return "NOM_PB_MODIFIER_ALIM_MENSUELLE" + i;
	}

	public boolean performPB_MODIFIER_ALIM_MENSUELLE(HttpServletRequest request, int indiceEltAConsulter)
			throws Exception {
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), Const.CHAINE_VIDE);
		viderZoneSaisieAlimMensuelle(request);

		// on recupere l'alim
		RefAlimCongesAnnuelsDto aChercher = new RefAlimCongesAnnuelsDto();
		aChercher.setAnnee(indiceEltAConsulter);
		RefAlimCongesAnnuelsDto type = getListeAlimMensuelle().get(getListeAlimMensuelle().indexOf(aChercher));
		setAlimCongesAnnuelsCourant(type);

		addZone(getNOM_EF_ANNEE_ALIM(), getAlimCongesAnnuelsCourant().getAnnee().toString());
		addZone(getNOM_EF_JANVIER_ALIM(), getAlimCongesAnnuelsCourant().getJanvier().toString());
		addZone(getNOM_EF_FEVRIER_ALIM(), getAlimCongesAnnuelsCourant().getFevrier().toString());
		addZone(getNOM_EF_MARS_ALIM(), getAlimCongesAnnuelsCourant().getMars().toString());
		addZone(getNOM_EF_AVRIL_ALIM(), getAlimCongesAnnuelsCourant().getAvril().toString());
		addZone(getNOM_EF_MAI_ALIM(), getAlimCongesAnnuelsCourant().getMai().toString());
		addZone(getNOM_EF_JUIN_ALIM(), getAlimCongesAnnuelsCourant().getJuin().toString());
		addZone(getNOM_EF_JUILLET_ALIM(), getAlimCongesAnnuelsCourant().getJuillet().toString());
		addZone(getNOM_EF_AOUT_ALIM(), getAlimCongesAnnuelsCourant().getAout().toString());
		addZone(getNOM_EF_SEPTEMBRE_ALIM(), getAlimCongesAnnuelsCourant().getSeptembre().toString());
		addZone(getNOM_EF_OCTOBRE_ALIM(), getAlimCongesAnnuelsCourant().getOctobre().toString());
		addZone(getNOM_EF_NOVEMBRE_ALIM(), getAlimCongesAnnuelsCourant().getNovembre().toString());
		addZone(getNOM_EF_DECEMBRE_ALIM(), getAlimCongesAnnuelsCourant().getDecembre().toString());

		// On nomme l'action
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), ACTION_MODIF_ALIM_MENSUELLE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void viderZoneSaisieAlimMensuelle(HttpServletRequest request) {

		addZone(getNOM_EF_ANNEE_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_JANVIER_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_FEVRIER_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MARS_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_AVRIL_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MAI_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_JUIN_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_JUILLET_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_AOUT_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SEPTEMBRE_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_OCTOBRE_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOVEMBRE_ALIM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DECEMBRE_ALIM(), Const.CHAINE_VIDE);

		setAlimCongesAnnuelsCourant(null);

	}

	public String getNOM_ST_ACTION_ALIM_MANUELLE() {
		return "NOM_ST_ACTION_ALIM_MANUELLE";
	}

	public String getVAL_ST_ACTION_ALIM_MANUELLE() {
		return getZone(getNOM_ST_ACTION_ALIM_MANUELLE());
	}

	public String getNOM_PB_CREER_ALIM_MENSUELLE() {
		return "NOM_PB_CREER_ALIM_MENSUELLE";
	}

	public boolean performPB_CREER_ALIM_MENSUELLE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), Const.CHAINE_VIDE);
		viderZoneSaisieAlimMensuelle(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), ACTION_CREATION_ALIM_MENSUELLE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER_ALIM_MENSUELLE() {
		return "NOM_PB_ANNULER_ALIM_MENSUELLE";
	}

	public boolean performPB_ANNULER_ALIM_MENSUELLE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), Const.CHAINE_VIDE);
		viderZoneSaisieAlimMensuelle(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_VALIDER_ALIM_MENSUELLE() {
		return "NOM_PB_VALIDER_ALIM_MENSUELLE";
	}

	public boolean performPB_VALIDER_ALIM_MENSUELLE(HttpServletRequest request) throws Exception {

		// Vérification de la validité du formulaire
		if (!performControlerChampsAlimMensuelle(request))
			return false;

		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		if (getVAL_ST_ACTION_ALIM_MANUELLE().equals(ACTION_MODIF_ALIM_MENSUELLE)
				|| getVAL_ST_ACTION_ALIM_MANUELLE().equals(ACTION_CREATION_ALIM_MENSUELLE)) {

			RefAlimCongesAnnuelsDto dto = new RefAlimCongesAnnuelsDto();
			dto.setAnnee(Integer.valueOf(getVAL_EF_ANNEE_ALIM()));
			dto.setIdRefTypeSaisiCongeAnnuel(getTypeAbsenceCourant().getIdRefTypeSaisiCongeAnnuel());
			dto.setJanvier(Double.valueOf(getVAL_EF_JANVIER_ALIM()));
			dto.setFevrier(Double.valueOf(getVAL_EF_FEVRIER_ALIM()));
			dto.setMars(Double.valueOf(getVAL_EF_MARS_ALIM()));
			dto.setAvril(Double.valueOf(getVAL_EF_AVRIL_ALIM()));
			dto.setMai(Double.valueOf(getVAL_EF_MAI_ALIM()));
			dto.setJuin(Double.valueOf(getVAL_EF_JUIN_ALIM()));
			dto.setJuillet(Double.valueOf(getVAL_EF_JUILLET_ALIM()));
			dto.setAout(Double.valueOf(getVAL_EF_AOUT_ALIM()));
			dto.setSeptembre(Double.valueOf(getVAL_EF_SEPTEMBRE_ALIM()));
			dto.setOctobre(Double.valueOf(getVAL_EF_OCTOBRE_ALIM()));
			dto.setNovembre(Double.valueOf(getVAL_EF_NOVEMBRE_ALIM()));
			dto.setDecembre(Double.valueOf(getVAL_EF_DECEMBRE_ALIM()));

			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
					.deepSerialize(dto);

			ReturnMessageDto srm = absService.saveRefAlimMensuelle(agentConnecte.getIdAgent(), json);

			String err = Const.CHAINE_VIDE;
			String info = Const.CHAINE_VIDE;
			if (srm.getErrors().size() > 0) {
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
			}
			if (srm.getInfos().size() > 0) {
				for (String erreur : srm.getInfos()) {
					info += " " + erreur;
				}
			}

			if (!err.equals(Const.CHAINE_VIDE)) {
				err += info;
				getTransaction().declarerErreur("ERREUR : " + err);
				return false;
			}
			if (!info.equals(Const.CHAINE_VIDE)) {
				getTransaction().declarerErreur(info);
			}
		}

		// on recharge le tableau
		addZone(getNOM_ST_ACTION_ALIM_MANUELLE(), Const.CHAINE_VIDE);
		viderZoneSaisieAlimMensuelle(request);
		initialiseListeAlimMensuelle(request);
		return true;
	}

	private boolean performControlerChampsAlimMensuelle(HttpServletRequest request) {
		// Verification année not null
		if (getZone(getNOM_EF_ANNEE_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			return false;
		}
		// format année
		if (!Services.estNumerique(getVAL_EF_ANNEE_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
			return false;
		}
		// Verification janvier not null
		if (getZone(getNOM_EF_JANVIER_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "janvier"));
			return false;
		}
		// format janvier
		if (!Services.estFloat(getVAL_EF_JANVIER_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "janvier"));
			return false;
		}
		// Verification février not null
		if (getZone(getNOM_EF_FEVRIER_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "février"));
			return false;
		}
		// format année
		if (!Services.estFloat(getVAL_EF_FEVRIER_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "février"));
			return false;
		}
		// Verification mars not null
		if (getZone(getNOM_EF_MARS_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mars"));
			return false;
		}
		// format année
		if (!Services.estFloat(getVAL_EF_MARS_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "mars"));
			return false;
		}
		// Verification avril not null
		if (getZone(getNOM_EF_AVRIL_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "avril"));
			return false;
		}
		// format avril
		if (!Services.estFloat(getVAL_EF_AVRIL_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "avril"));
			return false;
		}
		// Verification mai not null
		if (getZone(getNOM_EF_MAI_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mai"));
			return false;
		}
		// format mai
		if (!Services.estFloat(getVAL_EF_MAI_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "mai"));
			return false;
		}
		// Verification juin not null
		if (getZone(getNOM_EF_JUIN_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "juin"));
			return false;
		}
		// format juin
		if (!Services.estFloat(getVAL_EF_JUIN_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "juin"));
			return false;
		}
		// Verification juillet not null
		if (getZone(getNOM_EF_JUILLET_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "juillet"));
			return false;
		}
		// format juillet
		if (!Services.estFloat(getVAL_EF_JUILLET_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "juillet"));
			return false;
		}
		// Verification aout not null
		if (getZone(getNOM_EF_AOUT_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "aout"));
			return false;
		}
		// format aout
		if (!Services.estFloat(getVAL_EF_AOUT_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "aout"));
			return false;
		}
		// Verification septembre not null
		if (getZone(getNOM_EF_SEPTEMBRE_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "septembre"));
			return false;
		}
		// format septembre
		if (!Services.estFloat(getVAL_EF_SEPTEMBRE_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "septembre"));
			return false;
		}
		// Verification octobre not null
		if (getZone(getNOM_EF_OCTOBRE_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "octobre"));
			return false;
		}
		// format octobre
		if (!Services.estFloat(getVAL_EF_OCTOBRE_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "octobre"));
			return false;
		}
		// Verification novembre not null
		if (getZone(getNOM_EF_NOVEMBRE_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "novembre"));
			return false;
		}
		// format novembre
		if (!Services.estFloat(getVAL_EF_NOVEMBRE_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "novembre"));
			return false;
		}
		// Verification décembre not null
		if (getZone(getNOM_EF_DECEMBRE_ALIM()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "décembre"));
			return false;
		}
		// format décembre
		if (!Services.estFloat(getVAL_EF_DECEMBRE_ALIM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "décembre"));
			return false;
		}

		return true;
	}

	public String getNOM_EF_JANVIER_ALIM() {
		return "NOM_EF_JANVIER_ALIM";
	}

	public String getVAL_EF_JANVIER_ALIM() {
		return getZone(getNOM_EF_JANVIER_ALIM());
	}

	public String getNOM_EF_FEVRIER_ALIM() {
		return "NOM_EF_FEVRIER_ALIM";
	}

	public String getVAL_EF_FEVRIER_ALIM() {
		return getZone(getNOM_EF_FEVRIER_ALIM());
	}

	public String getNOM_EF_MARS_ALIM() {
		return "NOM_EF_MARS_ALIM";
	}

	public String getVAL_EF_MARS_ALIM() {
		return getZone(getNOM_EF_MARS_ALIM());
	}

	public String getNOM_EF_AVRIL_ALIM() {
		return "NOM_EF_AVRIL_ALIM";
	}

	public String getVAL_EF_AVRIL_ALIM() {
		return getZone(getNOM_EF_AVRIL_ALIM());
	}

	public String getNOM_EF_MAI_ALIM() {
		return "NOM_EF_MAI_ALIM";
	}

	public String getVAL_EF_MAI_ALIM() {
		return getZone(getNOM_EF_MAI_ALIM());
	}

	public String getNOM_EF_JUIN_ALIM() {
		return "NOM_EF_JUIN_ALIM";
	}

	public String getVAL_EF_JUIN_ALIM() {
		return getZone(getNOM_EF_JUIN_ALIM());
	}

	public String getNOM_EF_JUILLET_ALIM() {
		return "NOM_EF_JUILLET_ALIM";
	}

	public String getVAL_EF_JUILLET_ALIM() {
		return getZone(getNOM_EF_JUILLET_ALIM());
	}

	public String getNOM_EF_AOUT_ALIM() {
		return "NOM_EF_AOUT_ALIM";
	}

	public String getVAL_EF_AOUT_ALIM() {
		return getZone(getNOM_EF_AOUT_ALIM());
	}

	public String getNOM_EF_SEPTEMBRE_ALIM() {
		return "NOM_EF_SEPTEMBRE_ALIM";
	}

	public String getVAL_EF_SEPTEMBRE_ALIM() {
		return getZone(getNOM_EF_SEPTEMBRE_ALIM());
	}

	public String getNOM_EF_OCTOBRE_ALIM() {
		return "NOM_EF_OCTOBRE_ALIM";
	}

	public String getVAL_EF_OCTOBRE_ALIM() {
		return getZone(getNOM_EF_OCTOBRE_ALIM());
	}

	public String getNOM_EF_NOVEMBRE_ALIM() {
		return "NOM_EF_NOVEMBRE_ALIM";
	}

	public String getVAL_EF_NOVEMBRE_ALIM() {
		return getZone(getNOM_EF_NOVEMBRE_ALIM());
	}

	public String getNOM_EF_DECEMBRE_ALIM() {
		return "NOM_EF_DECEMBRE_ALIM";
	}

	public String getVAL_EF_DECEMBRE_ALIM() {
		return getZone(getNOM_EF_DECEMBRE_ALIM());
	}

	public String getNOM_EF_ANNEE_ALIM() {
		return "NOM_EF_ANNEE_ALIM";
	}

	public String getVAL_EF_ANNEE_ALIM() {
		return getZone(getNOM_EF_ANNEE_ALIM());
	}

	public RefAlimCongesAnnuelsDto getAlimCongesAnnuelsCourant() {
		return alimCongesAnnuelsCourant;
	}

	public void setAlimCongesAnnuelsCourant(RefAlimCongesAnnuelsDto alimCongesAnnuelsCourant) {
		this.alimCongesAnnuelsCourant = alimCongesAnnuelsCourant;
	}
}
