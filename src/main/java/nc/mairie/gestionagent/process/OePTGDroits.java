package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGDroits extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OePTGDroits.class);

	public static final int STATUT_RECHERCHER_AGENT = 1;
	public String ACTION_CREATION = "Création d'un approbateur.";
	public String ACTION_SUPPRESSION = "Suppression d'un approbateur.";

	private ArrayList<AgentNW> listeApprobateurs = new ArrayList<AgentNW>();

	@Override
	public String getJSP() {
		return "OePTGDroits.jsp";
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
		initialiseDao();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		if (getListeApprobateurs().size() == 0) {
			initialiseApprobateurs(request);
			afficheListeApprobateurs();
		}

	}

	private void initialiseApprobateurs(HttpServletRequest request) throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL_DROIT");
		URL url = new URL(urlWS);
		URLConnection urlc = url.openConnection();
		BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
		String res = "";
		String line;
		while ((line = bfr.readLine()) != null) {
			res += line;
		}

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(res);
		JSONArray jsonObject = (JSONArray) obj;

		int attributeSizeApp = jsonObject.size();
		for (int j = 0; j < attributeSizeApp; j++) {
			JSONObject t = (JSONObject) jsonObject.get(j);
			Long id = (Long) t.get("idAgent");
			AgentNW ag = AgentNW.chercherAgent(getTransaction(), id.toString());
			getListeApprobateurs().add(ag);
		}
	}

	private void afficheListeApprobateurs() throws Exception {
		for (int i = 0; i < getListeApprobateurs().size(); i++) {
			AgentNW ag = getListeApprobateurs().get(i);
			Affectation affCour = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), ag.getIdAgent());
			FichePoste fpCour = FichePoste.chercherFichePoste(getTransaction(), affCour.getIdFichePoste());
			Service servCour = Service.chercherService(getTransaction(), fpCour.getIdServi());
			addZone(getNOM_ST_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent() + " (" + ag.getNoMatricule() + ")");
			addZone(getNOM_ST_SERVICE(i), servCour.getLibService() + " (" + servCour.getSigleService() + ")");

		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {

	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-DROITS";
	}

	public ArrayList<AgentNW> getListeApprobateurs() {
		return listeApprobateurs;
	}

	public void setListeApprobateurs(ArrayList<AgentNW> listeApprobateurs) {
		this.listeApprobateurs = listeApprobateurs;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/09/11 11:31:37)
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
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		
		
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void videZonesDeSaisie(HttpServletRequest request) {
		/*
		 * addZone(getNOM_LB_STATUTS_SELECT(), Const.ZERO);
		 * addZone(getNOM_LB_REGIMES_SELECT(), Const.ZERO);
		 * addZone(getNOM_LB_BASE_HORAIRE_SELECT(), Const.ZERO);
		 */

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
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
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		AgentNW agentSelec = getListeApprobateurs().get(indiceEltASuprimer);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// init de la carriere courante
		/*
		 * if (!initialiseCarriereCourante(request)) return false;
		 */

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}
