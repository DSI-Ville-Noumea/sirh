package nc.mairie.spring.ws;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.AutreAdministrationAgentDto;
import nc.mairie.gestionagent.eae.dto.CalculEaeInfosDto;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String sirhDateAvancementUrl = "calculEae/calculDateAvancement";
	private static final String sirhConstruitArbreFDPUrl = "fichePostes/rebuildFichePosteTree";
	private static final String sirhAffectationActiveByAgentUrl = "calculEae/affectationActiveByAgent";
	private static final String sirhListeAutreAdministrationAgentUrl = "calculEae/listeAutreAdministrationAgent";
	private static final String sirhDeleteFDPUrl = "fichePostes/deleteFichePosteByIdFichePoste";

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

	// pour la gestion des droits
	private static final String sirhAgentSubordonnesUrl = "agents/agentsSubordonnes";

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
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}
		return response;
	}

	public <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {
		List<T> result = null;
		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class).use("values", targetClass).deserialize(output);
		return result;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new BaseWsConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH ABS WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}

	@Override
	public DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDateAvancementUrl);

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(DateAvctDto.class, res, url);
	}

	@Override
	public boolean miseAJourArbreFDP() {
		String urlTotal = String.format(sirhWsBaseUrl + sirhConstruitArbreFDPUrl);

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
				logger.error("Erreur dans la connexion a l'url des WS SIRH", e);
			}
		} catch (Exception e) {
			logger.error("Erreur dans la connexion a l'url des WS SIRH", e);
		}

		return response;
	}

	@Override
	public byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadTabAvctPUrl);

		String urlWSTableauAvctCAP = url + "?idCap=" + idCap + "&idCadreEmploi=" + idCadreEmploi + "&avisEAE=" + avisEAE;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSTableauAvctCAP);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadArretesPUrl);
		String urlWSArretes = url + "?isChangementClasse=" + isChangementClasse + "&csvIdAgents=" + csvAgents + "&annee=" + anneeAvct + "&isDetache=" + isAffecte;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);

	}

	@Override
	public byte[] downloadFichePoste(Integer idFichePoste) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadFichePosteSIRHPUrl);
		String urlWSArretes = url + "?idFichePoste=" + idFichePoste;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception {

		String url = null;

		if (typeDocument == null) {
			url = String.format(sirhWsBaseUrl + sirhDownloadNoteServiceInterneSIRHPUrl + "?idAffectation=" + idAffectation);
		} else {
			url = String.format(sirhWsBaseUrl + sirhDownloadNoteServiceSIRHPUrl + "?idAffectation=" + idAffectation + "&typeNoteService=" + typeDocument);
		}

		Client client = Client.create();

		WebResource webResource = client.resource(url);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadConvocationVisiteMedPUrl);
		String urlWSConvocation = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSConvocation);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadLettreAccompagnementVisiteMedPUrl);
		String urlWSAccomp = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSAccomp);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response);
	}

	@Override
	public byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadContratUrl);
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
	public List<AgentDto> getAgentsSubordonnes(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhAgentSubordonnesUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhDeleteFDPUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idFichePoste", idFichePoste.toString());
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	public <T> T readResponseWithReturnMessageDto(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new BaseWsConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			return null;
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}

	/**
	 * Retourne la liste des bases horaires par rapport à la liste des
	 * affectations de l'agent triees par date de debut dans l ordre croissant
	 */
	@Override
	public List<BaseHorairePointageDto> getListBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhBaseHorairePointageUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(BaseHorairePointageDto.class, res, url);
	}

	@Override
	public CalculEaeInfosDto getDetailAffectationActiveByAgent(Integer idAgent, Integer anneeFormation) {
		String url = String.format(sirhWsBaseUrl + sirhAffectationActiveByAgentUrl);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("anneeFormation", String.valueOf(anneeFormation));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponse(CalculEaeInfosDto.class, res, url);
	}

	@Override
	public List<AutreAdministrationAgentDto> getListeAutreAdministrationAgent(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhListeAutreAdministrationAgentUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(AutreAdministrationAgentDto.class, res, url);
	}

}
