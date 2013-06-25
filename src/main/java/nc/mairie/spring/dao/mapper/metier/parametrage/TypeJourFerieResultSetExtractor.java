package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.TypeJourFerieDao;
import nc.mairie.spring.domain.metier.parametrage.TypeJourFerie;

import org.springframework.jdbc.core.ResultSetExtractor;

public class TypeJourFerieResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		TypeJourFerie type = new TypeJourFerie();
		type.setIdTypeJour(rs.getInt(TypeJourFerieDao.CHAMP_ID_TYPE_JOUR_FERIE));
		type.setLibelle(rs.getString(TypeJourFerieDao.CHAMP_LIB_TYPE_JOUR_FERIE));

		return type;
	}
}
