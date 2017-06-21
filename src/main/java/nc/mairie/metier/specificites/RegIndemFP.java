package nc.mairie.metier.specificites;

/**
 * Objet metier RegIndemFP
 */
public class RegIndemFP {

	public Integer idFichePoste;
	public Integer idRegIndemn;

	/**
	 * Constructeur RegIndemFP.
	 */
	public RegIndemFP() {
		super();
	}

	/**
	 * Constructeur RegIndemFP.
	 */
	public RegIndemFP(Integer newIdFichePoste, Integer newIdRegIndemn) {
		super();
		setIdFichePoste(newIdFichePoste);
		setIdRegIndemn(newIdRegIndemn);
	}

	/**
	 * Getter de l'attribut idFichePoste.
	 */
	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	/**
	 * Setter de l'attribut idFichePoste.
	 */
	public void setIdFichePoste(Integer newIdFichePoste) {
		idFichePoste = newIdFichePoste;
	}

	/**
	 * Getter de l'attribut idRegIndemn.
	 */
	public Integer getIdRegIndemn() {
		return idRegIndemn;
	}

	/**
	 * Setter de l'attribut idRegIndemn.
	 */
	public void setIdRegIndemn(Integer newIdRegIndemn) {
		idRegIndemn = newIdRegIndemn;
	}

	@Override
	public boolean equals(Object object) {
		return idRegIndemn.toString().equals(((RegIndemFP) object).getIdRegIndemn().toString())
				&& idFichePoste.toString().equals(((RegIndemFP) object).getIdFichePoste().toString());
	}
}
