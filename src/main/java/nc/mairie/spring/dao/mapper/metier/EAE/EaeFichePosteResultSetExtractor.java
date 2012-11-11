package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeFichePosteResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeFichePoste eaeFDP = new EaeFichePoste();
		eaeFDP.setIdEaeFichePoste(rs.getInt(EaeFichePosteDao.CHAMP_ID_EAE_FICHE_POSTE));
		eaeFDP.setIdEae(rs.getInt(EaeFichePosteDao.CHAMP_ID_EAE));
		eaeFDP.setIdSHD(rs.getInt(EaeFichePosteDao.CHAMP_ID_SHD));
		eaeFDP.setPrimaire(rs.getBoolean(EaeFichePosteDao.CHAMP_PRIMAIRE));
		eaeFDP.setDirectionServ(rs.getString(EaeFichePosteDao.CHAMP_DIRECTION_SERVICE));
		eaeFDP.setServiceServ(rs.getString(EaeFichePosteDao.CHAMP_SERVICE));
		eaeFDP.setSectionServ(rs.getString(EaeFichePosteDao.CHAMP_SECTION_SERVICE));
		eaeFDP.setEmploi(rs.getString(EaeFichePosteDao.CHAMP_EMPLOI));
		eaeFDP.setFonction(rs.getString(EaeFichePosteDao.CHAMP_FONCTION));
		eaeFDP.setDateEntreeFonction(rs.getDate(EaeFichePosteDao.CHAMP_DATE_ENTREE_FONCTION));
		eaeFDP.setGradePoste(rs.getString(EaeFichePosteDao.CHAMP_GRADE_POSTE));
		eaeFDP.setLocalisation(rs.getString(EaeFichePosteDao.CHAMP_LOCALISATION));
		eaeFDP.setMission(rs.getString(EaeFichePosteDao.CHAMP_MISSIONS));
		eaeFDP.setFonctionResponsable(rs.getString(EaeFichePosteDao.CHAMP_FONCTION_RESP));
		eaeFDP.setDateEntreeServiceResponsable(rs.getDate(EaeFichePosteDao.CHAMP_DATE_ENTREE_SERVICE_RESP));
		eaeFDP.setDateEntreeCollectiviteResponsable(rs.getDate(EaeFichePosteDao.CHAMP_DATE_ENTREE_COLLECT_RESP));
		eaeFDP.setDateEntreeFonctionResponsable(rs.getDate(EaeFichePosteDao.CHAMP_DATE_ENTREE_FONCTION_RESP));
		eaeFDP.setCodeService(rs.getString(EaeFichePosteDao.CHAMP_CODE_SERVICE));

		return eaeFDP;
	}
}
