package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TypeRegIndemn;

public interface TypeRegIndemnDaoInterface {

	public ArrayList<TypeRegIndemn> listerTypeRegIndemn() throws Exception;

	public TypeRegIndemn chercherTypeRegIndemn(Integer idTypeRegIndemn) throws Exception;

	public void creerTypeRegIndemn(String libelleTypeRegIndemn) throws Exception;

	public void supprimerTypeRegIndemn(Integer idTypeRegIndemn) throws Exception;

}
