package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.MaladiePro;
import nc.mairie.spring.dao.utils.SirhDao;

public class MaladieProDao extends SirhDao implements MaladieProDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_RECHUTE = "RECHUTE";
	public static final String CHAMP_DATE_DECLARATION = "DATE_DECLARATION";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_NB_JOURS_ITT = "NB_JOURS_ITT";
	public static final String CHAMP_AVIS_COMMISSION = "AVIS_COMMISSION";
	public static final String CHAMP_ID_TYPE_MP = "ID_TYPE_MP";
	public static final String CHAMP_DATE_TRANSMISSION_CAFAT = "DATE_TRANSMISSION_CAFAT";
	public static final String CHAMP_DATE_DECISION_CAFAT = "DATE_DECISION_CAFAT";
	public static final String CHAMP_TAUX_PRIS_CHARGE_CAFAT = "TAUX_PRIS_CHARGE_CAFAT";
	public static final String CHAMP_DATE_TRANSMISSION_APTITUDE = "DATE_TRANSMISSION_APTITUDE";

	public MaladieProDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "MALADIE_PRO";
		super.CHAMP_ID = "ID_MALADIE_PRO";
	}

	@Override
	public ArrayList<MaladiePro> listerMaladieProAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_DECLARATION
				+ " desc";

		ArrayList<MaladiePro> liste = new ArrayList<MaladiePro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			MaladiePro a = new MaladiePro();
			a.setIdMaladiePro((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			Integer rechute = (Integer) row.get(CHAMP_RECHUTE);
			a.setRechute(rechute == 1 ? true : false);
			a.setDateDeclaration((Date) row.get(CHAMP_DATE_DECLARATION));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			a.setNbJoursItt((Integer) row.get(CHAMP_NB_JOURS_ITT));
			a.setAvisCommission((Integer) row.get(CHAMP_AVIS_COMMISSION));
			a.setIdTypeMp((Integer) row.get(CHAMP_ID_TYPE_MP));
			a.setDateTransmissionCafat((Date) row.get(CHAMP_DATE_TRANSMISSION_CAFAT));
			a.setDateDecisionCafat((Date) row.get(CHAMP_DATE_DECISION_CAFAT));
			a.setTauxPrisEnChargeCafat((Integer) row.get(CHAMP_TAUX_PRIS_CHARGE_CAFAT));
			a.setDateTransmissionAptitude((Date) row.get(CHAMP_DATE_TRANSMISSION_APTITUDE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<MaladiePro> listerMaladieProAvecTypeMP(Integer idTypeMp) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_MP + "=? ";

		ArrayList<MaladiePro> liste = new ArrayList<MaladiePro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeMp });
		for (Map<String, Object> row : rows) {
			MaladiePro a = new MaladiePro();
			a.setIdMaladiePro((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			Integer rechute = (Integer) row.get(CHAMP_RECHUTE);
			a.setRechute(rechute == 1 ? true : false);
			a.setDateDeclaration((Date) row.get(CHAMP_DATE_DECLARATION));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			a.setNbJoursItt((Integer) row.get(CHAMP_NB_JOURS_ITT));
			a.setAvisCommission((Integer) row.get(CHAMP_AVIS_COMMISSION));
			a.setIdTypeMp((Integer) row.get(CHAMP_ID_TYPE_MP));
			a.setDateTransmissionCafat((Date) row.get(CHAMP_DATE_TRANSMISSION_CAFAT));
			a.setDateDecisionCafat((Date) row.get(CHAMP_DATE_DECISION_CAFAT));
			a.setTauxPrisEnChargeCafat((Integer) row.get(CHAMP_TAUX_PRIS_CHARGE_CAFAT));
			a.setDateTransmissionAptitude((Date) row.get(CHAMP_DATE_TRANSMISSION_APTITUDE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public MaladiePro chercherMaladiePro(Integer idMP) throws Exception {
		return super.chercherObject(MaladiePro.class, idMP);
	}
	
	@Override
	public void creerMaladiePro(Integer idAgent, Boolean rechute, Date dateDeclaration,
			Date dateFin, Integer nbJoursITT, Integer avisCommission, Integer idTypeMP,
			Date dateTransmissionCafat, Date dateDecisionCafat, Integer tauxPrisEnChargeCafat,
			Date dateTransmissionAptitude) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_RECHUTE + "," + CHAMP_DATE_DECLARATION
				+ "," + CHAMP_DATE_FIN + "," + CHAMP_NB_JOURS_ITT + "," + CHAMP_AVIS_COMMISSION + "," + CHAMP_ID_TYPE_MP
				 + "," + CHAMP_DATE_TRANSMISSION_CAFAT + "," + CHAMP_DATE_DECISION_CAFAT + "," + CHAMP_TAUX_PRIS_CHARGE_CAFAT
				 + "," + CHAMP_DATE_TRANSMISSION_APTITUDE + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, rechute, dateDeclaration, dateFin, nbJoursITT, avisCommission, idTypeMP, 
				dateTransmissionCafat, dateDecisionCafat, tauxPrisEnChargeCafat, dateTransmissionAptitude });
	}

	@Override
	public void modifierMaladiePro(Integer idMP, Integer idAgent, Boolean rechute, Date dateDeclaration,
			Date dateFin, Integer nbJoursITT, Integer avisCommission, Integer idTypeMP,
			Date dateTransmissionCafat, Date dateDecisionCafat, Integer tauxPrisEnChargeCafat,
			Date dateTransmissionAptitude) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_RECHUTE + "=?,"
				+ CHAMP_DATE_DECLARATION + "=?," + CHAMP_DATE_FIN + "=?," + CHAMP_NB_JOURS_ITT + "=?," + CHAMP_AVIS_COMMISSION + "=?,"
				+ CHAMP_ID_TYPE_MP + "=?," + CHAMP_DATE_TRANSMISSION_CAFAT + "=?," + CHAMP_DATE_DECISION_CAFAT  + "=?,"
				+ CHAMP_TAUX_PRIS_CHARGE_CAFAT + "=?," + CHAMP_DATE_TRANSMISSION_APTITUDE + "=?"
				+ " where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idAgent, rechute, dateDeclaration, dateFin, nbJoursITT, avisCommission, idTypeMP, 
				dateTransmissionCafat, dateDecisionCafat, tauxPrisEnChargeCafat, dateTransmissionAptitude, idMP });
	}

	@Override
	public void supprimerMaladiePro(Integer idMP) throws Exception {
		super.supprimerObject(idMP);
	}
}
