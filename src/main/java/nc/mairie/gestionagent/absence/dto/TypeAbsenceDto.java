package nc.mairie.gestionagent.absence.dto;

public class TypeAbsenceDto {

	private Integer idRefTypeAbsence;
	private String libelle;
	private RefGroupeAbsenceDto groupeAbsence;
	private RefTypeSaisiDto typeSaisiDto;
	private RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto;

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

	@Override
	public boolean equals(Object obj) { 
		
		return idRefTypeAbsence.toString().equals(((TypeAbsenceDto) obj).getIdRefTypeAbsence().toString());
	}

	public RefGroupeAbsenceDto getGroupeAbsence() {
		return groupeAbsence;
	}

	public void setGroupeAbsence(RefGroupeAbsenceDto groupeAbsence) {
		this.groupeAbsence = groupeAbsence;
	}

	public RefTypeSaisiDto getTypeSaisiDto() {
		return typeSaisiDto;
	}

	public void setTypeSaisiDto(RefTypeSaisiDto typeSaisiDto) {
		this.typeSaisiDto = typeSaisiDto;
	}

	public RefTypeSaisiCongeAnnuelDto getTypeSaisiCongeAnnuelDto() {
		return typeSaisiCongeAnnuelDto;
	}

	public void setTypeSaisiCongeAnnuelDto(RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto) {
		this.typeSaisiCongeAnnuelDto = typeSaisiCongeAnnuelDto;
	}
}
