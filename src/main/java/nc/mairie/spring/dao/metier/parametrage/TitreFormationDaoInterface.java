package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TitreFormation;

public interface TitreFormationDaoInterface {

	public ArrayList<TitreFormation> listerTitreFormation() throws Exception;

	public TitreFormation chercherTitreFormation(Integer idTitreFormation) throws Exception;

	public void creerTitreFormation(String libelleTitre) throws Exception;

	public void modifierTitreFormation(Integer idTitre, String libelleTitre) throws Exception;

	public void supprimerTitreFormation(Integer idTitreFormation) throws Exception;

}
