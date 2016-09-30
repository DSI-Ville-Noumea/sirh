package nc.mairie.gestionagent.eae.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class DiplomeDto {

	private Integer idDiplome;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateObtention;
	private String libTitreDiplome;
	private String libSpeDiplome;

	public Integer getIdDiplome() {
		return idDiplome;
	}

	public void setIdDiplome(Integer idDiplome) {
		this.idDiplome = idDiplome;
	}

	public Date getDateObtention() {
		return dateObtention;
	}

	public void setDateObtention(Date dateObtention) {
		this.dateObtention = dateObtention;
	}

	public String getLibTitreDiplome() {
		return libTitreDiplome;
	}

	public void setLibTitreDiplome(String libTitreDiplome) {
		this.libTitreDiplome = libTitreDiplome;
	}

	public String getLibSpeDiplome() {
		return libSpeDiplome;
	}

	public void setLibSpeDiplome(String libSpeDiplome) {
		this.libSpeDiplome = libSpeDiplome;
	}

}
