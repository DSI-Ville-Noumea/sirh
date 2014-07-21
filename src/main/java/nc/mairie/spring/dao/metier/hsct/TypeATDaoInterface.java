package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.TypeAT;

public interface TypeATDaoInterface {

	public void creerTypeAT(String description) throws Exception;

	public void supprimerTypeAT(Integer idTypeAT) throws Exception;

	public ArrayList<TypeAT> listerTypeAT() throws Exception;

	public TypeAT chercherTypeAT(Integer idTypeAT) throws Exception;

}
