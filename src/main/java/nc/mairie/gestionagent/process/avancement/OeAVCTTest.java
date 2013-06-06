package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTTest extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	String jsonSimple;
	ArrayList<String> jsonTable;
	ArrayList<String> jsonListe;
	String jsonDepart;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private static Pattern p = Pattern.compile("\\((\\d+)([+-]\\d{2})(\\d{2})\\)");

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
		/*
		 * String urlWSTableauAvctCAP =
		 * "http://172.16.24.131:8085/sirh-eae-ws/evaluation/eaeIdentification?idEae=12417&idAgent=9002990"
		 * ;
		 * 
		 * URL url = new URL(urlWSTableauAvctCAP); URLConnection urlc =
		 * url.openConnection(); BufferedReader bfr = new BufferedReader(new
		 * InputStreamReader(urlc.getInputStream(), "UTF-8")); String res = "";
		 * String line; while ((line = bfr.readLine()) != null) {
		 * System.out.println(line); res += line; } setJsonDepart(res);
		 * 
		 * JSONParser parser = new JSONParser(); Object obj = parser.parse(res);
		 * JSONObject jsonObject = (JSONObject) obj;
		 * 
		 * Long idEae = (Long) jsonObject.get("idEae");
		 * 
		 * // objet simple JSONObject agent = (JSONObject)
		 * jsonObject.get("agent"); String nomAgent = (String) agent.get("nom");
		 * setJsonSimple(idEae.toString() + " " + nomAgent);
		 * 
		 * // date String dateEntretien = (String)
		 * jsonObject.get("dateEntretien"); Date date = jd2d(dateEntretien);
		 * SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		 * setJsonSimple(idEae.toString() + " : date Entretien : " +
		 * sdf.format(date));
		 * 
		 * // liste diplomes JSONArray tab = (JSONArray)
		 * jsonObject.get("diplomes"); setJsonTable(tab);
		 * 
		 * // test liste JSONObject position = (JSONObject)
		 * jsonObject.get("position"); JSONArray listePosition = (JSONArray)
		 * position.get("liste"); ArrayList<String> liste = new
		 * ArrayList<String>();
		 * 
		 * int attributeSize = listePosition.size(); for (int j = 0; j <
		 * attributeSize; j++) { JSONObject t = (JSONObject)
		 * listePosition.get(j); liste.add((String) t.get("code")); }
		 * setJsonListe(liste);
		 * 
		 * // liste de liste JSONObject statut = (JSONObject)
		 * jsonObject.get("statut"); JSONObject statut2 = (JSONObject)
		 * statut.get("statut"); JSONArray listePositionStatut = (JSONArray)
		 * statut2.get("liste"); ArrayList<String> liste2 = new
		 * ArrayList<String>();
		 * 
		 * int attributeSize2 = listePositionStatut.size(); for (int j = 0; j <
		 * attributeSize2; j++) { JSONObject t = (JSONObject)
		 * listePositionStatut.get(j); liste2.add((String) t.get("code") + " " +
		 * t.get("valeur")); } setJsonListe(liste2);
		 * 
		 * // liste des evaluateurs JSONArray evaluateurs = (JSONArray)
		 * jsonObject.get("evaluateurs"); ArrayList<String> listeEv = new
		 * ArrayList<String>();
		 * 
		 * int attributeSizeEv = evaluateurs.size(); for (int j = 0; j <
		 * attributeSizeEv; j++) { JSONObject t = (JSONObject)
		 * evaluateurs.get(j); listeEv.add((String) t.get("nom")); }
		 * setJsonListe(listeEv);
		 */
	}

	public static Date jd2d(String jsonDateString) {
		Matcher m = p.matcher(jsonDateString);
		if (m.find()) {
			long millis = Long.parseLong(m.group(1));
			long offsetHours = Long.parseLong(m.group(2));
			long offsetMinutes = Long.parseLong(m.group(3));
			if (offsetHours < 0)
				offsetMinutes *= -1;
			return new Date(millis + offsetHours * 60l * 60l * 1000l + offsetMinutes * 60l * 1000l);
		}
		return null;
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

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/09/11 11:31:37)
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
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on recupere le json de depart
		/*
		 * EaeIdentificationDto dto = new
		 * EaeIdentificationDto().deserializeFromJSON(getJsonDepart());
		 * SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		 * dto.setDateEntretien(sdf.parse("26/04/1985")); String result =
		 * dto.serializeInJSON(); HttpPost postRequest = new HttpPost(
		 * "http://172.16.24.131:8085/sirh-eae-ws/evaluation/eaeIdentification?idEae=12417&idAgent=9002990"
		 * );
		 * 
		 * StringEntity input = new StringEntity(result);
		 * input.setContentType("application/json");
		 * postRequest.setEntity(input);
		 * 
		 * DefaultHttpClient httpClient = new DefaultHttpClient(); HttpResponse
		 * response = httpClient.execute(postRequest);
		 * 
		 * if (response.getStatusLine().getStatusCode() != 200) { throw new
		 * RuntimeException("Failed : HTTP error code : " +
		 * response.getStatusLine().getStatusCode()); }
		 * 
		 * BufferedReader br = new BufferedReader(new
		 * InputStreamReader((response.getEntity().getContent()), "UTF-8"));
		 * 
		 * String output; StringBuffer totalOutput = new StringBuffer();
		 * System.out.println("Output from Server ...."); while ((output =
		 * br.readLine()) != null) { totalOutput.append(output); }
		 * System.out.println(totalOutput.toString());
		 */
		return true;
	}

	public String getJsonDepart() {
		return jsonDepart;
	}

	public void setJsonDepart(String jsonDepart) {
		this.jsonDepart = jsonDepart;
	}

}