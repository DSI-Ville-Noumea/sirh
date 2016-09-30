package nc.mairie.gestionagent.pointage.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class FichePointageDto {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateLundi;
	private AgentWithServiceDto agent;
	private String semaine;
	List<JourPointageDto> saisies;
	private boolean isDPM;
	private boolean isINASuperieur315;

	public FichePointageDto() {
		saisies = new ArrayList<JourPointageDto>();
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public String getSemaine() {
		return semaine;
	}

	public void setSemaine(String semaine) {
		this.semaine = semaine;
	}

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public List<JourPointageDto> getSaisies() {
		return saisies;
	}

	public void setSaisies(List<JourPointageDto> saisies) {
		this.saisies = saisies;
	}

	public boolean isDPM() {
		return isDPM;
	}

	public void setDPM(boolean isDPM) {
		this.isDPM = isDPM;
	}

	public boolean isINASuperieur315() {
		return isINASuperieur315;
	}

	public void setINASuperieur315(boolean isINASuperieur315) {
		this.isINASuperieur315 = isINASuperieur315;
	}
	
}
