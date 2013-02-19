package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeDeveloppementDao implements EaeDeveloppementDaoInterface {

	public static final String NOM_TABLE = "EAE_DEVELOPPEMENT";

	public static final String NOM_SEQUENCE = "EAE_S_DEVELOPPEMENT";

	public static final String CHAMP_ID_EAE_DEVELOPPEMENT = "ID_EAE_DEVELOPPEMENT";
	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_LIBELLE = "LIBELLE";
	public static final String CHAMP_ECHEANCE = "ECHEANCE";
	public static final String CHAMP_PRIORISATION = "PRIORISATION";
	public static final String CHAMP_TYPE_DEVELOPPEMENT = "TYPE_DEVELOPPEMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeDeveloppementDao() {

	}
}
