package nc.mairie.gestionagent.process.absence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.spring.ws.SirhWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OeABSRestitution extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OeABSRestitution.class);
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 1;

	public String ACTION_AJOUT = "Ajout d'un agents.";

	private List<Agent> listeAgent = new ArrayList<Agent>();
	private AgentDao agentDao;

	@Override
	public String getJSP() {
		return "OeABSRestitution.jsp";
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return Const.CHAINE_VIDE;
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

		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATION) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATION(), agt.getNomatr().toString());
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_SUPPRIMER_AGENT
			for (int j = 0; j < getListeAgent().size(); j++) {
				Integer i = getListeAgent().get(j).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT(i))) {
					return performPB_SUPPRIMER_AGENT(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_AGENT
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT())) {
				return performPB_AJOUTER_AGENT(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_CREATION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATION())) {
				return performPB_RECHERCHER_AGENT_CREATION(request);
			}
			// Si clic sur le bouton PB_LANCER_RESTITUTION
			if (testerParametre(request, getNOM_PB_LANCER_RESTITUTION())) {
				return performPB_LANCER_RESTITUTION(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNomEcran() {
		return "ECR-ABS-VISU";
	}

	public String getNOM_ST_DATE_RESTITUTION() {
		return "NOM_ST_DATE_RESTITUTION";
	}

	public String getVAL_ST_DATE_RESTITUTION() {
		return getZone(getNOM_ST_DATE_RESTITUTION());
	}

	public String getNOM_RG_TYPE_RESTITUTION() {
		return "NOM_RG_TYPE_RESTITUTION";
	}

	public String getVAL_RG_TYPE_RESTITUTION() {
		return getZone(getNOM_RG_TYPE_RESTITUTION());
	}

	public String getNOM_RB_TYPE_MATIN() {
		return "NOM_RB_TYPE_MATIN";
	}

	public String getNOM_RB_TYPE_AM() {
		return "NOM_RB_TYPE_AM";
	}

	public String getNOM_RB_TYPE_JOURNEE() {
		return "NOM_RB_TYPE_JOURNEE";
	}

	public List<Agent> getListeAgent() {
		return listeAgent;
	}

	public void setListeAgent(List<Agent> listeAgent) {
		this.listeAgent = listeAgent;
	}

	public String getNOM_PB_SUPPRIMER_AGENT(int i) {
		return "NOM_PB_SUPPRIMER_AGENT" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT(HttpServletRequest request, int elemASupprimer) throws Exception {
		Agent a = getAgentDao().chercherAgent(elemASupprimer);
		if (a != null) {
			if (getListeAgent().contains(a)) {
				getListeAgent().remove(a);
			}
		}
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_ST_NOMATR_AGENT(int i) {
		return "NOM_ST_NOMATR_AGENT" + i;
	}

	public String getVAL_ST_NOMATR_AGENT(int i) {
		return getZone(getNOM_ST_NOMATR_AGENT(i));
	}

	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT" + i;
	}

	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	public String getNOM_PB_AJOUTER_AGENT() {
		return "NOM_PB_AJOUTER_AGENT";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public boolean performPB_AJOUTER_AGENT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_AJOUT);
		return true;
	}

	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		String idAgent = Const.CHAINE_VIDE;
		// La date est obligatoire pour tester si l'agent est en base A ou D à
		// cette date
		// date
		if (getZone(getNOM_ST_DATE_RESTITUTION()).equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de restitution"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_ST_DATE_RESTITUTION()))) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de restitution"));
			return false;
		}

		if (getVAL_ST_AGENT_CREATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_CREATION();
			try {
				Agent a = getAgentDao().chercherAgent(Integer.valueOf(idAgent));
				if (a != null) {
					// on verifie si l'agent est en base A ou D
					String dateRestitution = Services.formateDate(getVAL_ST_DATE_RESTITUTION());
					SirhWSConsumer consu = new SirhWSConsumer();
					RefTypeSaisiCongeAnnuelDto dtoIdConge = consu.getBaseHoraireAbsence(a.getIdAgent(),
							new SimpleDateFormat("dd/MM/yyyy").parse(dateRestitution));
					if (dtoIdConge == null || dtoIdConge.getIdRefTypeSaisiCongeAnnuel() == null) {
						// "ERR805",
						// "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR805", a.getNomatr().toString()));
						return false;

					}
					SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
					TypeAbsenceDto dtoConge = consuAbs.getTypeAbsence(dtoIdConge.getIdRefTypeSaisiCongeAnnuel());
					if (!(dtoConge.getTypeSaisiCongeAnnuelDto().getCodeBaseHoraireAbsence().equals("A") || dtoConge
							.getTypeSaisiCongeAnnuelDto().getCodeBaseHoraireAbsence().equals("D"))) {
						// "ERR807",
						// "Cet agent n'a pas pour base congé A ou D. Il ne peut avoir de restitution de congés annuel."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR807"));
						return false;
					}

					if (!getListeAgent().contains(a)) {
						getListeAgent().add(a);
					}
				}
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;
			}
		}

		// On vide la zone
		addZone(getNOM_ST_AGENT_CREATION(), Const.CHAINE_VIDE);
		refreshListeAgent();

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void refreshListeAgent() {

		for (Agent abs : getListeAgent()) {
			Integer i = abs.getIdAgent();
			try {
				Agent ag = getAgentDao().chercherAgent(i);

				addZone(getNOM_ST_NOMATR_AGENT(i), ag.getNomatr().toString());
				addZone(getNOM_ST_LIB_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent());

			} catch (Exception e) {
				continue;
			}
		}
	}

	public String getNOM_ST_AGENT_CREATION() {
		return "NOM_ST_AGENT_CREATION";
	}

	public String getVAL_ST_AGENT_CREATION() {
		return getZone(getNOM_ST_AGENT_CREATION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_CREATION() {
		return "NOM_PB_RECHERCHER_AGENT_CREATION";
	}

	public boolean performPB_RECHERCHER_AGENT_CREATION(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATION, true);
		return true;
	}

	public String getNOM_PB_LANCER_RESTITUTION() {
		return "NOM_PB_LANCER_RESTITUTION";
	}

	public boolean performPB_LANCER_RESTITUTION(HttpServletRequest request) throws Exception {
		// vérification de la validité du formulaire
		if (!performControlerChamps(request)) {
			return false;
		}

		logger.debug("Appel au WS pour inserer une ligne pour le lancement du job");
		// TODO appel au WS

		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// type matin,am, journée
		if (getZone(getNOM_RG_TYPE_RESTITUTION()).equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type de restitution"));
			return false;
		}
		// date
		if (getZone(getNOM_ST_DATE_RESTITUTION()).equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de restitution"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_ST_DATE_RESTITUTION()))) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de restitution"));
			return false;
		}
		// agents
		if (getListeAgent().size() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "liste des agents"));
			return false;
		}
		return false;
	}
}
