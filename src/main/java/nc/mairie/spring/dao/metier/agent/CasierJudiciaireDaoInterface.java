package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.CasierJudiciaire;

public interface CasierJudiciaireDaoInterface {

	public ArrayList<CasierJudiciaire> listerCasierJudiciaireAvecAgent(Integer idAgent) throws Exception;

	public void creerCasierJudiciaire(Integer idAgent, Integer idDocument, String numExtrait, Date dateExtrait,
			boolean privationDroitsCiv, String commExtrait) throws Exception;

	public void modifierCasierJudiciaire(Integer idCasierJud, Integer idAgent, Integer idDocument, String numExtrait,
			Date dateExtrait, boolean privationDroitsCiv, String commExtrait) throws Exception;

	public void supprimerCasierJudiciaire(Integer idCasierJud) throws Exception;

}
