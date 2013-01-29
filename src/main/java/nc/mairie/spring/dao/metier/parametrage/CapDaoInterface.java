package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.Cap;

public interface CapDaoInterface {

	public ArrayList<Cap> listerCap() throws Exception;

	public void creerCap(String codeCap, String refCap) throws Exception;

	public void supprimerCap(Integer idCap) throws Exception;

}
