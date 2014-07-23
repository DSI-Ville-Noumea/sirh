package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.AutreAdministrationAgent;

public interface AutreAdministrationAgentDaoInterface {

	public ArrayList<AutreAdministrationAgent> listerAutreAdministrationAgentAvecAgent(Integer idAgent)
			throws Exception;

	public ArrayList<AutreAdministrationAgent> listerAutreAdministrationAgentAvecAutreAdministration(
			Integer idAutreAdmin) throws Exception;

	public void creerAutreAdministrationAgent(Integer idAutreAdmin, Integer idAgent, Date dateEntree, Date dateSortie,
			Integer fonctionnaire) throws Exception;

	public void supprimerAutreAdministrationAgent(Integer idAutreAdmin, Integer idAgent, Date dateEntree)
			throws Exception;

	public AutreAdministrationAgent chercherAutreAdministrationAgentFonctionnaireAncienne(Integer idAgent)
			throws Exception;

	public AutreAdministrationAgent chercherAutreAdministrationAgentAncienne(Integer idAgent) throws Exception;

	public AutreAdministrationAgent chercherAutreAdministrationAgentDateDebut(Integer idAgent, Date dateEntree)
			throws Exception;

	public AutreAdministrationAgent chercherAutreAdministrationAgentActive(Integer idAgent) throws Exception;

}
