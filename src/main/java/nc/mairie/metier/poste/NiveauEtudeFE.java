package nc.mairie.metier.poste;

/**
 * Objet metier NiveauEtudeFE
 */
public class NiveauEtudeFE {

	public Integer idNiveauEtude;
	public Integer idFicheEmploi;

	/**
	 * Constructeur NiveauEtudeFE.
	 */
	public NiveauEtudeFE() {
		super();
	}

	/**
	 * Constructeur NiveauEtudeFE.
	 */
	public NiveauEtudeFE(Integer newIdFicheEmploi, Integer newIdNiveauEtude) {
		super();
		setIdFicheEmploi(newIdFicheEmploi);
		setIdNiveauEtude(newIdNiveauEtude);
	}

	/**
	 * Getter de l'attribut idNiveauEtude.
	 */
	public Integer getIdNiveauEtude() {
		return idNiveauEtude;
	}

	/**
	 * Setter de l'attribut idNiveauEtude.
	 */
	public void setIdNiveauEtude(Integer newIdNiveauEtude) {
		idNiveauEtude = newIdNiveauEtude;
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
		return idNiveauEtude.toString().equals(((NiveauEtudeFE) object).getIdNiveauEtude().toString())
				&& idFicheEmploi.toString().equals(((NiveauEtudeFE) object).getIdFicheEmploi().toString());
	}
}
