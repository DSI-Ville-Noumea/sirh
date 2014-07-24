package nc.mairie.spring.dao.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.carriere.Categorie;
import nc.mairie.spring.dao.metier.poste.CategorieFEDao;

public interface CategorieDaoInterface {

	public ArrayList<Categorie> listerCategorie() throws Exception;

	public ArrayList<Categorie> listerCategorieAvecFE(Integer idFicheEmploi, CategorieFEDao categorieFEDao)
			throws Exception;

	public Categorie chercherCategorie(Integer idCategorie) throws Exception;

	public void creerCategorie(String libCategorie) throws Exception;

	public void supprimerCategorie(Integer idCategorie) throws Exception;

}
