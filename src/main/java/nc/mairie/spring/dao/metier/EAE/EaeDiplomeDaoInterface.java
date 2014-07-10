package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.metier.eae.EaeDiplome;

public interface EaeDiplomeDaoInterface {

	public void creerEaeDiplome(Integer idEae, String libDiplome) throws Exception;

	public ArrayList<EaeDiplome> listerEaeDiplome(Integer idEAE) throws Exception;

	public void supprimerEaeDiplome(Integer idDiplome) throws Exception;
}
