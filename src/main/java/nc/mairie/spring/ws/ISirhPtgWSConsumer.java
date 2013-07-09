package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;

public interface ISirhPtgWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, String codeService, Integer agentFrom, Integer agentTo,
			Integer idRefEtat, Integer idRefType);
}
