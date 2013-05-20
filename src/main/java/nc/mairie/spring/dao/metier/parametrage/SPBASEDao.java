package nc.mairie.spring.dao.metier.parametrage;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SPBASEDao implements SPBASEDaoInterface {

	private Logger logger = LoggerFactory.getLogger(SPBASEDao.class);

	public static final String NOM_TABLE = "MAIRIE.SPBASE";

	public static final String CHAMP_CDBASE = "CDBASE";
	public static final String CHAMP_NBASHH = "NBASHH";
	public static final String CHAMP_CDCBAS = "CDCBAS";
	public static final String CHAMP_LIBASE = "LIBASE";
	public static final String CHAMP_NBHSA = "NBHSA";
	public static final String CHAMP_NBHDI = "NBHDI";
	public static final String CHAMP_NBHLU = "NBHLU";
	public static final String CHAMP_NBHMA = "NBHMA";
	public static final String CHAMP_NBHME = "NBHME";
	public static final String CHAMP_NBHJE = "NBHJE";
	public static final String CHAMP_NBHVE = "NBHVE";
	public static final String CHAMP_NBASCH = "NBASCH";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public SPBASEDao() {

	}
}
