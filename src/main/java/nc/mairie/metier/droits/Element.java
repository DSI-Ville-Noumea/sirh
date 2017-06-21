package nc.mairie.metier.droits;

/**
 * Objet metier Element
 */
public class Element {

	public Integer idElement;
	public String libElement;

	/**
	 * Constructeur Element.
	 */
	public Element() {
		super();
	}

	/**
	 * Constructeur Element.
	 */
	public Element(String newLibElement) {
		super();
		setLibElement(newLibElement);
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
	 * Getter de l'attribut libElement.
	 */
	public String getLibElement() {
		return libElement;
	}

	/**
	 * Setter de l'attribut libElement.
	 */
	public void setLibElement(String newLibElement) {
		libElement = newLibElement;
	}

	@Override
	public boolean equals(Object object) {
		return idElement.toString().equals(((Element) object).getIdElement().toString());
	}

}
