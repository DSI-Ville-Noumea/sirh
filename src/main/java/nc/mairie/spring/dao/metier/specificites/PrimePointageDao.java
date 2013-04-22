package nc.mairie.spring.dao.metier.specificites;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageDao implements PrimePointageDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE";

	public static final String CHAMP_ID_PRIME_POINTAGE = "ID_PRIME_POINTAGE";
	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public PrimePointageDao() {

	}
}
