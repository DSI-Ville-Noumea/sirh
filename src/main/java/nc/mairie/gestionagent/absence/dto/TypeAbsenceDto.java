package nc.mairie.gestionagent.absence.dto;

public class TypeAbsenceDto {

	private Integer idRefTypeAbsence;
	private String libelle;

	public TypeAbsenceDto() {

	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
