package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.dto.RefEtatDto;
import nc.mairie.gestionagent.dto.RefTypePointageDto;

public interface ISirhPtgWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, String codeService, Integer agentFrom, Integer agentTo,
			Integer idRefEtat, Integer idRefType);

	List<RefEtatDto> getEtatsPointage();

	List<RefTypePointageDto> getTypesPointage();
}
