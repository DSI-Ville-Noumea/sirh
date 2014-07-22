package nc.mairie.spring.dao.metier.hsct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class VisiteMedicaleDao extends SirhDao implements VisiteMedicaleDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_MEDECIN = "ID_MEDECIN";
	public static final String CHAMP_ID_RECOMMANDATION = "ID_RECOMMANDATION";
	public static final String CHAMP_DATE_DERNIERE_VISITE = "DATE_DERNIERE_VISITE";
	public static final String CHAMP_DUREE_VALIDITE = "DUREE_VALIDITE";
	public static final String CHAMP_APTE = "APTE";
	public static final String CHAMP_ID_MOTIF_VM = "ID_MOTIF_VM";
	public static final String CHAMP_ID_SUIVI_MED = "ID_SUIVI_MED";

	public VisiteMedicaleDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "VISITE_MEDICALE";
		super.CHAMP_ID = "ID_VISITE";
	}

	@Override
	public VisiteMedicale chercherVisiteMedicale(Integer idVM) throws Exception {
		return super.chercherObject(VisiteMedicale.class, idVM);
	}

	@Override
	public void creerVisiteMedicale(Integer idAgent, Integer idMedecin, Integer idRecommandation,
			Date dateDerniereVisite, Integer dureeValidite, Integer apte, Integer idMotifVm, Integer idSuiviMed)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_MEDECIN + ","
				+ CHAMP_ID_RECOMMANDATION + "," + CHAMP_DATE_DERNIERE_VISITE + "," + CHAMP_DUREE_VALIDITE + ","
				+ CHAMP_APTE + "," + CHAMP_ID_MOTIF_VM + "," + CHAMP_ID_SUIVI_MED + ") " + "VALUES (?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idMedecin, idRecommandation, dateDerniereVisite,
				dureeValidite, apte, idMotifVm, idSuiviMed });
	}

	@Override
	public void modifierVisiteMedicale(Integer idVM, Integer idAgent, Integer idMedecin, Integer idRecommandation,
			Date dateDerniereVisite, Integer dureeValidite, Integer apte, Integer idMotifVm, Integer idSuiviMed)
			throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_ID_MEDECIN + "=?,"
				+ CHAMP_ID_RECOMMANDATION + "=?," + CHAMP_DATE_DERNIERE_VISITE + "=?," + CHAMP_DUREE_VALIDITE + "=?,"
				+ CHAMP_APTE + "=?," + CHAMP_ID_MOTIF_VM + "=?," + CHAMP_ID_SUIVI_MED + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idMedecin, idRecommandation, dateDerniereVisite,
				dureeValidite, apte, idMotifVm, idSuiviMed, idVM });
	}

	@Override
	public void supprimerVisiteMedicale(Integer idVM) throws Exception {
		super.supprimerObject(idVM);
	}

	@Override
	public ArrayList<VisiteMedicale> listerVisiteMedicalePourSMCas2(Integer moisChoisi, Integer anneeChoisi) {
		String sql = "select * from " + NOM_TABLE + " where month(ADD_MONTHS(" + CHAMP_DATE_DERNIERE_VISITE + ", "
				+ CHAMP_DUREE_VALIDITE + ")) = ? and year(ADD_MONTHS(" + CHAMP_DATE_DERNIERE_VISITE + ", "
				+ CHAMP_DUREE_VALIDITE + ")) =? ";

		ArrayList<VisiteMedicale> liste = new ArrayList<VisiteMedicale>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { moisChoisi, anneeChoisi });
		for (Map<String, Object> row : rows) {
			VisiteMedicale a = new VisiteMedicale();
			a.setIdVisite((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			a.setIdRecommandation((Integer) row.get(CHAMP_ID_RECOMMANDATION));
			a.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			a.setDureeValidite((Integer) row.get(CHAMP_DUREE_VALIDITE));
			a.setApte((Integer) row.get(CHAMP_APTE));
			a.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			BigDecimal idSuiviMed = (BigDecimal) row.get(CHAMP_ID_SUIVI_MED);
			a.setIdSuiviMed(idSuiviMed != null ? idSuiviMed.intValue() : null);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public VisiteMedicale chercherVisiteMedicaleCriteres(Integer idAgent, Integer idMedecin, Integer idMotif)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_ID_MEDECIN
				+ " =? and " + CHAMP_ID_MOTIF_VM + "=? and " + CHAMP_DUREE_VALIDITE + "=0 and " + CHAMP_APTE
				+ " is null and " + CHAMP_ID_RECOMMANDATION + " is null";
		VisiteMedicale vm = (VisiteMedicale) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idMedecin,
				idMotif }, new BeanPropertyRowMapper<VisiteMedicale>(VisiteMedicale.class));
		return vm;
	}

	@Override
	public ArrayList<VisiteMedicale> listerVisiteMedicalePourSMCas1(Integer idMotifAgent, Integer idMotifService,
			Integer idMedecin) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where (" + CHAMP_ID_MOTIF_VM + " = ? or " + CHAMP_ID_MOTIF_VM
				+ "=?) and " + CHAMP_DUREE_VALIDITE + "=0 and " + CHAMP_ID_MEDECIN + "=? and "
				+ CHAMP_ID_RECOMMANDATION + " is null and APTE is null";

		ArrayList<VisiteMedicale> liste = new ArrayList<VisiteMedicale>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idMotifAgent, idMotifService,
				idMedecin });
		for (Map<String, Object> row : rows) {
			VisiteMedicale a = new VisiteMedicale();
			a.setIdVisite((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			a.setIdRecommandation((Integer) row.get(CHAMP_ID_RECOMMANDATION));
			a.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			a.setDureeValidite((Integer) row.get(CHAMP_DUREE_VALIDITE));
			a.setApte((Integer) row.get(CHAMP_APTE));
			a.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			BigDecimal idSuiviMed = (BigDecimal) row.get(CHAMP_ID_SUIVI_MED);
			a.setIdSuiviMed(idSuiviMed != null ? idSuiviMed.intValue() : null);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public VisiteMedicale chercherVisiteMedicaleLieeSM(Integer idSuiviMed, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_ID_SUIVI_MED
				+ " =?";
		VisiteMedicale vm = (VisiteMedicale) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idSuiviMed },
				new BeanPropertyRowMapper<VisiteMedicale>(VisiteMedicale.class));
		return vm;
	}

	@Override
	public ArrayList<VisiteMedicale> listerVisiteMedicaleAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by "
				+ CHAMP_DATE_DERNIERE_VISITE + " desc";

		ArrayList<VisiteMedicale> liste = new ArrayList<VisiteMedicale>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			VisiteMedicale a = new VisiteMedicale();
			a.setIdVisite((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			a.setIdRecommandation((Integer) row.get(CHAMP_ID_RECOMMANDATION));
			a.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			a.setDureeValidite((Integer) row.get(CHAMP_DUREE_VALIDITE));
			a.setApte((Integer) row.get(CHAMP_APTE));
			a.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			BigDecimal idSuiviMed = (BigDecimal) row.get(CHAMP_ID_SUIVI_MED);
			a.setIdSuiviMed(idSuiviMed != null ? idSuiviMed.intValue() : null);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<VisiteMedicale> listerVisiteMedicaleAvecMedecin(Integer idMedecin) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_MEDECIN + "=? ";

		ArrayList<VisiteMedicale> liste = new ArrayList<VisiteMedicale>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idMedecin });
		for (Map<String, Object> row : rows) {
			VisiteMedicale a = new VisiteMedicale();
			a.setIdVisite((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			a.setIdRecommandation((Integer) row.get(CHAMP_ID_RECOMMANDATION));
			a.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			a.setDureeValidite((Integer) row.get(CHAMP_DUREE_VALIDITE));
			a.setApte((Integer) row.get(CHAMP_APTE));
			a.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			BigDecimal idSuiviMed = (BigDecimal) row.get(CHAMP_ID_SUIVI_MED);
			a.setIdSuiviMed(idSuiviMed != null ? idSuiviMed.intValue() : null);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<VisiteMedicale> listerVisiteMedicaleAvecRecommandation(Integer idRecommandation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_RECOMMANDATION + "=? ";

		ArrayList<VisiteMedicale> liste = new ArrayList<VisiteMedicale>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idRecommandation });
		for (Map<String, Object> row : rows) {
			VisiteMedicale a = new VisiteMedicale();
			a.setIdVisite((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			a.setIdRecommandation((Integer) row.get(CHAMP_ID_RECOMMANDATION));
			a.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			a.setDureeValidite((Integer) row.get(CHAMP_DUREE_VALIDITE));
			a.setApte((Integer) row.get(CHAMP_APTE));
			a.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			BigDecimal idSuiviMed = (BigDecimal) row.get(CHAMP_ID_SUIVI_MED);
			a.setIdSuiviMed(idSuiviMed != null ? idSuiviMed.intValue() : null);
			liste.add(a);
		}

		return liste;
	}
}
