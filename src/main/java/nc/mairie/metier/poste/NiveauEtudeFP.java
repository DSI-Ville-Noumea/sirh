package nc.mairie.metier.poste;

/**
 * Objet metier NiveauEtudeFP
 */
public class NiveauEtudeFP {

	public Integer idNiveauEtude;
	public Integer idFichePoste;

	/**
	 * Constructeur NiveauEtudeFP.
	 */
	public NiveauEtudeFP() {
		super();
	}

	/**
	 * Constructeur NiveauEtudeFP.
	 */
	public NiveauEtudeFP(Integer newIdFichePoste, Integer newIdNiveauEtude) {
		super();

		setIdFichePoste(newIdFichePoste);
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

	@Override
	public boolean equals(Object object) {
		return idNiveauEtude.toString().equals(((NiveauEtudeFP) object).getIdNiveauEtude().toString())
				&& idFichePoste.toString().equals(((NiveauEtudeFP) object).getIdFichePoste().toString());
	}
}
