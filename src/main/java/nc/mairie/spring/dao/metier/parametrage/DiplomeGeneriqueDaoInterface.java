package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.poste.DiplomeFE;

public interface DiplomeGeneriqueDaoInterface {

	public void creerDiplomeGenerique(String libelleDiplomeGenerique) throws Exception;

	public void supprimerDiplomeGenerique(Integer idDiplomeGenerique) throws Exception;

	public ArrayList<DiplomeGenerique> listerDiplomeGenerique() throws Exception;

	public DiplomeGenerique chercherDiplomeGenerique(Integer idDiplomeGenerique) throws Exception;

	public ArrayList<DiplomeGenerique> listerDiplomeGeneriqueAvecFE(Integer idFicheEmploi, ArrayList<DiplomeFE> liens)
			throws Exception;

}
