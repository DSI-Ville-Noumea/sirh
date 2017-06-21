package nc.mairie.metier.droits;

/**
 * Objet metier GroupeUtilisateur
 */
public class GroupeUtilisateur {

	public Integer idUtilisateur;
	public Integer idGroupe;

	/**
	 * Constructeur GroupeUtilisateur.
	 */
	public GroupeUtilisateur() {
		super();
	}

	/**
	 * Constructeur GroupeUtilisateur.
	 */
	public GroupeUtilisateur(Integer newIdUtilisateur, Integer newIdGroupe) {
		super();
		setIdUtilisateur(newIdUtilisateur);
		setIdGroupe(newIdGroupe);
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
	 * Getter de l'attribut idGroupe.
	 */
	public Integer getIdGroupe() {
		return idGroupe;
	}

	/**
	 * Setter de l'attribut idGroupe.
	 */
	public void setIdGroupe(Integer newIdGroupe) {
		idGroupe = newIdGroupe;
	}

	@Override
	public boolean equals(Object object) {
		return idUtilisateur.toString().equals(((GroupeUtilisateur) object).getIdUtilisateur().toString())
				&& idGroupe.toString().equals(((GroupeUtilisateur) object).getIdGroupe().toString());
	}
}
