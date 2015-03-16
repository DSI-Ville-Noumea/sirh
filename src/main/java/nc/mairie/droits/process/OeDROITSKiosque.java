package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.InputterDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

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

	public static final int STATUT_APPROBATEUR = 1;
	public static final int STATUT_DELEGATAIRE_ABS = 2;
	public static final int STATUT_DELEGATAIRE_PTG = 3;
	public String ACTION_CREATION = "Création d'un approbateur.";

	private ArrayList<ApprobateurDto> listeApprobateurs = new ArrayList<ApprobateurDto>();
	private ArrayList<ApprobateurDto> listeApprobateursPTG = new ArrayList<ApprobateurDto>();
	private ArrayList<ApprobateurDto> listeApprobateursABS = new ArrayList<ApprobateurDto>();
	private Hashtable<ApprobateurDto, ArrayList<String>> hashApprobateur;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentWithServiceDto approbateurCourant;

	public String focus = null;
	private boolean first = true;

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
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		if (etatStatut() == STATUT_APPROBATEUR) {
			saveAjoutApprobateurs(request);
		}

		if (etatStatut() == STATUT_DELEGATAIRE_PTG) {
			saveDelegatairePtg(request, false);
		}

		if (etatStatut() == STATUT_DELEGATAIRE_ABS) {
			saveDelegataireAbs(request, false);
		}
		if (isFirst()) {
			initialiseListeApprobateur();
			addZone(getNOM_RG_TRI(), getNOM_RB_TRI_AGENT());
			setFirst(false);
		}

		// on recupere les approbateurs de ABS
		afficheListeApprobateurs();

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
	}

	private void initialiseListeApprobateur() {

		SirhPtgWSConsumer ptgConsumer = new SirhPtgWSConsumer();
		SirhAbsWSConsumer absConsumer = new SirhAbsWSConsumer();
		// on construit la hashTable des approbateurs
		getHashApprobateur().clear();
		// on recupere les approbateurs de PTG
		ArrayList<ApprobateurDto> listeApproPTG = (ArrayList<ApprobateurDto>) ptgConsumer.getApprobateurs();
		setListeApprobateursPTG(listeApproPTG);
		ArrayList<ApprobateurDto> listeApproABS = (ArrayList<ApprobateurDto>) absConsumer.getApprobateurs();
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
					Service serv = Service.chercherService(getTransaction(), fpCourante.getIdServi());
					agentDto.setCodeService(fpCourante.getIdServi());
					agentDto.setService(serv.getLibService());
				} catch (Exception e) {
					// l'agent n'est pas affecté on ne peut donc pas avoir son
					// service
					agentDto.setCodeService("NA");
					agentDto.setService("non affecté");
				}

				agentDto.setNom(ag.getNomAgent());
				agentDto.setPrenom(ag.getPrenomAgent());
				approDto.setApprobateur(agentDto);

				ArrayList<String> values = new ArrayList<>();
				values.add("PTG");
				values.add("ABS");
				getHashApprobateur().put(approDto, values);
				getListeApprobateurs().add(approDto);
				getListeApprobateursABS().add(approDto);
				getListeApprobateursPTG().add(approDto);
				ReturnMessageDto messagePtg = saveApprobateurPTG(request, approDto.getApprobateur());
				ReturnMessageDto messageAbs = saveApprobateurABS(request, approDto.getApprobateur());

				String err = Const.CHAINE_VIDE;
				if (messagePtg.getErrors().size() > 0) {
					for (String erreur : messagePtg.getErrors()) {
						err += " " + erreur;
					}
				}
				if (messageAbs.getErrors().size() > 0) {
					for (String erreur : messageAbs.getErrors()) {
						err += " " + erreur;
					}
				}
				if (err != Const.CHAINE_VIDE) {
					getTransaction().declarerErreur("ERREUR : " + err);
				}
			}
			performPB_ANNULER(request);
		}
	}

	private ReturnMessageDto saveApprobateurABS(HttpServletRequest request, AgentWithServiceDto dto) {
		return new SirhAbsWSConsumer().setApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
	}

	private ReturnMessageDto saveApprobateurPTG(HttpServletRequest request, AgentWithServiceDto dto) {
		return new SirhPtgWSConsumer().setApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
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
					+ ag.getApprobateur().getCodeService() + ")");
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
	 * Retourne le nom de l'ecran utilisé par la gestion des droits
	 * 
	 * @return String
	 */
	public String getNomEcran() {
		return "ECR-DROIT-KIOSQUE";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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

			// Si clic sur le bouton PB_SUPPRIMER
			for (int indice = 0; indice < getListeApprobateurs().size(); indice++) {
				int i = getListeApprobateurs().get(indice).getApprobateur().getIdAgent();
				AgentWithServiceDto agDto = getListeApprobateurs().get(indice).getApprobateur();
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
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
				if (testerParametre(request, getNOM_PB_SET_APPROBATEUR_PTG(i))) {
					ReturnMessageDto res = saveApprobateurPTG(request, agDto);
					String err = Const.CHAINE_VIDE;
					if (res.getErrors().size() > 0) {
						for (String erreur : res.getErrors()) {
							err += " " + erreur;
						}
					}
					if (err != Const.CHAINE_VIDE) {
						getTransaction().declarerErreur("ERREUR : " + err);
						return false;
					}
					performPB_ANNULER(request);
				}
				if (testerParametre(request, getNOM_PB_SET_APPROBATEUR_ABS(i))) {
					ReturnMessageDto res = saveApprobateurABS(request, agDto);
					String err = Const.CHAINE_VIDE;
					if (res.getErrors().size() > 0) {
						for (String erreur : res.getErrors()) {
							err += " " + erreur;
						}
					}
					if (err != Const.CHAINE_VIDE) {
						getTransaction().declarerErreur("ERREUR : " + err);
						return false;
					}
					performPB_ANNULER(request);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_TRI
			if (testerParametre(request, getNOM_PB_TRI())) {
				return performPB_TRI(request);
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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

		getHashApprobateur().remove(agentSelec);
		getListeApprobateurs().remove(agentSelec);
		getListeApprobateursPTG().remove(agentSelec);
		getListeApprobateursABS().remove(agentSelec);

		ReturnMessageDto messagePtg = deleteApprobateurPTG(request, agentSelec.getApprobateur());
		ReturnMessageDto messageAbs = deleteApprobateurABS(request, agentSelec.getApprobateur());

		String err = Const.CHAINE_VIDE;
		if (messagePtg.getErrors().size() > 0) {
			for (String erreur : messagePtg.getErrors()) {
				err += " " + erreur;
			}
		}
		if (messageAbs.getErrors().size() > 0) {
			for (String erreur : messageAbs.getErrors()) {
				err += " " + erreur;
			}
		}
		if (err != Const.CHAINE_VIDE) {

			getTransaction().declarerErreur("ERREUR : " + err);
		}

		performPB_ANNULER(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private ReturnMessageDto deleteApprobateurABS(HttpServletRequest request, AgentWithServiceDto dto) {
		return new SirhAbsWSConsumer().deleteApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
	}

	private ReturnMessageDto deleteApprobateurPTG(HttpServletRequest request, AgentWithServiceDto dto) {
		return new SirhPtgWSConsumer().deleteApprobateur(new JSONSerializer().exclude("*.class").serialize(dto));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setFirst(true);
		setApprobateurCourant(null);

		setStatut(STATUT_MEME_PROCESS);
		return true;
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

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
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
		addZone(getNOM_ST_DELEGATAIRE_PTG(agentSelec.getApprobateur().getIdAgent()), Const.CHAINE_VIDE);
		saveDelegatairePtg(request, true);

		setStatut(STATUT_MEME_PROCESS);
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
		addZone(getNOM_ST_DELEGATAIRE_ABS(agentSelec.getApprobateur().getIdAgent()), Const.CHAINE_VIDE);
		saveDelegataireAbs(request, true);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void saveDelegataireAbs(HttpServletRequest request, boolean suppression) {

		if (!suppression) {
			Agent ag = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (ag != null && getApprobateurCourant() != null) {
				AgentDto agInputter = new AgentDto();
				agInputter.setIdAgent(ag.getIdAgent());
				InputterDto inputter = new InputterDto();
				inputter.setDelegataire(agInputter);
				ReturnMessageDto message = new SirhAbsWSConsumer().setDelegataire(getApprobateurCourant().getIdAgent(),
						new JSONSerializer().exclude("*.class").serialize(inputter));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
				}
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			ReturnMessageDto message = new SirhAbsWSConsumer().setDelegataire(getApprobateurCourant().getIdAgent(),
					new JSONSerializer().exclude("*.class").serialize(new DelegatorAndOperatorsDto()));

			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
			}
		}
	}

	private void saveDelegatairePtg(HttpServletRequest request, boolean suppression) {

		if (!suppression) {
			Agent ag = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (ag != null && getApprobateurCourant() != null) {
				AgentDto agInputter = new AgentDto();
				agInputter.setIdAgent(ag.getIdAgent());
				DelegatorAndOperatorsDto inputter = new DelegatorAndOperatorsDto();
				inputter.setDelegataire(agInputter);
				ReturnMessageDto message = new SirhPtgWSConsumer().setDelegataire(getApprobateurCourant().getIdAgent(),
						new JSONSerializer().exclude("*.class").serialize(inputter));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
				}
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			ReturnMessageDto message = new SirhPtgWSConsumer().setDelegataire(getApprobateurCourant().getIdAgent(),
					new JSONSerializer().exclude("*.class").serialize(new DelegatorAndOperatorsDto()));

			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
			}
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
}
