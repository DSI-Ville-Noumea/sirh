package nc.mairie.gestionagent.dto;

import java.util.ArrayList;
import java.util.List;

public class DelegatorAndOperatorsDto {

	private AgentDto delegataire;
	private List<AgentDto> saisisseurs;

	public DelegatorAndOperatorsDto() {
		saisisseurs = new ArrayList<AgentDto>();
	}

	public AgentDto getDelegataire() {
		return delegataire;
	}

	public void setDelegataire(AgentDto delegataire) {
		this.delegataire = delegataire;
	}

	public List<AgentDto> getSaisisseurs() {
		return saisisseurs;
	}

	public void setSaisisseurs(List<AgentDto> saisisseurs) {
		this.saisisseurs = saisisseurs;
	}
}
