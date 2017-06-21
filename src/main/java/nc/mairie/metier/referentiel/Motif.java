package nc.mairie.metier.referentiel;

import nc.mairie.metier.Const;

/**
 * Objet metier Motif
 */
public class Motif {

	public Integer idMotif;
	public String libMotif;

	/**
	 * Constructeur Motif.
	 */
	public Motif() {
		super();
	}

	/**
	 * Getter de l'attribut idMotif.
	 */
	public Integer getIdMotif() {
		return idMotif;
	}

	/**
	 * Setter de l'attribut idMotif.
	 */
	public void setIdMotif(Integer newIdMotif) {
		idMotif = newIdMotif;
	}

	/**
	 * Getter de l'attribut libMotif.
	 */
	public String getLibMotif() {
		return libMotif == null ? Const.CHAINE_VIDE : libMotif.trim();
	}

	/**
	 * Setter de l'attribut libMotif.
	 */
	public void setLibMotif(String newLibMotif) {
		libMotif = newLibMotif;
	}

	@Override
	public boolean equals(Object object) {
		return idMotif.toString().equals(((Motif) object).getIdMotif().toString());
	}
}
