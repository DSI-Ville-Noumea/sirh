package nc.mairie.metier.droits;

/**
 * Objet metier Groupe
 */
public class Groupe {

	public Integer idGroupe;
	public String libGroupe;

	/**
	 * Constructeur Groupe.
	 */
	public Groupe() {
		super();
	}

	/**
	 * Constructeur Groupe.
	 */
	public Groupe(String newLibGroupe) {
		super();
		setLibGroupe(newLibGroupe);
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
	 * Getter de l'attribut libGroupe.
	 */
	public String getLibGroupe() {
		return libGroupe;
	}

	/**
	 * Setter de l'attribut libGroupe.
	 */
	public void setLibGroupe(String newLibGroupe) {
		libGroupe = newLibGroupe;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            Groupe
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		Groupe grp = (Groupe) object;
		boolean result = idGroupe.toString().equals(grp.getIdGroupe().toString());

		if (libGroupe != null) {
			result = result && libGroupe.equals(grp.getLibGroupe());
		} else {
			result = result && (grp.getLibGroupe() == null);
		}

		return result;
	}
}
