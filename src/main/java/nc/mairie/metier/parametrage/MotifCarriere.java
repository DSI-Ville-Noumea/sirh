package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifCarriere
 */
public class MotifCarriere {

	public Integer idMotifCarriere;
	public String libMotifCarriere;

	/**
	 * Constructeur MotifCarriere.
	 */
	public MotifCarriere() {
		super();
	}

	public Integer getIdMotifCarriere() {
		return idMotifCarriere;
	}

	public void setIdMotifCarriere(Integer idMotifCarriere) {
		this.idMotifCarriere = idMotifCarriere;
	}

	public String getLibMotifCarriere() {
		return libMotifCarriere;
	}

	public void setLibMotifCarriere(String libMotifCarriere) {
		this.libMotifCarriere = libMotifCarriere;
	}

	@Override
	public boolean equals(Object object) {
		return idMotifCarriere.toString().equals(((MotifCarriere) object).getIdMotifCarriere().toString());
	}

}
