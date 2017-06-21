package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifRecrutement
 */
public class MotifRecrutement {

	public Integer idMotifRecrut;
	public String libMotifRecrut;

	/**
	 * Constructeur MotifRecrutement.
	 */
	public MotifRecrutement() {
		super();
	}

	/**
	 * Getter de l'attribut idMotifRecrut.
	 */
	public Integer getIdMotifRecrut() {
		return idMotifRecrut;
	}

	/**
	 * Setter de l'attribut idMotifRecrut.
	 */
	public void setIdMotifRecrut(Integer newIdMotifRecrut) {
		idMotifRecrut = newIdMotifRecrut;
	}

	/**
	 * Getter de l'attribut libMotifRecrut.
	 */
	public String getLibMotifRecrut() {
		return libMotifRecrut;
	}

	/**
	 * Setter de l'attribut libMotifRecrut.
	 */
	public void setLibMotifRecrut(String newLibMotifRecrut) {
		libMotifRecrut = newLibMotifRecrut;
	}

	@Override
	public boolean equals(Object object) {
		return idMotifRecrut.toString().equals(((MotifRecrutement) object).getIdMotifRecrut().toString());
	}
}
