package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.NatureAvantage;

public interface NatureAvantageDaoInterface {

	public void creerNatureAvantage(String libelleNatureAvantage) throws Exception;

	public void supprimerNatureAvantage(Integer idNatureAvantage) throws Exception;

	public ArrayList<NatureAvantage> listerNatureAvantage() throws Exception;

	public NatureAvantage chercherNatureAvantage(Integer idNatureAvantage) throws Exception;

}
