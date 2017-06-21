package nc.mairie.metier.referentiel;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeContrat
 */
public class TypeContrat {

	public Integer idTypeContrat;
	public String libTypeContrat;

	/**
	 * Constructeur TypeContrat.
	 */
	public TypeContrat() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeContrat.
	 */
	public Integer getIdTypeContrat() {
		return idTypeContrat;
	}

	/**
	 * Setter de l'attribut idTypeContrat.
	 */
	public void setIdTypeContrat(Integer newIdTypeContrat) {
		idTypeContrat = newIdTypeContrat;
	}

	/**
	 * Getter de l'attribut libTypeContrat.
	 */
	public String getLibTypeContrat() {
		return libTypeContrat == null ? Const.CHAINE_VIDE : libTypeContrat.trim();
	}

	/**
	 * Setter de l'attribut libTypeContrat.
	 */
	public void setLibTypeContrat(String newLibTypeContrat) {
		libTypeContrat = newLibTypeContrat;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeContrat.toString().equals(((TypeContrat) object).getIdTypeContrat().toString());
	}
}
