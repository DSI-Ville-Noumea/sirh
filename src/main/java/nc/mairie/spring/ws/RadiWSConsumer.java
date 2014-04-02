package nc.mairie.spring.ws;

import java.util.HashMap;
import java.util.Map;

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
		String employeenumber = "90" + nomatr;
		params.put("employeenumber", employeenumber);
		logger.debug("Call " + url + " with " + employeenumber);
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
}
