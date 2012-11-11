package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeEvalueResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeEvalue eaeEval = new EaeEvalue();
		eaeEval.setIdEaeEvalue(rs.getInt(EaeEvalueDao.CHAMP_ID_EAE_EVALUE));
		eaeEval.setIdEae(rs.getInt(EaeEvalueDao.CHAMP_ID_EAE));
		eaeEval.setIdAgent(rs.getInt(EaeEvalueDao.CHAMP_ID_AGENT));
		eaeEval.setDateEntreeService(rs.getDate(EaeEvalueDao.CHAMP_DATE_ENTREE_SERVICE));
		eaeEval.setDateEntreeCollectivite(rs.getDate(EaeEvalueDao.CHAMP_DATE_ENTREE_COLLECTIVITE));
		eaeEval.setDateEntreeFonctionnaire(rs.getDate(EaeEvalueDao.CHAMP_DATE_ENTREE_FONCTIONNAIRE));
		eaeEval.setDateEntreeAdministration(rs.getDate(EaeEvalueDao.CHAMP_DATE_ENTREE_ADMINISTRATION));
		eaeEval.setStatut(rs.getString(EaeEvalueDao.CHAMP_STATUT));
		eaeEval.setAncienneteEchelonJours(rs.getInt(EaeEvalueDao.CHAMP_ANCIENNETE_ECHELON_JOURS));
		eaeEval.setCadre(rs.getString(EaeEvalueDao.CHAMP_CADRE));
		eaeEval.setCategorie(rs.getString(EaeEvalueDao.CHAMP_CATEGORIE));
		eaeEval.setClassification(rs.getString(EaeEvalueDao.CHAMP_CLASSIFICATION));
		eaeEval.setGrade(rs.getString(EaeEvalueDao.CHAMP_GRADE));
		eaeEval.setEchelon(rs.getString(EaeEvalueDao.CHAMP_ECHELON));
		eaeEval.setDateEffetAvct(rs.getDate(EaeEvalueDao.CHAMP_DATE_EFFET_AVCT));
		eaeEval.setNouvGrade(rs.getString(EaeEvalueDao.CHAMP_NOUV_GRADE));
		eaeEval.setNouvEchelon(rs.getString(EaeEvalueDao.CHAMP_NOUV_ECHELON));
		eaeEval.setPosition(rs.getString(EaeEvalueDao.CHAMP_POSITION));
		eaeEval.setTypeAvct(rs.getString(EaeEvalueDao.CHAMP_TYPE_AVCT));

		return eaeEval;
	}
}
