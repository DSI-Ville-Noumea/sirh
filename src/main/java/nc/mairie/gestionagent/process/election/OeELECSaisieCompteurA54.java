package nc.mairie.gestionagent.process.election;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.AgentOrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 *
 */
public class OeELECSaisieCompteurA54 extends BasicProcess {

	/**
	 *
	 */
	private static final long							serialVersionUID					= 1L;
	private Logger										logger								= LoggerFactory.getLogger(OeELECSaisieCompteurA54.class);

	public static final int								STATUT_RECHERCHER_AGENT_CREATE		= 1;

	private ArrayList<CompteurDto>						listeCompteur;
	private ArrayList<String>							listeAnnee;
	private ArrayList<String>							listeAnneeFiltre;
	private String[]									LB_ANNEE;
	private String[]									LB_ANNEE_FILTRE;
	private String[]									LB_MOTIF;
	private ArrayList<MotifCompteurDto>					listeMotifCompteur;

	public String										ACTION_MODIFICATION					= "Modification d'un compteur.";
	public String										ACTION_CREATION						= "Création d'un compteur.";
	public String										ACTION_VISUALISATION				= "Consultation d'un compteur.";
	public String										ACTION_VISU_REPRESENTANT			= "Visualisation des représentants -";
	public String										ACTION_MODIFICATION_REPRESENTANT	= "Modification des représentants -";
	public String										ACTION_CREATION_REPRESENTANT		= "Création d'un représentant -";

	private AgentDao									agentDao;

	private IRadiService								radiService;

	private IAbsService									absService;

