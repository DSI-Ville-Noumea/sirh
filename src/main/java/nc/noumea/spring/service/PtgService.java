package nc.noumea.spring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasDemandeDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;
import nc.mairie.spring.ws.ISirhPtgWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PtgService implements IPtgService {

	@Autowired
	private ISirhPtgWSConsumer ptgConsumer;

	@Override
	public ReturnMessageDto saveAgentsSaisisOperateur(Integer idAgent, Integer idOperateur, List<AgentDto> listSelect) {
		return ptgConsumer.saveAgentsSaisisOperateur(idAgent, idOperateur, listSelect);
	}

	@Override
	public List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent) {
		return ptgConsumer.getApprobateurs(idServiceADS, idAgent);
	}

	@Override
	public ReturnMessageDto setApprobateur(String json) {
		return ptgConsumer.setApprobateur(json);
	}

	@Override
	public ReturnMessageDto deleteApprobateur(String json) {
		return ptgConsumer.deleteApprobateur(json);
	}

	@Override
	public ReturnMessageDto setDelegataire(Integer idAgent, String json) {
		return ptgConsumer.setDelegataire(idAgent, json);
	}

	@Override
	public DelegatorAndOperatorsDto getDelegateAndOperator(Integer idAgent) {
		return ptgConsumer.getDelegateAndOperator(idAgent);
	}

	@Override
	public List<AgentDto> getApprovedAgents(Integer idAgent) {
		return ptgConsumer.getApprovedAgents(idAgent);
	}

	@Override
	public ReturnMessageDto saveApprovedAgents(Integer idAgent, List<AgentDto> listeAgentsApprobateurPtg) {
		return ptgConsumer.saveApprovedAgents(idAgent, listeAgentsApprobateurPtg);
	}

	@Override
	public ReturnMessageDto saveDelegateAndOperator(Integer idAgent, DelegatorAndOperatorsDto dto) {
		return ptgConsumer.saveDelegateAndOperator(idAgent, dto);
	}

	@Override
	public Object getAgentsSaisisOperateur(Integer idAgent, Integer idOperateur) {
		return ptgConsumer.getAgentsSaisisOperateur(idAgent, idOperateur);
	}

	@Override
	public boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent) {
		return ptgConsumer.isPrimeUtilPointage(numRubrique, idAgent);
	}

	@Override
	public RefPrimeDto getPrimeDetail(Integer numRubrique) {
		return ptgConsumer.getPrimeDetail(numRubrique);
	}

	@Override
	public List<RefPrimeDto> getPrimes() {
		return ptgConsumer.getPrimes();
	}

	@Override
	public boolean canStartExportEtatsPayeur(String statut) {
		return ptgConsumer.canStartExportEtatsPayeur(statut);
	}

	@Override
	public List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut) {
		return ptgConsumer.getListEtatsPayeurByStatut(statut);
	}

	@Override
	public boolean startExportEtatsPayeur(Integer idAgent, String statut) {
		return ptgConsumer.startExportEtatsPayeur(idAgent, statut);
	}

	@Override
	public List<TypeAbsenceDto> getListeRefTypeAbsence() {
		return ptgConsumer.getListeRefTypeAbsence();
	}

	@Override
	public ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto listeFichePointage) {
		return ptgConsumer.setSaisiePointage(idAgent, listeFichePointage);
	}

	@Override
	public FichePointageDto getSaisiePointage(Integer idAgent, String date) {
		return ptgConsumer.getSaisiePointage(idAgent, date);
	}

	@Override
	public List<MotifHeureSupDto> getListeMotifHeureSup() {
		return ptgConsumer.getListeMotifHeureSup();
	}

	@Override
	public VentilDateDto getVentilationEnCours(String statut) {
		return ptgConsumer.getVentilationEnCours(statut);
	}

	@Override
	public boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut,
			String idRefTypePointage) {
		return ptgConsumer.startVentilation(idAgent, dateVentilation, agentsJson, statut, idRefTypePointage);
	}

	@Override
	public List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil, Integer idRefTypePointage,
			String statut, Date ventilationDate, String agentMin, String agentMax, boolean allVentilation) {
		return ptgConsumer.getListeAgentsForShowVentilation(idDateVentil, idRefTypePointage, statut, ventilationDate,
				agentMin, agentMax, allVentilation);
	}

	@Override
	public boolean startDeversementPaie(Integer idAgent, String statut) {
		return ptgConsumer.startDeversementPaie(idAgent, statut);
	}

	@Override
	public List<RefEtatDto> getEtatsPointage() {
		return ptgConsumer.getEtatsPointage();
	}

	@Override
	public List<RefTypePointageDto> getTypesPointage() {
		return ptgConsumer.getTypesPointage();
	}

	@Override
	public List<Integer> getListeIdAgentPointage() {
		return ptgConsumer.getListeIdAgentPointage();
	}

	@Override
	public List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents,
			Integer idRefEtat, Integer idRefType, String typeHeureSup,String dateEtat) {
		return ptgConsumer.getVisualisationPointage(fromDate, toDate, idAgents, idRefEtat, idRefType, typeHeureSup,dateEtat);
	}

	@Override
	public ReturnMessageDto setPtgState(ArrayList<Integer> idPtg, Integer idRefEtat, Integer idAgent) {
		return ptgConsumer.setPtgState(idPtg, idRefEtat, idAgent);
	}

	@Override
	public ReturnMessageDto setTRState(List<TitreRepasDemandeDto> listTitreRepasDemandeDto, Integer idAgent) {
		return ptgConsumer.setTRState(listTitreRepasDemandeDto, idAgent);
	}

	@Override
	public List<ConsultPointageDto> getVisualisationHistory(Integer idAgents) {
		return ptgConsumer.getVisualisationHistory(idAgents);
	}

	@Override
	public List<TitreRepasDemandeDto> getVisualisationTitreRepasHistory(Integer idTrDemande) {
		return ptgConsumer.getVisualisationTitreRepasHistory(idTrDemande);
	}

	@Override
	public List<TitreRepasDemandeDto> getListTitreRepas(Integer idAgentConnecte, String fromDate, String toDate, 
			Integer idRefEtat, Boolean commande, String dateMonth, Integer idServiceAds, Integer idAgent, List<Integer> listIdsAgent) {
		return ptgConsumer.getListTitreRepas(idAgentConnecte, fromDate, toDate, idRefEtat, commande, dateMonth, idServiceAds, idAgent, listIdsAgent);
	}

	@Override
	public byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur) {
		return ptgConsumer.downloadFicheEtatsPayeur(idEtatPayeur);
	}

	@Override
	public RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime) {
		return ptgConsumer.getPrimeDetailFromRefPrime(idRefPrime);
	}
	
	@Override
	public RefPrimeDto getPrimeDetailFromRefPrimeOptimise(List<RefPrimeDto> listRefPrimeDto, Integer idRefPrime) {
		
		if(null != listRefPrimeDto
				&& !listRefPrimeDto.isEmpty()) {
			for(RefPrimeDto refPrimeDto : listRefPrimeDto) {
				if(refPrimeDto.getIdRefPrime().equals(idRefPrime)) {
					return refPrimeDto;
				}
			}
		}
		
		RefPrimeDto dto = getPrimeDetailFromRefPrime(idRefPrime);
		
		if(null != listRefPrimeDto
				&& null != dto) {
			listRefPrimeDto.add(dto);
		}
		
		return dto;
	}

	@Override
	public boolean isVentilEnCours(String statut) {
		return ptgConsumer.isVentilEnCours(statut);
	}

	@Override
	public boolean isValidEnCours(String statut) {
		return ptgConsumer.isValidEnCours(statut);
	}

	@Override
	public boolean isVentilAvailable(String statut) {
		return ptgConsumer.isVentilAvailable(statut);
	}

	@Override
	public boolean isValidAvailable(String statut) {
		return ptgConsumer.isValidAvailable(statut);
	}

	@Override
	public List<VentilErreurDto> getErreursVentilation(String type) {
		return ptgConsumer.getErreursVentilation(type);
	}

	@Override
	public <T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage,
			String agentsJson, boolean allVentilation) {
		return ptgConsumer.getVentilations(targetClass, idDateVentil, idRefTypePointage, agentsJson, allVentilation);
	}

	@Override
	public <T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee,
			Integer idRefTypePointage, Integer idAgent, boolean allVentilation, Integer idVentilDate) {
		return ptgConsumer.getVentilationsHistory(targetClass, mois, annee, idRefTypePointage, idAgent, allVentilation,
				idVentilDate);
	}

	@Override
	public ReturnMessageDto saveMotifHeureSup(String json) {
		return ptgConsumer.saveMotifHeureSup(json);
	}

}
