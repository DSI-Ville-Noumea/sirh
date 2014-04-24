package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
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
	private static final String sirhAbsSoldeRecupAgent = "solde/soldeAgent";
	private static final String sirhAbsDemandesAgent = "demandes/listeDemandesAgent";
	private static final String sirhAbsDemandes = "demandes/listeDemandesSIRH";
	private static final String sirhAbsDemandesHistorique = "demandes/historiqueSIRH";
	private static final String sirhAbsMotif = "motif/getListeMotif";
	private static final String sirhAbsMotifSauvegarde = "motif/setMotif";
	private static final String sirhAbsMotifCompteur = "motifCompteur/getListeMotifCompteur";
	private static final String sirhAbsMotifCompteurSauvegarde = "motifCompteur/setMotifCompteur";
	private static final String sirhAbsAddCompteurRecup = "recuperations/addManual";
	private static final String sirhAbsAddCompteurReposComp = "reposcomps/addManual";
	private static final String sirhAbsListeCompteurA48 = "asaA48/listeCompteurA48";
	private static final String sirhAbsAddCompteurAsaA48 = "asaA48/addManual";
	private static final String sirhAbsHistoCompteurAgent = "solde/historiqueSolde";
	private static final String sirhAbsListOrganisationSyndicale = "organisation/listOrganisation";
	private static final String sirhAbsOrganisationSyndicaleSauvegarde = "organisation/addOS";
	private static final String sirhAbsDemandeSauvegarde = "demandes/demandeSIRH";
	private static final String sirhAbsStateSave = "demandes/changerEtatsSIRH";
	private static final String sirhAbsListeCompteurA54 = "asaA54/listeCompteurA54";
	private static final String sirhAbsAddCompteurAsaA54 = "asaA54/addManual";
	private static final String sirhAbsListeCompteurA55 = "asaA55/listeCompteurA55";
	private static final String sirhAbsAddCompteurAsaA55 = "asaA55/addManual";

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
			throw new SirhAbsWSConsumerException(String.format(
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
	public SoldeDto getSoldeAgent(String idAgent, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsSoldeRecupAgent;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent);
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(SoldeDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefType) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsDemandesAgent;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("ongletDemande", onglet);
		if (dateDebut != null)
			params.put("from", dateDebut);
		if (dateFin != null)
			params.put("to", dateFin);
		if (dateDemande != null)
			params.put("dateDemande", dateDemande);
		if (idRefEtat != null)
			params.put("etat", idRefEtat.toString());
		if (idRefType != null)
			params.put("type", idRefType.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent + ",ongletDemande : " + onglet + ",from : "
				+ dateDebut + ",to : " + dateFin + ",dateDemande : " + dateDemande + ",etat : " + idRefEtat
				+ ",type : " + idRefType);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(DemandeDto.class, res, url);

	}

	@Override
	public List<MotifDto> getListeMotif() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsMotif;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MotifDto.class, res, url);
	}

	@Override
	public List<MotifCompteurDto> getListeMotifCompteur(Integer idRefType) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsMotifCompteur;
		HashMap<String, String> params = new HashMap<>();
		if (idRefType != null) {
			params.put("idRefType", idRefType.toString());
		}
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MotifCompteurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotif(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsMotifSauvegarde;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotifCompteur(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsMotifCompteurSauvegarde;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurRecup(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAddCompteurRecup;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurReposComp(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAddCompteurReposComp;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurAsaDto> getListeCompteursA48() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsListeCompteurA48;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurAsaDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA48(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAddCompteurAsaA48;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsHistoCompteurAgent;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("codeRefTypeAbsence", codeTypeAbsence.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseAsList(HistoriqueSoldeDto.class, res, url);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsListOrganisationSyndicale;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(OrganisationSyndicaleDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveOrganisationSyndicale(String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsOrganisationSyndicaleSauvegarde;
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveDemande(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsDemandeSauvegarde;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, Integer idRefEtat, Integer idRefType,
			Integer idAgentRecherche) {

		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsDemandes;
		HashMap<String, String> params = new HashMap<>();
		if (dateDebut != null)
			params.put("from", dateDebut);
		if (dateFin != null)
			params.put("to", dateFin);
		if (idRefEtat != null)
			params.put("etat", idRefEtat.toString());
		if (idRefType != null)
			params.put("type", idRefType.toString());
		if (idAgentRecherche != null)
			params.put("idAgentRecherche", idAgentRecherche.toString());
		logger.debug("Call " + url + " with from : " + dateDebut + ",to : " + dateFin + ",etat : " + idRefEtat
				+ ",type : " + idRefType + ",idAgentRecherche : " + idAgentRecherche);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(DemandeDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getVisualisationHistory(int absId) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsDemandesHistorique;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idDemande", "" + absId);
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(DemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setAbsState(Integer idAgent, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsStateSave;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);

	}

	@Override
	public ReturnMessageDto addCompteurAsaA54(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAddCompteurAsaA54;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurAsaDto> getListeCompteursA54() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsListeCompteurA54;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurAsaDto.class, res, url);
	}

	@Override
	public List<CompteurAsaDto> getListeCompteursA55() {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsListeCompteurA55;
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurAsaDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA55(String idAgentConnecte, String json) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_ABS_WS");
		String url = urlWS + sirhAbsAddCompteurAsaA55;
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

}
