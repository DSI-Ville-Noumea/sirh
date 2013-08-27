package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.dto.FichePointageDto;
import nc.mairie.gestionagent.dto.RefEtatDto;
import nc.mairie.gestionagent.dto.RefPrimeDto;
import nc.mairie.gestionagent.dto.RefTypePointageDto;

import com.sun.jersey.api.client.ClientResponse;

public interface ISirhPtgWSConsumer {

    List<AgentWithServiceDto> getApprobateurs();

    List<AgentWithServiceDto> setApprobateurs(String json);

    List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents, Integer idRefEtat, Integer idRefType);

    FichePointageDto getSaisiePointage(String idAgent, String monday);

    ClientResponse setSaisiePointage(String idAgent, FichePointageDto toSerialize);

    ClientResponse setPtgState(ArrayList<Integer> idPtg, int idRefEtat, String idagent);

    List<ConsultPointageDto> getVisualisationHistory(int idAgents);

    List<RefPrimeDto> getPrimes(String agentStatus);

    List<RefPrimeDto> getPrimes();

    List<RefEtatDto> getEtatsPointage();

    List<RefTypePointageDto> getTypesPointage();

    RefPrimeDto getPrimeDetail(Integer numRubrique);

    RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime);

    boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent);
}
