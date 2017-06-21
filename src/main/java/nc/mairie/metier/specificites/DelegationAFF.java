package nc.mairie.metier.specificites;

/**
 * Objet metier DelegationAFF
 */
public class DelegationAFF {

	public Integer idAffectation;
	public Integer idDelegation;

	/**
	 * Constructeur DelegationAFF.
	 */
	public DelegationAFF() {
		super();
	}

	/**
	 * Constructeur DelegationAFF.
	 */
	public DelegationAFF(Integer newIdAffectation, Integer newIdDelegation) {
		super();
		setIdAffectation(newIdAffectation);
		setIdDelegation(newIdDelegation);
	}

	/**
	 * Getter de l'attribut idAffectation.
	 */
	public Integer getIdAffectation() {
		return idAffectation;
	}

	/**
	 * Setter de l'attribut idAffectation.
	 */
	public void setIdAffectation(Integer newIdAffectation) {
		idAffectation = newIdAffectation;
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

	@Override
	public boolean equals(Object object) {
		return idDelegation.toString().equals(((DelegationAFF) object).getIdDelegation().toString())
				&& idAffectation.toString().equals(((DelegationAFF) object).getIdAffectation().toString());
	}
}
