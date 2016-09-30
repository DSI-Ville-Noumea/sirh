package nc.mairie.gestionagent.eae.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;


public class EaeFinalizationDto {

	private String idDocument;
	private String versionDocument;
	private String commentaire;
	private Float noteAnnee;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateFinalisation;
	
	public EaeFinalizationDto() {
	}
	
	public String getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}

	public String getVersionDocument() {
		return versionDocument;
	}

	public void setVersionDocument(String versionDocument) {
		this.versionDocument = versionDocument;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Float getNoteAnnee() {
		return noteAnnee;
	}

	public void setNoteAnnee(Float noteAnnee) {
		this.noteAnnee = noteAnnee;
	}

	public Date getDateFinalisation() {
		return dateFinalisation;
	}

	public void setDateFinalisation(Date dateFinalisation) {
		this.dateFinalisation = dateFinalisation;
	}
	
}
