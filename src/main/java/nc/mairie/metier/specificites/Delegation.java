package nc.mairie.metier.specificites;

import nc.mairie.metier.Const;

/**
 * Objet metier Delegation
 */
public class Delegation {

	public Integer idDelegation;
	public Integer idTypeDelegation;
	public String libDelegation;

	/**
	 * Constructeur Delegation.
	 */
	public Delegation() {
		super();
	}

	/**
	 * Getter de l'attribut idDelegation.
	 */
	public Integer getIdDelegation() {
		return idDelegation;
	}

	/**
	 * Setter de l'attribut idDelegation.
	 */
	public void setIdDelegation(Integer newIdDelegation) {
		idDelegation = newIdDelegation;
	}

	/**
	 * Getter de l'attribut idTypeDelegation.
	 */
	public Integer getIdTypeDelegation() {
		return idTypeDelegation;
	}

	/**
	 * Setter de l'attribut idTypeDelegation.
	 */
	public void setIdTypeDelegation(Integer newIdTypeDelegation) {
		idTypeDelegation = newIdTypeDelegation;
	}

	/**
	 * Getter de l'attribut libDelegation.
	 */
	public String getLibDelegation() {
		return libDelegation == null ? Const.CHAINE_VIDE : libDelegation.trim();
	}

	/**
	 * Setter de l'attribut libDelegation.
	 */
	public void setLibDelegation(String newLibDelegation) {
		libDelegation = newLibDelegation;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            Delegation
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		Delegation deleg = (Delegation) object;
		boolean result = idTypeDelegation.toString().equals(deleg.getIdTypeDelegation().toString());
		if (libDelegation != null) {
			result = result && libDelegation.equals(deleg.getLibDelegation());
		} else {
			result = result && (deleg.getLibDelegation() == null);
		}
		return result;
	}
}
