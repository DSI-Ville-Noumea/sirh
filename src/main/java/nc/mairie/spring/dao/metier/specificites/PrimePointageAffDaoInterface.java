package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;

public interface PrimePointageAffDaoInterface {

	public void creerPrimePointageAff(Integer numRubrique, Integer idAffectation);

	public void supprimerPrimePointageAff(Integer idAffectation, Integer numRubrique);
	
	public ArrayList<PrimePointageAff> listerPrimePointageAff(Integer idAffectation) ;
}
