package nc.mairie.metier.poste;

/**
 * Objet metier CompetenceFP
 */
public class CompetenceFP {

	public Integer idCompetence;
	public Integer idFichePoste;

	/**
	 * Constructeur CompetenceFP.
	 */
	public CompetenceFP() {
		super();
	}

	/**
	 * Constructeur ActiviteFP.
	 */
	public CompetenceFP(Integer idFichePoste, Integer idCompetence) {
		super();
		this.idCompetence = idCompetence;
		this.idFichePoste = idFichePoste;
	}

	/**
	 * Getter de l'attribut idCompetence.
	 */
	public Integer getIdCompetence() {
		return idCompetence;
	}

	/**
	 * Setter de l'attribut idCompetence.
	 */
	public void setIdCompetence(Integer newIdCompetence) {
		idCompetence = newIdCompetence;
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
		return idCompetence.toString().equals(((CompetenceFP) object).getIdCompetence().toString())
				&& idFichePoste.toString().equals(((CompetenceFP) object).getIdFichePoste().toString());
	}
}
