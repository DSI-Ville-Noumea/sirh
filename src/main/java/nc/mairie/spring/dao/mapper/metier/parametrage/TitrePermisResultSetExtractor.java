package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.TitrePermisDao;
import nc.mairie.spring.domain.metier.parametrage.TitrePermis;

import org.springframework.jdbc.core.ResultSetExtractor;

public class TitrePermisResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		TitrePermis titre = new TitrePermis();
		titre.setIdTitrePermis(rs.getInt(TitrePermisDao.CHAMP_ID_PERMIS));
		titre.setLibTitrePermis(rs.getString(TitrePermisDao.CHAMP_LIB_PERMIS));

		return titre;
	}
}
