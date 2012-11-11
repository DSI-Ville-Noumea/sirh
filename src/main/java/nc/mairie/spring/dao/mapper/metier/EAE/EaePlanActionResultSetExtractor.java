package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaePlanActionDao;
import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaePlanActionResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaePlanAction plan = new EaePlanAction();
		plan.setIdEaePlanAction(rs.getInt(EaePlanActionDao.CHAMP_ID_EAE_PLAN_ACTION));
		plan.setIdEae(rs.getInt(EaePlanActionDao.CHAMP_ID_EAE));
		plan.setIdTypeObjectif(rs.getInt(EaePlanActionDao.CHAMP_ID_TYPE_OBJECTIF));
		plan.setObjectif(rs.getString(EaePlanActionDao.CHAMP_OBJECTIF));
		plan.setMesure(rs.getString(EaePlanActionDao.CHAMP_MESURE));

		return plan;
	}
}
