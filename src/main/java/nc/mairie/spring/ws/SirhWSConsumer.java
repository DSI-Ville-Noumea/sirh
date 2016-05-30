package nc.mairie.spring.ws;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.AutreAdministrationAgentDto;
import nc.mairie.gestionagent.eae.dto.CalculEaeInfosDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONSerializer;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String sirhDateAvancementUrl = "calculEae/calculDateAvancement";
	private static final String sirhConstruitArbreFDPUrl = "fichePostes/rebuildFichePosteTree";
	private static final String sirhAffectationActiveByAgentUrl = "calculEae/affectationActiveByAgent";
	private static final String sirhListeAutreAdministrationAgentUrl = "calculEae/listeAutreAdministrationAgent";
	private static final String sirhDeleteFDPUrl = "fichePostes/deleteFichePosteByIdFichePoste";

	// Pour les editions BIRT
	private static final String sirhDownloadTabAvctPDFUrl = "avancements/downloadTableauAvancementsPDF"; // edition PDF
	private static final String sirhDownloadArretesPUrl = "avancements/downloadArretes";
	private static final String sirhDownloadFichePosteSIRHPUrl = "fichePostes/downloadFichePosteSIRH";
	private static final String sirhDownloadNoteServiceSIRHPUrl = "noteService/downloadNoteServiceSIRH";
	private static final String sirhDownloadNoteServiceInterneSIRHPUrl = "noteService/downloadNoteServiceInterneSIRH";
	private static final String sirhDownloadConvocationVisiteMedPUrl = "suiviMedical/downloadConvocationSIRH";
	private static final String sirhDownloadLettreAccompagnementVisiteMedPUrl = "suiviMedical/downloadLettreAccompagnementSIRH";
	private static final String sirhDownloadContratUrl = "contrat/downloadContratSIRH";
	private static final String sirhBaseHorairePointageUrl = "pointages/baseHoraire";

	// pour la gestion des droits
	private static final String sirhAgentSubordonnesUrl = "agents/agentsSubordonnes";
	private static final String sirhArbreServicesWithListAgentsByServiceUrl = "agents/arbreServicesWithListAgentsByServiceWithoutAgentConnecte";
	private static final String sirhListAgentsWithServiceUrl = "services/listAgentsWithService";

	private Logger logger = LoggerFactory.getLogger(SirhWSConsumer.class);

	@Override
	public DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDateAvancementUrl);

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with idAgent : " + idAgent);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(DateAvctDto.class, res, url);
	}

	@Override
	public boolean miseAJourArbreFDP() {
		String urlTotal = String.format(sirhWsBaseUrl + sirhConstruitArbreFDPUrl);

		boolean response = true;

		try {
			URL url = new URL(urlTotal);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			try {
				if (conn.getResponseCode() != 200) {
					response = false;
					logger.error("Failed Arbre service : HTTP error code : " + conn.getResponseCode());
				}
			} catch (Exception e) {
				response = false;
				logger.error("Erreur dans la connexion a l'url des WS SIRH", e);
			}
		} catch (Exception e) {
			logger.error("Erreur dans la connexion a l'url des WS SIRH", e);
		}

		return response;
	}

	@Override
	public byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadTabAvctPDFUrl);

		String urlWSTableauAvctCAP = url + "?idCap=" + idCap + "&idCadreEmploi=" + idCadreEmploi + "&avisEAE=" + avisEAE;
		
		logger.debug("downloadTableauAvancement : URL génération tableau AVCT : " + urlWSTableauAvctCAP);

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSTableauAvctCAP);

		ClientResponse response = webResource.get(ClientResponse.class);
		
		logger.debug("downloadTableauAvancement : ClientResponse : " + response);

		return readResponseAsByteArray(response, urlWSTableauAvctCAP);
	}

	@Override
	public byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadArretesPUrl);
		String urlWSArretes = url + "?isChangementClasse=" + isChangementClasse + "&csvIdAgents=" + csvAgents + "&annee=" + anneeAvct + "&isDetache=" + isAffecte;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, urlWSArretes);

	}

	@Override
	public byte[] downloadFichePoste(Integer idFichePoste) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadFichePosteSIRHPUrl);
		String urlWSArretes = url + "?idFichePoste=" + idFichePoste;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, urlWSArretes);
	}

	@Override
	public byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception {

		String url = null;

		if (typeDocument == null) {
			url = String.format(sirhWsBaseUrl + sirhDownloadNoteServiceInterneSIRHPUrl + "?idAffectation=" + idAffectation);
		} else {
			url = String.format(sirhWsBaseUrl + sirhDownloadNoteServiceSIRHPUrl + "?idAffectation=" + idAffectation + "&typeNoteService=" + typeDocument);
		}

		Client client = Client.create();

		WebResource webResource = client.resource(url);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, url);
	}

	@Override
	public byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadConvocationVisiteMedPUrl);
		String urlWSConvocation = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSConvocation);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, urlWSConvocation);
	}

	@Override
	public byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadLettreAccompagnementVisiteMedPUrl);
		String urlWSAccomp = url + "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois + "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSAccomp);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, urlWSAccomp);
	}

	@Override
	public byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception {
		String url = String.format(sirhWsBaseUrl + sirhDownloadContratUrl);
		String urlWSContrat = url + "?idAgent=" + idAgent + "&idContrat=" + idContrat;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSContrat);

		ClientResponse response = webResource.get(ClientResponse.class);

		return readResponseAsByteArray(response, urlWSContrat);
	}

	@Override
	public List<AgentDto> getAgentsSubordonnes(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhAgentSubordonnesUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("maxDepth", "10");

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhDeleteFDPUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idFichePoste", idFichePoste.toString());
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	/**
	 * Retourne la liste des bases horaires par rapport à la liste des
	 * affectations de l'agent triees par date de debut dans l ordre croissant
	 */
	@Override
	public List<BaseHorairePointageDto> getListBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhBaseHorairePointageUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(BaseHorairePointageDto.class, res, url);
	}

	@Override
	public CalculEaeInfosDto getDetailAffectationActiveByAgent(Integer idAgent, Integer anneeFormation) {
		String url = String.format(sirhWsBaseUrl + sirhAffectationActiveByAgentUrl);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("anneeFormation", String.valueOf(anneeFormation));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponse(CalculEaeInfosDto.class, res, url);
	}

	@Override
	public List<AutreAdministrationAgentDto> getListeAutreAdministrationAgent(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhListeAutreAdministrationAgentUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(AutreAdministrationAgentDto.class, res, url);
	}

	@Override
	public EntiteWithAgentWithServiceDto getListeEntiteWithAgentWithServiceDtoByIdServiceAds(
			Integer idServiceAds, Integer idAgent, List<AgentDto> listAgentsAInclureDansArbre) {
		
		String url = String.format(sirhWsBaseUrl + sirhArbreServicesWithListAgentsByServiceUrl);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("idServiceADS", idServiceAds.toString());
		
		if (idAgent != null)
			params.put("idAgent", String.valueOf(idAgent));
		
		String json = null;
		if(null != listAgentsAInclureDansArbre
				&& !listAgentsAInclureDansArbre.isEmpty()) {
			
			List<Integer> listIdsAgent = new ArrayList<Integer>();
			for(AgentDto agent : listAgentsAInclureDansArbre) {
				listIdsAgent.add(agent.getIdAgent());
			}
			
			json = new JSONSerializer().exclude("*.class").deepSerialize(listIdsAgent);
		}

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(EntiteWithAgentWithServiceDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> getListAgentsWithService(List<Integer> listAgentDto, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhListAgentsWithServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listAgentDto);

		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			parameters.put("date", sf.format(date));
		}

		ClientResponse res = createAndPostRequest(parameters, url, json);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

}
