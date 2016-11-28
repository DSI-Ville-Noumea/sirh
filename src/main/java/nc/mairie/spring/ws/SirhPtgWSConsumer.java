package nc.mairie.spring.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import flexjson.JSONSerializer;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.CanStartVentilationDto;
import nc.mairie.gestionagent.pointage.dto.CanStartWorkflowPaieActionDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteAnneeDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteChoixAgentDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasDemandeDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;
import nc.mairie.metier.Const;

@Service
public class SirhPtgWSConsumer extends BaseWsConsumer implements ISirhPtgWSConsumer {

	@Autowired
	@Qualifier("ptgWsBaseUrl")
	private String ptgWsBaseUrl;

	// droits
	private static final String sirhPtgAgentsApprobateurs = "droits/approbateurs";
	private static final String sirhPtgDeleteApprobateurs = "droits/deleteApprobateurs";
	private static final String sirhPtgSauvegardeDelegataire = "droits/delegataire";
	private static final String ptgDroitsAgentsApprouvesUrl = "droits/agentsApprouves";
	private static final String ptgDroitsDelegataireOperateursUrl = "droits/delegataireOperateurs";
	private static final String ptgDroitsAgentsSaisisUrl = "droits/agentsSaisis";
	private static final String sirhDupliqueApprobateurUrl = "droits/dupliqueDroitsApprobateur";

	// Visualisation
	private static final String sirhPtgVisualisationPointage = "visualisation/pointagesSIRH";
	private static final String sirhPtgVisualisationHistory = "visualisation/historiqueSIRH";
	private static final String sirhPtgVisualisationSetState = "visualisation/changerEtatsSIRH";
	private static final String sirhPtgVisualisationIdAgentPointage = "visualisation/listeAgentsPointagesForSIRH";
	// saisie
	private static final String sirhPtgSaisie = "saisie/ficheSIRH";
	// ventilation
	private static final String sirhPtgStartVentilation = "ventilation/start";
	private static final String sirhPtgVentilationsShow = "ventilation/show";
	private static final String sirhPtgVentilationsHistory = "ventilation/showHistory";
	private static final String sirhPtgCheckVentil = "ventilation/canStartVentilation";
	private static final String sirhPtgCheckIsVentilRuning = "ventilation/isVentilation";
	private static final String sirhPtgVentilationEnCours = "ventilation/getVentilationEnCours";
	private static final String sirhPtgErreursVentilation = "ventilation/getErreursVentilation";
	private static final String sirhPtgListeAgentsForShowVentilation = "ventilation/listeAgentsToShowVentilation";

	// export paie
	private static final String sirhPtgCheckValid = "exportPaie/canStartExportPaie";
	private static final String sirhPtgCheckIsValidRuning = "exportPaie/isExportPaie";
	private static final String sirhPtgStartDeversement = "exportPaie/start";
	// etats du payeur
	private static final String sirhPtgCanStartExportEtatsPayeur = "etatsPayeur/canStartExportEtatsPayeur";
	private static final String sirhPtgListEtatsPayeur = "etatsPayeur/listEtatsPayeur";
	private static final String sirhPtgDownloadFicheEtatsPayeur = "etatsPayeur/downloadFicheEtatsPayeur";
	private static final String sirhPtgStartExportEtatsPayeur = "etatsPayeur/start";
	// filtres
	private static final String sirhPtgEtatsPointage = "filtres/getEtats";
	private static final String sirhPtgTypesPointage = "filtres/getTypes";
	private static final String sirhPtgTypeAbsence = "filtres/getTypesAbsence";
	private static final String sirhPtgMotifHeureSup = "filtres/getMotifHsup";
	private static final String sirhPtgSaveMotifHeureSup = "filtres/setMotifHsup";
	// primes
	private static final String sirhPtgPrimesStatut = "primes/getListePrimeWithStatus";
	private static final String sirhPtgPrimes = "primes/getListePrime";
	private static final String sirhPtgPrimeDetail = "primes/getPrime";
	private static final String sirhPtgPrimeDetailFromIdRefPrime = "primes/getPrimeFromIdRefPrime";
	private static final String sirhPtgPrimePointee = "primes/isPrimeUtilisee";

