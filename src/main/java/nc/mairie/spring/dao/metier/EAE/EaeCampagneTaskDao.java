package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import nc.mairie.metier.eae.EaeCampagneTask;
import nc.mairie.spring.dao.utils.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class EaeCampagneTaskDao extends EaeDao implements EaeCampagneTaskDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeCampagneTaskDao.class);

	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_CALCUL_EAE = "DATE_CALCUL_EAE";
	public static final String CHAMP_TASK_STATUS = "TASK_STATUS";

	public EaeCampagneTaskDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_CAMPAGNE_TASK";
		super.CHAMP_ID = "ID_CAMPAGNE_TASK";
	}

	@Override
	public void creerEaeCampagneTask(Integer idCampagneEae, Integer annee, Integer idAgent, Date dateCalculEae,
			String taskStatus) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ANNEE + ","
				+ CHAMP_ID_AGENT + "," + CHAMP_DATE_CALCUL_EAE + "," + CHAMP_TASK_STATUS + ") " + "VALUES (?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idCampagneEae, annee, idAgent, dateCalculEae, taskStatus });

	}

	@Override
	public EaeCampagneTask chercherEaeCampagneTask(Integer idCampagneTask) throws Exception {

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + "=?";
		try {
			EaeCampagneTask eaeCampagneTask = (EaeCampagneTask) jdbcTemplate.queryForObject(sql,
					new Object[] { idCampagneTask }, new BeanPropertyRowMapper<EaeCampagneTask>(EaeCampagneTask.class));
			return eaeCampagneTask;
		} catch (Exception e) {
			logger.debug("Aucun EaeCampagneTask trouvé pour idCampagneTask=" + idCampagneTask.toString());
			return null;
		}
	}

	@Override
	public EaeCampagneTask chercherEaeCampagneTaskByIdCampagneEae(Integer idCampagneEae) throws Exception {

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + "=? AND "
				+ CHAMP_DATE_CALCUL_EAE + " IS NULL AND " + CHAMP_TASK_STATUS + " IS NULL";
		try {
			EaeCampagneTask eaeCampagneTask = (EaeCampagneTask) jdbcTemplate.queryForObject(sql,
					new Object[] { idCampagneEae }, new BeanPropertyRowMapper<EaeCampagneTask>(EaeCampagneTask.class));
			return eaeCampagneTask;
		} catch (Exception e) {
			logger.debug("Aucun EaeCampagneTask trouvé pour idCampagneEae=" + idCampagneEae.toString());
			return null;
		}
	}

}
