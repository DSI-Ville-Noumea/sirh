package nc.mairie.spring.dao.mapper.metier.diplome;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.diplome.PermisAgentDao;
import nc.mairie.spring.domain.metier.diplome.PermisAgent;

import org.springframework.jdbc.core.ResultSetExtractor;

public class PermisAgentResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		PermisAgent permis = new PermisAgent();
		permis.setIdPermisAgent(rs.getInt(PermisAgentDao.CHAMP_ID_PERMIS_AGENT));
		permis.setIdPermis(rs.getInt(PermisAgentDao.CHAMP_ID_PERMIS));
		permis.setIdAgent(rs.getInt(PermisAgentDao.CHAMP_ID_AGENT));
		permis.setDureePermis(rs.getInt(PermisAgentDao.CHAMP_DUREE_PERMIS));
		permis.setUniteDuree(rs.getString(PermisAgentDao.CHAMP_UNITE_DUREE));
		permis.setDateObtention(rs.getDate(PermisAgentDao.CHAMP_DATE_OBTENTION));

		return permis;
	}
}
