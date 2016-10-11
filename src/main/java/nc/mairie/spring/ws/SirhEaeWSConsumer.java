package nc.mairie.spring.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import flexjson.JSONSerializer;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.BirtDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneTaskDto;
import nc.mairie.gestionagent.eae.dto.EaeDashboardItemDto;
import nc.mairie.gestionagent.eae.dto.EaeDocumentDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.EaeEvolutionDto;
import nc.mairie.gestionagent.eae.dto.EaePlanActionDto;
import nc.mairie.gestionagent.eae.dto.FormRehercheGestionEae;
import nc.mairie.gestionagent.eae.dto.ListItemDto;

@Service
public class SirhEaeWSConsumer extends BaseWsConsumer implements ISirhEaeWSConsumer {

	@Autowired
	@Qualifier("eaeWsBaseUrl")
	private String				eaeWsBaseUrl;

	private static final String	sirhEaelistEaesByAgentUrl		= "sirhEaes/listEaesByAgent";
	private static final String	sirhSaveEvaluationUrl			= "sirhEaes/eaeEvaluation";
	private static final String	sirhSavePlanActionUrl			= "sirhEaes/eaePlanAction";
	private static final String	sirhSaveEvolutionUrl			= "sirhEaes/eaeEvolution";
	private static final String	sirhEaeNumIncrementUrl			= "sirhEaes/eaeNumIncrement";
	private static final String	sirhListeTypeDeveloppementUrl	= "sirhEaes/listeTypeDeveloppement";
	private static final String	sirhDetailsEaeUrl				= "sirhEaes/detailsEae";

	private static final String	sirhListCampagnesEaeUrl			= "sirhEaes/listCampagnesEae";
	private static final String	sirhSetCampagneEaeUrl			= "sirhEaes/setCampagneEae";
	private static final String	sirhCampagneAnneePrecedenteUrl	= "sirhEaes/campagneAnneePrecedente";
	private static final String	sirhDocumentEaeUrl				= "sirhEaes/documentEae";
	private static final String	sirhDeleteDocumentEaeUrl		= "sirhEaes/deleteDocumentEae";
	private static final String	sirhSetEaeUrl					= "sirhEaes/eae";
	private static final String	sirhListeEaeUrl					= "sirhEaes/listeEae";

	private static final String	sirhEaeCampagneTaskUrl			= "sirhEaes/eaeCampagneTask";

	private static final String	sirhUpdateEaeUrl				= "sirhEaes/updateEae";
	private static final String	sirhLastDocumentEaeFinaliseUrl	= "sirhEaes/lastDocumentEaeFinalise";
	private static final String	sirhUpdateCapEaeUrl				= "sirhEaes/updateCapEae";
	private static final String	sirhTableauDeBordUrl			= "sirhEaes/tableauDeBord";

	// sur l'EAE de l'agent
	private static final String	saveDateEvaluateurFromSirhUrl	= "sirhEaes/saveDateEvaluateurFromSirh";
	private static final String	saveDateEvalueFromSirhUrl		= "sirhEaes/saveDateEvalueFromSirh";

	@Override
	public List<EaeDto> getListEaesByidAgent(Integer idAgentSirh, Integer idAgent) {

		String url = eaeWsBaseUrl + sirhEaelistEaesByAgentUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(EaeDto.class, res, url);
	}

