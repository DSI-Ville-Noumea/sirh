package nc.mairie.metier.poste;

/**
 * Objet metier AutreAppellationEmploi
 */
public class AutreAppellationEmploi {

	public Integer idAutreAppellationEmploi;
	public Integer idFicheEmploi;
	public String libAutreAppellationEmploi;

	/**
	 * Constructeur AutreAppellationEmploi.
	 */
	public AutreAppellationEmploi() {
		super();
	}

	/**
	 * Constructeur AutreAppellationEmploi.
	 */
	public AutreAppellationEmploi(Integer newIdFicheEmploi, String newLibAutreAppellationEmploi) {
		super();
		setIdFicheEmploi(newIdFicheEmploi);
		setLibAutreAppellationEmploi(newLibAutreAppellationEmploi);
	}

	/**
	 * Getter de l'attribut idAutreAppellationEmploi.
	 */
	public Integer getIdAutreAppellationEmploi() {
		return idAutreAppellationEmploi;
	}

	/**
	 * Setter de l'attribut idAutreAppellationEmploi.
	 */
	public void setIdAutreAppellationEmploi(Integer newIdAutreAppellationEmploi) {
		idAutreAppellationEmploi = newIdAutreAppellationEmploi;
	}

	/**
	 * Getter de l'attribut idFicheEmploi.
	 */
	public Integer getIdFicheEmploi() {
		return idFicheEmploi;
	}

	/**
	 * Setter de l'attribut idFicheEmploi.
	 */
	public void setIdFicheEmploi(Integer newIdFicheEmploi) {
		idFicheEmploi = newIdFicheEmploi;
	}

	/**
	 * Getter de l'attribut libAutreAppellationEmploi.
	 */
	public String getLibAutreAppellationEmploi() {
		return libAutreAppellationEmploi;
	}

	/**
	 * Setter de l'attribut libAutreAppellationEmploi.
	 */
	public void setLibAutreAppellationEmploi(String newLibAutreAppellationEmploi) {
		libAutreAppellationEmploi = newLibAutreAppellationEmploi;
	}

	/**
	 * Surchage de la methode d'egalite.
	 * 
	 * @param object
	 * @return true si les 2 objets sont egaux. False sinon.
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		boolean result = getLibAutreAppellationEmploi().equals(
				((AutreAppellationEmploi) object).getLibAutreAppellationEmploi());
		if (getIdFicheEmploi() != null) {
			result = result
					&& getIdFicheEmploi().toString().equals(
							((AutreAppellationEmploi) object).getIdFicheEmploi().toString());
		}
		return result;
	}

}
