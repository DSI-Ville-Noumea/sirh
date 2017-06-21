package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifAffectation
 */
public class MotifAffectation {

	public Integer idMotifAffectation;
	public String libMotifAffectation;

	/**
	 * Constructeur MotifAffectation.
	 */
	public MotifAffectation() {
		super();
	}

	/**
	 * Getter de l'attribut idMotifAffectation.
	 */
	public Integer getIdMotifAffectation() {
		return idMotifAffectation;
	}

	/**
	 * Setter de l'attribut idMotifAffectation.
	 */
	public void setIdMotifAffectation(Integer newIdMotifAffectation) {
		idMotifAffectation = newIdMotifAffectation;
	}

	/**
	 * Getter de l'attribut libMotifAffectation.
	 */
	public String getLibMotifAffectation() {
		return libMotifAffectation;
	}

	/**
	 * Setter de l'attribut libMotifAffectation.
	 */
	public void setLibMotifAffectation(String newLibMotifAffectation) {
		libMotifAffectation = newLibMotifAffectation;
	}

	/**
	 * Surchage de la methode d'egalite.
	 * 
	 * @param object
	 * @return true si les 2 objets sont egaux. False sinon.
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		boolean result = getIdMotifAffectation().toString().equals(((MotifAffectation) object).getIdMotifAffectation().toString());
		if (getLibMotifAffectation() != null) {
			result = result && getLibMotifAffectation().equals(((MotifAffectation) object).getLibMotifAffectation());
		}
		return result;
	}
}
