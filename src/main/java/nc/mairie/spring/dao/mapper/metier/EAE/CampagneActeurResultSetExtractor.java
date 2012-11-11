package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.CampagneActeurDao;
import nc.mairie.spring.domain.metier.EAE.CampagneActeur;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CampagneActeurResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		CampagneActeur camp = new CampagneActeur();
		camp.setIdCampagneActeur(rs.getInt(CampagneActeurDao.CHAMP_ID_CAMPAGNE_ACTEUR));
		camp.setIdCampagneAction(rs.getInt(CampagneActeurDao.CHAMP_ID_CAMPAGNE_ACTION));
		camp.setIdAgent(rs.getInt(CampagneActeurDao.CHAMP_ID_AGENT));

		return camp;
	}
}
