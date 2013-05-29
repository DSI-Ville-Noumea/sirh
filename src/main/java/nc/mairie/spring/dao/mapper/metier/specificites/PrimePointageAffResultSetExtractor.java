package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.specificites.PrimePointageAffDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PrimePointageAffResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		PrimePointageAff prime = new PrimePointageAff();
		prime.setNumRubrique(rs.getInt(PrimePointageAffDao.CHAMP_NUM_RUBRIQUE));
		prime.setIdAffectation(rs.getInt(PrimePointageAffDao.CHAMP_ID_AFFECTATION));

		return prime;
	}
}
