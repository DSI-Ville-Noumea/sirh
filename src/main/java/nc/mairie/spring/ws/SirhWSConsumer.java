package nc.mairie.spring.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
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
public class SirhWSConsumer implements ISirhWSConsumer {

	private static final String sirhBaseCongeUrl = "absences/baseHoraire";

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
		SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS");
		String url = urlWS + sirhBaseCongeUrl;

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponse(RefTypeSaisiCongeAnnuelDto.class, res, url);
	}

}
