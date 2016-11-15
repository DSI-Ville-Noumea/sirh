package nc.noumea.spring.service.cmis;

import java.io.File;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;

public interface IAlfrescoCMISService {

	File readDocument(String path);

	ReturnMessageDto uploadDocument(Integer idAgentOperateur, Agent agent, Document document, File file, String codTypeDoc) throws Exception;

	/**
	 * 
	 * @param idAgentOperateur
	 * @param agent
	 * @param document
	 * @param file
	 * @param annee
	 *            Integer utile au campagne EAE
	 * @return
	 * @throws Exception
	 */
	ReturnMessageDto uploadDocument(Integer idAgentOperateur, Agent agent, Document document, File file, Integer annee, String codTypeDoc)
			throws Exception;

	ReturnMessageDto removeDocument(Document document);

	ReturnMessageDto uploadDocumentWithByte(Integer idAgent, Agent agentCourant, Document documentCourant, byte[] doc, String codTypeDoc)
			throws Exception;

	/**
	 * Cree le dossier pour un agent dans le site SIRH sous Alfresco.
	 * 
	 * @param idAgent
	 *            Integer ID de l agent
	 * @param nomAgent
	 *            String Nom de l agent
	 * @param prenomAgent
	 *            String Prenom de l agent
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto createFolderAgent(Integer idAgent, String nomAgent, String prenomAgent);

}
