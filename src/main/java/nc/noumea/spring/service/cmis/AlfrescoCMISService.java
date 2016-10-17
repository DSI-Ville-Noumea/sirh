package nc.noumea.spring.service.cmis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.parametrage.PathAlfresco;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.parametrage.PathAlfrescoDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.noumea.mairie.alfresco.cmis.CmisService;
import nc.noumea.mairie.alfresco.cmis.CmisUtils;
import nc.noumea.mairie.alfresco.cmis.CreateSession;

@Service
public class AlfrescoCMISService implements IAlfrescoCMISService {

	private Logger				logger						= LoggerFactory.getLogger(AlfrescoCMISService.class);

	@Autowired
	@Qualifier("alfrescoUrl")
	private String				alfrescoUrl;

	@Autowired
	@Qualifier("alfrescoLogin")
	private String				alfrescoLogin;

	@Autowired
	@Qualifier("alfrescoPassword")
	private String				alfrescoPassword;

	@Autowired
	private CreateSession		createSession;

	@Autowired
	private CmisService			cmisService;

	@Autowired
	private SirhDao				sirhDao;

	private TypeDocumentDao		typeDocumentDao;

	private PathAlfrescoDao		pathAlfrescoDao;

	private static String		staticAlfrescoUrl;

	private static final String	TYPE_CAMPAGNE_EAE			= "CAMP";
	private static final String	TYPE_ACTION_CAMPAGNE_EAE	= "ACT";
	private static final String	MIME_TYPE					= "application/octet-stream";

	@PostConstruct
	public void init() {
		AlfrescoCMISService.staticAlfrescoUrl = alfrescoUrl;

		if (null == typeDocumentDao) {
			typeDocumentDao = new TypeDocumentDao(sirhDao);
		}

		if (null == pathAlfrescoDao) {
			pathAlfrescoDao = new PathAlfrescoDao(sirhDao);
		}
	}

	@Override
	public File readDocument(String nodeRef) {

		Session session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);

