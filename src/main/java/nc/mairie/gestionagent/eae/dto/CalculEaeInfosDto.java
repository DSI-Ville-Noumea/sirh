package nc.mairie.gestionagent.eae.dto;

import java.util.Date;
import java.util.List;

public class CalculEaeInfosDto {

	private Date dateDebut;

	private Date dateFin;

	private List<ParcoursProDto> listParcoursPro;

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

	public List<ParcoursProDto> getListParcoursPro() {
		return listParcoursPro;
	}

	public void setListParcoursPro(List<ParcoursProDto> listParcoursPro) {
		this.listParcoursPro = listParcoursPro;
	}

}
