package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableActivite;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;

/**
 * Process OeAGENTRecherche Date de cr�ation : (01/01/03 09:35:10)
 * 
 */
public class OeAGENTRecherche extends nc.mairie.technique.BasicProcess {

	public static final int STATUT_ETAT_CIVIL = 1;

	private ArrayList<AgentNW> listeAgent;

	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private nc.mairie.metier.agent.AgentNW AgentActivite;
	public String focus = null;
	private boolean first = true;

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (28/03/2003
	 * 08:50:20)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	private nc.mairie.metier.agent.AgentNW getAgentActivite() {
		return AgentActivite;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (01/01/2003
	 * 09:51:40)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<AgentNW> getListeAgent() {
		if (listeAgent == null) {
			listeAgent = new ArrayList<AgentNW>();
		}
		return listeAgent;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ZONE Date de
	 * cr�ation : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_EF_ZONE() {
		return "NOM_EF_ZONE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de cr�ation
	 * : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie : EF_ZONE
	 * Date de cr�ation : (01/01/03 09:35:10)
	 * 
	 */
	public String getVAL_EF_ZONE() {
		return getZone(getNOM_EF_ZONE());
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (01/01/03 09:35:10)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {

		// ----------------------------------//
		// V�rification des droits d'acc�s. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// R�cup de l'agent activit�, s'il existe
		AgentNW aAgent = (AgentNW) VariableActivite.recuperer(this, VariableActivite.ACTIVITE_AGENT_MAIRIE);
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

	/**
	 * Retourne le nom de l'�cran (notamment pour d�terminer les droits
	 * associ�s).
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

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du sup�rieur
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
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/01/03 09:35:10)
	 * 
	 * RG_AG_EC_C01
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		String zone = getVAL_EF_ZONE();

		ArrayList<AgentNW> aListe = new ArrayList<AgentNW>();
		// RG_AG_EC_C01
		// Si rien de saisi, recherche de tous les agents
		if (zone.length() == 0) {
			aListe = AgentNW.listerAgent(getTransaction());
			// Sinon, si num�rique on cherche l'agent
		} else if (Services.estNumerique(zone)) {
			AgentNW aAgent = AgentNW.chercherAgent(getTransaction(), Const.PREFIXE_MATRICULE + Services.lpad(zone, 5, "0"));
			// Si erreur alors pas trouv�. On traite
			if (getTransaction().isErreur())
				return false;

			aListe = new ArrayList<AgentNW>();
			aListe.add(aAgent);

			// Sinon, les agents dont le nom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_NOM())) {
			aListe = AgentNW.listerAgentAvecNomCommencant(getTransaction(), zone);
			// sinon les agents dont le pr�nom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_PRENOM())) {
			aListe = AgentNW.listerAgentAvecPrenomCommencant(getTransaction(), zone);
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_SERVICE())) {
			Service service = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			String prefixe = service.getCodService().substring(
					0,
					Service.isEntite(service.getCodService()) ? 1 : Service.isDirection(service.getCodService()) ? 2 : Service.isDivision(service
							.getCodService()) ? 3 : Service.isSection(service.getCodService()) ? 4 : 0);
			aListe = AgentNW.listerAgentAvecServiceCommencant(getTransaction(), prefixe);
		}

		// S'il y a un agent en entr�e alors on l'enl�ve de la liste
		if (getAgentActivite() != null) {
			for (int i = 0; i < aListe.size(); i++) {
				AgentNW a = (AgentNW) aListe.get(i);
				if (a.getNoMatricule().equals(getAgentActivite().getNoMatricule())) {
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
				AgentNW agent = (AgentNW) getListeAgent().get(i);

				addZone(getNOM_ST_MATR(indiceAgent), agent.getNoMatricule());
				addZone(getNOM_ST_NOM(indiceAgent), agent.getNomAgent());
				addZone(getNOM_ST_PRENOM(indiceAgent), agent.getPrenomAgent());

				indiceAgent++;
			}
		}

		// si 1 seul resultat dans la liste alors on selectionne directement
		// l'agent
		if (getListeAgent().size() == 1) {
			// Si agent activit� alors on alimente une var d'activit�
			if (getAgentActivite() != null) {
				VariableActivite.ajouter(this, VariableActivite.ACTIVITE_AGENT_MAIRIE, getListeAgent().get(0));
			} else {
				// Alimentation de la variable GLOBALE
				VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getListeAgent().get(0));
				// on recupere le service de l'agent si il y en a un
				String service = Const.CHAINE_VIDE;
				AgentNW agent = (AgentNW) getListeAgent().get(0);
				Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				if (aff != null && aff.getIdAffectation() != null && aff.getIdFichePoste() != null) {
					FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						Service serv = Service.chercherService(getTransaction(), fp.getIdServi());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							service = serv.getLibService();
						}
					}
				}

				VariableGlobale.ajouter(request, "SERVICE_AGENT", service);
			}
			setStatut(STATUT_PROCESS_APPELANT);
		}

		return true;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (28/03/2003
	 * 08:50:20)
	 * 
	 * @param newAgentActivite
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAgentActivite(AgentNW newAgentActivite) {
		AgentActivite = newAgentActivite;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (01/01/2003
	 * 09:51:40)
	 * 
	 * @param newListeAgent
	 *            ArrayList
	 */
	private void setListeAgent(ArrayList<AgentNW> newListeAgent) {
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
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_TRI
	 * Date de cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RG_TRI() {
		return "NOM_RG_TRI";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_TRI Date
	 * de cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getVAL_RG_TRI() {
		return getZone(getNOM_RG_TRI());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_NOM Date de
	 * cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_NOM() {
		return "NOM_RB_RECH_NOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_PRENOM Date de
	 * cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_PRENOM() {
		return "NOM_RB_RECH_PRENOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_NOM Date de cr�ation
	 * : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_NOM() {
		return "NOM_RB_TRI_NOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_NOMATR Date de
	 * cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_NOMATR() {
		return "NOM_RB_TRI_NOMATR";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TRI_PRENOM Date de
	 * cr�ation : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_TRI_PRENOM() {
		return "NOM_RB_TRI_PRENOM";
	}

	private boolean isFirst() {
		return first;
	}

	private void setFirst(boolean newFirst) {
		first = newFirst;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_TRI Date de cr�ation :
	 * (08/10/08 14:13:26)
	 * 
	 */
	public String getNOM_PB_TRI() {
		return "NOM_PB_TRI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (08/10/08 14:13:26)
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
		}

		// Remplissage de la liste
		String[] colonnes = { tri };
		boolean[] ordres = { true };
		ArrayList<AgentNW> a = Services.trier(getListeAgent(), colonnes, ordres);
		setListeAgent(a);

		int indiceAgent = 0;
		if (getListeAgent() != null) {
			for (int i = 0; i < getListeAgent().size(); i++) {
				AgentNW agent = (AgentNW) getListeAgent().get(i);

				addZone(getNOM_ST_MATR(indiceAgent), agent.getNoMatricule());
				addZone(getNOM_ST_NOM(indiceAgent), agent.getNomAgent());
				addZone(getNOM_ST_PRENOM(indiceAgent), agent.getPrenomAgent());

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
	 * cr�ation : (15/09/11 09:37:35)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de cr�ation : (15/09/11 09:37:35)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * cr�ation : (15/09/11 09:37:35)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de cr�ation : (15/09/11 09:37:35)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne une hashTable de la hi�rarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (01/01/03 09:35:10)
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
		// Si pas de retour d�finit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non g�r� par le process");
		return false;
	}

	/**
	 * Constructeur du process OeAGENTRecherche. Date de cr�ation : (15/09/11
	 * 10:51:20)
	 * 
	 */
	public OeAGENTRecherche() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (15/09/11 10:51:20)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTRecherche.jsp";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_RECHERCHE Date de cr�ation : (15/09/11 10:51:20)
	 * 
	 */
	public String getNOM_RG_RECHERCHE() {
		return "NOM_RG_RECHERCHE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_RECHERCHE
	 * Date de cr�ation : (15/09/11 10:51:20)
	 * 
	 */
	public String getVAL_RG_RECHERCHE() {
		return getZone(getNOM_RG_RECHERCHE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_SERVICE Date de
	 * cr�ation : (15/09/11 10:51:20)
	 * 
	 */
	public String getNOM_RB_RECH_SERVICE() {
		return "NOM_RB_RECH_SERVICE";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_MATR Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MATR Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PRENOM Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PRENOM(int i) {
		return "NOM_ST_PRENOM" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_PRENOM Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PRENOM(int i) {
		return getZone(getNOM_ST_PRENOM(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_NOM Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de cr�ation :
	 * (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_OK(int i) {
		return "NOM_PB_OK" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request, int elemSelection) throws Exception {

		// Si agent activit� alors on alimente une var d'activit�
		if (getAgentActivite() != null) {
			VariableActivite.ajouter(this, VariableActivite.ACTIVITE_AGENT_MAIRIE, getListeAgent().get(elemSelection));
		} else {
			// Alimentation de la variable GLOBALE
			VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getListeAgent().get(elemSelection));
			// on recupere le service de l'agent si il y en a un
			String service = Const.CHAINE_VIDE;
			AgentNW agent = (AgentNW) getListeAgent().get(elemSelection);
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			if (aff != null && aff.getIdAffectation() != null && aff.getIdFichePoste() != null) {
				FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					Service serv = Service.chercherService(getTransaction(), fp.getIdServi());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						service = serv.getLibService();
					}
				}
			}

			VariableGlobale.ajouter(request, "SERVICE_AGENT", service);
		}

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}
}
