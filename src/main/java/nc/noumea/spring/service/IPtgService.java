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
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;

public interface IPtgService {

	ReturnMessageDto saveAgentsSaisisOperateur(Integer idAgent, Integer idOperateur, List<AgentDto> listSelect);

	List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent);

	ReturnMessageDto setApprobateur(String json);

	ReturnMessageDto deleteApprobateur(String json);

	ReturnMessageDto setDelegataire(Integer idAgent, String json);

	DelegatorAndOperatorsDto getDelegateAndOperator(Integer idAgent);

	List<AgentDto> getApprovedAgents(Integer idAgent);

	ReturnMessageDto saveApprovedAgents(Integer idAgent, List<AgentDto> listeAgentsApprobateurPtg);

	ReturnMessageDto saveDelegateAndOperator(Integer idAgent, DelegatorAndOperatorsDto dto);

	Object getAgentsSaisisOperateur(Integer idAgent, Integer idOperateur);

	boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent);

	RefPrimeDto getPrimeDetail(Integer numRubrique);

	List<RefPrimeDto> getPrimes();

	boolean canStartExportEtatsPayeur(String statut);

	List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut);

	boolean startExportEtatsPayeur(Integer idAgent, String statut);

	List<TypeAbsenceDto> getListeRefTypeAbsence();

	ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto listeFichePointage);

	FichePointageDto getSaisiePointage(Integer idAgent, String date);

	List<MotifHeureSupDto> getListeMotifHeureSup();

	VentilDateDto getVentilationEnCours(String statut);

	boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut,
			String idRefTypePointage);

	List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil, Integer idRefTypePointage, String statut,
			Date ventilationDate, String agentMin, String agentMax, boolean allVentilation);

	boolean startDeversementPaie(Integer idAgent, String statut);

	<T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage,
			String agentsJson, boolean allVentilation);

	<T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee, Integer idRefTypePointage,
			Integer idAgent, boolean allVentilation, Integer idVentilDate);

	List<RefEtatDto> getEtatsPointage();

	List<RefTypePointageDto> getTypesPointage();

	List<Integer> getListeIdAgentPointage();

	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents,
 Integer idRefEtat, Integer idRefType, String typeHeureSup, String dateEtat);

	ReturnMessageDto setPtgState(ArrayList<Integer> idPtg, Integer idRefEtat, Integer idAgent);

	List<ConsultPointageDto> getVisualisationHistory(Integer idAgents);

	byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur);

	RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime);

	boolean isVentilEnCours(String statut);

	boolean isValidEnCours(String statut);

	boolean isVentilAvailable(String statut);

	boolean isValidAvailable(String statut);

	List<VentilErreurDto> getErreursVentilation(String type);

	ReturnMessageDto saveMotifHeureSup(String json);

	RefPrimeDto getPrimeDetailFromRefPrimeOptimise(
			List<RefPrimeDto> listRefPrimeDto, Integer idRefPrime);
}
