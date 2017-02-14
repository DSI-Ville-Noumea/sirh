package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasDemandeDto;
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
import nc.noumea.spring.service.PtgService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OePTGTitreRepas extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OePTGTitreRepas.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4481591314447007514L;

	public static final int STATUT_RECHERCHER_AGENT_DEMANDE = 1;
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 2;

	public String focus = null;
	private String[] LB_ETAT;
	private String[] LB_COMMANDE;
	private String[] LB_DATE_MOIS;

	private IPtgService ptgService;

	private IAdsService adsService;

	private IRadiService radiService;

	private AgentDao agentDao;

	private ArrayList<RefEtatDto> listeEtats;
	private ArrayList<Date> listeDateMois;

	private TreeMap<Integer, TitreRepasDemandeDto> listeDemandeTR;
	private HashMap<Integer, List<TitreRepasDemandeDto>> history = new HashMap<>();

	private String typeFiltre;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdfMMyyyy = new SimpleDateFormat("MM/yyyy");
	private SimpleDateFormat sdfHrs = new SimpleDateFormat("HH:mm");

	public String ACTION_CREATION = "Creation_demande";
	public String ACTION_MODIFICATION = "Modification_demande";
	public String ACTION_MOTIF_REJET = "Motif_rejet_demande";

	@Override
	public String getJSP() {
		return "OePTGTitreRepas.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-TITRE-REPAS";
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
		initialiseListeDeroulante();
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			List<RefEtatDto> etats = ptgService.getEtatsTitreRepas();
			setListeEtats((ArrayList<RefEtatDto>) etats);
			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (RefEtatDto etat : etats) {
				String ligne[] = { etat.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);
		}

		if (getLB_COMMANDE() == LBVide) {
			int[] tailles = { 3 };
			FormateListe aFormat = new FormateListe(tailles);
			String ligneOui[] = { "Oui" };
			String ligneNon[] = { "Non" };
			aFormat.ajouteLigne(ligneOui);
			aFormat.ajouteLigne(ligneNon);
			setLB_COMMANDE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_COMMANDE_SELECT(), Const.ZERO);
		}

		if (getLB_DATE_MOIS() == LBVide) {
			List<Date> listeDateMois = ptgService.getFiltreListeMois();
			setListeDateMois((ArrayList<Date>) listeDateMois);
			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (Date dateMois : listeDateMois) {
				String ligne[] = { sdfMMyyyy.format(dateMois) };
				aFormat.ajouteLigne(ligne);
			}
			setLB_DATE_MOIS(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_DATE_MOIS_SELECT(), Const.ZERO);
		}
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
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = null;
		if (!dateDeb.equals(Const.CHAINE_VIDE)) {
			dateMin = Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");
		}

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = null;
		if (!dateFin.equals(Const.CHAINE_VIDE)) {
			dateMax = Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");
		} else {
			// dateMax = new SimpleDateFormat("yyyyMMdd").format(new Date());
			dateMax = dateMin;
			addZone(getNOM_ST_DATE_MAX(), getVAL_ST_DATE_MIN());
		}

		// date mois
		int numDateMois = (Services.estNumerique(getZone(getNOM_LB_DATE_MOIS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_DATE_MOIS_SELECT())) : -1);
		String dateMoisStr = null;
		if (numDateMois != -1 && numDateMois != 0) {
			Date dateMois = (Date) getListeDateMois().get(numDateMois - 1);
			dateMoisStr = sdfyyyyMMdd.format(dateMois);
		}

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		RefEtatDto etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (RefEtatDto) getListeEtats().get(numEtat - 1);
		}

		// agent
		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();
		if (!getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_DEMANDE(), getVAL_ST_AGENT_DEMANDE());
		} else {
			idAgentDemande = null;
		}

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
		}

		if (listIdAgentService.size() >= 1000) {
			// "ERR501",
			// "La sélection des filtres engendre plus de 1000 agents. Merci de reduire la sélection."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR501"));
			return false;
		}

		// agent SIRH connecte
		Agent agentConnecte = getAgentConnecte(request);

		// COMMANDE ou pas
		int commande = (Services.estNumerique(getZone(getNOM_LB_COMMANDE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_COMMANDE_SELECT())) : -1);
		Boolean isCommande = null;
		if (commande == 1) {
			isCommande = true;
		} else if (commande == 2) {
			isCommande = false;
		}

		List<TitreRepasDemandeDto> listedemandeTR = ptgService.getListTitreRepas(agentConnecte.getIdAgent(), dateMin, dateMax, etat != null ? etat.getIdRefEtat() : null, isCommande, dateMoisStr,
				!idServiceAds.equals(Const.CHAINE_VIDE) ? new Integer(idServiceAds) : null, null != idAgentDemande ? new Integer(idAgentDemande) : null, listIdAgentService);

		setListeDemandeTR((ArrayList<TitreRepasDemandeDto>) listedemandeTR);

		afficheListeDemandesTR();

		setTypeFiltre("GLOBAL");

		return true;
	}

	private void afficheListeDemandesTR() throws Exception {

		for (Entry<Integer, TitreRepasDemandeDto> TRMap : getListeDemandeTR().entrySet()) {
			TitreRepasDemandeDto TR = TRMap.getValue();
			Integer i = TRMap.getKey();
			try {
				Agent ag = agentDao.chercherAgent(TR.getAgent().getIdAgent());

				addZone(getNOM_ST_MATRICULE(i), null != ag ? ag.getNomatr().toString() : "");
				addZone(getNOM_ST_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent());
				addZone(getNOM_ST_DATE_MONTH(i), sdf.format(TR.getDateMonth()));
				addZone(getNOM_ST_COMMANDE(i), TR.getCommande() ? "Oui" : "Non");

				String commentaire = TR.getCommentaire();
				addZone(getNOM_ST_COMMENTAIRE(i), commentaire);
				addZone(getNOM_ST_OPERATEUR(i), null == TR.getOperateur() ? "" : TR.getOperateur().getPrenom() + " " + TR.getOperateur().getNom() + " (" + (TR.getOperateur().getIdAgent() - 9000000)
						+ ")");
				addZone(getNOM_ST_ETAT(i), EtatPointageEnum.getDisplayableEtatPointageEnum(TR.getIdRefEtat()));
				addZone(getNOM_ST_DATE_ETAT(i), sdf.format(TR.getDateSaisie()));
			} catch (Exception e) {
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

	public boolean performPB_APPROUVER(HttpServletRequest request, int idDemandeTR) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		TitreRepasDemandeDto demandeTR = getListeDemandeTR().get(idDemandeTR);
		changeState(request, demandeTR, EtatPointageEnum.APPROUVE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_REJETER(HttpServletRequest request, int idDemandeTR) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}

		String info = "Veuillez saisir un motif de rejet de titre repas.";
		addZone(getNOM_ST_INFO_MOTIF_REJET(), info);
		addZone(getNOM_ST_MOTIF_REJET(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_DEMANDE_REJET(), new Integer(idDemandeTR).toString());

		addZone(getNOM_ST_ACTION(), ACTION_MOTIF_REJET);
		
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void changeState(HttpServletRequest request, TitreRepasDemandeDto dem, EtatPointageEnum state, String commentaire) throws Exception {
		ArrayList<TitreRepasDemandeDto> param = new ArrayList<TitreRepasDemandeDto>();
		param.add(dem);
		changeState(request, param, state, commentaire);
	}

	private void changeState(HttpServletRequest request, Collection<TitreRepasDemandeDto> dem, EtatPointageEnum state, String commentaire) throws Exception {
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			logger.debug("Agent nul dans jsp visualisation");
		} else {
			List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();
			for (TitreRepasDemandeDto dto : dem) {
				dto.setIdRefEtat(state.getCodeEtat());
				dto.setCommentaire(commentaire);
				listDto.add(dto);
			}

			ReturnMessageDto message = ptgService.setTRState(listDto, agentConnecte.getIdAgent());

			for (TitreRepasDemandeDto d : dem) {
				refreshHistory(d.getIdTrDemande());
			}

			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
			}
			if (message.getInfos().size() > 0) {
				String inf = Const.CHAINE_VIDE;
				for (String info : message.getInfos()) {
					inf += " " + info;
				}
				getTransaction().declarerErreur(inf);
			}
			if (null != getTypeFiltre() && getTypeFiltre().equals("GLOBAL")) {
				performPB_FILTRER(request);
			} else {
				performPB_FILTRER_DEMANDE_A_APPROUVER(request);
			}
		}
	}

	public boolean performPB_FILTRER_DEMANDE_A_APPROUVER(HttpServletRequest request) throws Exception {

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = dateDeb.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = dateFin.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();

		String dateJour = sdfyyyyMMdd.format(new DateTime().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate());

		// ////// APPEL WS /////////////
		List<TitreRepasDemandeDto> listedemandeTR = ptgService.getListTitreRepas(getAgentConnecte(request).getIdAgent(), dateMin, dateMax, EtatPointageEnum.SAISI.getCodeEtat(), Boolean.TRUE,
				dateJour, null, null != idAgentDemande ? new Integer(idAgentDemande) : null, null);

		logger.debug("Taille liste Titre Repas : " + listedemandeTR.size());

		setListeDemandeTR((ArrayList<TitreRepasDemandeDto>) listedemandeTR);

		afficheListeDemandesTR();

		setTypeFiltre("VALIDER");

		return true;
	}

	public boolean performPB_APPROUVER_ALL(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}

		changeState(request, getListeDemandeTR().values(), EtatPointageEnum.APPROUVE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_VALIDER_MOTIF_REJET(HttpServletRequest request) throws Exception {
		// on recupere les infos
		String idDemande = getVAL_ST_ID_DEMANDE_REJET();
		String motif = getVAL_ST_MOTIF_REJET();
		if (motif.equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}
		TitreRepasDemandeDto dem = getListeDemandeTR().get(Integer.valueOf(idDemande));

		changeState(request, dem, EtatPointageEnum.REJETE, motif);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		if (null != getTypeFiltre() && getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_APPROUVER(request);
		}
		return true;
	}

	public String getHistory(int idDemandeTR) {

		List<TitreRepasDemandeDto> data = getPtgService().getVisualisationTitreRepasHistory(idDemandeTR);
		int numParams = 6;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		for (TitreRepasDemandeDto tr : data) {
			ret[index][0] = formatDate(tr.getDateMonth());
			ret[index][1] = tr.getCommande() ? "Oui" : "Non";
			ret[index][2] = null == tr.getCommentaire() ? "" : tr.getCommentaire();
			AgentDto opPtg = tr.getOperateur();
			if (null != opPtg) {
				ret[index][3] = opPtg.getNom() + " " + opPtg.getPrenom() + " (" + opPtg.getIdAgent().toString().substring(3, opPtg.getIdAgent().toString().length()) + ")";
			} else {
				ret[index][3] = "";
			}
			ret[index][4] = EtatPointageEnum.getEtatPointageEnum(tr.getIdRefEtat()).name();
			ret[index][5] = formatDate(tr.getDateSaisie()) + " a " + formatHeure(tr.getDateSaisie());
			index++;
		}

		StringBuilder strret = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			// strret.append("[");
			for (int j = 0; j < numParams; j++) {
				strret.append(ret[i][j]).append(",");
			}
			strret.deleteCharAt(strret.lastIndexOf(","));
			strret.append("|");
		}
		strret.deleteCharAt(strret.lastIndexOf("|"));
		return strret.toString();
	}

	private String formatDate(Date d) {
		if (d != null) {
			return sdf.format(d);
		} else {
			return Const.CHAINE_VIDE;
		}
	}

	private String formatHeure(Date d) {
		if (d != null) {
			return sdfHrs.format(d);
		} else {
			return "00:00";
		}
	}

	public boolean performPB_AJOUTER_DEMANDE_TR(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		addZone(getNOM_RG_COMMANDE(), Const.CHAINE_VIDE);

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

		if (getZone(getNOM_RG_COMMANDE()).equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Oui ou Non"));
			return false;
		}

		AgentDto agentDto = new AgentDto();
		agentDto.setIdAgent(new Integer(idAgent));

		Date dateMonth = new DateTime().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();

		dto.setAgent(agentDto);
		dto.setCommande(getZone(getNOM_RG_COMMANDE()).equals(getNOM_RB_OUI()));
		dto.setDateMonth(dateMonth);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();
		listDto.add(dto);

		ReturnMessageDto srm = ptgService.enregistreTitreRepas(listDto, getAgentConnecte(request).getIdAgent());

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
		if (null != getTypeFiltre() && getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_APPROUVER(request);
		}

		return true;
	}

	public boolean performPB_MODIFICATION(HttpServletRequest request, Integer indiceTR) throws Exception {

		TitreRepasDemandeDto dto = getListeDemandeTR().get(indiceTR);

		addZone(getNOM_ST_INDICE_TR(), indiceTR.toString());
		addZone(getNOM_ST_AGENT_MODIFICATION(), new Integer(dto.getAgent().getIdAgent() - 9000000).toString());
		addZone(getNOM_RG_COMMANDE(), dto.getCommande() ? getNOM_RB_OUI() : getNOM_RB_NON());

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

		if (getZone(getNOM_RG_COMMANDE()).equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Oui ou Non"));
			return false;
		}

		Integer indiceTR = new Integer(getVAL_ST_INDICE_TR());

		TitreRepasDemandeDto dto = getListeDemandeTR().get(indiceTR);

		dto.setCommande(getZone(getNOM_RG_COMMANDE()).equals(getNOM_RB_OUI()));

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();
		listDto.add(dto);

		ReturnMessageDto srm = ptgService.enregistreTitreRepas(listDto, getAgentConnecte(request).getIdAgent());

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
		if (null != getTypeFiltre() && getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_APPROUVER(request);
		}
		
		return true;
	}
	
	public boolean performPB_GENERER(HttpServletRequest request) throws Exception {
		
		ReturnMessageDto srm = ptgService.genereEtatPayeurTitreRepas(getAgentConnecte(request).getIdAgent());
		
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
			getTransaction().declarerErreur("Génération : " + info);
		}

		return true;
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

	public ArrayList<RefEtatDto> getListeEtats() {
		return listeEtats;
	}

	public void setListeEtats(ArrayList<RefEtatDto> listeEtats) {
		this.listeEtats = listeEtats;
	}

	public ArrayList<Date> getListeDateMois() {
		return listeDateMois;
	}

	public void setListeDateMois(ArrayList<Date> pListeDateMois) {
		this.listeDateMois = pListeDateMois;
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ETAT() {
		if (LB_ETAT == null) {
			LB_ETAT = initialiseLazyLB();
		}
		return LB_ETAT;
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_COMMANDE(String[] newLB_COMMANDE) {
		LB_COMMANDE = newLB_COMMANDE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_COMMANDE() {
		if (LB_COMMANDE == null) {
			LB_COMMANDE = initialiseLazyLB();
		}
		return LB_COMMANDE;
	}

	public String getNOM_PB_VALIDATION() {
		return "NOM_PB_VALIDATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ETAT_SELECT() {
		return "NOM_LB_ETAT_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_COMMANDE_SELECT() {
		return "NOM_LB_COMMANDE_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_COMMANDE_SELECT() {
		return getZone(getNOM_LB_COMMANDE_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_DATE_MOIS_SELECT() {
		return "NOM_LB_DATE_MOIS_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_DATE_MOIS_SELECT() {
		return getZone(getNOM_LB_DATE_MOIS_SELECT());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_DATE_MOIS() {
		return getLB_DATE_MOIS();
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_DATE_MOIS(String[] newLB_DATE_MOIS) {
		LB_DATE_MOIS = newLB_DATE_MOIS;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_DATE_MOIS() {
		if (LB_DATE_MOIS == null) {
			LB_DATE_MOIS = initialiseLazyLB();
		}
		return LB_DATE_MOIS;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (13/09/11 11:47:15)
	 * 
	 */
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

	public String getNOM_ST_DATE_MONTH(int i) {
		return "NOM_ST_DATE_MONTH_" + i;
	}

	public String getVAL_ST_DATE_MONTH(int i) {
		return getZone(getNOM_ST_DATE_MONTH(i));
	}

	public String getNOM_ST_COMMANDE(int i) {
		return "NOM_ST_COMMANDE_" + i;
	}

	public String getVAL_ST_COMMANDE(int i) {
		return getZone(getNOM_ST_COMMANDE(i));
	}

	public String getNOM_ST_DATE_ETAT(int i) {
		return "NOM_ST_DATE_ETAT_" + i;
	}

	public String getVAL_ST_DATE_ETAT(int i) {
		return getZone(getNOM_ST_DATE_ETAT(i));
	}

	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE_" + i;
	}

	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	public String[] getVAL_LB_COMMANDE() {
		return getLB_COMMANDE();
	}

	public String getNOM_LB_ETAT() {
		return "NOM_LB_ETAT";
	}

	public String getNOM_LB_COMMANDE() {
		return "NOM_LB_COMMANDE";
	}

	public String getNOM_LB_DATE_MOIS() {
		return "NOM_LB_DATE_MOIS";
	}

	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
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

	public String getNOM_PB_FILTRER_DEMANDE_A_VALIDER() {
		return "NOM_PB_FILTRER_DEMANDE_A_VALIDER";
	}

	public String getNOM_PB_GENERER() {
		return "NOM_PB_GENERER";
	}

	public String getNOM_PB_VALIDER_ALL() {
		return "NOM_PB_VALIDER_ALL";
	}

	public String getNOM_PB_REJETER_ALL() {
		return "NOM_PB_REJETER_ALL";
	}

	public String getNOM_PB_AJOUTER_DEMANDE_TR() {
		return "NOM_PB_CREATE_BOX";
	}

	public String getNOM_PB_APPROUVER(int i) {
		return "NOM_PB_APPROUVER" + i;
	}

	public String getNOM_PB_REJETER(int i) {
		return "NOM_PB_REJETER" + i;
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

	public String getNOM_ST_INFO_MOTIF_REJET() {
		return "NOM_ST_INFO_MOTIF_REJET";
	}

	public String getVAL_ST_INFO_MOTIF_REJET() {
		return getZone(getNOM_ST_INFO_MOTIF_REJET());
	}

	public String getNOM_ST_MOTIF_REJET() {
		return "NOM_ST_MOTIF_REJET";
	}

	public String getVAL_ST_MOTIF_REJET() {
		return getZone(getNOM_ST_MOTIF_REJET());
	}

	public String getNOM_ST_ID_DEMANDE_REJET() {
		return "NOM_ST_ID_DEMANDE_REJET";
	}

	public String getVAL_ST_ID_DEMANDE_REJET() {
		return getZone(getNOM_ST_ID_DEMANDE_REJET());
	}

	public String getNOM_PB_VALIDER_MOTIF_REJET() {
		return "NOM_PB_VALIDER_MOTIF_REJET";
	}

	public String getTypeFiltre() {
		return typeFiltre;
	}

	public void setTypeFiltre(String typeFiltre) {
		this.typeFiltre = typeFiltre;
	}

	/**
     *
     */
	public String getNOM_PB_SAISIE_TR(int i) {
		return "NOM_PB_SAISIE_TR_" + i;
	}

	public String getNOM_ST_OPERATEUR(int i) {
		return "getNOM_ST_OPERATEUR" + i;
	}

	public String getVAL_ST_OPERATEUR(int i) {
		return getZone(getNOM_ST_OPERATEUR(i));
	}

	public String getNOM_RG_COMMANDE() {
		return "NOM_RG_COMMANDE";
	}

	public String getVAL_RG_COMMANDE() {
		return getZone(getNOM_RG_COMMANDE());
	}

	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	public String getNOM_ST_INDICE_TR() {
		return "NOM_ST_INDICE_TR";
	}

	public String getVAL_ST_INDICE_TR() {
		return getZone(getNOM_ST_INDICE_TR());
	}

	public TreeMap<Integer, TitreRepasDemandeDto> getListeDemandeTR() {
		return listeDemandeTR == null ? new TreeMap<Integer, TitreRepasDemandeDto>() : listeDemandeTR;
	}

	public void setListeDemandeTR(ArrayList<TitreRepasDemandeDto> listedemandeTR) {
		// on tri la liste
		Collections.sort(listedemandeTR, new Comparator<TitreRepasDemandeDto>() {
			@Override
			public int compare(TitreRepasDemandeDto o1, TitreRepasDemandeDto o2) {
				// tri par date
				// ajout du "0 -" pour trier en ordre decroissant
				return 0 - o1.getDateMonth().compareTo(o2.getDateMonth());
			}

		});

		listeDemandeTR = new TreeMap<>();
		int i = 0;
		for (TitreRepasDemandeDto dem : listedemandeTR) {
			listeDemandeTR.put(i, dem);
			i++;
		}
	}

	private void refreshHistory(int demandeTRId) {
		history.remove(demandeTRId);
		history.put(demandeTRId, ptgService.getVisualisationTitreRepasHistory(demandeTRId));
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

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_FILTRER_DEMANDE_A_VALIDER
			if (testerParametre(request, getNOM_PB_FILTRER_DEMANDE_A_VALIDER())) {
				return performPB_FILTRER_DEMANDE_A_APPROUVER(request);
			}

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
			// Si clic sur le bouton PB_AJOUTER_ABSENCE
			if (testerParametre(request, getNOM_PB_AJOUTER_DEMANDE_TR())) {
				return performPB_AJOUTER_DEMANDE_TR(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_CREATION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATION())) {
				return performPB_RECHERCHER_AGENT_CREATION(request);
			}
			// Si clic sur le bouton PB_GENERER
			if (testerParametre(request, getNOM_PB_GENERER())) {
				return performPB_GENERER(request);
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
			for (Integer indiceTR : getListeDemandeTR().keySet()) {
				// Si clic sur le bouton PB_VALIDER
				if (testerParametre(request, getNOM_PB_APPROUVER(indiceTR))) {
					return performPB_APPROUVER(request, indiceTR);
				}
				// Si clic sur le bouton PB_REJETER
				if (testerParametre(request, getNOM_PB_REJETER(indiceTR))) {
					return performPB_REJETER(request, indiceTR);
				}
				if (testerParametre(request, getNOM_PB_SAISIE_TR(indiceTR))) {
					return performPB_MODIFICATION(request, indiceTR);
				}
			}
			// Si clic sur le bouton PB_VALIDER_ALL
			if (testerParametre(request, getNOM_PB_VALIDER_ALL())) {
				return performPB_APPROUVER_ALL(request);
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_ANNULATION
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_REJET())) {
				return performPB_VALIDER_MOTIF_REJET(request);
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
