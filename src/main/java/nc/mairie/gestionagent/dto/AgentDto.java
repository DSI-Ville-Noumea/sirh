package nc.mairie.gestionagent.dto;

import nc.mairie.metier.agent.AgentNW;

public class AgentDto {

	private String nom;
	private String prenom;
	private Integer idAgent;

	public AgentDto() {

	}

	public AgentDto(AgentNW agent) {
		nom = agent.getNomAgent();
		prenom = agent.getPrenomAgent();
		idAgent = Integer.valueOf(agent.getIdAgent());
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
}
