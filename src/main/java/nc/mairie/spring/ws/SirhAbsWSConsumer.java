package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.absence.dto.ActeursDto;
import nc.mairie.gestionagent.absence.dto.AgentOrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.RefAlimCongesAnnuelsDto;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RestitutionMassiveDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.UnitePeriodeQuotaDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.InputterDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.dto.ViseursDto;
import nc.mairie.metier.Const;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SirhAbsWSConsumer implements ISirhAbsWSConsumer {

	@Autowired
	@Qualifier("absWsBaseUrl")
	private String absWsBaseUrl;

	private static final String sirhAbsListOrganisationSyndicale = "organisation/listOrganisation";
	private static final String sirhAbsOrganisationSyndicaleSauvegarde = "organisation/addOS";
	private static final String sirhAbsAddRepresentantAsaA52 = "asaA52/saveRepresentant";
	private static final String sirhAbsListOSA52Url = "asaA52/listeOrganisationSyndicaleA52";
	private static final String sirhAbsListRepresentantOSA52Url = "asaA52/listeRepresentantA52";

	private static final String sirhAbsListOrganisationActif = "organisation/listOrganisationActif";

	private static final String sirhAbsAgentsApprobateurs = "droits/approbateurs";
	private static final String sirhAbsDeleteApprobateurs = "droits/deleteApprobateurs";
	private static final String sirhAbsSauvegardeDelegataire = "droits/delegataire";
	private static final String sirhAbsListeActeurs = "droits/listeActeurs";
	private static final String sirhAgentApprobateurUrl = "droits/agentsApprouves";
	private static final String sirhOperateursDelegataireApprobateurUrl = "droits/inputter";
	private static final String sirhOperateurApprobateurUrl = "droits/operateurSIRH";
	private static final String sirhDeleteOperateurApprobateurUrl = "droits/deleteOperateurSIRH";
	private static final String sirhViseursApprobateurUrl = "droits/viseur";
	private static final String sirhViseursApprobateurSIRHUrl = "droits/viseurSIRH";
	private static final String sirhdeleteViseursApprobateurSIRHUrl = "droits/deleteViseurSIRH";
	private static final String sirhAgentsOperateurUrl = "droits/agentsSaisisByOperateur";
	private static final String sirhAgentsViseurUrl = "droits/agentsSaisisByViseur";

	private static final String sirhAbsSoldeRecupAgent = "solde/soldeAgent";
	private static final String sirhAbsHistoCompteurAgent = "solde/historiqueSolde";

	private static final String sirhAbsDemandesAgent = "demandes/listeDemandesAgent";
	private static final String sirhAbsDemandes = "demandes/listeDemandesSIRH";
	private static final String sirhAbsDemandesHistorique = "demandes/historiqueSIRH";
	private static final String sirhAbsDemandeSauvegarde = "demandes/demandeSIRH";
	private static final String sirhAbsStateSave = "demandes/changerEtatsSIRH";
	private static final String sirhDureeCongeAnnuelUrl = "demandes/dureeDemandeCongeAnnuel";

	private static final String sirhAbsMotif = "motif/getListeMotif";
	private static final String sirhAbsMotifSauvegarde = "motif/setMotif";

	private static final String sirhAbsMotifCompteur = "motifCompteur/getListeMotifCompteur";
	private static final String sirhAbsMotifCompteurSauvegarde = "motifCompteur/setMotifCompteur";

	private static final String sirhAbsListeCompteurA48 = "asaA48/listeCompteurA48";
	private static final String sirhAbsListeCompteurA54 = "asaA54/listeCompteurA54";
	private static final String sirhAbsListeCompteurA55 = "asaA55/listeCompteurA55";
	private static final String sirhAbsListeCompteurA53 = "asaA53/listeCompteurA53";
	private static final String sirhAbsListeCompteurA52 = "asaA52/listeCompteurA52";
	private static final String sirhAbsListeCompteurAmicale = "asaAmicale/listeCompteurAmicale";

	private static final String sirhAbsInitialiseCompteurCongeAnnuel = "congeannuel/intitCompteurCongeAnnuel";

	private static final String sirhAbsAddCompteurRecup = "recuperations/addManual";
	private static final String sirhAbsAddCompteurCongeAnnuel = "congeannuel/addManual";
	private static final String sirhAbsRestitutionMassive = "congeannuel/restitutionMassive";
	private static final String sirhAbsHistoRestitutionMassive = "congeannuel/getHistoRestitutionMassive";
	private static final String sirhAbsDetailsHistoRestitutionMassive = "congeannuel/getDetailsHistoRestitutionMassive";
	private static final String sirhAbsHistoRestitutionMassiveByIdAgent = "congeannuel/getHistoRestitutionMassiveByIdAgent";
	private static final String sirhAbsAddCompteurReposComp = "reposcomps/addManual";
	private static final String sirhAbsAddCompteurAsaA48 = "asaA48/addManual";
	private static final String sirhAbsAddCompteurAsaA54 = "asaA54/addManual";
	private static final String sirhAbsAddCompteurAsaA55 = "asaA55/addManual";
	private static final String sirhAbsAddCompteurAsaA53 = "asaA53/addManual";
	private static final String sirhAbsAddCompteurAsaA52 = "asaA52/addManual";
	private static final String sirhAbsAddCompteurAsaAmicale = "asaAmicale/addManual";

	private static final String sirhAbsListeRefTypeAbs = "typeAbsence/getListeTypeAbsence";
	private static final String sirhAbsAddCongeExcep = "typeAbsence/setTypeAbsence";
	private static final String sirhAbsDeleteCongeExcep = "typeAbsence/deleteTypeAbsence";
	private static final String sirhAbsRefTypeAbs = "typeAbsence/getTypeAbsence";

	private static final String sirhAbsListeUnitePeriodeQuota = "filtres/getUnitePeriodeQuota";
	private static final String sirhAbsGroupeAbsenceUrl = "filtres/getGroupesAbsence";

	private static final String sirhAbsMoisAlimAutoUrl = "congeannuel/getListeMoisAlimAutoCongeAnnuel";
	private static final String sirhAbsAlimAutoUrl = "congeannuel/getListeAlimAutoCongeAnnuel";
	private static final String sirhAbsHistoAlimAutoCongeAnnuelUrl = "congeannuel/getHistoAlimAutoCongeAnnuel";
	private static final String sirhAbsListRefAlimUrl = "congeannuel/getListRefAlimCongeAnnuel";
	private static final String sirhAbsRefAlimCongeAnnuelSauvegarde = "congeannuel/setRefAlimCongeAnnuel";
	private static final String sirhAbsCreateBaseConge = "congeannuel/createRefAlimCongeAnnuelAnnee";

	private static final String sirhAbsHistoAlimAutoRecupUrl = "recuperations/getHistoAlimAutoRecup";
	private static final String sirhAbsHistoAlimAutoReposCompUrl = "reposcomps/getHistoAlimAutoReposComp";

	private Logger logger = LoggerFactory.getLogger(SirhAbsWSConsumer.class);

	@Override
	public List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsAgentsApprobateurs);
		HashMap<String, String> params = new HashMap<>();
		if (idAgent != null) {
			params.put("idAgent", idAgent.toString());
		}
		if (idServiceADS != null) {
			params.put("idServiceADS", idServiceADS.toString());
		}
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(ApprobateurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setApprobateur(String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAgentsApprobateurs);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteApprobateur(String json) {
		String url = String.format(absWsBaseUrl + sirhAbsDeleteApprobateurs);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
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
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'.", url), ex);
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
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'.", url), ex);
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
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);
		logger.trace("json recu:" + output);
		result = new JSONDeserializer<List<T>>().use(Date.class, new MSDateTransformer()).use(null, ArrayList.class).use("values", targetClass).deserialize(output);
		return result;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {
			result = targetClass.newInstance();
		} catch (Exception ex) {
			throw new BaseWsConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH ABS WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
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
			throw new BaseWsConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH ABS WS request.", ex);
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
	public SoldeDto getSoldeAgent(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsSoldeRecupAgent);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(SoldeDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin, String dateDemande, String listIdRefEtat, Integer idRefType,
			Integer idRefGroupeAbsence) {
		String url = String.format(absWsBaseUrl + sirhAbsDemandesAgent);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("ongletDemande", onglet);
		if (dateDebut != null)
			params.put("from", dateDebut);
		if (dateFin != null)
			params.put("to", dateFin);
		if (dateDemande != null)
			params.put("dateDemande", dateDemande);
		if (listIdRefEtat != null)
			params.put("etat", listIdRefEtat);
		if (idRefType != null)
			params.put("type", idRefType.toString());
		if (idRefGroupeAbsence != null)
			params.put("groupe", idRefGroupeAbsence.toString());

		logger.debug("Call " + url + " with idAgent : " + idAgent + ",ongletDemande : " + onglet + ",from : " + dateDebut + ",to : " + dateFin + ",dateDemande : " + dateDemande + ",etat : "
				+ listIdRefEtat + ",type : " + idRefType);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(DemandeDto.class, res, url);

	}

	@Override
	public List<MotifDto> getListeMotif() {
		String url = String.format(absWsBaseUrl + sirhAbsMotif);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MotifDto.class, res, url);
	}

	@Override
	public List<MotifCompteurDto> getListeMotifCompteur(Integer idRefType) {
		String url = String.format(absWsBaseUrl + sirhAbsMotifCompteur);
		HashMap<String, String> params = new HashMap<>();
		if (idRefType != null) {
			params.put("idRefType", idRefType.toString());
		}
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MotifCompteurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotif(String json) {
		String url = String.format(absWsBaseUrl + sirhAbsMotifSauvegarde);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotifCompteur(String json) {
		String url = String.format(absWsBaseUrl + sirhAbsMotifCompteurSauvegarde);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurRecup(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurRecup);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurReposComp(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurReposComp);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursA48() {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurA48);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA48(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaA48);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoCompteurAgent);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("codeRefTypeAbsence", codeTypeAbsence.toString());
		params.put("isSIRH", Boolean.TRUE.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseAsList(HistoriqueSoldeDto.class, res, url);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		String url = String.format(absWsBaseUrl + sirhAbsListOrganisationSyndicale);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(OrganisationSyndicaleDto.class, res, url);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOrganisationSyndicaleActiveByAgent(Integer idAgent, Integer idRefTypeAbsence) {
		String url = String.format(absWsBaseUrl + sirhAbsListOrganisationActif);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("idRefTypeAbsence", idRefTypeAbsence.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(OrganisationSyndicaleDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveOrganisationSyndicale(String json) {
		String url = String.format(absWsBaseUrl + sirhAbsOrganisationSyndicaleSauvegarde);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveDemande(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsDemandeSauvegarde);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, String listIdRefEtat, Integer idRefType, Integer idAgentRecherche, Integer idRefGroupe, boolean aValider,
			List<String> idAgentsService) {

		String url = String.format(absWsBaseUrl + sirhAbsDemandes);
		HashMap<String, String> params = new HashMap<>();
		params.put("aValider", String.valueOf(aValider));

		if (dateDebut != null)
			params.put("from", dateDebut);
		if (dateFin != null)
			params.put("to", dateFin);
		if (listIdRefEtat != null)
			params.put("etat", listIdRefEtat);
		if (idRefType != null)
			params.put("type", idRefType.toString());
		if (idAgentRecherche != null)
			params.put("idAgentRecherche", idAgentRecherche.toString());
		if (idRefGroupe != null)
			params.put("groupe", idRefGroupe.toString());

		if (idAgentsService != null) {
			String csvId = Const.CHAINE_VIDE;
			for (String id : idAgentsService) {
				csvId += id + ",";
			}
			if (csvId != Const.CHAINE_VIDE) {
				csvId = csvId.substring(0, csvId.length() - 1);
			}
			params.put("idAgents", csvId);
		}

		logger.debug("Call " + url + " with from : " + dateDebut + ",to : " + dateFin + ",etat : " + listIdRefEtat + ",type : " + idRefType + ",idAgentRecherche : " + idAgentRecherche + ",groupe : "
				+ idRefGroupe + ",aValider : " + aValider + ", idAgents : " + idAgentsService);

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(DemandeDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getVisualisationHistory(Integer absId) {
		String url = String.format(absWsBaseUrl + sirhAbsDemandesHistorique);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idDemande", absId.toString());
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(DemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setAbsState(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsStateSave);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);

	}

	@Override
	public ReturnMessageDto addCompteurAsaA54(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaA54);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursA54() {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurA54);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursA55() {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurA55);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA55(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaA55);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA52(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaA52);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA53(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaA53);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursA52(Integer idOrganisationSyndicale) {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurA52);
		HashMap<String, String> params = new HashMap<>();
		params.put("idOrganisationSyndicale", idOrganisationSyndicale.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursA53() {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurA53);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}

	@Override
	public List<TypeAbsenceDto> getListeRefTypeAbsenceDto(Integer idRefGroupe) {
		String url = String.format(absWsBaseUrl + sirhAbsListeRefTypeAbs);
		HashMap<String, String> params = new HashMap<>();
		if (idRefGroupe != null)
			params.put("idRefGroupeAbsence", idRefGroupe.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(TypeAbsenceDto.class, res, url);
	}

	@Override
	public List<UnitePeriodeQuotaDto> getUnitePeriodeQuota() {
		String url = String.format(absWsBaseUrl + sirhAbsListeUnitePeriodeQuota);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(UnitePeriodeQuotaDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveTypeAbsence(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCongeExcep);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteTypeAbsence(Integer idAgentConnecte, Integer idRefTypeAbsence) {
		String url = String.format(absWsBaseUrl + sirhAbsDeleteCongeExcep);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		params.put("idRefTypeAbsence", idRefTypeAbsence.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<RefGroupeAbsenceDto> getRefGroupeAbsence() {
		String url = String.format(absWsBaseUrl + sirhAbsGroupeAbsenceUrl);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefGroupeAbsenceDto.class, res, url);

	}

	@Override
	public ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer idAgentConcerne) {
		String url = String.format(absWsBaseUrl + sirhAbsInitialiseCompteurCongeAnnuel);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", agentConnecte.toString());
		params.put("idAgentConcerne", idAgentConcerne.toString());
		ClientResponse res = createAndPostRequest(params, url, Const.CHAINE_VIDE);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public TypeAbsenceDto getTypeAbsence(Integer idBaseHoraireAbsence) {
		String url = String.format(absWsBaseUrl + sirhAbsRefTypeAbs);
		HashMap<String, String> params = new HashMap<>();
		params.put("idBaseHoraireAbsence", idBaseHoraireAbsence.toString());
		logger.debug("Call " + url + " with idBaseHoraireAbsence : " + idBaseHoraireAbsence);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(TypeAbsenceDto.class, res, url);
	}

	@Override
	public DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto) {
		String url = String.format(absWsBaseUrl + sirhDureeCongeAnnuelUrl);
		HashMap<String, String> params = new HashMap<>();

		String json = new JSONSerializer().exclude("*.class").exclude("*.civilite").exclude("*.signature").exclude("*.position").exclude("*.selectedDroitAbs")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(demandeDto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(DemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurCongeAnnuel(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurCongeAnnuel);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveRepresentantAsaA52(Integer idOrganisationSyndicale, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddRepresentantAsaA52);
		HashMap<String, String> params = new HashMap<>();
		params.put("idOrganisationSyndicale", idOrganisationSyndicale.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addRestitutionMassive(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsRestitutionMassive);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getListeMoisALimAUtoCongeAnnuel() {
		String url = String.format(absWsBaseUrl + sirhAbsMoisAlimAutoUrl);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MoisAlimAutoCongesAnnuelsDto.class, res, url);
	}

	@Override
	public List<RestitutionMassiveDto> getHistoRestitutionMassive(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoRestitutionMassive);

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RestitutionMassiveDto.class, res, url);
	}

	@Override
	public RestitutionMassiveDto getDetailsHistoRestitutionMassive(Integer idAgent, RestitutionMassiveDto dto) {
		String url = String.format(absWsBaseUrl + sirhAbsDetailsHistoRestitutionMassive);

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(RestitutionMassiveDto.class, res, url);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAutoCongeAnnuel(MoisAlimAutoCongesAnnuelsDto moisChoisi, boolean onlyErreur) {
		String url = String.format(absWsBaseUrl + sirhAbsAlimAutoUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("onlyErreur", String.valueOf(onlyErreur));

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(moisChoisi);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseAsList(MoisAlimAutoCongesAnnuelsDto.class, res, url);
	}

	@Override
	public List<RefAlimCongesAnnuelsDto> getListeRefAlimCongesAnnuels(Integer idRefTypeSaisiCongeAnnuel) {
		String url = String.format(absWsBaseUrl + sirhAbsListRefAlimUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idRefTypeSaisiCongeAnnuel", idRefTypeSaisiCongeAnnuel.toString());
		logger.debug("Call " + url + " with idRefTypeSaisiCongeAnnuel : " + idRefTypeSaisiCongeAnnuel);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefAlimCongesAnnuelsDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveRefAlimMensuelle(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsRefAlimCongeAnnuelSauvegarde);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setDelegataire(Integer idAgent, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsSauvegardeDelegataire);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ActeursDto getListeActeurs(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsListeActeurs);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ActeursDto.class, res, url);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoCongeAnnuelAgent(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoAlimAutoCongeAnnuelUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MoisAlimAutoCongesAnnuelsDto.class, res, url);
	}

	@Override
	public List<RestitutionMassiveDto> getHistoRestitutionMassiveByIdAgent(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoRestitutionMassiveByIdAgent);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RestitutionMassiveDto.class, res, url);
	}

	@Override
	public ReturnMessageDto createNouvelleAnneeBaseConges(Integer anneeCreation) {
		String url = String.format(absWsBaseUrl + sirhAbsCreateBaseConge);
		HashMap<String, String> params = new HashMap<>();
		params.put("annee", anneeCreation.toString());
		logger.debug("Call " + url + " with annee : " + anneeCreation);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getAgentsApprobateur(Integer idAgentApprobateur) {
		String url = String.format(absWsBaseUrl + sirhAgentApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public InputterDto getOperateursDelegataireApprobateur(Integer idAgentApprobateur) {
		String url = String.format(absWsBaseUrl + sirhOperateursDelegataireApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(InputterDto.class, res, url);
	}

	@Override
	public ViseursDto getViseursApprobateur(Integer idAgentApprobateur) {
		String url = String.format(absWsBaseUrl + sirhViseursApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ViseursDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveAgentsApprobateur(Integer idAgent, List<AgentDto> listSelect) {
		String url = String.format(absWsBaseUrl + sirhAgentApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveOperateurApprobateur(Integer idAgent, AgentDto dto) {
		String url = String.format(absWsBaseUrl + sirhOperateurApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteOperateurApprobateur(Integer idAgent, AgentDto dto) {
		String url = String.format(absWsBaseUrl + sirhDeleteOperateurApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveViseurApprobateur(Integer idAgent, AgentDto dto) {
		String url = String.format(absWsBaseUrl + sirhViseursApprobateurSIRHUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteViseurApprobateur(Integer idAgent, AgentDto dto) {
		String url = String.format(absWsBaseUrl + sirhdeleteViseursApprobateurSIRHUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur) {
		String url = String.format(absWsBaseUrl + sirhAgentsOperateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());
		params.put("idOperateur", idAgentOperateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> listSelect) {
		String url = String.format(absWsBaseUrl + sirhAgentsOperateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());
		params.put("idOperateur", idAgentOperateur.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur) {
		String url = String.format(absWsBaseUrl + sirhAgentsViseurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());
		params.put("idViseur", idAgentViseur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur, List<AgentDto> listSelect) {
		String url = String.format(absWsBaseUrl + sirhAgentsViseurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentApprobateur.toString());
		params.put("idViseur", idAgentViseur.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoRecupAgent(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoAlimAutoRecupUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MoisAlimAutoCongesAnnuelsDto.class, res, url);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoReposCompAgent(Integer idAgent) {
		String url = String.format(absWsBaseUrl + sirhAbsHistoAlimAutoReposCompUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(MoisAlimAutoCongesAnnuelsDto.class, res, url);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOSCompteursA52() {
		String url = String.format(absWsBaseUrl + sirhAbsListOSA52Url);
		HashMap<String, String> params = new HashMap<>();
		logger.debug("Call " + url);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(OrganisationSyndicaleDto.class, res, url);
	}

	@Override
	public List<AgentOrganisationSyndicaleDto> getListeRepresentantA52(Integer idOrganisation) {
		String url = String.format(absWsBaseUrl + sirhAbsListRepresentantOSA52Url);
		HashMap<String, String> params = new HashMap<>();
		params.put("idOrganisationSyndicale", idOrganisation.toString());
		logger.debug("Call " + url + " with idOrganisationSyndicale : " + idOrganisation);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentOrganisationSyndicaleDto.class, res, url);
	}

	@Override
	public ReturnMessageDto addCompteurAsaAmicale(Integer idAgentConnecte, String json) {
		String url = String.format(absWsBaseUrl + sirhAbsAddCompteurAsaAmicale);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentConnecte.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<CompteurDto> getListeCompteursAmicale() {
		String url = String.format(absWsBaseUrl + sirhAbsListeCompteurAmicale);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CompteurDto.class, res, url);
	}
}
