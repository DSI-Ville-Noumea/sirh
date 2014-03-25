package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;

import com.sun.jersey.api.client.ClientResponse;

public interface ISirhPtgWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents,
			Integer idRefEtat, Integer idRefType);

	FichePointageDto getSaisiePointage(String idAgent, String monday);

	ClientResponse setSaisiePointage(String idAgent, FichePointageDto toSerialize);

	ClientResponse setPtgState(ArrayList<Integer> idPtg, int idRefEtat, String idagent, String statutAgent);

	List<ConsultPointageDto> getVisualisationHistory(int idAgents);

	List<RefPrimeDto> getPrimes(String agentStatus);

	List<RefPrimeDto> getPrimes();

	List<RefEtatDto> getEtatsPointage();

	List<RefTypePointageDto> getTypesPointage();

	RefPrimeDto getPrimeDetail(Integer numRubrique);

	RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime);

	boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent);

	boolean isVentilAvailable(String agentStatus);

	VentilDateDto getVentilationEnCours(String statut);

	boolean startVentilation(String idAgent, Date dateVentilation, String agentsJson, String statut,
			String idRefTypePointage);

	boolean isValidAvailable(String agentStatus);

	boolean startDeversementPaie(String idAgent, String statut);

	boolean canStartExportEtatsPayeur(String statut);

	List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut);

	byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur);

	boolean startExportEtatsPayeur(String idAgentExporting, String statutString);

	public ArrayList<Integer> getListeIdAgentPointage();

	List<VentilErreurDto> getErreursVentilation(String type);

	List<TypeAbsenceDto> getListeRefTypeAbsence();
}
