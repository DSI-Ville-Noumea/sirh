package nc.mairie.metier.parametrage;

/**
 * Objet metier NatureAvantage
 */
public class NatureAvantage {

	public Integer idNatureAvantage;
	public String libNatureAvantage;

	/**
	 * Constructeur NatureAvantage.
	 */
	public NatureAvantage() {
		super();
	}

	/**
	 * Getter de l'attribut idNatureAvantage.
	 */
	public Integer getIdNatureAvantage() {
		return idNatureAvantage;
	}

	/**
	 * Setter de l'attribut idNatureAvantage.
	 */
	public void setIdNatureAvantage(Integer newIdNatureAvantage) {
		idNatureAvantage = newIdNatureAvantage;
	}

	/**
	 * Getter de l'attribut libNatureAvantage.
	 */
	public String getLibNatureAvantage() {
		return libNatureAvantage;
	}

	/**
	 * Setter de l'attribut libNatureAvantage.
	 */
	public void setLibNatureAvantage(String newLibNatureAvantage) {
		libNatureAvantage = newLibNatureAvantage;
	}

	@Override
	public boolean equals(Object object) {
		return idNatureAvantage.toString().equals(((NatureAvantage) object).getIdNatureAvantage().toString());
	}
}
