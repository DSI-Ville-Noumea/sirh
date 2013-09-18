package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.Cap;

public interface CapDaoInterface {

	public ArrayList<Cap> listerCap() throws Exception;

	public void creerCap(String codeCap, String refCap, String description, String typeCap, Integer capVDN)
			throws Exception;

	public void modifierCap(Integer idCap, String codeCap, String refCap, String description, String typeCap,
			Integer capVDN) throws Exception;

	public void supprimerCap(Integer idCap) throws Exception;

	public Cap chercherCap(String codeCap, String refCap) throws Exception;

	public Cap chercherCapByCodeCap(String codeCap) throws Exception;

	public Cap chercherCapByAgent(Integer idAgent, String type, Integer annee);

}
