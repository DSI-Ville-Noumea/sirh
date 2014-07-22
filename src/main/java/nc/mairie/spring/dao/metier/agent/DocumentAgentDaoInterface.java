package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.agent.DocumentAgent;

public interface DocumentAgentDaoInterface {

	public void supprimerDocumentAgent(Integer idAgent, Integer idDocument) throws Exception;

	public void creerDocumentAgent(Integer idAgent, Integer idDocument) throws Exception;

	public DocumentAgent chercherDocumentAgent(Integer idAgent, Integer idDocument) throws Exception;

	public ArrayList<DocumentAgent> listerDocumentAgentAvecModule(Integer idAgent, String module) throws Exception;

	public ArrayList<DocumentAgent> listerDocumentAgentAvecModuleEtVue(Integer idAgent, String module, String typDoc,
			Integer idDansListe) throws Exception;

}
