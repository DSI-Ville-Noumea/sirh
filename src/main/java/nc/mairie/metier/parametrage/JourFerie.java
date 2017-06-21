package nc.mairie.metier.parametrage;

import java.util.Date;

public class JourFerie {

	public Integer idJourFerie;
	public Integer idTypeJour;
	public Date dateJour;
	public String description;

	public JourFerie() {
		super();
	}

	public String toString() {
		return "Jour Férié : [idTypeJour : " + getIdTypeJour() + ", dateJour : " + getDateJour().toString() + "]";
	}

	public Integer getIdJourFerie() {
		return idJourFerie;
	}

	public void setIdJourFerie(Integer idJourFerie) {
		this.idJourFerie = idJourFerie;
	}

	public Integer getIdTypeJour() {
		return idTypeJour;
	}

	public void setIdTypeJour(Integer idTypeJour) {
		this.idTypeJour = idTypeJour;
	}

	public Date getDateJour() {
		return dateJour;
	}

	public void setDateJour(Date dateJour) {
		this.dateJour = dateJour;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object object) {
		return idJourFerie.toString().equals(((JourFerie) object).getIdJourFerie().toString());
	}
}
