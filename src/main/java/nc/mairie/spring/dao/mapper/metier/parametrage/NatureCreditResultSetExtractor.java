package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.NatureCreditDao;
import nc.mairie.spring.domain.metier.parametrage.NatureCredit;

import org.springframework.jdbc.core.ResultSetExtractor;

public class NatureCreditResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		NatureCredit nature = new NatureCredit();
		nature.setIdNatureCredit(rs.getInt(NatureCreditDao.CHAMP_ID_NATURE_CREDIT));
		nature.setLibNatureCredit(rs.getString(NatureCreditDao.CHAMP_LIB_NATURE_CREDIT));

		return nature;
	}
}
