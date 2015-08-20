package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

public class FiltreSoldeDto {

	private Date dateDebut;
	private Date dateFin;
	private Date dateDemande;
	private Integer typeDemande;

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

	public Integer getTypeDemande() {
		return typeDemande;
	}

	public void setTypeDemande(Integer typeDemande) {
		this.typeDemande = typeDemande;
	}

	public Date getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(Date dateDemande) {
		this.dateDemande = dateDemande;
	}
	
}
