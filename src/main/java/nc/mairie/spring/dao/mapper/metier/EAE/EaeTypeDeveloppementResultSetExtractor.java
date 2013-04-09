package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeDeveloppementDao;
import nc.mairie.spring.dao.metier.EAE.EaeTypeDeveloppementDao;
import nc.mairie.spring.domain.metier.EAE.EaeTypeDeveloppement;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeTypeDeveloppementResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeTypeDeveloppement dev = new EaeTypeDeveloppement();
		dev.setIdEaeTypeDeveloppement(rs.getInt(EaeTypeDeveloppementDao.CHAMP_ID_EAE_TYPE_DEVELOPPEMENT));
		dev.setLibelleTypeDeveloppement(rs.getString(EaeTypeDeveloppementDao.CHAMP_LIBELLE_TYPE_DEVELOPPEMENT));
		return dev;
	}
}
