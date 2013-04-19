package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.domain.metier.EAE.EaeFinalisation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeFinalisationResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeFinalisation finalisation = new EaeFinalisation();
		finalisation.setIdEaeFinalisation(rs.getInt(EaeFinalisationDao.CHAMP_ID_EAE_FINALISATION));
		finalisation.setIdEae(rs.getInt(EaeFinalisationDao.CHAMP_ID_EAE));
		finalisation.setDateFinalisation(rs.getDate(EaeFinalisationDao.CHAMP_DATE_FINALISATION));
		finalisation.setIdAgent(rs.getInt(EaeFinalisationDao.CHAMP_ID_AGENT));
		finalisation.setIdGedDocument(rs.getString(EaeFinalisationDao.CHAMP_ID_GED_DOCUMENT));
		finalisation.setVersionGedDocument(rs.getString(EaeFinalisationDao.CHAMP_VERSION_GED_DOCUMENT));
		finalisation.setCommentaire(rs.getString(EaeFinalisationDao.CHAMP_COMMENTAIRE));

		return finalisation;
	}
}
