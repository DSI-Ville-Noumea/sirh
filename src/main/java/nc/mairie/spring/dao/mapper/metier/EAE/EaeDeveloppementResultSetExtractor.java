package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeDeveloppementDao;
import nc.mairie.spring.domain.metier.EAE.EaeDeveloppement;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeDeveloppementResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeDeveloppement dev = new EaeDeveloppement();
		dev.setIdEaeDeveloppement(rs.getInt(EaeDeveloppementDao.CHAMP_ID_EAE_DEVELOPPEMENT));
		dev.setIdEaeEvolution(rs.getInt(EaeDeveloppementDao.CHAMP_ID_EAE_EVOLUTION));
		dev.setLibelleDeveloppement(rs.getString(EaeDeveloppementDao.CHAMP_LIBELLE));
		dev.setEcheanceDeveloppement(rs.getDate(EaeDeveloppementDao.CHAMP_ECHEANCE));
		dev.setPriorisation(rs.getInt(EaeDeveloppementDao.CHAMP_PRIORISATION));
		dev.setTypeDeveloppement(rs.getString(EaeDeveloppementDao.CHAMP_TYPE_DEVELOPPEMENT));
		return dev;
	}
}
