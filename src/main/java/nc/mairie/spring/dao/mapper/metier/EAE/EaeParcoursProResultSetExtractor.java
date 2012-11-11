package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeParcoursProDao;
import nc.mairie.spring.domain.metier.EAE.EaeParcoursPro;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeParcoursProResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeParcoursPro eaeParcoursPro = new EaeParcoursPro();
		eaeParcoursPro.setIdEaeParcoursPro(rs.getInt(EaeParcoursProDao.CHAMP_ID_EAE_PARCOURS_PRO));
		eaeParcoursPro.setIdEAE(rs.getInt(EaeParcoursProDao.CHAMP_ID_EAE));
		eaeParcoursPro.setDateDebut(rs.getDate(EaeParcoursProDao.CHAMP_DATE_DEBUT));
		eaeParcoursPro.setDateFin(rs.getDate(EaeParcoursProDao.CHAMP_DATE_FIN));
		eaeParcoursPro.setLibelleParcoursPro(rs.getString(EaeParcoursProDao.CHAMP_LIBELLE_PARCOURS_PRO));

		return eaeParcoursPro;
	}
}
