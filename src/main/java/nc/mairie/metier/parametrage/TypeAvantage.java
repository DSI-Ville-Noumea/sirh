package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeAvantage
 */
public class TypeAvantage {

	public Integer idTypeAvantage;
	public String libTypeAvantage;

	/**
	 * Constructeur TypeAvantage.
	 */
	public TypeAvantage() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeAvantage.
	 */
	public Integer getIdTypeAvantage() {
		return idTypeAvantage;
	}

	/**
	 * Setter de l'attribut idTypeAvantage.
	 */
	public void setIdTypeAvantage(Integer newIdTypeAvantage) {
		idTypeAvantage = newIdTypeAvantage;
	}

	/**
	 * Getter de l'attribut libTypeAvantage.
	 */
	public String getLibTypeAvantage() {
		return libTypeAvantage == null ? Const.CHAINE_VIDE : libTypeAvantage.trim();
	}

	/**
	 * Setter de l'attribut libTypeAvantage.
	 */
	public void setLibTypeAvantage(String newLibTypeAvantage) {
		libTypeAvantage = newLibTypeAvantage;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeAvantage.toString().equals(((TypeAvantage) object).getIdTypeAvantage().toString());
	}
}
