package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.DeliberationDao;
import nc.mairie.spring.domain.metier.parametrage.Deliberation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class DeliberationResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		Deliberation delib = new Deliberation();
		delib.setIdDeliberation(rs.getInt(DeliberationDao.CHAMP_ID_DELIBERATION));
		delib.setCodeDeliberation(rs.getString(DeliberationDao.CHAMP_ID_DELIBERATION));
		delib.setLibDeliberation(rs.getString(DeliberationDao.CHAMP_ID_DELIBERATION));
		delib.setTypeDeliberation(rs.getString(DeliberationDao.CHAMP_ID_DELIBERATION));
		delib.setTexteCAP(rs.getString(DeliberationDao.CHAMP_ID_DELIBERATION));

		return delib;
	}
}
