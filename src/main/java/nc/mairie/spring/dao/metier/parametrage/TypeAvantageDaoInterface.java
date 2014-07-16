package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TypeAvantage;

public interface TypeAvantageDaoInterface {

	public ArrayList<TypeAvantage> listerTypeAvantage() throws Exception;

	public TypeAvantage chercherTypeAvantage(Integer idTypeAvantage) throws Exception;

	public void creerTypeAvantage(String libelleTypeAvantage) throws Exception;

	public void supprimerTypeAvantage(Integer idTypeAvantage) throws Exception;

}
