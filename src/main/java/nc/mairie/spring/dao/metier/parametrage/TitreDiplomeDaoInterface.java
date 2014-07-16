package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TitreDiplome;

public interface TitreDiplomeDaoInterface {

	public void creerTitreDiplome(String libelleTitreDiplome, String niveauTitreDiplome) throws Exception;

	public void supprimerTitreDiplome(Integer idTitreDiplome) throws Exception;

	public void modifierTitreDiplome(Integer idTitreDiplome, String libelleTitreDiplome, String niveauTitreDiplome)
			throws Exception;

	public TitreDiplome chercherTitreDiplome(Integer idTitreDiplome) throws Exception;

	public ArrayList<TitreDiplome> listerTitreDiplome() throws Exception;

}
