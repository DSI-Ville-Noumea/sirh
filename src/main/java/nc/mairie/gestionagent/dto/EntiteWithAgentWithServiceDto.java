package nc.mairie.gestionagent.dto;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;

public class EntiteWithAgentWithServiceDto extends EntiteDto {

	private List<AgentWithServiceDto>			listAgentWithServiceDto;

	private List<EntiteWithAgentWithServiceDto>	entiteEnfantWithAgents;

	public EntiteWithAgentWithServiceDto() {
		listAgentWithServiceDto = new ArrayList<AgentWithServiceDto>();
		entiteEnfantWithAgents = new ArrayList<EntiteWithAgentWithServiceDto>();
	}

	public List<AgentWithServiceDto> getListAgentWithServiceDto() {
		return listAgentWithServiceDto;
	}

	public void setListAgentWithServiceDto(List<AgentWithServiceDto> listAgentWithServiceDto) {
		this.listAgentWithServiceDto = listAgentWithServiceDto;
	}

	public List<EntiteWithAgentWithServiceDto> getEntiteEnfantWithAgents() {
		return entiteEnfantWithAgents;
	}

	public void setEntiteEnfantWithAgents(List<EntiteWithAgentWithServiceDto> entiteEnfantWithAgents) {
		this.entiteEnfantWithAgents = entiteEnfantWithAgents;
	}
}
