package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.Utilisateur;

public interface UtilisateurDaoInterface {

	public ArrayList<Utilisateur> listerUtilisateur() throws Exception;

	public void supprimerUtilisateur(Integer idUtilisateur) throws Exception;

	public void modifierUtilisateur(Integer idUtilisateur, String login) throws Exception;

	public void creerUtilisateur(String login) throws Exception;

}
