package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier SpecialiteDiplome
 */
public class SpecialiteDiplome {

	public Integer idSpecialiteDiplome;
	public String libSpecialiteDiplome;

	/**
	 * Constructeur SpecialiteDiplome
	 */
	public SpecialiteDiplome() {
		super();
	}

	public Integer getIdSpecialiteDiplome() {
		return idSpecialiteDiplome;
	}

	public void setIdSpecialiteDiplome(Integer idSpecialiteDiplome) {
		this.idSpecialiteDiplome = idSpecialiteDiplome;
	}

	public String getLibSpecialiteDiplome() {
		return libSpecialiteDiplome == null ? Const.CHAINE_VIDE : libSpecialiteDiplome.trim();
	}

	public void setLibSpecialiteDiplome(String libSpecialiteDiplome) {
		this.libSpecialiteDiplome = libSpecialiteDiplome;
	}

	@Override
	public boolean equals(Object object) {
		return idSpecialiteDiplome.toString().equals(((SpecialiteDiplome) object).getIdSpecialiteDiplome().toString());
	}
}
