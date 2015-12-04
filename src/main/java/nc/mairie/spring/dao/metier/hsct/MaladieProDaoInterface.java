package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.MaladiePro;

public interface MaladieProDaoInterface {

	public ArrayList<MaladiePro> listerMaladiePro() throws Exception;

	public void creerMaladiePro(String codeMaladiePro, String libMaladiePro) throws Exception;

	public void supprimerMaladiePro(Integer idMaladiePro) throws Exception;

}
