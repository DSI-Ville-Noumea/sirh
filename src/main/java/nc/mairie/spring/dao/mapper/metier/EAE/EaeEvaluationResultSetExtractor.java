package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeEvaluationResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeEvaluation eval = new EaeEvaluation();
		eval.setIdEaeEvaluation(rs.getInt(EaeEvaluationDao.CHAMP_ID_EAE_EVALUATION));
		eval.setIdEae(rs.getInt(EaeEvaluationDao.CHAMP_ID_EAE));
		eval.setIdNiveau(rs.getInt(EaeEvaluationDao.CHAMP_ID_NIVEAU));
		eval.setNoteAnnee(rs.getDouble(EaeEvaluationDao.CHAMP_NOTE_ANNEE));
		eval.setNoteAnneeN1(rs.getDouble(EaeEvaluationDao.CHAMP_NOTE_ANNEE_N1));
		eval.setNoteAnneeN2(rs.getDouble(EaeEvaluationDao.CHAMP_NOTE_ANNEE_N2));
		eval.setNoteAnneeN3(rs.getDouble(EaeEvaluationDao.CHAMP_NOTE_ANNEE_N3));
		eval.setAvisRevalorisation(rs.getInt(EaeEvaluationDao.CHAMP_AVIS_REVALORISATION));
		eval.setPropositionAvancement(rs.getString(EaeEvaluationDao.CHAMP_PROPOSITION_AVANCEMENT));
		eval.setAvisChangementClasse(rs.getInt(EaeEvaluationDao.CHAMP_AVIS_CHANGEMENT_CLASSE));
		eval.setAvis_shd(rs.getString(EaeEvaluationDao.CHAMP_AVIS_SHD));

		return eval;
	}
}
