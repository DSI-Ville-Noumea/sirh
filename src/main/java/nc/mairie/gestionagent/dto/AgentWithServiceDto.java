package nc.mairie.gestionagent.dto;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class AgentWithServiceDto implements IJSONSerialize, IJSONDeserialize<AgentWithServiceDto> {

	private String nom;
	private String prenom;
	private Integer idAgent;
	private String service;
	private String codeService;
	private String statut;

	public AgentWithServiceDto() {

	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getCodeService() {
		return codeService;
	}

	public void setCodeService(String codeService) {
		this.codeService = codeService;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	@Override
	public String serializeInJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentWithServiceDto deserializeFromJSON(String json) {
		return new JSONDeserializer<AgentWithServiceDto>().deserializeInto(json, this);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
}
