package nc.mairie.metier.commun;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeContact
 */
public class TypeContact {

	public Integer idTypeContact;
	public String libelle;

	/**
	 * Constructeur TypeContact.
	 */
	public TypeContact() {
		super();
	}

	public Integer getIdTypeContact() {
		return idTypeContact;
	}

	public void setIdTypeContact(Integer idTypeContact) {
		this.idTypeContact = idTypeContact;
	}

	public String getLibelle() {
		return libelle == null ? Const.CHAINE_VIDE : libelle.trim();
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeContact.toString().equals(((TypeContact) object).getIdTypeContact().toString());
	}
}
