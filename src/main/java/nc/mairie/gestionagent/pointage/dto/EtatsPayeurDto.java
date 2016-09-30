package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EtatsPayeurDto {
	
	private Integer idEtatPayeur;
	private String statut;
	private Integer type;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEtatPayeur;
	private String label;
	private String fichier;
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEdition;
	private String displayNom;
	private String displayPrenom;
	
	private String urlAlfresco;
	
	public EtatsPayeurDto(){
		
	}
	
	public EtatsPayeurDto(Integer idEtatPayeur, String statut, Integer type,
			Date dateEtatPayeur, String label, String fichier, Integer idAgent, Date dateEdition) {
		this.idEtatPayeur = idEtatPayeur;
		this.statut = statut;
		this.type = type;
		this.dateEtatPayeur = dateEtatPayeur;
		this.label = label;
		this.fichier = fichier;
		this.idAgent = idAgent;
		this.dateEdition = dateEdition;
	}

	public Integer getIdEtatPayeur() {
		return idEtatPayeur;
	}

	public void setIdEtatPayeur(Integer idEtatPayeur) {
		this.idEtatPayeur = idEtatPayeur;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getDateEtatPayeur() {
		return dateEtatPayeur;
	}

	public void setDateEtatPayeur(Date dateEtatPayeur) {
		this.dateEtatPayeur = dateEtatPayeur;
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

	public String getDisplayNom() {
		return displayNom;
	}

	public void setDisplayNom(String displayNom) {
		this.displayNom = displayNom;
	}

	public String getDisplayPrenom() {
		return displayPrenom;
	}

	public void setDisplayPrenom(String displayPrenom) {
		this.displayPrenom = displayPrenom;
	}

	public String getUrlAlfresco() {
		return urlAlfresco;
	}

	public void setUrlAlfresco(String urlAlfresco) {
		this.urlAlfresco = urlAlfresco;
	}
	
}
