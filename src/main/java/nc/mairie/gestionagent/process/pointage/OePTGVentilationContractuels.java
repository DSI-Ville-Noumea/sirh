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

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.pointage.dto.VentilAbsenceDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilHSupDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

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

	private ArrayList<AgentNW> listeAgentsVentil;
	private String tabVisuP;
	private String tabErreurVentil;

	private Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> hashVentilAbs;
	private Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> hashVentilHsup;

	@Override
	public String getJSP() {
		return "OePTGVentilationContractuels.jsp";
	}

	@Override
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

		addZone(getNOM_RG_TYPE(), getNOM_RB_TYPE_TOUT());

		if (etatStatut() == STATUT_RECHERCHER_AGENT_MIN) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MIN(), agt.getNoMatricule());
			}
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_MAX) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MAX(), agt.getNoMatricule());
			}
		}

		// initialisation de la liste des agents
		initialiseListeAgent();

		initialiseTabErreurVentil();

	}

	private void initialiseTabErreurVentil() {
		setTabErreurVentil(OePTGVentilationUtils.getTabErreurVentil("C"));
	}

	private void initialiseListeAgent() {
		// on recupere les agents selectionnées dans l'ecran de
		// selection
		ArrayList<AgentNW> listeAgentSelect = (ArrayList<AgentNW>) VariablesActivite.recuperer(this, "AGENTS");
		if (listeAgentSelect != null) {
			setListeAgentsVentil(new ArrayList<AgentNW>());
			getListeAgentsVentil().addAll(listeAgentSelect);
		}
		VariablesActivite.enlever(this, "AGENTS");

		int indiceAgent = 0;
		if (getListeAgentsVentil() != null) {
			for (int i = 0; i < getListeAgentsVentil().size(); i++) {
				AgentNW ag = (AgentNW) getListeAgentsVentil().get(i);
				addZone(getNOM_ST_LIB_AGENT(indiceAgent),
						ag.getNomAgent() + " " + ag.getPrenomAgent() + " (" + ag.getNoMatricule() + ")");

				indiceAgent++;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

	}

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
					VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_PTG, rawAgent);
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
		setTabErreurVentil("");
		addZone(getNOM_ST_AGENT_MIN(), "");
		addZone(getNOM_ST_AGENT_MAX(), "");

		return true;
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
		ArrayList<AgentNW> listeAg = new ArrayList<AgentNW>();
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
		AgentNW a = (AgentNW) getListeAgentsVentil().get(elemASupprimer);

		if (a != null) {
			if (getListeAgentsVentil() != null) {
				getListeAgentsVentil().remove(a);
				supprimeAgent(a);
			}
		}

		return true;
	}

	private void supprimeAgent(AgentNW a) throws Exception {
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

	public ArrayList<AgentNW> getListeAgentsVentil() {
		return listeAgentsVentil;
	}

	public void setListeAgentsVentil(ArrayList<AgentNW> listeAgentsVentil) {
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
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		VentilDateDto dto = t.getVentilationEnCours(statut);
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
			for (AgentNW ag : getListeAgentsVentil()) {
				listeIdAgents.add(Integer.valueOf(ag.getIdAgent()));
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

		// on recupere l'agent connecté
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via la
		// table SIIDMA
		AgentNW agentConnecte = null;
		if (!(u.getUserName().equals("nicno85"))) {
			Siidma user = Siidma.chercherSiidma(getTransaction(), u.getUserName().toUpperCase());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
		} else {
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateVentilation = sdf.parse(getVAL_EF_DATE_DEBUT());

		// on lance la ventilation
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		if (!t.startVentilation(agentConnecte.getIdAgent(), dateVentilation,
				new JSONSerializer().serialize(listeIdAgents), "C", idRefTypePointage)) {
			// "ERR602",
			// "La ventilation des @ n'a pu être lancée. Merci de contacter le responsable du projet.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR602", "contractuels"));
			return false;
		}
		return true;
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateVentilation = sdf.parse(getVAL_EF_DATE_DEBUT());
		DateTime givenVentilationDate = new DateTime(dateVentilation);
		if (givenVentilationDate.dayOfWeek().get() != DateTimeConstants.SUNDAY) {
			// "ERR600",
			// "La date de ventilation choisie est un @. Impossible de ventiler les pointages à une date autre qu'un dimanche."
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_MIN, true);
		return true;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_MIN(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_AFFICHER_VENTIL(int typePointage) {
		return "NOM_PB_AFFICHER_VENTIL" + typePointage;
	}

	public boolean performPB_AFFICHER_VENTIL(HttpServletRequest request, int typePointage) throws Exception {
		ArrayList<Carriere> listeCarr = new ArrayList<Carriere>();
		List<Integer> agents = new ArrayList<Integer>();

		if (getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
		}
		if (getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) && !getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX());
		}

		if (!verifieFiltres(getVAL_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX())) {
			return false;
		}
		if (!getVAL_ST_AGENT_MIN().equals("")) {
			if (getVAL_ST_AGENT_MAX().equals("")) {
				AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT_MIN());
				Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
				listeCarr.add(carr);
				addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
			} else {
				listeCarr = Carriere.listerCarriereActiveParCategorieNoMatrBetweenPourPointage(getTransaction(), "C",
						getVAL_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX());
			}
		} else {
			listeCarr = Carriere.listerCarriereActiveParCategoriePourPointage(getTransaction(), "C");
		}

		for (Carriere carr : listeCarr) {
			AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), carr.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (!agents.contains(Integer.valueOf(ag.getIdAgent()))) {
				agents.add(Integer.valueOf(ag.getIdAgent()));
			}

		}
		// on recupere la ventilation en cours
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		if (ventilEnCours == null || ventilEnCours.getIdVentilDate() == null) {
			// "ERR601", "Il n'y a pas de ventilation en cours."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR601"));
			return false;
		}
		if (typePointage == 1) {
			initialiseHashTableAbs(typePointage);
		} else if (typePointage == 2) {
			initialiseHashTableHsup(typePointage);
		} else if (typePointage == 3) {
			setTabVisuP(OePTGVentilationUtils.getTabVisu(getTransaction(), ventilEnCours.getIdVentilDate(),
					typePointage,  new JSONSerializer().serialize(agents)));
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
			AgentNW agMin = AgentNW.chercherAgent(getTransaction(), idAgentMin);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
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
			AgentNW agMax = AgentNW.chercherAgent(getTransaction(), idAgentMax);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_MAX, true);
		return true;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_MAX(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_DEVERSER() {
		return "NOM_PB_DEVERSER";
	}

	public boolean performPB_DEVERSER(HttpServletRequest request) throws Exception {
		// on recupere l'agent connecté
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via la
		// table SIIDMA
		AgentNW agentConnecte = null;
		if (!u.getUserName().equals("nicno85")) {
			Siidma user = Siidma.chercherSiidma(getTransaction(), u.getUserName().toUpperCase());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
		} else {
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		// on lance le deversement
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		if (!t.startDeversementPaie(agentConnecte.getIdAgent(), "C")) {
			// "ERR603",
			// "La déversement dans la paie des @ n'a pu être lancée. Merci de contacter le responsable du projet.");
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
		return tabErreurVentil == null ? "" : tabErreurVentil;
	}

	public void setTabErreurVentil(String tabErreurVentil) {
		this.tabErreurVentil = tabErreurVentil;
	}

	public String getTabVisuP() {
		return tabVisuP == null ? "" : tabVisuP;
	}

	public void setTabVisuP(String tabVisuP) {
		this.tabVisuP = tabVisuP;
	}

	public String getValHistoryAbs(String moisAnnee, Integer idAgent) {
		return "abs_" + moisAnnee + "_" + idAgent;
	}

	public String getHistoryAbs(String moisAnnee, Integer idAgent) throws Exception {
		List<Integer> agents = new ArrayList<Integer>();
		ArrayList<Carriere> listeCarr = Carriere.listerCarriereActiveParCategoriePourPointage(getTransaction(), "C");
		for (Carriere carr : listeCarr) {
			AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), carr.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (!agents.contains(Integer.valueOf(ag.getIdAgent()))) {
				agents.add(Integer.valueOf(ag.getIdAgent()));
			}

		}
		SimpleDateFormat moisAnneeFormat = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat moisFormat = new SimpleDateFormat("MM");
		SimpleDateFormat anneeFormat = new SimpleDateFormat("yyyy");
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, ventilEnCours.getIdVentilDate(), 1,
				new JSONSerializer().serialize(agents));
		Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> list = new Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>>();
		for (VentilAbsenceDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<>();
			cle.put(abs.getId_agent(), moisAnneeFormat.format(abs.getDateLundi()));
			List<VentilAbsenceDto> listVentilAbs = consum.getVentilationsHistory(VentilAbsenceDto.class,
					Integer.valueOf(moisFormat.format(abs.getDateLundi())),
					Integer.valueOf(anneeFormat.format(abs.getDateLundi())), 1, abs.getId_agent());
			list.put(cle, listVentilAbs);
		}
		// on construit la clé
		Hashtable<Integer, String> cle = new Hashtable<>();
		cle.put(idAgent, moisAnnee);
		// on recupere les valeurs
		List<VentilAbsenceDto> data = list.get(cle);

		int numParams = 5;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		GregorianCalendar greg = new GregorianCalendar();
		for (VentilAbsenceDto abs : data) {
			greg.setTime(abs.getDateLundi());
			ret[index][0] = "S " + String.valueOf(greg.get(Calendar.WEEK_OF_YEAR));
			ret[index][1] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesConcertees()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(abs.getMinutesConcertees());
			ret[index][2] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesNonConcertees()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(abs.getMinutesNonConcertees());
			ret[index][3] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesImmediates()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(abs.getMinutesImmediates());
			ret[index][4] = OePTGVentilationUtils.getHeureMinute(abs.getMinutesConcertees()
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

	private void initialiseHashTableAbs(int typePointage) throws Exception {
		List<Integer> agents = new ArrayList<Integer>();
		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat mois = new SimpleDateFormat("MM");
		SimpleDateFormat annee = new SimpleDateFormat("yyyy");
		ArrayList<Carriere> listeCarr = Carriere.listerCarriereActiveParCategoriePourPointage(getTransaction(), "C");
		for (Carriere carr : listeCarr) {
			AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), carr.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (!agents.contains(Integer.valueOf(ag.getIdAgent()))) {
				agents.add(Integer.valueOf(ag.getIdAgent()));
			}

		}
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, ventilEnCours.getIdVentilDate(),
				typePointage, new JSONSerializer().serialize(agents));
		Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>> hashVentilAbs = new Hashtable<Hashtable<Integer, String>, List<VentilAbsenceDto>>();
		for (VentilAbsenceDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<Integer, String>();
			cle.put(abs.getId_agent(), moisAnnee.format(abs.getDateLundi()));
			List<VentilAbsenceDto> listVentilAbs = consum.getVentilationsHistory(VentilAbsenceDto.class,
					Integer.valueOf(mois.format(abs.getDateLundi())),
					Integer.valueOf(annee.format(abs.getDateLundi())), typePointage, abs.getId_agent());
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
		ArrayList<Carriere> listeCarr = Carriere.listerCarriereActiveParCategoriePourPointage(getTransaction(), "C");
		for (Carriere carr : listeCarr) {
			AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), carr.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (!agents.contains(Integer.valueOf(ag.getIdAgent()))) {
				agents.add(Integer.valueOf(ag.getIdAgent()));
			}

		}
		SimpleDateFormat moisAnneeFormat = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat moisFormat = new SimpleDateFormat("MM");
		SimpleDateFormat anneeFormat = new SimpleDateFormat("yyyy");
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, ventilEnCours.getIdVentilDate(), 2,
				new JSONSerializer().serialize(agents));
		Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> list = new Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>>();
		for (VentilHSupDto hsup : rep) {
			Hashtable<Integer, String> cle = new Hashtable<>();
			cle.put(hsup.getId_agent(), moisAnneeFormat.format(hsup.getDateLundi()));
			List<VentilHSupDto> listVentilHsup = consum.getVentilationsHistory(VentilHSupDto.class,
					Integer.valueOf(moisFormat.format(hsup.getDateLundi())),
					Integer.valueOf(anneeFormat.format(hsup.getDateLundi())), 2, hsup.getId_agent());
			list.put(cle, listVentilHsup);
		}
		// on construit la clé
		Hashtable<Integer, String> cle = new Hashtable<>();
		cle.put(idAgent, moisAnnee);
		// on recupere les valeurs
		List<VentilHSupDto> data = list.get(cle);

		int numParams = 10;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		GregorianCalendar greg = new GregorianCalendar();
		for (VentilHSupDto hsup : data) {
			greg.setTime(hsup.getDateLundi());
			ret[index][0] = "S " + String.valueOf(greg.get(Calendar.WEEK_OF_YEAR));
			ret[index][1] = OePTGVentilationUtils.getHeureMinute(hsup.getMabs()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getMabs());
			ret[index][2] = "&nbsp;";
			ret[index][3] = OePTGVentilationUtils.getHeureMinute(hsup.getmHorsContrat()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmHorsContrat());
			ret[index][4] = OePTGVentilationUtils.getHeureMinute(hsup.getmComplementaires()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmComplementaires());
			ret[index][5] = OePTGVentilationUtils.getHeureMinute(hsup.getmSup25()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmSup25());
			ret[index][6] = OePTGVentilationUtils.getHeureMinute(hsup.getmSup50()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmSup50());
			ret[index][7] = OePTGVentilationUtils.getHeureMinute(hsup.getmNuit()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmNuit());
			ret[index][8] = OePTGVentilationUtils.getHeureMinute(hsup.getmDjf()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getmDjf());
			ret[index][9] = OePTGVentilationUtils.getHeureMinute(hsup.getM1Mai()).equals("") ? "&nbsp;"
					: OePTGVentilationUtils.getHeureMinute(hsup.getM1Mai());
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

	private void initialiseHashTableHsup(int typePointage) throws Exception {
		List<Integer> agents = new ArrayList<Integer>();
		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat mois = new SimpleDateFormat("MM");
		SimpleDateFormat annee = new SimpleDateFormat("yyyy");
		ArrayList<Carriere> listeCarr = Carriere.listerCarriereActiveParCategoriePourPointage(getTransaction(), "C");
		for (Carriere carr : listeCarr) {
			AgentNW ag = AgentNW.chercherAgentParMatricule(getTransaction(), carr.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			}
			if (!agents.contains(Integer.valueOf(ag.getIdAgent()))) {
				agents.add(Integer.valueOf(ag.getIdAgent()));
			}

		}
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		VentilDateDto ventilEnCours = getInfoVentilation("C");
		List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, ventilEnCours.getIdVentilDate(),
				typePointage, new JSONSerializer().serialize(agents));
		Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>> hashVentilHsup = new Hashtable<Hashtable<Integer, String>, List<VentilHSupDto>>();
		for (VentilHSupDto abs : rep) {
			Hashtable<Integer, String> cle = new Hashtable<Integer, String>();
			cle.put(abs.getId_agent(), moisAnnee.format(abs.getDateLundi()));
			List<VentilHSupDto> listVentilHsup = consum.getVentilationsHistory(VentilHSupDto.class,
					Integer.valueOf(mois.format(abs.getDateLundi())),
					Integer.valueOf(annee.format(abs.getDateLundi())), typePointage, abs.getId_agent());
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
}
