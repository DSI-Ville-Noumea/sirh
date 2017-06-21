package nc.mairie.metier.parametrage;

import nc.mairie.metier.Const;

/**
 * Objet metier TypeDocument
 */
public class TypeDocument {

	public Integer	idTypeDocument;
	public String	libTypeDocument;
	public String	codTypeDocument;
	public String	moduleTypeDocument;
	public Integer	idPathAlfresco;

	/**
	 * Constructeur TypeDocument.
	 */
	public TypeDocument() {
		super();
	}

	/**
	 * Getter de l'attribut idTypeDocument.
	 */
	public Integer getIdTypeDocument() {
		return idTypeDocument;
	}

	/**
	 * Setter de l'attribut idTypeDocument.
	 */
	public void setIdTypeDocument(Integer newIdTypeDocument) {
		idTypeDocument = newIdTypeDocument;
	}

	/**
	 * Getter de l'attribut libTypeDocument.
	 */
	public String getLibTypeDocument() {
		return libTypeDocument == null ? Const.CHAINE_VIDE : libTypeDocument.trim();
	}

	/**
	 * Setter de l'attribut libTypeDocument.
	 */
	public void setLibTypeDocument(String newLibTypeDocument) {
		libTypeDocument = newLibTypeDocument;
	}

	/**
	 * Getter de l'attribut codTypeDocument.
	 */
	public String getCodTypeDocument() {
		return codTypeDocument;
	}

	/**
	 * Setter de l'attribut codTypeDocument.
	 */
	public void setCodTypeDocument(String newCod_type_document) {
		codTypeDocument = newCod_type_document;
	}

	public String getModuleTypeDocument() {
		return moduleTypeDocument;
	}

	public void setModuleTypeDocument(String moduleTypeDocument) {
		this.moduleTypeDocument = moduleTypeDocument;
	}

	@Override
	public boolean equals(Object object) {
		return idTypeDocument.toString().equals(((TypeDocument) object).getIdTypeDocument().toString());
	}

	public Integer getIdPathAlfresco() {
		return idPathAlfresco;
	}

	public void setIdPathAlfresco(Integer idPathAlfresco) {
		this.idPathAlfresco = idPathAlfresco;
	}
}
