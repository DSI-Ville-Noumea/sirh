package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeResultatDao;
import nc.mairie.spring.domain.metier.EAE.EaeResultat;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeResultatResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeResultat eaeRes = new EaeResultat();
		eaeRes.setIdEaeResultat(rs.getInt(EaeResultatDao.CHAMP_ID_EAE_RESULTAT));
		eaeRes.setIdEae(rs.getInt(EaeResultatDao.CHAMP_ID_EAE));
		eaeRes.setIdTypeObjectif(rs.getInt(EaeResultatDao.CHAMP_ID_EAE_TYPE_OBJECTIF));
		eaeRes.setObjectif(rs.getString(EaeResultatDao.CHAMP_OBJECTIF));
		eaeRes.setResultat(rs.getString(EaeResultatDao.CHAMP_RESULTAT));
		eaeRes.setCommentaire(rs.getString(EaeResultatDao.CHAMP_COMMENTAIRE));

		return eaeRes;
	}
}
