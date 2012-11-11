package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;

import org.springframework.jdbc.core.ResultSetExtractor;

public class CampagneEAEResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		CampagneEAE camp = new CampagneEAE();
		camp.setIdCampagneEAE(rs.getInt(CampagneEAEDao.CHAMP_ID_CAMPAGNE_EAE));
		camp.setAnnee(rs.getInt(CampagneEAEDao.CHAMP_ANNEE));
		camp.setDateDebut(rs.getDate(CampagneEAEDao.CHAMP_DATE_DEBUT));
		camp.setDateFin(rs.getDate(CampagneEAEDao.CHAMP_DATE_FIN));
		camp.setDateOuvertureKiosque(rs.getDate(CampagneEAEDao.CHAMP_DATE_OUVERTURE_KIOSQUE));
		camp.setDateFermetureKiosque(rs.getDate(CampagneEAEDao.CHAMP_DATE_FERMETURE_KIOSQUE));
		camp.setCommentaire(rs.getString(CampagneEAEDao.CHAMP_COMMENTAIRE));

		return camp;
	}
}
