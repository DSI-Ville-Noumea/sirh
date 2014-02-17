package nc.mairie.gestionagent.dto;

public class AbsenceDto extends PointageDto {

	private Integer idTypeAbsence;

	//TODO à supprimer
	private Boolean concertee ;

	public AbsenceDto() {
	}

	public Integer getIdTypeAbsence() {
		return idTypeAbsence;
	}

	public void setIdTypeAbsence(Integer idTypeAbsence) {
		this.idTypeAbsence = idTypeAbsence;
	}

	public Boolean getConcertee() {
		return concertee;
	}

	public void setConcertee(Boolean concertee) {
		this.concertee = concertee;
	}
}
