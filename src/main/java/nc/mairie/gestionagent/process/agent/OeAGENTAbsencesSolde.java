package nc.mairie.gestionagent.process.agent;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.SoldeDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeAGENTAbsences Date de création : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTAbsencesSolde extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private AgentNW agentCourant;

	/**
	 * Constructeur du process OeAGENTAbsences. Date de création : (05/09/11
	 * 11:39:24)
	 * 
	 */
	public OeAGENTAbsencesSolde() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseSoldesAgent(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseSoldesAgent(HttpServletRequest request) {

		getSoldeConge(getAgentCourant());

	}

	private void getSoldeConge(AgentNW agentCourant) {
		// Solde depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		SoldeDto soldeGlobal = consuAbs.getSoldeAgent(agentCourant.getIdAgent());

		// solde congés
		addZone(getNOM_ST_SOLDE_CONGE(), soldeGlobal.getSoldeCongeAnnee() == 0 ? "&nbsp;" : soldeGlobal
				.getSoldeCongeAnnee().toString() + " j");
		addZone(getNOM_ST_SOLDE_CONGE_PREC(), soldeGlobal.getSoldeCongeAnneePrec() == 0 ? "&nbsp;" : soldeGlobal
				.getSoldeCongeAnneePrec().toString() + " j");

		// Solde recup
		int soldeRecup = soldeGlobal.getSoldeRecup().intValue();
		String soldeRecupHeure = (soldeRecup / 60) == 0 ? "" : soldeRecup / 60 + "h ";
		String soldeRecupMinute = (soldeRecup % 60) == 0 ? "&nbsp;" : soldeRecup % 60 + "m";
		addZone(getNOM_ST_SOLDE_RECUP(), soldeRecupHeure + soldeRecupMinute);

	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (29/09/11 10:03:37)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTAbsencesSolde.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ABS";
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	public String getNOM_ST_SOLDE_CONGE_PREC() {
		return "NOM_ST_SOLDE_CONGE_PREC";
	}

	public String getVAL_ST_SOLDE_CONGE_PREC() {
		return getZone(getNOM_ST_SOLDE_CONGE_PREC());
	}

	public String getNOM_ST_SOLDE_CONGE() {
		return "NOM_ST_SOLDE_CONGE";
	}

	public String getVAL_ST_SOLDE_CONGE() {
		return getZone(getNOM_ST_SOLDE_CONGE());
	}

	public String getNOM_ST_SOLDE_RECUP() {
		return "NOM_ST_SOLDE_RECUP";
	}

	public String getVAL_ST_SOLDE_RECUP() {
		return getZone(getNOM_ST_SOLDE_RECUP());
	}
}
