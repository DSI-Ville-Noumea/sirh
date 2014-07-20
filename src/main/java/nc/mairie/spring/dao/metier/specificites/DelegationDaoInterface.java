package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.Delegation;

public interface DelegationDaoInterface {

	public ArrayList<Delegation> listerDelegationAvecTypeDelegation(Integer idTypeDelegation) throws Exception;

	public ArrayList<Delegation> listerDelegationAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<Delegation> listerDelegationAvecAFF(Integer idAffectation) throws Exception;

	public void creerDelegation(Integer idTypeDelegation, String libDelegation);

	public void supprimerDelegation(Integer idDelegation) throws Exception;

}
