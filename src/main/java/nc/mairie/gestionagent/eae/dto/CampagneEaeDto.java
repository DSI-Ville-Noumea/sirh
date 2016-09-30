package nc.mairie.gestionagent.eae.dto;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class CampagneEaeDto {

	private Integer						idCampagneEae;
	private Integer						annee;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateDebut;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateFin;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateOuvertureKiosque;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateFermetureKiosque;
	private String						commentaire;

	private List<EaeCampagneActionDto>	listeCampagneAction;
	private List<EaeDocumentDto>		listeEaeDocument;

	public CampagneEaeDto() {
	}

	public Integer getIdCampagneEae() {
		return idCampagneEae;
	}

	public void setIdCampagneEae(Integer idCampagneEae) {
		this.idCampagneEae = idCampagneEae;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Date getDateOuvertureKiosque() {
		return dateOuvertureKiosque;
	}

	public void setDateOuvertureKiosque(Date dateOuvertureKiosque) {
		this.dateOuvertureKiosque = dateOuvertureKiosque;
	}

	public Date getDateFermetureKiosque() {
		return dateFermetureKiosque;
	}

	public void setDateFermetureKiosque(Date dateFermetureKiosque) {
		this.dateFermetureKiosque = dateFermetureKiosque;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public List<EaeCampagneActionDto> getListeCampagneAction() {
		return listeCampagneAction;
	}

	public void setListeCampagneAction(
			List<EaeCampagneActionDto> listeCampagneAction) {
		this.listeCampagneAction = listeCampagneAction;
	}

	public List<EaeDocumentDto> getListeEaeDocument() {
		return listeEaeDocument;
	}

	public void setListeEaeDocument(List<EaeDocumentDto> listeEaeDocument) {
		this.listeEaeDocument = listeEaeDocument;
	}

	@Transient
	public boolean estOuverte() {
		if (getDateFin() == null) {
			return true;
		}
		return false;
	}
	
}
