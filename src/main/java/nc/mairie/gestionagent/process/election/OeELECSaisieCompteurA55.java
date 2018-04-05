package nc.mairie.gestionagent.process.election;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.vo.VoAgentCompteur;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.process.OePaginable;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
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

/**
 *
 */
public class OeELECSaisieCompteurA55 extends OePaginable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(OeELECSaisieCompteurA55.class);

	public static final int STATUT_RECHERCHER_AGENT_CREATE = 1;

	private ArrayList<CompteurDto> listeCompteur;
	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_MODIFICATION = "Modification d'un compteur.";
	public String ACTION_CREATION = "Création d'un compteur.";

	private AgentDao agentDao;

	private IAbsService absService;

	private IRadiService radiService;

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA55.jsp";
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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
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
		if (etatStatut() == MaClasse.STATUT_RECHERCHE_AGENT) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_DEMANDE(), agt.getNomatr().toString());
			}
		}

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();
		
		initialisePagination();
	}
	
	@Override
	public void getAllResultCount() {
		// Filtres
		Integer idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : Integer.valueOf("900" + getVAL_ST_AGENT_DEMANDE());

		String dateDeb = StringUtils.isNotBlank(getVAL_ST_DATE_MIN()) ? getVAL_ST_DATE_MIN() : null;
		String dateFin = StringUtils.isNotBlank(getVAL_ST_DATE_MAX()) ? getVAL_ST_DATE_MAX() : null;
		
		setResultSize(absService.getCountAllCompteursByYearAndOS("asaA55", null, null, idAgentDemande, dateDeb, dateFin));
	}

	private void initialiseListeDeroulante() {
		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) absService.getListeMotifCompteur(EnumTypeAbsence.ASA_A55.getCode());
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
		// Filtres
		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();

		String dateDeb = StringUtils.isNotBlank(getVAL_ST_DATE_MIN()) ? getVAL_ST_DATE_MIN() : null;
		String dateFin = StringUtils.isNotBlank(getVAL_ST_DATE_MAX()) ? getVAL_ST_DATE_MAX() : null;
		
		// Recherche
		ArrayList<CompteurDto> listeCompteur = (ArrayList<CompteurDto>) absService.getListeCompteursA55(getPageSize(), getPageNumber(), idAgentDemande, dateDeb, dateFin);
		logger.debug("Taille liste des compteurs ASA A55 : " + listeCompteur.size());
		// #14737 tri par ordre alpha
		List<VoAgentCompteur> listCompteurAgent = new ArrayList<VoAgentCompteur>();
		for (CompteurDto dto : listeCompteur) {

			Agent ag = getAgentDao().chercherAgent(dto.getIdAgent());

			VoAgentCompteur voCompteur = new VoAgentCompteur(dto, ag);
			listCompteurAgent.add(voCompteur);
		}
		Collections.sort(listCompteurAgent);

		ArrayList<CompteurDto> listeCompteurTriee = new ArrayList<CompteurDto>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indiceLigne = 0;
		for (VoAgentCompteur vo : listCompteurAgent) {

			addZone(getNOM_ST_MATRICULE(indiceLigne), vo.getAgent().getNomatr().toString());
			addZone(getNOM_ST_AGENT(indiceLigne), vo.getAgent().getNomAgent() + " " + vo.getAgent().getPrenomAgent());
			addZone(getNOM_ST_DATE_DEBUT(indiceLigne), sdf.format(vo.getCompteur().getDateDebut()));
			addZone(getNOM_ST_DATE_FIN(indiceLigne), sdf.format(vo.getCompteur().getDateFin()));
			String soldeAsaA55Heure = (vo.getCompteur().getDureeAAjouter().intValue() / 60) == 0 ? Const.CHAINE_VIDE : vo.getCompteur().getDureeAAjouter().intValue() / 60 + "h ";
			String soldeAsaA55Minute = (vo.getCompteur().getDureeAAjouter().intValue() % 60) == 0 ? "&nbsp;" : vo.getCompteur().getDureeAAjouter().intValue() % 60 + "m";
			addZone(getNOM_ST_NB_HEURES(indiceLigne), soldeAsaA55Heure + soldeAsaA55Minute);
			addZone(getNOM_ST_MOTIF(indiceLigne), vo.getCompteur().getMotifCompteurDto() == null ? Const.CHAINE_VIDE : vo.getCompteur().getMotifCompteurDto().getLibelle());

			indiceLigne++;

			listeCompteurTriee.add(vo.getCompteur());
		}

		setListeCompteur(listeCompteurTriee);
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			super.recupererStatut(request);
			
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
			
			// Si clic sur le bouton PB_RECHERCHER_AGENT_DEMANDE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_RECHERCHER_AGENT_DEMANDE(request);
			}

			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATE())) {
				return performPB_RECHERCHER_AGENT_CREATE(request);
			}

			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeCompteur().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_CHANGE_PAGINATION(HttpServletRequest request) throws Exception {
		super.updatePagination(request);
		initialiseListeCompteur(request);
		return true;
	}

	public boolean performPB_NEXT_PAGE(HttpServletRequest request) throws Exception {
		super.performPB_NEXT_PAGE(request);
		initialiseListeCompteur(request);
		return true;
	}

	public boolean performPB_PREVIOUS_PAGE(HttpServletRequest request) throws Exception {
		super.performPB_PREVIOUS_PAGE(request);
		initialiseListeCompteur(request);
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

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	public String getVAL_ST_NB_HEURES(int i) {
		return getZone(getNOM_ST_NB_HEURES(i));
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NB_HEURES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NB_MINUTES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dto.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), sdf.format(dto.getDateFin()));
		String soldeAsaA55Heure = (dto.getDureeAAjouter().intValue() / 60) == 0 ? Const.CHAINE_VIDE : dto.getDureeAAjouter().intValue() / 60 + Const.CHAINE_VIDE;
		String soldeAsaA55Minute = (dto.getDureeAAjouter().intValue() % 60) == 0 ? Const.CHAINE_VIDE : dto.getDureeAAjouter().intValue() % 60 + Const.CHAINE_VIDE;
		addZone(getNOM_ST_NB_HEURES(), soldeAsaA55Heure);
		addZone(getNOM_ST_NB_MINUTES(), soldeAsaA55Minute);
		addZone(getNOM_ST_AGENT_CREATE(), dto.getIdAgent().toString().substring(3, dto.getIdAgent().toString().length()));
		int ligneMotif = getListeMotifCompteur().indexOf(dto.getMotifCompteurDto());
		addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif + 1));
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

	public String getNOM_ST_AGENT_CREATE() {
		return "NOM_ST_AGENT_CREATE";
	}

	public String getVAL_ST_AGENT_CREATE() {
		return getZone(getNOM_ST_AGENT_CREATE());
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

		// agent obligatoire
		if (getVAL_ST_AGENT_CREATE().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
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

	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		getAllResultCount();
		updatePagination(request);
		initialiseListeCompteur(request);
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

		// on recupere la saisie
		String nomatr = getVAL_ST_AGENT_CREATE();
		Agent agCompteur = getAgentDao().chercherAgentParMatricule(Integer.valueOf(nomatr));

		// motif
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		MotifCompteurDto motif = null;
		if (indiceMotif > 0) {
			motif = getListeMotifCompteur().get(indiceMotif - 1);
		}

		// on recupere la saisie
		String dureeHeure = getVAL_ST_NB_HEURES().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_NB_HEURES();
		String dureeMin = getVAL_ST_NB_MINUTES().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_NB_MINUTES();

		int dureeTotaleSaisie = (Integer.valueOf(dureeHeure) * 60) + (Integer.valueOf(dureeMin));

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateDeb = Services.formateDate(getVAL_ST_DATE_DEBUT()) + " 00:00:00";
		String dateFin = Services.formateDate(getVAL_ST_DATE_FIN()) + " 23:59:59";
		
		// #43463 : Les dates doivent correspondre aux début et fin de mois.
		DateTime tmpDate = new DateTime(sdf.parse(dateDeb)).withDayOfMonth(01);
		if (!new DateTime(sdf.parse(dateDeb)).equals(tmpDate)) {
			getTransaction().declarerErreur("La date de début ne correspond pas au premier jour du mois.");
			return false;
		}
		// #43463 : La date de début ne peut être rétroactive.
		if (sdf.parse(dateDeb).before(new DateTime().withDayOfMonth(1).minusDays(1).toDate())) {
			getTransaction().declarerErreur("La date de début ne peut pas être inférieure au mois courant.");
			return false;
		}
		tmpDate = new DateTime(sdf.parse(dateFin)).withDayOfMonth(01).plusMonths(1).minusDays(1);
		if (!new DateTime(sdf.parse(dateFin)).equals(tmpDate)) {
			getTransaction().declarerErreur("La date de fin ne correspond pas au dernier jour du mois.");
			return false;
		}
		
		CompteurDto compteurDto = new CompteurDto();
		compteurDto.setIdAgent(agCompteur.getIdAgent());
		MotifCompteurDto motifDto = new MotifCompteurDto();
		motifDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setMotifCompteurDto(motifDto);
		compteurDto.setDureeAAjouter(new Double(dureeTotaleSaisie));
		compteurDto.setDateDebut(sdf.parse(dateDeb));
		compteurDto.setDateFin(sdf.parse(dateFin));

		// on sauvegarde
		message = absService.addCompteurAsaA55(agentConnecte.getIdAgent(), new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
		} else {
			// "INF010", "Le compteur @ a bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A55.getValue()));
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

	public boolean peutModifierCompteur(int i) {
		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(i);
		if (compteurCourant.getDateFin().compareTo(new Date()) < 0) {
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
	
	// Filtres	
	public String getNOM_EF_MATRICULE() {
		return "NOM_EF_MATRICULE";
	}

	public String getVAL_EF_MATRICULE() {
		return getZone(getNOM_EF_MATRICULE());
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public String getNOM_ST_AGENT_DEMANDE() {
		return "NOM_ST_AGENT_DEMANDE";
	}

	public String getVAL_ST_AGENT_DEMANDE() {
		return getZone(getNOM_ST_AGENT_DEMANDE());
	}

	public String getNOM_PB_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(MaClasse.STATUT_RECHERCHE_AGENT, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_DEMANDE(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}
}
