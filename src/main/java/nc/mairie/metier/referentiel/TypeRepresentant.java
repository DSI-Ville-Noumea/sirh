package nc.mairie.metier.referentiel;

public class TypeRepresentant {

	public Integer idTypeRepresentant;
	public String libTypeRepresentant;

	public TypeRepresentant() {
		super();
	}

	public String toString() {
		return "Type Representant : [id : " + getIdTypeRepresentant() + ", lib : " + getLibTypeRepresentant() + "]";
	}

	public Integer getIdTypeRepresentant() {
		return idTypeRepresentant;
	}

	public void setIdTypeRepresentant(Integer idTypeRepresentant) {
		this.idTypeRepresentant = idTypeRepresentant;
	}

	public String getLibTypeRepresentant() {
		return libTypeRepresentant;
	}

	public void setLibTypeRepresentant(String libTypeRepresentant) {
		this.libTypeRepresentant = libTypeRepresentant;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeRepresentant.toString().equals(((TypeRepresentant) object).getIdTypeRepresentant().toString());
	}
}
