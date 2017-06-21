package nc.mairie.metier.specificites;

/**
 * Objet metier RegIndemnAFF
 */
public class RegIndemnAFF {

	public Integer idRegime;
	public Integer idAffectation;

	/**
	 * Constructeur RegIndemnAFF.
	 */
	public RegIndemnAFF() {
		super();
	}

	/**
	 * Constructeur RegIndemnAFF.
	 */
	public RegIndemnAFF(Integer newIdAffectation, Integer newIdRegime) {
		super();
		setIdAffectation(newIdAffectation);
		setIdRegime(newIdRegime);
	}

	/**
	 * Getter de l'attribut idRegime.
	 */
	public Integer getIdRegime() {
		return idRegime;
	}

	/**
	 * Setter de l'attribut idRegime.
	 */
	public void setIdRegime(Integer newIdRegime) {
		idRegime = newIdRegime;
	}

	/**
	 * Getter de l'attribut idAffectation.
	 */
	public Integer getIdAffectation() {
		return idAffectation;
	}

	/**
	 * Setter de l'attribut idAffectation.
	 */
	public void setIdAffectation(Integer newIdAffectation) {
		idAffectation = newIdAffectation;
	}

	@Override
	public boolean equals(Object object) {
		return idRegime.toString().equals(((RegIndemnAFF) object).getIdRegime().toString())
				&& idAffectation.toString().equals(((RegIndemnAFF) object).getIdAffectation().toString());
	}
}
