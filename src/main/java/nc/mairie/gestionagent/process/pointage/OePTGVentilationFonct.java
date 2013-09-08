package nc.mairie.gestionagent.process.pointage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.VentilDateDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
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
public class OePTGVentilationFonct extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	public static final int STATUT_AGENT = 2;

	private Logger logger = LoggerFactory.getLogger(OePTGVentilationFonct.class);

	private ArrayList<AgentNW> listeAgentsVentil;

	@Override
	public String getJSP() {
		return "OePTGVentilationFonct.jsp";
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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// initialisation de la liste des agents
		initialiseListeAgent();

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

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-VENT-TITU";
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

	public String getTab(int typePointage) {
		Map<Integer, AgentNW> agents = new HashMap<>();
		try {
			agents.put(9003041, AgentNW.chercherAgent(getTransaction(), "9003041"));
		} catch (Exception ex) {
			logger.debug("agent non trouvé.");
		}
		return OePTGVentilationUtils.getTabVisu(agents, 50, typePointage, true);
	}

	public String getValid() {
		return OePTGVentilationUtils.getTabValid("F");
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
		VariablesActivite.ajouter(this, "TYPE", "F");
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
		VentilDateDto ventilEnCours = getInfoVentilation("F");
		if (ventilEnCours == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(ventilEnCours.getDateVentil()));
		return true;
	}

	private VentilDateDto getInfoVentilation(String statut) {
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateVentilation = sdf.parse(getVAL_EF_DATE_DEBUT());

		// on lance la ventilation
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		return t.startVentilation(agentConnecte.getIdAgent(), dateVentilation,
				new JSONSerializer().serialize(listeIdAgents), "F", idRefTypePointage);
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
					MessageUtils.getMessage("ERR600", givenVentilationDate.dayOfWeek().getAsText()));
			return false;
		}
		return true;
	}
}
