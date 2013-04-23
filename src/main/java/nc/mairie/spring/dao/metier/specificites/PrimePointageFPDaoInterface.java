package nc.mairie.spring.dao.metier.specificites;

public interface PrimePointageFPDaoInterface {

	public void creerPrimePointageFP(Integer idPrimePointage, Integer idFichePoste);

	public void supprimerPrimePointageFP(Integer idFichePoste, Integer idPrimePointage);
}
