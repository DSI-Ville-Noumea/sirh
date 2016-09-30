package nc.mairie.gestionagent.eae.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeEvaluateurDto {
	
	private Integer idEaeEvaluateur;
	private int idAgent;
	private String fonction;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEntreeService;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEntreeCollectivite;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEntreeFonction;
	private AgentDto agent;
	
	public EaeEvaluateurDto() {
	}
	
	public Integer getIdEaeEvaluateur() {
		return idEaeEvaluateur;
	}
	public void setIdEaeEvaluateur(Integer idEaeEvaluateur) {
		this.idEaeEvaluateur = idEaeEvaluateur;
	}
	public int getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(int idAgent) {
		this.idAgent = idAgent;
	}
	public String getFonction() {
		return fonction;
	}
	public void setFonction(String fonction) {
		this.fonction = fonction;
	}
	public Date getDateEntreeService() {
		return dateEntreeService;
	}
	public void setDateEntreeService(Date dateEntreeService) {
		this.dateEntreeService = dateEntreeService;
	}
	public Date getDateEntreeCollectivite() {
		return dateEntreeCollectivite;
	}
	public void setDateEntreeCollectivite(Date dateEntreeCollectivite) {
		this.dateEntreeCollectivite = dateEntreeCollectivite;
	}
	public Date getDateEntreeFonction() {
		return dateEntreeFonction;
	}
	public void setDateEntreeFonction(Date dateEntreeFonction) {
		this.dateEntreeFonction = dateEntreeFonction;
	}
	public AgentDto getAgent() {
		return agent;
	}
	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}
}