	private ArrayList<OrganisationSyndicaleDto>			listeOrganisationSyndicale;
	private OrganisationSyndicaleDto					organisationCourante;
	private ArrayList<AgentOrganisationSyndicaleDto>	listeRepresentant;

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA54.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-ELEC-COMPTEUR";
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

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATE(), agt.getNomatr().toString());
			}
		}

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		initialiseListeCompteur(request);

		initialiseListeOS(request);
	}

	private void initialiseListeDeroulante() {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE_FILTRE() == LBVide) {
			List<String> listeAnnee = new ArrayList<>();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			Integer anneeCourante = cal.get(Calendar.YEAR);
			for (int i = 2015; i <= anneeCourante + 1; i++) {
				listeAnnee.add(i + "");
			}
			setListeAnneeFiltre((ArrayList<String>) listeAnnee);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (String annee : getListeAnneeFiltre()) {
				String ligne[] = { annee };
				aFormat.ajouteLigne(ligne);
			}

			setLB_ANNEE_FILTRE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_FILTRE_SELECT(), "1");
		}
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			Integer annee = cal.get(Calendar.YEAR);

			String[] list = { String.valueOf(annee), String.valueOf(annee + 1), String.valueOf(annee + 2) };

			ArrayList<String> arrayList = new ArrayList<String>();

			for (String an : list)
				arrayList.add(an);

			setListeAnnee(arrayList);

			setLB_ANNEE(list);
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}

		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) absService
					.getListeMotifCompteur(EnumTypeAbsence.ASA_A54.getCode());
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

	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private void initialiseListeCompteur(HttpServletRequest request) throws Exception {
		// recupération année du filtre
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_FILTRE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_FILTRE_SELECT()) : -1);
		String anneeFiltre = getListeAnneeFiltre().get(indiceAnnee);

		ArrayList<CompteurDto> listeCompteur = (ArrayList<CompteurDto>) absService.getListeCompteursA54(new Integer(anneeFiltre));
		logger.debug("Taille liste des compteurs ASA A54 : " + listeCompteur.size());
		// #14737 tri par ordre alpha
		List<VoAgentCompteur> listCompteurAgent = new ArrayList<VoAgentCompteur>();
		for (CompteurDto dto : listeCompteur) {

			Agent ag = getAgentDao().chercherAgent(dto.getIdAgent());

			VoAgentCompteur voCompteur = new VoAgentCompteur(dto, ag);
			listCompteurAgent.add(voCompteur);
		}
		Collections.sort(listCompteurAgent);

		ArrayList<CompteurDto> listeCompteurTriee = new ArrayList<CompteurDto>();
		int indiceLigne = 0;
		for (VoAgentCompteur vo : listCompteurAgent) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(vo.getCompteur().getDateDebut());
			Integer annee = cal.get(Calendar.YEAR);

			addZone(getNOM_ST_MATRICULE(indiceLigne), vo.getAgent().getNomatr().toString());
			addZone(getNOM_ST_AGENT(indiceLigne), vo.getAgent().getNomAgent() + " " + vo.getAgent().getPrenomAgent());
			addZone(getNOM_ST_ANNEE(indiceLigne), annee.toString());
			addZone(getNOM_ST_NB_JOURS(indiceLigne), String.valueOf(vo.getCompteur().getDureeAAjouter().intValue()));
			addZone(getNOM_ST_MOTIF(indiceLigne),
					vo.getCompteur().getMotifCompteurDto() == null ? Const.CHAINE_VIDE : vo.getCompteur().getMotifCompteurDto().getLibelle());
			addZone(getNOM_ST_ACTIF(indiceLigne), vo.isActif() ? "oui" : "non");

			indiceLigne++;

			listeCompteurTriee.add(vo.getCompteur());
		}
		setListeCompteur(listeCompteurTriee);
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

			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}

			// Si clic sur le bouton PB_DUPLIQUER
			if (testerParametre(request, getNOM_PB_DUPLIQUER())) {
				return performPB_DUPLIQUER(request);
			}

			for (int i = 0; i < getListeCompteur().size(); i++) {
				// Si clic sur le bouton PB_MODIFIER
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
				// Si clic sur le bouton PB_VISUALISER
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}

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

			for (int j = 0; j < getListeOrganisationSyndicale().size(); j++) {
				Integer i = getListeOrganisationSyndicale().get(j).getIdOrganisation();
				// Si clic sur le bouton PB_VISU_REPRESENTANT
				if (testerParametre(request, getNOM_PB_VISU_REPRESENTANT(i))) {
					return performPB_VISU_REPRESENTANT(request, i);
				}
				// Si clic sur le bouton PB_MODIFIER_REPRESENTANT
				if (testerParametre(request, getNOM_PB_MODIFIER_REPRESENTANT(i))) {
					return performPB_MODIFIER_REPRESENTANT(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_AJOUTER_REPRESENTANT())) {
				return performPB_AJOUTER_REPRESENTANT(request);
			}

			for (int j = 0; j < getListeRepresentant().size(); j++) {
				Integer i = getListeRepresentant().get(j).getIdAgent();
				// Si clic sur le bouton PB_MODIFIER_REPRE
				if (testerParametre(request, getNOM_PB_MODIFIER_REPRESENTANT(i))) {
					return performPB_MODIFIER_REPRESENTANT(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER_REPRE
				if (testerParametre(request, getNOM_PB_SUPPRIMER_REPRESENTANT(i))) {
					return performPB_SUPPRIMER_REPRESENTANT(request, i);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<CompteurDto> getListeCompteur() {
		return listeCompteur == null ? new ArrayList<CompteurDto>() : listeCompteur;
	}

	public void setListeCompteur(ArrayList<CompteurDto> listeCompteur) {
		this.listeCompteur = listeCompteur;
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	public String getNOM_ST_NB_JOURS(int i) {
		return "NOM_ST_NB_JOURS" + i;
	}

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	public String getNOM_ST_ACTIF(int i) {
		return "NOM_ST_ACTIF" + i;
	}

	public String getVAL_ST_ACTIF(int i) {
		return getZone(getNOM_ST_ACTIF(i));
	}

	public String getVAL_ST_NB_JOURS(int i) {
		return getZone(getNOM_ST_NB_JOURS(i));
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NB_JOURS(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_AGENT_INACTIF(), getNOM_RB_OUI());
		addZone(getNOM_ST_ANNEE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		addZone(getNOM_ST_AGENT_CREATE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
	}

	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(indiceEltAModifier);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseCompteurCourant(HttpServletRequest request, CompteurDto dto) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dto.getDateDebut());
		Integer annee = cal.get(Calendar.YEAR);

		int ligneAnnee = getListeAnnee().indexOf(annee.toString());
		addZone(getNOM_LB_ANNEE_SELECT(), String.valueOf(ligneAnnee));
		addZone(getNOM_ST_NB_JOURS(), String.valueOf(dto.getDureeAAjouter().intValue()));
		addZone(getNOM_ST_ANNEE(), annee.toString());
		addZone(getNOM_ST_AGENT_CREATE(), dto.getIdAgent().toString().substring(3, dto.getIdAgent().toString().length()));
		int ligneMotif = getListeMotifCompteur().indexOf(dto.getMotifCompteurDto());
		addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif + 1));
		addZone(getNOM_RG_AGENT_INACTIF(), dto.isActif() ? getNOM_RB_OUI() : getNOM_RB_NON());
		return true;
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(indiceEltAConsulter);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_NB_JOURS() {
		return "NOM_ST_NB_JOURS";
	}

	public String getVAL_ST_NB_JOURS() {
		return getZone(getNOM_ST_NB_JOURS());
	}

	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
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

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		// nbjours numerique
		if (!Services.estNumerique(getZone(getNOM_ST_NB_JOURS()))) {
			// "ERR992", "La zone @ doit être numérique.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nb jours"));
			return false;
		}

		// idAgent numerique
		if (!Services.estNumerique(getZone(getNOM_ST_AGENT_CREATE()))) {
			// "ERR992", "La zone @ doit être numérique.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "agent"));
			return false;
		}

		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indiceMotif <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}

		// annee obligatoire
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			if (indiceAnnee < 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
				return false;
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			if (getVAL_ST_ANNEE().equals(Const.CHAINE_VIDE)) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
				return false;
			}

		}

		// agent obligatoire
		if (getVAL_ST_AGENT_CREATE().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		}

		// nb jours obligatoire
		if (getVAL_ST_NB_JOURS().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nb jours"));
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
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		ReturnMessageDto message = new ReturnMessageDto();

		// on recupere la saisie
		String nomatr = getVAL_ST_AGENT_CREATE();
		Agent agCompteur = getAgentDao().chercherAgentParMatricule(Integer.valueOf(nomatr));

		// motif
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		MotifCompteurDto motif = null;
		if (indiceMotif > 0) {
			motif = getListeMotifCompteur().get(indiceMotif - 1);
		}

		// annee
		Integer annee = null;
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			if (indiceAnnee >= 0) {
				annee = Integer.valueOf(getListeAnnee().get(indiceAnnee));
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			annee = Integer.valueOf(getVAL_ST_ANNEE());
		}

		CompteurDto compteurDto = new CompteurDto();
		compteurDto.setIdAgent(agCompteur.getIdAgent());
		MotifCompteurDto motifDto = new MotifCompteurDto();
		motifDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setMotifCompteurDto(motifDto);
		compteurDto.setDureeAAjouter(new Double(Integer.valueOf(getVAL_ST_NB_JOURS())));
		compteurDto.setDateDebut(new DateTime(annee, 1, 1, 0, 0, 0).toDate());
		compteurDto.setDateFin(new DateTime(annee, 12, 31, 23, 59, 0).toDate());
		Boolean actif = getZone(getNOM_RG_AGENT_INACTIF()).equals(getNOM_RB_OUI());
		compteurDto.setActif(actif);

		// on sauvegarde
		message = absService.addCompteurAsaA54(agentConnecte.getIdAgent(),
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
		} else {
			// "INF010", "Le compteur @ a bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A54.getValue()));
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

	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	public ArrayList<String> getListeAnnee() {
		return listeAnnee;
	}

	public void setListeAnnee(ArrayList<String> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	public boolean peutModifierCompteur(int i) {
		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(i);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer anneeCourante = cal.get(Calendar.YEAR);

		Calendar calDto = Calendar.getInstance();
		calDto.setTime(compteurCourant.getDateDebut());
		Integer anneeDto = calDto.get(Calendar.YEAR);
		if (anneeDto < anneeCourante) {
			return false;
		}
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

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_RG_AGENT_INACTIF() {
		return "NOM_RG_AGENT_INACTIF";
	}

	public String getVAL_RG_AGENT_INACTIF() {
		return getZone(getNOM_RG_AGENT_INACTIF());
	}

	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	private String[] getLB_ANNEE_FILTRE() {
		if (LB_ANNEE_FILTRE == null)
			LB_ANNEE_FILTRE = initialiseLazyLB();
		return LB_ANNEE_FILTRE;
	}

	private void setLB_ANNEE_FILTRE(String[] newLB_ANNEE_FILTRE) {
		LB_ANNEE_FILTRE = newLB_ANNEE_FILTRE;
	}

	public String getNOM_LB_ANNEE_FILTRE() {
		return "NOM_LB_ANNEE_FILTRE";
	}

	public String getNOM_LB_ANNEE_FILTRE_SELECT() {
		return "NOM_LB_ANNEE_FILTRE_SELECT";
	}

	public String[] getVAL_LB_ANNEE_FILTRE() {
		return getLB_ANNEE_FILTRE();
	}

	public String getVAL_LB_ANNEE_FILTRE_SELECT() {
		return getZone(getNOM_LB_ANNEE_FILTRE_SELECT());
	}

	public ArrayList<String> getListeAnneeFiltre() {
		return listeAnneeFiltre;
	}

	public void setListeAnneeFiltre(ArrayList<String> listeAnneeFiltre) {
		this.listeAnneeFiltre = listeAnneeFiltre;
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {

		initialiseListeCompteur(request);
		return true;
	}

	public boolean isDuplicationPossible() {
		// on ne peut dupliquer que si on est sur l'année en cours
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer anneeCourante = cal.get(Calendar.YEAR);

		// recupération année du filtre
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_FILTRE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_FILTRE_SELECT()) : -1);
		String anneeFiltre = getListeAnneeFiltre().get(indiceAnnee);
		if (new Integer(anneeFiltre).equals(anneeCourante)) {
			return true;
		}
		return false;
	}

	public String getNOM_PB_DUPLIQUER() {
		return "NOM_PB_DUPLIQUER";
	}

	public boolean performPB_DUPLIQUER(HttpServletRequest request) throws Exception {
		if (!isDuplicationPossible()) {
			getTransaction().declarerErreur("ERREUR : La duplication ne peut se faire que sur l'année en cours, merci de choisir l'année en cours.");
			return false;
		}

		// on recupere l'agent connecte
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		// on recupere la liste de tous les compteurs pour l'année en cours
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer anneeCourante = cal.get(Calendar.YEAR);
		ArrayList<CompteurDto> listeCompteur = (ArrayList<CompteurDto>) absService.getListeCompteursA54(anneeCourante);

		// on met le motif "reprise de données"
		MotifCompteurDto motifReprise = null;
		for (MotifCompteurDto mo : getListeMotifCompteur()) {
			if (mo.getLibelle().equals("Reprise de données")) {
				motifReprise = mo;
				break;
			}
		}

		// on construit le DTO
		List<CompteurDto> listeDto = new ArrayList<>();
		for (CompteurDto dtoExist : listeCompteur) {
			// on ne prend que les actifs
			if (dtoExist.isActif()) {

				CompteurDto compteurDto = new CompteurDto();
				compteurDto.setIdAgent(dtoExist.getIdAgent());

				compteurDto.setMotifCompteurDto(motifReprise);
				compteurDto.setDureeAAjouter(10.0);
				compteurDto.setDateDebut(new DateTime(anneeCourante + 1, 1, 1, 0, 0, 0).toDate());
				compteurDto.setDateFin(new DateTime(anneeCourante + 1, 12, 31, 23, 59, 0).toDate());
				compteurDto.setActif(true);
				// on ajoute le DTO
				listeDto.add(compteurDto);
			}
		}

		// on sauvegarde
		ReturnMessageDto message = absService.addCompteurAsaA54ByList(agentConnecte.getIdAgent(),
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).serialize(listeDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
		} else {
			// "INF010", "Les compteurs @ a bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, "INFO : les compteurs ont bien été dupliqués");
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		return listeOrganisationSyndicale == null ? new ArrayList<OrganisationSyndicaleDto>() : listeOrganisationSyndicale;
	}

	public void setListeOrganisationSyndicale(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale) {
		this.listeOrganisationSyndicale = listeOrganisationSyndicale;
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
			listeRepresentantsTriee.add(vo.getAgentOS());
		}
		setListeRepresentant(listeRepresentantsTriee);
		return true;
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

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicale()
				.get(getListeOrganisationSyndicale().indexOf(dto));
		setOrganisationCourante(osCourant);

		ArrayList<AgentOrganisationSyndicaleDto> listeAgent = (ArrayList<AgentOrganisationSyndicaleDto>) absService
				.getListeRepresentantA54(getOrganisationCourante().getIdOrganisation());

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

		OrganisationSyndicaleDto osCourant = (OrganisationSyndicaleDto) getListeOrganisationSyndicale()
				.get(getListeOrganisationSyndicale().indexOf(dto));
		setOrganisationCourante(osCourant);

		ArrayList<AgentOrganisationSyndicaleDto> listeAgent = (ArrayList<AgentOrganisationSyndicaleDto>) absService
				.getListeRepresentantA54(getOrganisationCourante().getIdOrganisation());

		setListeRepresentant(listeAgent);

		if (!affichageRepresentant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_REPRESENTANT);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public OrganisationSyndicaleDto getOrganisationCourante() {
		return organisationCourante;
	}

	public void setOrganisationCourante(OrganisationSyndicaleDto organisationCourante) {
		this.organisationCourante = organisationCourante;
	}

	public ArrayList<AgentOrganisationSyndicaleDto> getListeRepresentant() {
		return listeRepresentant == null ? new ArrayList<AgentOrganisationSyndicaleDto>() : listeRepresentant;
	}

	public void setListeRepresentant(ArrayList<AgentOrganisationSyndicaleDto> listeRepresentant) {
		this.listeRepresentant = listeRepresentant;
	}

	public String getNOM_ST_AGENT_REPRESENTANT(int i) {
		return "NOM_ST_AGENT_REPRESENTANT" + i;
	}

	public String getVAL_ST_AGENT_REPRESENTANT(int i) {
		return getZone(getNOM_ST_AGENT_REPRESENTANT(i));
	}

	public String getNOM_PB_AJOUTER_REPRESENTANT() {
		return "NOM_PB_AJOUTER_REPRESENTANT";
	}

	public boolean performPB_AJOUTER_REPRESENTANT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_REPRESENTANT(), ACTION_CREATION_REPRESENTANT);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_REPRESENTANT(int i) {
		return "NOM_PB_SUPPRIMER_REPRESENTANT" + i;
	}

	public boolean performPB_SUPPRIMER_REPRESENTANT(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// on supprime le representant de la liste
		AgentOrganisationSyndicaleDto dto = new AgentOrganisationSyndicaleDto();
		dto.setIdAgent(indiceEltAModifier);

		getListeRepresentant().remove(dto);
		affichageRepresentant(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION_REPRESENTANT() {
		return "NOM_ST_ACTION_REPRESENTANT";
	}

	public String getVAL_ST_ACTION_REPRESENTANT() {
		return getZone(getNOM_ST_ACTION_REPRESENTANT());
	}

	public String getNOM_PB_CREATE() {
		return "NOM_PB_CREATE";
	}

	public boolean performPB_CREATE(HttpServletRequest request) throws Exception {
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION_REPRESENTANT)) {
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

				getListeRepresentant().add(dto);
				affichageRepresentant(request);
				videZonesDeSaisie(request);
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule
				// existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", nomatr));
				return false;
			}
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
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		ReturnMessageDto message = new ReturnMessageDto();

		// on sauvegarde
		message = absService.saveRepresentantAsaA54(getOrganisationCourante().getIdOrganisation(),
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

	private void initialiseListeOS(HttpServletRequest request) {
		ArrayList<OrganisationSyndicaleDto> listeOS = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOrganisationSyndicale();
		logger.debug("Taille liste des OS : " + listeOS.size());

		ArrayList<OrganisationSyndicaleDto> listeOSActif = new ArrayList<>();
		for (OrganisationSyndicaleDto vo : listeOS) {
			if (vo.isActif()) {
				Integer indiceLigne = vo.getIdOrganisation();
				listeOSActif.add(vo);
				addZone(getNOM_ST_SIGLE_OS(indiceLigne), vo.getSigle());
				addZone(getNOM_ST_OS(indiceLigne), vo.getLibelle());
			}
		}
		setListeOrganisationSyndicale(listeOSActif);
	}
}
