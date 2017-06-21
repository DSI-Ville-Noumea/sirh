package nc.mairie.metier.referentiel;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeCompetence
 */
public class TypeCompetence {

	public Integer idTypeCompetence;
	public String libTypeCompetence;

	/**
	 * Constructeur TypeCompetence.
	 */
	public TypeCompetence() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeCompetence.
	 */
	public Integer getIdTypeCompetence() {
		return idTypeCompetence;
	}

	/**
	 * Setter de l'attribut idTypeCompetence.
	 */
	public void setIdTypeCompetence(Integer newIdTypeCompetence) {
		idTypeCompetence = newIdTypeCompetence;
	}

	/**
	 * Getter de l'attribut libTypeCompetence.
	 */
	public String getLibTypeCompetence() {
		return libTypeCompetence == null ? Const.CHAINE_VIDE : libTypeCompetence.trim();
	}

	/**
	 * Setter de l'attribut libTypeCompetence.
	 */
	public void setLibTypeCompetence(String newLibTypeCompetence) {
		libTypeCompetence = newLibTypeCompetence;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeCompetence.toString().equals(((TypeCompetence) object).getIdTypeCompetence().toString());
	}
}