	@Override
	public EaeDto getDetailsEae(Integer idAgentSirh, Integer idEae) {

		String url = eaeWsBaseUrl + sirhDetailsEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(EaeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveEvaluation(Integer idAgentSirh, Integer idEae, EaeEvaluationDto eaeEvaluationDto) {

		String url = eaeWsBaseUrl + sirhSaveEvaluationUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(eaeEvaluationDto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto savePlanAction(Integer idAgentSirh, Integer idEae, EaePlanActionDto eaePlanActionDto) {

		String url = eaeWsBaseUrl + sirhSavePlanActionUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(eaePlanActionDto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveEvolution(Integer idAgentSirh, Integer idEae, EaeEvolutionDto eaeEvolutionDto) {

		String url = eaeWsBaseUrl + sirhSaveEvolutionUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(eaeEvolutionDto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public Integer chercherEaeNumIncrement(Integer idAgentSirh) {

		String url = eaeWsBaseUrl + sirhEaeNumIncrementUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(Integer.class, res, url);
	}

	@Override
	public List<ListItemDto> getListeTypeDeveloppement() {

		String url = eaeWsBaseUrl + sirhListeTypeDeveloppementUrl;

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(ListItemDto.class, res, url);
	}

	@Override
	public List<CampagneEaeDto> getListeCampagnesEae(Integer idAgentSirh) {

		String url = eaeWsBaseUrl + sirhListCampagnesEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(CampagneEaeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto createOrModifyCampagneEae(Integer idAgentSirh, CampagneEaeDto dto) {

		String url = eaeWsBaseUrl + sirhSetCampagneEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public CampagneEaeDto getCampagneAnneePrecedente(Integer idAgentSirh, Integer anneePrecedente) {

		String url = eaeWsBaseUrl + sirhCampagneAnneePrecedenteUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("anneePrecedente", anneePrecedente.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(CampagneEaeDto.class, res, url);
	}

	@Override
	public EaeDocumentDto getDocumentEaeByIdDocument(Integer idAgentSirh, Integer idDocument) {

		String url = eaeWsBaseUrl + sirhDocumentEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idDocument", idDocument.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(EaeDocumentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteDocumentEae(Integer idAgentSirh, Integer idEaeDocument) {

		String url = eaeWsBaseUrl + sirhDeleteDocumentEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idEaeDocument", idEaeDocument.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setEae(Integer idAgentSirh, EaeDto eaeDto) {

		String url = eaeWsBaseUrl + sirhSetEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(eaeDto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<EaeDto> getListeEaeDto(Integer idAgentSirh, FormRehercheGestionEae form) {

		String url = eaeWsBaseUrl + sirhListeEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(form));
		return readResponseAsList(EaeDto.class, res, url);
	}

	@Override
	public EaeCampagneTaskDto findEaeCampagneTaskByIdCampagneEae(Integer idAgentSirh, Integer idEaeCampagne) {

		String url = eaeWsBaseUrl + sirhEaeCampagneTaskUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEaeCampagne", idEaeCampagne.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(EaeCampagneTaskDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveEaeCampagneTask(Integer idAgentSirh, EaeCampagneTaskDto campagneTaskDto) {

		String url = eaeWsBaseUrl + sirhEaeCampagneTaskUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(campagneTaskDto));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto updateEae(Integer idAgentSirh, Integer idEae) {

		String url = eaeWsBaseUrl + sirhUpdateEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public String getLastDocumentEaeFinalise(Integer idAgentSirh, Integer idEae) {

		String url = eaeWsBaseUrl + sirhLastDocumentEaeFinaliseUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(String.class, res, url);
	}

	@Override
	public ReturnMessageDto updateCapEae(Integer idAgentSirh, Integer idEae, Boolean cap) {

		String url = eaeWsBaseUrl + sirhUpdateCapEaeUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("idEae", idEae.toString());
		params.put("cap", cap.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<EaeDashboardItemDto> getEaesDashboard(Integer idAgentSirh, Integer annee) {

		String url = eaeWsBaseUrl + sirhTableauDeBordUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentSirh", idAgentSirh.toString());
		params.put("annee", annee.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(EaeDashboardItemDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveDateEvaluateurFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evaluateur) {

		String url = eaeWsBaseUrl + saveDateEvaluateurFromSirhUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(evaluateur));
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveDateEvalueFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evalue) {

		String url = eaeWsBaseUrl + saveDateEvalueFromSirhUrl;

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentSirh.toString());
		params.put("idEae", idEae.toString());

		ClientResponse res = createAndPostRequest(params, url,
				new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(evalue));
		return readResponse(ReturnMessageDto.class, res, url);
	}
}
