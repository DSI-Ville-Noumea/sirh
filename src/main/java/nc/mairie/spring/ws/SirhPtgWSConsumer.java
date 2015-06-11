package nc.mairie.spring.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.CanStartVentilationDto;
import nc.mairie.gestionagent.pointage.dto.CanStartWorkflowPaieActionDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;

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
import flexjson.JSONSerializer;

@Service
public class SirhPtgWSConsumer implements ISirhPtgWSConsumer {

	// droits
	private static final String sirhPtgAgentsApprobateurs = "droits/approbateurs";
	private static final String sirhPtgDeleteApprobateurs = "droits/deleteApprobateurs";
	private static final String sirhPtgSauvegardeDelegataire = "droits/delegataire";
	private static final String ptgDroitsAgentsApprouvesUrl = "droits/agentsApprouves";
	private static final String ptgDroitsAgentsApprouvesSIRHUrl = "droits/agentsApprouvesForSIRH";
	private static final String ptgDroitsDelegataireOperateursUrl = "droits/delegataireOperateurs";
	private static final String ptgDroitsAgentsSaisisUrl = "droits/agentsSaisis";

	// Visualisation
	private static final String sirhPtgVisualisationPointage = "visualisation/pointagesSIRH";
	private static final String sirhPtgVisualisationHistory = "visualisation/historiqueSIRH";
	private static final String sirhPtgVisualisationSetState = "visualisation/changerEtatsSIRH";
	private static final String sirhPtgVisualisationIdAgentPointage = "visualisation/listeAgentsPointagesForSIRH";
	// saisie
	private static final String sirhPtgSaisie = "saisie/ficheSIRH";
	// ventilation
	private static final String sirhPtgStartVentilation = "ventilation/start";
	private static final String sirhPtgVentilationsShow = "ventilation/show";
	private static final String sirhPtgVentilationsHistory = "ventilation/showHistory";
	private static final String sirhPtgCheckVentil = "ventilation/canStartVentilation";
	private static final String sirhPtgCheckIsVentilRuning = "ventilation/isVentilation";
	private static final String sirhPtgVentilationEnCours = "ventilation/getVentilationEnCours";
	private static final String sirhPtgErreursVentilation = "ventilation/getErreursVentilation";
	private static final String sirhPtgListeAgentsForShowVentilation = "ventilation/listeAgentsToShowVentilation";

	// export paie
	private static final String sirhPtgCheckValid = "exportPaie/canStartExportPaie";
	private static final String sirhPtgCheckIsValidRuning = "exportPaie/isExportPaie";
	private static final String sirhPtgStartDeversement = "exportPaie/start";
	// etats du payeur
	private static final String sirhPtgCanStartExportEtatsPayeur = "etatsPayeur/canStartExportEtatsPayeur";
	private static final String sirhPtgListEtatsPayeur = "etatsPayeur/listEtatsPayeur";
	private static final String sirhPtgDownloadFicheEtatsPayeur = "etatsPayeur/downloadFicheEtatsPayeur";
	private static final String sirhPtgStartExportEtatsPayeur = "etatsPayeur/start";
	// filtres
	private static final String sirhPtgEtatsPointage = "filtres/getEtats";
	private static final String sirhPtgTypesPointage = "filtres/getTypes";
	private static final String sirhPtgTypeAbsence = "filtres/getTypesAbsence";
	private static final String sirhPtgMotifHeureSup = "filtres/getMotifHsup";
	private static final String sirhPtgSaveMotifHeureSup = "filtres/setMotifHsup";
	// primes
	private static final String sirhPtgPrimesStatut = "primes/getListePrimeWithStatus";
	private static final String sirhPtgPrimes = "primes/getListePrime";
	private static final String sirhPtgPrimeDetail = "primes/getPrime";
	private static final String sirhPtgPrimeDetailFromIdRefPrime = "primes/getPrimeFromIdRefPrime";
	private static final String sirhPtgPrimePointee = "primes/isPrimeUtilisee";

	private Logger logger = LoggerFactory.getLogger(SirhPtgWSConsumer.class);

