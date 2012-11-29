package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaeFormation;

public interface EaeFormationDaoInterface {

	public void creerEaeFormation(Integer idEae, Integer anneeFormation, String dureeFormation, String libFormation) throws Exception;

	public ArrayList<EaeFormation> listerEaeFormation(Integer idEAE) throws Exception;

	public void supprimerEaeFormation(Integer idFormation) throws Exception;

}
