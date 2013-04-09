package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaePlanActionDao;
import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaePlanActionResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaePlanAction eaePlanAction = new EaePlanAction();
		eaePlanAction.setIdEaePlanAction(rs.getInt(EaePlanActionDao.CHAMP_ID_EAE_PLAN_ACTION));
		eaePlanAction.setIdTypeObjectif(rs.getInt(EaePlanActionDao.CHAMP_ID_EAE_TYPE_OBJECTIF));
		eaePlanAction.setIdEae(rs.getInt(EaePlanActionDao.CHAMP_ID_EAE));
		eaePlanAction.setObjectif(rs.getString(EaePlanActionDao.CHAMP_OBJECTIF));
		eaePlanAction.setMesure(rs.getString(EaePlanActionDao.CHAMP_MESURE));

		return eaePlanAction;
	}
}
