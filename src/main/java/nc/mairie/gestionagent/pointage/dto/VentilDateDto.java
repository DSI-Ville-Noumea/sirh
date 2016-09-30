package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class VentilDateDto {

	private Integer idVentilDate;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateVentil;
	private boolean isPaie;
	private String typeChaine;

	public VentilDateDto() {
	}

	@Override
	public String toString() {
		return "VentilDateDto: [Id : " + getIdVentilDate() + ",date : " + getDateVentil() + ",isPaie : " + isPaie()
				+ ", typeChaine : " + getTypeChaine() + "]";
	}

	public Date getDateVentil() {
		return dateVentil;
	}

	public void setDateVentil(Date dateVentil) {
		this.dateVentil = dateVentil;
	}

	public boolean isPaie() {
		return isPaie;
	}

	public void setPaie(boolean isPaie) {
		this.isPaie = isPaie;
	}

	public String getTypeChaine() {
		return typeChaine;
	}

	public void setTypeChaine(String typeChaine) {
		this.typeChaine = typeChaine;
	}

	public Integer getIdVentilDate() {
		return idVentilDate;
	}

	public void setIdVentilDate(Integer idVentilDate) {
		this.idVentilDate = idVentilDate;
	}

}
