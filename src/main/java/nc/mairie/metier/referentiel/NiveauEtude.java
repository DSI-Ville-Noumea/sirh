package nc.mairie.metier.referentiel;

import nc.mairie.metier.Const;

/**
 * Objet metier NiveauEtude
 */
public class NiveauEtude {

	public Integer idNiveauEtude;
	public String codeNiveauEtude;

	/**
	 * Constructeur NiveauEtude.
	 */
	public NiveauEtude() {
		super();
	}

	/**
	 * Getter de l'attribut idNiveauEtude.
	 */
	public Integer getIdNiveauEtude() {
		return idNiveauEtude;
	}

	/**
	 * Setter de l'attribut idNiveauEtude.
	 */
	public void setIdNiveauEtude(Integer newIdNiveauEtude) {
		idNiveauEtude = newIdNiveauEtude;
	}

	public String getCodeNiveauEtude() {
		return codeNiveauEtude == null ? Const.CHAINE_VIDE : codeNiveauEtude.trim();
	}

	public void setCodeNiveauEtude(String codeNiveauEtude) {
		this.codeNiveauEtude = codeNiveauEtude;
	}

	/**
	 * Surcharge de la methode equals.
	 * 
	 * @param object
	 *            NiveauEtude
	 * @return boolean
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return idNiveauEtude.toString().equals(((NiveauEtude) object).getIdNiveauEtude().toString());
	}
}
