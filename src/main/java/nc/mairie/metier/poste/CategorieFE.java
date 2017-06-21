package nc.mairie.metier.poste;

/**
 * Objet metier CategorieFE
 */
public class CategorieFE {

	public Integer idCategorieStatut;
	public Integer idFicheEmploi;

	/**
	 * Constructeur CategorieFE.
	 */
	public CategorieFE() {
		super();
	}

	/**
	 * Constructeur CategorieFE.
	 */
	public CategorieFE(Integer newIdFicheEmploi, Integer newIdCategorie) {
		super();
		setIdFicheEmploi(newIdFicheEmploi);
		setIdCategorieStatut(newIdCategorie);
	}

	/**
	 * Getter de l'attribut idCategorieStatut.
	 */
	public Integer getIdCategorieStatut() {
		return idCategorieStatut;
	}

	/**
	 * Setter de l'attribut idCategorieStatut.
	 */
	public void setIdCategorieStatut(Integer newIdCategorieStatut) {
		idCategorieStatut = newIdCategorieStatut;
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
		return idCategorieStatut.toString().equals(((CategorieFE) object).getIdCategorieStatut().toString())
				&& idFicheEmploi.toString().equals(((CategorieFE) object).getIdFicheEmploi().toString());
	}
}
