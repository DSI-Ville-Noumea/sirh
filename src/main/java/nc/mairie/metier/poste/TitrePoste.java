package nc.mairie.metier.poste;

import nc.mairie.metier.Const;

/**
 * Objet metier TitrePoste
 */
public class TitrePoste {

	public Integer idTitrePoste;
	public String libTitrePoste;
	public String libTitreLong;
	public String libTitreCourt;

	/**
	 * Constructeur TitrePoste.
	 */
	public TitrePoste() {
		super();
	}

	/**
	 * Getter de l'attribut idTitrePoste.
	 */
	public Integer getIdTitrePoste() {
		return idTitrePoste;
	}

	/**
	 * Setter de l'attribut idTitrePoste.
	 */
	public void setIdTitrePoste(Integer newIdTitrePoste) {
		idTitrePoste = newIdTitrePoste;
	}

	/**
	 * Getter de l'attribut libTitrePoste.
	 */
	public String getLibTitrePoste() {
		return libTitrePoste == null ? Const.CHAINE_VIDE : libTitrePoste.trim();
	}

	/**
	 * Setter de l'attribut libTitrePoste.
	 */
	public void setLibTitrePoste(String newLibTitrePoste) {
		libTitrePoste = newLibTitrePoste;
	}

	public String getLibTitreLong() {
		return libTitreLong;
	}

	public void setLibTitreLong(String libTitreLong) {
		this.libTitreLong = libTitreLong;
	}

	public String getLibTitreCourt() {
		return libTitreCourt;
	}

	public void setLibTitreCourt(String libTitreCourt) {
		this.libTitreCourt = libTitreCourt;
	}

	@Override
	public boolean equals(Object object) {
		return idTitrePoste.toString().equals(((TitrePoste) object).getIdTitrePoste().toString());
	}
}