	@Override
	public List<ApprobateurDto> getApprobateurs(String codeService, Integer idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgAgentsApprobateurs;
		HashMap<String, String> params = new HashMap<>();
		if (idAgent != null) {
			params.put("idAgent", idAgent.toString());
		}
		if (codeService != null) {
			params.put("codeService", codeService);
		}
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(ApprobateurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setApprobateur(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgAgentsApprobateurs;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteApprobateur(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgDeleteApprobateurs;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public FichePointageDto getSaisiePointage(Integer idAgent, String monday) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgSaisie;
		HashMap<String, String> params = new HashMap<>();
		idAgent = idAgent.toString().startsWith("900") ? idAgent : Integer.valueOf("900" + idAgent);
		params.put("date", monday);
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with " + idAgent + ", " + monday);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(FichePointageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setPtgState(ArrayList<Integer> idPtgs, Integer idRefEtat, Integer idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVisualisationSetState;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		StringBuilder json = new StringBuilder("[");
		for (Integer id : idPtgs) {
			json.append("{\"idPointage\" : " + id + ",\"idRefEtat\" : " + idRefEtat + "},");
		}
		if (idPtgs.size() > 0) {
			json.substring(0, json.length() - 1);
		}
		json.append("]");
		ClientResponse res = createAndPostRequest(params, url, json.toString());
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<ConsultPointageDto> getVisualisationHistory(Integer idPointage) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVisualisationHistory;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idPointage", idPointage.toString());
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(ConsultPointageDto.class, res, url);
	}

	/**
	 * POST
	 */
	private ClientResponse createAndPostRequest(String url, String json) {
		return createAndPostRequest(new HashMap<String, String>(), url, json);
	}

	private ClientResponse createAndPostRequest(Map<String, String> parameters, String url, String json) {

		Client client = Client.create();
		WebResource webResource = client.resource(url);

		for (String key : parameters.keySet()) {
			webResource = webResource.queryParam(key, parameters.get(key));
		}

		ClientResponse response = null;
		logger.trace("json poste:" + json);
		try {
			response = webResource.type("application/json").post(ClientResponse.class, json);

		} catch (ClientHandlerException ex) {
			throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}

		return response;
	}

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
			throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}
		return response;
	}

	public void readResponse(ClientResponse response, String url) {

		if (response.getStatus() == HttpStatus.OK.value())
			return;

		throw new SirhPtgWSConsumerException(String.format(
				"An error occured when querying '%s'. Return code is : %s, content is %s", url, response.getStatus(),
				response.getEntity(String.class)));
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new SirhPtgWSConsumerException(
					"An error occured when instantiating return type when deserializing JSON from SIRH WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}

	public byte[] readResponseWithFile(ClientResponse response, String url) {

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		return response.getEntity(byte[].class);
	}

	public <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {
		List<T> result = null;
		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class)
				.use("values", targetClass).deserialize(output);
		return result;
	}

	public <K, V> Map<K, V> readResponseAsMap(Class<K> targetClassKey, Class<V> targetClassValue,
			ClientResponse response, String url) {
		Map<K, V> result = null;
		result = new HashMap<K, V>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		result = new JSONDeserializer<Map<K, V>>().use(Date.class, new MSDateTransformer()).use(null, HashMap.class)
				.deserialize(output);
		return result;
	}

	@Override
	public List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents,
			Integer idRefEtat, Integer idRefType, String typeHeureSup) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVisualisationPointage;

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("from", fromDate);
		if (toDate != null) {
			parameters.put("to", toDate);
		}
		if (idAgents != null) {
			String csvId = Const.CHAINE_VIDE;
			for (String id : idAgents) {
				csvId += id + ",";
			}
			if (csvId != Const.CHAINE_VIDE) {
				csvId = csvId.substring(0, csvId.length() - 1);
			}
			parameters.put("idAgents", csvId);
		}
		if (idRefEtat != null) {
			parameters.put("etat", idRefEtat.toString());
		}
		if (idRefType != null) {
			parameters.put("type", idRefType.toString());
		}
		if (typeHeureSup != null) {
			parameters.put("typeHS", typeHeureSup);
		}

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(ConsultPointageDto.class, res, url);
	}

	@Override
	public List<RefEtatDto> getEtatsPointage() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgEtatsPointage;

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

		return readResponseAsList(RefEtatDto.class, res, url);
	}

