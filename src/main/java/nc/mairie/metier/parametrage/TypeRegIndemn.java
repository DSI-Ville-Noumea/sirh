package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeRegIndemn
 */
public class TypeRegIndemn {

	public Integer idTypeRegIndemn;
	public String libTypeRegIndemn;

	/**
	 * Constructeur TypeRegIndemn.
	 */
	public TypeRegIndemn() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeRegIndemn.
	 */
	public Integer getIdTypeRegIndemn() {
		return idTypeRegIndemn;
	}

	/**
	 * Setter de l'attribut idTypeRegIndemn.
	 */
	public void setIdTypeRegIndemn(Integer newIdTypeRegIndemn) {
		idTypeRegIndemn = newIdTypeRegIndemn;
	}

	/**
	 * Getter de l'attribut libTypeRegIndemn.
	 */
	public String getLibTypeRegIndemn() {
		return libTypeRegIndemn == null ? Const.CHAINE_VIDE : libTypeRegIndemn.trim();
	}

	/**
	 * Setter de l'attribut libTypeRegIndemn.
	 */
	public void setLibTypeRegIndemn(String newLibTypeRegIndemn) {
		libTypeRegIndemn = newLibTypeRegIndemn;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeRegIndemn.toString().equals(((TypeRegIndemn) object).getIdTypeRegIndemn().toString());
	}
}
