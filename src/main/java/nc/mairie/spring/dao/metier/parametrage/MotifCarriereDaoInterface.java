package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.MotifCarriere;

public interface MotifCarriereDaoInterface {

	public void creerMotifCarriere(String libelleMotifCarriere) throws Exception;

	public void supprimerMotifCarriere(Integer idMotifCarriere) throws Exception;

	public ArrayList<MotifCarriere> listerMotifCarriere() throws Exception;

}
