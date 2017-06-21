package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier TitreDiplome
 */
public class TitreDiplome {

	public Integer idTitreDiplome;
	public String libTitreDiplome;
	public String niveauEtude;

	/**
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * 
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez
		// remplacer ou completer le message.
		return super.toString();
	}

	/**
	 * Constructeur TitreDiplome.
	 */
	public TitreDiplome() {
		super();
	}

	/**
	 * Getter de l'attribut idTitreDiplome.
	 */
	public Integer getIdTitreDiplome() {
		return idTitreDiplome;
	}

	/**
	 * Setter de l'attribut idTitreDiplome.
	 */
	public void setIdTitreDiplome(Integer newIdTitreDiplome) {
		idTitreDiplome = newIdTitreDiplome;
	}

	/**
	 * Getter de l'attribut libTitreDiplome.
	 */
	public String getLibTitreDiplome() {
		return libTitreDiplome == null ? Const.CHAINE_VIDE : libTitreDiplome.trim();
	}

	/**
	 * Setter de l'attribut libTitreDiplome.
	 */
	public void setLibTitreDiplome(String newLibTitreDiplome) {
		libTitreDiplome = newLibTitreDiplome;
	}

	/**
	 * Getter de l'attribut niveauEtude.
	 */
	public String getNiveauEtude() {
		return niveauEtude == null ? Const.CHAINE_VIDE : niveauEtude.trim();
	}

	/**
	 * Setter de l'attribut niveauEtude.
	 */
	public void setNiveauEtude(String newNiveauEtude) {
		niveauEtude = newNiveauEtude;
	}

	@Override
	public boolean equals(Object object) {
		return idTitreDiplome.toString().equals(((TitreDiplome) object).getIdTitreDiplome().toString());
	}
}
