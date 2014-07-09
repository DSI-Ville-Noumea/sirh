package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.CorpsCap;

public interface CorpsCapDaoInterface {

	public ArrayList<CorpsCap> listerCorpsCapParCap(Integer idCap) throws Exception;

	public void creerCorpsCap(String codeSpgeng, Integer idCap) throws Exception;

	public void supprimerCorpsCapParCap(Integer idCap) throws Exception;

}
