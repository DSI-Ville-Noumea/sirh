package nc.mairie.spring.ws;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONDeserializer;

public abstract class BaseWsConsumer {

	private Logger logger = LoggerFactory.getLogger(BaseWsConsumer.class);

	/**
	 * POST
	 */
	protected ClientResponse createAndPostRequest(String url, String json) {
		return createAndPostRequest(new HashMap<String, String>(), url, json);
	}

	protected ClientResponse createAndPostRequest(Map<String, String> parameters, String url, String json) {

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
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}

		return response;
	}

	/**
	 * GET
	 */
	protected ClientResponse createAndFireRequest(Map<String, String> parameters, String url) {
		Client client = Client.create();
		WebResource webResource = client.resource(url);

		for (String key : parameters.keySet()) {
			webResource = webResource.queryParam(key, parameters.get(key));
		}

		ClientResponse response = null;

		try {
			response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
		} catch (ClientHandlerException ex) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}
		return response;
	}

	protected <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {
		List<T> result = null;
		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class)
				.use("values", targetClass).deserialize(output);
		return result;
	}
	
	protected <T> T readResponseWithReturnMessageDto(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new SirhWSConsumerException(
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

	public void readResponse(ClientResponse response, String url) {

		if (response.getStatus() == HttpStatus.OK.value())
			return;

		throw new SirhWSConsumerException(String.format(
				"An error occured when querying '%s'. Return code is : %s, content is %s", url, response.getStatus(),
				response.getEntity(String.class)));
	}
	
	protected <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new SirhWSConsumerException(
					"An error occured when instantiating return type when deserializing JSON from " + url + " request.",
					ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
		return result;
	}
	
	protected byte[] readResponseWithFile(ClientResponse response, String url) {

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		return response.getEntity(byte[].class);
	}
	
	protected <K, V> Map<K, V> readResponseAsMap(Class<K> targetClassKey, Class<V> targetClassValue,
			ClientResponse response, String url) {
		Map<K, V> result = null;
		result = new HashMap<K, V>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		result = new JSONDeserializer<Map<K, V>>().use(Date.class, new MSDateTransformer()).use(null, HashMap.class)
				.deserialize(output);
		return result;
	}

	protected <T> List<T> readResponseAsListDate(ClientResponse response, String url) {
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
		result = new JSONDeserializer<List<T>>().use("values", new MSDateTransformer()).use(null, ArrayList.class).deserialize(output);
		return result;
	}

	protected byte[] readResponseAsByteArray(ClientResponse response, String url) throws Exception {

		if (response.getStatus() != HttpStatus.OK.value()) {
			logger.error(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		byte[] reponseData = null;
		File reportFile = null;

		try {
			reportFile = response.getEntity(File.class);
			reponseData = IOUtils.toByteArray(new FileInputStream(reportFile));
		} catch (Exception e) {
			logger.error("Erreur dans readResponseAsByteArray" + e.getMessage());
			throw new Exception("An error occured while reading the downloaded report.", e);
		} finally {
			if (reportFile != null && reportFile.exists())
				reportFile.delete();
		}

		return reponseData;
	}
	
	protected Integer readResponseAsInteger(ClientResponse response, String url) {

		Integer result = 0;

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format(
					"An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new Integer(output);
		return result;
	}
}
