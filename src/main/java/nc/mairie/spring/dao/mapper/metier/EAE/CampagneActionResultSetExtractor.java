package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.CampagneActionDao;
import nc.mairie.spring.domain.metier.EAE.CampagneAction;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CampagneActionResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		CampagneAction camp = new CampagneAction();
		camp.setIdCampagneAction(rs.getInt(CampagneActionDao.CHAMP_ID_CAMPAGNE_ACTION));
		camp.setNomAction(rs.getString(CampagneActionDao.CHAMP_NOM_ACTION));
		camp.setMessage(rs.getString(CampagneActionDao.CHAMP_MESSAGE));
		camp.setDateTransmission(rs.getDate(CampagneActionDao.CHAMP_DATE_TRANSMISSION));
		camp.setDiffuse(rs.getBoolean(CampagneActionDao.CHAMP_DIFFUSE));
		camp.setDateAFaireLe(rs.getDate(CampagneActionDao.CHAMP_DATE_A_FAIRE_LE));
		camp.setDateFaitLe(rs.getDate(CampagneActionDao.CHAMP_DATE_FAIT_LE));
		camp.setCommentaire(rs.getString(CampagneActionDao.CHAMP_COMMENTAIRE));
		camp.setIdAgentRealisation(rs.getInt(CampagneActionDao.CHAMP_ID_AGENT_REALISATION));
		camp.setIdCampagneEAE(rs.getInt(CampagneActionDao.CHAMP_ID_CAMPAGNE_EAE));

		return camp;
	}
}
