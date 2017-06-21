package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeDelegation
 */
public class TypeDelegation {

	public Integer idTypeDelegation;
	public String libTypeDelegation;

	/**
	 * Constructeur TypeDelegation.
	 */
	public TypeDelegation() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeDelegation.
	 */
	public Integer getIdTypeDelegation() {
		return idTypeDelegation;
	}

	/**
	 * Setter de l'attribut idTypeDelegation.
	 */
	public void setIdTypeDelegation(Integer newIdTypeDelegation) {
		idTypeDelegation = newIdTypeDelegation;
	}

	/**
	 * Getter de l'attribut libTypeDelegation.
	 */
	public String getLibTypeDelegation() {
		return libTypeDelegation == null ? Const.CHAINE_VIDE : libTypeDelegation.trim();
	}

	/**
	 * Setter de l'attribut libTypeDelegation.
	 */
	public void setLibTypeDelegation(String newLibTypeDelegation) {
		libTypeDelegation = newLibTypeDelegation;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeDelegation.toString().equals(((TypeDelegation) object).getIdTypeDelegation().toString());
	}
}
