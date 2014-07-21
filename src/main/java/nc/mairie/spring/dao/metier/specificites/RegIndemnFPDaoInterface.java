package nc.mairie.spring.dao.metier.specificites;

public interface RegIndemnFPDaoInterface {

	public void creerRegIndemFP(Integer idRegime, Integer idFichePoste);

	public void supprimerRegIndemFP(Integer idRegime, Integer idFichePoste);
}
