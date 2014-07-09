package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.CentreFormation;

public interface CentreFormationDaoInterface {

	public ArrayList<CentreFormation> listerCentreFormation() throws Exception;

	public CentreFormation chercherCentreFormation(Integer idCentreFormation) throws Exception;

	public void creerCentreFormation(String libelleCentre) throws Exception;

	public void modifierCentreFormation(Integer idCentre, String libelleCentre) throws Exception;

	public void supprimerCentreFormation(Integer idCentreFormation) throws Exception;

}
