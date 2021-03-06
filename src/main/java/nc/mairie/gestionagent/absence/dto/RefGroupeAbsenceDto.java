package nc.mairie.gestionagent.absence.dto;


public class RefGroupeAbsenceDto {

	private Integer idRefGroupeAbsence;
	private String code;
	private String libelle;

	public RefGroupeAbsenceDto() {
	}

	public RefGroupeAbsenceDto(Integer typeGroupe) {
		this.idRefGroupeAbsence = typeGroupe;
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

	@Override
	public boolean equals(Object obj) {
		return idRefGroupeAbsence.toString().equals(((RefGroupeAbsenceDto) obj).getIdRefGroupeAbsence().toString());
	}

}
