package nc.mairie.metier.poste;

import nc.mairie.metier.Const;

/**
 * Objet metier Activite
 */
public class Activite {

	public Integer idActivite;
	public String nomActivite;

	/**
	 * Constructeur Activite.
	 */
	public Activite() {
		super();
	}

	/**
	 * Constructeur Activite.
	 */
	public Activite(String descActivite) {
		super();
		setNomActivite(descActivite);
	}

	/**
	 * Getter de l'attribut idActivite.
	 */
	public Integer getIdActivite() {
		return idActivite;
	}

	/**
	 * Setter de l'attribut idActivite.
	 */
	public void setIdActivite(Integer newIdActivite) {
		idActivite = newIdActivite;
	}

	/**
	 * Getter de l'attribut nomActivite.
	 */
	public String getNomActivite() {
		return nomActivite == null ? Const.CHAINE_VIDE : nomActivite.trim();
	}

	/**
	 * Setter de l'attribut nomActivite.
	 */
	public void setNomActivite(String newNomActivite) {
		nomActivite = newNomActivite;
	}

	@Override
	public boolean equals(Object obj) {
		return idActivite.toString().equals(((Activite) obj).getIdActivite().toString());
	}
}
