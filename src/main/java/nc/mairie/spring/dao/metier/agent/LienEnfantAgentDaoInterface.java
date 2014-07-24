package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.agent.LienEnfantAgent;

public interface LienEnfantAgentDaoInterface {

	public ArrayList<LienEnfantAgent> listerLienEnfantAgentAvecEnfant(Integer idEnfant) throws Exception;

	public ArrayList<LienEnfantAgent> listerLienEnfantAgentAvecAgent(Integer idAgent) throws Exception;

	public void creerLienEnfantAgent(Integer idAgent, Integer idEnfant, boolean enfantACharge) throws Exception;

	public void modifierLienEnfantAgent(Integer idAgent, Integer idEnfant, boolean enfantACharge) throws Exception;

	public void supprimerLienEnfantAgent(Integer idAgent, Integer idEnfant) throws Exception;

}
