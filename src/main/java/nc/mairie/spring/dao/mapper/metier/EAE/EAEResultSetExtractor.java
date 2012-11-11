package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.domain.metier.EAE.EAE;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EAEResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EAE eae = new EAE();
		eae.setIdEAE(rs.getInt(EAEDao.CHAMP_ID_EAE));
		eae.setIdCampagneEAE(rs.getInt(EAEDao.CHAMP_ID_CAMPAGNE_EAE));
		eae.setEtat(rs.getString(EAEDao.CHAMP_ETAT));
		eae.setCap(rs.getBoolean(EAEDao.CHAMP_CAP));
		eae.setDocumentAttache(rs.getBoolean(EAEDao.CHAMP_DOC_ATTACHE));
		eae.setDateCreation(rs.getDate(EAEDao.CHAMP_DATE_CREATION));
		eae.setDateFin(rs.getDate(EAEDao.CHAMP_DATE_FIN));
		eae.setDateEntretien(rs.getDate(EAEDao.CHAMP_DATE_ENTRETIEN));
		eae.setDureeEntretien(rs.getInt(EAEDao.CHAMP_DUREE_ENTRETIEN));
		eae.setDateFinalise(rs.getDate(EAEDao.CHAMP_DATE_FINALISE));
		eae.setDateControle(rs.getDate(EAEDao.CHAMP_DATE_CONTROLE));
		eae.setHeureControle(rs.getString(EAEDao.CHAMP_HEURE_CONTROLE));
		eae.setUserControle(rs.getString(EAEDao.CHAMP_USER_CONTROLE));
		eae.setIdDelegataire(rs.getInt(EAEDao.CHAMP_ID_DELEGATAIRE));

		return eae;
	}
}
