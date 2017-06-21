package nc.mairie.metier.agent;

import nc.mairie.metier.Const;

public class Contact {

	public Integer idContact;
	public Integer idAgent;
	public Integer idTypeContact;
	public String description;
	public boolean diffusable;
	public boolean prioritaire;

	public Contact() {
		super();
	}

	/**
	 * Getter de l'attribut idContact.
	 */
	public Integer getIdContact() {
		return idContact;
	}

	/**
	 * Setter de l'attribut idContact.
	 */
	public void setIdContact(Integer newIdContact) {
		idContact = newIdContact;
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
	 * Getter de l'attribut idTypeContact.
	 */
	public Integer getIdTypeContact() {
		return idTypeContact;
	}

	/**
	 * Setter de l'attribut idTypeContact.
	 */
	public void setIdTypeContact(Integer newIdTypeContact) {
		idTypeContact = newIdTypeContact;
	}

	/**
	 * Getter de l'attribut description.
	 */
	public String getDescription() {
		return description == null ? Const.CHAINE_VIDE : description.trim();
	}

	/**
	 * Setter de l'attribut description.
	 */
	public void setDescription(String newDescription) {
		description = newDescription;
	}

	/**
	 * Getter de l'attribut diffusable.
	 */
	public boolean isDiffusable() {
		return diffusable;
	}

	/**
	 * Setter de l'attribut diffusable.
	 */
	public void setDiffusable(boolean newDiffusable) {
		diffusable = newDiffusable;
	}

	/**
	 * Getter de l'attribut enfantACharge.
	 */
	public boolean isPrioritaire() {
		return prioritaire;
	}

	/**
	 * Setter de l'attribut enfantACharge.
	 */
	public void setPrioritaire(boolean newPrioritaire) {
		prioritaire = newPrioritaire;
	}

	@Override
	public boolean equals(Object object) {
		return idContact.toString().equals(((Contact) object).getIdContact().toString());
	}
}
