package nc.mairie.metier.agent;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier Document
 */
public class Document {

	public Integer idDocument;
	public String classeDocument;
	public String nomDocument;
	public String lienDocument;
	public Date dateDocument;
	public String commentaire;
	public Integer idTypeDocument;
	public String nomOriginal;
	public String nodeRefAlfresco;
	// ce champ etait cree pour la reprise alfresco 
	// afin d y ajouter un commentaire si besoin
	public String commentaireAlfresco;
	
	// reference : Id de l'AT ou VM ou HANDICAP, etc
	public Integer reference;

	/**
	 * Constructeur Document.
	 */
	public Document() {
		super();
	}

	/**
	 * Getter de l'attribut idDocument.
	 */
	public Integer getIdDocument() {
		return idDocument;
	}

	/**
	 * Setter de l'attribut idDocument.
	 */
	public void setIdDocument(Integer newIdDocument) {
		idDocument = newIdDocument;
	}

	/**
	 * Getter de l'attribut classeDocument.
	 */
	public String getClasseDocument() {
		return classeDocument;
	}

	/**
	 * Setter de l'attribut classeDocument.
	 */
	public void setClasseDocument(String newClasseDocument) {
		classeDocument = newClasseDocument;
	}

	/**
	 * Getter de l'attribut nomDocument.
	 */
	public String getNomDocument() {
		return nomDocument == null ? Const.CHAINE_VIDE : nomDocument.trim();
	}

	/**
	 * Setter de l'attribut nomDocument.
	 */
	public void setNomDocument(String newNomDocument) {
		nomDocument = newNomDocument;
	}

	/**
	 * Getter de l'attribut lienDocument.
	 */
	public String getLienDocument() {
		return null != lienDocument ? lienDocument : Const.CHAINE_VIDE;
	}

	/**
	 * Setter de l'attribut lienDocument.
	 */
	public void setLienDocument(String newLienDocument) {
		lienDocument = newLienDocument;
	}

	/**
	 * Getter de l'attribut dateDocument.
	 */
	public Date getDateDocument() {
		return dateDocument;
	}

	/**
	 * Setter de l'attribut dateDocument.
	 */
	public void setDateDocument(Date newDateDocument) {
		dateDocument = newDateDocument;
	}

	/**
	 * Getter de l'attribut commentaire.
	 */
	public String getCommentaire() {
		return commentaire == null ? Const.CHAINE_VIDE : commentaire.trim();
	}

	/**
	 * Setter de l'attribut commentaire.
	 */
	public void setCommentaire(String newCommentaire) {
		commentaire = newCommentaire;
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

	public String getNomOriginal() {
		return nomOriginal;
	}

	public void setNomOriginal(String nomOriginal) {
		this.nomOriginal = nomOriginal;
	}

	@Override
	public boolean equals(Object object) {
		return idDocument.toString().equals(((Document) object).getIdDocument().toString());
	}

	public String getNodeRefAlfresco() {
		return nodeRefAlfresco;
	}

	public void setNodeRefAlfresco(String nodeRefAlfresco) {
		this.nodeRefAlfresco = nodeRefAlfresco;
	}

	public String getCommentaireAlfresco() {
		return commentaireAlfresco;
	}

	public void setCommentaireAlfresco(String commentaireAlfresco) {
		this.commentaireAlfresco = commentaireAlfresco;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}
	
}
