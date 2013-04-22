package nc.mairie.spring.dao.metier.specificites;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageFPDao implements PrimePointageFPDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE_FP";

	public static final String CHAMP_ID_PRIME_POINTAGE = "ID_PRIME_POINTAGE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public PrimePointageFPDao() {

	}
}
