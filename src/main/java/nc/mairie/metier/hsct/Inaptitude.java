package nc.mairie.metier.hsct;

import java.util.Date;

/**
 * Objet metier Inaptitude
 */
public class Inaptitude {

	public Integer idInaptitude;
	public Integer idVisite;
	public Integer idTypeInaptitude;
	public Date dateDebutInaptitude;
	public Integer dureeAnnee;
	public Integer dureeMois;
	public Integer dureeJour;

	/**
	 * Constructeur Inaptitude.
	 */
	public Inaptitude() {
		super();
	}

	/**
	 * Getter de l'attribut idInaptitude.
	 */
	public Integer getIdInaptitude() {
		return idInaptitude;
	}

	/**
	 * Setter de l'attribut idInaptitude.
	 */
	public void setIdInaptitude(Integer newIdInaptitude) {
		idInaptitude = newIdInaptitude;
	}

	/**
	 * Getter de l'attribut idVisite.
	 */
	public Integer getIdVisite() {
		return idVisite;
	}

	/**
	 * Setter de l'attribut idVisite.
	 */
	public void setIdVisite(Integer newIdVisite) {
		idVisite = newIdVisite;
	}

	/**
	 * Getter de l'attribut idTypeInaptitude.
	 */
	public Integer getIdTypeInaptitude() {
		return idTypeInaptitude;
	}

	/**
	 * Setter de l'attribut idTypeInaptitude.
	 */
	public void setIdTypeInaptitude(Integer newIdTypeInaptitude) {
		idTypeInaptitude = newIdTypeInaptitude;
	}

	/**
	 * Getter de l'attribut dateDebutInaptitude.
	 */
	public Date getDateDebutInaptitude() {
		return dateDebutInaptitude;
	}

	/**
	 * Setter de l'attribut dateDebutInaptitude.
	 */
	public void setDateDebutInaptitude(Date newDateDebutInaptitude) {
		dateDebutInaptitude = newDateDebutInaptitude;
	}

	/**
	 * Getter de l'attribut dureeAnnee.
	 */
	public Integer getDureeAnnee() {
		return dureeAnnee;
	}

	/**
	 * Setter de l'attribut dureeAnnee.
	 */
	public void setDureeAnnee(Integer newDureeAnnee) {
		dureeAnnee = newDureeAnnee;
	}

	/**
	 * Getter de l'attribut dureeMois.
	 */
	public Integer getDureeMois() {
		return dureeMois;
	}

	/**
	 * Setter de l'attribut dureeMois.
	 */
	public void setDureeMois(Integer newDureeMois) {
		dureeMois = newDureeMois;
	}

	/**
	 * Getter de l'attribut dureeJour.
	 */
	public Integer getDureeJour() {
		return dureeJour;
	}

	/**
	 * Setter de l'attribut dureeJour.
	 */
	public void setDureeJour(Integer newDureeJour) {
		dureeJour = newDureeJour;
	}

}
