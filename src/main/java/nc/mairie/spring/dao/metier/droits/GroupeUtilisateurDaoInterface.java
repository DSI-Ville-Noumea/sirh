package nc.mairie.spring.dao.metier.droits;

import nc.mairie.metier.droits.GroupeUtilisateur;

public interface GroupeUtilisateurDaoInterface {

	public void supprimerGroupeUtilisateur(Integer idUtilisateur, Integer idGroupe) throws Exception;

	public void creerGroupeUtilisateur(Integer idUtilisateur, Integer idGroupe) throws Exception;

	public GroupeUtilisateur chercherGroupeUtilisateur(Integer idUtilisateur, Integer idGroupe) throws Exception;

	public void supprimerGroupeUtilisateurAvecGroupe(Integer idGroupe);

}
