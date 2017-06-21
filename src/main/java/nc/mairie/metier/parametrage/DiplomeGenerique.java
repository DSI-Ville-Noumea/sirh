package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier DiplomeGenerique
 */
public class DiplomeGenerique {
	public Integer idDiplomeGenerique;
	public String libDiplomeGenerique;

	/**
	 * Constructeur DiplomeGenerique.
	 */
	public DiplomeGenerique() {
		super();
	}

	/**
	 * Getter de l'attribut idDiplomeGenerique.
	 */
	public Integer getIdDiplomeGenerique() {
		return idDiplomeGenerique;
	}

	/**
	 * Setter de l'attribut idDiplomeGenerique.
	 */
	public void setIdDiplomeGenerique(Integer newIdDiplomeGenerique) {
		idDiplomeGenerique = newIdDiplomeGenerique;
	}

	/**
	 * Getter de l'attribut libDiplomeGenerique.
	 */
	public String getLibDiplomeGenerique() {
		return libDiplomeGenerique == null ? Const.CHAINE_VIDE : libDiplomeGenerique.trim();
	}

	/**
	 * Setter de l'attribut libDiplomeGenerique.
	 */
	public void setLibDiplomeGenerique(String newLibDiplomeGenerique) {
		libDiplomeGenerique = newLibDiplomeGenerique;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            DiplomeGenerique
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return idDiplomeGenerique.toString().equals(((DiplomeGenerique) object).getIdDiplomeGenerique().toString());
	}
}
