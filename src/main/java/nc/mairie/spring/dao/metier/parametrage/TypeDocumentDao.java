package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class TypeDocumentDao extends SirhDao implements TypeDocumentDaoInterface {

	public static final String CHAMP_LIB_TYPE_DOCUMENT = "LIB_TYPE_DOCUMENT";
	public static final String CHAMP_COD_TYPE_DOCUMENT = "COD_TYPE_DOCUMENT";
	public static final String CHAMP_MODULE_TYPE_DOCUMENT = "MODULE_TYPE_DOCUMENT";
	public static final String CHAMP_ID_PATH_ALFRESCO = "ID_PATH_ALFRESCO";

	public TypeDocumentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_DOCUMENT";
		super.CHAMP_ID = "ID_TYPE_DOCUMENT";
	}

	@Override
	public ArrayList<TypeDocument> listerTypeDocumentAvecModule(String moduleTypeDocument) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_MODULE_TYPE_DOCUMENT + "=?  order by "
				+ CHAMP_LIB_TYPE_DOCUMENT;

		ArrayList<TypeDocument> listeTitreFormation = new ArrayList<TypeDocument>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { moduleTypeDocument });
		for (Map<String, Object> row : rows) {
			TypeDocument titre = new TypeDocument();
			titre.setIdTypeDocument((Integer) row.get(CHAMP_ID));
			titre.setLibTypeDocument((String) row.get(CHAMP_LIB_TYPE_DOCUMENT));
			titre.setCodTypeDocument((String) row.get(CHAMP_COD_TYPE_DOCUMENT));
			titre.setModuleTypeDocument((String) row.get(CHAMP_MODULE_TYPE_DOCUMENT));
			titre.setIdPathAlfresco((Integer) row.get(CHAMP_ID_PATH_ALFRESCO));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TypeDocument chercherTypeDocument(Integer idTypeDocument) throws Exception {
		return super.chercherObject(TypeDocument.class, idTypeDocument);
	}

	@Override
	public TypeDocument chercherTypeDocumentByCod(String codTypeDocument) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_COD_TYPE_DOCUMENT + " = ? ";
		TypeDocument c = (TypeDocument) jdbcTemplate.queryForObject(sql, new Object[] { codTypeDocument },
				new BeanPropertyRowMapper<TypeDocument>(TypeDocument.class));
		return c;
	}

	@Override
	public void creerTypeDocument(String libelleTypeDocument, String codTypeDocument, String moduleTypeDocument, Integer idPathAlfresco)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TYPE_DOCUMENT + "," + CHAMP_COD_TYPE_DOCUMENT + ","
				+ CHAMP_MODULE_TYPE_DOCUMENT + "," + CHAMP_ID_PATH_ALFRESCO + ") " + "VALUES (?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleTypeDocument.toUpperCase(), codTypeDocument.toUpperCase(),
				moduleTypeDocument, idPathAlfresco });
	}

	@Override
	public void supprimerTypeDocument(Integer idTypeDocument) throws Exception {
		super.supprimerObject(idTypeDocument);
	}


	@Override
	public ArrayList<TypeDocument> listerTypeDocument() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TYPE_DOCUMENT;

		ArrayList<TypeDocument> listeTitreFormation = new ArrayList<TypeDocument>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] {});
		for (Map<String, Object> row : rows) {
			TypeDocument titre = new TypeDocument();
			titre.setIdTypeDocument((Integer) row.get(CHAMP_ID));
			titre.setLibTypeDocument((String) row.get(CHAMP_LIB_TYPE_DOCUMENT));
			titre.setCodTypeDocument((String) row.get(CHAMP_COD_TYPE_DOCUMENT));
			titre.setModuleTypeDocument((String) row.get(CHAMP_MODULE_TYPE_DOCUMENT));
			titre.setIdPathAlfresco((Integer) row.get(CHAMP_ID_PATH_ALFRESCO));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public ArrayList<String> listerModuleDocument() throws Exception {
		String sql = "select DISTINCT( " + CHAMP_MODULE_TYPE_DOCUMENT + " ) from " + NOM_TABLE + " order by " + CHAMP_MODULE_TYPE_DOCUMENT;

		ArrayList<String> listeModule = new ArrayList<String>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] {});
		for (Map<String, Object> row : rows) {
			String module = (String) row.get(CHAMP_MODULE_TYPE_DOCUMENT);
			listeModule.add(module);
		}

		return listeModule;
	}
}
