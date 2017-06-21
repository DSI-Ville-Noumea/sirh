package nc.mairie.metier.specificites;

/**
 * Objet metier AvantageNatureAFF
 */
public class AvantageNatureAFF {

	public Integer idAvantage;
	public Integer idAffectation;

	/**
	 * Constructeur AvantageNatureAFF.
	 */
	public AvantageNatureAFF() {
		super();
	}

	/**
	 * Constructeur AvantageNatureAFF.
	 */
	public AvantageNatureAFF(Integer newIdAffectation, Integer newIdAvantage) {
		super();
		setIdAffectation(newIdAffectation);
		setIdAvantage(newIdAvantage);
	}

	/**
	 * Getter de l'attribut idAvantage.
	 */
	public Integer getIdAvantage() {
		return idAvantage;
	}

	/**
	 * Setter de l'attribut idAvantage.
	 */
	public void setIdAvantage(Integer newIdAvantage) {
		idAvantage = newIdAvantage;
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
		return idAvantage.toString().equals(((AvantageNatureAFF) object).getIdAvantage().toString())
				&& idAffectation.toString().equals(((AvantageNatureAFF) object).getIdAffectation().toString());
	}
}
