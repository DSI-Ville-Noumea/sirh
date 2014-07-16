package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.SpecialiteDiplome;

public interface SpecialiteDiplomeDaoInterface {

	public void creerSpecialiteDiplome(String libelleSpecialiteDiplome) throws Exception;

	public void supprimerSpecialiteDiplome(Integer idSpecialiteDiplome) throws Exception;

	public ArrayList<SpecialiteDiplome> listerSpecialiteDiplome() throws Exception;

	public SpecialiteDiplome chercherSpecialiteDiplome(Integer idSpecialiteDiplome) throws Exception;

}
