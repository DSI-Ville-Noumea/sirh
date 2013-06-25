package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.JourFerieDao;
import nc.mairie.spring.domain.metier.parametrage.JourFerie;

import org.springframework.jdbc.core.ResultSetExtractor;

public class JourFerieResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		JourFerie jour = new JourFerie();
		jour.setIdJourFerie(rs.getInt(JourFerieDao.CHAMP_ID_JOUR_FERIE));
		jour.setIdTypeJour(rs.getInt(JourFerieDao.CHAMP_ID_TYPE_JOUR_FERIE));
		jour.setDateJour(rs.getDate(JourFerieDao.CHAMP_DATE_JOUR));
		jour.setDescription(rs.getString(JourFerieDao.CHAMP_DESCRIPTION));

		return jour;
	}
}
