package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.technique.Transaction;

public interface DiplomeGeneriqueDaoInterface {

	public void creerDiplomeGenerique(String libelleDiplomeGenerique) throws Exception;

	public void supprimerDiplomeGenerique(Integer idDiplomeGenerique) throws Exception;

	public ArrayList<DiplomeGenerique> listerDiplomeGenerique() throws Exception;

	public DiplomeGenerique chercherDiplomeGenerique(Integer idDiplomeGenerique) throws Exception;

	public ArrayList<DiplomeGenerique> listerDiplomeGeneriqueAvecFE(Transaction aTransaction, FicheEmploi ficheEmploi)
			throws Exception;

}
