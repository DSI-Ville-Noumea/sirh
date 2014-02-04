package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.diplome.FormationAgent;

public interface FormationAgentDaoInterface {

	public ArrayList<FormationAgent> listerFormationAgent(Integer idAgent) throws Exception;

	public void supprimerFormationAgent(Integer idFormationAgent) throws Exception;

	public Integer creerFormationAgent(Integer idTitreFormation, Integer idCentreFormation, Integer idAgent,
			Integer dureeFormation, String uniteDuree, Integer anneeFormation) throws Exception;

	public void modifierFormationAgent(Integer idFormation, Integer idTitreFormation, Integer idCentreFormation,
			Integer idAgent, Integer dureeFormation, String uniteDuree, Integer anneeFormation) throws Exception;

	public ArrayList<FormationAgent> listerFormationAgentAvecTitreFormation(Integer idTitreFormation) throws Exception;

	public ArrayList<FormationAgent> listerFormationAgentAvecCentreFormation(Integer idCentreFormation)
			throws Exception;

	public ArrayList<FormationAgent> listerFormationAgentByAnnee(Integer idAgent, Integer annee) throws Exception;

}
