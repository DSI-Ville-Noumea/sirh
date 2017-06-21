package nc.mairie.metier.poste;

/**
 * Objet metier CompetenceFE
 */
public class CompetenceFE {

	public Integer idCompetence;
	public Integer idFicheEmploi;

	/**
	 * Constructeur CompetenceFE.
	 */
	public CompetenceFE() {
		super();
	}

	public CompetenceFE(Integer idFicheEmploi, Integer idCompetence) {
		super();
		setIdFicheEmploi(idFicheEmploi);
		setIdCompetence(idCompetence);
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
		return idCompetence.toString().equals(((CompetenceFE) object).getIdCompetence().toString())
				&& idFicheEmploi.toString().equals(((CompetenceFE) object).getIdFicheEmploi().toString());
	}
}
