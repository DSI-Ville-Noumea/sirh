package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeCampagneTaskDao;
import nc.mairie.spring.domain.metier.EAE.EaeCampagneTask;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeCampagneTaskResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeCampagneTask camp = new EaeCampagneTask();
		camp.setIdCampagneTask(rs.getInt(EaeCampagneTaskDao.CHAMP_ID_CAMPAGNE_TASK));
		camp.setIdCampagneEAE(rs.getInt(EaeCampagneTaskDao.CHAMP_ID_CAMPAGNE_EAE));
		camp.setAnnee(rs.getInt(EaeCampagneTaskDao.CHAMP_ANNEE));
		camp.setIdAgent(rs.getInt(EaeCampagneTaskDao.CHAMP_ID_AGENT));
		camp.setDateCalculEae(rs.getDate(EaeCampagneTaskDao.CHAMP_DATE_CALCUL_EAE));
		camp.setTaskStatus(rs.getString(EaeCampagneTaskDao.CHAMP_TASK_STATUS));

		return camp;
	}
}
