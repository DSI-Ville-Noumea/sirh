package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;

/**
 * Process OePOSTEFEActiviteSelection Date de création : (03/02/09 14:56:59)
 * 
 */
public class OeAVCTSelectionActeurs extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Agent> listeActeurs;
	public String focus = null;
	private AgentDao agentDao;
	private IAdsService adsService;

	public ArrayList<Agent> getListeActeurs() {
		return listeActeurs == null ? new ArrayList<Agent>() : listeActeurs;
	}

	public void setListeActeurs(ArrayList<Agent> listeActeurs) {
		this.listeActeurs = listeActeurs;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
	}

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();
		if (getListeActeurs().size() == 0) {
			@SuppressWarnings("unchecked")
			ArrayList<Agent> xcludeListe = (ArrayList<Agent>) VariablesActivite.recuperer(this, "LISTEACTEURS");
			ArrayList<Agent> aListe = new ArrayList<Agent>();
			// on recupere la liste des sous services de la DRH
			EntiteDto drh = adsService.getEntiteBySigle("DRH");
			if (drh != null && drh.getIdEntite() != null) {
				EntiteDto listeSousServ = adsService.getEntiteWithChildrenByIdEntite(drh.getIdEntite());
				List<Integer> listIdService = new ArrayList<Integer>();
				listIdService.add(drh.getIdEntite());
				for (EntiteDto enfant : listeSousServ.getEnfants()) {
					listIdService.add(enfant.getIdEntite());
				}
				aListe = getAgentDao().listerAgentAvecServiceCommencant(listIdService);
				aListe = elim_doubure_acteurs(aListe, xcludeListe);
			}

			// Affectation de la liste
			setListeActeurs(new ArrayList<Agent>());
			for (int j = 0; j < aListe.size(); j++) {
				Agent agent = (Agent) aListe.get(j);
				Integer i = agent.getIdAgent();

				if (agent != null) {
					getListeActeurs().add(agent);
					addZone(getNOM_ST_ID_AGENT(i), agent.getIdAgent().toString());
					addZone(getNOM_ST_LIB_AGENT(i), agent.getNomAgent() + " " + agent.getPrenomAgent());
				}
			}
		}
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant elemine de la liste l1 les éléments en communs
	 *         avec l2 fonctionne uniquement avec une liste l1 n'ayant pas 2
	 *         elements identiques
	 */
	public static ArrayList<Agent> elim_doubure_acteurs(ArrayList<Agent> l1, ArrayList<Agent> l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Agent) l2.get(i)).getIdAgent().toString()).equals(((Agent) l1.get(j)).getIdAgent()
							.toString()))
						l1.remove(j);

				}
			}
		}
		return l1;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (03/02/09 14:56:59)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (19/07/11 16:22:13)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (19/07/11 16:22:13)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		ArrayList<Agent> listAgentSelect = new ArrayList<Agent>();
		for (int j = 0; j < getListeActeurs().size(); j++) {
			// on recupere la ligne concernée
			Agent ag = (Agent) getListeActeurs().get(j);
			Integer i = ag.getIdAgent();
			// si la colonne selection est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				listAgentSelect.add(ag);
			}
		}
		VariablesActivite.ajouter(this, "ACTEURS", listAgentSelect);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFEActiviteSelection. Date de création :
	 * (24/08/11 09:15:05)
	 * 
	 */
	public OeAVCTSelectionActeurs() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (24/08/11 09:15:05)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTSelectionActeurs.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_AGENT(int i) {
		return "NOM_ST_ID_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ID_AGENT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_AGENT(int i) {
		return getZone(getNOM_ST_ID_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_AGENT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la case
	 * a cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
