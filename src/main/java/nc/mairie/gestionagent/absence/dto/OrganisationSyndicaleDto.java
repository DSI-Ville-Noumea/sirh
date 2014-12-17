package nc.mairie.gestionagent.absence.dto;

import java.util.List;

public class OrganisationSyndicaleDto {

	private Integer idOrganisation;
	private String libelle;
	private String sigle;
	private boolean actif;
	private List<AgentOrganisationSyndicaleDto> listeAgents;

	public Integer getIdOrganisation() {
		return idOrganisation;
	}

	public void setIdOrganisation(Integer idOrganisation) {
		this.idOrganisation = idOrganisation;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	@Override
	public boolean equals(Object obj) {
		return idOrganisation.toString().equals(((OrganisationSyndicaleDto) obj).getIdOrganisation().toString());
	}

	public List<AgentOrganisationSyndicaleDto> getListeAgents() {
		return listeAgents;
	}

	public void setListeAgents(List<AgentOrganisationSyndicaleDto> listeAgents) {
		this.listeAgents = listeAgents;
	}

}
