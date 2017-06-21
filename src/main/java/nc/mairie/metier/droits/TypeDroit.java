package nc.mairie.metier.droits;

/**
 * Objet metier TypeDroit
 */
public class TypeDroit {

	public Integer idTypeDroit;
	public String libTypeDroit;

	/**
	 * Constructeur TypeDroit.
	 */
	public TypeDroit() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeDroit.
	 */
	public Integer getIdTypeDroit() {
		return idTypeDroit;
	}

	/**
	 * Setter de l'attribut idTypeDroit.
	 */
	public void setIdTypeDroit(Integer newIdTypeDroit) {
		idTypeDroit = newIdTypeDroit;
	}

	/**
	 * Getter de l'attribut libTypeDroit.
	 */
	public String getLibTypeDroit() {
		return libTypeDroit;
	}

	/**
	 * Setter de l'attribut libTypeDroit.
	 */
	public void setLibTypeDroit(String newLibTypeDroit) {
		libTypeDroit = newLibTypeDroit;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeDroit.toString().equals(((TypeDroit) object).getIdTypeDroit().toString());
	}

}
