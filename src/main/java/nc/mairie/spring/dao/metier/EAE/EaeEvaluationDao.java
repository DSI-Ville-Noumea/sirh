package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeEvaluationRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeEvaluationDao implements EaeEvaluationDaoInterface {

	public static final String NOM_TABLE = "EAE_EVALUATION";

	public static final String NOM_SEQUENCE = "EAE_S_EVALUATION";

	public static final String CHAMP_ID_EAE_EVALUATION = "ID_EAE_EVALUATION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_NIVEAU = "ID_EAE_NIVEAU";
	public static final String CHAMP_NOTE_ANNEE = "NOTE_ANNEE";
	public static final String CHAMP_NOTE_ANNEE_N1 = "NOTE_ANNEE_N1";
	public static final String CHAMP_NOTE_ANNEE_N2 = "NOTE_ANNEE_N2";
	public static final String CHAMP_NOTE_ANNEE_N3 = "NOTE_ANNEE_N3";
	public static final String CHAMP_AVIS_REVALORISATION = "AVIS_REVALORISATION";
	public static final String CHAMP_PROPOSITION_AVANCEMENT = "PROPOSITION_AVANCEMENT";
	public static final String CHAMP_AVIS_CHANGEMENT_CLASSE = "AVIS_CHANGEMENT_CLASSE";
	public static final String CHAMP_AVIS_SHD = "AVIS_SHD";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeEvaluationDao() {

	}

	@Override
	public EaeEvaluation chercherEaeEvaluation(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + " = ? ";
		EaeEvaluation eval = (EaeEvaluation) jdbcTemplate.queryForObject(sql, new Object[] { idEAE }, new EaeEvaluationRowMapper());
		return eval;
	}

	@Override
	public int compterAvisSHDNonDefini(Integer idCampagneEAE, String direction, String section) throws Exception {
		String sql = "";
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ " inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, idCampagneEAE });
		} else if (direction == null && section == null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null";
			total = jdbcTemplate.queryForInt(sql, new Object[] { idCampagneEAE });
		} else {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, idCampagneEAE });
		}
		return total;
	}

	@Override
	public int compterAvisSHDAvct(Integer idCampagneEAE, String direction, String section, String dureeAvct) throws Exception {
		String sql = "";
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null and ev." + CHAMP_PROPOSITION_AVANCEMENT + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, idCampagneEAE, dureeAvct });
		} else if (section == null && direction != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null and ev." + CHAMP_PROPOSITION_AVANCEMENT + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, idCampagneEAE, dureeAvct });
		} else if (direction == null && section == null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null and ev." + CHAMP_PROPOSITION_AVANCEMENT + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { idCampagneEAE, dureeAvct });
		} else {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_REVALORISATION + " is null and ev." + CHAMP_PROPOSITION_AVANCEMENT + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, idCampagneEAE, dureeAvct });
		}
		return total;
	}

	@Override
	public int compterAvisSHDChangementClasse(Integer idCampagneEAE, String direction, String section) throws Exception {
		String sql = "";
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_CHANGEMENT_CLASSE + "=1";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_CHANGEMENT_CLASSE + "=1";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, idCampagneEAE });
		} else if (direction == null && section == null) {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_CHANGEMENT_CLASSE + "=1";
			total = jdbcTemplate.queryForInt(sql, new Object[] { idCampagneEAE });
		} else {
			sql = "select count(ev.ID_EAE) from "
					+ NOM_TABLE
					+ " ev inner join EAE e on e.ID_EAE = ev."
					+ CHAMP_ID_EAE
					+ "  inner join EAE_FICHE_POSTE fp on fp.id_eae=ev.id_eae where fp.DIRECTION_SERVICE =? and fp.SECTION_SERVICE =? and e.ID_CAMPAGNE_EAE =? and ev."
					+ CHAMP_AVIS_CHANGEMENT_CLASSE + "=1";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, idCampagneEAE });
		}
		return total;
	}
}
