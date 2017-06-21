package nc.mairie.metier.referentiel;

import nc.mairie.metier.Const;

/**
 * Objet metier AutreAdministration
 */
public class AutreAdministration {

	public Integer idAutreAdmin;
	public String libAutreAdmin;

	/**
	 * Constructeur AutreAdministration.
	 */
	public AutreAdministration() {
		super();
	}

	public Integer getIdAutreAdmin() {
		return idAutreAdmin;
	}

	public void setIdAutreAdmin(Integer idAutreAdmin) {
		this.idAutreAdmin = idAutreAdmin;
	}

	/**
	 * Getter de l'attribut lib_autre_admin.
	 */
	public String getLibAutreAdmin() {
		return libAutreAdmin == null ? Const.CHAINE_VIDE : libAutreAdmin.trim();
	}

	/**
	 * Setter de l'attribut lib_autre_admin.
	 */
	public void setLibAutreAdmin(String newLibAutreAdmin) {
		libAutreAdmin = newLibAutreAdmin;
	}

	@Override
	public boolean equals(Object object) {
		return idAutreAdmin.toString().equals(((AutreAdministration) object).getIdAutreAdmin().toString());
	}
}
