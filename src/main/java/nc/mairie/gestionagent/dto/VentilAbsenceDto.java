package nc.mairie.gestionagent.dto;

import java.util.Date;

public class VentilAbsenceDto extends VentilDto {

	private int minutesConcertees;
	private int minutesNonConcertees;

	public VentilAbsenceDto() {
	}

	public int getIdVentilAbsence() {
		return idVentil;
	}

	public void setIdVentilAbsence(int idVentilAbsence) {
		this.idVentil = idVentilAbsence;
	}

	public Date getDateLundi() {
		return date;
	}

	public void setDateLundi(Date dateLundi) {
		this.date = dateLundi;
	}

	public int getMinutesConcertees() {
		return minutesConcertees;
	}

	public void setMinutesConcertees(int minutesConcertees) {
		this.minutesConcertees = minutesConcertees;
	}

	public int getMinutesNonConcertees() {
		return minutesNonConcertees;
	}

	public void setMinutesNonConcertees(int minutesNonConcertees) {
		this.minutesNonConcertees = minutesNonConcertees;
	}
}
