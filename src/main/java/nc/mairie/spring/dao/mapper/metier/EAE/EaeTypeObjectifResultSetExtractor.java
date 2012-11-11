package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeTypeObjectifDao;
import nc.mairie.spring.domain.metier.EAE.EaeTypeObjectif;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeTypeObjectifResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeTypeObjectif typeRes = new EaeTypeObjectif();
		typeRes.setIdEaeTypeObjectif(rs.getInt(EaeTypeObjectifDao.CHAMP_ID_EAE_TYPE_OBJECTIF));
		typeRes.setLibelleTypeObjectif(rs.getString(EaeTypeObjectifDao.CHAMP_LIBELLE_TYPE_OBJECTIF));

		return typeRes;
	}
}
