package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTTest extends nc.mairie.technique.BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	String jsonSimple;
	ArrayList<String> jsonTable;
	ArrayList<String> jsonListe;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
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
		String urlWSTableauAvctCAP = "http://172.16.24.131:8085/sirh-eae-ws/evaluation/eaeIdentification?idEae=12417&idAgent=9002990";

		URL url = new URL(urlWSTableauAvctCAP);
		URLConnection urlc = url.openConnection();
		BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));
		String res = "";
		String line;
		while ((line = bfr.readLine()) != null) {
			System.out.println(line);
			res += line;
		}
		System.out.println(res);

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(res);

		JSONObject jsonObject = (JSONObject) obj;

		Long idEae = (Long) jsonObject.get("idEae");
		System.out.println(idEae.toString());

		JSONObject agent = (JSONObject) jsonObject.get("agent");
		String nomAgent = (String) agent.get("nom");
		System.out.println(nomAgent);
		setJsonSimple(idEae.toString() + " " + nomAgent);
		
		JSONArray tab = (JSONArray) jsonObject.get("diplomes");
		setJsonTable(tab);
		Iterator<String> iterator = tab.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

		JSONObject position = (JSONObject) jsonObject.get("position");
		JSONArray listePosition =(JSONArray) position.get("liste");
		ArrayList<String> liste = new ArrayList<String>();
		
		int attributeSize = listePosition.size();


		for(int j = 0; j < attributeSize; j++){
			JSONObject t = (JSONObject) listePosition.get(j);
			liste.add((String) t.get("code"));
		    System.out.println(t.get("code"));
		}
		setJsonListe(liste);
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-TEST";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTTest() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTTest.jsp";
	}

	public String getJsonSimple() {
		return jsonSimple == null ? Const.CHAINE_VIDE : jsonSimple;
	}

	public void setJsonSimple(String jsonSimple) {
		this.jsonSimple = jsonSimple;
	}

	public ArrayList<String> getJsonTable() {
		return jsonTable;
	}

	public void setJsonTable(ArrayList<String> jsonTable) {
		this.jsonTable = jsonTable;
	}

	public ArrayList<String> getJsonListe() {
		return jsonListe;
	}

	public void setJsonListe(ArrayList<String> jsonListe) {
		this.jsonListe = jsonListe;
	}

}