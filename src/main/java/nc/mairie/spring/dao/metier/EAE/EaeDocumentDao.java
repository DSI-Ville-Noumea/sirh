package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeDocumentDao implements EaeDocumentDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeDocumentDao.class);

	public static final String NOM_TABLE = "EAE_DOCUMENT";

	public static final String CHAMP_ID_EAE_DOCUMENT = "ID_EAE_DOCUMENT";
	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_ID_CAMPAGNE_ACTION = "ID_CAMPAGNE_ACTION";
	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";
	public static final String CHAMP_TYPE_DOCUMENT = "TYPE_DOCUMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeDocumentDao() {

	}

	@Override
	public void creerEaeDocument(Integer idCampagneEae, Integer idCampagneAction, Integer idDocument, String type)
			throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ID_CAMPAGNE_ACTION + ","
				+ CHAMP_ID_DOCUMENT + "," + CHAMP_TYPE_DOCUMENT + ") " + "VALUES (?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idCampagneEae, idCampagneAction, idDocument, type });
	}

	@Override
	public EaeDocument chercherEaeDocument(Integer idDocument) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_DOCUMENT + " = ? ";
		EaeDocument doc = (EaeDocument) jdbcTemplate.queryForObject(sql, new Object[] { idDocument }, new BeanPropertyRowMapper<EaeDocument>(EaeDocument.class));
		return doc;
	}

	@Override
	public void supprimerEaeDocument(Integer idEaeDocument) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EAE_DOCUMENT + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaeDocument });
	}

	@Override
	public ArrayList<EaeDocument> listerEaeDocument(Integer idCampagneEae, Integer idCampagneAction, String type)
			throws Exception {
		List<Map<String, Object>> rows;
		if (idCampagneAction == null) {
			String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + "=? and "
					+ CHAMP_ID_CAMPAGNE_ACTION + " is null and " + CHAMP_TYPE_DOCUMENT + "=?";

			rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEae, type });
		} else {
			String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + "=? and "
					+ CHAMP_ID_CAMPAGNE_ACTION + "=? and " + CHAMP_TYPE_DOCUMENT + "=?";

			rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEae, idCampagneAction, type });
		}

		ArrayList<EaeDocument> listeEaeDocument = new ArrayList<EaeDocument>();
		for (Map<String, Object> row : rows) {
			EaeDocument docu = new EaeDocument();
			logger.info("List doc campagne EAE : " + row.toString());
			docu.setIdEaeDocument((Integer) row.get(CHAMP_ID_EAE_DOCUMENT));
			docu.setIdCampagneEae((Integer) row.get(CHAMP_ID_CAMPAGNE_EAE));
			docu.setIdCampagneAction((Integer) row.get(CHAMP_ID_CAMPAGNE_ACTION));
			docu.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			docu.setTypeDocument((String) row.get(CHAMP_TYPE_DOCUMENT));
			listeEaeDocument.add(docu);
		}

		return listeEaeDocument;
	}
}
