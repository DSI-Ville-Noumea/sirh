package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.TitreFormationDao;
import nc.mairie.spring.domain.metier.parametrage.TitreFormation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class TitreFormationResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		TitreFormation titre = new TitreFormation();
		titre.setIdTitreFormation(rs.getInt(TitreFormationDao.CHAMP_ID_TITRE_FORMATION));
		titre.setLibTitreFormation(rs.getString(TitreFormationDao.CHAMP_LIB_TITRE_FORMATION));

		return titre;
	}
}
