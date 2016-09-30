package nc.mairie.gestionagent.process.poste;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.ActionFdpJob;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.ActionFdpJobDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFicheEmploi Date de création : (21/06/11 16:27:37)
 * 
 */
public class OePOSTESupressionFDP extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ActionFdpJob> listeJobSuppression;
	private ActionFdpJobDao actionFdpJobDao;
	private AgentDao agentDao;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (22/06/11 13:59:14)
	 * 
	 */
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
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getActionFdpJobDao() == null) {
			setActionFdpJobDao(new ActionFdpJobDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/06/11 16:27:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AFFICHER
			if (testerParametre(request, getNOM_PB_AFFICHER())) {
				return performPB_AFFICHER(request);
			}
			// Si clic sur le bouton PB_AFFICHER_ERREUR
			if (testerParametre(request, getNOM_PB_AFFICHER_ERREUR())) {
				return performPB_AFFICHER_ERREUR(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFicheEmploi. Date de création : (05/09/11
	 * 14:10:47)
	 * 
	 */
	public OePOSTESupressionFDP() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (05/09/11 14:10:47)
	 * 
	 */
	public String getJSP() {
		return "OePOSTESupressionFDP.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PE-FP-ORGANIGRAMME";
	}

	public String getNOM_PB_AFFICHER() {
		return "NOM_PB_AFFICHER";
	}

	public boolean performPB_AFFICHER(HttpServletRequest request) throws Exception {
		// Liste des job de suppression
		setListeJobSuppression(getActionFdpJobDao().listerActionFdpJobSuppression());
		afficheTableau();
		return true;
	}

	private void afficheTableau() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (int i = 0; i < getListeJobSuppression().size(); i++) {
			ActionFdpJob actionSupp = getListeJobSuppression().get(i);
			Integer j = actionSupp.getIdActionFdpJob();
			Agent ag = getAgentDao().chercherAgent(actionSupp.getIdAgent());
			addZone(getNOM_ST_LIB_AGENT(j), ag.getNomAgent() + " " + ag.getPrenomAgent());
			addZone(getNOM_ST_DATE(j), sdf.format(actionSupp.getDateSubmission()));
			if (actionSupp.getStatut() == null) {
				addZone(getNOM_ST_INFO(j), "En attente de traitement");
			} else {
				if (actionSupp.getStatut().length() > 150) {
					addZone(getNOM_ST_INFO(j), actionSupp.getStatut().substring(0, 150));
				} else {
					addZone(getNOM_ST_INFO(j), actionSupp.getStatut());
				}
			}
		}
	}

	public String getNOM_PB_AFFICHER_ERREUR() {
		return "NOM_PB_AFFICHER_ERREUR";
	}

	public boolean performPB_AFFICHER_ERREUR(HttpServletRequest request) throws Exception {
		// Liste des job de suppression en erreur
		setListeJobSuppression(getActionFdpJobDao().listerActionFdpJobSuppressionErreur());
		afficheTableau();
		return true;
	}

	public List<ActionFdpJob> getListeJobSuppression() {
		return listeJobSuppression == null ? new ArrayList<ActionFdpJob>() : listeJobSuppression;
	}

	public void setListeJobSuppression(List<ActionFdpJob> listeJobSuppression) {
		this.listeJobSuppression = listeJobSuppression;
	}

	public ActionFdpJobDao getActionFdpJobDao() {
		return actionFdpJobDao;
	}

	public void setActionFdpJobDao(ActionFdpJobDao actionFdpJobDao) {
		this.actionFdpJobDao = actionFdpJobDao;
	}

	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT" + i;
	}

	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	public String getNOM_ST_INFO(int i) {
		return "NOM_ST_INFO" + i;
	}

	public String getVAL_ST_INFO(int i) {
		return getZone(getNOM_ST_INFO(i));
	}

	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
