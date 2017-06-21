package nc.mairie.metier.hsct;

import nc.mairie.metier.Const;

/**
 * Objet metier Recommandation
 */
public class Recommandation {

	public Integer idRecommandation;
	public String descRecommandation;

	/**
	 * Constructeur Recommandation.
	 */
	public Recommandation() {
		super();
	}

	/**
	 * Getter de l'attribut idRecommandation.
	 */
	public Integer getIdRecommandation() {
		return idRecommandation;
	}

	/**
	 * Setter de l'attribut idRecommandation.
	 */
	public void setIdRecommandation(Integer newIdRecommandation) {
		idRecommandation = newIdRecommandation;
	}

	/**
	 * Getter de l'attribut descRecommandation.
	 */
	public String getDescRecommandation() {
		return descRecommandation == null ? Const.CHAINE_VIDE : descRecommandation.trim();
	}

	/**
	 * Setter de l'attribut descRecommandation.
	 */
	public void setDescRecommandation(String newDescRecommandation) {
		descRecommandation = newDescRecommandation;
	}

	@Override
	public boolean equals(Object object) {
		return idRecommandation.toString().equals(((Recommandation) object).getIdRecommandation().toString());
	}
}
