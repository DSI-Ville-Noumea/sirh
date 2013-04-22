package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.specificites.PrimePointageAffDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PrimePointageAffResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		PrimePointageAff prime = new PrimePointageAff();
		prime.setIdPrimePointage(rs.getInt(PrimePointageAffDao.CHAMP_ID_PRIME_POINTAGE));
		prime.setIdAffectation(rs.getInt(PrimePointageAffDao.CHAMP_ID_AFFECTATION));

		return prime;
	}
}
