package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.diplome.PermisAgent;

public interface PermisAgentDaoInterface {

	public ArrayList<PermisAgent> listerPermisAgent(Integer idAgent) throws Exception;

	public void supprimerPermisAgent(Integer idPermisAgent) throws Exception;

	public Integer creerPermisAgent(Integer idPermis, Integer idAgent, Integer dureePermis, String uniteDuree, Date dateObtention) throws Exception;

	public void modifierPermisAgent(Integer idPermisAgent, Integer idPermis, Integer idAgent, Integer dureePermis, String uniteDuree,
			Date dateObtention) throws Exception;

	public ArrayList<PermisAgent> listerPermisAgentAvecTitrePermis(Integer idPermis) throws Exception;

}
