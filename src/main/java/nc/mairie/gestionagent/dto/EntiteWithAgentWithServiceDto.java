package nc.mairie.gestionagent.dto;

/*
 * #%L
 * sirh-kiosque-j2ee
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;

public class EntiteWithAgentWithServiceDto extends EntiteDto {

	private List<AgentWithServiceDto> listAgentWithServiceDto;

	private List<EntiteWithAgentWithServiceDto> entiteEnfantWithAgents;
	
	public EntiteWithAgentWithServiceDto() {
		listAgentWithServiceDto = new ArrayList<AgentWithServiceDto>();
		entiteEnfantWithAgents = new ArrayList<EntiteWithAgentWithServiceDto>();
	}

	public List<AgentWithServiceDto> getListAgentWithServiceDto() {
		return listAgentWithServiceDto;
	}

	public void setListAgentWithServiceDto(
			List<AgentWithServiceDto> listAgentWithServiceDto) {
		this.listAgentWithServiceDto = listAgentWithServiceDto;
	}

	public List<EntiteWithAgentWithServiceDto> getEntiteEnfantWithAgents() {
		return entiteEnfantWithAgents;
	}

	public void setEntiteEnfantWithAgents(
			List<EntiteWithAgentWithServiceDto> entiteEnfantWithAgents) {
		this.entiteEnfantWithAgents = entiteEnfantWithAgents;
	}
}
