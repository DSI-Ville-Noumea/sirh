package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.radi.dto.LightUserDto;
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
public class RadiWSConsumer implements IRadiWSConsumer {
	private Logger logger = LoggerFactory.getLogger(RadiWSConsumer.class);

	// droits
	private static final String searchAgentRadi = "users";

	@Override
	public boolean asAgentCompteAD(Integer nomatr) {
		String urlWS = (String) ServletAgent.getMesParametres().get("RADI_WS");
		String url = urlWS + searchAgentRadi;
		HashMap<String, String> params = new HashMap<>();
		params.put("employeenumber", getEmployeeNumberWithNomatr(nomatr));
		logger.debug("Call " + url + " with employeenumber=" + getEmployeeNumberWithNomatr(nomatr));
		ClientResponse res = createAndFireRequest(params, url);

		if (res.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return false;
		} else if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			throw new RadiWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s",
					url, res.getStatus()));
		}
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

	@Override
	public LightUserDto getAgentCompteAD(Integer nomatr) {
		String urlWS = (String) ServletAgent.getMesParametres().get("RADI_WS");
		String url = urlWS + searchAgentRadi;
		HashMap<String, String> params = new HashMap<>();
		params.put("employeenumber", getEmployeeNumberWithNomatr(nomatr));
		logger.debug("Call " + url + " with employeenumber=" + getEmployeeNumberWithNomatr(nomatr));
		ClientResponse res = createAndFireRequest(params, url);
		List<LightUserDto> list = readResponseAsList(LightUserDto.class, res, url);
		return list.size() == 0 ? null : list.get(0);
	}

	public <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {
		List<T> result = null;
		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhAbsWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class)
				.use("values", targetClass).deserialize(output);
		return result;
	}

	@Override
	public String getEmployeeNumberWithNomatr(Integer nomatr) {
		return "90" + nomatr;
	}

	@Override
	public String getIdAgentWithNomatr(Integer nomatr) {
		return "900" + nomatr;
	}

	@Override
	public String getNomatrWithIdAgent(Integer idAgent) {
		return idAgent.toString().substring(3, idAgent.toString().length());
	}

	@Override
	public LightUserDto getAgentCompteADByLogin(String login) {
		String urlWS = (String) ServletAgent.getMesParametres().get("RADI_WS");
		String url = urlWS + searchAgentRadi;
		HashMap<String, String> params = new HashMap<>();
		params.put("sAMAccountName", login);
		logger.debug("Call " + url + " with sAMAccountName=" + login);
		ClientResponse res = createAndFireRequest(params, url);
		List<LightUserDto> list = readResponseAsList(LightUserDto.class, res, url);
		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public String getNomatrWithEmployeeNumber(Integer employeeNumber) {
		return employeeNumber.toString().substring(2, employeeNumber.toString().length());
	}
}
