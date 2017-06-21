package nc.mairie.metier.parametrage;

public class Employeur {

	public Integer idEmployeur;
	public String libEmployeur;
	public String titreEmployeur;

	public Employeur() {
		super();
	}

	public String toString() {
		return "Employeur : [id : " + getIdEmployeur() + ", lib : " + getLibEmployeur() + "]";
	}

	public Integer getIdEmployeur() {
		return idEmployeur;
	}

	public void setIdEmployeur(Integer idEmployeur) {
		this.idEmployeur = idEmployeur;
	}

	public String getLibEmployeur() {
		return libEmployeur;
	}

	public void setLibEmployeur(String libEmployeur) {
		this.libEmployeur = libEmployeur;
	}

	public String getTitreEmployeur() {
		return titreEmployeur;
	}

	public void setTitreEmployeur(String titreEmployeur) {
		this.titreEmployeur = titreEmployeur;
	}

	@Override
	public boolean equals(Object object) {
		return idEmployeur.toString().equals(((Employeur) object).getIdEmployeur().toString());
	}
}
