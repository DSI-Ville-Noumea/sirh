package nc.mairie.gestionagent.dto;

public class AbsenceDto extends PointageDto {

	private Boolean concertee;

	public AbsenceDto() {
	}

	public Boolean getConcertee() {
		return concertee;
	}

	public void setConcertee(Boolean concertee) {
		this.concertee = concertee;
	}
}
