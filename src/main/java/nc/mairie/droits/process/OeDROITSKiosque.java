package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.InputterDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.dto.ViseursDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.PtgService;

import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 * Process OeDROITSGestion Date de création : (10/10/11 14:37:55)
 */
public class OeDROITSKiosque extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String ACTION_GERER_DROIT_ABS = "Gestion des droits ABS de l'approbateur";
	public String ACTION_GERER_DROIT_PTG = "Gestion des droits PTG de l'approbateur";
	
	public String ACTION_DUPLIQUER_DROIT_ABS_PTG = "Dupliquer les droits ABS et PTG de l'approbateur";

	public static final int STATUT_APPROBATEUR = 1;
	public static final int STATUT_DELEGATAIRE_ABS = 2;
	public static final int STATUT_DELEGATAIRE_PTG = 3;
	public static final int STATUT_RECHERCHER_AGENT = 4;
	public static final int STATUT_AGENT_APPROBATEUR_ABS = 5;
	public static final int STATUT_OPE_APPROBATEUR_ABS = 6;
	public static final int STATUT_VISEUR_APPROBATEUR_ABS = 7;
	public static final int STATUT_AGENT_APPROBATEUR_PTG = 8;
	public static final int STATUT_OPE_APPROBATEUR_PTG = 9;
	public static final int STATUT_AGENT_OPE_APPROBATEUR_PTG = 10;
	public static final int STATUT_AGENT_OPE_APPROBATEUR_ABS = 11;
	public static final int STATUT_AGENT_VISEUR_APPROBATEUR_ABS = 12;
	public static final int STATUT_AGENT_MAIRIE_APPROBATEUR_ABS = 13;
	public static final int STATUT_AGENT_MAIRIE_APPROBATEUR_PTG = 14;
	public static final int STATUT_RECHERCHER_AGENT_DUPLICATION = 15;

	private ArrayList<ApprobateurDto> listeApprobateurs = new ArrayList<ApprobateurDto>();
	private ArrayList<ApprobateurDto> listeApprobateursPTG = new ArrayList<ApprobateurDto>();
	private ArrayList<ApprobateurDto> listeApprobateursABS = new ArrayList<ApprobateurDto>();
	private Hashtable<ApprobateurDto, ArrayList<String>> hashApprobateur;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	private AgentWithServiceDto approbateurCourant;

	private ArrayList<AgentDto> listeAgentsApprobateurAbs = new ArrayList<AgentDto>();
	private ArrayList<AgentDto> listeAgentsOperateurAbs = new ArrayList<AgentDto>();
	private ArrayList<AgentDto> listeAgentsViseurAbs = new ArrayList<AgentDto>();

	private ArrayList<AgentDto> listeAgentsApprobateurPtg = new ArrayList<AgentDto>();
	private ArrayList<AgentDto> listeAgentsOperateurPtg = new ArrayList<AgentDto>();
	private AgentDto operateurCourant;
	private AgentDto viseurCourant;

	public String focus = null;

	private IAdsService adsService;

	private IAbsService absService;

	private IPtgService ptgService;

	private IRadiService radiService;

	/**
	 * @return String Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (10/10/11 16:15:05)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		if (getVAL_RG_TRI().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_RG_TRI(), getNOM_RB_TRI_AGENT());
		}
		
		// on gere ici le click sur le bouton annuler provenant de OeAgentRechercheDroitKiosque
		// afin de ne pas supprimer tous les agents
		Boolean isClickAnnule = (Boolean) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_ANNULATION);
		if(null != isClickAnnule) {
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_ANNULATION);
			if(isClickAnnule) 
				return;
		}

		if (etatStatut() == STATUT_APPROBATEUR) {
			saveAjoutApprobateurs(request);
		}

		if (etatStatut() == STATUT_DELEGATAIRE_PTG) {
			saveDelegatairePtg(request, false);
			performPB_AFFICHER(request);
		}

		if (etatStatut() == STATUT_DELEGATAIRE_ABS) {
			saveDelegataireAbs(request, false);
			performPB_AFFICHER(request);
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {

			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null && agt.getIdAgent() != null) {
				addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
				performPB_AFFICHER(request);
			}
		}

		if (etatStatut() == STATUT_AGENT_APPROBATEUR_ABS) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listAgt = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			saveAgentApprobateurAbs(request, listAgt == null ? new ArrayList<AgentDto>() : listAgt, false, true);
		}

		if (etatStatut() == STATUT_AGENT_MAIRIE_APPROBATEUR_ABS) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			
			if(null != agt) {
				saveAgentApprobateurAbs(request,
					agt == null ? new ArrayList<AgentDto>() : Arrays.asList(new AgentDto(agt)), false, false);
			}
		}

		if (etatStatut() == STATUT_OPE_APPROBATEUR_ABS) {
			Agent operateur = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			saveOperateurApprobateurAbs(request, operateur, false);
		}

		if (etatStatut() == STATUT_VISEUR_APPROBATEUR_ABS) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listViseur = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			saveViseurApprobateurAbs(request, listViseur == null ? new ArrayList<AgentDto>() : listViseur, false);
		}

		if (etatStatut() == STATUT_AGENT_APPROBATEUR_PTG) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listAgt = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			saveAgentApprobateurPtg(request, listAgt == null ? new ArrayList<AgentDto>() : listAgt, false, true);
		}

		if (etatStatut() == STATUT_AGENT_MAIRIE_APPROBATEUR_PTG) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if(null != agt) {
				saveAgentApprobateurPtg(request,
					agt == null ? new ArrayList<AgentDto>() : Arrays.asList(new AgentDto(agt)), false, false);
			}
		}

		if (etatStatut() == STATUT_OPE_APPROBATEUR_PTG) {
			Agent operateur = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			saveOperateurApprobateurPtg(request, operateur, false);
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_DUPLICATION) {
			Agent agent = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_DUPLICATION(), agent.getNomatr().toString());
		}

		if (etatStatut() == STATUT_AGENT_OPE_APPROBATEUR_PTG) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listAgt = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			if (null != listAgt) {
				saveAgentApprobateurOperateurPtg(request, listAgt == null ? new ArrayList<AgentDto>() : listAgt);
			}
		}

		if (etatStatut() == STATUT_AGENT_OPE_APPROBATEUR_ABS) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listAgt = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			if (null != listAgt) {
				saveAgentApprobateurOperateurAbs(request, listAgt == null ? new ArrayList<AgentDto>() : listAgt);
			}
		}

		if (etatStatut() == STATUT_AGENT_VISEUR_APPROBATEUR_ABS) {
			@SuppressWarnings("unchecked")
			List<AgentDto> listAgt = (List<AgentDto>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT);
			if (null != listAgt) {
				saveAgentApprobateurViseurAbs(request, listAgt == null ? new ArrayList<AgentDto>() : listAgt);
			}
		}

	}

	private void saveAgentApprobateurViseurAbs(HttpServletRequest request, List<AgentDto> list) throws Exception {
		if (getApprobateurCourant() != null && getViseurCourant() != null) {
			Agent agentConnecte = getAgentConnecte(request);

			ReturnMessageDto result = absService.saveAgentsViseur(getApprobateurCourant().getIdAgent(),
					getViseurCourant().getIdAgent(), list,agentConnecte.getIdAgent());

			String err = Const.CHAINE_VIDE;
			for (String erreur : result.getErrors()) {
				err += " " + erreur;
			}
			if (!err.equals(Const.CHAINE_VIDE))
				getTransaction().declarerErreur("ERREUR : " + err);

		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return;
		}
	}

	private void saveAgentApprobateurOperateurAbs(HttpServletRequest request, List<AgentDto> list) throws Exception {
		if (getApprobateurCourant() != null && getOperateurCourant() != null) {
			Agent agentConnecte = getAgentConnecte(request);

			ReturnMessageDto result = absService.saveAgentsOperateur(getApprobateurCourant().getIdAgent(),
					getOperateurCourant().getIdAgent(), list,agentConnecte.getIdAgent());

			String err = Const.CHAINE_VIDE;
			for (String erreur : result.getErrors()) {
				err += " " + erreur;
			}
			if (!err.equals(Const.CHAINE_VIDE))
				getTransaction().declarerErreur("ERREUR : " + err);

		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return;
		}
	}

	private void saveAgentApprobateurOperateurPtg(HttpServletRequest request, List<AgentDto> list) {
		if (getApprobateurCourant() != null && getOperateurCourant() != null) {

			ReturnMessageDto result = ptgService.saveAgentsSaisisOperateur(getApprobateurCourant().getIdAgent(),
					getOperateurCourant().getIdAgent(), list);

			String err = Const.CHAINE_VIDE;
			for (String erreur : result.getErrors()) {
				err += " " + erreur;
			}
			if (!err.equals(Const.CHAINE_VIDE))
				getTransaction().declarerErreur("ERREUR : " + err);

		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return;
		}

	}

	private void saveViseurApprobateurAbs(HttpServletRequest request, List<AgentDto> listAgtAAjouter,
			boolean suppression) throws Exception {
		Agent agentConnecte = getAgentConnecte(request);
		if (!suppression) {
			if (getApprobateurCourant() != null) {
				// #15731
				AgentDto viseurAAjouter = null;
				String err = Const.CHAINE_VIDE;
				for (AgentDto ajout : listAgtAAjouter) {
					if (!getListeAgentsViseurAbs().contains(ajout))
						getListeAgentsViseurAbs().add(ajout);
					viseurAAjouter = ajout;

					ReturnMessageDto result = absService.saveViseurApprobateur(getApprobateurCourant().getIdAgent(),
							viseurAAjouter,agentConnecte.getIdAgent());

					for (String erreur : result.getErrors()) {
						err += " " + erreur;
					}
				}

				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			if (getApprobateurCourant() != null) {
				// #15731
				AgentDto viseurASupprimer = null;
				for (AgentDto delete : listAgtAAjouter) {
					if (getListeAgentsViseurAbs().contains(delete)) {
						getListeAgentsViseurAbs().remove(delete);
						viseurASupprimer = delete;
					}
				}

				ReturnMessageDto result = absService.deleteViseurApprobateur(getApprobateurCourant().getIdAgent(),
						viseurASupprimer,agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		// on rafraichit les données
		performPB_GERER_DROIT_ABS(request, getApprobateurCourant().getIdAgent());

	}

	private void saveOperateurApprobateurAbs(HttpServletRequest request, Agent operateur, boolean suppression)
			throws Exception {

		if (operateur == null) {
			return;
		}
		if (operateur.getIdAgent().toString().equals(getApprobateurCourant().getIdAgent().toString())) {
			getTransaction().declarerErreur("ERREUR : L'agent ne peut être opérateur de lui même.");
			return;
		}
		Agent agentConnecte = getAgentConnecte(request);

		if (!suppression) {
			if (getApprobateurCourant() != null) {
				// #15731
				AgentDto ajoutOperateur = null;
				if (operateur != null) {
					ajoutOperateur = new AgentDto();
					ajoutOperateur.setIdAgent(operateur.getIdAgent());
					if (!getListeAgentsOperateurAbs().contains(ajoutOperateur))
						getListeAgentsOperateurAbs().add(ajoutOperateur);
				}

				InputterDto dto = new InputterDto();
				dto.setOperateurs(getListeAgentsOperateurAbs());
				// on recupere le delagataire existant
				ArrayList<ApprobateurDto> approExistant = (ArrayList<ApprobateurDto>) absService.getApprobateurs(null,
						getApprobateurCourant().getIdAgent());
				dto.setDelegataire(approExistant.get(0).getDelegataire());
				ReturnMessageDto result = absService.saveOperateurApprobateur(getApprobateurCourant().getIdAgent(),
						ajoutOperateur,agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			if (getApprobateurCourant() != null) {
				// #15731
				AgentDto deleteOperateur = null;
				if (operateur != null) {
					deleteOperateur = new AgentDto();
					deleteOperateur.setIdAgent(operateur.getIdAgent());
					if (getListeAgentsOperateurAbs().contains(deleteOperateur))
						getListeAgentsOperateurAbs().remove(deleteOperateur);
				}
				// on recupere le delagataire existant
				ReturnMessageDto result = absService.deleteOperateurApprobateur(getApprobateurCourant().getIdAgent(),
						deleteOperateur,agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		// on rafraichi les données
		performPB_GERER_DROIT_ABS(request, getApprobateurCourant().getIdAgent());
	}

	private void saveAgentApprobateurAbs(HttpServletRequest request, List<AgentDto> listAgtAAjouter, boolean suppression, boolean fromRechercheAbreServiceWithAgents)
			throws Exception {
		Agent agentConnecte = getAgentConnecte(request);

		if (!suppression) {
			if (getApprobateurCourant() != null) {
				
				if(fromRechercheAbreServiceWithAgents)
					getListeAgentsApprobateurAbs().clear();
				
				for (AgentDto ajout : listAgtAAjouter) {
					if (!getListeAgentsApprobateurAbs().contains(ajout))
						getListeAgentsApprobateurAbs().add(ajout);
				}

				ReturnMessageDto result = absService.saveAgentsApprobateur(getApprobateurCourant().getIdAgent(),
						getListeAgentsApprobateurAbs(),agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			if (getApprobateurCourant() != null) {
				for (AgentDto ajout : listAgtAAjouter) {
					if (getListeAgentsApprobateurAbs().contains(ajout))
						getListeAgentsApprobateurAbs().remove(ajout);
				}

				ReturnMessageDto result = absService.saveAgentsApprobateur(getApprobateurCourant().getIdAgent(),
						getListeAgentsApprobateurAbs(),agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		// on rafraichi les données
		performPB_GERER_DROIT_ABS(request, getApprobateurCourant().getIdAgent());

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
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
	}

	private void initialiseListeApprobateur(Integer idServiceADS, Agent agent) {

		// on construit la hashTable des approbateurs
		getHashApprobateur().clear();
		// on recupere les approbateurs de PTG
		ArrayList<ApprobateurDto> listeApproPTG = (ArrayList<ApprobateurDto>) ptgService.getApprobateurs(idServiceADS,
				agent == null ? null : agent.getIdAgent());
		setListeApprobateursPTG(listeApproPTG);
		ArrayList<ApprobateurDto> listeApproABS = (ArrayList<ApprobateurDto>) absService.getApprobateurs(idServiceADS,
				agent == null ? null : agent.getIdAgent());
		setListeApprobateursABS(listeApproABS);
		ArrayList<ApprobateurDto> listeComplete = new ArrayList<ApprobateurDto>();
		for (ApprobateurDto agDto : listeApproPTG) {
			if (!listeComplete.contains(agDto)) {
				listeComplete.add(agDto);
			}
		}
		for (ApprobateurDto agDto : listeApproABS) {
			if (!listeComplete.contains(agDto)) {
				listeComplete.add(agDto);
			}
		}
		for (ApprobateurDto agDto : listeComplete) {
			ArrayList<String> issuDe = new ArrayList<>();
			if (listeApproPTG.contains(agDto)) {
				issuDe.add("PTG");
			}
			if (listeApproABS.contains(agDto)) {
				issuDe.add("ABS");
			}
			getHashApprobateur().put(agDto, issuDe);
		}

		// on tri la liste
		Collections.sort(listeComplete, new Comparator<ApprobateurDto>() {
			@Override
			public int compare(ApprobateurDto o1, ApprobateurDto o2) {
				return o1.getApprobateur().getNom().compareTo(o2.getApprobateur().getNom());
			}

		});
		setListeApprobateurs(listeComplete);
	}

	private void saveAjoutApprobateurs(HttpServletRequest request) throws Exception {

		Agent ag = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (ag != null) {
			ApprobateurDto approDto = new ApprobateurDto();
			AgentWithServiceDto agentDto = new AgentWithServiceDto();
			agentDto.setIdAgent(ag.getIdAgent());

			if (!getListeApprobateurs().contains(approDto)) {
				Affectation affCourante = null;
				try {
					affCourante = getAffectationDao().chercherAffectationActiveAvecAgent(ag.getIdAgent());
					FichePoste fpCourante = getFichePosteDao().chercherFichePoste(affCourante.getIdFichePoste());
					EntiteDto serv = adsService.getEntiteByIdEntite(fpCourante.getIdServiceAds());
					agentDto.setIdServiceADS(fpCourante.getIdServiceAds());
					agentDto.setService(serv.getLabel());
				} catch (Exception e) {
					// l'agent n'est pas affecté on ne peut donc pas avoir son
					// service
					agentDto.setIdServiceADS(null);
					agentDto.setService("non affecté");
				}

				agentDto.setNom(ag.getNomAgent());
				agentDto.setPrenom(ag.getPrenomAgent());
				approDto.setApprobateur(agentDto);

				ReturnMessageDto messagePtg = saveApprobateurPTG(request, approDto.getApprobateur(), false);
				ReturnMessageDto messageAbs = saveApprobateurABS(request, approDto.getApprobateur(), false);

				String err = Const.CHAINE_VIDE;
				for (String erreur : messagePtg.getErrors()) {
					err += " " + erreur;
				}

				for (String erreur : messageAbs.getErrors()) {
					err += " " + erreur;
				}

				if (!err.equals(Const.CHAINE_VIDE)) {
					getTransaction().declarerErreur("ERREUR : " + err);
				}
				performPB_AFFICHER(request);
			}
		}
	}

	private ReturnMessageDto saveApprobateurABS(HttpServletRequest request, AgentWithServiceDto dto, boolean suppression) throws Exception {
		Agent agentConnecte = getAgentConnecte(request);
		if (suppression)
			return deleteApprobateurABS(request, dto,agentConnecte.getIdAgent());
		else
			return absService.setApprobateur(new JSONSerializer().exclude("*.class").serialize(dto),agentConnecte.getIdAgent());
	}

	private ReturnMessageDto saveApprobateurPTG(HttpServletRequest request, AgentWithServiceDto dto, boolean suppression) {
		if (suppression)
			return deleteApprobateurPTG(request, dto);
		else
			return ptgService.setApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
	}

	private void afficheListeApprobateurs() throws Exception {
		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			ArrayList<String> t = getHashApprobateur().get(ag);
			int i = ag.getApprobateur().getIdAgent();
			addZone(getNOM_ST_AGENT(i),
					ag.getApprobateur().getNom()
							+ " "
							+ ag.getApprobateur().getPrenom()
							+ " ("
							+ ag.getApprobateur().getIdAgent().toString()
									.substring(3, ag.getApprobateur().getIdAgent().toString().length()) + ")");
			addZone(getNOM_ST_SERVICE(i), ag.getApprobateur().getService() + " ("
					+ ag.getApprobateur().getSigleService() + ")");
			// on cherche si l'agent a un delegataire dans ABS
			if (t.contains("ABS")) {
				ApprobateurDto approAbs = getListeApprobateursABS().get(getListeApprobateursABS().indexOf(ag));
				addZone(getNOM_ST_DELEGATAIRE_ABS(i), approAbs.getDelegataire() == null ? Const.CHAINE_VIDE : approAbs
						.getDelegataire().getNom() + " " + approAbs.getDelegataire().getPrenom());
			} else {
				addZone(getNOM_ST_DELEGATAIRE_ABS(i), Const.CHAINE_VIDE);
			}
			// on cherche si l'agent a un delegataire dans PTG
			if (t.contains("PTG")) {
				ApprobateurDto approPtg = getListeApprobateursPTG().get(getListeApprobateursPTG().indexOf(ag));
				addZone(getNOM_ST_DELEGATAIRE_PTG(i), approPtg.getDelegataire() == null ? Const.CHAINE_VIDE : approPtg
						.getDelegataire().getNom() + " " + approPtg.getDelegataire().getPrenom());
			} else {
				addZone(getNOM_ST_DELEGATAIRE_PTG(i), Const.CHAINE_VIDE);
			}

			addZone(getNOM_CK_DROIT_PTG(i), t.contains("PTG") ? getCHECKED_ON() : getCHECKED_OFF());

			addZone(getNOM_CK_DROIT_ABS(i), t.contains("ABS") ? getCHECKED_ON() : getCHECKED_OFF());
		}
	}

	/**
	 * Retourne le nom de l'ecran utilise par la gestion des droits
	 * 
	 * @return String
	 */
	public String getNomEcran() {
		return "ECR-DROIT-KIOSQUE";
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_APPRO_ABS
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_APPRO_ABS())) {
				return performPB_AJOUTER_AGENT_APPRO_ABS(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_MAIRIE_APPRO_ABS
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_ABS())) {
				return performPB_AJOUTER_AGENT_MAIRIE_APPRO_ABS(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_OPE_ABS
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_OPE_ABS())) {
				return performPB_AJOUTER_AGENT_OPE_ABS(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_VISEUR_ABS
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_VISEUR_ABS())) {
				return performPB_AJOUTER_AGENT_VISEUR_ABS(request);
			}
			// GESTION DROITS DE L'APPROBATEUR
			for (int indice = 0; indice < getListeAgentsApprobateurAbs().size(); indice++) {
				int i = getListeAgentsApprobateurAbs().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT_APPRO_ABS(i))) {
					return performPB_SUPPRIMER_AGENT_APPRO_ABS(request, i);
				}
			}
			for (int indice = 0; indice < getListeAgentsOperateurAbs().size(); indice++) {
				int i = getListeAgentsOperateurAbs().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT_OPE_ABS(i))) {
					return performPB_SUPPRIMER_AGENT_OPE_ABS(request, i);
				}
				if (testerParametre(request, getNOM_PB_GERER_AGENT_OPE_APPRO_ABS(i))) {
					return performPB_GERER_AGENT_OPE_APPRO_ABS(request, i);
				}
			}
			for (int indice = 0; indice < getListeAgentsViseurAbs().size(); indice++) {
				int i = getListeAgentsViseurAbs().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT_VISEUR_ABS(i))) {
					return performPB_SUPPRIMER_AGENT_VISEUR_ABS(request, i);
				}
				if (testerParametre(request, getNOM_PB_GERER_AGENT_VISEUR_APPRO_ABS(i))) {
					return performPB_GERER_AGENT_VISEUR_APPRO_ABS(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_AGENT_APPRO_PTG
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_APPRO_PTG())) {
				return performPB_AJOUTER_AGENT_APPRO_PTG(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_MAIRIE_APPRO_PTG
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_PTG())) {
				return performPB_AJOUTER_AGENT_MAIRIE_APPRO_PTG(request);
			}
			// Si clic sur le bouton PB_AJOUTER_AGENT_OPE_PTG
			if (testerParametre(request, getNOM_PB_AJOUTER_AGENT_OPE_PTG())) {
				return performPB_AJOUTER_AGENT_OPE_PTG(request);
			}
			// GESTION DROITS DE L'APPROBATEUR
			for (int indice = 0; indice < getListeAgentsApprobateurPtg().size(); indice++) {
				int i = getListeAgentsApprobateurPtg().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT_APPRO_PTG(i))) {
					return performPB_SUPPRIMER_AGENT_APPRO_PTG(request, i);
				}
			}
			for (int indice = 0; indice < getListeAgentsOperateurPtg().size(); indice++) {
				int i = getListeAgentsOperateurPtg().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AGENT_OPE_PTG(i))) {
					return performPB_SUPPRIMER_AGENT_OPE_PTG(request, i);
				}
				if (testerParametre(request, getNOM_PB_GERER_AGENT_OPE_APPRO_PTG(i))) {
					return performPB_GERER_AGENT_OPE_APPRO_PTG(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int indice = 0; indice < getListeApprobateurs().size(); indice++) {
				int i = getListeApprobateurs().get(indice).getApprobateur().getIdAgent();
				ApprobateurDto approDto = getListeApprobateurs().get(indice);
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
				if (testerParametre(request, getNOM_PB_DUPLIQUER(i))) {
					return performPB_DUPLIQUER(request, i);
				}
				if (testerParametre(request, getNOM_PB_GERER_DROIT_ABS(i))) {
					return performPB_GERER_DROIT_ABS(request, i);
				}
				if (testerParametre(request, getNOM_PB_GERER_DROIT_PTG(i))) {
					return performPB_GERER_DROIT_PTG(request, i);
				}
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATAIRE_ABS(i))) {
					return performPB_SUPPRIMER_DELEGATAIRE_ABS(request, i);
				}
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATAIRE_PTG(i))) {
					return performPB_SUPPRIMER_DELEGATAIRE_PTG(request, i);
				}
				if (testerParametre(request, getNOM_PB_MODIFIER_DELEGATAIRE_ABS(i))) {
					return performPB_MODIFIER_DELEGATAIRE_ABS(request, i);
				}
				if (testerParametre(request, getNOM_PB_MODIFIER_DELEGATAIRE_PTG(i))) {
					return performPB_MODIFIER_DELEGATAIRE_PTG(request, i);
				}

				// Si clic sur le bouton PB_ANNULER
				if (testerParametre(request, getNOM_PB_ANNULER())) {
					return performPB_ANNULER(request);
				}
				if (testerParametre(request, getNOM_PB_SET_APPROBATEUR_PTG(i))) {
					boolean suppression = false;
					if (getVAL_CK_DROIT_PTG(i).equals(getCHECKED_OFF())) {
						suppression = true;
					}
					ReturnMessageDto res = saveApprobateurPTG(request, approDto.getApprobateur(), suppression);
					String err = Const.CHAINE_VIDE;
					for (String erreur : res.getErrors()) {
						err += " " + erreur;
					}

					if (!err.equals(Const.CHAINE_VIDE)) {
						getTransaction().declarerErreur("ERREUR : " + err);
						return false;
					} else {
						if (suppression) {
							addZone(getNOM_ST_DELEGATAIRE_PTG(i), Const.CHAINE_VIDE);
							getListeApprobateursPTG().remove(approDto);
						} else {
							getListeApprobateursPTG().add(approDto);
						}
					}
				}
				if (testerParametre(request, getNOM_PB_SET_APPROBATEUR_ABS(i))) {
					boolean suppression = false;
					if (getVAL_CK_DROIT_ABS(i).equals(getCHECKED_OFF())) {
						suppression = true;
					}
					ReturnMessageDto res = saveApprobateurABS(request, approDto.getApprobateur(), suppression);
					String err = Const.CHAINE_VIDE;
					for (String erreur : res.getErrors()) {
						err += " " + erreur;
					}

					if (!err.equals(Const.CHAINE_VIDE)) {
						getTransaction().declarerErreur("ERREUR : " + err);
						return false;
					} else {
						if (suppression) {
							addZone(getNOM_ST_DELEGATAIRE_ABS(i), Const.CHAINE_VIDE);
							getListeApprobateursABS().remove(approDto);
						} else {
							getListeApprobateursABS().add(approDto);
						}
					}
				}
			}

			// Si clic sur le bouton PB_TRI
			if (testerParametre(request, getNOM_PB_TRI())) {
				return performPB_TRI(request);
			}

			// Si clic sur le bouton PB_AFFICHER
			if (testerParametre(request, getNOM_PB_AFFICHER())) {
				return performPB_AFFICHER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_DUPLICATION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_DUPLICATION())) {
				return performPB_RECHERCHER_AGENT_DUPLICATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION
			if (testerParametre(request, getNOM_PB_VALIDER_DUPLICATION())) {
				return performPB_VALIDER_DUPLICATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeDROITSGestion. Date de création : (20/10/11
	 * 11:05:27)
	 * 
	 */
	public OeDROITSKiosque() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (20/10/11 11:05:27)
	 * 
	 * @return String
	 * 
	 */
	public String getJSP() {
		return "OeDROITSKiosque.jsp";
	}

	public ArrayList<ApprobateurDto> getListeApprobateurs() {
		return listeApprobateurs;
	}

	public void setListeApprobateurs(ArrayList<ApprobateurDto> listeApprobateurs) {
		this.listeApprobateurs = listeApprobateurs;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 * @return String
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_APPROBATEUR, true);
		setApprobateurCourant(null);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : NOM_PB_DUPLIQUER
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_PB_DUPLIQUER(int i) {
		return "NOM_PB_DUPLIQUER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param indiceEltASuprimer
	 *            indice element
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setApprobateurCourant(null);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}

		String err = Const.CHAINE_VIDE;
		if (getListeApprobateursPTG().contains(agentSelec)) {
			ReturnMessageDto messagePtg = deleteApprobateurPTG(request, agentSelec.getApprobateur());
			for (String erreur : messagePtg.getErrors()) {
				err += " " + erreur;
			}
		}
		if (getListeApprobateursABS().contains(agentSelec)) {
			Agent agentConnecte = getAgentConnecte(request);
			ReturnMessageDto messageAbs = deleteApprobateurABS(request, agentSelec.getApprobateur(),agentConnecte.getIdAgent());

			for (String erreur : messageAbs.getErrors()) {
				err += " " + erreur;
			}
		}

		if (!err.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur("ERREUR : " + err);
		}
		performPB_AFFICHER(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
	
	public boolean performPB_DUPLIQUER(HttpServletRequest request, int indiceEltADupliquer) throws Exception {
		
		addZone(getNOM_ST_ACTION(), ACTION_DUPLIQUER_DROIT_ABS_PTG);
		setApprobateurCourant(null);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltADupliquer) {
				agentSelec = ag;
				break;
			}
		}
		
		setApprobateurCourant(agentSelec.getApprobateur());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
	
	public String getNOM_PB_VALIDER_DUPLICATION() {
		return "NOM_PB_VALIDER_DUPLICATION";
	}
	
	public boolean performPB_VALIDER_DUPLICATION(HttpServletRequest request) throws Exception {
		
		if(null == getVAL_ST_AGENT_DUPLICATION()
				|| Const.CHAINE_VIDE.equals(getVAL_ST_AGENT_DUPLICATION())) {
			getTransaction().declarerErreur("ERREUR : veuillez sélectionner un agent.");
			return false;
		}
		
		AgentWithServiceDto oldApprobateur = getApprobateurCourant();
		
		// recuperation agent
		Agent agentDestinataire = null;
		agentDestinataire = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT_DUPLICATION()));
		
		if(null == agentDestinataire) {
			getTransaction().declarerErreur("ERREUR : L'agent saisi n'existe pas.");
			return false;
		}
		
		ApprobateurDto newApprobateur = new ApprobateurDto();
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(agentDestinataire.getIdAgent());

		if(getListeApprobateurs().contains(newApprobateur)) {
			getTransaction().declarerErreur("ERREUR : L'agent saisi existe déjà  en tant que approbateur.");
			return false;
		}
		
		//////////////// on duplique ///////////////////////
		ReturnMessageDto resultAbs = absService.dupliqueApprobateur(getAgentConnecte(request).getIdAgent(), oldApprobateur.getIdAgent(), agentDestinataire.getIdAgent());
		String errAbs = Const.CHAINE_VIDE;
		for (String erreur : resultAbs.getErrors()) {
			errAbs += " " + erreur;
		}
		ReturnMessageDto resultPtg = ptgService.dupliqueApprobateur(getAgentConnecte(request).getIdAgent(), oldApprobateur.getIdAgent(), agentDestinataire.getIdAgent());
		String errPtg = Const.CHAINE_VIDE;
		for (String erreur : resultPtg.getErrors()) {
			errPtg += " " + erreur;
		}

		if (!errAbs.equals(Const.CHAINE_VIDE)
				|| !errPtg.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur("ERREUR ABS : " + errAbs + "; ERREUR PTG : " + errPtg);
			return false;
		}

		setStatut(STATUT_MEME_PROCESS);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_DUPLICATION(), Const.CHAINE_VIDE);
		setApprobateurCourant(null);
		return true;
	}

	private ReturnMessageDto deleteApprobateurABS(HttpServletRequest request, AgentWithServiceDto dto,Integer idAgentConnecte) {
		return absService.deleteApprobateur(new JSONSerializer().exclude("*.class").serialize(dto),idAgentConnecte);
	}

	private ReturnMessageDto deleteApprobateurPTG(HttpServletRequest request, AgentWithServiceDto dto) {
		return ptgService.deleteApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
	}

	public String getNOM_CK_DROIT_PTG(int i) {
		return "NOM_CK_DROIT_PTG_" + i;
	}

	public String getVAL_CK_DROIT_PTG(int i) {
		return getZone(getNOM_CK_DROIT_PTG(i));
	}

	public String getNOM_CK_DROIT_ABS(int i) {
		return "NOM_CK_DROIT_ABS_" + i;
	}

	public String getVAL_CK_DROIT_ABS(int i) {
		return getZone(getNOM_CK_DROIT_ABS(i));
	}

	public Hashtable<ApprobateurDto, ArrayList<String>> getHashApprobateur() {
		if (hashApprobateur == null) {
			hashApprobateur = new Hashtable<ApprobateurDto, ArrayList<String>>();
		}
		return hashApprobateur;
	}

	public void setHashApprobateur(Hashtable<ApprobateurDto, ArrayList<String>> hashApprobateur) {
		this.hashApprobateur = hashApprobateur;
	}

	public String getNOM_PB_TRI() {
		return "NOM_PB_TRI";
	}

	public boolean performPB_TRI(HttpServletRequest request) throws Exception {
		setApprobateurCourant(null);
		if (getVAL_RG_TRI().equals(getNOM_RB_TRI_AGENT())) {
			// on tri la liste
			Collections.sort(getListeApprobateurs(), new Comparator<ApprobateurDto>() {
				@Override
				public int compare(ApprobateurDto o1, ApprobateurDto o2) {
					return o1.getApprobateur().getNom().compareTo(o2.getApprobateur().getNom());
				}

			});
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_SERVICE())) {
			// on tri la liste
			Collections.sort(getListeApprobateurs(), new Comparator<ApprobateurDto>() {
				@Override
				public int compare(ApprobateurDto o1, ApprobateurDto o2) {
					return o1.getApprobateur().getService().compareTo(o2.getApprobateur().getService());
				}

			});
		}
		return true;
	}

	public String getNOM_RG_TRI() {
		return "NOM_RG_TRI";
	}

	public String getVAL_RG_TRI() {
		return getZone(getNOM_RG_TRI());
	}

	public String getNOM_RB_TRI_AGENT() {
		return "NOM_RB_TRI_AGENT";
	}

	public String getNOM_RB_TRI_SERVICE() {
		return "NOM_RB_TRI_SERVICE";
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

	public String getNOM_ST_DELEGATAIRE_PTG(int i) {
		return "NOM_ST_DELEGATAIRE_PTG" + i;
	}

	public String getVAL_ST_DELEGATAIRE_PTG(int i) {
		return getZone(getNOM_ST_DELEGATAIRE_PTG(i));
	}

	public String getNOM_PB_MODIFIER_DELEGATAIRE_PTG(int i) {
		return "NOM_PB_MODIFIER_DELEGATAIRE_PTG" + i;
	}

	public boolean performPB_MODIFIER_DELEGATAIRE_PTG(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// redmine #14134
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_DELEGATAIRE_PTG, true);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}
		setApprobateurCourant(agentSelec.getApprobateur());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_DELEGATAIRE_PTG(int i) {
		return "NOM_PB_SUPPRIMER_DELEGATAIRE_PTG" + i;
	}

	public boolean performPB_SUPPRIMER_DELEGATAIRE_PTG(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		// redmine #14134
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}

		setApprobateurCourant(agentSelec.getApprobateur());
		saveDelegatairePtg(request, true);

		setStatut(STATUT_MEME_PROCESS);
		performPB_AFFICHER(request);
		return true;
	}

	public String getNOM_ST_DELEGATAIRE_ABS(int i) {
		return "NOM_ST_DELEGATAIRE_ABS" + i;
	}

	public String getVAL_ST_DELEGATAIRE_ABS(int i) {
		return getZone(getNOM_ST_DELEGATAIRE_ABS(i));
	}

	public String getNOM_PB_MODIFIER_DELEGATAIRE_ABS(int i) {
		return "NOM_PB_MODIFIER_DELEGATAIRE_ABS" + i;
	}

	public boolean performPB_MODIFIER_DELEGATAIRE_ABS(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// redmine #14134
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_DELEGATAIRE_ABS, true);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}
		setApprobateurCourant(agentSelec.getApprobateur());
		return true;
	}

	public String getNOM_PB_SUPPRIMER_DELEGATAIRE_ABS(int i) {
		return "NOM_PB_SUPPRIMER_DELEGATAIRE_ABS" + i;
	}

	public boolean performPB_SUPPRIMER_DELEGATAIRE_ABS(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		// redmine #14134
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}
		setApprobateurCourant(agentSelec.getApprobateur());
		saveDelegataireAbs(request, true);

		setStatut(STATUT_MEME_PROCESS);
		performPB_AFFICHER(request);
		return true;
	}

	private void saveDelegataireAbs(HttpServletRequest request, boolean suppression) throws Exception {
		Agent agentConnecte = getAgentConnecte(request);

		if (!suppression) {
			Agent ag = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (ag != null && getApprobateurCourant() != null) {
				if (ag.getIdAgent().toString().equals(getApprobateurCourant().getIdAgent().toString())) {
					getTransaction().declarerErreur("ERREUR : L'agent ne peut être délégataire de lui même.");
					return;
				}
				AgentDto agInputter = new AgentDto();
				agInputter.setIdAgent(ag.getIdAgent());
				InputterDto inputter = new InputterDto();
				inputter.setDelegataire(agInputter);
				ReturnMessageDto message = absService.setDelegataire(getApprobateurCourant().getIdAgent(),
						new JSONSerializer().exclude("*.class").serialize(inputter),agentConnecte.getIdAgent());

				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			ReturnMessageDto message = absService.setDelegataire(getApprobateurCourant().getIdAgent(),
					new JSONSerializer().exclude("*.class").serialize(new DelegatorAndOperatorsDto()),agentConnecte.getIdAgent());

			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			if (!err.equals(Const.CHAINE_VIDE))
				getTransaction().declarerErreur("ERREUR : " + err);
		}
	}

	private void saveDelegatairePtg(HttpServletRequest request, boolean suppression) {

		if (!suppression) {
			Agent ag = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (ag != null && getApprobateurCourant() != null) {
				if (ag.getIdAgent().toString().equals(getApprobateurCourant().getIdAgent().toString())) {
					getTransaction().declarerErreur("ERREUR : L'agent ne peut être délégataire de lui même.");
					return;
				}
				AgentDto agInputter = new AgentDto();
				agInputter.setIdAgent(ag.getIdAgent());
				DelegatorAndOperatorsDto inputter = new DelegatorAndOperatorsDto();
				inputter.setDelegataire(agInputter);
				ReturnMessageDto message = ptgService.setDelegataire(getApprobateurCourant().getIdAgent(),
						new JSONSerializer().exclude("*.class").serialize(inputter));

				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			ReturnMessageDto message = ptgService.setDelegataire(getApprobateurCourant().getIdAgent(),
					new JSONSerializer().exclude("*.class").serialize(new DelegatorAndOperatorsDto()));

			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			if (!err.equals(Const.CHAINE_VIDE))
				getTransaction().declarerErreur("ERREUR : " + err);
		}

	}

	public AgentWithServiceDto getApprobateurCourant() {
		return approbateurCourant;
	}

	public void setApprobateurCourant(AgentWithServiceDto approbateurCourant) {
		this.approbateurCourant = approbateurCourant;
	}

	public ArrayList<ApprobateurDto> getListeApprobateursPTG() {
		return listeApprobateursPTG;
	}

	public void setListeApprobateursPTG(ArrayList<ApprobateurDto> listeApprobateursPTG) {
		this.listeApprobateursPTG = listeApprobateursPTG;
	}

	public ArrayList<ApprobateurDto> getListeApprobateursABS() {
		return listeApprobateursABS;
	}

	public void setListeApprobateursABS(ArrayList<ApprobateurDto> listeApprobateursABS) {
		this.listeApprobateursABS = listeApprobateursABS;
	}

	public String getVAL_PB_SET_APPROBATEUR_PTG(int i) {
		return getZone(getNOM_PB_SET_APPROBATEUR_PTG(i));
	}

	public String getNOM_PB_SET_APPROBATEUR_PTG(int i) {
		return "NOM_PB_SET_APPROBATEUR_PTG_" + i;
	}

	public String getVAL_PB_SET_APPROBATEUR_ABS(int i) {
		return getZone(getNOM_PB_SET_APPROBATEUR_ABS(i));
	}

	public String getNOM_PB_SET_APPROBATEUR_ABS(int i) {
		return "NOM_PB_SET_APPROBATEUR_ABS_" + i;
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	public String getNOM_ST_AGENT_DUPLICATION() {
		return "NOM_ST_AGENT_DUPLICATION";
	}

	public String getVAL_ST_AGENT_DUPLICATION() {
		return getZone(getNOM_ST_AGENT_DUPLICATION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_DUPLICATION() {
		return "NOM_PB_RECHERCHER_AGENT_DUPLICATION";
	}

	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	public boolean performPB_RECHERCHER_AGENT_DUPLICATION(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_DUPLICATION, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DUPLICATION(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_DUPLICATION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_PB_AFFICHER() {
		return "NOM_PB_AFFICHER";
	}

	public boolean performPB_AFFICHER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (!performControlerFiltres()) {
			return false;
		}
		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}
		initialiseListeApprobateur(Const.CHAINE_VIDE.equals(getVAL_ST_ID_SERVICE_ADS()) ? null : new Integer(getVAL_ST_ID_SERVICE_ADS()), agent);
		afficheListeApprobateurs();

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean performControlerFiltres() {
		if (!getVAL_ST_AGENT().equals(Const.CHAINE_VIDE)) {
			try {
				@SuppressWarnings("unused")
				Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + getVAL_ST_AGENT()));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", getVAL_ST_AGENT()));
				return false;
			}
		}
		return true;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public boolean peutModifierDelegatairePTG(int idAgent) {

		ApprobateurDto agentSelec = new ApprobateurDto();
		AgentWithServiceDto approbateur = new AgentWithServiceDto();
		approbateur.setIdAgent(idAgent);
		agentSelec.setApprobateur(approbateur);
		if (getListeApprobateursPTG().contains(agentSelec)) {
			return true;
		}

		return false;
	}

	public boolean peutModifierDelegataireABS(int idAgent) {

		ApprobateurDto agentSelec = new ApprobateurDto();
		AgentWithServiceDto approbateur = new AgentWithServiceDto();
		approbateur.setIdAgent(idAgent);
		agentSelec.setApprobateur(approbateur);
		if (getListeApprobateursABS().contains(agentSelec)) {
			return true;
		}

		return false;
	}

	public String getNOM_PB_GERER_DROIT_ABS(int i) {
		return "NOM_PB_GERER_DROIT_ABS" + i;
	}

	public boolean performPB_GERER_DROIT_ABS(HttpServletRequest request, int indiceEltAGerer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setApprobateurCourant(null);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltAGerer) {
				agentSelec = ag;
				break;
			}
		}
		setApprobateurCourant(agentSelec.getApprobateur());

		// on recupère les agents de l'approbateurs
		ArrayList<AgentDto> resultAgeAppro = (ArrayList<AgentDto>) absService
				.getAgentsApprobateur(getApprobateurCourant().getIdAgent());
		setListeAgentsApprobateurAbs(resultAgeAppro);
		// on tri la liste
		Collections.sort(getListeAgentsApprobateurAbs(), new Comparator<AgentDto>() {
			@Override
			public int compare(AgentDto o1, AgentDto o2) {
				return o1.getNom().compareTo(o2.getNom());
			}

		});
		afficherListeAgentsApprobateurAbs();

		// on recupere les opérateurs de l'approbateur
		InputterDto resultOperateurs = absService.getOperateursDelegataireApprobateur(getApprobateurCourant()
				.getIdAgent());
		setListeAgentsOperateurAbs((ArrayList<AgentDto>) resultOperateurs.getOperateurs());
		// on tri la liste
		Collections.sort(getListeAgentsOperateurAbs(), new Comparator<AgentDto>() {
			@Override
			public int compare(AgentDto o1, AgentDto o2) {
				return o1.getNom().compareTo(o2.getNom());
			}

		});
		afficherListeAgentsOperateurAbs();

		// on recupere les viseurs de l'approbateur
		ViseursDto resultViseurs = absService.getViseursApprobateur(getApprobateurCourant().getIdAgent());
		setListeAgentsViseurAbs((ArrayList<AgentDto>) resultViseurs.getViseurs());
		// on tri la liste
		Collections.sort(getListeAgentsViseurAbs(), new Comparator<AgentDto>() {
			@Override
			public int compare(AgentDto o1, AgentDto o2) {
				return o1.getNom().compareTo(o2.getNom());
			}

		});
		afficherListeAgentsViseurAbs();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_GERER_DROIT_ABS);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_GERER_DROIT_PTG(int i) {
		return "NOM_PB_GERER_DROIT_PTG" + i;
	}

	public boolean performPB_GERER_DROIT_PTG(HttpServletRequest request, int indiceEltAGerer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setApprobateurCourant(null);

		ApprobateurDto agentSelec = new ApprobateurDto();

		Enumeration<ApprobateurDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			ApprobateurDto ag = e.nextElement();
			int i = ag.getApprobateur().getIdAgent();

			if (i == indiceEltAGerer) {
				agentSelec = ag;
				break;
			}
		}
		setApprobateurCourant(agentSelec.getApprobateur());

		// on recupère les agents de l'approbateurs
		ArrayList<AgentDto> resultAgeAppro = (ArrayList<AgentDto>) ptgService.getApprovedAgents(getApprobateurCourant()
				.getIdAgent());
		setListeAgentsApprobateurPtg(resultAgeAppro);
		// on tri la liste
		Collections.sort(getListeAgentsApprobateurPtg(), new Comparator<AgentDto>() {
			@Override
			public int compare(AgentDto o1, AgentDto o2) {
				return o1.getNom().compareTo(o2.getNom());
			}

		});
		afficherListeAgentsApprobateurPtg();

		// on recupere les opérateurs de l'approbateur
		DelegatorAndOperatorsDto resultOperateurs = ptgService.getDelegateAndOperator(getApprobateurCourant()
				.getIdAgent());
		setListeAgentsOperateurPtg((ArrayList<AgentDto>) resultOperateurs.getSaisisseurs());
		// on tri la liste
		Collections.sort(getListeAgentsOperateurPtg(), new Comparator<AgentDto>() {
			@Override
			public int compare(AgentDto o1, AgentDto o2) {
				return o1.getNom().compareTo(o2.getNom());
			}

		});
		afficherListeAgentsOperateurPtg();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_GERER_DROIT_PTG);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setApprobateurCourant(null);
		return true;
	}

	public String getNOM_ST_AGENT_OPE(int i) {
		return "NOM_ST_AGENT_OPE" + i;
	}

	public String getVAL_ST_AGENT_OPE(int i) {
		return getZone(getNOM_ST_AGENT_OPE(i));
	}

	public String getNOM_ST_AGENT_VISEUR(int i) {
		return "NOM_ST_AGENT_VISEUR" + i;
	}

	public String getVAL_ST_AGENT_VISEUR(int i) {
		return getZone(getNOM_ST_AGENT_VISEUR(i));
	}

	public String getNOM_ST_AGENT_APPRO(int i) {
		return "NOM_ST_AGENT_APPRO" + i;
	}

	public String getVAL_ST_AGENT_APPRO(int i) {
		return getZone(getNOM_ST_AGENT_APPRO(i));
	}

	private void afficherListeAgentsApprobateurPtg() throws Exception {
		for (AgentDto agAppro : getListeAgentsApprobateurPtg()) {
			Integer i = agAppro.getIdAgent();
			addZone(getNOM_ST_AGENT_APPRO(i), agAppro.getNom() + " " + agAppro.getPrenom() + " ("
					+ agAppro.getIdAgent().toString().substring(3, agAppro.getIdAgent().toString().length()) + ")");
		}
	}

	private void afficherListeAgentsOperateurPtg() throws Exception {
		for (AgentDto agAppro : getListeAgentsOperateurPtg()) {
			Integer i = agAppro.getIdAgent();
			addZone(getNOM_ST_AGENT_OPE(i), agAppro.getNom() + " " + agAppro.getPrenom() + " ("
					+ agAppro.getIdAgent().toString().substring(3, agAppro.getIdAgent().toString().length()) + ")");
		}
	}

	private void afficherListeAgentsApprobateurAbs() throws Exception {
		for (AgentDto agAppro : getListeAgentsApprobateurAbs()) {
			Integer i = agAppro.getIdAgent();
			addZone(getNOM_ST_AGENT_APPRO(i), agAppro.getNom() + " " + agAppro.getPrenom() + " ("
					+ agAppro.getIdAgent().toString().substring(3, agAppro.getIdAgent().toString().length()) + ")");
		}
	}

	private void afficherListeAgentsOperateurAbs() throws Exception {
		for (AgentDto agAppro : getListeAgentsOperateurAbs()) {
			Integer i = agAppro.getIdAgent();
			addZone(getNOM_ST_AGENT_OPE(i), agAppro.getNom() + " " + agAppro.getPrenom() + " ("
					+ agAppro.getIdAgent().toString().substring(3, agAppro.getIdAgent().toString().length()) + ")");
		}
	}

	private void afficherListeAgentsViseurAbs() throws Exception {
		for (AgentDto agAppro : getListeAgentsViseurAbs()) {
			Integer i = agAppro.getIdAgent();
			addZone(getNOM_ST_AGENT_VISEUR(i), agAppro.getNom() + " " + agAppro.getPrenom() + " ("
					+ agAppro.getIdAgent().toString().substring(3, agAppro.getIdAgent().toString().length()) + ")");
		}
	}

	public String getNOM_PB_AJOUTER_AGENT_APPRO_ABS() {
		return "NOM_PB_AJOUTER_AGENT_APPRO_ABS";
	}

	public boolean performPB_AJOUTER_AGENT_APPRO_ABS(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, getListeAgentsApprobateurAbs());
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		setStatut(STATUT_AGENT_APPROBATEUR_ABS, true);
		return true;

	}

	public String getNOM_PB_AJOUTER_AGENT_OPE_ABS() {
		return "NOM_PB_AJOUTER_AGENT_OPE_ABS";
	}

	public boolean performPB_AJOUTER_AGENT_OPE_ABS(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_OPE_APPROBATEUR_ABS, true);
		return true;

	}

	public String getNOM_PB_AJOUTER_AGENT_VISEUR_ABS() {
		return "NOM_PB_AJOUTER_AGENT_VISEUR_ABS";
	}

	public boolean performPB_AJOUTER_AGENT_VISEUR_ABS(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, getListeAgentsViseurAbs());
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		setStatut(STATUT_VISEUR_APPROBATEUR_ABS, true);
		return true;

	}

	public String getNOM_PB_SUPPRIMER_AGENT_APPRO_ABS(int i) {
		return "NOM_PB_SUPPRIMER_AGENT_APPRO_ABS" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT_APPRO_ABS(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		AgentDto agToDelete = new AgentDto();
		agToDelete.setIdAgent(indiceEltASuprimer);

		saveAgentApprobateurAbs(request, Arrays.asList(agToDelete), true, false);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_AGENT_OPE_ABS(int i) {
		return "NOM_PB_SUPPRIMER_AGENT_OPE_ABS" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT_OPE_ABS(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		Agent agToDelete = new Agent();
		agToDelete.setIdAgent(indiceEltASuprimer);
		saveOperateurApprobateurAbs(request, agToDelete, true);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_AGENT_VISEUR_ABS(int i) {
		return "NOM_PB_SUPPRIMER_AGENT_VISEUR" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT_VISEUR_ABS(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		AgentDto agToDelete = new AgentDto();
		agToDelete.setIdAgent(indiceEltASuprimer);
		saveViseurApprobateurAbs(request, Arrays.asList(agToDelete), true);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<AgentDto> getListeAgentsApprobateurAbs() {
		return listeAgentsApprobateurAbs;
	}

	public void setListeAgentsApprobateurAbs(ArrayList<AgentDto> listeAgentsApprobateurAbs) {
		this.listeAgentsApprobateurAbs = listeAgentsApprobateurAbs;
	}

	public ArrayList<AgentDto> getListeAgentsOperateurAbs() {
		return listeAgentsOperateurAbs;
	}

	public void setListeAgentsOperateurAbs(ArrayList<AgentDto> listeAgentsOperateurAbs) {
		this.listeAgentsOperateurAbs = listeAgentsOperateurAbs;
	}

	public ArrayList<AgentDto> getListeAgentsViseurAbs() {
		return listeAgentsViseurAbs;
	}

	public void setListeAgentsViseurAbs(ArrayList<AgentDto> listeAgentsViseurAbs) {
		this.listeAgentsViseurAbs = listeAgentsViseurAbs;
	}

	public ArrayList<AgentDto> getListeAgentsApprobateurPtg() {
		return listeAgentsApprobateurPtg;
	}

	public void setListeAgentsApprobateurPtg(ArrayList<AgentDto> listeAgentsApprobateurPtg) {
		this.listeAgentsApprobateurPtg = listeAgentsApprobateurPtg;
	}

	public ArrayList<AgentDto> getListeAgentsOperateurPtg() {
		return listeAgentsOperateurPtg;
	}

	public void setListeAgentsOperateurPtg(ArrayList<AgentDto> listeAgentsOperateurPtg) {
		this.listeAgentsOperateurPtg = listeAgentsOperateurPtg;
	}

	public String getNOM_PB_AJOUTER_AGENT_APPRO_PTG() {
		return "NOM_PB_AJOUTER_AGENT_APPRO_PTG";
	}

	public boolean performPB_AJOUTER_AGENT_APPRO_PTG(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, getListeAgentsApprobateurPtg());
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		setStatut(STATUT_AGENT_APPROBATEUR_PTG, true);
		return true;
	}

	public String getNOM_PB_AJOUTER_AGENT_OPE_PTG() {
		return "NOM_PB_AJOUTER_AGENT_OPE_PTG";
	}

	public boolean performPB_AJOUTER_AGENT_OPE_PTG(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_OPE_APPROBATEUR_PTG, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_AGENT_APPRO_PTG(int i) {
		return "NOM_PB_SUPPRIMER_AGENT_APPRO_PTG" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT_APPRO_PTG(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		AgentDto agToDelete = new AgentDto();
		agToDelete.setIdAgent(indiceEltASuprimer);

		saveAgentApprobateurPtg(request, Arrays.asList(agToDelete), true, false);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_AGENT_OPE_PTG(int i) {
		return "NOM_PB_SUPPRIMER_AGENT_OPE_PTG" + i;
	}

	public boolean performPB_SUPPRIMER_AGENT_OPE_PTG(HttpServletRequest request, int indiceEltASuprimer)
			throws Exception {
		Agent agToDelete = new Agent();
		agToDelete.setIdAgent(indiceEltASuprimer);
		saveOperateurApprobateurPtg(request, agToDelete, true);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void saveAgentApprobateurPtg(HttpServletRequest request, List<AgentDto> listAgtAAjouter, boolean suppression, boolean fromRechercheAbreServiceWithAgents)
			throws Exception {

		if (!suppression) {
			if (getApprobateurCourant() != null) {
				
				if(fromRechercheAbreServiceWithAgents)
					getListeAgentsApprobateurPtg().clear();
				
				for (AgentDto ajout : listAgtAAjouter) {
					if (!getListeAgentsApprobateurPtg().contains(ajout))
						getListeAgentsApprobateurPtg().add(ajout);
				}

				ReturnMessageDto result = ptgService.saveApprovedAgents(getApprobateurCourant().getIdAgent(),
						getListeAgentsApprobateurPtg());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			if (getApprobateurCourant() != null) {
				for (AgentDto ajout : listAgtAAjouter) {
					if (getListeAgentsApprobateurPtg().contains(ajout))
						getListeAgentsApprobateurPtg().remove(ajout);
				}

				ReturnMessageDto result = ptgService.saveApprovedAgents(getApprobateurCourant().getIdAgent(),
						getListeAgentsApprobateurPtg());

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		// on rafraichi les données
		performPB_GERER_DROIT_PTG(request, getApprobateurCourant().getIdAgent());

	}

	private void saveOperateurApprobateurPtg(HttpServletRequest request, Agent operateur, boolean suppression)
			throws Exception {

		if (operateur == null) {
			return;
		}

		if (operateur.getIdAgent().toString().equals(getApprobateurCourant().getIdAgent().toString())) {
			getTransaction().declarerErreur("ERREUR : L'agent ne peut être opérateur de lui même.");
			return;
		}

		if (!suppression) {
			if (getApprobateurCourant() != null) {
				if (operateur != null) {
					AgentDto ajout = new AgentDto();
					ajout.setIdAgent(operateur.getIdAgent());
					if (!getListeAgentsOperateurPtg().contains(ajout))
						getListeAgentsOperateurPtg().add(ajout);
				}

				DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
				dto.setSaisisseurs(getListeAgentsOperateurPtg());
				// on recupere le delagataire existant
				ArrayList<ApprobateurDto> approExistant = (ArrayList<ApprobateurDto>) ptgService.getApprobateurs(null,
						getApprobateurCourant().getIdAgent());
				dto.setDelegataire(approExistant.get(0).getDelegataire());
				ReturnMessageDto result = ptgService.saveDelegateAndOperator(getApprobateurCourant().getIdAgent(), dto);

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			if (getApprobateurCourant() != null) {
				if (operateur != null) {
					AgentDto ajout = new AgentDto();
					ajout.setIdAgent(operateur.getIdAgent());
					if (getListeAgentsOperateurPtg().contains(ajout))
						getListeAgentsOperateurPtg().remove(ajout);
				}

				DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
				dto.setSaisisseurs(getListeAgentsOperateurPtg());
				// on recupere le delagataire existant
				ArrayList<ApprobateurDto> approExistant = (ArrayList<ApprobateurDto>) ptgService.getApprobateurs(null,
						getApprobateurCourant().getIdAgent());
				dto.setDelegataire(approExistant.get(0).getDelegataire());
				ReturnMessageDto result = ptgService.saveDelegateAndOperator(getApprobateurCourant().getIdAgent(), dto);

				String err = Const.CHAINE_VIDE;
				for (String erreur : result.getErrors()) {
					err += " " + erreur;
				}
				if (!err.equals(Const.CHAINE_VIDE))
					getTransaction().declarerErreur("ERREUR : " + err);

			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		// on rafraichi les données
		performPB_GERER_DROIT_PTG(request, getApprobateurCourant().getIdAgent());
	}

	public String getNOM_PB_GERER_AGENT_OPE_APPRO_PTG(int i) {
		return "NOM_PB_GERER_AGENT_OPE_APPRO_PTG" + i;
	}

	public boolean performPB_GERER_AGENT_OPE_APPRO_PTG(HttpServletRequest request, int indiceEltAGerer)
			throws Exception {
		// on recupere l'operateur
		AgentDto ope = new AgentDto();
		ope.setIdAgent(indiceEltAGerer);
		setOperateurCourant(getListeAgentsOperateurPtg().get(getListeAgentsOperateurPtg().indexOf(ope)));

		// on a besoin de l approbateur pour recuperer l arbre des services et agents associe a celui-ci
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		// on a besoin des agents deja affectes a l operateur
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, ptgService
				.getAgentsSaisisOperateur(getApprobateurCourant().getIdAgent(), getOperateurCourant().getIdAgent()));
		// et des agents affectes a l approbateur
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST, getListeAgentsApprobateurPtg());
		setStatut(STATUT_AGENT_OPE_APPROBATEUR_PTG, true);
		return true;
	}

	public AgentDto getOperateurCourant() {
		return operateurCourant;
	}

	public void setOperateurCourant(AgentDto operateurCourant) {
		this.operateurCourant = operateurCourant;
	}

	public String getNOM_PB_GERER_AGENT_OPE_APPRO_ABS(int i) {
		return "NOM_PB_GERER_AGENT_OPE_APPRO_ABS" + i;
	}

	public boolean performPB_GERER_AGENT_OPE_APPRO_ABS(HttpServletRequest request, int indiceEltAGerer)
			throws Exception {
		// on recupere l'operateur
		AgentDto ope = new AgentDto();
		ope.setIdAgent(indiceEltAGerer);
		setOperateurCourant(getListeAgentsOperateurAbs().get(getListeAgentsOperateurAbs().indexOf(ope)));
		// on a besoin de l approbateur pour recuperer l arbre des services et agents associe a celui-ci
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		// on a besoin des agents deja affectes a l operateur
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, absService.getAgentsOperateur(
				getApprobateurCourant().getIdAgent(), getOperateurCourant().getIdAgent()));
		// et des agents affectes a l approbateur
		VariablesActivite
				.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST, getListeAgentsApprobateurAbs());
		setStatut(STATUT_AGENT_OPE_APPROBATEUR_ABS, true);
		
		return true;
	}

	public String getNOM_PB_GERER_AGENT_VISEUR_APPRO_ABS(int i) {
		return "NOM_PB_GERER_AGENT_VISEUR_APPRO_ABS" + i;
	}

	public boolean performPB_GERER_AGENT_VISEUR_APPRO_ABS(HttpServletRequest request, int indiceEltAGerer)
			throws Exception {
		// on recupere l'operateur
		AgentDto ope = new AgentDto();
		ope.setIdAgent(indiceEltAGerer);
		setViseurCourant(getListeAgentsViseurAbs().get(getListeAgentsViseurAbs().indexOf(ope)));

		// on a besoin de l approbateur pour recuperer l arbre des services et agents associe a celui-ci
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getApprobateurCourant());
		// on a besoin des agents deja affectes a l operateur
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT, 
				absService.getAgentsViseur(getApprobateurCourant().getIdAgent(), getViseurCourant().getIdAgent()));
		// et des agents affectes a l approbateur
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE_DROIT_EXIST, getListeAgentsApprobateurAbs());
		setStatut(STATUT_AGENT_VISEUR_APPROBATEUR_ABS, true);
		return true;
	}

	public AgentDto getViseurCourant() {
		return viseurCourant;
	}

	public void setViseurCourant(AgentDto viseurCourant) {
		this.viseurCourant = viseurCourant;
	}

	public String getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_ABS() {
		return "NOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_ABS";
	}

	public boolean performPB_AJOUTER_AGENT_MAIRIE_APPRO_ABS(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_AGENT_MAIRIE_APPROBATEUR_ABS, true);
		return true;
	}

	public String getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_PTG() {
		return "NOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_PTG";
	}

	public boolean performPB_AJOUTER_AGENT_MAIRIE_APPRO_PTG(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_AGENT_MAIRIE_APPROBATEUR_PTG, true);
		return true;
	}

	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(
				null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
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
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}
}
