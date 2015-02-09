package nc.mairie.spring.ws;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.servlets.ServletAgent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONDeserializer;

@Service
public class SirhWSConsumer implements ISirhWSConsumer {

	private static final String sirhBaseCongeUrl = "absences/baseHoraire";
	private static final String sirhDateAvancementUrl = "calculEae/calculDateAvancement";
	private static final String sirhConstruitArbreFDPUrl = "fichePostes/rebuildFichePosteTree";

	// Pour les editions BIRT
	private static final String sirhDownloadTabAvctPUrl = "avancements/downloadTableauAvancements";
	private static final String sirhDownloadArretesPUrl = "avancements/downloadArretes";
	private static final String sirhDownloadFichePosteSIRHPUrl = "fichePostes/downloadFichePosteSIRH";
	private static final String sirhDownloadNoteServiceSIRHPUrl = "noteService/downloadNoteServiceSIRH";
	private static final String sirhDownloadNoteServiceInterneSIRHPUrl = "noteService/downloadNoteServiceInterneSIRH";
	private static final String sirhDownloadConvocationVisiteMedPUrl = "suiviMedical/downloadConvocationSIRH";
	private static final String sirhDownloadLettreAccompagnementVisiteMedPUrl = "suiviMedical/downloadLettreAccompagnementSIRH";
	private static final String sirhDownloadContratUrl = "contrat/downloadContratSIRH";
	private static final String sirhBaseHorairePointageUrl = "pointages/baseHoraire";

	private Logger logger = LoggerFactory.getLogger(SirhWSConsumer.class);

	/**
	 * GET
	 */
	public ClientResponse createAndFireRequest(Map<String, String> parameters, String url) {
		Client client = Client.create();
		WebResource webResource = client.resource(url);

		for (String key : parameters.keySet()) {
			webResource = webResource.queryParam(key, parameters.get(key));
		}

		ClientResponse response = null;

		try {
			response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
		} catch (ClientHandlerException ex) {
			throw new SirhAbsWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}
		return response;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new SirhAbsWSConsumerException(
					"An error occured when instantiating return type when deserializing JSON from SIRH ABS WS request.",
					ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhAbsWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}

	@Override
	public RefTypeSaisiCongeAnnuelDto getBaseHoraireAbsence(Integer idAgent, Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhBaseCongeUrl;

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponse(RefTypeSaisiCongeAnnuelDto.class, res, url);
	}

	@Override
	public DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDateAvancementUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(DateAvctDto.class, res, url);
	}

	@Override
	public boolean miseAJourArbreFDP() {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String urlTotal = urlWS + sirhConstruitArbreFDPUrl;

		boolean response = true;

		try {
			URL url = new URL(urlTotal);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			try {
				if (conn.getResponseCode() != 200) {
					response = false;
					logger.error("Failed Arbre service : HTTP error code : " + conn.getResponseCode());
				}
			} catch (Exception e) {
				response = false;
				logger.error("Erreur dans la connexion à l'url des WS SIRH", e);
			}
		} catch (Exception e) {
			logger.error("Erreur dans la connexion à l'url des WS SIRH", e);
		}

		return response;
	}

	@Override
	public byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format)
			throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadTabAvctPUrl;

		String urlWSTableauAvctCAP = url + "?idCap=" + idCap + "&idCadreEmploi=" + idCadreEmploi + "&avisEAE="
				+ avisEAE;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSTableauAvctCAP);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte)
			throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadArretesPUrl;
		String urlWSArretes = url + "?isChangementClasse=" + isChangementClasse + "&csvIdAgents=" + csvAgents
				+ "&annee=" + anneeAvct + "&isDetache=" + isAffecte;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);

	}

	@Override
	public byte[] downloadFichePoste(Integer idFichePoste) throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadFichePosteSIRHPUrl;
		String urlWSArretes = url + "?idFichePoste=" + idFichePoste;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		if (typeDocument == null) {
			urlWS = urlWS + sirhDownloadNoteServiceInterneSIRHPUrl + "?idAffectation=" + idAffectation;
		} else {
			urlWS = urlWS + sirhDownloadNoteServiceSIRHPUrl + "?idAffectation=" + idAffectation + "&typeNoteService="
					+ typeDocument;
		}

		Client client = Client.create();

		WebResource webResource = client.resource(urlWS);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadConvocationVisiteMedPUrl;
		String urlWSConvocation = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation
				+ "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSConvocation);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadLettreAccompagnementVisiteMedPUrl;
		String urlWSAccomp = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation
				+ "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSAccomp);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhDownloadContratUrl;
		String urlWSContrat = url + "?idAgent=" + idAgent + "&idContrat=" + idContrat;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSContrat);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	public byte[] readResponseAsByteArray(ClientResponse response) throws Exception {

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new Exception(String.format("An error occured ", response.getStatus()));
		}

		byte[] reponseData = null;
		File reportFile = null;

		try {
			reportFile = response.getEntity(File.class);
			reponseData = IOUtils.toByteArray(new FileInputStream(reportFile));
		} catch (Exception e) {
			throw new Exception("An error occured while reading the downloaded report.", e);
		} finally {
			if (reportFile != null && reportFile.exists())
				reportFile.delete();
		}

		return reponseData;
	}

	@Override
	public BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date date) {
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL");
		String url = urlWS + sirhBaseHorairePointageUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("date", sf.format(date));
		
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(BaseHorairePointageDto.class, res, url);
	}

}
