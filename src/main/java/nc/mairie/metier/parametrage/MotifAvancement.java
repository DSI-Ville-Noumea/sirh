package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifAvancement
 */
public class MotifAvancement {
	public Integer idMotifAvct;
	public String libMotifAvct;
	public String code;

	/**
	 * Constructeur MotifAvancement.
	 */
	public MotifAvancement() {
		super();
	}

	/**
	 * Getter de l'attribut idMotifAvct.
	 */
	public Integer getIdMotifAvct() {
		return idMotifAvct;
	}

	/**
	 * Setter de l'attribut idMotifAvct.
	 */
	public void setIdMotifAvct(Integer newIdMotifAvct) {
		idMotifAvct = newIdMotifAvct;
	}

	/**
	 * Getter de l'attribut libMotifAvct.
	 */
	public String getLibMotifAvct() {
		return libMotifAvct;
	}

	/**
	 * Setter de l'attribut libMotifAvct.
	 */
	public void setLibMotifAvct(String newLibMotifAvct) {
		libMotifAvct = newLibMotifAvct;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String codeMotifAvct) {
		this.code = codeMotifAvct;
	}

	@Override
	public boolean equals(Object object) {
		return idMotifAvct.toString().equals(((MotifAvancement) object).getIdMotifAvct().toString());
	}
}
