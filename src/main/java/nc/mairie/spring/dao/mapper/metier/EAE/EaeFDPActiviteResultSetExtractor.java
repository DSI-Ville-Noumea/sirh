package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeFDPActiviteDao;
import nc.mairie.spring.domain.metier.EAE.EaeFDPActivite;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeFDPActiviteResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeFDPActivite eaeActi = new EaeFDPActivite();
		eaeActi.setIdEaeFDPActivite(rs.getInt(EaeFDPActiviteDao.CHAMP_ID_EAE_FDP_ACTIVITE));
		eaeActi.setIdEaeFDP(rs.getInt(EaeFDPActiviteDao.CHAMP_ID_EAE_FICHE_POSTE));
		eaeActi.setLibActivite(rs.getString(EaeFDPActiviteDao.CHAMP_LIBELLE_ACTIVITE));

		return eaeActi;
	}
}
