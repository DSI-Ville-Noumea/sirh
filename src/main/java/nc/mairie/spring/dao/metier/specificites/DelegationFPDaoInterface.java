package nc.mairie.spring.dao.metier.specificites;

public interface DelegationFPDaoInterface {

	public void creerDelegationFP(Integer idDelegation, Integer idFichePoste);

	public void supprimerDelegationFP(Integer idDelegation, Integer idFichePoste);
}
