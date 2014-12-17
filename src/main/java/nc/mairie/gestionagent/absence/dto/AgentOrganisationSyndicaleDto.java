package nc.mairie.gestionagent.absence.dto;

public class AgentOrganisationSyndicaleDto {

	private Integer idAgent;
	private boolean actif;

	public AgentOrganisationSyndicaleDto() {

	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	@Override
	public boolean equals(Object obj) {
		return idAgent.toString().equals(((AgentOrganisationSyndicaleDto) obj).getIdAgent().toString());
	}
}
