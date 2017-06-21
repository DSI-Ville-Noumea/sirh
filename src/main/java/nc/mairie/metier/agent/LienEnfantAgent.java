package nc.mairie.metier.agent;

public class LienEnfantAgent {

	public Integer idAgent;
	public Integer idEnfant;
	public boolean enfantACharge;

	public LienEnfantAgent() {
		super();
	}

	public LienEnfantAgent(Integer newIdAgent, Integer newIdEnfant, boolean newEnfantACharge) {
		super();
		setIdAgent(newIdAgent);
		setIdEnfant(newIdEnfant);
		setEnfantACharge(newEnfantACharge);
	}

	/**
	 * Getter de l'attribut idAgent.
	 */
	public Integer getIdAgent() {
		return idAgent;
	}

	/**
	 * Setter de l'attribut idAgent.
	 */
	public void setIdAgent(Integer newIdAgent) {
		idAgent = newIdAgent;
	}

	/**
	 * Getter de l'attribut idEnfant.
	 */
	public Integer getIdEnfant() {
		return idEnfant;
	}

	/**
	 * Setter de l'attribut idEnfant.
	 */
	public void setIdEnfant(Integer newIdEnfant) {
		idEnfant = newIdEnfant;
	}

	/**
	 * Getter de l'attribut enfantACharge.
	 */
	public boolean isEnfantACharge() {
		return enfantACharge;
	}

	/**
	 * Setter de l'attribut enfantACharge.
	 */
	public void setEnfantACharge(boolean newEnfantACharge) {
		enfantACharge = newEnfantACharge;
	}

	@Override
	public boolean equals(Object object) {
		return idEnfant.toString().equals(((LienEnfantAgent) object).getIdEnfant().toString())
				&& idAgent.toString().equals(((LienEnfantAgent) object).getIdAgent().toString());
	}
}
