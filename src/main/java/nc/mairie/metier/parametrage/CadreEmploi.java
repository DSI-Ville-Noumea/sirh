package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier CadreEmploi
 */
public class CadreEmploi {
	public Integer idCadreEmploi;
	public String libCadreEmploi;

	/**
	 * Constructeur CadreEmploi.
	 */
	public CadreEmploi() {
		super();
	}

	/**
	 * Getter de l'attribut idCadreEmploi.
	 */
	public Integer getIdCadreEmploi() {
		return idCadreEmploi;
	}

	/**
	 * Setter de l'attribut idCadreEmploi.
	 */
	public void setIdCadreEmploi(Integer newIdCadreEmploi) {
		idCadreEmploi = newIdCadreEmploi;
	}

	/**
	 * Getter de l'attribut libCadreEmploi.
	 */
	public String getLibCadreEmploi() {
		return libCadreEmploi == null ? Const.CHAINE_VIDE : libCadreEmploi.trim();
	}

	/**
	 * Setter de l'attribut libCadreEmploi.
	 */
	public void setLibCadreEmploi(String newLibCadreEmploi) {
		libCadreEmploi = newLibCadreEmploi;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            CadreEmploi
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return idCadreEmploi.toString().equals(((CadreEmploi) object).getIdCadreEmploi().toString());
	}
}
