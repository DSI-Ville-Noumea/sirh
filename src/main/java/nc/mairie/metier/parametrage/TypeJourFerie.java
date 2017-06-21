package nc.mairie.metier.parametrage;

public class TypeJourFerie {

	public Integer idTypeJourFerie;
	public String libTypeJourFerie;

	public TypeJourFerie() {
		super();
	}

	public String toString() {
		return "Jour Férié : [idTypeJour : " + getIdTypeJourFerie() + ", libelle : " + getLibTypeJourFerie() + "]";
	}

	public Integer getIdTypeJourFerie() {
		return idTypeJourFerie;
	}

	public void setIdTypeJourFerie(Integer idTypeJourFerie) {
		this.idTypeJourFerie = idTypeJourFerie;
	}

	public String getLibTypeJourFerie() {
		return libTypeJourFerie;
	}

	public void setLibTypeJourFerie(String libTypeJourFerie) {
		this.libTypeJourFerie = libTypeJourFerie;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeJourFerie.toString().equals(((TypeJourFerie) object).getIdTypeJourFerie().toString());
	}

}
