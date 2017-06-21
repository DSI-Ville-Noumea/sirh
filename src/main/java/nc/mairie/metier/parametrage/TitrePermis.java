package nc.mairie.metier.parametrage;

public class TitrePermis {
	public Integer idPermis;
	public String libPermis;

	public TitrePermis() {
		super();
	}

	public String toString() {
		return "Titre Permis : [id : " + getIdPermis() + ", lib : " + getLibPermis() + "]";
	}

	public Integer getIdPermis() {
		return idPermis;
	}

	public void setIdPermis(Integer idPermis) {
		this.idPermis = idPermis;
	}

	public String getLibPermis() {
		return libPermis;
	}

	public void setLibPermis(String libPermis) {
		this.libPermis = libPermis;
	}

	@Override
	public boolean equals(Object object) {
		return idPermis.toString().equals(((TitrePermis) object).getIdPermis().toString());
	}

}
