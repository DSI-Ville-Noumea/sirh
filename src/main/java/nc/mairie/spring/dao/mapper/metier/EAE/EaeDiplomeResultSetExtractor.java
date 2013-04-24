package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeDiplomeDao;
import nc.mairie.spring.domain.metier.EAE.EaeDiplome;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeDiplomeResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeDiplome eaeDiplome = new EaeDiplome();
		eaeDiplome.setIdEaeDiplome(rs.getInt(EaeDiplomeDao.CHAMP_ID_EAE_DIPLOME));
		eaeDiplome.setIdEae(rs.getInt(EaeDiplomeDao.CHAMP_ID_EAE));
		eaeDiplome.setLibelleDiplome(rs.getString(EaeDiplomeDao.CHAMP_LIBELLE_DIPLOME));

		return eaeDiplome;
	}
}
