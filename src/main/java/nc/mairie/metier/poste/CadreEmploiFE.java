package nc.mairie.metier.poste;

/**
 * Objet metier CadreEmploiFE
 */
public class CadreEmploiFE {
	public Integer idCadreEmploi;
	public Integer idFicheEmploi;

	/**
	 * Constructeur CadreEmploiFE.
	 */
	public CadreEmploiFE() {
		super();
	}

	/**
	 * Constructeur CadreEmploiFE.
	 */
	public CadreEmploiFE(Integer newIdFicheEmploi, Integer newIdCadreEmploi) {
		super();
		setIdCadreEmploi(newIdCadreEmploi);
		setIdFicheEmploi(newIdFicheEmploi);
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
		return idCadreEmploi.toString().equals(((CadreEmploiFE) object).getIdCadreEmploi().toString())
				&& idFicheEmploi.toString().equals(((CadreEmploiFE) object).getIdFicheEmploi().toString());
	}
}