		try {
			return cmisService.getFile(session, nodeRef);
		} catch (CmisObjectNotFoundException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public ReturnMessageDto uploadDocument(Integer idAgentOperateur, Agent agent, nc.mairie.metier.agent.Document document, File file,
			String codTypeDoc) throws Exception {
		return uploadDocument(idAgentOperateur, agent, document, file, null, codTypeDoc);
	}

	@Override
	public ReturnMessageDto uploadDocument(Integer idAgentOperateur, Agent agent, nc.mairie.metier.agent.Document document, File file, Integer annee,
			String codTypeDoc) throws Exception {

		ReturnMessageDto returnDto = new ReturnMessageDto();

		//////////////////// verification des donnees /////////////////////
		if (null == file || null == document) {
			logger.debug("Aucun document à ajouter.");
			returnDto.getErrors().add("Aucun document à ajouter.");
			return returnDto;
		}

		if (null == document.getIdTypeDocument()) {
			logger.debug("Aucun type de document sélectionné.");
			returnDto.getErrors().add("Aucun type de document sélectionné.");
			return returnDto;
		}

		TypeDocument typeDocument = typeDocumentDao.chercherTypeDocument(document.getIdTypeDocument());

		if (null == typeDocument) {
			logger.debug("Le type de document sélectionné n'existe pas.");
			returnDto.getErrors().add("Le type de document sélectionné n'existe pas.");
			return returnDto;
		}

		PathAlfresco pathAlfresco = pathAlfrescoDao.chercherPathAlfresco(typeDocument.getIdPathAlfresco());

		if (null == pathAlfresco || null == pathAlfresco.getPathAlfresco()) {
			logger.debug("Pas de répertoire distant alfresco défini.");
			returnDto.getErrors().add("Pas de répertoire distant alfresco défini.");
			return returnDto;
		}

		//////////////////////// connexion alfresco //////////////////////////
		Session session = null;
		try {
			session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);
		} catch (CmisConnectionException e) {
			logger.debug("Erreur de connexion a Alfresco CMIS : " + e.getMessage());
			returnDto.getErrors().add("Erreur de connexion à Alfresco CMIS");
			return returnDto;
		}

		//////////// on cherche le repertoire distant /////////////////////
		CmisObject object = null;

		Integer idAgent = null != agent && null != agent.getIdAgent() ? agent.getIdAgent() : null;
		String nomUsage = null != agent && null != agent.getNomUsage() ? agent.getNomUsage() : null;
		String prenomUsage = null != agent && null != agent.getPrenomUsage() ? agent.getPrenomUsage() : null;

		try {
			object = session.getObjectByPath(CmisUtils.getPathSIRH(idAgent, nomUsage, prenomUsage, pathAlfresco.getPathAlfresco()));
		} catch (CmisUnauthorizedException e) {
			logger.debug("Probleme d autorisation Alfresco CMIS : " + e.getMessage());
			returnDto.getErrors().add("Erreur Alfresco CMIS : non autorisé.");
			return returnDto;
		} catch (CmisObjectNotFoundException e) {
			logger.debug("Le dossier n'existe pas sous Alfresco : " + e.getMessage());
			returnDto.getErrors().add("Impossible d'ajouter une pièce jointe : répertoire distant non trouvé.");
			return returnDto;
		}

		if (null == object) {
			returnDto.getErrors().add(CmisUtils.ERROR_PATH);
			return returnDto;
		}

		// cas specifique aux campagnes EAE et leurs actions
		if (TYPE_CAMPAGNE_EAE.equals(typeDocument.getCodTypeDocument()) || TYPE_ACTION_CAMPAGNE_EAE.equals(typeDocument.getCodTypeDocument())) {
			String pathCampagneEAE = CmisUtils.getPathSIRH(idAgent, nomUsage, prenomUsage, pathAlfresco.getPathAlfresco()) + annee + CmisUtils.SLASH;
			try {
				object = session.getObjectByPath(pathCampagneEAE);
			} catch (CmisUnauthorizedException e) {
				logger.debug("Probleme d autorisation Alfresco CMIS : " + e.getMessage());
				returnDto.getErrors().add("Erreur Alfresco CMIS : non autorisé.");
				return returnDto;
			} catch (CmisObjectNotFoundException e) {
				logger.debug("Le dossier Campagne EAE " + annee + " n'existe pas sous Alfresco.");
				object = session.getObjectByPath(CmisUtils.getPathSIRH(idAgent, nomUsage, prenomUsage, pathAlfresco.getPathAlfresco()));
				Folder folder = (Folder) object;

				Map<String, String> propertiesFolderCampagneEAE = new HashMap<String, String>();
				propertiesFolderCampagneEAE.put("cmis:objectTypeId", "cmis:folder");
				propertiesFolderCampagneEAE.put("cmis:name", annee.toString());

				object = folder.createFolder(propertiesFolderCampagneEAE);
				logger.debug("Le dossier Campagne EAE " + annee + " vient d etre cree.");
			}
		}

		Folder folder = (Folder) object;

		int maxItemsPerPage = 5;
		OperationContext operationContext = session.createOperationContext();
		operationContext.setMaxItemsPerPage(maxItemsPerPage);

		Document docAlfresco = null;

		String nomDocument = CmisUtils.getPatternSIRH(codTypeDoc, nomUsage, prenomUsage, idAgent, new Date(), new DateTime().getMillisOfSecond(),
				annee);

		// properties
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, nomDocument);
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.DESCRIPTION, document.getCommentaire());

		ByteArrayInputStream stream;
		try {
			stream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
		} catch (IOException e) {
			logger.debug("Erreur à la lecture du document à ajouter : " + e.getMessage());
			returnDto.getErrors().add("Erreur à la lecture du document à ajouter.");
			return returnDto;
		}

		ContentStream contentStream = new ContentStreamImpl(nomDocument, BigInteger.valueOf(file.length()),
				new MimetypesFileTypeMap().getContentType(file), stream);

		// create a major version
		try {
			docAlfresco = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
		} catch (CmisContentAlreadyExistsException e) {
			logger.debug("CmisContentAlreadyExistsException : " + e.getMessage());
			returnDto.getErrors().add("Un document avec le même nom existe déjà pour cet agent.");
			return returnDto;
		}

		if (null == docAlfresco) {
			returnDto.getErrors().add(CmisUtils.ERROR_UPLOAD);
			return returnDto;
		}

		if (null != docAlfresco.getProperty("cmis:secondaryObjectTypeIds")) {
			List<Object> aspects = docAlfresco.getProperty("cmis:secondaryObjectTypeIds").getValues();
			if (!aspects.contains("P:mairie:customDocumentAspect")) {
				aspects.add("P:mairie:customDocumentAspect");
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put("cmis:secondaryObjectTypeIds", aspects);
				docAlfresco.updateProperties(props);
				logger.debug("Added aspect");
			} else {
				logger.debug("Doc already had aspect");
			}
		}

		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("mairie:idAgentOwner", null != idAgent ? idAgent : 0);
		props.put("mairie:idAgentCreateur", idAgentOperateur);
		props.put("mairie:commentaire", document.getCommentaire());
		docAlfresco.updateProperties(props);

		document.setNomDocument(nomDocument);
		document.setNodeRefAlfresco(docAlfresco.getProperty("alfcmis:nodeRef").getFirstValue().toString());

		return returnDto;
	}

	/**
	 * Supprime un document de Alfresco
	 */
	@Override
	public ReturnMessageDto removeDocument(nc.mairie.metier.agent.Document document) {

		ReturnMessageDto returnDto = new ReturnMessageDto();

		Session session = null;
		try {
			session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);
		} catch (CmisConnectionException e) {
			logger.debug("Erreur de connexion a Alfresco CMIS : " + e.getMessage());
			returnDto.getErrors().add("Erreur de connexion à Alfresco CMIS");
			return returnDto;
		}

		CmisObject object = null;
		try {
			object = session.getObject(document.getNodeRefAlfresco());
		} catch (CmisObjectNotFoundException e) {
			logger.debug("removeDocument : document non trouve dans Alfresco : ID " + document.getNodeRefAlfresco());
			return returnDto;
		}

		if (null == object) {
			logger.debug("removeDocument : document non trouve dans Alfresco : ID " + document.getNodeRefAlfresco());
			return returnDto;
		}

		object.delete();

		return returnDto;
	}

	/**
	 * exemple de nodeRef :
	 * "workspace://SpacesStore/1a344bd7-6422-45c6-94f7-5640048b20ab" exemple d
	 * URL a retourner :
	 * http://localhost:8080/alfresco/service/api/node/workspace/SpacesStore/418c511a-7c0a-4bb1-95a2-37e5946be726/content
	 * 
	 * @param nodeRef
	 *            String
	 * @return String l URL pour acceder au document directement a alfresco
	 */
	public static String getUrlOfDocument(String nodeRef) {

		return CmisUtils.getUrlOfDocument(staticAlfrescoUrl, nodeRef);
	}

	@Override
	public ReturnMessageDto uploadDocumentWithByte(Integer idAgentConnecte, Agent agent, nc.mairie.metier.agent.Document document, byte[] doc,
			String codTypeDoc) throws Exception {

		ReturnMessageDto returnDto = new ReturnMessageDto();

		//////////////////// verification des donnees /////////////////////
		if (null == doc || null == document) {
			logger.debug("Aucun document à ajouter.");
			returnDto.getErrors().add("Aucun document à ajouter.");
			return returnDto;
		}

		if (null == document.getIdTypeDocument()) {
			logger.debug("Aucun type de document sélectionné.");
			returnDto.getErrors().add("Aucun type de document sélectionné.");
			return returnDto;
		}

		TypeDocument typeDocument = typeDocumentDao.chercherTypeDocument(document.getIdTypeDocument());

		if (null == typeDocument) {
			logger.debug("Le type de document sélectionné n'existe pas.");
			returnDto.getErrors().add("Le type de document sélectionné n'existe pas.");
			return returnDto;
		}

		PathAlfresco pathAlfresco = pathAlfrescoDao.chercherPathAlfresco(typeDocument.getIdPathAlfresco());

		if (null == pathAlfresco || null == pathAlfresco.getPathAlfresco()) {
			logger.debug("Pas de répertoire distant alfresco défini.");
			returnDto.getErrors().add("Pas de répertoire distant alfresco défini.");
			return returnDto;
		}

		//////////////////////// connexion alfresco //////////////////////////
		Session session = null;
		try {
			session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);
		} catch (CmisConnectionException e) {
			logger.debug("Erreur de connexion a Alfresco CMIS : " + e.getMessage());
			returnDto.getErrors().add("Erreur de connexion à Alfresco CMIS");
			return returnDto;
		}

		//////////// on cherche le repertoire distant /////////////////////
		CmisObject object = null;

		Integer idAgent = null != agent && null != agent.getIdAgent() ? agent.getIdAgent() : null;
		String nomUsage = null != agent && null != agent.getNomUsage() ? agent.getNomUsage() : null;
		String prenomUsage = null != agent && null != agent.getPrenomUsage() ? agent.getPrenomUsage() : null;

		try {
			object = session.getObjectByPath(CmisUtils.getPathSIRH(idAgent, nomUsage, prenomUsage, pathAlfresco.getPathAlfresco()));
		} catch (CmisUnauthorizedException e) {
			logger.debug("Probleme d autorisation Alfresco CMIS : " + e.getMessage());
			returnDto.getErrors().add("Erreur Alfresco CMIS : non autorisé.");
			return returnDto;
		} catch (CmisObjectNotFoundException e) {
			logger.debug("Le dossier n'existe pas sous Alfresco : " + e.getMessage());
			returnDto.getErrors().add("Impossible d'ajouter une pièce jointe : répertoire distant non trouvé.");
			return returnDto;
		}

		if (null == object) {
			returnDto.getErrors().add(CmisUtils.ERROR_PATH);
			return returnDto;
		}

		Folder folder = (Folder) object;

		int maxItemsPerPage = 5;
		OperationContext operationContext = session.createOperationContext();
		operationContext.setMaxItemsPerPage(maxItemsPerPage);

		Document docAlfresco = null;

		String nomDocument = CmisUtils.getPatternSIRH(codTypeDoc, nomUsage, prenomUsage, idAgent, new Date(), new DateTime().getMillisOfSecond(),
				null);

		// properties
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, nomDocument);
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.DESCRIPTION, document.getCommentaire());

		ByteArrayInputStream stream = new ByteArrayInputStream(doc);
		ContentStream contentStream = new ContentStreamImpl(nomDocument, BigInteger.valueOf(doc.length), MIME_TYPE, stream);

		// create a major version
		try {
			docAlfresco = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
		} catch (CmisContentAlreadyExistsException e) {
			logger.debug("CmisContentAlreadyExistsException : " + e.getMessage());
			returnDto.getErrors().add("Un document avec le même nom existe déjà pour cet agent.");
			return returnDto;
		}

		if (null == docAlfresco) {
			returnDto.getErrors().add(CmisUtils.ERROR_UPLOAD);
			return returnDto;
		}

		if (null != docAlfresco.getProperty("cmis:secondaryObjectTypeIds")) {
			List<Object> aspects = docAlfresco.getProperty("cmis:secondaryObjectTypeIds").getValues();
			if (!aspects.contains("P:mairie:customDocumentAspect")) {
				aspects.add("P:mairie:customDocumentAspect");
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put("cmis:secondaryObjectTypeIds", aspects);
				docAlfresco.updateProperties(props);
				logger.debug("Added aspect");
			} else {
				logger.debug("Doc already had aspect");
			}
		}

		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("mairie:idAgentOwner", null != idAgent ? idAgent : 0);
		props.put("mairie:idAgentCreateur", idAgentConnecte);
		props.put("mairie:commentaire", document.getCommentaire());
		docAlfresco.updateProperties(props);

		document.setNomDocument(nomDocument);
		document.setNodeRefAlfresco(docAlfresco.getProperty("alfcmis:nodeRef").getFirstValue().toString());

		return returnDto;
	}

}
