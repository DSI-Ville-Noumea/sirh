package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OeAGENTRecherche Date de création : (01/01/03 09:35:10)
 * 
 */
public class OeAGENTRechercheDroitKiosqueAgentApprobateur extends BasicProcess {

	private static final long serialVersionUID = 1L;
	private List<AgentDto> listeAgents;

	public void initialiseZones(HttpServletRequest request) throws Exception {

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Récup des agents de l'operateur
		@SuppressWarnings("unchecked")
		List<AgentDto> listAgentExistantOperateur = (List<AgentDto>) VariableActivite.recuperer(this,
				VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST);

		// Récup des agents de l'approbateur
		@SuppressWarnings("unchecked")
		List<AgentDto> listAgentExistantApprobateur = (List<AgentDto>) VariableActivite.recuperer(this,
				VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);

		setListeAgents(listAgentExistantApprobateur);
		afficheListe(listAgentExistantOperateur, listAgentExistantApprobateur);

	}

	private void afficheListe(List<AgentDto> listAgentExistantOperateur, List<AgentDto> listAgentExistantApprobateur) {
		if (getListeAgents() != null) {
			for (int i = 0; i < getListeAgents().size(); i++) {
				AgentDto agent = (AgentDto) getListeAgents().get(i);

				addZone(getNOM_ST_MATR(agent.getIdAgent()),
						agent.getIdAgent().toString().substring(3, agent.getIdAgent().toString().length()));
				addZone(getNOM_ST_NOM(agent.getIdAgent()), agent.getNom());
				addZone(getNOM_ST_PRENOM(agent.getIdAgent()), agent.getPrenom());
				addZone(getNOM_CK_AGENT(agent.getIdAgent()),
						listAgentExistantOperateur.contains(agent) ? getCHECKED_ON() : getCHECKED_OFF());

			}
		}

	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getNomEcran() {
		return "ECR-DROIT-KIOSQUE";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_OK
			if (testerParametre(request, getNOM_PB_OK())) {
				return performPB_OK(request);
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	public OeAGENTRechercheDroitKiosqueAgentApprobateur() {
		super();
	}

	public String getJSP() {
		return "OeAGENTRechercheDroitKiosqueAgentApprobateur.jsp";
	}

	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR" + i;
	}

	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	public String getNOM_ST_PRENOM(int i) {
		return "NOM_ST_PRENOM" + i;
	}

	public String getVAL_ST_PRENOM(int i) {
		return getZone(getNOM_ST_PRENOM(i));
	}

	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	public String getNOM_PB_OK() {
		return "NOM_PB_OK";
	}

	public boolean performPB_OK(HttpServletRequest request) throws Exception {
		List<AgentDto> listeRetour = new ArrayList<AgentDto>();
		for (AgentDto list : getListeAgents()) {
			// si l'agent est coché
			if (getVAL_CK_AGENT(list.getIdAgent()).equals(getCHECKED_ON())) {
				listeRetour.add(list);
			}
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, listeRetour);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	public List<AgentDto> getListeAgents() {
		return listeAgents;
	}

	public void setListeAgents(List<AgentDto> listeAgents) {
		this.listeAgents = listeAgents;
	}

	public String getNOM_CK_AGENT(int i) {
		return "NOM_CK_AGENT_" + i;
	}

	public String getVAL_CK_AGENT(int i) {
		return getZone(getNOM_CK_AGENT(i));
	}
}
