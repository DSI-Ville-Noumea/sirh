package nc.mairie.metier.hsct;

import nc.mairie.metier.Const;

/**
 * Objet metier Medecin
 */
public class Medecin {

	public Integer idMedecin;
	public String nomMedecin;
	public String prenomMedecin;
	public String titreMedecin;

	/**
	 * Constructeur Medecin.
	 */
	public Medecin() {
		super();
	}

	/**
	 * Getter de l'attribut idMedecin.
	 */
	public Integer getIdMedecin() {
		return idMedecin;
	}

	/**
	 * Setter de l'attribut idMedecin.
	 */
	public void setIdMedecin(Integer newIdMedecin) {
		idMedecin = newIdMedecin;
	}

	/**
	 * Getter de l'attribut nomMedecin.
	 */
	public String getNomMedecin() {
		return nomMedecin == null ? Const.CHAINE_VIDE : nomMedecin.trim();
	}

	/**
	 * Setter de l'attribut nomMedecin.
	 */
	public void setNomMedecin(String newNomMedecin) {
		nomMedecin = newNomMedecin;
	}

	public String getPrenomMedecin() {
		return prenomMedecin == null ? Const.CHAINE_VIDE : prenomMedecin.trim();
	}

	public void setPrenomMedecin(String prenomMedecin) {
		this.prenomMedecin = prenomMedecin;
	}

	public String getTitreMedecin() {
		return titreMedecin == null ? Const.CHAINE_VIDE : titreMedecin.trim();
	}

	public void setTitreMedecin(String titreMedecin) {
		this.titreMedecin = titreMedecin;
	}

	@Override
	public boolean equals(Object object) {
		return idMedecin.toString().equals(((Medecin) object).getIdMedecin().toString());
	}
}
