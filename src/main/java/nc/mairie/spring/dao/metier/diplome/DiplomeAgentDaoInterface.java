package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.diplome.DiplomeAgent;

public interface DiplomeAgentDaoInterface {

	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecTitreDiplome(Integer idTitreDiplome) throws Exception;

	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecSpecialiteDiplome(Integer idSpecialite) throws Exception;

	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecAgent(Integer idAgent) throws Exception;

	public DiplomeAgent chercherDernierDiplomeAgentAvecAgent(Integer idAgent) throws Exception;

	public ArrayList<DiplomeAgent> listerEcolesDiplomeAgent() throws Exception;

	public Integer creerDiplomeAgent(Integer idTitreDiplome, Integer idAgent, Integer idDocument,
			Integer idSpecialiteDiplome, Date dateObtention, String nomEcole) throws Exception;

	public void modifierDiplomeAgent(Integer idDiplome, Integer idTitreDiplome, Integer idAgent, Integer idDocument,
			Integer idSpecialiteDiplome, Date dateObtention, String nomEcole) throws Exception;

	public void supprimerDiplomeAgent(Integer idDiplome) throws Exception;

}
