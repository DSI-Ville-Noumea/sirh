package nc.mairie.gestionagent.eae.dto;

public class DureeDto {

	private int	heures;
	private int	minutes;

	public DureeDto() {

	}

	public DureeDto(Integer minutes) {
		if (null != minutes) {
			this.heures = minutes / 60;
			this.minutes = minutes % 60;
		}
	}

	public int getHeures() {
		return heures;
	}

	public void setHeures(int heures) {
		this.heures = heures;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
}
