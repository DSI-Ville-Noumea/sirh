package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

public class SoldeMonthDto {

	private int soldeAsa;
	private Date dateDebut;
	private Date dateFin;

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

	public int getSoldeAsa() {
		return soldeAsa;
	}

	public void setSoldeAsa(int soldeAsa) {
		this.soldeAsa = soldeAsa;
	}

}
