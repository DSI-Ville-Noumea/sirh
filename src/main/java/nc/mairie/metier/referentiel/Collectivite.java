package nc.mairie.metier.referentiel;

/**
 * Objet metier Collectivite
 */
public class Collectivite {

	public Integer idCollectivite;
	public String codeCollectivite;
	public String libCourtCollectivite;
	public String libLongCollectivite;

	/**
	 * Constructeur Collectivite.
	 */
	public Collectivite() {
		super();
	}

	/**
	 * Getter de l'attribut idCollectivite.
	 */
	public Integer getIdCollectivite() {
		return idCollectivite;
	}

	/**
	 * Setter de l'attribut idCollectivite.
	 */
	public void setIdCollectivite(Integer newIdCollectivite) {
		idCollectivite = newIdCollectivite;
	}

	/**
	 * Getter de l'attribut codeCollectivite.
	 */
	public String getCodeCollectivite() {
		return codeCollectivite;
	}

	/**
	 * Setter de l'attribut codeCollectivite.
	 */
	public void setCodeCollectivite(String newCodeCollectivite) {
		codeCollectivite = newCodeCollectivite;
	}

	/**
	 * Getter de l'attribut libCourtCollectivite.
	 */
	public String getLibCourtCollectivite() {
		return libCourtCollectivite;
	}

	/**
	 * Setter de l'attribut libCourtCollectivite.
	 */
	public void setLibCourtCollectivite(String newLibCourtCollectivite) {
		libCourtCollectivite = newLibCourtCollectivite;
	}

	/**
	 * Getter de l'attribut libLongCollectivite.
	 */
	public String getLibLongCollectivite() {
		return libLongCollectivite;
	}

	/**
	 * Setter de l'attribut libLongCollectivite.
	 */
	public void setLibLongCollectivite(String newLibLongCollectivite) {
		libLongCollectivite = newLibLongCollectivite;
	}

	@Override
	public boolean equals(Object object) {
		return idCollectivite.toString().equals(((Collectivite) object).getIdCollectivite().toString());
	}
}
