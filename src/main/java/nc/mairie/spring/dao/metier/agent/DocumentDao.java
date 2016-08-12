package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DocumentDao extends SirhDao implements DocumentDaoInterface {

	public static final String CHAMP_CLASSE_DOCUMENT = "CLASSE_DOCUMENT";
	public static final String CHAMP_NOM_DOCUMENT = "NOM_DOCUMENT";
	public static final String CHAMP_LIEN_DOCUMENT = "LIEN_DOCUMENT";
	public static final String CHAMP_DATE_DOCUMENT = "DATE_DOCUMENT";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";
	public static final String CHAMP_ID_TYPE_DOCUMENT = "ID_TYPE_DOCUMENT";
	public static final String CHAMP_NOM_ORIGINAL = "NOM_ORIGINAL";
	public static final String CHAMP_NODE_REF_ALFRESCO = "NODE_REF_ALFRESCO";
	public static final String CHAMP_COMMENTAIRE_ALFRESCO = "COMMENTAIRE_ALFRESCO";
	public static final String CHAMP_REFERENCE = "REFERENCE";

	public DocumentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DOCUMENT_ASSOCIE";
		super.CHAMP_ID = "ID_DOCUMENT";
	}

	@Override
	public Document chercherDocumentById(Integer idDocument) throws Exception {
		return super.chercherObject(Document.class, idDocument);
	}

	@Override
	public ArrayList<Document> listerDocumentAvecType(Integer idTypeDocument) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_DOCUMENT + "=?";

		ArrayList<Document> liste = new ArrayList<Document>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeDocument });
		for (Map<String, Object> row : rows) {
			Document a = new Document();
			a.setIdDocument((Integer) row.get(CHAMP_ID));
			a.setClasseDocument((String) row.get(CHAMP_CLASSE_DOCUMENT));
			a.setNomDocument((String) row.get(CHAMP_NOM_DOCUMENT));
			a.setLienDocument((String) row.get(CHAMP_LIEN_DOCUMENT));
			a.setDateDocument((Date) row.get(CHAMP_DATE_DOCUMENT));
			a.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			a.setIdTypeDocument((Integer) row.get(CHAMP_ID_TYPE_DOCUMENT));
			a.setNomOriginal((String) row.get(CHAMP_NOM_ORIGINAL));
			a.setNodeRefAlfresco((String) row.get(CHAMP_NODE_REF_ALFRESCO));
			a.setCommentaireAlfresco((String) row.get(CHAMP_COMMENTAIRE_ALFRESCO));
			a.setReference((Integer) row.get(CHAMP_REFERENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerDocument(Integer idDocument) throws Exception {
		super.supprimerObject(idDocument);
	}

	@Override
	public Integer creerDocument(String classeDocument, String nomDocument, String lienDocument, Date dateDocument,
			String commentaire, Integer idTypeDocument, String nomOriginal, String nodeRefAlfresco, String commentaireAlfresco,
			Integer reference) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_CLASSE_DOCUMENT
				+ "," + CHAMP_NOM_DOCUMENT + "," + CHAMP_LIEN_DOCUMENT + "," + CHAMP_DATE_DOCUMENT + ","
				+ CHAMP_COMMENTAIRE + "," + CHAMP_ID_TYPE_DOCUMENT + "," + CHAMP_NOM_ORIGINAL + "," 
				+ CHAMP_NODE_REF_ALFRESCO + "," + CHAMP_COMMENTAIRE_ALFRESCO + "," + CHAMP_REFERENCE + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { classeDocument, nomDocument, lienDocument,
				dateDocument, commentaire, idTypeDocument, nomOriginal, nodeRefAlfresco, commentaireAlfresco, reference }, Integer.class);
		return id;
	}

	@Override
	public Document chercherDocumentByContainsNom(String nomFichier) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOM_DOCUMENT + " like ? ";
		Document doc = (Document) jdbcTemplate.queryForObject(sql, new Object[] { nomFichier + "%" },
				new BeanPropertyRowMapper<Document>(Document.class));
		return doc;
	}

	@Override
	public Document chercherDocumentParTypeEtAgent(String typeFichier, Integer idAgent) throws Exception {
		String sql = "select d.* from " + NOM_TABLE + " d,  P_TYPE_DOCUMENT t, DOCUMENT_AGENT da where d." + CHAMP_ID
				+ " = da.ID_DOCUMENT and d." + CHAMP_ID_TYPE_DOCUMENT
				+ " = t.ID_TYPE_DOCUMENT and d." + CHAMP_NODE_REF_ALFRESCO + " is NOT NULL and t.COD_TYPE_DOCUMENT = ? and da.ID_AGENT =?";
		
		Document doc = null;
		try {
			doc = (Document) jdbcTemplate.queryForObject(sql, new Object[] { typeFichier, idAgent },
				new BeanPropertyRowMapper<Document>(Document.class));
		} catch(EmptyResultDataAccessException e) {
			return doc;
		}
		
		return doc;
	}

	@Override
	public ArrayList<Document> listerDocumentAgentTYPE(DocumentAgentDao daoLienDocument, Integer idAgent,
			String module, String typDoc, Integer idDansListe) throws Exception {
		// Recherche de tous les liens Agent/ Document
		ArrayList<DocumentAgent> liens = daoLienDocument.listerDocumentAgentAvecModuleEtVue(idAgent, module, typDoc,
				idDansListe);
		if (liens.size() == 0)
			return new ArrayList<Document>();

		// Construction de la liste
		ArrayList<Document> result = new ArrayList<Document>();
		for (int i = 0; i < liens.size(); i++) {
			DocumentAgent aLien = (DocumentAgent) liens.get(i);
			Document aDocument = chercherDocumentById(aLien.getIdDocument());
			if (aDocument == null || aDocument.getIdDocument() == null)
				return new ArrayList<Document>();

			if (aDocument != null)
				result.add(aDocument);
		}
		return result;
	}

	@Override
	public ArrayList<Document> listerDocumentAgent(DocumentAgentDao daoLienDocument, Integer idAgent, String vue,
			String module) throws Exception {
		// Recherche de tous les liens Agent/ Document
		ArrayList<DocumentAgent> liens = daoLienDocument.listerDocumentAgentAvecModule(idAgent, module);
		if (liens.size() == 0)
			return new ArrayList<Document>();

		// Construction de la liste
		ArrayList<Document> result = new ArrayList<Document>();
		for (int i = 0; i < liens.size(); i++) {
			DocumentAgent aLien = (DocumentAgent) liens.get(i);
			Document aDocument = chercherDocumentById(aLien.getIdDocument());
			if (aDocument == null || aDocument.getIdDocument() == null)
				return new ArrayList<Document>();
			if (vue != null && vue.equals("Sauvegarde")) {
				if (aDocument != null && aDocument.getNomDocument().substring(0, 4).equals("Sauv"))
					result.add(aDocument);
			} else {
				if (aDocument != null && !aDocument.getNomDocument().substring(0, 4).equals("Sauv"))
					result.add(aDocument);
			}
		}
		return result;
	}
}
