package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;

import com.sun.jersey.api.client.ClientResponse;

public interface ISirhPtgWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	ClientResponse setApprobateurs(String json);
}
