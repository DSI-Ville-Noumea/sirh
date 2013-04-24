package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeFormationDao;
import nc.mairie.spring.domain.metier.EAE.EaeFormation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeFormationResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeFormation formation = new EaeFormation();
		formation.setIdEaeFormation(rs.getInt(EaeFormationDao.CHAMP_ID_EAE_FORMATION));
		formation.setIdEAE(rs.getInt(EaeFormationDao.CHAMP_ID_EAE));
		formation.setAnneeFormation(rs.getInt(EaeFormationDao.CHAMP_ANNEE_FORMATION));
		formation.setDureeFormation(rs.getString(EaeFormationDao.CHAMP_DUREE_FORMATION));
		formation.setLibelleFormation(rs.getString(EaeFormationDao.CHAMP_LIBELLE_FORMATION));

		return formation;
	}
}
