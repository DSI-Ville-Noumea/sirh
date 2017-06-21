package nc.mairie.metier.parametrage;

public class EmployeurCap {

	public Integer idEmployeur;
	public Integer idCap;
	public Integer position;

	public EmployeurCap() {
		super();
	}

	public String toString() {
		return "Employeur-CAP : [idEmployeur : " + getIdEmployeur() + ", idCap : " + getIdCap() + "]";
	}

	public Integer getIdEmployeur() {
		return idEmployeur;
	}

	public void setIdEmployeur(Integer idEmployeur) {
		this.idEmployeur = idEmployeur;
	}

	public Integer getIdCap() {
		return idCap;
	}

	public void setIdCap(Integer idCap) {
		this.idCap = idCap;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public boolean equals(Object object) {
		return idEmployeur.toString().equals(((EmployeurCap) object).getIdEmployeur().toString())
				&& idCap.toString().equals(((EmployeurCap) object).getIdCap().toString());
	}
}
