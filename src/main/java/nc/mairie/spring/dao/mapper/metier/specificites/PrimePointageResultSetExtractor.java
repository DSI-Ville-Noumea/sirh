package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.specificites.PrimePointageDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointage;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PrimePointageResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		PrimePointage prime = new PrimePointage();
		prime.setIdPrimePointage(rs.getInt(PrimePointageDao.CHAMP_ID_PRIME_POINTAGE));
		prime.setIdRubrique(rs.getInt(PrimePointageDao.CHAMP_NUM_RUBRIQUE));

		return prime;
	}
}
