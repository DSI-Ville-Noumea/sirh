package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;

public interface PrimePointageAffDaoInterface {

	public void creerPrimePointageAff(Integer idPrimePointage, Integer idAffectation);

	public void supprimerPrimePointageAff(Integer idAffectation, Integer idPrimePointage);

	public ArrayList<PrimePointageAff> listerPrimePointageAffAvecPP(Integer idPrimePointage);
}
