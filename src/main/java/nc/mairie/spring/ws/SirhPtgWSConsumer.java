package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.servlets.ServletAgent;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONDeserializer;

@Service
public class SirhPtgWSConsumer implements ISirhPtgWSConsumer {

	private static final String sirhPtgAgentsApprobateurs = "droits/approbateurs";
	private static final String sirhPtgVisulaisationPointage = "visualisation/pointagesSIRH";

	@Override
	public List<AgentWithServiceDto> getApprobateurs() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
		String url = String.format(urlWS + sirhPtgAgentsApprobateurs);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> setApprobateurs(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
		String url = String.format(urlWS + sirhPtgAgentsApprobateurs);
		ClientResponse res = createAndPostRequest(json, url);
		return readResponseAsList(AgentWithServiceDto.class, res, url);

	}

	private ClientResponse createAndPostRequest(String json, String url) {

		Client client = Client.create();
		WebResource webResource = client.resource(url);

		ClientResponse response = null;

		try {
			response = webResource.type("application/json").post(ClientResponse.class, json);

		} catch (ClientHandlerException ex) {
			throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}

		return response;
	}

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

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {

			result = targetClass.newInstance();

		} catch (Exception ex) {
			throw new SirhPtgWSConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);

		result = new JSONDeserializer<T>().deserializeInto(output, result);

		return result;
	}

	public <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {

		List<T> result = null;

		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);

		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class).use("values", targetClass)
				.deserialize(output);

		return result;
	}

	@Override
	public List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, String codeService, Integer agentFrom, Integer agentTo,
			Integer idRefEtat, Integer idRefType) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
		String url = String.format(urlWS + sirhPtgVisulaisationPointage);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("from", fromDate);
		parameters.put("to", toDate);
		if (codeService != null)
			parameters.put("codeService", codeService);
		if (agentFrom != null)
			parameters.put("agentFrom", agentFrom.toString());
		if (agentTo != null)
			parameters.put("agentTo", agentTo.toString());
		if (idRefEtat != null)
			parameters.put("etat", idRefEtat.toString());
		if (idRefType != null)
			parameters.put("type", idRefType.toString());

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(ConsultPointageDto.class, res, url);
	}
}