	// titre repas
	private static final String sirhPtgVisualisationTitreRepasHistory = "titreRepas/historique";
	private static final String sirhPtglistTitreRepas = "titreRepas/listTitreRepas";
	private static final String sirhPtgEnregistreTitreRepas = "titreRepas/enregistreListTitreDemande";
	private static final String sirhPtgUpdateEtatForListTitreRepasDemande = "titreRepas/updateEtatForListTitreRepasDemande";
	private static final String sirhPtgFiltreEtatTitreRepas = "titreRepas/getEtats";
	private static final String sirhPtgFiltreDateMoisTitreRepas = "titreRepas/getListeMoisTitreRepasSaisie";
	private static final String sirhPtgGenereEtatPayeurTitreRepas = "titreRepas/genereEtatPayeur";
	private static final String sirhPtgListTitreRepasEtatPayeur = "titreRepas/listTitreRepasEtatPayeur";
	
	// Prime DPM #30544 */
	private static final String ptgSaveListIndemniteChoixAgentUrl = "dpm/saveIndemniteChoixAgentForSIRH";
	private static final String ptgListDpmIndemniteChoixAgentForSIRHUrl = "dpm/listDpmIndemniteChoixAgentForSIRH";
	private static final String ptgCreateDpmIndemAnneeUrl = "dpm/createDpmIndemAnnee";
	private static final String ptgSaveDpmIndemAnneeUrl = "dpm/saveDpmIndemAnnee";
	private static final String ptgListDpmIndemAnneeUrl = "dpm/listDpmIndemAnnee";
	private static final String ptgGetDpmIndemAnneeUrl = "dpm/getDpmIndemAnneeByAnnee";
	private static final String ptgListDpmIndemAnneeOuverteUrl = "dpm/listDpmIndemAnneeOuverte";
	private static final String ptgDeleteIndemniteChoixAgentUrl = "dpm/deleteIndemniteChoixAgent";
	
	private Logger logger = LoggerFactory.getLogger(SirhPtgWSConsumer.class);

