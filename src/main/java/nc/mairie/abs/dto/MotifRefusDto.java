package nc.mairie.abs.dto;

import nc.mairie.gestionagent.dto.IJSONDeserialize;
import flexjson.JSONDeserializer;

public class MotifRefusDto implements IJSONDeserialize<MotifRefusDto> {

	private Integer idMotifRefus;
	private String libelle;
	private Integer idRefTypeAbsence;

	public MotifRefusDto() {
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdMotifRefus() {
		return idMotifRefus;
	}

	public void setIdMotifRefus(Integer idMotifRefus) {
		this.idMotifRefus = idMotifRefus;
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}

	@Override
	public MotifRefusDto deserializeFromJSON(String json) {
		return new JSONDeserializer<MotifRefusDto>().deserializeInto(json, this);
	}
}
