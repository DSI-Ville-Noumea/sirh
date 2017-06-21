package nc.mairie.metier.specificites;

/**
 * Objet metier DelegationFP
 */
public class DelegationFP {

	public Integer idDelegation;
	public Integer idFichePoste;

	/**
	 * Constructeur DelegationFP.
	 */
	public DelegationFP() {
		super();
	}

	/**
	 * Constructeur DelegationFP.
	 */
	public DelegationFP(Integer newIdFichePoste, Integer newIdDelegation) {
		super();
		setIdFichePoste(newIdFichePoste);
		setIdDelegation(newIdDelegation);
	}

	/**
	 * Getter de l'attribut idDelegation.
	 */
	public Integer getIdDelegation() {
		return idDelegation;
	}

	/**
	 * Setter de l'attribut idDelegation.
	 */
	public void setIdDelegation(Integer newIdDelegation) {
		idDelegation = newIdDelegation;
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

	@Override
	public boolean equals(Object object) {
		return idDelegation.toString().equals(((DelegationFP) object).getIdDelegation().toString())
				&& idFichePoste.toString().equals(((DelegationFP) object).getIdFichePoste().toString());
	}
}
