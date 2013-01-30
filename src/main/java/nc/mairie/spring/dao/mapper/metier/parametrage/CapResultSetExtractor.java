package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.domain.metier.parametrage.Cap;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CapResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		Cap cap = new Cap();
		cap.setIdCap(rs.getInt(CapDao.CHAMP_ID_CAP));
		cap.setCodeCap(rs.getString(CapDao.CHAMP_CODE_CAP));
		cap.setRefCap(rs.getString(CapDao.CHAMP_REF_CAP));
		cap.setDescription(rs.getString(CapDao.CHAMP_DESCRIPTION));

		return cap;
	}
}
