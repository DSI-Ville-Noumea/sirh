package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.Droit;

public interface DroitDaoInterface {

	public ArrayList<Droit> listerDroit() throws Exception;

	public void supprimerDroitAvecGroupe(Integer idGroupe) throws Exception;

	public void modifierDroit(Integer idElement, Integer idGroupe, Integer idTypeDroit) throws Exception;

	public void creerDroit(Integer idElement, Integer idGroupe, Integer idTypeDroit) throws Exception;

	public Droit chercherDroit(Integer idElement, Integer idGroupe) throws Exception;

}
