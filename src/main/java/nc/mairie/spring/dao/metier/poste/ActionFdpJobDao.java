package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.ActionFdpJob;
import nc.mairie.spring.dao.utils.SirhDao;

public class ActionFdpJobDao extends SirhDao implements ActionFdpJobDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_ID_NEW_SERVICE_ADS = "ID_NEW_SERVICE_ADS";
	public static final String CHAMP_TYPE_ACTION = "TYPE_ACTION";
	public static final String CHAMP_STATUT = "STATUT";
	public static final String CHAMP_DATE_SUBMISSION = "DATE_SUBMISSION";
	public static final String CHAMP_DATE_STATUT = "DATE_STATUT";

	public ActionFdpJobDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ACTION_FDP_JOB";
		super.CHAMP_ID = "ID_ACTION_FDP_JOB";
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobSuppression() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='SUPPRESSION' order by "
				+ CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}

	private ActionFdpJob mappRow(Map<String, Object> row) {
		ActionFdpJob a = new ActionFdpJob();
		a.setIdActionFdpJob((Integer) row.get(CHAMP_ID));
		a.setDateStatut((Date) row.get(CHAMP_DATE_STATUT));
		a.setDateSubmission((Date) row.get(CHAMP_DATE_SUBMISSION));
		a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
		a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
		a.setIdNewServiceAds((Integer) row.get(CHAMP_ID_NEW_SERVICE_ADS));
		a.setStatut((String) row.get(CHAMP_STATUT));
		a.setTypeAction((String) row.get(CHAMP_TYPE_ACTION));
		return a;
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobSuppressionErreur() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='SUPPRESSION' and "
				+ CHAMP_STATUT + " != 'OK' order by " + CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobActivation() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='ACTIVATION' order by "
				+ CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobActivationErreur() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='ACTIVATION' and " + CHAMP_STATUT
				+ " != 'OK' order by " + CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobDuplication() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='DUPLICATION' order by "
				+ CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}

	@Override
	public List<ActionFdpJob> listerActionFdpJobDuplicationErreur() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_ACTION + "='DUPLICATION' and "
				+ CHAMP_STATUT + " not like 'OK%' order by " + CHAMP_ID + " desc ";

		ArrayList<ActionFdpJob> liste = new ArrayList<ActionFdpJob>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {

			liste.add(mappRow(row));
		}

		return liste;
	}
}
