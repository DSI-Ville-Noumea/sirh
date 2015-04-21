package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import nc.mairie.gestionagent.dto.AgentDto;

public class MoisAlimAutoCongesAnnuelsDto {

	private Date dateMois;

	private AgentDto agent;
	private Date dateModification;
	private String status;
	private String infos;
	private Double nbJours;

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

	public String getInfos() {
		return infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

	public Double getNbJours() {
		return nbJours;
	}

	public void setNbJours(Double nbJours) {
		this.nbJours = nbJours;
	}

}
