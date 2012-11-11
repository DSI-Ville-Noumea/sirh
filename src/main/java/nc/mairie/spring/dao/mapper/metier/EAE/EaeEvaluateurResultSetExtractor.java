package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeEvaluateurResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeEvaluateur eval = new EaeEvaluateur();
		eval.setIdEaeEvaluateur(rs.getInt(EaeEvaluateurDao.CHAMP_ID_EAE_EVALUATEUR));
		eval.setIdEae(rs.getInt(EaeEvaluateurDao.CHAMP_ID_EAE));
		eval.setIdAgent(rs.getInt(EaeEvaluateurDao.CHAMP_ID_AGENT));
		eval.setFonction(rs.getString(EaeEvaluateurDao.CHAMP_FONCTION));
		eval.setDateEntreeService(rs.getDate(EaeEvaluateurDao.CHAMP_DATE_ENTREE_SERVICE));
		eval.setDateEntreeCollectivite(rs.getDate(EaeEvaluateurDao.CHAMP_DATE_ENTREE_COLLECTIVITE));
		eval.setDateEntreeFonction(rs.getDate(EaeEvaluateurDao.CHAMP_DATE_ENTREE_FONCTION));

		return eval;
	}
}
