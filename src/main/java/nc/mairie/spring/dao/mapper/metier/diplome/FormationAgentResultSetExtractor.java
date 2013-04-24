package nc.mairie.spring.dao.mapper.metier.diplome;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.diplome.FormationAgentDao;
import nc.mairie.spring.domain.metier.diplome.FormationAgent;

import org.springframework.jdbc.core.ResultSetExtractor;

public class FormationAgentResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		FormationAgent formation = new FormationAgent();
		formation.setIdFormation(rs.getInt(FormationAgentDao.CHAMP_ID_FORMATION));
		formation.setIdTitreFormation(rs.getInt(FormationAgentDao.CHAMP_ID_TITRE_FORMATION));
		formation.setIdCentreFormation(rs.getInt(FormationAgentDao.CHAMP_ID_CENTRE_FORMATION));
		formation.setIdAgent(rs.getInt(FormationAgentDao.CHAMP_ID_AGENT));
		formation.setDureeFormation(rs.getInt(FormationAgentDao.CHAMP_DUREE_FORMATION));
		formation.setAnneeFormation(rs.getInt(FormationAgentDao.CHAMP_ANNEE_FORMATION));

		return formation;
	}
}
