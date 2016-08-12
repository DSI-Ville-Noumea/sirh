package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TypeDocument;

public interface TypeDocumentDaoInterface {

	public ArrayList<TypeDocument> listerTypeDocumentAvecModule(String moduleTypeDocument) throws Exception;

	public TypeDocument chercherTypeDocument(Integer idTypeDocument) throws Exception;

	public TypeDocument chercherTypeDocumentByCod(String codTypeDocument) throws Exception;

	public void creerTypeDocument(String libelleTypeDocument, String codTypeDocument, String moduleTypeDocument, Integer idPathAlfresco)
			throws Exception;

	public void supprimerTypeDocument(Integer idTypeDocument) throws Exception;

	ArrayList<TypeDocument> listerTypeDocument() throws Exception;

	ArrayList<String> listerModuleDocument() throws Exception;

}
