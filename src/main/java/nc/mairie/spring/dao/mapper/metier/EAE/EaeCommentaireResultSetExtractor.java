package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeCommentaireDao;
import nc.mairie.spring.domain.metier.EAE.EaeCommentaire;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeCommentaireResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeCommentaire comm = new EaeCommentaire();
		comm.setIdEaeCommenatire(rs.getInt(EaeCommentaireDao.CHAMP_ID_EAE_COMMENTAIRE));
		comm.setCommentaire(rs.getString(EaeCommentaireDao.CHAMP_TEXT));

		return comm;
	}
}
