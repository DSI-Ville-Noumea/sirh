package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TypeDelegation;

public interface TypeDelegationDaoInterface {

	public ArrayList<TypeDelegation> listerTypeDelegation() throws Exception;

	public TypeDelegation chercherTypeDelegation(Integer idTypeAvantage) throws Exception;

	public void creerTypeDelegation(String libelleTypeDelegation) throws Exception;

	public void supprimerTypeDelegation(Integer idTypeDelegation) throws Exception;

}
