package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;

public interface ISirhPtgWSConsumer {

	public ArrayList<Integer> getListeIdAgentPointage();

	// Droits
	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	// Ventilation
	<T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage, String agentsJson);

	<T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee, Integer idRefTypePointage,
			Integer idAgent);

	boolean isVentilAvailable(String agentStatus);

	boolean isVentilEnCours(String agentStatus);

	VentilDateDto getVentilationEnCours(String statut);

	boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut,
			String idRefTypePointage);

	boolean isValidAvailable(String agentStatus);

	boolean isValidEnCours(String agentStatus);

	boolean startDeversementPaie(Integer idAgent, String statut);

	boolean canStartExportEtatsPayeur(String statut);

	List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut);

	byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur);

	boolean startExportEtatsPayeur(Integer idAgentExporting, String statutString);

	List<VentilErreurDto> getErreursVentilation(String type);

	// Visualisation
	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents,
			Integer idRefEtat, Integer idRefType);

	List<ConsultPointageDto> getVisualisationHistory(int idAgents);

	// Saisie
	FichePointageDto getSaisiePointage(Integer idAgent, String monday);

	ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto toSerialize);

	ReturnMessageDto setPtgState(ArrayList<Integer> idPtg, int idRefEtat, Integer idAgent, String statutAgent);

	// Filtres
	List<RefEtatDto> getEtatsPointage();

	List<RefTypePointageDto> getTypesPointage();

	List<TypeAbsenceDto> getListeRefTypeAbsence();

	// Primes
	List<RefPrimeDto> getPrimes(String agentStatus);

	List<RefPrimeDto> getPrimes();

	RefPrimeDto getPrimeDetail(Integer numRubrique);

	RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime);

	boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent);

	List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil,
			Integer idRefTypePointage, String statut, Date ventilationDate,
			String agentMin, String agentMax);

}
