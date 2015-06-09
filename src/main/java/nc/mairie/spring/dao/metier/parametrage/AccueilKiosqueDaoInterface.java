package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.AccueilKiosque;

public interface AccueilKiosqueDaoInterface {

	public List<AccueilKiosque> getAccueilKiosque() throws Exception;

	public void creerAccueilKiosque(String titre, String texte) throws Exception;

	public void supprimerAccueilKiosque(Integer idAccueil) throws Exception;

	public void modifierAccueilKiosque(Integer idAccueil, String titre, String texte) throws Exception;

}
