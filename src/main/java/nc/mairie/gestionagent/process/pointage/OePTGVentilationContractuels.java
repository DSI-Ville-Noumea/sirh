package nc.mairie.gestionagent.process.pointage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.pointage.dto.VentilAbsenceDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilHSupDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.PtgService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGVentilationContractuels extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	public static final int STATUT_AGENT = 2;
	public static final int STATUT_RECHERCHER_AGENT_MIN = 3;
	public static final int STATUT_RECHERCHER_AGENT_MAX = 4;
	private Logger logger = LoggerFactory.getLogger(OePTGVentilationContractuels.class);
	public static final int STATUT_SAISIE_PTG = 5;

	private ArrayList<Agent> listeAgentsVentil;
	private String tabVisuP;
	private String tabErreurVentil;

	private boolean showAllVentilation;

	private Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> hashVentilAbs;
	private Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> hashVentilHsup;

	private AgentDao agentDao;

	private IRadiService radiService;

	private IPtgService ptgService;

	private ISirhService sirhService;

	@Override
	public String getJSP() {
		return "OePTGVentilationContractuels.jsp";
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

		addZone(getNOM_RG_TYPE(), getNOM_RB_TYPE_TOUT());

		if (etatStatut() == STATUT_RECHERCHER_AGENT_MIN) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MIN(), agt.getNomatr().toString());
			}
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_MAX) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MAX(), agt.getNomatr().toString());
			}
		}

		// initialisation de la liste des agents
		initialiseListeAgent();

		initialiseTabErreurVentil();

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
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
	}

	private void initialiseTabErreurVentil() {
		setTabErreurVentil(OePTGVentilationUtils.getTabErreurVentil("C", ptgService));
	}

	private void initialiseListeAgent() {
		// on recupere les agents selectionnées dans l'ecran de
		// selection
		@SuppressWarnings("unchecked")
		ArrayList<Agent> listeAgentSelect = (ArrayList<Agent>) VariablesActivite.recuperer(this, "AGENTS");
		if (listeAgentSelect != null) {
			setListeAgentsVentil(new ArrayList<Agent>());
			getListeAgentsVentil().addAll(listeAgentSelect);
		}
		VariablesActivite.enlever(this, "AGENTS");

		int indiceAgent = 0;
		if (getListeAgentsVentil() != null) {
			for (int i = 0; i < getListeAgentsVentil().size(); i++) {
				Agent ag = (Agent) getListeAgentsVentil().get(i);
				addZone(getNOM_ST_LIB_AGENT(indiceAgent),
						ag.getNomAgent() + " " + ag.getPrenomAgent() + " (" + ag.getNomatr() + ")");

				indiceAgent++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

			// Si clic sur le bouton PB_RAFRAICHIR
			if (testerParametre(request, getNOM_PB_RAFRAICHIR())) {
				return performPB_RAFRAICHIR(request);
			}

			// Si clic sur le bouton PB_AJOUTER_AGENT
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT())) {
				return performPB_AJOUTER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AGENT
			if (getListeAgentsVentil() != null) {
				for (int i = 0; i < getListeAgentsVentil().size(); i++) {
					if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT(i))) {
						return performPB_SUPPRIMER_AGENT(request, i);
					}
				}
			}

			// Si clic sur le bouton PB_VENTILER
			if (testerParametre(request, getNOM_PB_VENTILER())) {
				return performPB_VENTILER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_MIN
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MIN())) {
				return performPB_RECHERCHER_AGENT_MIN(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MIN
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(request);
			}

			// Si clic sur le bouton PB_AFFICHER_VENTIL
			for (int i = 1; i < 4; i++) {
				if (testerParametre(request, getNOM_PB_AFFICHER_VENTIL(i))) {
					return performPB_AFFICHER_VENTIL(request, i);
				}
			}

			// Si clic sur le bouton PB_AFFICHER_TOUT_VENTIL
			for (int i = 1; i < 4; i++) {
				if (testerParametre(request, getNOM_PB_AFFICHER_TOUT_VENTIL(i))) {
					return performPB_AFFICHER_TOUT_VENTIL(request, i);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_MAX
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MAX())) {
				return performPB_RECHERCHER_AGENT_MAX(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MAX
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(request);
			}
			for (String s : (Set<String>) request.getParameterMap().keySet()) {
				// Si clic sur le bouton edit
				if (s.startsWith("JMP_SAISIE:")) {
					StringTokenizer tok = new StringTokenizer(s, ":");
					tok.nextToken();
					VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LUNDI_PTG, OePTGVentilationUtils
							.getMondayFromWeekNumberAndYear(Integer.parseInt(tok.nextToken()),
									Integer.parseInt(tok.nextToken())));
					String rawAgent = tok.nextToken();
					logger.info("\n rawAgent=" + rawAgent);
					int index1 = 0;
					if (rawAgent.startsWith("900"))
						index1 = 3;
					rawAgent = rawAgent.substring(index1, rawAgent.lastIndexOf("."));
					logger.info("\n rawAgent=" + rawAgent);
					VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_PTG, Integer.valueOf(rawAgent));
					setStatut(STATUT_SAISIE_PTG, true);
					return true;
				}
			}
			// Si clic sur le bouton PB_DEVERSER
			if (testerParametre(request, getNOM_PB_DEVERSER())) {
				return performPB_DEVERSER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-VENT-NON-TITU";
	}

	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_VENTILATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_HS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_PRIMES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_ABS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_VALIDATION(), Const.CHAINE_VIDE);
		setTabVisuP(null);
		setHashVentilAbs(null);
		setHashVentilHsup(null);
		setTabErreurVentil(Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_MIN(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_MAX(), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Process incoming requests for information
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
	public boolean recupererOnglet(javax.servlet.http.HttpServletRequest request) throws Exception {

		if (super.recupererOnglet(request)) {
			performPB_RESET(request);
			return true;
		}
		return false;
	}

	public String getNOM_ST_ACTION_VENTILATION() {
		return "NOM_ST_ACTION_VENTILATION";
	}

	public String getNOM_ST_ACTION_HS() {
		return "NOM_ST_ACTION_HS";
	}

	public String getNOM_ST_ACTION_PRIMES() {
		return "NOM_ST_ACTION_PRIMES";
	}

	public String getNOM_ST_ACTION_ABS() {
		return "NOM_ST_ACTION_ABS";
	}

	public String getNOM_ST_ACTION_VALIDATION() {
		return "NOM_ST_ACTION_VALIDATION";
	}

	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	public String getNOM_PB_AJOUTER_AGENT() {
		return "NOM_PB_AJOUTER_AGENT";
	}

	public boolean performPB_AJOUTER_AGENT(HttpServletRequest request) throws Exception {
		ArrayList<Agent> listeAg = new ArrayList<Agent>();
		if (getListeAgentsVentil() != null) {
			listeAg.addAll(getListeAgentsVentil());
		}
		VariablesActivite.ajouter(this, "LISTEAGENT", listeAg);
		VariablesActivite.ajouter(this, "TYPE", "C");
		setStatut(STATUT_AGENT, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_AGENT(int i) {
		return "NOM_PB_SUPPRIMER_AGENT" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT(HttpServletRequest request, int elemASupprimer) throws Exception {
		Agent a = (Agent) getListeAgentsVentil().get(elemASupprimer);

		if (a != null) {
			if (getListeAgentsVentil() != null) {
				getListeAgentsVentil().remove(a);
				supprimeAgent(a);
			}
		}

		return true;
	}

	private void supprimeAgent(Agent a) throws Exception {
		if (getListeAgentsVentil().contains(a)) {
			getListeAgentsVentil().remove(a);
		}

	}

	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT" + i;
	}

	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	public ArrayList<Agent> getListeAgentsVentil() {
		return listeAgentsVentil;
	}

	public void setListeAgentsVentil(ArrayList<Agent> listeAgentsVentil) {
		this.listeAgentsVentil = listeAgentsVentil;
	}

	public boolean ventilationExist() {
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		if (ventilEnCours == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(ventilEnCours.getDateVentil()));
		return true;
	}

	public VentilDateDto getInfoVentilation(String statut) {
		VentilDateDto dto = ptgService.getVentilationEnCours(statut);
		return dto;
	}

	public String getNOM_RG_TYPE() {
		return "NOM_RG_TYPE";
	}

	public String getVAL_RG_TYPE() {
		return getZone(getNOM_RG_TYPE());
	}

	public String getNOM_RB_TYPE_HS() {
		return "NOM_RB_TYPE_HS";
	}

	public String getNOM_RB_TYPE_PRIME() {
		return "NOM_RB_TYPE_PRIME";
	}

	public String getNOM_RB_TYPE_ABS() {
		return "NOM_RB_TYPE_ABS";
	}

	public String getNOM_RB_TYPE_TOUT() {
		return "NOM_RB_TYPE_TOUT";
	}

	public String getNOM_PB_VENTILER() {
		return "NOM_PB_VENTILER";
	}

	public boolean performPB_VENTILER(HttpServletRequest request) throws Exception {
		if (!performControlerDateVentilation()) {
			return false;
		}

		// on construit la liste des agents
		List<Integer> listeIdAgents = new ArrayList<>();
		if (getListeAgentsVentil() != null) {
			for (Agent ag : getListeAgentsVentil()) {
				listeIdAgents.add(ag.getIdAgent());
			}
		}

		// on recuepere le type de ventilation (prime, hs,abs..)
		String idRefTypePointage = null;
		if (getVAL_RG_TYPE().equals(getNOM_RB_TYPE_ABS())) {
			idRefTypePointage = String.valueOf(RefTypePointageEnum.ABSENCE.getValue());
		} else if (getVAL_RG_TYPE().equals(getNOM_RB_TYPE_PRIME())) {
			idRefTypePointage = String.valueOf(RefTypePointageEnum.PRIME.getValue());
		} else if (getVAL_RG_TYPE().equals(getNOM_RB_TYPE_HS())) {
			idRefTypePointage = String.valueOf(RefTypePointageEnum.H_SUP.getValue());
		}

		// on recupere l'agent connecte
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		agentConnecte = getAgentDao().chercherAgentParMatricule(
				radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on transforme la date
		Date dateVentilation = transformDate(getVAL_EF_DATE_DEBUT());

		// on lance la ventilation
		if (!ptgService.startVentilation(agentConnecte.getIdAgent(), dateVentilation,
				new JSONSerializer().exclude("*.class").serialize(listeIdAgents), "C", idRefTypePointage)) {
			// "ERR602",
			// "La ventilation des @ n'a pu être lancee. Merci de contacter le responsable du projet.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR602", "contractuels"));
			return false;
		}
		return true;
	}

	// #14346
	private Date transformDate(String dateVentilationStr) throws ParseException {
		Date dateVentilation = null;
		if (Services.estNumerique(dateVentilationStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			dateVentilation = sdf.parse(dateVentilationStr);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			dateVentilation = sdf.parse(dateVentilationStr);
		}
		return dateVentilation;
	}

	private boolean performControlerDateVentilation() throws ParseException {

		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_DEBUT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_EF_DATE_DEBUT())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de ventilation"));
			return false;
		}

		// Check that the ventilation date must be a sunday. Otherwise stop here
		Date dateVentilation = transformDate(getVAL_EF_DATE_DEBUT());
		DateTime givenVentilationDate = new DateTime(dateVentilation);
		if (givenVentilationDate.dayOfWeek().get() != DateTimeConstants.SUNDAY) {
			// "ERR600",
			// "La date de ventilation choisie est un @. Impossible de ventiler les pointages a une date autre qu'un dimanche."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR600", givenVentilationDate.dayOfWeek().getAsText(Locale.FRANCE)));
			return false;
		}
		return true;
	}

	public String getNOM_ST_AGENT_MIN() {
		return "NOM_ST_AGENT_MIN";
	}

	public String getVAL_ST_AGENT_MIN() {
		return getZone(getNOM_ST_AGENT_MIN());
	}

	public String getNOM_PB_RECHERCHER_AGENT_MIN() {
		return "NOM_PB_RECHERCHER_AGENT_MIN";
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN";
	}

	public boolean performPB_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_MIN, true);
		return true;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_MIN(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_AFFICHER_VENTIL(int typePointage) {
		return "NOM_PB_AFFICHER_VENTIL" + typePointage;
	}

	public boolean performPB_AFFICHER_VENTIL(HttpServletRequest request, int typePointage) throws Exception {
		setShowAllVentilation(false);

		if (getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
		}
		if (getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) && !getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX());
		}

		if (!verifieFiltres(getVAL_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX())) {
			return false;
		}

		VentilDateDto ventilEnCours = getInfoVentilation("C");
		// on recupere la ventilation en cours
		if (ventilEnCours == null || ventilEnCours.getIdVentilDate() == null) {
			// "ERR601", "Il n'y a pas de ventilation en cours."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR601"));
			return false;
		}

		List<Integer> agents = ptgService.getListeAgentsForShowVentilation(ventilEnCours.getIdVentilDate(),
				new Integer(typePointage), "C", ventilEnCours.getDateVentil(), getVAL_ST_AGENT_MIN(),
				getVAL_ST_AGENT_MAX(), false);

		if (typePointage == 1) {
			initialiseHashTableAbs(typePointage, agents, false);
		} else if (typePointage == 2) {
			initialiseHashTableHsup(typePointage, agents, false);
		} else if (typePointage == 3) {
			setTabVisuP(OePTGVentilationUtils.getTabVisu(getTransaction(), ventilEnCours.getIdVentilDate(),
					typePointage, new JSONSerializer().exclude("*.class").serialize(agents), getAgentDao(), false,
					ptgService));
		}
		return true;
	}

	public boolean verifieFiltres(String agentMin, String agentMax) throws Exception {

		// on verifie que l'id agent min saisie existe
		if (!agentMin.equals(Const.CHAINE_VIDE)) {
			if (!Services.estNumerique(agentMin)) {
				// "ERR992", "La zone @ doit être numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Agent min"));
				return false;
			}
			String idAgentMin = "900" + agentMin;
			try {
				@SuppressWarnings("unused")
				Agent agMin = getAgentDao().chercherAgent(Integer.valueOf(idAgentMin));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgentMin));
				return false;
			}
		}
		// on verifie que l'id agent max saisie existe
		if (!agentMax.equals(Const.CHAINE_VIDE)) {
			if (!Services.estNumerique(agentMax)) {
				// "ERR992", "La zone @ doit être numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Agent max"));
				return false;
			}
			String idAgentMax = "900" + agentMax;
			try {
				@SuppressWarnings("unused")
				Agent agMax = getAgentDao().chercherAgent(Integer.valueOf(idAgentMax));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgentMax));
				return false;
			}
		}
		return true;
	}

	public String getNOM_ST_AGENT_MAX() {
		return "NOM_ST_AGENT_MAX";
	}

	public String getVAL_ST_AGENT_MAX() {
		return getZone(getNOM_ST_AGENT_MAX());
	}

	public String getNOM_PB_RECHERCHER_AGENT_MAX() {
		return "NOM_PB_RECHERCHER_AGENT_MAX";
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX";
	}

	public boolean performPB_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_MAX, true);
		return true;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_MAX(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_DEVERSER() {
		return "NOM_PB_DEVERSER";
	}

	public boolean performPB_DEVERSER(HttpServletRequest request) throws Exception {
		// on recupere l'agent connecte
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		agentConnecte = getAgentDao().chercherAgentParMatricule(
				radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on lance le deversement
		if (!ptgService.startDeversementPaie(agentConnecte.getIdAgent(), "C")) {
			// "ERR603",
			// "La déversement dans la paie des @ n'a pu être lancee. Merci de contacter le responsable du projet.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR603", "contractuels"));
			return false;
		}
		return true;
	}

	public String getNOM_PB_RAFRAICHIR() {
		return "NOM_PB_RAFRAICHIR";
	}

	public boolean performPB_RAFRAICHIR(HttpServletRequest request) throws Exception {
		return true;
	}

	public String getTabErreurVentil() {
		return tabErreurVentil == null ? Const.CHAINE_VIDE : tabErreurVentil;
	}

	public void setTabErreurVentil(String tabErreurVentil) {
		this.tabErreurVentil = tabErreurVentil;
	}

	public String getTabVisuP() {
		return tabVisuP == null ? Const.CHAINE_VIDE : tabVisuP;
	}

	public void setTabVisuP(String tabVisuP) {
		this.tabVisuP = tabVisuP;
	}

	public String getValHistoryAbs(String moisAnnee, Integer idAgent) {
		return "abs_" + moisAnnee + "_" + idAgent;
	}

	public String getHistoryAbs(String moisAnnee, Integer idAgent) throws Exception {
		List<Integer> agents = new ArrayList<Integer>();
		agents.add(idAgent);

		SimpleDateFormat moisAnneeFormat = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat moisFormat = new SimpleDateFormat("MM");
		SimpleDateFormat anneeFormat = new SimpleDateFormat("yyyy");
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilAbsenceDto> rep = ptgService.getVentilations(VentilAbsenceDto.class,
				ventilEnCours.getIdVentilDate(), 1, new JSONSerializer().serialize(agents), isShowAllVentilation());
		Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> list = new Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>>();
		for (VentilAbsenceDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<>();
			cle.put(abs.getId_agent(), moisAnneeFormat.format(abs.getDateLundi()));
			List<VentilAbsenceDto> listVentilAbs = ptgService.getVentilationsHistory(VentilAbsenceDto.class,
					Integer.valueOf(moisFormat.format(abs.getDateLundi())),
					Integer.valueOf(anneeFormat.format(abs.getDateLundi())), 1, abs.getId_agent(),
					isShowAllVentilation(), abs.getIdVentilDate());
			list.put(cle, listVentilAbs);
		}
		// on construit la cle
		Hashtable<Integer, String> cle = new Hashtable<>();
		cle.put(idAgent, moisAnnee);
		// on recupere les valeurs
		List<VentilAbsenceDto> data = list.get(cle);

		int numParams = 5;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		for (VentilAbsenceDto abs : data) {
			greg.setTime(abs.getDateLundi());
			ret[index][0] = "S " + String.valueOf(greg.get(Calendar.WEEK_OF_YEAR));
			ret[index][1] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesConcertees()).equals(Const.CHAINE_VIDE) ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(abs.getMinutesConcertees());
			ret[index][2] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesNonConcertees()).equals(
					Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(abs.getMinutesNonConcertees());
			ret[index][3] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesImmediates()).equals(Const.CHAINE_VIDE) ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(abs.getMinutesImmediates());
			ret[index][4] = OePTGVentilationUtils.getHeureMinuteWithAffichageZero(abs.getMinutesConcertees()
					+ abs.getMinutesNonConcertees() + abs.getMinutesImmediates());
			index++;
		}

		StringBuilder strret = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			// strret.append("[");
			for (int j = 0; j < numParams; j++) {
				strret.append(ret[i][j]).append(",");
			}
			strret.deleteCharAt(strret.lastIndexOf(","));
			strret.append("|");
		}

		if (strret.lastIndexOf("|") != -1)
			strret.deleteCharAt(strret.lastIndexOf("|"));

		return strret.toString();
	}

	private void initialiseHashTableAbs(int typePointage, List<Integer> agents, boolean allVentilation)
			throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat mois = new SimpleDateFormat("MM");
		SimpleDateFormat annee = new SimpleDateFormat("yyyy");

		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilAbsenceDto> rep = ptgService.getVentilations(VentilAbsenceDto.class,
				ventilEnCours.getIdVentilDate(), typePointage, new JSONSerializer().exclude("*.class")
						.serialize(agents), allVentilation);
		Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> hashVentilAbs = new Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>>();
		for (VentilAbsenceDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<Integer, String>();
			cle.put(abs.getId_agent(), moisAnnee.format(abs.getDateLundi()));
			List<VentilAbsenceDto> listVentilAbs = ptgService.getVentilationsHistory(VentilAbsenceDto.class,
					Integer.valueOf(mois.format(abs.getDateLundi())),
					Integer.valueOf(annee.format(abs.getDateLundi())), typePointage, abs.getId_agent(),
					isShowAllVentilation(), abs.getIdVentilDate());
			hashVentilAbs.put(cle, listVentilAbs);
		}
		setHashVentilAbs(hashVentilAbs);
	}

	private void setHashVentilAbs(Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> hashVentilAbs2) {
		this.hashVentilAbs = hashVentilAbs2;
	}

	public Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> getHashVentilAbs() throws Exception {
		return hashVentilAbs == null ? new Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>>()
				: hashVentilAbs;
	}

	public String getValHistoryHsup(String moisAnnee, Integer idAgent) {
		return "hsup_" + moisAnnee + "_" + idAgent;
	}

	public String getHistoryHsup(String moisAnnee, Integer idAgent) throws Exception {
		List<Integer> agents = new ArrayList<Integer>();
		agents.add(idAgent);

		SimpleDateFormat moisAnneeFormat = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat moisFormat = new SimpleDateFormat("MM");
		SimpleDateFormat anneeFormat = new SimpleDateFormat("yyyy");
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilHSupDto> rep = ptgService.getVentilations(VentilHSupDto.class, ventilEnCours.getIdVentilDate(), 2,
				new JSONSerializer().exclude("*.class").serialize(agents), isShowAllVentilation());
		Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> list = new Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>>();
		for (VentilHSupDto hsup : rep) {
			Hashtable<Integer, String> cle = new Hashtable<>();
			cle.put(hsup.getId_agent(), moisAnneeFormat.format(hsup.getDateLundi()));
			List<VentilHSupDto> listVentilHsup = ptgService.getVentilationsHistory(VentilHSupDto.class,
					Integer.valueOf(moisFormat.format(hsup.getDateLundi())),
					Integer.valueOf(anneeFormat.format(hsup.getDateLundi())), 2, hsup.getId_agent(),
					isShowAllVentilation(), hsup.getIdVentilDate());
			list.put(cle, listVentilHsup);
		}
		// on construit la cle
		Hashtable<Integer, String> cle = new Hashtable<>();
		cle.put(idAgent, moisAnnee);
		// on recupere les valeurs
		List<VentilHSupDto> data = list.get(cle);

		int numParams = 12;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		for (VentilHSupDto hsup : data) {
			if (0 < hsup.getmHorsContrat() - hsup.getmRecuperees()) {
				greg.setTime(hsup.getDateLundi());
				ret[index][0] = "S " + String.valueOf(greg.get(Calendar.WEEK_OF_YEAR));
				ret[index][1] = OePTGVentilationUtils.getHeureMinute(hsup.getMabs() + hsup.getMabsAs400()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getMabs()
						+ hsup.getMabsAs400());
				ret[index][2] = OePTGVentilationUtils.getHeureMinute(hsup.getMabs()).equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: OePTGVentilationUtils.getHeureMinute(hsup.getMabs());
				ret[index][3] = OePTGVentilationUtils.getHeureMinute(hsup.getMabsAs400()).equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: OePTGVentilationUtils.getHeureMinute(hsup.getMabsAs400());
				ret[index][4] = "&nbsp;";
				ret[index][5] = OePTGVentilationUtils.getHeureMinute(hsup.getmHorsContrat() - hsup.getmRecuperees())
						.equals(Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup
						.getmHorsContrat() - hsup.getmRecuperees());
				ret[index][6] = OePTGVentilationUtils.getHeureMinute(hsup.getmNormales() - hsup.getmNormalesR())
						.equals(Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup
						.getmNormales() - hsup.getmNormalesR());
				ret[index][7] = OePTGVentilationUtils.getHeureMinute(hsup.getmSup25() - hsup.getmSup25R()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getmSup25()
						- hsup.getmSup25R());
				ret[index][8] = OePTGVentilationUtils.getHeureMinute(hsup.getmSup50() - hsup.getmSup50R()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getmSup50()
						- hsup.getmSup50R());
				ret[index][9] = OePTGVentilationUtils.getHeureMinute(hsup.getmNuit() - hsup.getmNuitR()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getmNuit()
						- hsup.getmNuitR());
				ret[index][10] = OePTGVentilationUtils.getHeureMinute(hsup.getmDjf() - hsup.getmDjfR()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getmDjf()
						- hsup.getmDjfR());
				ret[index][11] = OePTGVentilationUtils.getHeureMinute(hsup.getM1Mai() - hsup.getM1maiR()).equals(
						Const.CHAINE_VIDE) ? "&nbsp;" : OePTGVentilationUtils.getHeureMinute(hsup.getM1Mai()
						- hsup.getM1maiR());
				index++;
			}
		}

		StringBuilder strret = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			if (null != ret[i][5]) {
				for (int j = 0; j < numParams; j++) {
					strret.append(ret[i][j]).append(",");
				}
				strret.deleteCharAt(strret.lastIndexOf(","));
				strret.append("|");
			}
		}
		if (strret.lastIndexOf("|") != -1)
			strret.deleteCharAt(strret.lastIndexOf("|"));

		return strret.toString();
	}

	private void initialiseHashTableHsup(int typePointage, List<Integer> agents, boolean allVentilation)
			throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat mois = new SimpleDateFormat("MM");
		SimpleDateFormat annee = new SimpleDateFormat("yyyy");

		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilHSupDto> rep = ptgService.getVentilations(VentilHSupDto.class, ventilEnCours.getIdVentilDate(),
				typePointage, new JSONSerializer().exclude("*.class").serialize(agents), allVentilation);
		Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> hashVentilHsup = new Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>>();
		for (VentilHSupDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<Integer, String>();
			cle.put(abs.getId_agent(), moisAnnee.format(abs.getDateLundi()));
			List<VentilHSupDto> listVentilHsup = ptgService.getVentilationsHistory(VentilHSupDto.class,
					Integer.valueOf(mois.format(abs.getDateLundi())),
					Integer.valueOf(annee.format(abs.getDateLundi())), typePointage, abs.getId_agent(),
					isShowAllVentilation(), abs.getIdVentilDate());
			hashVentilHsup.put(cle, listVentilHsup);
		}
		setHashVentilHsup(hashVentilHsup);
	}

	private void setHashVentilHsup(Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> hashVentilHsup2) {
		this.hashVentilHsup = hashVentilHsup2;
	}

	public Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> getHashVentilHsup() throws Exception {
		return hashVentilHsup == null ? new Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>>()
				: hashVentilHsup;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public Agent getAgent(Integer idAgent) throws Exception {
		return getAgentDao().chercherAgent(idAgent);
	}

	public String getNOM_PB_AFFICHER_TOUT_VENTIL(int typePointage) {
		return "NOM_PB_AFFICHER_TOUT_VENTIL" + typePointage;
	}

	public boolean performPB_AFFICHER_TOUT_VENTIL(HttpServletRequest request, int typePointage) throws Exception {
		setShowAllVentilation(true);

		if (getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
		}
		if (getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) && !getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX());
		}

		if (!verifieFiltres(getVAL_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX())) {
			return false;
		}

		VentilDateDto ventilEnCours = getInfoVentilation("C");
		// on recupere la ventilation en cours
		if (ventilEnCours == null || ventilEnCours.getIdVentilDate() == null) {
			// "ERR601", "Il n'y a pas de ventilation en cours."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR601"));
			return false;
		}

		List<Integer> agents = ptgService.getListeAgentsForShowVentilation(ventilEnCours.getIdVentilDate(),
				new Integer(typePointage), "C", ventilEnCours.getDateVentil(), getVAL_ST_AGENT_MIN(),
				getVAL_ST_AGENT_MAX(), true);

		if (typePointage == 1) {
			initialiseHashTableAbs(typePointage, agents, true);
		} else if (typePointage == 2) {
			initialiseHashTableHsup(typePointage, agents, true);
		} else if (typePointage == 3) {
			setTabVisuP(OePTGVentilationUtils.getTabVisu(getTransaction(), ventilEnCours.getIdVentilDate(),
					typePointage, new JSONSerializer().exclude("*.class").serialize(agents), getAgentDao(), true,
					ptgService));
		}
		return true;
	}

	public boolean isShowAllVentilation() {
		return showAllVentilation;
	}

	public void setShowAllVentilation(boolean showAllVentilation) {
		this.showAllVentilation = showAllVentilation;
	}

	public double getWeekBase(Agent agent, Date dateLundi) throws Exception {
		BaseHorairePointageDto dto = sirhService.getBaseHorairePointageAgent(agent.getIdAgent(), dateLundi);
		return dto.getBaseLegale();
	}
}
