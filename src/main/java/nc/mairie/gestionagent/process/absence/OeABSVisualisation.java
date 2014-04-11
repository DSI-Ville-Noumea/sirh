package nc.mairie.gestionagent.process.absence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeABSVisualisation extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OeABSVisualisation.class);

	public static final int STATUT_RECHERCHER_AGENT_DEMANDE = 1;
	public static final int STATUT_RECHERCHER_AGENT_ACTION = 2;
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 3;

	private String[] LB_ETAT;
	private String[] LB_FAMILLE;
	private String[] LB_FAMILLE_CREATION;

	public Hashtable<String, TreeHierarchy> hTree = null;
	private ArrayList<Service> listeServices;
	private ArrayList<EnumEtatAbsence> listeEtats;
	private ArrayList<EnumTypeAbsence> listeFamilleAbsence;

	public String ACTION_CREATION = "Création d'une absence.";
	public String ACTION_CREATION_A48 = "Création d'une réunion des membres du bureau directeur(ASA).";

	private EnumTypeAbsence typeCreation;
	private AgentNW agentCreation;

	@Override
	public String getJSP() {
		return "OeABSVisualisation.jsp";
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
		return "";
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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		if (etatStatut() == STATUT_RECHERCHER_AGENT_DEMANDE) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_DEMANDE(), agt.getNoMatricule());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_ACTION) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_ACTION(), agt.getNoMatricule());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATION) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATION(), agt.getNoMatricule());
			}
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			List<EnumEtatAbsence> etats = EnumEtatAbsence.getValues();
			setListeEtats((ArrayList<EnumEtatAbsence>) etats);
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (EnumEtatAbsence etat : etats) {
				String ligne[] = { etat.getValue() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);

		}

		// Si liste famille absence vide alors affectation
		if (getLB_FAMILLE() == LBVide || getLB_FAMILLE_CREATION() == LBVide) {
			setListeFamilleAbsence(EnumTypeAbsence.getValues());

			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EnumTypeAbsence> list = getListeFamilleAbsence().listIterator(); list.hasNext();) {
				EnumTypeAbsence type = (EnumTypeAbsence) list.next();
				String ligne[] = { type.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FAMILLE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_FAMILLE_SELECT(), Const.ZERO);
			setLB_FAMILLE_CREATION(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		}

		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().isEmpty()) {
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
			hTree = new Hashtable<>();
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

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER_AGENT_DEMANDE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_RECHERCHER_AGENT_DEMANDE(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_ACTION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_ACTION())) {
				return performPB_RECHERCHER_AGENT_ACTION(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_ACTION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_ACTION(request);
			}
			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}
			// Si clic sur le bouton PB_AJOUTER_ABSENCE
			if (testerParametre(request, getNOM_PB_AJOUTER_ABSENCE())) {
				return performPB_AJOUTER_ABSENCE(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_CREATION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATION())) {
				return performPB_RECHERCHER_AGENT_CREATION(request);
			}
			// Si clic sur le bouton PB_CREATION
			if (testerParametre(request, getNOM_PB_CREATION())) {
				return performPB_CREATION(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNomEcran() {
		return "ECR-ABS-VISU";
	}

	public String getNOM_ST_AGENT_DEMANDE() {
		return "NOM_ST_AGENT_DEMANDE";
	}

	public String getVAL_ST_AGENT_DEMANDE() {
		return getZone(getNOM_ST_AGENT_DEMANDE());
	}

	public String getNOM_PB_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_DEMANDE, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_DEMANDE(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	private String[] getLB_ETAT() {
		if (LB_ETAT == null)
			LB_ETAT = initialiseLazyLB();
		return LB_ETAT;
	}

	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	public String getNOM_LB_ETAT() {
		return "NOM_LB_ETAT";
	}

	public String getNOM_LB_ETAT_SELECT() {
		return "NOM_LB_ETAT_SELECT";
	}

	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	private String[] getLB_FAMILLE() {
		if (LB_FAMILLE == null)
			LB_FAMILLE = initialiseLazyLB();
		return LB_FAMILLE;
	}

	private void setLB_FAMILLE(String[] newLB_FAMILLE) {
		LB_FAMILLE = newLB_FAMILLE;
	}

	public String getNOM_LB_FAMILLE() {
		return "NOM_LB_FAMILLE";
	}

	public String getNOM_LB_FAMILLE_SELECT() {
		return "NOM_LB_FAMILLE_SELECT";
	}

	public String[] getVAL_LB_FAMILLE() {
		return getLB_FAMILLE();
	}

	public String getVAL_LB_FAMILLE_SELECT() {
		return getZone(getNOM_LB_FAMILLE_SELECT());
	}

	public String getNOM_ST_AGENT_ACTION() {
		return "NOM_ST_AGENT_ACTION";
	}

	public String getVAL_ST_AGENT_ACTION() {
		return getZone(getNOM_ST_AGENT_ACTION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_ACTION() {
		return "NOM_PB_RECHERCHER_AGENT_ACTION";
	}

	public boolean performPB_RECHERCHER_AGENT_ACTION(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_ACTION, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_ACTION(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {

		if (!performControlerFiltres()) {
			return false;
		}

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = dateDeb.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateDeb, "dd/MM/yyyy",
				"yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = dateFin.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateFin, "dd/MM/yyyy",
				"yyyyMMdd");

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		EnumEtatAbsence etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (EnumEtatAbsence) getListeEtats().get(numEtat - 1);
		}
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FAMILLE_SELECT())) : -1);
		EnumTypeAbsence type = null;
		if (numType != -1 && numType != 0) {
			type = (EnumTypeAbsence) getListeFamilleAbsence().get(numType - 1);
		}

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900"
				+ getVAL_ST_AGENT_DEMANDE();
		String idAgentAction = getVAL_ST_AGENT_ACTION().equals(Const.CHAINE_VIDE) ? null : "900"
				+ getVAL_ST_AGENT_ACTION();

		// SERVICE
		String sigleService = getVAL_EF_SERVICE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_SERVICE().toUpperCase();

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		List<DemandeDto> listeDemande = t.getListeDemandes(dateMin, dateMax, etat == null ? null : etat.getCode(),
				type == null ? null : type.getCode(), idAgentDemande == null ? null : Integer.valueOf(idAgentDemande));
		/*
		 * setListeAbsence((ArrayList<DemandeDto>) listeDemande); loadHistory();
		 * 
		 * afficheListePointages();
		 */

		return true;
	}

	private boolean performControlerFiltres() throws Exception {

		// on controle que le service saisie est bien un service
		String sigleService = getVAL_EF_SERVICE().toUpperCase();
		if (!sigleService.equals(Const.CHAINE_VIDE)) {
			// on cherche le code service associé
			Service siserv = Service.chercherServiceBySigle(getTransaction(), sigleService);
			if (getTransaction().isErreur() || siserv == null || siserv.getCodService() == null) {
				getTransaction().traiterErreur();
				// ERR502", "Le sigle service saisie ne permet pas de trouver le
				// service associé."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR502"));
				return false;

			}
		}

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

	public ArrayList<EnumEtatAbsence> getListeEtats() {
		return listeEtats == null ? new ArrayList<EnumEtatAbsence>() : listeEtats;
	}

	public void setListeEtats(ArrayList<EnumEtatAbsence> listeEtats) {
		this.listeEtats = listeEtats;
	}

	public ArrayList<EnumTypeAbsence> getListeFamilleAbsence() {
		return listeFamilleAbsence == null ? new ArrayList<EnumTypeAbsence>() : listeFamilleAbsence;
	}

	public void setListeFamilleAbsence(ArrayList<EnumTypeAbsence> listeFamilleAbsence) {
		this.listeFamilleAbsence = listeFamilleAbsence;
	}

	public String getNOM_PB_AJOUTER_ABSENCE() {
		return "NOM_PB_CREATE_BOX";
	}

	public boolean performPB_AJOUTER_ABSENCE(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private String[] getLB_FAMILLE_CREATION() {
		if (LB_FAMILLE_CREATION == null)
			LB_FAMILLE_CREATION = initialiseLazyLB();
		return LB_FAMILLE_CREATION;
	}

	private void setLB_FAMILLE_CREATION(String[] newLB_FAMILLE_CREATION) {
		LB_FAMILLE_CREATION = newLB_FAMILLE_CREATION;
	}

	public String getNOM_LB_FAMILLE_CREATION() {
		return "NOM_LB_FAMILLE_CREATION";
	}

	public String getNOM_LB_FAMILLE_CREATION_SELECT() {
		return "NOM_LB_FAMILLE_CREATION_SELECT";
	}

	public String[] getVAL_LB_FAMILLE_CREATION() {
		return getLB_FAMILLE_CREATION();
	}

	public String getVAL_LB_FAMILLE_CREATION_SELECT() {
		return getZone(getNOM_LB_FAMILLE_CREATION_SELECT());
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_CREATION, true);
		return true;
	}

	public String getNOM_PB_CREATION() {
		return "NOM_PB_CREATION";
	}

	public boolean performPB_CREATION(HttpServletRequest request) throws Exception {
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) : -1);
		EnumTypeAbsence type = null;
		if (numType != -1) {
			type = (EnumTypeAbsence) getListeFamilleAbsence().get(numType);
			setTypeCreation(type);
		}
		String idAgent = "";
		if (getVAL_ST_AGENT_CREATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_CREATION();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), idAgent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;

			}
			setAgentCreation(agent);
		}

		if (type != null && type.equals(EnumTypeAbsence.ASA_A48)) {
			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A48);
		} else {
			getTransaction().declarerErreur("Cette famille ne peut être saisi dans SIRH");
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		viderZoneSaisie(request);
		return true;
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_CREATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_DEBUT_MAM(), getNOM_RB_M());
		addZone(getNOM_RG_FIN_MAM(), getNOM_RB_M());
		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		setAgentCreation(null);
		setTypeCreation(null);
	}

	public String getNOM_RG_DEBUT_MAM() {
		return "NOM_RG_DEBUT_MAM";
	}

	public String getVAL_RG_DEBUT_MAM() {
		return getZone(getNOM_RG_DEBUT_MAM());
	}

	public String getNOM_RB_M() {
		return "NOM_RB_M";
	}

	public String getNOM_RB_AM() {
		return "NOM_RB_AM";
	}

	public String getNOM_ST_DATE_DEBUT() {
		return "NOM_ST_DATE_DEBUT";
	}

	public String getVAL_ST_DATE_DEBUT() {
		return getZone(getNOM_ST_DATE_DEBUT());
	}

	public String getNOM_ST_DATE_FIN() {
		return "NOM_ST_DATE_FIN";
	}

	public String getVAL_ST_DATE_FIN() {
		return getZone(getNOM_ST_DATE_FIN());
	}

	public String getNOM_RG_FIN_MAM() {
		return "NOM_RG_FIN_MAM";
	}

	public String getVAL_RG_FIN_MAM() {
		return getZone(getNOM_RG_FIN_MAM());
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		EnumTypeAbsence type = getTypeCreation();

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT());
		Date dateFin = sdf.parse(getVAL_ST_DATE_FIN());
		Boolean matinDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM());
		Boolean matinFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM());

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateDebutAM(matinDebut);
		dto.setDateDebutPM(apresMidiDebut);
		dto.setDateFin(dateFin);
		dto.setDateFinAM(matinFin);
		dto.setDateFinPM(apresMidiFin);
		dto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setIdTypeDemande(type.getCode());
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private AgentNW getAgentConnecte(HttpServletRequest request) throws Exception {
		AgentNW agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if (!uUser.getUserName().equals("nicno85") && !uUser.getUserName().equals("rebjo84")) {
			Siidma user = Siidma.chercherSiidma(getTransaction(), uUser.getUserName().toUpperCase());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return null;
			}
			if (user != null && user.getNomatr() != null) {
				agent = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		} else {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		return agent;
	}

	public EnumTypeAbsence getTypeCreation() {
		return typeCreation;
	}

	public void setTypeCreation(EnumTypeAbsence typeCreation) {
		this.typeCreation = typeCreation;
	}

	public AgentNW getAgentCreation() {
		return agentCreation;
	}

	public void setAgentCreation(AgentNW agentCreation) {
		this.agentCreation = agentCreation;
	}
}
