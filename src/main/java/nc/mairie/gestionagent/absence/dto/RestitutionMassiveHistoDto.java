package nc.mairie.gestionagent.absence.dto;


public class RestitutionMassiveHistoDto {
	
	private Integer idAgent;
	private String status;
	private Double jours;
	
	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getJours() {
		return jours;
	}

	public void setJours(Double jours) {
		this.jours = jours;
	}
	
}
