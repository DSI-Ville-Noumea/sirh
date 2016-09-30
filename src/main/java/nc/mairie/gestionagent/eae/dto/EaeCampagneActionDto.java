package nc.mairie.gestionagent.eae.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeCampagneActionDto {

	private Integer idCampagneAction;
	private String nomAction;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateTransmission;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateAFaireLe;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateFaitLe;
	private String commentaire;
	private Integer idAgentRealisation;
	private String message;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMailEnvoye;
	private List<EaeCampagneActeursDto> listeCampagneActeurs;
	private List<EaeDocumentDto> listeEaeDocument;
	
	public EaeCampagneActionDto(){
		this.listeCampagneActeurs = new ArrayList<EaeCampagneActeursDto>();
		this.listeEaeDocument = new ArrayList<EaeDocumentDto>();
	}
	
	public Integer getIdCampagneAction() {
		return idCampagneAction;
	}
	public void setIdCampagneAction(Integer idCampagneAction) {
		this.idCampagneAction = idCampagneAction;
	}
	public String getNomAction() {
		return nomAction;
	}
	public void setNomAction(String nomAction) {
		this.nomAction = nomAction;
	}
	public Date getDateTransmission() {
		return dateTransmission;
	}
	public void setDateTransmission(Date dateTransmission) {
		this.dateTransmission = dateTransmission;
	}
	public Date getDateAFaireLe() {
		return dateAFaireLe;
	}
	public void setDateAFaireLe(Date dateAFaireLe) {
		this.dateAFaireLe = dateAFaireLe;
	}
	public Date getDateFaitLe() {
		return dateFaitLe;
	}
	public void setDateFaitLe(Date dateFaitLe) {
		this.dateFaitLe = dateFaitLe;
	}
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public Integer getIdAgentRealisation() {
		return idAgentRealisation;
	}
	public void setIdAgentRealisation(Integer idAgentRealisation) {
		this.idAgentRealisation = idAgentRealisation;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDateMailEnvoye() {
		return dateMailEnvoye;
	}
	public void setDateMailEnvoye(Date dateMailEnvoye) {
		this.dateMailEnvoye = dateMailEnvoye;
	}

	public List<EaeCampagneActeursDto> getListeCampagneActeurs() {
		return listeCampagneActeurs;
	}

	public void setListeCampagneActeurs(
			List<EaeCampagneActeursDto> listeCampagneActeurs) {
		this.listeCampagneActeurs = listeCampagneActeurs;
	}

	public List<EaeDocumentDto> getListeEaeDocument() {
		return listeEaeDocument;
	}

	public void setListeEaeDocument(List<EaeDocumentDto> listeEaeDocument) {
		this.listeEaeDocument = listeEaeDocument;
	}
	
}
