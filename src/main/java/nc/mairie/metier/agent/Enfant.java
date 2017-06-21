package nc.mairie.metier.agent;

import java.util.Date;

import nc.mairie.metier.Const;

public class Enfant {

	public Integer idEnfant;
	public Integer idDocument;
	public String nom;
	public String prenom;
	public String sexe;
	public Date dateNaissance;
	public Integer codePaysNaissEt;
	public Integer codeCommuneNaissEt;
	public Integer codeCommuneNaissFr;
	public Date dateDeces;
	public String nationalite;
	public String commentaire;

	public Enfant() {
		super();
	}

	public Integer getIdEnfant() {
		return idEnfant;
	}

	public void setIdEnfant(Integer idEnfant) {
		this.idEnfant = idEnfant;
	}

	public Integer getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Integer idDocument) {
		this.idDocument = idDocument;
	}

	public String getNom() {
		return nom == null ? Const.CHAINE_VIDE : nom.trim();
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom == null ? Const.CHAINE_VIDE : prenom.trim();
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getSexe() {
		return sexe == null ? Const.CHAINE_VIDE : sexe.trim();
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public Integer getCodePaysNaissEt() {
		return codePaysNaissEt;
	}

	public void setCodePaysNaissEt(Integer codePaysNaissEt) {
		this.codePaysNaissEt = codePaysNaissEt;
	}

	public Integer getCodeCommuneNaissEt() {
		return codeCommuneNaissEt;
	}

	public void setCodeCommuneNaissEt(Integer codeCommuneNaissEt) {
		this.codeCommuneNaissEt = codeCommuneNaissEt;
	}

	public Integer getCodeCommuneNaissFr() {
		return codeCommuneNaissFr;
	}

	public void setCodeCommuneNaissFr(Integer codeCommuneNaissFr) {
		this.codeCommuneNaissFr = codeCommuneNaissFr;
	}

	public Date getDateDeces() {
		return dateDeces;
	}

	public void setDateDeces(Date dateDeces) {
		this.dateDeces = dateDeces;
	}

	public String getNationalite() {
		return nationalite;
	}

	public void setNationalite(String nationalite) {
		this.nationalite = nationalite;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	@Override
	public boolean equals(Object object) {
		return idEnfant.toString().equals(((Enfant) object).getIdEnfant().toString());
	}

}
