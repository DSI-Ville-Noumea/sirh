package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifNonRecrutement
 */
public class MotifNonRecrutement {

	public Integer idMotifNonRecrut;
	public String libMotifNonRecrut;

	/**
	 * Constructeur MotifNonRecrutement.
	 */
	public MotifNonRecrutement() {
		super();
	}

	/**
	 * Getter de l'attribut idMotifNonRecrut.
	 */
	public Integer getIdMotifNonRecrut() {
		return idMotifNonRecrut;
	}

	/**
	 * Setter de l'attribut idMotifNonRecrut.
	 */
	public void setIdMotifNonRecrut(Integer newIdMotifNonRecrut) {
		idMotifNonRecrut = newIdMotifNonRecrut;
	}

	/**
	 * Getter de l'attribut libMotifNonRecrut.
	 */
	public String getLibMotifNonRecrut() {
		return libMotifNonRecrut;
	}

	/**
	 * Setter de l'attribut libMotifNonRecrut.
	 */
	public void setLibMotifNonRecrut(String newLibMotifNonRecrut) {
		libMotifNonRecrut = newLibMotifNonRecrut;
	}

	@Override
	public boolean equals(Object object) {
		return idMotifNonRecrut.toString().equals(((MotifNonRecrutement) object).getIdMotifNonRecrut().toString());
	}
}
