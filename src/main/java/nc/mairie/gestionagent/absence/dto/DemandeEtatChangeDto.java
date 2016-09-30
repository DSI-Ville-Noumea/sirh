package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class DemandeEtatChangeDto {

	private Integer idDemande;
	private Integer idRefEtat;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateAvis;
	private String motif;

	public Integer getIdDemande() {
		return idDemande;
	}

	public void setIdDemande(Integer idPointage) {
		this.idDemande = idPointage;
	}

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public Date getDateAvis() {
		return dateAvis;
	}

	public void setDateAvis(Date dateAvis) {
		this.dateAvis = dateAvis;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}
}
