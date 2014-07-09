package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.EaeDeveloppement;

public interface EaeDeveloppementDaoInterface {

	public ArrayList<EaeDeveloppement> listerEaeDeveloppementParEvolution(Integer idEvolution) throws Exception;

	public void supprimerEaeDeveloppement(Integer idEaeDeveloppement) throws Exception;

	public void creerEaeDeveloppement(Integer idEaeEvolution, String typeDeveloppement, String libelleDeveloppement, Date echeanceDeveloppement,
			Integer priorisation) throws Exception;

	public void modifierEaeDeveloppement(Integer idEaeDeveloppement, String typeDeveloppement, String libelleDeveloppement,
			Date echeanceDeveloppement, Integer priorisation) throws Exception;
}
