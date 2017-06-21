package nc.mairie.metier.hsct;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeInaptitude
 */
public class TypeInaptitude {

	public Integer idTypeInaptitude;
	public String descTypeInaptitude;

	/**
	 * Constructeur TypeInaptitude.
	 */
	public TypeInaptitude() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeInaptitude.
	 */
	public Integer getIdTypeInaptitude() {
		return idTypeInaptitude;
	}

	/**
	 * Setter de l'attribut idTypeInaptitude.
	 */
	public void setIdTypeInaptitude(Integer newIdTypeInaptitude) {
		idTypeInaptitude = newIdTypeInaptitude;
	}

	/**
	 * Getter de l'attribut descTypeInaptitude.
	 */
	public String getDescTypeInaptitude() {
		return descTypeInaptitude == null ? Const.CHAINE_VIDE : descTypeInaptitude.trim();
	}

	/**
	 * Setter de l'attribut descTypeInaptitude.
	 */
	public void setDescTypeInaptitude(String newDescTypeInaptitude) {
		descTypeInaptitude = newDescTypeInaptitude;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeInaptitude.toString().equals(((TypeInaptitude) object).getIdTypeInaptitude().toString());
	}
}
