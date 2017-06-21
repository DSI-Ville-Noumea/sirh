package nc.mairie.metier.poste;

import nc.mairie.metier.Const;

/**
 * Objet metier Competence
 */
public class Competence {

	public Integer idCompetence;
	public Integer idTypeCompetence;
	public String nomCompetence;

	/**
	 * Constructeur Competence.
	 */
	public Competence() {
		super();
	}

	/**
	 * Constructeur Competence.
	 */
	public Competence(Integer idType, String descCompetence) {
		super();
		setIdTypeCompetence(idType);
		setNomCompetence(descCompetence);
	}

	/**
	 * Getter de l'attribut idCompetence.
	 */
	public Integer getIdCompetence() {
		return idCompetence;
	}

	/**
	 * Setter de l'attribut idCompetence.
	 */
	public void setIdCompetence(Integer newIdCompetence) {
		idCompetence = newIdCompetence;
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
	 * Getter de l'attribut nomCompetence.
	 */
	public String getNomCompetence() {
		return nomCompetence == null ? Const.CHAINE_VIDE : nomCompetence.trim();
	}

	/**
	 * Setter de l'attribut nomCompetence.
	 */
	public void setNomCompetence(String newNomCompetence) {
		nomCompetence = newNomCompetence;
	}

	@Override
	public boolean equals(Object object) {
		return idCompetence.toString().equals(((Competence) object).getIdCompetence().toString());
	}
}
