package nc.mairie.metier.specificites;

/**
 * Objet metier RegimeIndemnitaire
 */
public class RegimeIndemnitaire {

	public Integer idRegIndemn;
	public Integer idTypeRegIndemn;
	public Integer numRubrique;
	public Double forfait;
	public Integer nombrePoints;

	/**
	 * Constructeur RegimeIndemnitaire.
	 */
	public RegimeIndemnitaire() {
		super();
	}

	/**
	 * Getter de l'attribut idRegIndemn.
	 */
	public Integer getIdRegIndemn() {
		return idRegIndemn;
	}

	/**
	 * Setter de l'attribut idRegIndemn.
	 */
	public void setIdRegIndemn(Integer newIdRegIndemn) {
		idRegIndemn = newIdRegIndemn;
	}

	/**
	 * Getter de l'attribut idTypeRegIndemn.
	 */
	public Integer getIdTypeRegIndemn() {
		return idTypeRegIndemn;
	}

	/**
	 * Setter de l'attribut idTypeRegIndemn.
	 */
	public void setIdTypeRegIndemn(Integer newIdTypeRegIndemn) {
		idTypeRegIndemn = newIdTypeRegIndemn;
	}

	/**
	 * Getter de l'attribut numRubrique.
	 */
	public Integer getNumRubrique() {
		return numRubrique;
	}

	/**
	 * Setter de l'attribut numRubrique.
	 */
	public void setNumRubrique(Integer newNumRubrique) {
		numRubrique = newNumRubrique;
	}

	/**
	 * Getter de l'attribut forfait.
	 */
	public Double getForfait() {
		return forfait;
	}

	/**
	 * Setter de l'attribut forfait.
	 */
	public void setForfait(Double newForfait) {
		forfait = newForfait;
	}

	/**
	 * Getter de l'attribut nombrePoints.
	 */
	public Integer getNombrePoints() {
		return nombrePoints;
	}

	/**
	 * Setter de l'attribut nombrePoints.
	 */
	public void setNombrePoints(Integer newNombrePoints) {
		nombrePoints = newNombrePoints;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            RegimeIndemnitaire
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) object;
		boolean result = idTypeRegIndemn.toString().equals(regIndemn.getIdTypeRegIndemn().toString());
		if (forfait != null) {
			result = result && forfait.toString().equals(regIndemn.getForfait().toString());
		} else {
			result = result && (regIndemn.getForfait() == null);
		}
		if (nombrePoints != null) {
			result = result && nombrePoints.toString().equals(regIndemn.getNombrePoints().toString());
		} else {
			result = result && (regIndemn.getNombrePoints() == null);
		}
		if (numRubrique != null) {
			result = result && numRubrique.toString().equals(regIndemn.getNumRubrique().toString());
		}
		return result;
	}
}
