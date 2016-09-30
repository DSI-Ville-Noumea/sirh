package nc.mairie.gestionagent.eae.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeCampagneTaskDto {
	
	private Integer idEaeCampagneTask;
	private Integer idEaeCampagne;
	private Integer annee;
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateCalculEae;
	private String taskStatus;
	
	public Integer getIdEaeCampagneTask() {
		return idEaeCampagneTask;
	}
	public void setIdEaeCampagneTask(Integer idEaeCampagneTask) {
		this.idEaeCampagneTask = idEaeCampagneTask;
	}
	
	public Integer getIdEaeCampagne() {
		return idEaeCampagne;
	}
	public void setIdEaeCampagne(Integer idEaeCampagne) {
		this.idEaeCampagne = idEaeCampagne;
	}
	public Integer getAnnee() {
		return annee;
	}
	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public Date getDateCalculEae() {
		return dateCalculEae;
	}
	public void setDateCalculEae(Date dateCalculEae) {
		this.dateCalculEae = dateCalculEae;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	
}
