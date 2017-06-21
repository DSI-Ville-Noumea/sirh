package nc.mairie.metier.poste;

/**
 * Objet metier DiplomeFE
 */
public class DiplomeFE {

	public Integer idDiplomeGenerique;
	public Integer idFicheEmploi;

	/**
	 * Constructeur DiplomeFE.
	 */
	public DiplomeFE() {
		super();
	}

	/**
	 * Constructeur DiplomeFE.
	 */
	public DiplomeFE(Integer newIdFicheEmploi, Integer newIdDiplomeGenerique) {
		super();
		setIdFicheEmploi(newIdFicheEmploi);
		setIdDiplomeGenerique(newIdDiplomeGenerique);
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

	@Override
	public boolean equals(Object object) {
		return idDiplomeGenerique.toString().equals(((DiplomeFE) object).getIdDiplomeGenerique().toString())
				&& idFicheEmploi.toString().equals(((DiplomeFE) object).getIdFicheEmploi().toString());
	}
}
