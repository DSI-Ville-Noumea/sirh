package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.Document;

public interface DocumentDaoInterface {

	public Document chercherDocumentById(Integer idDocument) throws Exception;

	public ArrayList<Document> listerDocumentAvecType(Integer idTypeDocument) throws Exception;

	public void supprimerDocument(Integer idDocument) throws Exception;

	public Integer creerDocument(String classeDocument, String nomDocument, String lienDocument, Date dateDocument,
			String commentaire, Integer idTypeDocument, String nomOriginal, String nodeRefAlfresco, String commentaireAlfresco,
			Integer reference) throws Exception;

	public Document chercherDocumentByContainsNom(String nomFichier) throws Exception;

	public Document chercherDocumentParTypeEtAgent(String typeFichier, Integer idAgent) throws Exception;

	public ArrayList<Document> listerDocumentAgentTYPE(DocumentAgentDao daoLienDocument, Integer idAgent,
			String module, String typDoc, Integer idDansListe) throws Exception;

	public ArrayList<Document> listerDocumentAgent(DocumentAgentDao daoLienDocument, Integer idAgent, String vue,
			String module) throws Exception;

}
