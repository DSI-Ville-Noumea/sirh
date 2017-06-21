package nc.mairie.metier.parametrage;

/**
 * Objet metier FamilleEmploi
 */
public class FamilleEmploi {
	public Integer idFamilleEmploi;
	public String codeFamilleEmploi;
	public String libFamilleEmploi;

	/**
	 * Constructeur FamilleEmploi.
	 */
	public FamilleEmploi() {
		super();
	}

	/**
	 * Getter de l'attribut idFamilleEmploi.
	 */
	public Integer getIdFamilleEmploi() {
		return idFamilleEmploi;
	}

	/**
	 * Setter de l'attribut idFamilleEmploi.
	 */
	public void setIdFamilleEmploi(Integer newIdFamilleEmploi) {
		idFamilleEmploi = newIdFamilleEmploi;
	}

	/**
	 * Getter de l'attribut codeFamilleEmploi.
	 */
	public String getCodeFamilleEmploi() {
		return codeFamilleEmploi;
	}

	/**
	 * Setter de l'attribut codeFamilleEmploi.
	 */
	public void setCodeFamilleEmploi(String newCodeFamilleEmploi) {
		codeFamilleEmploi = newCodeFamilleEmploi;
	}

	/**
	 * Getter de l'attribut libFamilleEmploi.
	 */
	public String getLibFamilleEmploi() {
		return libFamilleEmploi;
	}

	/**
	 * Setter de l'attribut libFamilleEmploi.
	 */
	public void setLibFamilleEmploi(String newLibFamilleEmploi) {
		libFamilleEmploi = newLibFamilleEmploi;
	}

	/**
	 * Surcharge de la methode equals
	 * 
	 * @param object
	 * @return boolean. True si egaux. False sinon.
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return idFamilleEmploi.toString().equals(((FamilleEmploi) object).getIdFamilleEmploi().toString());
	}
}
