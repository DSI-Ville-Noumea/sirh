package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeFDPCompetenceDao;
import nc.mairie.spring.domain.metier.EAE.EaeFDPCompetence;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeFDPCompetenceResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeFDPCompetence eaeComp = new EaeFDPCompetence();
		eaeComp.setIdEaeFDPCompetence(rs.getInt(EaeFDPCompetenceDao.CHAMP_ID_EAE_FDP_COMPETENCE));
		eaeComp.setIdEaeFDP(rs.getInt(EaeFDPCompetenceDao.CHAMP_ID_EAE_FICHE_POSTE));
		eaeComp.setTypeCompetence(rs.getString(EaeFDPCompetenceDao.CHAMP_TYPE_COMPETENCE));
		eaeComp.setLibCompetence(rs.getString(EaeFDPCompetenceDao.CHAMP_LIBELLE_COMPETENCE));

		return eaeComp;
	}
}
