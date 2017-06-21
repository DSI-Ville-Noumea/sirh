package nc.mairie.metier.droits;

/**
 * Objet metier Droit
 */
public class Droit {

	public Integer idElement;
	public Integer idGroupe;
	public Integer idTypeDroit;

	/**
	 * Constructeur Droit.
	 */
	public Droit() {
		super();
	}

	/**
	 * Constructeur Droit.
	 */
	public Droit(Integer newIdElement, Integer newIdGroupe) {
		super();
		setIdElement(newIdElement);
		setIdGroupe(newIdGroupe);
	}

	/**
	 * Getter de l'attribut idElement.
	 */
	public Integer getIdElement() {
		return idElement;
	}

	/**
	 * Setter de l'attribut idElement.
	 */
	public void setIdElement(Integer newIdElement) {
		idElement = newIdElement;
	}

	/**
	 * Getter de l'attribut idGroupe.
	 */
	public Integer getIdGroupe() {
		return idGroupe;
	}

	/**
	 * Setter de l'attribut idGroupe.
	 */
	public void setIdGroupe(Integer newIdGroupe) {
		idGroupe = newIdGroupe;
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

	@Override
	public boolean equals(Object object) {
		return idElement.toString().equals(((Droit) object).getIdElement().toString())
				&& idGroupe.toString().equals(((Droit) object).getIdGroupe().toString());
	}
}
