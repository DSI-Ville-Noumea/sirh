package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.TypeMaladiePro;

public interface TypeMaladieProDaoInterface {

	public ArrayList<TypeMaladiePro> listerMaladiePro() throws Exception;

	public void creerMaladiePro(String codeMaladiePro, String libMaladiePro) throws Exception;

	public void supprimerMaladiePro(Integer idMaladiePro) throws Exception;

}
