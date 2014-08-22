package nc.mairie.gestionagent.process.pointage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

public class OePTGSelectionAgent extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Agent> listeAgents = new ArrayList<Agent>();
	private ArrayList<Agent> listeAgentsDepart;
	private ArrayList<Agent> listAff = new ArrayList<Agent>();
	private String typePopulation;

	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;
	public String focus = null;
	private boolean first = true;

	private AgentDao agentDao;

	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();

		// Initialise la liste des services
		initialiseListeService();
		if (isFirst()) {
			addZone(getNOM_RG_RECHERCHE(), getNOM_RB_RECH_NOM());
			// on recupere le type de population F, C ou CC
			String type = (String) VariablesActivite.recuperer(this, "TYPE");
			setTypePopulation(type);
			setFirst(false);
		}

		@SuppressWarnings("unchecked")
		ArrayList<Agent> xcludeListe = (ArrayList<Agent>) VariablesActivite.recuperer(this, "LISTEAGENT");
		if (getListeAgentsDepart() == null)
			setListeAgentsDepart(new ArrayList<Agent>());
		getListeAgentsDepart().addAll(xcludeListe == null ? new ArrayList<Agent>() : xcludeListe);

		VariablesActivite.enlever(this, "LISTEAGENT");
		VariablesActivite.enlever(this, "TYPE");
		afficheListeAgents();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void afficheListeAgents() {
		setListAff(new ArrayList<Agent>());
		getListAff().addAll(getListeAgentsDepart());
		getListAff().addAll(getListeAgents() == null ? new ArrayList<Agent>() : getListeAgents());
		for (int j = 0; j < getListAff().size(); j++) {
			Agent agent = (Agent) getListAff().get(j);
			Integer i = Integer.valueOf(agent.getIdAgent());
			if (getListeAgentsDepart().contains(agent)) {
				addZone(getNOM_CK_SELECT_LIGNE(i), getCHECKED_ON());
			}
			addZone(getNOM_ST_ID_AGENT(i), agent.getIdAgent().toString());
			addZone(getNOM_ST_LIB_AGENT(i), agent.getNomAgent() + " " + agent.getPrenomAgent());
		}

	}

	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService())) {
					continue;
				}

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant éléminé de la liste l1 les éléments en communs
	 *         avec l2 fonctionne uniquement avec une liste l1 n'ayant pas 2
	 *         elements identiques
	 */
	public static ArrayList<Agent> elim_doubure_agents(ArrayList<Agent> l1, ArrayList<Agent> l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Agent) l2.get(i)).getIdAgent()).equals(((Agent) l1.get(j)).getIdAgent()))
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * @param focus
	 *            focus à définir.
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (19/07/11 16:22:13)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		ArrayList<Agent> listAgentSelect = new ArrayList<Agent>();
		for (int j = 0; j < getListAff().size(); j++) {
			// on recupère la ligne concernée
			Agent ag = (Agent) getListAff().get(j);
			Integer i = Integer.valueOf(ag.getIdAgent());
			// si la colonne selection est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				listAgentSelect.add(ag);
			}
		}
		VariablesActivite.ajouter(this, "AGENTS", listAgentSelect);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public OePTGSelectionAgent() {
		super();
	}

	public String getJSP() {
		return "OePTGSelectionAgent.jsp";
	}

	public String getNOM_ST_ID_AGENT(int i) {
		return "NOM_ST_ID_AGENT_" + i;
	}

	public String getVAL_ST_ID_AGENT(int i) {
		return getZone(getNOM_ST_ID_AGENT(i));
	}

	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT_" + i;
	}

	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	public String getNOM_RG_RECHERCHE() {
		return "NOM_RG_RECHERCHE";
	}

	public String getVAL_RG_RECHERCHE() {
		return getZone(getNOM_RG_RECHERCHE());
	}

	public String getNOM_RB_RECH_SERVICE() {
		return "NOM_RB_RECH_SERVICE";
	}

	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public String getNOM_RB_RECH_NOM() {
		return "NOM_RB_RECH_NOM";
	}

	public String getNOM_RB_RECH_PRENOM() {
		return "NOM_RB_RECH_PRENOM";
	}

	public String getVAL_EF_ZONE() {
		return getZone(getNOM_EF_ZONE());
	}

	public String getNOM_EF_ZONE() {
		return "NOM_EF_ZONE";
	}

	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		String zone = getVAL_EF_ZONE();

		ArrayList<Agent> aListe = new ArrayList<Agent>();
		// RG_AG_EC_C01
		// Si rien de saisi, recherche de tous les agents
		if (zone.length() == 0) {
			aListe = (ArrayList<Agent>) getAgentDao().listerAgent();
			// Sinon, si numérique on cherche l'agent
		} else if (Services.estNumerique(zone)) {
			Agent aAgent = getAgentDao().chercherAgent(
					Integer.valueOf(Const.PREFIXE_MATRICULE + Services.lpad(zone, 5, "0")));
			// Si erreur alors pas trouvé. On traite
			if (getTransaction().isErreur()) {
				return false;
			}

			aListe = new ArrayList<Agent>();
			aListe.add(aAgent);

			// Sinon, les agents dont le nom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_NOM())) {
			aListe = getAgentDao().listerAgentAvecNomCommencant(zone);
			// sinon les agents dont le prénom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_PRENOM())) {
			aListe = getAgentDao().listerAgentAvecPrenomCommencant(zone);
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_SERVICE())) {
			Service service = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			String prefixe = service.getCodService().substring(
					0,
					Service.isEntite(service.getCodService()) ? 1 : Service.isDirection(service.getCodService()) ? 2
							: Service.isDivision(service.getCodService()) ? 3 : Service.isSection(service
									.getCodService()) ? 4 : 0);
			aListe = getAgentDao().listerAgentAvecServiceCommencant(prefixe);
		}

		// Si la liste est vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}
		// on verifie que l'agent soit bien C,CC ou F
		ArrayList<Agent> listeAExclure = new ArrayList<Agent>();
		for (Agent ag : aListe) {
			Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				listeAExclure.add(ag);
				continue;
			}
			String statutCarriere = Carriere.getStatutCarriere(carrEnCours.getCodeCategorie());
			if (!statutCarriere.equals(getTypePopulation())) {
				listeAExclure.add(ag);
			}
		}
		aListe.removeAll(listeAExclure);
		getListeAgents().addAll(aListe);
		afficheListeAgents();

		return true;
	}

	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	private boolean isFirst() {
		return first;
	}

	private void setFirst(boolean newFirst) {
		first = newFirst;
	}

	public ArrayList<Agent> getListAff() {
		return listAff;
	}

	public void setListAff(ArrayList<Agent> listAff) {
		this.listAff = listAff;
	}

	public ArrayList<Agent> getListeAgentsDepart() {
		return listeAgentsDepart;
	}

	public void setListeAgentsDepart(ArrayList<Agent> listeAgentsDepart) {
		this.listeAgentsDepart = listeAgentsDepart;
	}

	public ArrayList<Agent> getListeAgents() {
		return listeAgents;
	}

	public void setListeAgents(ArrayList<Agent> listeAgents) {
		this.listeAgents = listeAgents;
	}

	public String getTypePopulation() {
		return typePopulation;
	}

	public void setTypePopulation(String typePopulation) {
		this.typePopulation = typePopulation;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
