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
				+ getApprobateur().getIdAgent() + " service " + getApprobateur().getService() + " codeService "
				+ getApprobateur().getCodeService();
	}

	@Override
	public boolean equals(Object obj) {
		return approbateur.getIdAgent().toString()
				.equals(((ApprobateurDto) obj).getApprobateur().getIdAgent().toString());
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
