package nc.mairie.spring.dao.metier.specificites;

public interface AvantageNatureFPDaoInterface {

	public void creerAvantageNatureFP(Integer idAvantage, Integer idFichePOste);

	public void supprimerAvantageNatureFP(Integer idAvantage, Integer idFichePOste);
}
