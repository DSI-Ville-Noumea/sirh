package nc.mairie.gestionagent.absence.dto;

import nc.mairie.gestionagent.dto.IJSONDeserialize;
import flexjson.JSONDeserializer;

public class MotifDto implements IJSONDeserialize<MotifDto> {

	private Integer idMotif;
	private String libelle;

	public MotifDto() {
	}

	@Override
	public MotifDto deserializeFromJSON(String json) {
		return new JSONDeserializer<MotifDto>().deserializeInto(json, this);
	}

	public Integer getIdMotif() {
		return idMotif;
	}

	public void setIdMotif(Integer idMotif) {
		this.idMotif = idMotif;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
