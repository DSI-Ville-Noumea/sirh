package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.metier.specificites.AvantageNatureAFF;

public interface AvantageNatureAffDaoInterface {

	public void creerAvantageNatureAff(Integer idAvantage, Integer idAffectation);

	public void supprimerAvantageNatureAff(Integer idAvantage, Integer idAffectation);

	public AvantageNatureAFF chercherAvantageNatureAFF(Integer idAvantage, Integer idAffectation) throws Exception;
}
