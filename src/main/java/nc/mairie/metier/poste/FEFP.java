package nc.mairie.metier.poste;

/**
 * Objet metier FEFP
 */
public class FEFP {

	public Integer idFicheEmploi;
	public Integer idFichePoste;
	public boolean fePrimaire;

	/**
	 * Constructeur FEFP.
	 */
	public FEFP() {
		super();
	}

	/**
	 * Constructeur FEFP.
	 */
	public FEFP(Integer idFichePoste, Integer idFicheEmploi, boolean fePrimaire) {
		super();

		this.idFichePoste = idFichePoste;
		this.idFicheEmploi = idFicheEmploi;
		this.fePrimaire = fePrimaire;
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
	 * Getter de l'attribut idFichePoste.
	 */
	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	/**
	 * Setter de l'attribut idFichePoste.
	 */
	public void setIdFichePoste(Integer newIdFichePoste) {
		idFichePoste = newIdFichePoste;
	}

	/**
	 * Getter de l'attribut fePrimaire.
	 */
	public boolean isFePrimaire() {
		return fePrimaire;
	}

	/**
	 * Setter de l'attribut fePrimaire.
	 */
	public void setFePrimaire(boolean newFePrimaire) {
		fePrimaire = newFePrimaire;
	}

	@Override
	public boolean equals(Object object) {
		return idFicheEmploi.toString().equals(((FEFP) object).getIdFicheEmploi().toString())
				&& idFichePoste.toString().equals(((FEFP) object).getIdFichePoste().toString());
	}
}
