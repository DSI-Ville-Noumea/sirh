package nc.mairie.spring.dao.metier.EAE;

import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

public interface EaeEvolutionDaoInterface {

	public EaeEvolution chercherEaeEvolution(Integer idEAE) throws Exception;
}
