package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeNumIncrementDocumentDao implements EaeNumIncrementDocumentDaoInterface {

	public static final String NOM_SEQUENCE = "EAE_S_NUM_INCREMENT_DOCUMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeNumIncrementDocumentDao() {

	}

	@Override
	public Integer chercherEaeNumIncrement() throws Exception {
		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);
		String sqlAugmenteVersion = "ALTER sequence " + NOM_SEQUENCE + " increment BY 1";
		jdbcTemplate.update(sqlAugmenteVersion);

		return id;
	}
}
