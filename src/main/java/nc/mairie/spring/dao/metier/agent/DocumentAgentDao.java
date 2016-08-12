package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DocumentAgentDao extends SirhDao implements DocumentAgentDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";

	public DocumentAgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DOCUMENT_AGENT";
	}

	@Override
	public void supprimerDocumentAgent(Integer idAgent, Integer idDocument) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AGENT + "=? and " + CHAMP_ID_DOCUMENT + "=?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idDocument });
	}

	@Override
	public void creerDocumentAgent(Integer idAgent, Integer idDocument) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_DOCUMENT + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idDocument });
	}

	@Override
	public DocumentAgent chercherDocumentAgent(Integer idAgent, Integer idDocument) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " =?  and " + CHAMP_ID_DOCUMENT
				+ " =?";
		DocumentAgent doc = (DocumentAgent) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idDocument },
				new BeanPropertyRowMapper<DocumentAgent>(DocumentAgent.class));
		return doc;
	}

	@Override
	public ArrayList<DocumentAgent> listerDocumentAgentAvecModule(Integer idAgent, String module) throws Exception {
		String sql = "select lien.* from " + NOM_TABLE + " "
				+ " lien inner join DOCUMENT_ASSOCIE doc on doc.ID_DOCUMENT = lien." + CHAMP_ID_DOCUMENT
				+ " and doc.node_ref_alfresco is not NULL "
				+ " inner join P_TYPE_DOCUMENT typeDoc on typeDoc.ID_TYPE_DOCUMENT = doc.ID_TYPE_DOCUMENT  "
				+ " where lien." + CHAMP_ID_AGENT
				+ " =?  and typeDoc.MODULE_TYPE_DOCUMENT =? order by doc.nom_original";

		ArrayList<DocumentAgent> liste = new ArrayList<DocumentAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent, module });
		for (Map<String, Object> row : rows) {
			DocumentAgent a = new DocumentAgent();
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<DocumentAgent> listerDocumentAgentAvecModuleEtVue(Integer idAgent, String module, String typDoc,
			Integer idDansListe) throws Exception {
		String sql = "select lien.* from "
				+ NOM_TABLE
				+ " lien inner join DOCUMENT_ASSOCIE doc on doc.ID_DOCUMENT = lien.ID_DOCUMENT and doc.NODE_REF_ALFRESCO is not NULL "
				+ " inner join P_TYPE_DOCUMENT typeDoc on typeDoc.ID_TYPE_DOCUMENT = doc.ID_TYPE_DOCUMENT  "
				+ " where lien.ID_AGENT =? and typeDoc.MODULE_TYPE_DOCUMENT =? and typeDoc.COD_TYPE_DOCUMENT = ? and doc.REFERENCE =? order by doc.nom_original";

		ArrayList<DocumentAgent> liste = new ArrayList<DocumentAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent, module,
				typDoc, idDansListe });
		for (Map<String, Object> row : rows) {
			DocumentAgent a = new DocumentAgent();
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			liste.add(a);
		}

		return liste;
	}
}
