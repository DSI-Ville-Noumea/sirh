package nc.mairie.gestionagent.absence.dto;

import nc.mairie.gestionagent.dto.IJSONDeserialize;
import flexjson.JSONDeserializer;

public class MotifCompteurDto implements IJSONDeserialize<MotifCompteurDto> {

	private Integer idMotifCompteur;
	private String libelle;
	private Integer idRefTypeAbsence;

	public MotifCompteurDto() {
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}

	@Override
	public MotifCompteurDto deserializeFromJSON(String json) {
		return new JSONDeserializer<MotifCompteurDto>().deserializeInto(json, this);
	}

	public Integer getIdMotifCompteur() {
		return idMotifCompteur;
	}

	public void setIdMotifCompteur(Integer idMotifCompteur) {
		this.idMotifCompteur = idMotifCompteur;
	}
}
