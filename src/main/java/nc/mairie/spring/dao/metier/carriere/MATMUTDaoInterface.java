package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.metier.carriere.MATMUT;

public interface MATMUTDaoInterface {

	MATMUT chercherMatmutByMatrAndPeriod(Integer matricule, Integer dateMonth);
	
	void creerMATMUT(MATMUT matmut);
	
	void supprimerMATMUT(MATMUT matmut);
	
	Integer getNextPKVal();
}
