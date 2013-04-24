package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointageFP;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PrimePointageFPResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		PrimePointageFP prime = new PrimePointageFP();
		prime.setIdPrimePointage(rs.getInt(PrimePointageFPDao.CHAMP_ID_PRIME_POINTAGE));
		prime.setIdFichePoste(rs.getInt(PrimePointageFPDao.CHAMP_ID_FICHE_POSTE));

		return prime;
	}
}
