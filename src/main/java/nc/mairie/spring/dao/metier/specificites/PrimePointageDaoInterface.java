package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.specificites.PrimePointage;

public interface PrimePointageDaoInterface {

	public ArrayList<PrimePointage> listerPrimePointageAvecFP(Integer idFDP);

	public ArrayList<PrimePointage> listerPrimePointageAvecAFF(Integer idAff);

	public Integer creerPrimePointage(Integer idRubrique);

	public void supprimerPrimePointage(Integer idPrimePointage);

}
