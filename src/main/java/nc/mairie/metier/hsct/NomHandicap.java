package nc.mairie.metier.hsct;

import nc.mairie.metier.Const;

/**
 * Objet metier NomHandicap
 */
public class NomHandicap {

	public Integer idTypeHandicap;
	public String nomTypeHandicap;

	/**
	 * Constructeur NomHandicap.
	 */
	public NomHandicap() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeHandicap.
	 */
	public Integer getIdTypeHandicap() {
		return idTypeHandicap;
	}

	/**
	 * Setter de l'attribut idTypeHandicap.
	 */
	public void setIdTypeHandicap(Integer newIdTypeHandicap) {
		idTypeHandicap = newIdTypeHandicap;
	}

	/**
	 * Getter de l'attribut nomTypeHandicap.
	 */
	public String getNomTypeHandicap() {
		return nomTypeHandicap == null ? Const.CHAINE_VIDE : nomTypeHandicap.trim();
	}

	/**
	 * Setter de l'attribut nomTypeHandicap.
	 */
	public void setNomTypeHandicap(String newNomTypeHandicap) {
		nomTypeHandicap = newNomTypeHandicap;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeHandicap.toString().equals(((NomHandicap) object).getIdTypeHandicap().toString());
	}
}
