package nc.mairie.metier.parametrage;

public class TitreFormation {
	public Integer idTitreFormation;
	public String libTitreFormation;

	public TitreFormation() {
		super();
	}

	public String toString() {
		return "Titre Formation : [id : " + getIdTitreFormation() + ", lib : " + getLibTitreFormation() + "]";
	}

	public Integer getIdTitreFormation() {
		return idTitreFormation;
	}

	public void setIdTitreFormation(Integer idTitreFormation) {
		this.idTitreFormation = idTitreFormation;
	}

	public String getLibTitreFormation() {
		return libTitreFormation;
	}

	public void setLibTitreFormation(String libTitreFormation) {
		this.libTitreFormation = libTitreFormation;
	}

	@Override
	public boolean equals(Object object) {
		return idTitreFormation.toString().equals(((TitreFormation) object).getIdTitreFormation().toString());
	}
}
