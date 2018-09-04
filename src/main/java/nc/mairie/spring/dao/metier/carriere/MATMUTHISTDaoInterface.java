package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.metier.carriere.MATMUT;
import nc.mairie.metier.carriere.MATMUTHIST;

public interface MATMUTHISTDaoInterface {

	void creerMATMUTHIST(MATMUT matmut);

	MATMUTHIST chercherMATMUTHISTVentileByAgentAndPeriod(Integer idAgent, Integer perrep);
	
	public Integer getNextPKVal();
}
