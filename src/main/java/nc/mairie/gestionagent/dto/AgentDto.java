package nc.mairie.gestionagent.dto;

import nc.mairie.metier.agent.Agent;

public class AgentDto {

	private String nom;
	private String prenom;
	private Integer idAgent;

	public AgentDto() {

	}

	public AgentDto(Agent agent) {
		nom = agent.getNomAgent();
		prenom = agent.getPrenomAgent();
		idAgent = agent.getIdAgent();
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

	@Override
	public boolean equals(Object obj) {
		return idAgent.toString().equals(((AgentDto) obj).getIdAgent().toString());
	}
}
