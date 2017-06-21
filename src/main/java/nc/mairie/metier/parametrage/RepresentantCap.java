package nc.mairie.metier.parametrage;

public class RepresentantCap {

	public Integer idRepresentant;
	public Integer idCap;
	public Integer position;

	public RepresentantCap() {
		super();
	}

	public String toString() {
		return "Representant-CAP : [idRepresentant : " + getIdRepresentant() + ", idCap : " + getIdCap() + "]";
	}

	public Integer getIdCap() {
		return idCap;
	}

	public void setIdCap(Integer idCap) {
		this.idCap = idCap;
	}

	public Integer getIdRepresentant() {
		return idRepresentant;
	}

	public void setIdRepresentant(Integer idRepresentant) {
		this.idRepresentant = idRepresentant;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public boolean equals(Object object) {
		return idCap.toString().equals(((RepresentantCap) object).getIdCap().toString())
				&& idRepresentant.toString().equals(((RepresentantCap) object).getIdRepresentant().toString());
	}
}
