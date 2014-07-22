package nc.mairie.spring.dao.metier.referentiel;

import nc.mairie.metier.referentiel.TypeCompetence;

public interface TypeCompetenceDaoInterface {

	public TypeCompetence chercherTypeCompetenceAvecLibelle(String typeComp) throws Exception;

	public TypeCompetence chercherTypeCompetence(Integer idTypeComp) throws Exception;

}
