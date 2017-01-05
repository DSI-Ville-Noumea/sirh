package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class CompteurDto {

	private Integer						idCompteur;

	private Integer						idAgent;

	private Double						dureeAAjouter;

	private Double						dureeARetrancher;

	private MotifCompteurDto			motifCompteurDto;

	private boolean						isAnneePrecedente;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateDebut;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateFin;

	private OrganisationSyndicaleDto	organisationSyndicaleDto;

	private boolean						actif;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public boolean isAnneePrecedente() {
		return isAnneePrecedente;
	}

	public void setAnneePrecedente(boolean isAnneePrecedente) {
		this.isAnneePrecedente = isAnneePrecedente;
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

	public Double getDureeAAjouter() {
		return dureeAAjouter;
	}

	public void setDureeAAjouter(Double dureeAAjouter) {
		this.dureeAAjouter = dureeAAjouter;
	}

	public Double getDureeARetrancher() {
		return dureeARetrancher;
	}

	public void setDureeARetrancher(Double dureeARetrancher) {
		this.dureeARetrancher = dureeARetrancher;
	}

	public OrganisationSyndicaleDto getOrganisationSyndicaleDto() {
		return organisationSyndicaleDto;
	}

	public void setOrganisationSyndicaleDto(OrganisationSyndicaleDto organisationSyndicaleDto) {
		this.organisationSyndicaleDto = organisationSyndicaleDto;
	}

	public MotifCompteurDto getMotifCompteurDto() {
		return motifCompteurDto;
	}

	public void setMotifCompteurDto(MotifCompteurDto motifCompteurDto) {
		this.motifCompteurDto = motifCompteurDto;
	}

	public Integer getIdCompteur() {
		return idCompteur;
	}

	public void setIdCompteur(Integer idCompteur) {
		this.idCompteur = idCompteur;
	}

	@Override
	public boolean equals(Object obj) {
		if (idCompteur == null) {
			return false;
		}
		return idCompteur.toString().equals(((CompteurDto) obj).getIdCompteur().toString());
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

}
