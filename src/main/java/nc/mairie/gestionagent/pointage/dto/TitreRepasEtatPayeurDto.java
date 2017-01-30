package nc.mairie.gestionagent.pointage.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class TitreRepasEtatPayeurDto implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5252910063935208786L;
	
	private Integer idTrEtatPayeur;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEtatPayeur;
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEdition;
	private String label;
	private String fichier;
	private AgentDto agent;	
	private String urlAlfresco;
	
	private String urlAlfrescoPrestataire;
	private String labelPrestataire;
	
	public TitreRepasEtatPayeurDto(){
	}
	
	public Integer getIdTrEtatPayeur() {
		return idTrEtatPayeur;
	}
	public void setIdTrEtatPayeur(Integer idTrEtatPayeur) {
		this.idTrEtatPayeur = idTrEtatPayeur;
	}
	public Date getDateEtatPayeur() {
		return dateEtatPayeur;
	}
	public void setDateEtatPayeur(Date dateEtatPayeur) {
		this.dateEtatPayeur = dateEtatPayeur;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public Date getDateEdition() {
		return dateEdition;
	}
	public void setDateEdition(Date dateEdition) {
		this.dateEdition = dateEdition;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getFichier() {
		return fichier;
	}
	public void setFichier(String fichier) {
		this.fichier = fichier;
	}

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public String getUrlAlfresco() {
		return urlAlfresco;
	}

	public void setUrlAlfresco(String urlAlfresco) {
		this.urlAlfresco = urlAlfresco;
	}

	public String getUrlAlfrescoPrestataire() {
		return urlAlfrescoPrestataire;
	}

	public void setUrlAlfrescoPrestataire(String urlAlfrescoPrestataire) {
		this.urlAlfrescoPrestataire = urlAlfrescoPrestataire;
	}

	public String getLabelPrestataire() {
		return labelPrestataire;
	}

	public void setLabelPrestataire(String labelPrestataire) {
		this.labelPrestataire = labelPrestataire;
	}
	
}
