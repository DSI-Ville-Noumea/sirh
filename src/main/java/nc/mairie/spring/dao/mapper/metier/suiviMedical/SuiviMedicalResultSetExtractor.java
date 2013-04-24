package nc.mairie.spring.dao.mapper.metier.suiviMedical;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.domain.metier.suiviMedical.SuiviMedical;

import org.springframework.jdbc.core.ResultSetExtractor;

public class SuiviMedicalResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		SuiviMedical sm = new SuiviMedical();
		sm.setIdSuiviMed(rs.getInt(SuiviMedicalDao.CHAMP_ID_SUIVI_MED));
		sm.setIdAgent(rs.getInt(SuiviMedicalDao.CHAMP_ID_AGENT));
		sm.setNomatr(rs.getInt(SuiviMedicalDao.CHAMP_NOMATR));
		sm.setAgent(rs.getString(SuiviMedicalDao.CHAMP_AGENT));
		sm.setStatut(rs.getString(SuiviMedicalDao.CHAMP_STATUT));
		sm.setIdServi(rs.getString(SuiviMedicalDao.CHAMP_ID_SERVI));
		sm.setDateDerniereVisite(rs.getDate(SuiviMedicalDao.CHAMP_DATE_DERNIERE_VISITE));
		sm.setDatePrevisionVisite(rs.getDate(SuiviMedicalDao.CHAMP_DATE_PREVISION_VISITE));
		sm.setIdMotifVM(rs.getInt(SuiviMedicalDao.CHAMP_ID_MOTIF_VM));
		sm.setNbVisitesRatees(rs.getInt(SuiviMedicalDao.CHAMP_NB_VISITES_RATEES));
		sm.setIdMedecin(rs.getInt(SuiviMedicalDao.CHAMP_ID_MEDECIN));
		sm.setDateProchaineVisite(rs.getDate(SuiviMedicalDao.CHAMP_DATE_PROCHAINE_VISITE));
		sm.setHeureProchaineVisite(rs.getString(SuiviMedicalDao.CHAMP_HEURE_PROCHAINE_VISITE));
		sm.setEtat(rs.getString(SuiviMedicalDao.CHAMP_ETAT));
		sm.setMois(rs.getInt(SuiviMedicalDao.CHAMP_MOIS));
		sm.setAnnee(rs.getInt(SuiviMedicalDao.CHAMP_ANNEE));
		sm.setRelance(rs.getInt(SuiviMedicalDao.CHAMP_RELANCE));
		return sm;
	}
}
