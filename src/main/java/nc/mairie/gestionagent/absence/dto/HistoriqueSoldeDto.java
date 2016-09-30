package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class HistoriqueSoldeDto {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateModifcation;
	private MotifCompteurDto motif;
	private Integer idAgentModification;
	private String textModification;

	public Date getDateModifcation() {
		return dateModifcation;
	}

	public void setDateModifcation(Date dateModifcation) {
		this.dateModifcation = dateModifcation;
	}

	public MotifCompteurDto getMotif() {
		return motif;
	}

	public void setMotif(MotifCompteurDto motif) {
		this.motif = motif;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

	public String getTextModification() {
		return textModification;
	}

	public void setTextModification(String textModification) {
		this.textModification = textModification;
	}

}
