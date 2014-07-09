package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.Employeur;

public interface EmployeurDaoInterface {

	public List<Employeur> listerEmployeur() throws Exception;

	public Employeur chercherEmployeur(Integer idEmployeur) throws Exception;

	public void creerEmployeur(String libelleEmployeur, String titreEmployeur) throws Exception;

	public void modifierEmployeur(Integer idEmployeur, String libelleEmployeur, String titreEmployeur) throws Exception;

	public void supprimerEmployeur(Integer idEmployeur) throws Exception;

}
