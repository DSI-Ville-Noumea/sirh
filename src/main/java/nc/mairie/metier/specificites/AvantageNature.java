package nc.mairie.metier.specificites;

/**
 * Objet metier AvantageNature
 */
public class AvantageNature {

	public Integer idAvantage;
	public Integer numRubrique;
	public Integer idTypeAvantage;
	public Integer idNatureAvantage;
	public Double montant;

	/**
	 * Constructeur AvantageNature.
	 */
	public AvantageNature() {
		super();
	}

	/**
	 * Getter de l'attribut idAvantage.
	 */
	public Integer getIdAvantage() {
		return idAvantage;
	}

	/**
	 * Setter de l'attribut idAvantage.
	 */
	public void setIdAvantage(Integer newIdAvantage) {
		idAvantage = newIdAvantage;
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
	 * Getter de l'attribut idTypeAvantage.
	 */
	public Integer getIdTypeAvantage() {
		return idTypeAvantage;
	}

	/**
	 * Setter de l'attribut idTypeAvantage.
	 */
	public void setIdTypeAvantage(Integer newIdTypeAvantage) {
		idTypeAvantage = newIdTypeAvantage;
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
	 * Getter de l'attribut montant.
	 */
	public Double getMontant() {
		return montant;
	}

	/**
	 * Setter de l'attribut montant.
	 */
	public void setMontant(Double newMontant) {
		montant = newMontant;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            AvantageNature
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		AvantageNature avNat = (AvantageNature) object;
		boolean result = idTypeAvantage.toString().equals(avNat.getIdTypeAvantage().toString());
		if (idNatureAvantage != null) {
			result = result && idNatureAvantage.toString().equals(avNat.getIdNatureAvantage().toString());
		} else {
			result = result && (avNat.getIdNatureAvantage() == null);
		}
		if (montant != null) {
			result = result && montant.toString().equals(avNat.getMontant().toString());
		} else {
			result = result && (avNat.getMontant() == null);
		}
		if (numRubrique != null) {
			result = result && numRubrique.toString().equals(avNat.getNumRubrique().toString());
		}
		return result;
	}
}
