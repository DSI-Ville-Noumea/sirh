package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.avancement.AvancementCapPrintJob;

import org.springframework.jdbc.core.JdbcTemplate;

public class AvancementCapPrintJobDao implements AvancementCapPrintJobDaoInterface {

	public static final String NOM_TABLE = "AVCT_CAP_PRINT_JOB";

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
	public static final String CHAMP_JOB_ID = "JOB_ID";
	public static final String CHAMP_AVIS_EAE = "AVIS_EAE";

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
			String libCadreEmploi, boolean isEaes, boolean avisEAE) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_LOGIN + "," + CHAMP_ID_CAP + "," + CHAMP_CODE_CAP + ","
				+ CHAMP_ID_CADRE_EMPLOI + "," + CHAMP_LIB_CADRE_EMPLOI + "," + CHAMP_IS_EAES + "," + CHAMP_AVIS_EAE + ") " + "VALUES (?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, login, idCap, codeCap, idCadreEmploi, libCadreEmploi, isEaes ,avisEAE});

	}

	@Override
	public ArrayList<AvancementCapPrintJob> listerAvancementCapPrintJob() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_DATE_SUBMISSION + " desc";

		ArrayList<AvancementCapPrintJob> listeAvancementCapPrintJob = new ArrayList<AvancementCapPrintJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			AvancementCapPrintJob job = new AvancementCapPrintJob();

			job.setIdAvancementCapPrintJob((Integer) row.get(CHAMP_ID_AVCT_CAP_PRINT_JOB));
			job.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			job.setLogin((String) row.get(CHAMP_LOGIN));
			job.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			job.setCodeCap((String) row.get(CHAMP_CODE_CAP));
			job.setIdCadreEmploi((Integer) row.get(CHAMP_ID_CADRE_EMPLOI));
			job.setLibCadreEmploi((String) row.get(CHAMP_LIB_CADRE_EMPLOI));
			Integer isEae = (Integer) row.get(CHAMP_IS_EAES);
			job.setEaes(isEae == 0 ? false : true);
			job.setDateSubmission((Date) row.get(CHAMP_DATE_SUBMISSION));
			job.setDateStatut((Date) row.get(CHAMP_DATE_STATUT));
			job.setStatut((String) row.get(CHAMP_STATUT));
			job.setJobId((String) row.get(CHAMP_JOB_ID));
			Integer avisEAE = (Integer) row.get(CHAMP_AVIS_EAE);
			job.setAvisEAE(avisEAE == 0 ? false : true);
			listeAvancementCapPrintJob.add(job);
		}

		return listeAvancementCapPrintJob;
	}
}
