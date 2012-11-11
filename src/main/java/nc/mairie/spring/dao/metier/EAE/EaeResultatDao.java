package nc.mairie.spring.dao.metier.EAE;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class EaeResultatDao implements EaeResultatDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaeResultatDao.class);

	public static final String NOM_TABLE = "EAE_RESULTAT";

	public static final String NOM_SEQUENCE = "EAE_S_RESULTAT";

	public static final String CHAMP_ID_EAE_RESULTAT = "ID_EAE_RESULTAT";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_EAE_TYPE_OBJECTIF = "ID_EAE_TYPE_OBJECTIF";
	public static final String CHAMP_OBJECTIF = "OBJECTIF";
	public static final String CHAMP_RESULTAT = "RESULTAT";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeResultatDao() {

	}

	@Override
	public Connection creerEaeResultat(Integer idEae, Integer idTypeObjectif, String objectif, String resultat, String commentaire) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_RESULTAT + "," + CHAMP_ID_EAE + "," + CHAMP_ID_EAE_TYPE_OBJECTIF + ","
				+ CHAMP_OBJECTIF + "," + CHAMP_RESULTAT + "," + CHAMP_COMMENTAIRE + ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?,?,?,?)";
		DataSourceUtils.getConnection(jdbcTemplate.getDataSource()).setAutoCommit(false);

		jdbcTemplate.update(sql, new Object[] { idEae, idTypeObjectif, objectif, resultat, commentaire });

		return DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
	}
}