	@Override
	public List<RefTypePointageDto> getTypesPointage() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgTypesPointage;

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(RefTypePointageDto.class, res, url);
	}

	@Override
	public List<RefPrimeDto> getPrimes(String agentStatus) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgPrimesStatut;
		HashMap<String, String> params = new HashMap<>();
		params.put("statutAgent", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefPrimeDto.class, res, url);
	}

	@Override
	public List<RefPrimeDto> getPrimes() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgPrimes;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefPrimeDto.class, res, url);
	}

	@Override
	public RefPrimeDto getPrimeDetail(Integer numRubrique) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgPrimeDetail;
		HashMap<String, String> params = new HashMap<>();
		params.put("noRubr", numRubrique.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(RefPrimeDto.class, res, url);
	}

	@Override
	public RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgPrimeDetailFromIdRefPrime;
		HashMap<String, String> params = new HashMap<>();
		params.put("idRefPrime", idRefPrime.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(RefPrimeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto toSerialize) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgSaisie;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(
				params,
				url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
						.deepSerialize(toSerialize));
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgPrimePointee;
		HashMap<String, String> params = new HashMap<>();
		params.put("noRubr", numRubrique.toString());
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public <T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage,
			String agentsJson, boolean allVentilation) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVentilationsShow;
		HashMap<String, String> params = new HashMap<>();
		params.put("idDateVentil", idDateVentil.toString());
		params.put("typePointage", idRefTypePointage.toString());
		params.put("allVentilation", String.valueOf(allVentilation));
		if (agentsJson.equals("[]"))
			return new ArrayList<T>();
		ClientResponse res = createAndPostRequest(params, url, agentsJson);
		return readResponseAsList(targetClass, res, url);
	}

	@Override
	public <T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee,
			Integer idRefTypePointage, Integer idAgent, boolean allVentilation, Integer idVentilDate) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVentilationsHistory;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("mois", mois.toString());
		parameters.put("annee", annee.toString());
		parameters.put("typePointage", idRefTypePointage.toString());
		parameters.put("idAgent", idAgent.toString());
		parameters.put("allVentilation", String.valueOf(allVentilation));
		parameters.put("idVentilDate", String.valueOf(idVentilDate));
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(targetClass, res, url);
	}

	@Override
	public boolean isValidAvailable(String agentStatus) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgCheckValid;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			return false;
		}
	}

	@Override
	public boolean isVentilAvailable(String agentStatus) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgCheckVentil;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartVentilationDto result = readResponse(CanStartVentilationDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartVentilation();
		} else {
			return false;
		}
	}

	@Override
	public VentilDateDto getVentilationEnCours(String statut) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVentilationEnCours;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			VentilDateDto result = readResponse(VentilDateDto.class, res, url);
			logger.trace(result.toString());
			return result;
		} else {
			return null;
		}
	}

	@Override
	public boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut,
			String idRefTypePointage) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgStartVentilation;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("date", sdf.format(dateVentilation));
		params.put("statut", statut);
		if (idRefTypePointage != null)
			params.put("typePointage", idRefTypePointage);
		ClientResponse res = createAndPostRequest(params, url, agentsJson);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("StartVentilation NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public boolean startDeversementPaie(Integer idAgent, String statut) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgStartDeversement;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("StartDeversement NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public boolean canStartExportEtatsPayeur(String statut) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgCanStartExportEtatsPayeur;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			logger.debug("StartExportEtatsPayeur NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgListEtatsPayeur;
		HashMap<String, String> params = new HashMap<>();
		params.put("statutAgent", statut);
		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(EtatsPayeurDto.class, res, url);
	}

	@Override
	public byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgDownloadFicheEtatsPayeur;
		HashMap<String, String> params = new HashMap<>();
		params.put("idEtatPayeur", idEtatPayeur.toString());
		ClientResponse res = createAndFireRequest(params, url);

		return readResponseWithFile(res, url);
	}

	@Override
	public boolean startExportEtatsPayeur(Integer idAgentExporting, String statutString) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgStartExportEtatsPayeur;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentExporting.toString());
		params.put("statut", statutString);
		ClientResponse res = createAndFireRequest(params, url);

		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("startExportEtatsPayeur NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public ArrayList<Integer> getListeIdAgentPointage() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgVisualisationIdAgentPointage;
		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		List<AgentDto> liste = readResponseAsList(AgentDto.class, res, url);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (AgentDto agDto : liste) {
			result.add(agDto.getIdAgent());
		}
		return result;
	}

	@Override
	public List<VentilErreurDto> getErreursVentilation(String type) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgErreursVentilation;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("statut", type);
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(VentilErreurDto.class, res, url);
	}

	@Override
	public List<TypeAbsenceDto> getListeRefTypeAbsence() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgTypeAbsence;
		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(TypeAbsenceDto.class, res, url);
	}

	@Override
	public boolean isValidEnCours(String agentStatus) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgCheckIsValidRuning;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			return false;
		}
	}

	@Override
	public boolean isVentilEnCours(String agentStatus) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgCheckIsVentilRuning;
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartVentilationDto result = readResponse(CanStartVentilationDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartVentilation();
		} else {
			return false;
		}
	}

	public <T> T readResponseWithReturnMessageDto(Class<T> targetClass, ClientResponse response, String url) {

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

		if (response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			return null;
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}

	@Override
	public List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil, Integer idRefTypePointage,
			String statut, Date ventilationDate, String agentMin, String agentMax, boolean allVentilation) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgListeAgentsForShowVentilation;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		HashMap<String, String> params = new HashMap<>();
		params.put("idDateVentil", idDateVentil.toString());
		params.put("typePointage", idRefTypePointage.toString());
		params.put("ventilationDate", sdf.format(ventilationDate));
		params.put("statut", statut);
		params.put("agentMin", agentMin);
		params.put("agentMax", agentMax);
		params.put("allVentilation", String.valueOf(allVentilation));

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(Integer.class, res, url);
	}

	@Override
	public List<MotifHeureSupDto> getListeMotifHeureSup() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgMotifHeureSup;

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(MotifHeureSupDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotifHeureSup(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgSaveMotifHeureSup;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndPostRequest(params, url, json);

		ReturnMessageDto message = new ReturnMessageDto();

		if (res.getStatus() == HttpStatus.CONFLICT.value()) {
			message.getErrors().add("Le motif n'a pu être sauvegardé.");
		}

		return message;
	}

	@Override
	public ReturnMessageDto setDelegataire(Integer idAgent, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + sirhPtgSauvegardeDelegataire;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getApprovedAgents(Integer idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsAgentsApprouvesSIRHUrl;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public DelegatorAndOperatorsDto getDelegateAndOperator(Integer idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsDelegataireOperateursUrl;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(DelegatorAndOperatorsDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveApprovedAgents(Integer idAgent, List<AgentDto> listSelect) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsAgentsApprouvesUrl;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);

		ReturnMessageDto dto = new ReturnMessageDto();
		try {
			readResponse(res, url);
		} catch (SirhPtgWSConsumerException e) {
			dto.setErrors(Arrays.asList("Une erreur est survenue lors de la sauvegarde des agents à approuver."));
		}

		return dto;
	}

	@Override
	public ReturnMessageDto saveDelegateAndOperator(Integer idAgent, DelegatorAndOperatorsDto dto) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsDelegataireOperateursUrl;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getAgentsSaisisOperateur(Integer idAgent, Integer idOperateur) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsAgentsSaisisUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("idOperateur", idOperateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveAgentsSaisisOperateur(Integer idAgent, Integer idOperateur, List<AgentDto> listSelect) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS_URL");
		String url = urlWS + ptgDroitsAgentsSaisisUrl;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("idOperateur", idOperateur.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);
		ReturnMessageDto dto = new ReturnMessageDto();
		try {
			readResponse(res, url);
		} catch (SirhPtgWSConsumerException e) {
			dto.setErrors(Arrays.asList("Une erreur est survenue lors de la sauvegarde des agents à approuver."));
		}

		return dto;
	}

}
