package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFinalisationDao implements EaeFinalisationDaoInterface {

	public static final String NOM_TABLE = "EAE_FINALISATION";

	public static final String NOM_SEQUENCE = "EAE_S_FINALISATION";

	public static final String CHAMP_ID_EAE_FINALISATION = "ID_EAE_FINALISATION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_DATE_FINALISATION = "DATE_FINALISATION";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_GED_DOCUMENT = "ID_GED_DOCUMENT";
	public static final String CHAMP_VERSION_GED_DOCUMENT = "VERSION_GED_DOCUMENT";

	private Logger logger = LoggerFactory.getLogger(EaeFinalisationDao.class);

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeFinalisationDao() {

	}

	@Override
	public String chercherDernierDocumentFinalise(Integer idEAE) throws Exception {
		String sql = "select distinct " + CHAMP_ID_GED_DOCUMENT + " from " + NOM_TABLE + " where " + CHAMP_ID_EAE + " = ? ";

		String finalisation = null;
		try {
			finalisation = (String) jdbcTemplate.queryForObject(sql, new Object[] { idEAE }, String.class);

		} catch (Exception e) {
			logger.error("Erreur dans la recherche du document finalise : ", e);
		}
		return finalisation;
	}
}
