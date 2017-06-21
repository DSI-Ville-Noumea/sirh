package nc.mairie.metier.parametrage;

public class Representant {

	public Integer idRepresentant;
	public Integer idTypeRepresentant;
	public String nomRepresentant;
	public String prenomRepresentant;

	public Representant() {
		super();
	}

	public String toString() {
		return "Representant : [id : " + getIdRepresentant() + ", nom : " + getNomRepresentant() + "]";
	}

	public Integer getIdRepresentant() {
		return idRepresentant;
	}

	public void setIdRepresentant(Integer idRepresentant) {
		this.idRepresentant = idRepresentant;
	}

	public String getNomRepresentant() {
		return nomRepresentant;
	}

	public void setNomRepresentant(String nomRepresentant) {
		this.nomRepresentant = nomRepresentant;
	}

	public String getPrenomRepresentant() {
		return prenomRepresentant;
	}

	public void setPrenomRepresentant(String prenomRepresentant) {
		this.prenomRepresentant = prenomRepresentant;
	}

	public Integer getIdTypeRepresentant() {
		return idTypeRepresentant;
	}

	public void setIdTypeRepresentant(Integer idTypeRepresentant) {
		this.idTypeRepresentant = idTypeRepresentant;
	}

	@Override
	public boolean equals(Object object) {
		return idRepresentant.toString().equals(((Representant) object).getIdRepresentant().toString());
	}
}
