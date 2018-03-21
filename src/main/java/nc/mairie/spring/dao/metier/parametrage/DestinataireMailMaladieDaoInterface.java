package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.DestinataireMailMaladie;

public interface DestinataireMailMaladieDaoInterface {

	public ArrayList<DestinataireMailMaladie> listerDestinataireMailMaladie(boolean isForJob) throws Exception;

	public void creerDestinataireMailMaladie(Integer idGroupe, boolean isForJob) throws Exception;

	public void supprimerDestinataireMailMaladie(Integer idDestinataireMailMaladie) throws Exception;
	
	public DestinataireMailMaladie chercherDestinataireMailMaladieById(Integer idDestinataireMailMaladie) throws Exception;
	
	public DestinataireMailMaladie chercherDestinataireMailMaladieByIdGroupe(Integer idGroup, boolean isForJob) throws Exception;
	

}