	@Override
	public List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + sirhPtgAgentsApprobateurs);
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
		String url = String.format(ptgWsBaseUrl + sirhPtgAgentsApprobateurs);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteApprobateur(String json) {
		String url = String.format(ptgWsBaseUrl + sirhPtgDeleteApprobateurs);
		ClientResponse res = createAndPostRequest(url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public FichePointageDto getSaisiePointage(Integer idAgent, String monday) {
		String url = String.format(ptgWsBaseUrl + sirhPtgSaisie);
		HashMap<String, String> params = new HashMap<>();
		idAgent = idAgent.toString().startsWith("900") ? idAgent : Integer.valueOf("900" + idAgent);
		params.put("date", monday);
		params.put("idAgent", idAgent.toString());
		logger.debug("Call " + url + " with " + idAgent + ", " + monday);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(FichePointageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setPtgState(ArrayList<Integer> idPtgs, Integer idRefEtat, Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVisualisationSetState);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		StringBuilder json = new StringBuilder("[");
		for (Integer id : idPtgs) {
			json.append("{\"idPointage\" : " + id + ",\"idRefEtat\" : " + idRefEtat + "},");
		}
		if (idPtgs.size() > 0) {
			json.substring(0, json.length() - 1);
		}
		json.append("]");
		ClientResponse res = createAndPostRequest(params, url, json.toString());
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<ConsultPointageDto> getVisualisationHistory(Integer idPointage) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVisualisationHistory);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idPointage", idPointage.toString());
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(ConsultPointageDto.class, res, url);
	}

	@Override
	public List<TitreRepasDemandeDto> getVisualisationTitreRepasHistory(Integer idTrDemande) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVisualisationTitreRepasHistory);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idTrDemande", idTrDemande.toString());
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(TitreRepasDemandeDto.class, res, url);
	}

	@Override
	public List<TitreRepasDemandeDto> getListTitreRepas(Integer idAgentConnecte, String fromDate, String toDate, Integer idRefEtat, Boolean commande, String dateMonth, Integer idServiceAds,
			Integer idAgent, List<Integer> listIdsAgent) {

		String url = String.format(ptgWsBaseUrl + sirhPtglistTitreRepas);

		Map<String, String> parameters = new HashMap<String, String>();

		if (idAgentConnecte != null) {
			parameters.put("idAgentConnecte", idAgentConnecte.toString());
		}
		if (fromDate != null) {
			parameters.put("fromDate", fromDate);
		}
		if (toDate != null) {
			parameters.put("toDate", toDate);
		}
		if (idRefEtat != null) {
			parameters.put("etat", idRefEtat.toString());
		}
		if (commande != null) {
			parameters.put("commande", commande.toString());
		}
		if (dateMonth != null) {
			parameters.put("dateMonth", dateMonth);
		}
		if (idServiceAds != null) {
			parameters.put("idServiceADS", idServiceAds.toString());
		}
		if (idAgent != null) {
			parameters.put("idAgent", idAgent.toString());
		}
		if (null != listIdsAgent && !listIdsAgent.isEmpty()) {

			String csvId = Const.CHAINE_VIDE;
			for (Integer id : listIdsAgent) {
				csvId += id + ",";
			}
			if (csvId != Const.CHAINE_VIDE) {
				csvId = csvId.substring(0, csvId.length() - 1);
			}
			parameters.put("listIdsAgent", csvId);
		}
		parameters.put("isFromSIRH", "true");

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(TitreRepasDemandeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto enregistreTitreRepas(List<TitreRepasDemandeDto> dto, Integer idAgent) {

		String url = String.format(ptgWsBaseUrl + sirhPtgEnregistreTitreRepas);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgentConnecte", idAgent.toString());
		parameters.put("isFromSIRH", "true");

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(parameters, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<RefEtatDto> getEtatsTitreRepas() {

		String url = String.format(ptgWsBaseUrl + sirhPtgFiltreEtatTitreRepas);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

		return readResponseAsList(RefEtatDto.class, res, url);
	}

	@Override
	public List<Date> getFiltreListeMois() {

		String url = String.format(ptgWsBaseUrl + sirhPtgFiltreDateMoisTitreRepas);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

		// cas particulier des dates
		return readResponseAsListDate(res, url);
	}

	@Override
	public ReturnMessageDto genereEtatPayeurTitreRepas(Integer idAgent) {

		String url = String.format(ptgWsBaseUrl + sirhPtgGenereEtatPayeurTitreRepas);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeur(Integer idAgent) {

		String url = String.format(ptgWsBaseUrl + sirhPtgListTitreRepasEtatPayeur);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(TitreRepasEtatPayeurDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setTRState(List<TitreRepasDemandeDto> listTitreRepasDemandeDto, Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + sirhPtgUpdateEtatForListTitreRepasDemande);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listTitreRepasDemandeDto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents, Integer idRefEtat, Integer idRefType, String typeHeureSup, String dateEtat) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVisualisationPointage);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("from", fromDate);
		if (toDate != null) {
			parameters.put("to", toDate);
		}
		if (idAgents != null) {
			String csvId = Const.CHAINE_VIDE;
			for (String id : idAgents) {
				csvId += id + ",";
			}
			if (csvId != Const.CHAINE_VIDE) {
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
		if (typeHeureSup != null) {
			parameters.put("typeHS", typeHeureSup);
		}
		if (dateEtat != null) {
			parameters.put("dateEtat", dateEtat);
		}

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(ConsultPointageDto.class, res, url);
	}

	@Override
	public List<RefEtatDto> getEtatsPointage() {
		String url = String.format(ptgWsBaseUrl + sirhPtgEtatsPointage);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);

		return readResponseAsList(RefEtatDto.class, res, url);
	}

	@Override
	public List<RefTypePointageDto> getTypesPointage() {
		String url = String.format(ptgWsBaseUrl + sirhPtgTypesPointage);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(RefTypePointageDto.class, res, url);
	}

	@Override
	public List<RefPrimeDto> getPrimes(String agentStatus) {
		String url = String.format(ptgWsBaseUrl + sirhPtgPrimesStatut);
		HashMap<String, String> params = new HashMap<>();
		params.put("statutAgent", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefPrimeDto.class, res, url);
	}

	@Override
	public List<RefPrimeDto> getPrimes() {
		String url = String.format(ptgWsBaseUrl + sirhPtgPrimes);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(RefPrimeDto.class, res, url);
	}

	@Override
	public RefPrimeDto getPrimeDetail(Integer numRubrique) {
		String url = String.format(ptgWsBaseUrl + sirhPtgPrimeDetail);
		HashMap<String, String> params = new HashMap<>();
		params.put("noRubr", numRubrique.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(RefPrimeDto.class, res, url);
	}

	@Override
	public RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime) {
		String url = String.format(ptgWsBaseUrl + sirhPtgPrimeDetailFromIdRefPrime);
		HashMap<String, String> params = new HashMap<>();
		params.put("idRefPrime", idRefPrime.toString());
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(RefPrimeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto toSerialize) {
		String url = String.format(ptgWsBaseUrl + sirhPtgSaisie);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(toSerialize));
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + sirhPtgPrimePointee);
		HashMap<String, String> params = new HashMap<>();
		params.put("noRubr", numRubrique.toString());
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public <T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage, String agentsJson, boolean allVentilation) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVentilationsShow);
		HashMap<String, String> params = new HashMap<>();
		params.put("idDateVentil", idDateVentil.toString());
		params.put("typePointage", idRefTypePointage.toString());
		params.put("allVentilation", String.valueOf(allVentilation));
		if (agentsJson.equals("[]"))
			return new ArrayList<T>();
		ClientResponse res = createAndPostRequest(params, url, agentsJson);
		return readResponseAsList(targetClass, res, url);
	}

	@Override
	public <T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee, Integer idRefTypePointage, Integer idAgent, boolean allVentilation, Integer idVentilDate) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVentilationsHistory);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("mois", mois.toString());
		parameters.put("annee", annee.toString());
		parameters.put("typePointage", idRefTypePointage.toString());
		parameters.put("idAgent", idAgent.toString());
		parameters.put("allVentilation", String.valueOf(allVentilation));
		parameters.put("idVentilDate", String.valueOf(idVentilDate));
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(targetClass, res, url);
	}

	@Override
	public boolean isValidAvailable(String agentStatus) {
		String url = String.format(ptgWsBaseUrl + sirhPtgCheckValid);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			return false;
		}
	}

	@Override
	public boolean isVentilAvailable(String agentStatus) {
		String url = String.format(ptgWsBaseUrl + sirhPtgCheckVentil);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartVentilationDto result = readResponse(CanStartVentilationDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartVentilation();
		} else {
			return false;
		}
	}

	@Override
	public VentilDateDto getVentilationEnCours(String statut) {
		String url = String.format(ptgWsBaseUrl + sirhPtgVentilationEnCours);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			VentilDateDto result = readResponse(VentilDateDto.class, res, url);
			logger.trace(result.toString());
			return result;
		} else {
			return null;
		}
	}

	@Override
	public boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut, String idRefTypePointage) {
		String url = String.format(ptgWsBaseUrl + sirhPtgStartVentilation);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("date", sdf.format(dateVentilation));
		params.put("statut", statut);
		if (idRefTypePointage != null)
			params.put("typePointage", idRefTypePointage);
		ClientResponse res = createAndPostRequest(params, url, agentsJson);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("StartVentilation NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public boolean startDeversementPaie(Integer idAgent, String statut) {
		String url = String.format(ptgWsBaseUrl + sirhPtgStartDeversement);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("StartDeversement NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public boolean canStartExportEtatsPayeur(String statut) {
		String url = String.format(ptgWsBaseUrl + sirhPtgCanStartExportEtatsPayeur);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", statut);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			logger.debug("StartExportEtatsPayeur NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut) {
		String url = String.format(ptgWsBaseUrl + sirhPtgListEtatsPayeur);
		HashMap<String, String> params = new HashMap<>();
		params.put("statutAgent", statut);
		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(EtatsPayeurDto.class, res, url);
	}

	@Override
	public byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur) {
		String url = String.format(ptgWsBaseUrl + sirhPtgDownloadFicheEtatsPayeur);
		HashMap<String, String> params = new HashMap<>();
		params.put("idEtatPayeur", idEtatPayeur.toString());
		ClientResponse res = createAndFireRequest(params, url);

		try {
			return readResponseAsByteArray(res, url);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean startExportEtatsPayeur(Integer idAgentExporting, String statutString) {
		String url = String.format(ptgWsBaseUrl + sirhPtgStartExportEtatsPayeur);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgentExporting.toString());
		params.put("statut", statutString);
		ClientResponse res = createAndFireRequest(params, url);

		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			logger.debug("startExportEtatsPayeur NON OK : " + res.getStatus());
			return false;
		}
	}

	@Override
	public ArrayList<Integer> getListeIdAgentPointage() {
		String url = String.format(ptgWsBaseUrl + sirhPtgVisualisationIdAgentPointage);
		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		List<AgentDto> liste = readResponseAsList(AgentDto.class, res, url);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (AgentDto agDto : liste) {
			result.add(agDto.getIdAgent());
		}
		return result;
	}

	@Override
	public List<VentilErreurDto> getErreursVentilation(String type) {
		String url = String.format(ptgWsBaseUrl + sirhPtgErreursVentilation);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("statut", type);
		ClientResponse res = createAndFireRequest(parameters, url);
		return readResponseAsList(VentilErreurDto.class, res, url);
	}

	@Override
	public List<TypeAbsenceDto> getListeRefTypeAbsence() {
		String url = String.format(ptgWsBaseUrl + sirhPtgTypeAbsence);
		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(TypeAbsenceDto.class, res, url);
	}

	@Override
	public boolean isValidEnCours(String agentStatus) {
		String url = String.format(ptgWsBaseUrl + sirhPtgCheckIsValidRuning);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartWorkflowPaieActionDto result = readResponse(CanStartWorkflowPaieActionDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartAction();
		} else {
			return false;
		}
	}

	@Override
	public boolean isVentilEnCours(String agentStatus) {
		String url = String.format(ptgWsBaseUrl + sirhPtgCheckIsVentilRuning);
		HashMap<String, String> params = new HashMap<>();
		params.put("statut", agentStatus);
		ClientResponse res = createAndFireRequest(params, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			CanStartVentilationDto result = readResponse(CanStartVentilationDto.class, res, url);
			logger.trace(result.toString());
			return result.isCanStartVentilation();
		} else {
			return false;
		}
	}

	@Override
	public List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil, Integer idRefTypePointage, String statut, Date ventilationDate, String agentMin, String agentMax, boolean allVentilation) {
		String url = String.format(ptgWsBaseUrl + sirhPtgListeAgentsForShowVentilation);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		HashMap<String, String> params = new HashMap<>();
		params.put("idDateVentil", idDateVentil.toString());
		params.put("typePointage", idRefTypePointage.toString());
		params.put("ventilationDate", sdf.format(ventilationDate));
		params.put("statut", statut);
		params.put("agentMin", agentMin);
		params.put("agentMax", agentMax);
		params.put("allVentilation", String.valueOf(allVentilation));

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(Integer.class, res, url);
	}

	@Override
	public List<MotifHeureSupDto> getListeMotifHeureSup() {
		String url = String.format(ptgWsBaseUrl + sirhPtgMotifHeureSup);

		ClientResponse res = createAndFireRequest(new HashMap<String, String>(), url);
		return readResponseAsList(MotifHeureSupDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveMotifHeureSup(String json) {
		String url = String.format(ptgWsBaseUrl + sirhPtgSaveMotifHeureSup);
		HashMap<String, String> params = new HashMap<>();
		ClientResponse res = createAndPostRequest(params, url, json);

		ReturnMessageDto message = new ReturnMessageDto();

		if (res.getStatus() == HttpStatus.CONFLICT.value()) {
			message.getErrors().add("Le motif n'a pu être sauvegardé.");
		}

		return message;
	}

	@Override
	public ReturnMessageDto setDelegataire(Integer idAgent, String json) {
		String url = String.format(ptgWsBaseUrl + sirhPtgSauvegardeDelegataire);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponseWithReturnMessageDto(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getApprovedAgents(Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsAgentsApprouvesUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public DelegatorAndOperatorsDto getDelegateAndOperator(Integer idAgent) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsDelegataireOperateursUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(DelegatorAndOperatorsDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveApprovedAgents(Integer idAgent, List<AgentDto> listSelect) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsAgentsApprouvesUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);

		ReturnMessageDto dto = new ReturnMessageDto();
		try {
			readResponse( res, url);
		} catch (BaseWsConsumerException e) {
			dto.setErrors(Arrays.asList("Une erreur est survenue lors de la sauvegarde des agents à approuver."));
		}

		return dto;
	}

	@Override
	public ReturnMessageDto saveDelegateAndOperator(Integer idAgent, DelegatorAndOperatorsDto dto) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsDelegataireOperateursUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<AgentDto> getAgentsSaisisOperateur(Integer idAgent, Integer idOperateur) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsAgentsSaisisUrl);

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("idOperateur", idOperateur.toString());

		ClientResponse res = createAndFireRequest(params, url);
		return readResponseAsList(AgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveAgentsSaisisOperateur(Integer idAgent, Integer idOperateur, List<AgentDto> listSelect) {
		String url = String.format(ptgWsBaseUrl + ptgDroitsAgentsSaisisUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		params.put("idOperateur", idOperateur.toString());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listSelect);

		ClientResponse res = createAndPostRequest(params, url, json);
		ReturnMessageDto dto = new ReturnMessageDto();
		try {
			readResponse( res, url);
		} catch (BaseWsConsumerException e) {
			dto.setErrors(Arrays.asList("Une erreur est survenue lors de la sauvegarde des agents à approuver."));
		}

		return dto;
	}

	@Override
	public ReturnMessageDto dupliqueApprobateur(Integer idAgentConnecte, Integer idAgentSource, Integer idAgentDestinataire) {
		
		String url = String.format(ptgWsBaseUrl + sirhDupliqueApprobateurUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgentConnecte.toString());
		params.put("idAgentSource", idAgentSource.toString());
		params.put("idAgentDest", idAgentDestinataire.toString());
		
		ClientResponse res = createAndPostRequest(params, url, null);
		return readResponse(ReturnMessageDto.class, res, url);
	}
	
	///////////////// PRIME DPM ////////////

	@Override
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(Integer idAgent, Integer annee,
			Boolean isChoixIndemnite, Boolean isChoixRecuperation,
			List<Integer> listIdsAgent) {

		String url = String.format(ptgWsBaseUrl + ptgListDpmIndemniteChoixAgentForSIRHUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgent.toString());
		
		if(null != annee)
			params.put("annee", annee.toString());
		
		if (null != listIdsAgent && !listIdsAgent.isEmpty()) {

			String csvId = Const.CHAINE_VIDE;
			for (Integer id : listIdsAgent) {
				csvId += id + ",";
			}
			if (csvId != Const.CHAINE_VIDE) {
				csvId = csvId.substring(0, csvId.length() - 1);
			}
			params.put("listIdsAgent", csvId);
		}
		
		if(null != isChoixIndemnite)
			params.put("isChoixIndemnite", isChoixIndemnite.toString());
		
		if(null != isChoixRecuperation)
			params.put("isChoixRecuperation", isChoixRecuperation.toString());

		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(DpmIndemniteChoixAgentDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveIndemniteChoixAgent(Integer idAgentConnecte, Integer annee, DpmIndemniteChoixAgentDto listDto) {
		
		String url = String.format(ptgWsBaseUrl + ptgSaveListIndemniteChoixAgentUrl);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgentConnecte.toString());
		params.put("annee", annee.toString());

		String json = new JSONSerializer()
				.exclude("*.class").exclude("*.choix")
				.exclude("*.civilite").exclude("*.selectedDroitAbs").exclude("*.position")
				.exclude("*.signature").exclude("*.statut")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(listDto);
		
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(Integer idAgent) {

		String url = String.format(ptgWsBaseUrl + ptgListDpmIndemAnneeUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgent.toString());

		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(DpmIndemniteAnneeDto.class, res, url);
	}

	@Override
	public DpmIndemniteAnneeDto getDpmIndemAnneeByAnnee(Integer annee) {

		String url = String.format(ptgWsBaseUrl + ptgGetDpmIndemAnneeUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("annee", annee.toString());

		ClientResponse res = createAndFireRequest(params, url);

		return readResponse(DpmIndemniteAnneeDto.class, res, url);
	}

	@Override
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnneeOuverte() {

		String url = String.format(ptgWsBaseUrl + ptgListDpmIndemAnneeOuverteUrl);
		
		HashMap<String, String> params = new HashMap<>();

		ClientResponse res = createAndFireRequest(params, url);

		return readResponseAsList(DpmIndemniteAnneeDto.class, res, url);
	}

	@Override
	public ReturnMessageDto createDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto) {
		
		String url = String.format(ptgWsBaseUrl + ptgCreateDpmIndemAnneeUrl);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgentConnecte.toString());

		String json = new JSONSerializer()
				.exclude("*.class").exclude("*.radioButtonZK")
				.exclude("*.civilite").exclude("*.selectedDroitAbs").exclude("*.position")
				.exclude("*.signature").exclude("*.statut")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(dto);
		
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto) {
		
		String url = String.format(ptgWsBaseUrl + ptgSaveDpmIndemAnneeUrl);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgentConnecte.toString());

		String json = new JSONSerializer()
				.exclude("*.class").exclude("*.radioButtonZK")
				.exclude("*.civilite").exclude("*.selectedDroitAbs").exclude("*.position")
				.exclude("*.signature").exclude("*.statut")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(dto);
		
		ClientResponse res = createAndPostRequest(params, url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}
	
	@Override
	public ReturnMessageDto deleteIndemniteChoixAgent(Integer idAgentConnecte, Integer idDpmIndemChoixAgent) {
		
		String url = String.format(ptgWsBaseUrl + ptgDeleteIndemniteChoixAgentUrl);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgentConnecte", idAgentConnecte.toString());
		params.put("idDpmIndemChoixAgent", idDpmIndemChoixAgent.toString());
		
		ClientResponse res = createAndFireRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

}
