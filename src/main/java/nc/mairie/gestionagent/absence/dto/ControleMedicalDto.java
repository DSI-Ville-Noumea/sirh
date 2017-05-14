package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class ControleMedicalDto {

	private Integer id;
	private Integer idDemandeMaladie;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date date;
	private Integer idAgent;
	private String commentaire;
	
	public ControleMedicalDto() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdDemandeMaladie() {
		return idDemandeMaladie;
	}

	public void setIdDemandeMaladie(Integer idDemandeMaladie) {
		this.idDemandeMaladie = idDemandeMaladie;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

}

