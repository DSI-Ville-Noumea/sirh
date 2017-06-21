package nc.mairie.metier.droits;

import nc.mairie.metier.Const;

/**
 * Objet metier Utilisateur
 */
public class Utilisateur {

	public Integer idUtilisateur;
	public String loginUtilisateur;

	/**
	 * Constructeur Utilisateur.
	 */
	public Utilisateur() {
		super();
	}

	/**
	 * Getter de l'attribut idUtilisateur.
	 */
	public Integer getIdUtilisateur() {
		return idUtilisateur;
	}

	/**
	 * Setter de l'attribut idUtilisateur.
	 */
	public void setIdUtilisateur(Integer newIdUtilisateur) {
		idUtilisateur = newIdUtilisateur;
	}

	/**
	 * Getter de l'attribut loginUtilisateur.
	 */
	public String getLoginUtilisateur() {
		return loginUtilisateur == null ? Const.CHAINE_VIDE : loginUtilisateur.trim();
	}

	/**
	 * Setter de l'attribut loginUtilisateur.
	 */
	public void setLoginUtilisateur(String newLoginUtilisateur) {
		loginUtilisateur = newLoginUtilisateur;
	}

	@Override
	public boolean equals(Object object) {
		return idUtilisateur.toString().equals(((Utilisateur) object).getIdUtilisateur().toString());
	}
}
