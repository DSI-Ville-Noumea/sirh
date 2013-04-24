package nc.mairie.spring.dao.mapper.metier.suiviMedical;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.domain.metier.suiviMedical.MotifVisiteMed;

import org.springframework.jdbc.core.ResultSetExtractor;

public class MotifVisiteMedResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		MotifVisiteMed mvm = new MotifVisiteMed();
		mvm.setIdMotifVM(rs.getInt(MotifVisiteMedDao.CHAMP_ID_MOTIF_VM));
		mvm.setLibMotifVM(rs.getString(MotifVisiteMedDao.CHAMP_LIB_MOTIF_VM));
		return mvm;
	}
}
