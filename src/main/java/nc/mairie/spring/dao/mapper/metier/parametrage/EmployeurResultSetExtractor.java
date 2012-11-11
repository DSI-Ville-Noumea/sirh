package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.EmployeurDao;
import nc.mairie.spring.domain.metier.parametrage.Employeur;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EmployeurResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		Employeur employeur = new Employeur();
		employeur.setIdEmployeur(rs.getInt(EmployeurDao.CHAMP_ID_EMPLOYEUR));
		employeur.setLibEmployeur(rs.getString(EmployeurDao.CHAMP_LIB_EMPLOYEUR));
		employeur.setTitreEmployeur(rs.getString(EmployeurDao.CHAMP_TITRE_EMPLOYEUR));

		return employeur;
	}
}
