package nc.mairie.spring.dao.metier.avancement;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class AvancementCapPrintJobDao implements AvancementCapPrintJobDaoInterface {

	public static final String NOM_TABLE = "SIRH.AVCT_CAP_PRINT_JOB";

	public static final String CHAMP_ID_AVCT_CAP_PRINT_JOB = "ID_AVCT_CAP_PRINT_JOB";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_LOGIN = "LOGIN";
	public static final String CHAMP_ID_CAP = "ID_CAP";
	public static final String CHAMP_CODE_CAP = "CODE_CAP";
	public static final String CHAMP_ID_CADRE_EMPLOI = "ID_CADRE_EMPLOI";
	public static final String CHAMP_LIB_CADRE_EMPLOI = "LIB_CADRE_EMPLOI";
	public static final String CHAMP_IS_EAES = "IS_EAES";
	public static final String CHAMP_DATE_SUBMISSION = "DATE_SUBMISSION";
	public static final String CHAMP_DATE_STATUT = "DATE_STATUT";
	public static final String CHAMP_STATUT = "STATUT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public AvancementCapPrintJobDao() {

	}

	@Override
	public void creerAvancementCapPrintJob(Integer idAgent, String login, Integer idCap, String codeCap, Integer idCadreEmploi,
			String libCadreEmploi, boolean isEaes) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_LOGIN + "," + CHAMP_ID_CAP + "," + CHAMP_CODE_CAP + ","
				+ CHAMP_ID_CADRE_EMPLOI + "," + CHAMP_LIB_CADRE_EMPLOI + "," + CHAMP_IS_EAES + ") " + "VALUES (?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, login, idCap, codeCap, idCadreEmploi, libCadreEmploi, isEaes });

	}
}
