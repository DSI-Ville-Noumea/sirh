package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.AccidentTravail;
import nc.mairie.spring.dao.SirhDao;

public class AccidentTravailDao extends SirhDao implements AccidentTravailDaoInterface {

	public static final String CHAMP_ID_TYPE_AT = "ID_TYPE_AT";
	public static final String CHAMP_ID_SIEGE = "ID_SIEGE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_AT = "DATE_AT";
	public static final String CHAMP_DATE_AT_INITIAL = "DATE_AT_INITIAL";
	public static final String CHAMP_NB_JOURS_ITT = "NB_JOURS_ITT";

	public AccidentTravailDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ACCIDENT_TRAVAIL";
		super.CHAMP_ID = "ID_AT";
	}

	@Override
	public ArrayList<AccidentTravail> listerAccidentTravailAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_AT
				+ " desc";

		ArrayList<AccidentTravail> liste = new ArrayList<AccidentTravail>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			AccidentTravail a = new AccidentTravail();
			a.setIdAt((Integer) row.get(CHAMP_ID));
			a.setIdTypeAt((Integer) row.get(CHAMP_ID_TYPE_AT));
			a.setIdSiege((Integer) row.get(CHAMP_ID_SIEGE));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateAt((Date) row.get(CHAMP_DATE_AT));
			a.setDateAtInitial((Date) row.get(CHAMP_DATE_AT_INITIAL));
			a.setNbJoursItt((Integer) row.get(CHAMP_NB_JOURS_ITT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<AccidentTravail> listerAccidentTravailAvecTypeAT(Integer idTypeAT) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_AT + "=? ";

		ArrayList<AccidentTravail> liste = new ArrayList<AccidentTravail>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeAT });
		for (Map<String, Object> row : rows) {
			AccidentTravail a = new AccidentTravail();
			a.setIdAt((Integer) row.get(CHAMP_ID));
			a.setIdTypeAt((Integer) row.get(CHAMP_ID_TYPE_AT));
			a.setIdSiege((Integer) row.get(CHAMP_ID_SIEGE));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateAt((Date) row.get(CHAMP_DATE_AT));
			a.setDateAtInitial((Date) row.get(CHAMP_DATE_AT_INITIAL));
			a.setNbJoursItt((Integer) row.get(CHAMP_NB_JOURS_ITT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<AccidentTravail> listerAccidentTravailAvecSiegeLesion(Integer idSiege) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_SIEGE + "=? ";

		ArrayList<AccidentTravail> liste = new ArrayList<AccidentTravail>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idSiege });
		for (Map<String, Object> row : rows) {
			AccidentTravail a = new AccidentTravail();
			a.setIdAt((Integer) row.get(CHAMP_ID));
			a.setIdTypeAt((Integer) row.get(CHAMP_ID_TYPE_AT));
			a.setIdSiege((Integer) row.get(CHAMP_ID_SIEGE));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateAt((Date) row.get(CHAMP_DATE_AT));
			a.setDateAtInitial((Date) row.get(CHAMP_DATE_AT_INITIAL));
			a.setNbJoursItt((Integer) row.get(CHAMP_NB_JOURS_ITT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public AccidentTravail chercherAccidentTravail(Integer idAT) throws Exception {
		return super.chercherObject(AccidentTravail.class, idAT);
	}

	@Override
	public void creerAccidentTravail(Integer idTypeAT, Integer idSiege, Integer idAgent, Date dateAT,
			Date dateInitiale, Integer nbJoursITT) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TYPE_AT + "," + CHAMP_ID_SIEGE + "," + CHAMP_ID_AGENT
				+ "," + CHAMP_DATE_AT + "," + CHAMP_DATE_AT_INITIAL + "," + CHAMP_NB_JOURS_ITT + ") "
				+ "VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idTypeAT, idSiege, idAgent, dateAT, dateInitiale, nbJoursITT });
	}

	@Override
	public void modifierAccidentTravail(Integer idAT, Integer idTypeAT, Integer idSiege, Integer idAgent, Date dateAT,
			Date dateInitiale, Integer nbJoursITT) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_AT + "=?," + CHAMP_ID_SIEGE + "=?,"
				+ CHAMP_ID_AGENT + "=?," + CHAMP_DATE_AT + "=?," + CHAMP_DATE_AT_INITIAL + "=?," + CHAMP_NB_JOURS_ITT
				+ "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idTypeAT, idSiege, idAgent, dateAT, dateInitiale, nbJoursITT, idAT });
	}

	@Override
	public void supprimerAccidentTravail(Integer idAT) throws Exception {
		super.supprimerObject(idAT);
	}
}
