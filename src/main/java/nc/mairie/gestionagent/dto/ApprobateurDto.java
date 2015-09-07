package nc.mairie.gestionagent.dto;

import flexjson.JSONDeserializer;

public class ApprobateurDto implements IJSONSerialize, IJSONDeserialize<ApprobateurDto> {

	private AgentWithServiceDto approbateur;

	private AgentDto delegataire;

	public ApprobateurDto() {

	}

	@Override
	public String serializeInJSON() {
		return null;
	}

	@Override
	public ApprobateurDto deserializeFromJSON(String json) {
		return new JSONDeserializer<ApprobateurDto>().deserializeInto(json, this);
	}

	public String toString() {
		return " Agent:" + getApprobateur().getNom() + " " + getApprobateur().getPrenom() + " id="
				+ getApprobateur().getIdAgent() + " service " + getApprobateur().getService() + " idServiceADS "
				+ getApprobateur().getIdServiceADS();
	}

	@Override
	public boolean equals(Object obj) {
		ApprobateurDto other = (ApprobateurDto) obj;
		if (approbateur == null) {
			if (other.approbateur != null)
				return false;
		} else if (!approbateur.getIdAgent().toString().equals(other.approbateur.getIdAgent().toString()))
			return false;
		return true;
	}

	public AgentDto getDelegataire() {
		return delegataire;
	}

	public void setDelegataire(AgentDto delegataire) {
		this.delegataire = delegataire;
	}

	public AgentWithServiceDto getApprobateur() {
		return approbateur;
	}

	public void setApprobateur(AgentWithServiceDto approbateur) {
		this.approbateur = approbateur;
	}
}
