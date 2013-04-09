package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeDeveloppement;

public interface EaeDeveloppementDaoInterface {

	public ArrayList<EaeDeveloppement> listerEaeDeveloppementParEvolution(Integer idEvolution) throws Exception;

	public void supprimerEaeDeveloppement(Integer idEaeDeveloppement) throws Exception;

	public void creerEaeDeveloppement(Integer idEaeEvolution, String typeDeveloppement, String libelleDeveloppement, Date echeanceDeveloppement,
			Integer priorisation) throws Exception;

	public void modifierEaeDeveloppement(Integer idEaeDeveloppement, String typeDeveloppement, String libelleDeveloppement,
			Date echeanceDeveloppement, Integer priorisation) throws Exception;
}
