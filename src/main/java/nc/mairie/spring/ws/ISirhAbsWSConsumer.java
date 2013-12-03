package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;

public interface ISirhAbsWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);
}
