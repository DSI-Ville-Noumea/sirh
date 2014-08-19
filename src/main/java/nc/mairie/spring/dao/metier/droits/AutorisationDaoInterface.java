package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.Autorisation;

public interface AutorisationDaoInterface {

	public ArrayList<Autorisation> listerAutorisationAvecUtilisateur(String userName) throws Exception;

}
