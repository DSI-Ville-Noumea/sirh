package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.SiegeLesion;

public interface SiegeLesionDaoInterface {

	public void creerSiegeLesion(String description) throws Exception;

	public void supprimerSiegeLesion(Integer idSiege) throws Exception;

	public ArrayList<SiegeLesion> listerSiegeLesion() throws Exception;

}
