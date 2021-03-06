package nc.mairie.gestionagent.process.election;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.AgentOrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.vo.VoAgentCompteur;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 *
 */
public class OeELECSaisieCompteurA52 extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT_CREATE = 1;
	private Logger logger = LoggerFactory.getLogger(OeELECSaisieCompteurA52.class);

	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicaleExistante;
	private OrganisationSyndicaleDto organisationCourante;
	private ArrayList<CompteurDto> listeCompteur;
	private CompteurDto compteurCourant;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale;
	private ArrayList<AgentOrganisationSyndicaleDto> listeRepresentant;
	private String[] LB_OS;
	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_MODIFICATION = "Modification d'un compteur -";
	public String ACTION_CREATION = "Création d'un compteur -";
	public String ACTION_VISUALISATION = "Consultation d'un compteur -";
	public String ACTION_VISU_REPRESENTANT = "Visualisation des représentants -";
	public String ACTION_MODIFICATION_REPRESENTANT = "Modification des représentants -";
	public String ACTION_CREATION_REPRE = "Création d'un représentant -";
	public String ACTION_MODIFICATION_REPRE = "Modification d'un représentant -";
	public String ACTION_VISUALISATION_COMPTEUR = "Visu compteur -";
	public String ACTION_MODIFIER_COMPTEUR = "Modif compteur";

	private AgentDao agentDao;

	private IRadiService radiService;

	private IAbsService absService;

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA52.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-ELEC-COMPTEUR";
	}

	@Override
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

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		initialiseListeOSExistant(request);

		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATE(), agt.getNomatr().toString());
			}
		}
	}

	private void initialiseListeOSExistant(HttpServletRequest request) {
		ArrayList<OrganisationSyndicaleDto> listeOS = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOSCompteursA52();
		logger.debug("Taille liste des AS ASA A52 : " + listeOS.size());

		for (OrganisationSyndicaleDto vo : listeOS) {
			Integer indiceLigne = vo.getIdOrganisation();
			addZone(getNOM_ST_SIGLE_OS(indiceLigne), vo.getSigle());
			addZone(getNOM_ST_OS(indiceLigne), vo.getLibelle());
		}
		setListeOrganisationSyndicaleExistante(listeOS);
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

	private void initialiseListeDeroulante() {
		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) absService.getListeMotifCompteur(EnumTypeAbsence.ASA_A52.getCode());
			setListeMotifCompteur(listeMotifs);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (MotifCompteurDto motif : listeMotifs) {
				String ligne[] = { motif.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		}

		// Si liste organisations syndicales vide alors affectation
		if (getLB_OS() == LBVide) {
			ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOrganisationSyndicale();
			setListeOrganisationSyndicale(listeOrga);

			int[] tailles = { 20, 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (OrganisationSyndicaleDto os : listeOrga) {
				String ligne[] = { os.getSigle(), os.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_OS(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
		}
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			for (int j = 0; j < getListeOrganisationSyndicaleExistante().size(); j++) {
				Integer i = getListeOrganisationSyndicaleExistante().get(j).getIdOrganisation();
				// Si clic sur le bouton PB_MODIFIER
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
				// Si clic sur le bouton PB_VISUALISER
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
				// Si clic sur le bouton PB_VISU_REPRESENTANT
				if (testerParametre(request, getNOM_PB_VISU_REPRESENTANT(i))) {
					return performPB_VISU_REPRESENTANT(request, i);
				}
				// Si clic sur le bouton PB_MODIFIER_REPRESENTANT
				if (testerParametre(request, getNOM_PB_MODIFIER_REPRESENTANT(i))) {
					return performPB_MODIFIER_REPRESENTANT(request, i);
				}
			}

			for (int j = 0; j < getListeCompteur().size(); j++) {
				Integer i = getListeCompteur().get(j).getIdCompteur();
				// Si clic sur le bouton PB_MODIFIER_COMPTEUR
				if (testerParametre(request, getNOM_PB_MODIFIER_COMPTEUR(i))) {
					return performPB_MODIFIER_COMPTEUR(request, i);
				}
				// Si clic sur le bouton PB_VISUALISATION_COMPTEUR
				if (testerParametre(request, getNOM_PB_VISUALISATION_COMPTEUR(i))) {
					return performPB_VISUALISATION_COMPTEUR(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_AJOUTER_REPRESENTANT())) {
				return performPB_AJOUTER_REPRESENTANT(request);
			}

			for (int j = 0; j < getListeRepresentant().size(); j++) {
				Integer i = getListeRepresentant().get(j).getIdAgent();
				// Si clic sur le bouton PB_MODIFIER_REPRE
				if (testerParametre(request, getNOM_PB_MODIFIER_REPRE(i))) {
					return performPB_MODIFIER_REPRE(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER_REPRE
				if (testerParametre(request, getNOM_PB_SUPPRIMER_REPRE(i))) {
					return performPB_SUPPRIMER_REPRE(request, i);
				}
			}

			// Si clic sur le bouton PB_PB_RECHERCHER_AGENT_CREATE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATE())) {
				return performPB_RECHERCHER_AGENT_CREATE(request);
			}

			// Si clic sur le bouton PB_CREATE
			if (testerParametre(request, getNOM_PB_CREATE())) {
				return performPB_CREATE(request);
			}

			// Si clic sur le bouton PB_VALIDER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_VALIDER_REPRESENTANT())) {
				return performPB_VALIDER_REPRESENTANT(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_SIGLE_OS(int i) {
		return "NOM_ST_SIGLE_OS" + i;
	}

	public String getVAL_ST_SIGLE_OS(int i) {
		return getZone(getNOM_ST_SIGLE_OS(i));
	}

	public String getNOM_ST_OS(int i) {
		return "NOM_ST_OS" + i;
	}

	public String getVAL_ST_OS(int i) {
		return getZone(getNOM_ST_OS(i));
	}

	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_ST_NB_HEURES(int i) {
		return "NOM_ST_NB_HEURES" + i;
	}

	public String getVAL_ST_NB_HEURES(int i) {
		return getZone(getNOM_ST_NB_HEURES(i));
	}

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NB_HEURES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NB_MINUTES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
		addZone(getNOM_ST_AGENT_CREATE(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_REPRESENTANT_INACTIF(), getNOM_RB_NON());
	}

	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_COMPTEUR(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);

		OrganisationSyndicaleDto dto = new OrganisationSyndicaleDto();
		dto.setIdOrganisation(indiceEltAModifier);

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicaleExistante().get(getListeOrganisationSyndicaleExistante().indexOf(dto));
		setOrganisationCourante(osCourant);

		setListeCompteur((ArrayList<CompteurDto>) absService.getListeCompteursA52(getOrganisationCourante().getIdOrganisation()));
		afficheListeCompteur(request, getListeCompteur());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void afficheListeCompteur(HttpServletRequest request, List<CompteurDto> listeCompteursA52) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (CompteurDto vo : listeCompteursA52) {
			Integer indiceLigne = vo.getIdCompteur();
			addZone(getNOM_ST_DATE_DEBUT(indiceLigne), sdf.format(vo.getDateDebut()));
			addZone(getNOM_ST_DATE_FIN(indiceLigne), sdf.format(vo.getDateFin()));
			String soldeAsaA52Heure = (vo.getDureeAAjouter().intValue() / 60) == 0 ? Const.CHAINE_VIDE : vo.getDureeAAjouter().intValue() / 60 + "h ";
			String soldeAsaA52Minute = (vo.getDureeAAjouter().intValue() % 60) == 0 ? "&nbsp;" : vo.getDureeAAjouter().intValue() % 60 + "m";
			addZone(getNOM_ST_NB_HEURES(indiceLigne), soldeAsaA52Heure + soldeAsaA52Minute);
			addZone(getNOM_ST_MOTIF(indiceLigne), vo.getMotifCompteurDto() == null ? Const.CHAINE_VIDE : vo.getMotifCompteurDto().getLibelle());
		}
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_COMPTEUR(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);

		OrganisationSyndicaleDto dto = new OrganisationSyndicaleDto();
		dto.setIdOrganisation(indiceEltAConsulter);

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicaleExistante().get(getListeOrganisationSyndicaleExistante().indexOf(dto));
		setOrganisationCourante(osCourant);

		setListeCompteur((ArrayList<CompteurDto>) absService.getListeCompteursA52(getOrganisationCourante().getIdOrganisation()));
		afficheListeCompteur(request, getListeCompteur());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_NB_MINUTES() {
		return "NOM_ST_NB_MINUTES";
	}

	public String getVAL_ST_NB_MINUTES() {
		return getZone(getNOM_ST_NB_MINUTES());
	}

	public String getNOM_ST_NB_HEURES() {
		return "NOM_ST_NB_HEURES";
	}

	public String getVAL_ST_NB_HEURES() {
		return getZone(getNOM_ST_NB_HEURES());
	}

	public String getNOM_ST_DATE_DEBUT() {
		return "NOM_ST_DATE_DEBUT";
	}

	public String getVAL_ST_DATE_DEBUT() {
		return getZone(getNOM_ST_DATE_DEBUT());
	}

	public String getNOM_ST_DATE_FIN() {
		return "NOM_ST_DATE_FIN";
	}

	public String getVAL_ST_DATE_FIN() {
		return getZone(getNOM_ST_DATE_FIN());
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		if (!getVAL_ST_NB_HEURES().equals(Const.CHAINE_VIDE)) {
			// nbheures numerique
			if (!Services.estNumerique(getZone(getNOM_ST_NB_HEURES()))) {
				// "ERR992", "La zone @ doit être numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nb heures"));
				return false;
			}
		}

		if (!getVAL_ST_NB_MINUTES().equals(Const.CHAINE_VIDE)) {
			// nbminutes numerique
			if (!Services.estNumerique(getZone(getNOM_ST_NB_MINUTES()))) {
				// "ERR992", "La zone @ doit être numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nb minutes"));
				return false;
			}
		}

		// organisation syndicale obligatoire
		int indiceOS = (Services.estNumerique(getVAL_LB_OS_SELECT()) ? Integer.parseInt(getVAL_LB_OS_SELECT()) : -1);
		if (indiceOS <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
			return false;
		}

		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indiceMotif <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}

		// date debut obligatoire
		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// date fin obligatoire
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		// nb heures obligatoire
		if (getVAL_ST_NB_HEURES().equals(Const.CHAINE_VIDE) && getVAL_ST_NB_MINUTES().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nb heures"));
			return false;
		}

		return true;
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Vérification de la validité du formulaire
		if (!performControlerChamps(request))
			return false;

		// on recupere l'agent connecte
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		ReturnMessageDto message = new ReturnMessageDto();

		// motif
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		MotifCompteurDto motif = null;
		if (indiceMotif > 0) {
			motif = getListeMotifCompteur().get(indiceMotif - 1);
		}

		// organisation syndicale
		OrganisationSyndicaleDto os = null;
		int indiceOS = (Services.estNumerique(getVAL_LB_OS_SELECT()) ? Integer.parseInt(getVAL_LB_OS_SELECT()) : -1);
		if (indiceOS > 0) {
			os = getListeOrganisationSyndicale().get(indiceOS - 1);
		}

		// on recupere la saisie
		String dureeHeure = getVAL_ST_NB_HEURES().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_NB_HEURES();
		String dureeMin = getVAL_ST_NB_MINUTES().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_NB_MINUTES();

		int dureeTotaleSaisie = (Integer.valueOf(dureeHeure) * 60) + (Integer.valueOf(dureeMin));

		String dateDeb = Services.formateDate(getVAL_ST_DATE_DEBUT()) + " 00:00:00";
		String dateFin = Services.formateDate(getVAL_ST_DATE_FIN()) + " 23:59:59";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		CompteurDto compteurDto = new CompteurDto();
		if (getCompteurCourant() != null)
			compteurDto = getCompteurCourant();
		OrganisationSyndicaleDto orgaDto = new OrganisationSyndicaleDto();
		orgaDto.setIdOrganisation(os.getIdOrganisation());
		compteurDto.setOrganisationSyndicaleDto(orgaDto);
		MotifCompteurDto motifDto = new MotifCompteurDto();
		motifDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setMotifCompteurDto(motifDto);
		compteurDto.setDureeAAjouter(new Double(dureeTotaleSaisie));
		compteurDto.setDateDebut(sdf.parse(dateDeb));
		compteurDto.setDateFin(sdf.parse(dateFin));

		// on sauvegarde
		message = absService.addCompteurAsaA52(agentConnecte.getIdAgent(), new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR :  " + err);
		} else {
			// "INF010", "Le compteur @ a bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A52.getValue()));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			return null;
		}
		try {
			agentConnecte = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	public ArrayList<MotifCompteurDto> getListeMotifCompteur() {
		if (listeMotifCompteur == null)
			return new ArrayList<MotifCompteurDto>();
		return listeMotifCompteur;
	}

	public void setListeMotifCompteur(ArrayList<MotifCompteurDto> listeMotifCompteur) {
		this.listeMotifCompteur = listeMotifCompteur;
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		if (listeOrganisationSyndicale == null)
			return new ArrayList<OrganisationSyndicaleDto>();
		return listeOrganisationSyndicale;
	}

	public void setListeOrganisationSyndicale(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale) {
		this.listeOrganisationSyndicale = listeOrganisationSyndicale;
	}

	private String[] getLB_OS() {
		if (LB_OS == null)
			LB_OS = initialiseLazyLB();
		return LB_OS;
	}

	private void setLB_OS(String[] newLB_OS) {
		LB_OS = newLB_OS;
	}

	public String getNOM_LB_OS() {
		return "NOM_LB_OS";
	}

	public String getNOM_LB_OS_SELECT() {
		return "NOM_LB_OS_SELECT";
	}

	public String[] getVAL_LB_OS() {
		return getLB_OS();
	}

	public String getVAL_LB_OS_SELECT() {
		return getZone(getNOM_LB_OS_SELECT());
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_PB_VISU_REPRESENTANT(int i) {
		return "NOM_PB_VISU_REPRESENTANT" + i;
	}

	public boolean performPB_VISU_REPRESENTANT(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);

		OrganisationSyndicaleDto dto = new OrganisationSyndicaleDto();
		dto.setIdOrganisation(indiceEltAConsulter);

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicaleExistante().get(getListeOrganisationSyndicaleExistante().indexOf(dto));
		setOrganisationCourante(osCourant);

		ArrayList<AgentOrganisationSyndicaleDto> listeAgent = (ArrayList<AgentOrganisationSyndicaleDto>) absService.getListeRepresentantA52(getOrganisationCourante().getIdOrganisation());

		setListeRepresentant(listeAgent);

		if (!affichageRepresentant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISU_REPRESENTANT);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER_REPRESENTANT(int i) {
		return "NOM_PB_MODIFIER_REPRESENTANT" + i;
	}

	public boolean performPB_MODIFIER_REPRESENTANT(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);

		OrganisationSyndicaleDto dto = new OrganisationSyndicaleDto();
		dto.setIdOrganisation(indiceEltAModifier);

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicaleExistante().get(getListeOrganisationSyndicaleExistante().indexOf(dto));
		setOrganisationCourante(osCourant);

		ArrayList<AgentOrganisationSyndicaleDto> listeAgent = (ArrayList<AgentOrganisationSyndicaleDto>) absService.getListeRepresentantA52(getOrganisationCourante().getIdOrganisation());

		setListeRepresentant(listeAgent);

		if (!affichageRepresentant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_REPRESENTANT);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean affichageRepresentant(HttpServletRequest request) throws Exception {
		// #14737 tri par ordre alpha
		ArrayList<VoAgentCompteur> listeVoAgentCompteur = new ArrayList<VoAgentCompteur>();
		for (AgentOrganisationSyndicaleDto dto : getListeRepresentant()) {
			Agent agent = getAgentDao().chercherAgent(dto.getIdAgent());
			listeVoAgentCompteur.add(new VoAgentCompteur(dto, agent));
		}
		Collections.sort(listeVoAgentCompteur);

		ArrayList<AgentOrganisationSyndicaleDto> listeRepresentantsTriee = new ArrayList<AgentOrganisationSyndicaleDto>();
		for (VoAgentCompteur vo : listeVoAgentCompteur) {
			addZone(getNOM_ST_AGENT_REPRESENTANT(vo.getAgentOS().getIdAgent()), vo.getAgent().getNomAgent() + " " + vo.getAgent().getPrenomAgent());
			addZone(getNOM_ST_AGENT_REPRESENTANT_ACTIF(vo.getAgentOS().getIdAgent()), vo.getAgentOS().isActif() ? "oui" : "non");
			listeRepresentantsTriee.add(vo.getAgentOS());
		}
		setListeRepresentant(listeRepresentantsTriee);
		return true;
	}

	public String getNOM_ST_AGENT_REPRESENTANT(int i) {
		return "NOM_ST_AGENT_REPRESENTANT" + i;
	}

	public String getVAL_ST_AGENT_REPRESENTANT(int i) {
		return getZone(getNOM_ST_AGENT_REPRESENTANT(i));
	}

	public String getNOM_ST_AGENT_REPRESENTANT_ACTIF(int i) {
		return "NOM_ST_AGENT_REPRESENTANT_ACTIF" + i;
	}

	public String getVAL_ST_AGENT_REPRESENTANT_ACTIF(int i) {
		return getZone(getNOM_ST_AGENT_REPRESENTANT_ACTIF(i));
	}

	public ArrayList<AgentOrganisationSyndicaleDto> getListeRepresentant() {
		return listeRepresentant == null ? new ArrayList<AgentOrganisationSyndicaleDto>() : listeRepresentant;
	}

	public void setListeRepresentant(ArrayList<AgentOrganisationSyndicaleDto> listeRepresentant) {
		this.listeRepresentant = listeRepresentant;
	}

	public String getNOM_PB_AJOUTER_REPRESENTANT() {
		return "NOM_PB_AJOUTER_REPRESENTANT";
	}

	public boolean performPB_AJOUTER_REPRESENTANT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_REPRESENTANT(), ACTION_CREATION_REPRE);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION_REPRESENTANT() {
		return "NOM_ST_ACTION_REPRESENTANT";
	}

	public String getVAL_ST_ACTION_REPRESENTANT() {
		return getZone(getNOM_ST_ACTION_REPRESENTANT());
	}

	public String getNOM_PB_MODIFIER_REPRE(int i) {
		return "NOM_PB_MODIFIER_REPRE" + i;
	}

	public boolean performPB_MODIFIER_REPRE(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_REPRESENTANT(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		AgentOrganisationSyndicaleDto dto = new AgentOrganisationSyndicaleDto();
		dto.setIdAgent(indiceEltAModifier);

		AgentOrganisationSyndicaleDto agentCourant = (AgentOrganisationSyndicaleDto) getListeRepresentant().get(getListeRepresentant().indexOf(dto));

		if (!initialiseRepresentantCourant(request, agentCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_REPRESENTANT(), ACTION_MODIFICATION_REPRE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseRepresentantCourant(HttpServletRequest request, AgentOrganisationSyndicaleDto agentCourant) throws Exception {
		Agent ag = getAgentDao().chercherAgent(agentCourant.getIdAgent());
		addZone(getNOM_ST_AGENT_CREATE(), ag.getNomatr().toString());
		addZone(getNOM_RG_REPRESENTANT_INACTIF(), agentCourant.isActif() ? getNOM_RB_OUI() : getNOM_RB_NON());
		return true;
	}

	public String getNOM_ST_AGENT_CREATE() {
		return "NOM_ST_AGENT_CREATE";
	}

	public String getVAL_ST_AGENT_CREATE() {
		return getZone(getNOM_ST_AGENT_CREATE());
	}

	public String getNOM_PB_RECHERCHER_AGENT_CREATE() {
		return "NOM_PB_RECHERCHER_AGENT_CREATE";
	}

	public boolean performPB_RECHERCHER_AGENT_CREATE(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATE, true);
		return true;
	}

	public String getNOM_PB_CREATE() {
		return "NOM_PB_CREATE";
	}

	public boolean performPB_CREATE(HttpServletRequest request) throws Exception {
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION_REPRE)) {
			String nomatr = getVAL_ST_AGENT_CREATE();
			if (nomatr.equals(Const.CHAINE_VIDE)) {
				// "ERR002","La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
				return false;
			}
			try {
				Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + nomatr));
				AgentOrganisationSyndicaleDto dto = new AgentOrganisationSyndicaleDto();
				dto.setIdAgent(agent.getIdAgent());
				Boolean actif = getZone(getNOM_RG_REPRESENTANT_INACTIF()).equals(getNOM_RB_OUI());
				dto.setActif(actif);

				getListeRepresentant().add(dto);
				affichageRepresentant(request);
				videZonesDeSaisie(request);
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", nomatr));
				return false;
			}
		} else if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_MODIFICATION_REPRE)) {
			Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + getVAL_ST_AGENT_CREATE()));
			AgentOrganisationSyndicaleDto dto = new AgentOrganisationSyndicaleDto();
			dto.setIdAgent(agent.getIdAgent());
			AgentOrganisationSyndicaleDto select = getListeRepresentant().get(getListeRepresentant().indexOf(dto));
			Boolean actif = getZone(getNOM_RG_REPRESENTANT_INACTIF()).equals(getNOM_RB_OUI());
			select.setActif(actif);
			affichageRepresentant(request);
			addZone(getNOM_ST_ACTION_REPRESENTANT(), Const.CHAINE_VIDE);
			videZonesDeSaisie(request);
		}
		return true;
	}

	public String getNOM_PB_VALIDER_REPRESENTANT() {
		return "NOM_PB_VALIDER_REPRESENTANT";
	}

	public boolean performPB_VALIDER_REPRESENTANT(HttpServletRequest request) throws Exception {

		// on recupere l'agent connecte
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		ReturnMessageDto message = new ReturnMessageDto();

		// on sauvegarde
		message = absService.saveRepresentantAsaA52(getOrganisationCourante().getIdOrganisation(),
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).serialize(getListeRepresentant()));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
		} else {
			// "INF700", "Les représentants ont bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF700"));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_REPRESENTANT(), Const.CHAINE_VIDE);
		return true;
	}

	public OrganisationSyndicaleDto getOrganisationCourante() {
		return organisationCourante;
	}

	public void setOrganisationCourante(OrganisationSyndicaleDto organisationCourante) {
		this.organisationCourante = organisationCourante;
	}

	public String getNOM_RG_REPRESENTANT_INACTIF() {
		return "NOM_RG_REPRESENTANT_INACTIF";
	}

	public String getVAL_RG_REPRESENTANT_INACTIF() {
		return getZone(getNOM_RG_REPRESENTANT_INACTIF());
	}

	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	public String getNOM_PB_SUPPRIMER_REPRE(int i) {
		return "NOM_PB_SUPPRIMER_REPRE" + i;
	}

	public boolean performPB_SUPPRIMER_REPRE(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// on supprime le representant de la liste
		AgentOrganisationSyndicaleDto dto = new AgentOrganisationSyndicaleDto();
		dto.setIdAgent(indiceEltAModifier);

		getListeRepresentant().remove(dto);
		affichageRepresentant(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean peutSupprimerAgent(Integer idAgent) {
		// on cherche si il y a deja des demandes de decharge de service pour
		// cet agent
		List<DemandeDto> list = absService.getListeDemandes(null, null, null, EnumTypeAbsence.ASA_A52.getCode(), idAgent, EnumTypeGroupeAbsence.AS.getValue(), false, null);
		if (list.size() == 0)
			return true;
		return false;
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicaleExistante() {
		return listeOrganisationSyndicaleExistante == null ? new ArrayList<OrganisationSyndicaleDto>() : listeOrganisationSyndicaleExistante;
	}

	public void setListeOrganisationSyndicaleExistante(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicaleExistante) {
		this.listeOrganisationSyndicaleExistante = listeOrganisationSyndicaleExistante;
	}

	public ArrayList<CompteurDto> getListeCompteur() {
		return listeCompteur == null ? new ArrayList<CompteurDto>() : listeCompteur;
	}

	public void setListeCompteur(ArrayList<CompteurDto> listeCompteur) {
		this.listeCompteur = listeCompteur;
		Collections.sort(this.listeCompteur);
	}

	public CompteurDto getCompteurCourant() {
		return compteurCourant;
	}

	public void setCompteurCourant(CompteurDto compteurCourant) {
		this.compteurCourant = compteurCourant;
	}

	public boolean peutModifierCompteur(int i) {
		//on parcours la liste des compteurs
		CompteurDto compteurCourant  = null;
		for(CompteurDto cpt : getListeCompteur()){
			if(i==cpt.getIdCompteur())
				compteurCourant = cpt;
		}
		if (compteurCourant.getDateFin().compareTo(new Date()) < 0) {
			return false;
		}
		return true;
	}

	public String getNOM_PB_VISUALISATION_COMPTEUR(int i) {
		return "NOM_PB_VISUALISATION_COMPTEUR" + i;
	}

	public boolean performPB_VISUALISATION_COMPTEUR(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_COMPTEUR(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);
		
		//on parcours la liste des compteurs
		CompteurDto cptCourant  = null;
		for(CompteurDto cpt : getListeCompteur()){
			if(indiceEltAConsulter==cpt.getIdCompteur())
				cptCourant = cpt;
		}

		setCompteurCourant(cptCourant);

		if (!initialiseCompteurCourant(request, getCompteurCourant()))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_COMPTEUR(), ACTION_VISUALISATION_COMPTEUR);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER_COMPTEUR(int i) {
		return "NOM_PB_MODIFIER_COMPTEUR" + i;
	}

	public boolean performPB_MODIFIER_COMPTEUR(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_COMPTEUR(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);
		setOrganisationCourante(null);
		
		//on parcours la liste des compteurs
		CompteurDto cptCourant  = null;
		for(CompteurDto cpt : getListeCompteur()){
			if(indiceEltAConsulter==cpt.getIdCompteur())
				cptCourant = cpt;
		}

		setCompteurCourant(cptCourant);

		if (!initialiseCompteurCourant(request, getCompteurCourant()))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_COMPTEUR(), ACTION_MODIFIER_COMPTEUR);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseCompteurCourant(HttpServletRequest request, CompteurDto dto) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int ligneOS = getListeOrganisationSyndicale().indexOf(dto.getOrganisationSyndicaleDto());
		addZone(getNOM_LB_OS_SELECT(), String.valueOf(ligneOS + 1));
		int ligneMotif = getListeMotifCompteur().indexOf(dto.getMotifCompteurDto());
		addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif + 1));
		addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dto.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), sdf.format(dto.getDateFin()));
		String soldeAsaA52Heure = (dto.getDureeAAjouter().intValue() / 60) == 0 ? Const.CHAINE_VIDE : dto.getDureeAAjouter().intValue() / 60 + Const.CHAINE_VIDE;
		String soldeAsaA52Minute = (dto.getDureeAAjouter().intValue() % 60) == 0 ? Const.CHAINE_VIDE : dto.getDureeAAjouter().intValue() % 60 + Const.CHAINE_VIDE;
		addZone(getNOM_ST_NB_HEURES(), soldeAsaA52Heure);
		addZone(getNOM_ST_NB_MINUTES(), soldeAsaA52Minute);
		return true;
	}

	public String getNOM_ST_ACTION_COMPTEUR() {
		return "NOM_ST_ACTION_COMPTEUR";
	}

	public String getVAL_ST_ACTION_COMPTEUR() {
		return getZone(getNOM_ST_ACTION_COMPTEUR());
	}
}
