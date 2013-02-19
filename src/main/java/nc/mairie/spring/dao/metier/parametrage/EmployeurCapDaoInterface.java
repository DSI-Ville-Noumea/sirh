package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.EmployeurCap;

public interface EmployeurCapDaoInterface {

	public ArrayList<EmployeurCap> listerEmployeurCapParEmployeur(Integer idEmployeur) throws Exception;

	public ArrayList<EmployeurCap> listerEmployeurCapParCap(Integer idCap) throws Exception;

	public void creerEmployeurCap(Integer idEmployeur, Integer idCap, Integer position) throws Exception;

	public void supprimerEmployeurCapParCap(Integer idCap) throws Exception;

}
