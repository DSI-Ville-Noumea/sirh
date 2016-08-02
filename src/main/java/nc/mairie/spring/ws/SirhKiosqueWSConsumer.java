package nc.mairie.spring.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import flexjson.JSONDeserializer;
import nc.mairie.gestionagent.dto.KiosqueDto;
import nc.mairie.gestionagent.servlets.ServletAgent;

@Service
public class SirhKiosqueWSConsumer implements ISirhKiosqueWSConsumer {
	private Logger logger = LoggerFactory.getLogger(SirhKiosqueWSConsumer.class);

	@Override
	public KiosqueDto setDroitEvalueEAE(String idDocument, boolean removeDroit) {

		HashMap<String, String> params = new HashMap<>();
		params.put("idDocument", idDocument);
		params.put("userSharepoint", (String) ServletAgent.getMesParametres().get("USER_SHAREPOINT_DROIT_EAE"));
		params.put("userPwdSharepoint", (String) ServletAgent.getMesParametres().get("USER_PWD_SHAREPOINT_DROIT_EAE"));
		params.put("domainSharepoint", (String) ServletAgent.getMesParametres().get("DOMAIN_SHAREPOINT_DROIT_EAE"));
		params.put("urlSharepoint", (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_DROIT_EAE"));
		params.put("portSharepoint", (String) ServletAgent.getMesParametres().get("PORT_SHAREPOINT_DROIT_EAE"));
		params.put("urlDroitEAESharepoint", (String) ServletAgent.getMesParametres().get("URL_DROIT_EAE_SHAREPOINT"));

		logger.debug("Call WS Sharepoint mise à jour droit EAE => with parameters idDocument = {}", idDocument);

		HttpResponse res = null;
		try {
			res = createAndFireRequest(params, removeDroit);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return readResponse(KiosqueDto.class, res, params.get("urlDroitEAESharepoint"));
	}

	public HttpResponse createAndFireRequest(Map<String, String> parameters, boolean removeDroit) throws ClientProtocolException, IOException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());

		httpclient.getCredentialsProvider().setCredentials(new AuthScope(parameters.get("urlSharepoint"), Integer.valueOf(parameters.get("portSharepoint"))),
				new NTCredentials(parameters.get("userSharepoint"), parameters.get("userPwdSharepoint"), "", parameters.get("domainSharepoint")));

		HttpHost target = new HttpHost(parameters.get("urlSharepoint"), Integer.valueOf(parameters.get("portSharepoint")), "http");

		// Make sure the same context is used to execute logically related
		// requests
		HttpContext localContext = new BasicHttpContext();

		// Execute a cheap method first. This will trigger NTLM authentication

		HttpGet httpget = new HttpGet(parameters.get("urlDroitEAESharepoint") + "?idDocument=" + parameters.get("idDocument") + (removeDroit ? "&action=rem" : ""));

		HttpResponse response1 = httpclient.execute(target, httpget, localContext);

		return response1;
	}

	public <T> T readResponse(Class<T> targetClass, HttpResponse response, String url) {

		T result = null;

		try {

			result = targetClass.newInstance();

		} catch (Exception ex) {
			throw new BaseWsConsumerException("An error occured when instantiating return type when deserializing JSON from Sharepoint WS request.", ex);
		}

		if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatusLine().getStatusCode()));
		}
		String output = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			output = br.readLine();
		} catch (IOException e) {

		}

		result = new JSONDeserializer<T>().deserializeInto(output, result);

		return result;
	}
}