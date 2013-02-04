package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.CorpsCapDao;
import nc.mairie.spring.domain.metier.parametrage.CorpsCap;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CorpsCapResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		CorpsCap corpsCap = new CorpsCap();
		corpsCap.setCodeSpgeng(rs.getString(CorpsCapDao.CHAMP_CDGENG));
		corpsCap.setIdCap(rs.getInt(CorpsCapDao.CHAMP_ID_CAP));

		return corpsCap;
	}
}
