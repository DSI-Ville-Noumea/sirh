package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.Employeur;

public interface EmployeurDaoInterface {

	public ArrayList<Employeur> listerEmployeur() throws Exception;

	public Employeur chercherEmployeur(Integer idEmployeur) throws Exception;

	public void creerEmployeur(String libelleEmployeur, String titreEmployeur) throws Exception;

	public void modifierEmployeur(Integer idEmployeur, String libelleEmployeur, String titreEmployeur) throws Exception;

	public void supprimerEmployeur(Integer idEmployeur) throws Exception;

}
