package nc.mairie.gestionagent.absence.dto;

import nc.mairie.enums.EnumTypeGroupeAbsence;

public class RefGroupeAbsenceDto {

	private Integer idRefGroupeAbsence;
	private String code;
	private String libelle;

	public RefGroupeAbsenceDto() {
	}

	public RefGroupeAbsenceDto(EnumTypeGroupeAbsence typeGroupe) {
		this.idRefGroupeAbsence = typeGroupe.getValue();
	}

	public Integer getIdRefGroupeAbsence() {
		return idRefGroupeAbsence;
	}

	public void setIdRefGroupeAbsence(Integer idRefGroupeAbsence) {
		this.idRefGroupeAbsence = idRefGroupeAbsence;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

}
