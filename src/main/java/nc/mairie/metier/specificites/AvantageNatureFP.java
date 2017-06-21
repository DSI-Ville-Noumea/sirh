package nc.mairie.metier.specificites;

/**
 * Objet metier AvantageNatureFP
 */
public class AvantageNatureFP {

	public Integer idFichePoste;
	public Integer idAvantage;

	/**
	 * Constructeur AvantageNatureFP.
	 */
	public AvantageNatureFP() {
		super();
	}

	/**
	 * Constructeur AvantageNatureFP.
	 */
	public AvantageNatureFP(Integer newIdFichePoste, Integer newIdAvantage) {
		super();
		setIdFichePoste(newIdFichePoste);
		setIdAvantage(newIdAvantage);
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

	@Override
	public boolean equals(Object object) {
		return idAvantage.toString().equals(((AvantageNatureFP) object).getIdAvantage().toString())
				&& idFichePoste.toString().equals(((AvantageNatureFP) object).getIdFichePoste().toString());
	}
}
