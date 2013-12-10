package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.SoldeDto;
import nc.mairie.gestionagent.servlets.ServletAgent;

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
public class SirhAbsWSConsumer implements ISirhAbsWSConsumer {

	private static final String sirhAbsAgentsApprobateurs = "droits/approbateurs";
	private static final String sirhAbsSoldeRecupAgent = "recuperations/soldeRecup";

	private Logger logger = LoggerFactory.getLogger(SirhAbsWSConsumer.class);

	@Override
	public List<AgentWithServiceDto> getApprobateurs() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAgentsApprobateurs;
		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> setApprobateurs(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAgentsApprobateurs;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseAsList(AgentWithServiceDto.class, res, url);

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
			throw new SirhAbsWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
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
			throw new SirhAbsWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
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
			throw new SirhPtgWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class)
				.use("values", targetClass).deserialize(output);
		return result;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new SirhPtgWSConsumerException(
					"An error occured when instantiating return type when deserializing JSON from SIRH ABS WS request.", ex);
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

	@Override
	public SoldeDto getSoldeRecup(String idAgent) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsSoldeRecupAgent;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent);
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(SoldeDto.class, res, url);
	}
}
