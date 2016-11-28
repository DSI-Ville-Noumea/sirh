package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteAnneeDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteChoixAgentDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.PtgService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OePTGPrimeDpm extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OePTGPrimeDpm.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4481591314447007514L;
	
	public final static String RECUPERATION = "Récupération";
	public final static String INDEMNITE = "Indémnité";

	public static final int STATUT_RECHERCHER_AGENT_DEMANDE = 1;
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 2;

	public String focus = null;
	// Liste des années pour la recherche
	private String[] LB_ANNEE;
	// Liste des années pour la création
	private String[] LB_ANNEE_CREATION;
	private String[] LB_CHOIX;

	private IPtgService ptgService;

	private IAdsService adsService;

	private IRadiService radiService;

	private ISirhService sirhService;

	private AgentDao agentDao;

	private ArrayList<Integer> listeAnnee;

	private TreeMap<Integer, DpmIndemniteChoixAgentDto> listeChoixAgent;
	
	private DpmIndemniteAnneeDto dpmAnneeOuverte;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public String ACTION_CREATION = "Creation_demande";
	public String ACTION_MODIFICATION = "Modification_demande";
	public String ACTION_MOTIF_REJET = "Motif_rejet_demande";

	@Override
	public String getJSP() {
		return "OePTGPrimeDpm.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-PRIME-DPM";
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190","Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		setFocus(getDefaultFocus());

		if (etatStatut() == STATUT_RECHERCHER_AGENT_DEMANDE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_DEMANDE(), agt.getNomatr().toString());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATION) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATION(), agt.getNomatr().toString());
			}
		}

		// Initialisation des listes deroulantes
		initialiseListeDeroulante(request);
		
		List<DpmIndemniteAnneeDto> anneesOuverte = ptgService.getListDpmIndemAnneeOuverte();
		if(null != anneesOuverte
				&& !anneesOuverte.isEmpty()) {
			setDpmAnneeOuverte(anneesOuverte.get(0));
		}else{
			setDpmAnneeOuverte(null);
		}
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante(HttpServletRequest request) throws Exception {
		// Si liste etat vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			List<DpmIndemniteAnneeDto> listeDpmAnnee = ptgService.getListDpmIndemAnnee(getAgentConnecte(request).getIdAgent());
			

			ArrayList<Integer> listAnnee = new ArrayList<Integer>();
			if(null != listeDpmAnnee) {
				for(DpmIndemniteAnneeDto anneeDto : listeDpmAnnee) {
					listAnnee.add(anneeDto.getAnnee());
				}
			}
			
			setListeAnnee((ArrayList<Integer>) listAnnee);
			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (Integer annee : listAnnee) {
				String ligne[] = { annee.toString() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ANNEE(aFormat.getListeFormatee(true));
			setLB_ANNEE_CREATION(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			addZone(getNOM_LB_ANNEE_CREATION_SELECT(), Const.ZERO);
		}
		
		int[] tailles = { 30 };
		FormateListe aFormat = new FormateListe(tailles);
		String ligne[] = { "Indemnité" };
		aFormat.ajouteLigne(ligne);
		String ligne2[] = { "Récupération" };
		aFormat.ajouteLigne(ligne2);
		setLB_CHOIX(aFormat.getListeFormatee(true));
		addZone(getNOM_LB_CHOIX_SELECT(), Const.ZERO);
		
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == agentDao) {
			agentDao = (new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {

		// date annee
		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);
		Integer annee = null;
		if (numAnnee != -1 && numAnnee != 0) {
			annee = (Integer) getListeAnnee().get(numAnnee - 1);
		}

		// agent
		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();
		if (idAgentDemande != null)
			addZone(getNOM_ST_AGENT_DEMANDE(), getVAL_ST_AGENT_DEMANDE());

		// SERVICE
		String sigle = getVAL_EF_SERVICE().toUpperCase();
		String idServiceAds = getVAL_ST_ID_SERVICE_ADS().toUpperCase();

		if (!sigle.equals(Const.CHAINE_VIDE)) {
			EntiteDto service = adsService.getEntiteBySigle(sigle);
			idServiceAds = service.getIdEntite().toString();
		}

		// SERVICE
		List<Integer> listIdAgentService = new ArrayList<Integer>();

		if (!sigle.equals(Const.CHAINE_VIDE) && null == idAgentDemande) {
			EntiteDto service = adsService.getEntiteBySigle(sigle);
			idServiceAds = new Long(service.getIdEntite()).toString();

			// Récupération des agents
			// on recupere les sous-service du service selectionne
			List<Integer> listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));

			if (null != listeSousService && !listeSousService.isEmpty()) {
				ArrayList<Agent> listAgent = agentDao.listerAgentAvecServicesETMatricules(listeSousService, null, null);
				for (Agent ag : listAgent) {
					if (!listIdAgentService.contains(ag.getIdAgent())) {
						listIdAgentService.add(ag.getIdAgent());
					}
				}
			}
		} else if (idAgentDemande != null) {
			// Si un agent est renseigné, on feinte le filtre en renseignant son id dans le filtre par service
			listIdAgentService.add(Integer.valueOf(idAgentDemande));
		}

		if (listIdAgentService.size() >= 1000) {
			// "ERR501",
			// "La sélection des filtres engendre plus de 1000 agents. Merci de reduire la sélection."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR501"));
			return false;
		}

		// agent SIRH connecte
		Agent agentConnecte = getAgentConnecte(request);

		// Recup ou indemnite
		int choix = (Services.estNumerique(getZone(getNOM_LB_CHOIX_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_CHOIX_SELECT())) : -1);
		Boolean isChoixIndemnite = null;
		Boolean isChoixRecuperation = null;
		if (choix == 1) {
			isChoixIndemnite = true;
		} else if (choix == 2) {
			isChoixRecuperation = true;
		}
		
		List<DpmIndemniteChoixAgentDto> listChoixAgent = ptgService.getListDpmIndemniteChoixAgent(
				agentConnecte.getIdAgent(), annee, isChoixIndemnite, isChoixRecuperation, listIdAgentService);
				
		setListeChoixAgent((ArrayList<DpmIndemniteChoixAgentDto>) listChoixAgent);

		afficheListeChoixAgent();

		return true;
	}
	
	public boolean performPB_AFFICHER_AGENTS_SANS_CHOIX(HttpServletRequest request) throws Exception {
		
		// @Comment by theo on 28th Nov. 2016 : 
		/* La liste des agents n'ayant pas fait de choix est disponible même lorsque l'année en cours n'est pas ouverte.
		 Les changements ont été fait sur la condition uniquement, l'algorithme de récupération de ces agents n'a pas été vérifié/changé. */
		
		Integer annee = new DateTime().getYear();
		DpmIndemniteAnneeDto dpmIndemnAnnee = ptgService.getDpmIndemAnneeByAnnee(annee);
		if (dpmIndemnAnnee.getAnnee() == null) {
			getTransaction().declarerErreur("Aucune correspondance n'a été trouvé à cette date : " + annee);
			return false;
		}
		
		// on cherche les agents ayant fait un choix pour l'année en cours
		List<DpmIndemniteChoixAgentDto> listChoixAgent = 
					ptgService.getListDpmIndemniteChoixAgent(getAgentConnecte(request).getIdAgent(), annee, null, null, null);
		
		List<Integer> listIdsAgentAvecChoix = new ArrayList<Integer>();
		if(null != listChoixAgent) {
			for(DpmIndemniteChoixAgentDto choixAgent : listChoixAgent) {
				if(!listIdsAgentAvecChoix.contains(choixAgent.getIdAgent()))
					listIdsAgentAvecChoix.add(choixAgent.getIdAgent());
			}
		}
		
		// on cherche les agents ayant la prime DPM 7718 ou 7719
		List<AgentWithServiceDto> listAgentsAvecPrimeDpm = sirhService.getListeAgentWithIndemniteForfaitTravailDPM(new HashSet<Integer>());
		
		List<DpmIndemniteChoixAgentDto> listSansChoixAgent = new ArrayList<DpmIndemniteChoixAgentDto>();
		// puis on trie pour ne récuperer que les agents sans choix
		if(null != listAgentsAvecPrimeDpm) {
			for(AgentWithServiceDto agent : listAgentsAvecPrimeDpm) {
				if(!listIdsAgentAvecChoix.contains(agent.getIdAgent())) {
					DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
					dto.setIdAgent(agent.getIdAgent());
					dto.setAgent(agent);
					dto.setDpmIndemniteAnnee(dpmIndemnAnnee);
					
					listSansChoixAgent.add(dto);
				}
			}
		}
		
		setListeChoixAgent((ArrayList<DpmIndemniteChoixAgentDto>) listSansChoixAgent);

		afficheListeChoixAgent();

		return true;
	}

	private void afficheListeChoixAgent() throws Exception {

		for (Entry<Integer, DpmIndemniteChoixAgentDto> choixMap : getListeChoixAgent().entrySet()) {
			DpmIndemniteChoixAgentDto choix = choixMap.getValue();
			Integer i = choixMap.getKey();
			try {
				Agent agent = agentDao.chercherAgent(choix.getIdAgent());

				addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
				addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " " + agent.getPrenomAgent());
				addZone(getNOM_ST_ANNEE(i), choix.getDpmIndemniteAnnee().getAnnee().toString());
				addZone(getNOM_ST_CHOIX(i), choix.getChoix());
				
				Agent operateur = agentDao.chercherAgent(choix.getIdAgentCreation());
				
				addZone(getNOM_ST_OPERATEUR(i), operateur.getNomAgent() + " " + operateur.getPrenomAgent() + " (" + operateur.getNomatr() + ")");
				addZone(getNOM_ST_DATE_ETAT(i), sdf.format(choix.getDateMaj()));
			} catch (Exception e) {
				logger.debug(e.getMessage());
				continue;
			}
		}
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = agentDao.chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_DEMANDE(), Const.CHAINE_VIDE);
		return true;
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		viderZoneSaisie(request);
		return true;
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_CREATION(), Const.CHAINE_VIDE);
	}

	public boolean performPB_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_DEMANDE, true);
		return true;
	}

	public boolean performPB_AJOUTER_CHOIX_DPM(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		addZone(getNOM_RG_CHOIX(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_RECHERCHER_AGENT_CREATION(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATION, true);
		return true;
	}

	public boolean performPB_CREATION(HttpServletRequest request) throws Exception {
		
		// date annee
		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_CREATION_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ANNEE_CREATION_SELECT())) : -1);
		Integer annee = null;
		if (numAnnee != -1 && numAnnee != 0) {
			annee = (Integer) getListeAnnee().get(numAnnee - 1);
		}
		
		String idAgent = Const.CHAINE_VIDE;
		if (getVAL_ST_AGENT_CREATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_CREATION();
			try {
				agentDao.chercherAgent(Integer.valueOf(idAgent));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;
			}
		}

		if (getZone(getNOM_RG_CHOIX()).equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "indemnité ou récupération"));
			return false;
		}

		if (annee == null) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			return false;
		}
		
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(new Integer(idAgent));

		DpmIndemniteAnneeDto dpmIndemnAnnee = ptgService.getDpmIndemAnneeByAnnee(annee);
		if (dpmIndemnAnnee == null) {
			getTransaction().declarerErreur("L'année " + annee + " n'a pas encore été paramétrée.");
			return false;
		}

		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setIdAgent(new Integer(idAgent));
		dto.setAgent(agentDto);
		dto.setChoixIndemnite(getVAL_RG_CHOIX().equals(getNOM_RB_INDEMNITE()));
		dto.setChoixRecuperation(getVAL_RG_CHOIX().equals(getNOM_RB_RECUPERATION()));
		dto.setIdAgentCreation(getAgentConnecte(request).getIdAgent());
		dto.setDpmIndemniteAnnee(dpmIndemnAnnee);

		ReturnMessageDto srm = ptgService.saveIndemniteChoixAgent(getAgentConnecte(request).getIdAgent(), dto);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur("Demande créée : " + info);
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);

		return true;
	}

	public boolean performPB_MODIFICATION(HttpServletRequest request, Integer indiceTR) throws Exception {

		DpmIndemniteChoixAgentDto dto = getListeChoixAgent().get(indiceTR);

		addZone(getNOM_ST_INDICE_CHOIX_AGENT(), indiceTR.toString());
		addZone(getNOM_ST_AGENT_MODIFICATION(), new Integer(dto.getIdAgent() - 9000000).toString());
		addZone(getNOM_ST_ANNEE_MODIFICATION(), dto.getDpmIndemniteAnnee().getAnnee().toString());
		
		String choix = "";
		if(dto.isChoixIndemnite()) {
			choix = getNOM_RB_INDEMNITE();
		}
		if(dto.isChoixRecuperation()) {
			choix = getNOM_RB_RECUPERATION();
		}
		addZone(getNOM_RG_CHOIX(), choix);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		return true;
	}

	public boolean performPB_VALIDER_MODIFICATION(HttpServletRequest request) throws Exception {

		String idAgent = Const.CHAINE_VIDE;
		if (getVAL_ST_AGENT_MODIFICATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_MODIFICATION();
			try {
				agentDao.chercherAgent(Integer.valueOf(idAgent));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;
			}
		}

		if (getZone(getNOM_RG_CHOIX()).equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Indemnité ou Récupération"));
			return false;
		}

		Integer indiceTR = new Integer(getVAL_ST_INDICE_CHOIX_AGENT());

		DpmIndemniteChoixAgentDto dto = getListeChoixAgent().get(indiceTR);

		dto.setChoixIndemnite(getVAL_RG_CHOIX().equals(getNOM_RB_INDEMNITE()));
		dto.setChoixRecuperation(getVAL_RG_CHOIX().equals(getNOM_RB_RECUPERATION()));
		dto.setIdAgentCreation(getAgentConnecte(request).getIdAgent());

		ReturnMessageDto srm = ptgService.saveIndemniteChoixAgent(getAgentConnecte(request).getIdAgent(), dto);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur("Demande modifiée : " + info);
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		
		return true;
	}
	
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int idChoix) throws Exception {
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		
		if (getAgentConnecte(request) == null) {
			return false;
		}
		
		// on recupere la demande
		DpmIndemniteChoixAgentDto dto = getListeChoixAgent().get(idChoix);

		ReturnMessageDto srm = ptgService.deleteIndemniteChoixAgent(getAgentConnecte(request).getIdAgent(), dto.getIdDpmIndemChoixAgent());

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur("Choix supprimé : " + info);
		}
		
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		
		return false;
	}
	
	public String getDefaultFocus() {
		return getNOM_PB_VALIDATION();
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
	
	public ArrayList<Integer> getListeAnnee() {
		return listeAnnee;
	}

	public void setListeAnnee(ArrayList<Integer> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	public String getNOM_PB_VALIDATION() {
		return "NOM_PB_VALIDATION";
	}

	
	/* Liste déroulante des années, pour recherche */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}
	
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}
	
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null) {
			LB_ANNEE = initialiseLazyLB();
		}
		return LB_ANNEE;
	}
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}
	
	
	/* Liste déroulante des années, pour création et modification d'une prime agent */
	public String getNOM_LB_ANNEE_CREATION_SELECT() {
		return "NOM_LB_ANNEE_CREATION_SELECT";
	}
	public String getVAL_LB_ANNEE_CREATION_SELECT() {
		return getZone(getNOM_LB_ANNEE_CREATION_SELECT());
	}
	
	private void setLB_ANNEE_CREATION(String[] newLB_ANNEE_CREATION) {
		LB_ANNEE_CREATION = newLB_ANNEE_CREATION;
	}
	
	private String[] getLB_ANNEE_CREATION() {
		if (LB_ANNEE_CREATION == null) {
			LB_ANNEE_CREATION = initialiseLazyLB();
		}
		return LB_ANNEE_CREATION;
	}
	public String[] getVAL_LB_ANNEE_CREATION() {
		return getLB_ANNEE_CREATION();
	}

	// Année lors d'une modification
	public String getNOM_ST_ANNEE_MODIFICATION() {
		return "NOM_ST_ANNEE_MODIFICATION";
	}

	public String getVAL_ST_ANNEE_MODIFICATION() {
		return getZone(getNOM_ST_ANNEE_MODIFICATION());
	}
	
	
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getNOM_ST_AGENT_DEMANDE() {
		return "NOM_ST_AGENT_DEMANDE";
	}

	public String getVAL_ST_AGENT_DEMANDE() {
		return getZone(getNOM_ST_AGENT_DEMANDE());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE_" + i;
	}

	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	public String getNOM_ST_CHOIX(int i) {
		return "NOM_ST_CHOIX_" + i;
	}

	public String getVAL_ST_CHOIX(int i) {
		return getZone(getNOM_ST_CHOIX(i));
	}

	public String getNOM_ST_DATE_ETAT(int i) {
		return "NOM_ST_DATE_ETAT_" + i;
	}

	public String getVAL_ST_DATE_ETAT(int i) {
		return getZone(getNOM_ST_DATE_ETAT(i));
	}

	public String getNOM_LB_ANNEE_CREATION() {
		return "NOM_LB_ANNEE_CREATION";
	}

	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE";
	}

	public String getNOM_PB_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_RECHERCHER_AGENT_DEMANDE";
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public String getNOM_PB_AFFICHER_AGENT_SANS_CHOIX() {
		return "NOM_PB_AFFICHER_AGENT_SANS_CHOIX";
	}

	public String getNOM_PB_AJOUTER_CHOIX_DPM() {
		return "NOM_PB_CREATE_BOX";
	}

	public String getNOM_ST_AGENT_CREATION() {
		return "NOM_ST_AGENT_CREATION";
	}

	public String getVAL_ST_AGENT_CREATION() {
		return getZone(getNOM_ST_AGENT_CREATION());
	}

	public String getNOM_ST_AGENT_MODIFICATION() {
		return "NOM_ST_AGENT_MODIFICATION";
	}

	public String getVAL_ST_AGENT_MODIFICATION() {
		return getZone(getNOM_ST_AGENT_MODIFICATION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_CREATION() {
		return "NOM_PB_RECHERCHER_AGENT_CREATION";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public String getNOM_PB_CREATION() {
		return "NOM_PB_CREATION";
	}

	public String getNOM_PB_MODIFICATION() {
		return "NOM_PB_MODIFICATION";
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getNOM_PB_SAISIE_CHOIX(int i) {
		return "NOM_PB_SAISIE_CHOIX_" + i;
	}

	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER_" + i;
	}

	public String getNOM_PB_VALIDER_SUPPRESSION() {
		return "NOM_PB_VALIDER_SUPPRESSION";
	}

	public String getNOM_ST_OPERATEUR(int i) {
		return "getNOM_ST_OPERATEUR" + i;
	}

	public String getVAL_ST_OPERATEUR(int i) {
		return getZone(getNOM_ST_OPERATEUR(i));
	}

	public String getNOM_RG_CHOIX() {
		return "NOM_RG_CHOIX";
	}

	public String getVAL_RG_CHOIX() {
		return getZone(getNOM_RG_CHOIX());
	}

	public String getNOM_RB_INDEMNITE() {
		return "NOM_RB_INDEMNITE";
	}

	public String getNOM_RB_RECUPERATION() {
		return "NOM_RB_RECUPERATION";
	}

	public String getNOM_ST_INDICE_CHOIX_AGENT() {
		return "NOM_ST_INDICE_CHOIX_AGENT";
	}

	public String getVAL_ST_INDICE_CHOIX_AGENT() {
		return getZone(getNOM_ST_INDICE_CHOIX_AGENT());
	}

	public String[] getVAL_LB_CHOIX() {
		return getLB_CHOIX();
	}

	public String getNOM_LB_CHOIX() {
		return "NOM_LB_CHOIX";
	}
	
	private void setLB_CHOIX(String[] newLB_CHOIX) {
		LB_CHOIX = newLB_CHOIX;
	}
	private String[] getLB_CHOIX() {
		if (LB_CHOIX == null) {
			LB_CHOIX = initialiseLazyLB();
		}
		return LB_CHOIX;
	}
	
	public String getNOM_LB_CHOIX_SELECT() {
		return "NOM_LB_CHOIX_SELECT";
	}
	public String getVAL_LB_CHOIX_SELECT() {
		return getZone(getNOM_LB_CHOIX_SELECT());
	}

	public TreeMap<Integer, DpmIndemniteChoixAgentDto> getListeChoixAgent() {
		return listeChoixAgent == null ? new TreeMap<Integer, DpmIndemniteChoixAgentDto>() : listeChoixAgent;
	}

	public void setListeChoixAgent(ArrayList<DpmIndemniteChoixAgentDto> pListeChoixAgent) {

		listeChoixAgent = new TreeMap<>();
		int i = 0;
		for (DpmIndemniteChoixAgentDto choix : pListeChoixAgent) {
			listeChoixAgent.put(i, choix);
			i++;
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	public IPtgService getPtgService() {

		if (null == ptgService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			ptgService = (PtgService) context.getBean("ptgService");
		}
		return ptgService;
	}
	
	

	public DpmIndemniteAnneeDto getDpmAnneeOuverte() {
		return dpmAnneeOuverte;
	}

	public void setDpmAnneeOuverte(DpmIndemniteAnneeDto dpmAnneeOuverte) {
		this.dpmAnneeOuverte = dpmAnneeOuverte;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton
			// PB_RECHERCHER_AGENT_DEMANDE
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
			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}
			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_AFFICHER_AGENT_SANS_CHOIX())) {
				return performPB_AFFICHER_AGENTS_SANS_CHOIX(request);
			}
			// Si clic sur le bouton PB_AJOUTER_ABSENCE
			if (testerParametre(request, getNOM_PB_AJOUTER_CHOIX_DPM())) {
				return performPB_AJOUTER_CHOIX_DPM(request);
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

			// Si clic sur les boutons du tableau
			for (Integer indiceTR : getListeChoixAgent().keySet()) {
				if (testerParametre(request, getNOM_PB_SAISIE_CHOIX(indiceTR))) {
					return performPB_MODIFICATION(request, indiceTR);
				}
				if (testerParametre(request, getNOM_PB_SUPPRIMER(indiceTR))) {
					return performPB_SUPPRIMER(request, indiceTR);
				}
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_ANNULATION
			if (testerParametre(request, getNOM_PB_MODIFICATION())) {
				return performPB_VALIDER_MODIFICATION(request);
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_ANNULATION
			if (testerParametre(request, getNOM_PB_MODIFICATION())) {
				return performPB_VALIDER_MODIFICATION(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}
