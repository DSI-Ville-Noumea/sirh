package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.MotifAffectation;

public interface MotifAffectationDaoInterface {

	public List<MotifAffectation> listerMotifAffectation() throws Exception;

	public MotifAffectation chercherMotifAffectation(Integer idMotifAffectation) throws Exception;

}
