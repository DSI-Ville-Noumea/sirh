package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;

public interface CarriereDaoInterface {

	Carriere chercherCarriereEnCoursAvecAgent(Agent agent) throws Exception;
}
