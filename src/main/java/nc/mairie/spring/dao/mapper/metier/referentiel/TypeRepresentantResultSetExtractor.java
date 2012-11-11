package nc.mairie.spring.dao.mapper.metier.referentiel;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.referentiel.TypeRepresentantDao;
import nc.mairie.spring.domain.metier.referentiel.TypeRepresentant;

import org.springframework.jdbc.core.ResultSetExtractor;

public class TypeRepresentantResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		TypeRepresentant typeRepre = new TypeRepresentant();
		typeRepre.setIdTypeRepresentant(rs.getInt(TypeRepresentantDao.CHAMP_ID_TYPE_REPRESENTANT));
		typeRepre.setLibTypeRepresentant(rs.getString(TypeRepresentantDao.CHAMP_LIB_TYPE_REPRESENTANT));

		return typeRepre;
	}
}
