package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
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
	public String ACTION_CREATION = "Création d'un approbateur.";
	public String ACTION_SUPPRESSION = "Suppression d'un approbateur.";

	private ArrayList<AgentWithServiceDto> listeApprobateurs = new ArrayList<AgentWithServiceDto>();
	private Hashtable<AgentWithServiceDto, ArrayList<String>> hashApprobateur;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;

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
		return "";
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
			ajouteApprobateurs(request);
		}
		if (isFirst()) {
			initialiseListeApprobateur();
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
		ArrayList<AgentWithServiceDto> listeApproPTG = (ArrayList<AgentWithServiceDto>) ptgConsumer.getApprobateurs();
		ArrayList<AgentWithServiceDto> listeApproABS = (ArrayList<AgentWithServiceDto>) absConsumer.getApprobateurs();
		ArrayList<AgentWithServiceDto> listeComplete = new ArrayList<AgentWithServiceDto>();
		for (AgentWithServiceDto agDto : listeApproPTG) {
			if (!listeComplete.contains(agDto)) {
				listeComplete.add(agDto);
			}
		}
		for (AgentWithServiceDto agDto : listeApproABS) {
			if (!listeComplete.contains(agDto)) {
				listeComplete.add(agDto);
			}
		}
		for (AgentWithServiceDto agDto : listeComplete) {
			ArrayList<String> issuDe = new ArrayList<>();
			if (listeApproPTG.contains(agDto)) {
				issuDe.add("PTG");
			}
			if (listeApproABS.contains(agDto)) {
				issuDe.add("ABS");
			}
			getHashApprobateur().put(agDto, issuDe);
		}
		setListeApprobateurs(listeComplete);
	}

	private void ajouteApprobateurs(HttpServletRequest request) throws Exception {

		@SuppressWarnings("unchecked")
		ArrayList<AgentNW> listeEvaluateurSelect = (ArrayList<AgentNW>) VariablesActivite.recuperer(this,
				"APPROBATEURS");
		VariablesActivite.enlever(this, "APPROBATEURS");
		if (listeEvaluateurSelect != null && listeEvaluateurSelect.size() > 0) {
			for (AgentNW ag : listeEvaluateurSelect) {
				AgentWithServiceDto agentDto = new AgentWithServiceDto();
				agentDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));

				if (!getListeApprobateurs().contains(agentDto)) {
					Affectation affCourante = null;
					try {
						affCourante = getAffectationDao().chercherAffectationActiveAvecAgent(
								Integer.valueOf(ag.getIdAgent()));
					} catch (Exception e) {
						// "ERR400", //
						// "L'agent @ n'est affecté à aucun poste. Il ne peut être ajouté en tant qu'approbateur."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR400", ag.getIdAgent()));
						throw new Exception();
					}
					FichePoste fpCourante = getFichePosteDao().chercherFichePoste(affCourante.getIdFichePoste());
					Service serv = Service.chercherService(getTransaction(), fpCourante.getIdServi());

					agentDto.setNom(ag.getNomAgent());
					agentDto.setPrenom(ag.getPrenomAgent());
					agentDto.setCodeService(fpCourante.getIdServi());
					agentDto.setService(serv.getLibService());

					ArrayList<String> values = new ArrayList<>();
					values.add("PTG");
					values.add("ABS");
					getHashApprobateur().put(agentDto, values);
					getListeApprobateurs().add(agentDto);
				}
			}
		}
	}

	private void afficheListeApprobateurs() throws Exception {
		Enumeration<AgentWithServiceDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			AgentWithServiceDto ag = e.nextElement();
			ArrayList<String> t = getHashApprobateur().get(ag);
			int i = ag.getIdAgent();
			addZone(getNOM_ST_AGENT(i), ag.getNom() + " " + ag.getPrenom() + " ("
					+ ag.getIdAgent().toString().substring(3, ag.getIdAgent().toString().length()) + ")");
			addZone(getNOM_ST_SERVICE(i), ag.getService() + " (" + ag.getCodeService() + ")");

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
				int i = getListeApprobateurs().get(indice).getIdAgent();
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
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

	public ArrayList<AgentWithServiceDto> getListeApprobateurs() {
		return listeApprobateurs;
	}

	public void setListeApprobateurs(ArrayList<AgentWithServiceDto> listeApprobateurs) {
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
		setStatut(STATUT_APPROBATEUR, true);
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

		AgentWithServiceDto agentSelec = new AgentWithServiceDto();

		Enumeration<AgentWithServiceDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			AgentWithServiceDto ag = e.nextElement();
			int i = ag.getIdAgent();

			if (i == indiceEltASuprimer) {
				agentSelec = ag;
				break;
			}
		}

		getHashApprobateur().remove(agentSelec);
		getListeApprobateurs().remove(agentSelec);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
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
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		SirhPtgWSConsumer ptgConsumer = new SirhPtgWSConsumer();
		SirhAbsWSConsumer absConsumer = new SirhAbsWSConsumer();
		List<AgentWithServiceDto> listeApprobateurPTG = new ArrayList<AgentWithServiceDto>();
		List<AgentWithServiceDto> listeApprobateurABS = new ArrayList<AgentWithServiceDto>();

		Enumeration<AgentWithServiceDto> e = getHashApprobateur().keys();
		while (e.hasMoreElements()) {
			AgentWithServiceDto ag = e.nextElement();
			int i = ag.getIdAgent();

			if (getVAL_CK_DROIT_PTG(i).equals(getCHECKED_ON())) {
				listeApprobateurPTG.add(ag);
			}
			if (getVAL_CK_DROIT_ABS(i).equals(getCHECKED_ON())) {
				listeApprobateurABS.add(ag);
			}
		}

		List<AgentWithServiceDto> listeAgentErreurPTG = ptgConsumer.setApprobateurs(new JSONSerializer().exclude(
				"*.class").serialize(listeApprobateurPTG));
		List<AgentWithServiceDto> listeAgentErreurABS = absConsumer.setApprobateurs(new JSONSerializer().exclude(
				"*.class").serialize(listeApprobateurABS));
		List<AgentWithServiceDto> listeAgentErreur = new ArrayList<AgentWithServiceDto>();
		listeAgentErreur.addAll(listeAgentErreurPTG);
		listeAgentErreur.addAll(listeAgentErreurABS);

		if (listeAgentErreur.size() > 0) {
			String agents = Const.CHAINE_VIDE;
			for (AgentWithServiceDto agentDtoErreur : listeAgentErreur) {
				agents += " - " + agentDtoErreur.getNom() + " " + agentDtoErreur.getPrenom();
			}
			// "INF600",
			// "Les agents suivants n'ont pu être ajouté en tant qu'approbateurs car ils sont dejà opérateurs/viseurs : @"
			getTransaction().declarerErreur(MessageUtils.getMessage("INF600", agents));
			return false;
		}
		setFirst(true);
		return true;
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

	public Hashtable<AgentWithServiceDto, ArrayList<String>> getHashApprobateur() {
		if (hashApprobateur == null) {
			hashApprobateur = new Hashtable<AgentWithServiceDto, ArrayList<String>>();
		}
		return hashApprobateur;
	}

	public void setHashApprobateur(Hashtable<AgentWithServiceDto, ArrayList<String>> hashApprobateur) {
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
		if (getVAL_RG_TRI().equals(getNOM_RB_TRI_AGENT())) {
			// on tri la liste
			Collections.sort(getListeApprobateurs(), new Comparator<AgentWithServiceDto>() {
				@Override
				public int compare(AgentWithServiceDto o1, AgentWithServiceDto o2) {
					return o1.getNom().compareTo(o2.getNom());
				}

			});
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_SERVICE())) {
			// on tri la liste
			Collections.sort(getListeApprobateurs(), new Comparator<AgentWithServiceDto>() {
				@Override
				public int compare(AgentWithServiceDto o1, AgentWithServiceDto o2) {
					return o1.getService().compareTo(o2.getService());
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
}
