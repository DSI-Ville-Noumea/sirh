package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

public class CompteurAsaDto {

	private Integer idAgent;

	private Double nb;

	private Date dateDebut;

	private Date dateFin;

	private OrganisationSyndicaleDto organisationSyndicaleDto;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
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

	public Double getNb() {
		return nb;
	}

	public void setNb(Double nb) {
		this.nb = nb;
	}

	public OrganisationSyndicaleDto getOrganisationSyndicaleDto() {
		return organisationSyndicaleDto;
	}

	public void setOrganisationSyndicaleDto(OrganisationSyndicaleDto organisationSyndicaleDto) {
		this.organisationSyndicaleDto = organisationSyndicaleDto;
	}

}
