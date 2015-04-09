package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableActivite;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTRecherche Date de création : (01/01/03 09:35:10)
 * 
 */
public class OeAGENTRecherche extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_ETAT_CIVIL = 1;
	private ArrayList<Agent> listeAgent;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;
	private Agent AgentActivite;
	public String focus = null;
	private boolean first = true;

	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	/**
	 * Insérez la description de la méthode ici. Date de création : (28/03/2003
	 * 08:50:20)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	private Agent getAgentActivite() {
		return AgentActivite;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (01/01/2003
	 * 09:51:40)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Agent> getListeAgent() {
		if (listeAgent == null) {
			listeAgent = new ArrayList<Agent>();
		}
		return listeAgent;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ZONE Date de
	 * création : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_EF_ZONE() {
		return "NOM_EF_ZONE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_ZONE
	 * Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public String getVAL_EF_ZONE() {
		return getZone(getNOM_EF_ZONE());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (01/01/03 09:35:10)
	 * 
	 */
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
		Agent aAgent = (Agent) VariableActivite.recuperer(this, VariableActivite.ACTIVITE_AGENT_MAIRIE);
		if (aAgent != null) {
			setAgentActivite(aAgent);
			VariableActivite.enlever(this, VariableActivite.ACTIVITE_AGENT_MAIRIE);
		}
		if (isFirst()) {
			addZone(getNOM_RG_RECHERCHE(), getNOM_RB_RECH_NOM());
			addZone(getNOM_RG_TRI(), getNOM_RB_TRI_NOMATR());
			setFirst(false);
		}

		// Initialise la liste des services
		initialiseListeService();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-AG-RECHERCHE";
	}

	/**
	 * Initialise la liste des services.
	 * 
	 * @throws Exception
	 */
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/01/03 09:35:10)
	 * 
	 * RG_AG_EC_C01
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		String zone = getVAL_EF_ZONE();

		ArrayList<Agent> aListe = new ArrayList<Agent>();
		// RG_AG_EC_C01
		// Si rien de saisi, recherche de tous les agents
		if (zone.length() == 0) {
			aListe = (ArrayList<Agent>) getAgentDao().listerAgent();
			// Sinon, si numérique on cherche l'agent
		} else if (Services.estNumerique(zone)) {
			if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_CAFAT())) {
				aListe = getAgentDao().listerAgentAvecCafatCommencant(zone);
			} else {
				try {
					Agent aAgent = getAgentDao().chercherAgent(
							Integer.valueOf(Const.PREFIXE_MATRICULE + Services.lpad(zone, 5, "0")));

					aListe = new ArrayList<Agent>();
					aListe.add(aAgent);
				} catch (Exception e) {
					return false;
				}
			}

			// Sinon, les agents dont le nom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_NOM())) {
			aListe = getAgentDao().listerAgentAvecNomCommencant(zone);
			// sinon les agents dont le prenom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_PRENOM())) {
			aListe = getAgentDao().listerAgentAvecPrenomCommencant(zone);
			// sinon les agents dont le numero cafat commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_SERVICE())) {
			Service service = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			String prefixe = service.getCodService().substring(
					0,
					Service.isEntite(service.getCodService()) ? 1 : Service.isDirection(service.getCodService()) ? 2
							: Service.isDivision(service.getCodService()) ? 3 : Service.isSection(service
									.getCodService()) ? 4 : 0);
			aListe = getAgentDao().listerAgentAvecServiceCommencant(prefixe);
		}

		// S'il y a un agent en entrée alors on l'enleve de la liste
		if (getAgentActivite() != null && null != getAgentActivite().getNomatr()) {
			for (int i = 0; i < aListe.size(); i++) {
				Agent a = (Agent) aListe.get(i);
				if (a.getNomatr().toString().equals(getAgentActivite().getNomatr().toString())) {
					aListe.remove(a);
				}
			}
		}

		// Si la liste est vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}

		setListeAgent(aListe);

		int indiceAgent = 0;
		if (getListeAgent() != null) {
			for (int i = 0; i < getListeAgent().size(); i++) {
				Agent agent = (Agent) getListeAgent().get(i);

				addZone(getNOM_ST_MATR(indiceAgent), agent.getNomatr().toString());
				addZone(getNOM_ST_NOM(indiceAgent), agent.getNomAgent());
				addZone(getNOM_ST_PRENOM(indiceAgent), agent.getPrenomAgent());
				addZone(getNOM_ST_CAFAT(indiceAgent),
						agent.getNumCafat().equals(Const.CHAINE_VIDE) ? "&nbsp;" : agent.getNumCafat());
				addZone(getNOM_ST_RUAMM(indiceAgent),
						agent.getNumRuamm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : agent.getNumRuamm());

				indiceAgent++;
			}
		}

		// si 1 seul resultat dans la liste alors on selectionne directement
		// l'agent
		if (getListeAgent().size() == 1) {
			// Si agent activité alors on alimente une var d'activité
			if (getAgentActivite() != null) {
				VariableActivite.ajouter(this, VariableActivite.ACTIVITE_AGENT_MAIRIE, getListeAgent().get(0));
			} else {
				// Alimentation de la variable GLOBALE
				VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getListeAgent().get(0));
				// on recupere le service de l'agent si il y en a un
				String service = Const.CHAINE_VIDE;
				Agent agent = (Agent) getListeAgent().get(0);
				try {
					Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
					if (aff != null && aff.getIdAffectation() != null && aff.getIdFichePoste() != null) {
						try {
							FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
							Service serv = Service.chercherService(getTransaction(), fp.getIdServi());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							} else {
								service = serv.getLibService();
							}
						} catch (Exception e) {

						}
					}
				} catch (Exception e) {

				}

				VariableGlobale.ajouter(request, "SERVICE_AGENT", service);
			}
			setStatut(STATUT_PROCESS_APPELANT);
		}

		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (28/03/2003
	 * 08:50:20)
	 * 
	 * @param newAgentActivite
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAgentActivite(Agent newAgentActivite) {
		AgentActivite = newAgentActivite;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (01/01/2003
	 * 09:51:40)
	 * 
	 * @param newListeAgent
	 *            ArrayList
	 */
	private void setListeAgent(ArrayList<Agent> newListeAgent) {
		listeAgent = newListeAgent;
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_EF_ZONE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_TRI
	 * Date de création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RG_TRI() {
		return "NOM_RG_TRI";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_TRI Date
	 * de création : (08/10/08 13:07:23)
	 * 
	 */
	public String getVAL_RG_TRI() {
		return getZone(getNOM_RG_TRI());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_NOM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_NOM() {
		return "NOM_RB_RECH_NOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_PRENOM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_PRENOM() {
		return "NOM_RB_RECH_PRENOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_CAFAT Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_CAFAT() {
		return "NOM_RB_RECH_CAFAT";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_NOM Date de création
	 * : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_NOM() {
		return "NOM_RB_TRI_NOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_NOMATR Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_NOMATR() {
		return "NOM_RB_TRI_NOMATR";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_PRENOM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_PRENOM() {
		return "NOM_RB_TRI_PRENOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_CAFAT Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_CAFAT() {
		return "NOM_RB_TRI_CAFAT";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_RUAMM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_RUAMM() {
		return "NOM_RB_TRI_RUAMM";
	}

	private boolean isFirst() {
		return first;
	}

	private void setFirst(boolean newFirst) {
		first = newFirst;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_TRI Date de création :
	 * (08/10/08 14:13:26)
	 * 
	 */
	public String getNOM_PB_TRI() {
		return "NOM_PB_TRI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/10/08 14:13:26)
	 * 
	 * RG_AG_RE_A01
	 */
	public boolean performPB_TRI(HttpServletRequest request) throws Exception {
		// RG_AG_RE_A01
		String tri = "nomUsage";
		if (getVAL_RG_TRI().equals(getNOM_RB_TRI_NOM())) {
			tri = "nomUsage";
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_NOMATR())) {
			tri = "noMatricule";
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_PRENOM())) {
			tri = "prenom";
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_CAFAT())) {
			tri = "numCafat";
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_RUAMM())) {
			tri = "numRuamm";
		}

		// Remplissage de la liste
		String[] colonnes = { tri };
		boolean[] ordres = { true };
		ArrayList<Agent> a = Services.trier(getListeAgent(), colonnes, ordres);
		setListeAgent(a);

		int indiceAgent = 0;
		if (getListeAgent() != null) {
			for (int i = 0; i < getListeAgent().size(); i++) {
				Agent agent = (Agent) getListeAgent().get(i);

				addZone(getNOM_ST_MATR(indiceAgent), agent.getNomatr().toString());
				addZone(getNOM_ST_NOM(indiceAgent), agent.getNomAgent());
				addZone(getNOM_ST_PRENOM(indiceAgent), agent.getPrenomAgent());
				addZone(getNOM_ST_CAFAT(indiceAgent),
						agent.getNumCafat().equals(Const.CHAINE_VIDE) ? "&nbsp;" : agent.getNumCafat());
				addZone(getNOM_ST_RUAMM(indiceAgent),
						agent.getNumRuamm().equals(Const.CHAINE_VIDE) ? "&nbsp;" : agent.getNumRuamm());

				indiceAgent++;
			}
		}
		return true;
	}

	/**
	 * Getter de la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	/**
	 * Setter de la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (15/09/11 09:37:35)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (15/09/11 09:37:35)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (15/09/11 09:37:35)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (15/09/11 09:37:35)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne une hashTable de la hierarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_TRI
			if (testerParametre(request, getNOM_PB_TRI())) {
				return performPB_TRI(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_OK
			for (int i = 0; i < getListeAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_OK(i))) {
					return performPB_OK(request, i);
				}
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	/**
	 * Constructeur du process OeAGENTRecherche. Date de création : (15/09/11
	 * 10:51:20)
	 * 
	 */
	public OeAGENTRecherche() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (15/09/11 10:51:20)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTRecherche.jsp";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_RECHERCHE Date de création : (15/09/11 10:51:20)
	 * 
	 */
	public String getNOM_RG_RECHERCHE() {
		return "NOM_RG_RECHERCHE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_RECHERCHE
	 * Date de création : (15/09/11 10:51:20)
	 * 
	 */
	public String getVAL_RG_RECHERCHE() {
		return getZone(getNOM_RG_RECHERCHE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_SERVICE Date de
	 * création : (15/09/11 10:51:20)
	 * 
	 */
	public String getNOM_RB_RECH_SERVICE() {
		return "NOM_RB_RECH_SERVICE";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_MATR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MATR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PRENOM Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PRENOM(int i) {
		return "NOM_ST_PRENOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PRENOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PRENOM(int i) {
		return getZone(getNOM_ST_PRENOM(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_NOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_OK(int i) {
		return "NOM_PB_OK" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request, int elemSelection) throws Exception {

		// Si agent activité alors on alimente une var d'activité
		if (getAgentActivite() != null) {
			VariableActivite.ajouter(this, VariableActivite.ACTIVITE_AGENT_MAIRIE, getListeAgent().get(elemSelection));
		} else {
			// Alimentation de la variable GLOBALE
			VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getListeAgent().get(elemSelection));
			// on recupere le service de l'agent si il y en a un
			String service = Const.CHAINE_VIDE;
			Agent agent = (Agent) getListeAgent().get(elemSelection);
			try {
				Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
				if (aff != null && aff.getIdAffectation() != null && aff.getIdFichePoste() != null) {
					try {
						FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
						Service serv = Service.chercherService(getTransaction(), fp.getIdServi());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							service = serv.getLibService();
						}
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {

			}

			VariableGlobale.ajouter(request, "SERVICE_AGENT", service);
		}

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	public String getNOM_ST_CAFAT(int i) {
		return "NOM_ST_CAFAT" + i;
	}

	public String getVAL_ST_CAFAT(int i) {
		return getZone(getNOM_ST_CAFAT(i));
	}

	public String getNOM_ST_RUAMM(int i) {
		return "NOM_ST_RUAMM" + i;
	}

	public String getVAL_ST_RUAMM(int i) {
		return getZone(getNOM_ST_RUAMM(i));
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
