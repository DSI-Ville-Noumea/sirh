package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.EmployeurCapDao;
import nc.mairie.spring.domain.metier.parametrage.EmployeurCap;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EmployeurCapResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EmployeurCap employeurCap = new EmployeurCap();
		employeurCap.setIdEmployeur(rs.getInt(EmployeurCapDao.CHAMP_ID_EMPLOYEUR));
		employeurCap.setIdCap(rs.getInt(EmployeurCapDao.CHAMP_ID_CAP));
		employeurCap.setPosition(rs.getInt(EmployeurCapDao.CHAMP_POSITION));

		return employeurCap;
	}
}
