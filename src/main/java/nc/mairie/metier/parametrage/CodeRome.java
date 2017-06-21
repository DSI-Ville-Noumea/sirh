package nc.mairie.metier.parametrage;

/**
 * Objet metier CodeRome
 */
public class CodeRome {
	public Integer idCodeRome;
	public String libCodeRome;
	public String descCodeRome;

	/**
	 * Constructeur CodeRome.
	 */
	public CodeRome() {
		super();
	}

	/**
	 * Getter de l'attribut idCodeRome.
	 */
	public Integer getIdCodeRome() {
		return idCodeRome;
	}

	/**
	 * Setter de l'attribut idCodeRome.
	 */
	public void setIdCodeRome(Integer newIdCodeRome) {
		idCodeRome = newIdCodeRome;
	}

	/**
	 * Getter de l'attribut libCodeRome.
	 */
	public String getLibCodeRome() {
		return libCodeRome;
	}

	/**
	 * Setter de l'attribut libCodeRome.
	 */
	public void setLibCodeRome(String newLibCodeRome) {
		libCodeRome = newLibCodeRome;
	}

	public String getDescCodeRome() {
		return descCodeRome;
	}

	public void setDescCodeRome(String descCodeRome) {
		this.descCodeRome = descCodeRome;
	}

	@Override
	public boolean equals(Object object) {
		return idCodeRome.toString().equals(((CodeRome) object).getIdCodeRome().toString());
	}
}
