package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.metier.specificites.DelegationAFF;

public interface DelegationAffDaoInterface {

	public void creerDelegationAFF(Integer idDelegation, Integer idAffectation);

	public void supprimerDelegationAFF(Integer idDelegation, Integer idAffectation);

	public DelegationAFF chercherDelegationAFF(Integer idDelegation, Integer idAffectation) throws Exception;
}
