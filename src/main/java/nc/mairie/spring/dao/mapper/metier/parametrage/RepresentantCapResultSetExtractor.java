package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.RepresentantCapDao;
import nc.mairie.spring.domain.metier.parametrage.RepresentantCap;

import org.springframework.jdbc.core.ResultSetExtractor;

public class RepresentantCapResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		RepresentantCap repreCap = new RepresentantCap();
		repreCap.setIdRepresentant(rs.getInt(RepresentantCapDao.CHAMP_ID_REPRESENTANT));
		repreCap.setIdCap(rs.getInt(RepresentantCapDao.CHAMP_ID_CAP));
		repreCap.setPosition(rs.getInt(RepresentantCapDao.CHAMP_POSITION));

		return repreCap;
	}
}
