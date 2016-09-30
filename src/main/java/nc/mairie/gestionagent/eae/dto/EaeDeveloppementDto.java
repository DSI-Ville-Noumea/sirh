package nc.mairie.gestionagent.eae.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeDeveloppementDto {

	private Integer idEaeDeveloppement;
	private String libelle;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date echeance;
	private int priorisation;
	private String typeDeveloppement;
	
	public EaeDeveloppementDto() {
		
	}
	
	public Integer getIdEaeDeveloppement() {
		return idEaeDeveloppement;
	}
	public void setIdEaeDeveloppement(Integer idEaeDeveloppement) {
		this.idEaeDeveloppement = idEaeDeveloppement;
	}
	
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public Date getEcheance() {
		return echeance;
	}
	public void setEcheance(Date echeance) {
		this.echeance = echeance;
	}
	
	public int getPriorisation() {
		return priorisation;
	}
	public void setPriorisation(int priorisation) {
		this.priorisation = priorisation;
	}
	
	public String getTypeDeveloppement() {
		return typeDeveloppement;
	}
	public void setTypeDeveloppement(String typeDeveloppement) {
		this.typeDeveloppement = typeDeveloppement;
	}
	
	
}
