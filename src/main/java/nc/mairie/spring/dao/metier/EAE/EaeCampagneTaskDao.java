package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeCampagneTaskRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeCampagneTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeCampagneTaskDao implements EaeCampagneTaskDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeCampagneTaskDao.class);

	public static final String NOM_TABLE = "EAE_CAMPAGNE_TASK";

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_TASK";

	public static final String CHAMP_ID_CAMPAGNE_TASK = "ID_CAMPAGNE_TASK";
	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_CALCUL_EAE = "DATE_CALCUL_EAE";
	public static final String CHAMP_TASK_STATUS = "TASK_STATUS";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeCampagneTaskDao() {
	}
	
	@Override
	public Integer creerEaeCampagneTask(Integer idCampagneEae, Integer annee, String idAgent,
			Date dateCalculEae, String taskStatus) throws Exception {

		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_TASK + "," + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ANNEE
				+ "," + CHAMP_ID_AGENT + "," + CHAMP_DATE_CALCUL_EAE + "," + CHAMP_TASK_STATUS + ") " 
				+ "VALUES (?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { id, idCampagneEae, annee, idAgent, dateCalculEae, taskStatus});

		return id;
	}

	@Override
	public EaeCampagneTask chercherEaeCampagneTask(Integer idCampagneTask)
			throws Exception {
		
		String sql = "select * from " + NOM_TABLE
				+ " where "	+ CHAMP_ID_CAMPAGNE_TASK + "=?";
		try {
			EaeCampagneTask eaeCampagneTask = (EaeCampagneTask) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneTask },
					new EaeCampagneTaskRowMapper());
			return eaeCampagneTask;
		} catch (Exception e) {
			logger.debug("Aucun EaeCampagneTask trouvé pour idCampagneTask="
					+ idCampagneTask.toString());
			return null;
		}
	}
	
	@Override
	public EaeCampagneTask chercherEaeCampagneTaskByIdCampagneEae(Integer idCampagneEae)
			throws Exception {
		
		String sql = "select * from " + NOM_TABLE
				+ " where "	+ CHAMP_ID_CAMPAGNE_EAE + "=? AND " + CHAMP_DATE_CALCUL_EAE + " IS NULL AND " + CHAMP_TASK_STATUS + " IS NULL";
		try {
			EaeCampagneTask eaeCampagneTask = (EaeCampagneTask) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneEae },
					new EaeCampagneTaskRowMapper());
			return eaeCampagneTask;
		} catch (Exception e) {
			logger.debug("Aucun EaeCampagneTask trouvé pour idCampagneEae="
					+ idCampagneEae.toString());
			return null;
		}
	}

}
