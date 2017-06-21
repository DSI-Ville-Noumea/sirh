package nc.mairie.metier.poste;

import nc.mairie.metier.Const;

/**
 * Objet metier StatutFP
 */
public class StatutFP {

	public Integer idStatutFp;
	public String libStatutFp;

	/**
	 * Constructeur StatutFP.
	 */
	public StatutFP() {
		super();
	}

	/**
	 * Getter de l'attribut idStatutFP.
	 */
	public Integer getIdStatutFp() {
		return idStatutFp;
	}

	/**
	 * Setter de l'attribut idStatutFP.
	 */
	public void setIdStatutFp(Integer newIdStatutFP) {
		idStatutFp = newIdStatutFP;
	}

	/**
	 * Getter de l'attribut libStatutFP.
	 */
	public String getLibStatutFp() {
		return libStatutFp == null ? Const.CHAINE_VIDE : libStatutFp.trim();
	}

	/**
	 * Setter de l'attribut libStatutFP.
	 */
	public void setLibStatutFp(String newLibStatutFP) {
		libStatutFp = newLibStatutFP;
	}

	@Override
	public boolean equals(Object object) {
		return idStatutFp.toString().equals(((StatutFP) object).getIdStatutFp().toString());
	}
}
