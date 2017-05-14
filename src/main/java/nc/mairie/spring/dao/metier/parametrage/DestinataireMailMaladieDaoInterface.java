package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.DestinataireMailMaladie;

public interface DestinataireMailMaladieDaoInterface {

	public ArrayList<DestinataireMailMaladie> listerDestinataireMailMaladie() throws Exception;

	public void creerDestinataireMailMaladie(Integer idGroupe) throws Exception;

	public void supprimerDestinataireMailMaladie(Integer idDestinataireMailMaladie) throws Exception;
	
	public DestinataireMailMaladie chercherDestinataireMailMaladieById(Integer idDestinataireMailMaladie) throws Exception;
	

}
