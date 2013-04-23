package nc.mairie.spring.dao.metier.specificites;

public interface PrimePointageAffDaoInterface {

	public void creerPrimePointageAff(Integer idPrimePointage, Integer idAffectation);

	public void supprimerPrimePointageAff(Integer idAffectation, Integer idPrimePointage);
}
