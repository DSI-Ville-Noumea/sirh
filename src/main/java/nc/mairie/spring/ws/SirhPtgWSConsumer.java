package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.dto.RefEtatDto;
import nc.mairie.gestionagent.dto.RefPrimeDto;
import nc.mairie.gestionagent.dto.RefTypePointageDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.gestionagent.dto.FichePointageDto;

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

    private static final String sirhPtgAgentsApprobateurs = "droits/approbateurs";
    private static final String sirhPtgVisulaisationPointage = "visualisation/pointagesSIRH";
    private static final String sirhPtgVisualisationHistory = "visualisation/historiqueSIRH";
    private static final String sirhPtgVisualisationSetState = "visualisation/changerEtatsSIRH";
    private static final String sirhPtgSaisie = "saisie/ficheSIRH";
    private static final String sirhPtgEtatsPointage = "filtres/getEtats";
    private static final String sirhPtgTypesPointage = "filtres/getTypes";
    private static final String sirhPtgPrimesStatut = "primes/getListePrimeWithStatus";
    private static final String sirhPtgPrimes = "primes/getListePrime";
    private static final String sirhPtgPrimeDetail = "primes/getPrime";

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

    @Override
    public FichePointageDto getSaisiePointage(String idAgent, String monday) {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgSaisie);
        HashMap<String, String> params = new HashMap<>();
        params.put("idAgent", idAgent);
        params.put("date", monday);
        //System.out.println("Call "+url+" with "+idAgent+", "+monday);
        ClientResponse res = createAndFireRequest(params, url);
        return readResponse(FichePointageDto.class, res, url);
    }

    @Override
    public ClientResponse setPtgState(ArrayList<Integer> idPtgs, int idRefEtat, String idagent) {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgVisualisationSetState + "?idAgent=" + idagent);

        StringBuilder json = new StringBuilder("[");
        for (Integer id : idPtgs) {
            json.append("{\"idPointage\" : " + id + ",\"idRefEtat\" : " + idRefEtat + "},");
        }
        if (idPtgs.size() > 0) {
            json.substring(0, json.length() - 1);
        }
        json.append("]");
        return createAndPostRequest(json.toString(), url);
    }

    @Override
    public List<ConsultPointageDto> getVisualisationHistory(int idPointage) {

        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgVisualisationHistory);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("idPointage", "" + idPointage);
        ClientResponse res = createAndFireRequest(parameters, url);
        return readResponseAsList(ConsultPointageDto.class, res, url);
    }

    private ClientResponse createAndPostRequest(Map parameters, String url) {
        String json = new JSONSerializer().serialize(parameters);
        System.out.println("appel :" + url + " avec " + json);
        return createAndPostRequest(json, url);
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
        result = new JSONDeserializer<T>().use(Date.class, new MSDateTransformer()).deserializeInto(output, result);
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
        result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class).use("values", targetClass).deserialize(output);
        return result;
    }

    public <K, V> Map<K, V> readResponseAsMap(Class<K> targetClassKey, Class<V> targetClassValue, ClientResponse response, String url) {
        Map<K, V> result = null;
        result = new HashMap<K, V>();

        if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
            return result;
        }

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new SirhPtgWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
        }

        String output = response.getEntity(String.class);
        result = new JSONDeserializer<Map<K, V>>().use(Date.class, new MSDateTransformer()).use(null, HashMap.class).deserialize(output);
        return result;
    }

    @Override
    public List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents, Integer idRefEtat, Integer idRefType) {

        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgVisulaisationPointage);

        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("from", fromDate);
        if (toDate != null) {
            parameters.put("to", toDate);
        }
        if (idAgents != null) {
            String csvId = "";
            for (String id : idAgents) {
                csvId += id + ",";
            }
            if (csvId != "") {
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

        ClientResponse res = createAndFireRequest(parameters, url);

        return readResponseAsList(ConsultPointageDto.class, res, url);
    }

    @Override
    public List<RefEtatDto> getEtatsPointage() {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgEtatsPointage);

        ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

        return readResponseAsList(RefEtatDto.class, res, url);
    }

    @Override
    public List<RefTypePointageDto> getTypesPointage() {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgTypesPointage);

        ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

        return readResponseAsList(RefTypePointageDto.class, res, url);
    }

    @Override
    public List<RefPrimeDto> getPrimes(String agentStatus) {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgPrimesStatut);
        HashMap<String, String> params = new HashMap<>();
        params.put("statutAgent", agentStatus);
        ClientResponse res = createAndFireRequest(params, url);
        return readResponseAsList(RefPrimeDto.class, res, url);
    }

    @Override
    public List<RefPrimeDto> getPrimes() {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgPrimes);
        HashMap<String, String> params = new HashMap<>();
        ClientResponse res = createAndFireRequest(params, url);
        return readResponseAsList(RefPrimeDto.class, res, url);
    }

    @Override
    public RefPrimeDto getPrimeDetail(Integer numRubrique) {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgPrimeDetail);
        HashMap<String, String> params = new HashMap<>();
        params.put("noRubr", numRubrique.toString());
        ClientResponse res = createAndFireRequest(params, url);
        return readResponse(RefPrimeDto.class, res, url);
    }

    @Override
    public ClientResponse setSaisiePointage(String idagent, FichePointageDto toSerialize) {
        String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_PTG_WS");
        String url = String.format(urlWS + sirhPtgSaisie + "?idAgent=" + idagent);
       // String url = String.format(urlWS + "saisie/fiche?idAgent=9003047");  //pour un pointage 5463
        return createAndPostRequest(new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(toSerialize), url);
    }
}