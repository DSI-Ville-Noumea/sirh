package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.ISirhService;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTRecherche Date de création : (01/01/03 09:35:10)
 * 
 */
public class OeAGENTRechercheDroitKiosque extends BasicProcess {

	private static final long serialVersionUID = 1L;
	private List<AgentDto> listeAgents;
	private List<AgentWithServiceDto> listeAgentsOtherService;

	private String treeAgent;

	private ISirhService sirhService;
	private IAdsService adsService;
	private AffectationDao affectationDao;
	private FichePosteDao fichePosteDao;

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
		initialiseDao();

		// Récup de l'agent activité, s'il existe
		AgentWithServiceDto approbateur = (AgentWithServiceDto) VariableActivite.recuperer(this, VariableActivite.ACTIVITE_AGENT_MAIRIE);
		VariableActivite.enlever(this, VariableActivite.ACTIVITE_AGENT_MAIRIE);

		// Récup des agents dejà  affectés
		@SuppressWarnings("unchecked")
		List<AgentDto> listAgentExistant = (List<AgentDto>) VariableActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);

		// recup des agents affectes a l approbateur 
		// dans le cas ou on gere les agents d un operateur ou viseur
		@SuppressWarnings("unchecked")
		List<AgentDto> filtreAgents = (List<AgentDto>) VariableActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST);
		
		try {
			// on cherche le service de l'approbateur
			Affectation affCourante = getAffectationDao().chercherAffectationActiveAvecAgent(approbateur.getIdAgent());
			FichePoste fpCourante = getFichePosteDao().chercherFichePoste(affCourante.getIdFichePoste());

			EntiteWithAgentWithServiceDto tree = sirhService.getListeEntiteWithAgentWithServiceDtoByIdServiceAdsWithoutAgentConnecte(
					fpCourante.getIdServiceAds(), approbateur.getIdAgent(), (null != filtreAgents ? filtreAgents : listAgentExistant));

			setTreeAgent(adsService.getCurrentWholeTreeWithAgent(tree, true, listAgentExistant, filtreAgents));
			
			setListeAgents(getListeAgentsOfEntiteTree(tree));
			
		} catch (Exception e) {
			// l'agent n' pas d'entite ADS ou d'affectation active
		}
	}
	
	private List<AgentDto> getListeAgentsOfEntiteTree(EntiteWithAgentWithServiceDto tree) {
		
		List<AgentDto> result = new ArrayList<AgentDto>();
		
		for(AgentWithServiceDto agent : tree.getListAgentWithServiceDto()) {
			AgentDto agentTmp = new AgentDto(agent);
			if(!result.contains(agentTmp)) {
				result.add(agentTmp);
			}
		}
		
		parcoursTreeToAddAgent(tree, result);
		
		return result;
	}
	
	private void parcoursTreeToAddAgent(EntiteWithAgentWithServiceDto tree, List<AgentDto> result) {
		
		for(EntiteWithAgentWithServiceDto nodeEnfant : tree.getEntiteEnfantWithAgents()) {
			
			for(AgentWithServiceDto agent : nodeEnfant.getListAgentWithServiceDto()) {
				AgentDto agentTmp = new AgentDto(agent);
				if(!result.contains(agentTmp)) {
					result.add(agentTmp);
				}
			}
			
			parcoursTreeToAddAgent(nodeEnfant, result);
		}
	}
	
	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_ANNULATION, true);
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

	public OeAGENTRechercheDroitKiosque() {
		super();
	}

	public String getJSP() {
		return "OeAGENTRechercheDroitKiosque.jsp";
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
		for (AgentDto agentCoche : getListeAgents()) {
			// si l'agent est coché
			if (getVAL_CK_AGENT_TREE(agentCoche.getIdAgent()).equals(getCHECKED_ON())) {
				listeRetour.add(agentCoche);
			}
		}
		if(null != getListeAgentsOtherService()) {
			for (AgentWithServiceDto agentCoche : getListeAgentsOtherService()) {
				// si l'agent est coché
				if (getVAL_CK_AGENT_TREE(agentCoche.getIdAgent()).equals(getCHECKED_ON())) {
					listeRetour.add(new AgentDto(agentCoche));
				}
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

	public String getNOM_CK_AGENT_TREE(int i) {
		return "NOM_CK_AGENT_" + i;
	}

	public String getVAL_CK_AGENT_TREE(int i) {
		return getZone(getNOM_CK_AGENT_TREE(i));
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public String getTreeAgent() {
		return treeAgent;
	}

	public void setTreeAgent(String treeAgent) {
		this.treeAgent = treeAgent;
	}

	public List<AgentWithServiceDto> getListeAgentsOtherService() {
		return listeAgentsOtherService;
	}

	public void setListeAgentsOtherService(List<AgentWithServiceDto> listeAgentsOtherService) {
		this.listeAgentsOtherService = listeAgentsOtherService;
	}
	
}
