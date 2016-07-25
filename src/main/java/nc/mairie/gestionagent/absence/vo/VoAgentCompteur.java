package nc.mairie.gestionagent.absence.vo;

import nc.mairie.gestionagent.absence.dto.AgentOrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.metier.agent.Agent;

public class VoAgentCompteur implements Comparable<VoAgentCompteur> {

	private String							nom;
	private Agent							agent;
	private CompteurDto						compteur;
	private AgentOrganisationSyndicaleDto	agentOS;
	private boolean							actif;
	private String							sigleOS;

	public VoAgentCompteur(CompteurDto compteur, Agent agent) {
		this.agent = agent;
		this.compteur = compteur;
		this.nom = null != agent ? agent.getNomUsage() : "";
		this.actif = compteur.isActif();
		this.sigleOS = compteur.getOrganisationSyndicaleDto()==null ? null : compteur.getOrganisationSyndicaleDto().getSigle();
	}

	public VoAgentCompteur(AgentOrganisationSyndicaleDto agentOS, Agent agent) {
		this.agent = agent;
		this.agentOS = agentOS;
		this.nom = null != agent ? agent.getNomUsage() : "";
	}

	public int compareTo(VoAgentCompteur arg) {
		return this.nom.compareTo(arg.nom);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public CompteurDto getCompteur() {
		return compteur;
	}

	public void setCompteur(CompteurDto compteur) {
		this.compteur = compteur;
	}

	public AgentOrganisationSyndicaleDto getAgentOS() {
		return agentOS;
	}

	public void setAgentOS(AgentOrganisationSyndicaleDto agentOS) {
		this.agentOS = agentOS;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public String getSigleOS() {
		return sigleOS;
	}

	public void setSigleOS(String sigleOS) {
		this.sigleOS = sigleOS;
	}

}