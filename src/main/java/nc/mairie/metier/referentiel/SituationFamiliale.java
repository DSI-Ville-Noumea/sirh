package nc.mairie.metier.referentiel;

/**
 * Objet metier SituationFamiliale
 */
public class SituationFamiliale {

	public Integer idSituation;
	public String codeSituation;
	public String libSituation;

	/**
	 * Constructeur SituationFamiliale.
	 */
	public SituationFamiliale() {
		super();
	}

	/**
	 * Getter de l'attribut idSituation.
	 */
	public Integer getIdSituation() {
		return idSituation;
	}

	/**
	 * Setter de l'attribut idSituation.
	 */
	public void setIdSituation(Integer newIdSituation) {
		idSituation = newIdSituation;
	}

	/**
	 * Getter de l'attribut codeSituation.
	 */
	public String getCodeSituation() {
		return codeSituation;
	}

	/**
	 * Setter de l'attribut codeSituation.
	 */
	public void setCodeSituation(String newCodeSituation) {
		codeSituation = newCodeSituation;
	}

	/**
	 * Getter de l'attribut libSituation.
	 */
	public String getLibSituation() {
		return libSituation;
	}

	/**
	 * Setter de l'attribut libSituation.
	 */
	public void setLibSituation(String newLibSituation) {
		libSituation = newLibSituation;
	}

	@Override
	public boolean equals(Object object) {
		return idSituation.toString().equals(((SituationFamiliale) object).getIdSituation().toString());
	}
}
