package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

public class SoldeMonthDto {

	private Double soldeAsaA55;
	private Date dateDebut;
	private Date dateFin;

	public Double getSoldeAsaA55() {
		return soldeAsaA55;
	}

	public void setSoldeAsaA55(Double soldeAsaA55) {
		this.soldeAsaA55 = soldeAsaA55;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

}
