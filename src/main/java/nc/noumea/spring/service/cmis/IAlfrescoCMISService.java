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
	 * @param annee Integer utile au campagne EAE  
	 * @return
	 * @throws Exception
	 */
	ReturnMessageDto uploadDocument(Integer idAgentOperateur, Agent agent, Document document, File file, Integer annee, String codTypeDoc)
		 throws Exception;

	ReturnMessageDto removeDocument(Document document);

}
