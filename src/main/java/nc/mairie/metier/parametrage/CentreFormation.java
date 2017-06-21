package nc.mairie.metier.parametrage;

public class CentreFormation {

	public Integer idCentreFormation;
	public String libCentreFormation;

	public CentreFormation() {
		super();
	}

	public String toString() {
		return "Centre formation : [id : " + getIdCentreFormation() + ", lib : " + getLibCentreFormation() + "]";
	}

	public Integer getIdCentreFormation() {
		return idCentreFormation;
	}

	public void setIdCentreFormation(Integer idCentreFormation) {
		this.idCentreFormation = idCentreFormation;
	}

	public String getLibCentreFormation() {
		return libCentreFormation;
	}

	public void setLibCentreFormation(String libCentreFormation) {
		this.libCentreFormation = libCentreFormation;
	}

	@Override
	public boolean equals(Object object) {
		return idCentreFormation.toString().equals(((CentreFormation) object).getIdCentreFormation().toString());
	}
}
