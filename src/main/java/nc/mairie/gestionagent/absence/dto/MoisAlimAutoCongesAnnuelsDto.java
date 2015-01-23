package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import nc.mairie.gestionagent.dto.AgentDto;

public class MoisAlimAutoCongesAnnuelsDto {

	private Date dateMois;

	private AgentDto agent;
	private Date dateModification;
	private String status;

	public Date getDateMois() {
		return dateMois;
	}

	public void setDateMois(Date dateMois) {
		this.dateMois = dateMois;
	}

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
