package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.Groupe;

public interface GroupeDaoInterface {

	public ArrayList<Groupe> listerGroupe() throws Exception;

	public ArrayList<Groupe> listerGroupeAvecUtilisateur(Integer idUtilisateur) throws Exception;

	public void supprimerGroupe(Integer idGroupe) throws Exception;

	public void modifierGroupe(Integer idGroupe, String libGroupe) throws Exception;

	public Integer creerGroupe(String libGroupe) throws Exception;

}
