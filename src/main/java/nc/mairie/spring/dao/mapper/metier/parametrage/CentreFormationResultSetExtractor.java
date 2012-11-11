package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.CentreFormationDao;
import nc.mairie.spring.domain.metier.parametrage.CentreFormation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CentreFormationResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		CentreFormation centre = new CentreFormation();
		centre.setIdCentreFormation(rs.getInt(CentreFormationDao.CHAMP_ID_CENTRE_FORMATION));
		centre.setLibCentreFormation(rs.getString(CentreFormationDao.CHAMP_LIB_CENTRE_FORMATION));

		return centre;
	}
}
