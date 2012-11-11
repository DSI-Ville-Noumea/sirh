package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.RepresentantDao;
import nc.mairie.spring.domain.metier.parametrage.Representant;

import org.springframework.jdbc.core.ResultSetExtractor;

public class RepresentantResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		Representant repre = new Representant();
		repre.setIdRepresentant(rs.getInt(RepresentantDao.CHAMP_ID_REPRESENTANT));
		repre.setIdTypeRepresentant(rs.getInt(RepresentantDao.CHAMP_ID_TYPE_REPRESENTANT));
		repre.setNomRepresentant(rs.getString(RepresentantDao.CHAMP_NOM_REPRESENTANT));
		repre.setPrenomRepresentant(rs.getString(RepresentantDao.CHAMP_PRENOM_REPRESENTANT));

		return repre;
	}
}
