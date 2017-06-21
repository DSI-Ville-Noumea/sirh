package nc.mairie.metier.referentiel;

/**
 * Objet metier AvisCap
 */
public class AvisCap {

	public Integer idAvisCap;
	public String libCourtAvisCap;
	public String libLongAvisCap;

	/**
	 * Constructeur AvisCap.
	 */
	public AvisCap() {
		super();
	}

	public Integer getIdAvisCap() {
		return idAvisCap;
	}

	public void setIdAvisCap(Integer idAvisCap) {
		this.idAvisCap = idAvisCap;
	}

	public String getLibCourtAvisCap() {
		return libCourtAvisCap;
	}

	public void setLibCourtAvisCap(String libCourtAvisCap) {
		this.libCourtAvisCap = libCourtAvisCap;
	}

	public String getLibLongAvisCap() {
		return libLongAvisCap;
	}

	public void setLibLongAvisCap(String libLongAvisCap) {
		this.libLongAvisCap = libLongAvisCap;
	}

	@Override
	public boolean equals(Object object) {
		return idAvisCap.toString().equals(((AvisCap) object).getIdAvisCap().toString());
	}
}
