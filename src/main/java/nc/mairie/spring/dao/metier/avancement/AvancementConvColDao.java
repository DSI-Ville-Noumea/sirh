package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AvancementConvColDao extends SirhDao implements AvancementConvColDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DIRECTION_SERVICE = "DIRECTION_SERVICE";
	public static final String CHAMP_SECTION_SERVICE = "SECTION_SERVICE";
	public static final String CHAMP_GRADE = "GRADE";
	public static final String CHAMP_LIB_GRADE = "LIB_GRADE";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_NUM_ARRETE = "NUM_ARRETE";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_ETAT = "ETAT";
	public static final String CHAMP_DATE_EMBAUCHE = "DATE_EMBAUCHE";
	public static final String CHAMP_CARRIERE_SIMU = "CARRIERE_SIMU";
	public static final String CHAMP_MONTANT_PRIME_1200 = "MONTANT_PRIME_1200";
	public static final String CHAMP_CODE_PA = "CODE_PA";

	public AvancementConvColDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVCT_CONV_COL";
		super.CHAMP_ID = "ID_AVCT";
	}

	@Override
	public ArrayList<AvancementConvCol> listerAvancementConvColAvecAnnee(Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? ";

		ArrayList<AvancementConvCol> liste = new ArrayList<AvancementConvCol>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee });
		for (Map<String, Object> row : rows) {
			AvancementConvCol a = new AvancementConvCol();
			a.setIdAvct((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDirectionService((String) row.get(CHAMP_DIRECTION_SERVICE));
			a.setSectionService((String) row.get(CHAMP_SECTION_SERVICE));
			a.setGrade((String) row.get(CHAMP_GRADE));
			a.setLibGrade((String) row.get(CHAMP_LIB_GRADE));
			a.setAnnee((Integer) row.get(CHAMP_ANNEE));
			a.setNumArrete((String) row.get(CHAMP_NUM_ARRETE));
			a.setDateArrete((Date) row.get(CHAMP_DATE_ARRETE));
			a.setEtat((String) row.get(CHAMP_ETAT));
			a.setDateEmbauche((Date) row.get(CHAMP_DATE_EMBAUCHE));
			a.setCarriereSimu((String) row.get(CHAMP_CARRIERE_SIMU));
			a.setMontantPrime1200((String) row.get(CHAMP_MONTANT_PRIME_1200));
			a.setCodePa((String) row.get(CHAMP_CODE_PA));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerAvancementConvColTravailAvecAnnee(Integer annee) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? and " + CHAMP_ETAT + "=?";
		jdbcTemplate.update(sql, new Object[] { annee, "T" });
	}

	@Override
	public void modifierAvancementConvCol(Integer idAvct, Integer idAgent, Integer annee, String etat,
			String numArrete, Date dateArrete, Date dateEmbauche, String grade, String libGrade,
			String directionService, String sectionService, String carriereSimu, String montantPrime1200, String codePa) {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_DIRECTION_SERVICE + "=?,"
				+ CHAMP_SECTION_SERVICE + "=?," + CHAMP_GRADE + "=?," + CHAMP_LIB_GRADE + "=?," + CHAMP_ANNEE + "=?,"
				+ CHAMP_NUM_ARRETE + "=?," + CHAMP_DATE_ARRETE + "=?," + CHAMP_ETAT + "=?," + CHAMP_DATE_EMBAUCHE
				+ "=?," + CHAMP_CARRIERE_SIMU + "=?," + CHAMP_MONTANT_PRIME_1200 + "=?," + CHAMP_CODE_PA + "=? where "
				+ CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { idAgent, directionService, sectionService, grade, libGrade, annee,
				numArrete, dateArrete, etat, dateEmbauche, carriereSimu, montantPrime1200, codePa, idAvct });
	}

	@Override
	public void creerAvancementConvCol(Integer idAgent, Integer annee, String etat, String numArrete, Date dateArrete,
			Date dateEmbauche, String grade, String libGrade, String directionService, String sectionService,
			String carriereSimu, String montantPrime1200, String codePa) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_DIRECTION_SERVICE + ","
				+ CHAMP_SECTION_SERVICE + "," + CHAMP_GRADE + "," + CHAMP_LIB_GRADE + "," + CHAMP_ANNEE + ","
				+ CHAMP_NUM_ARRETE + "," + CHAMP_DATE_ARRETE + "," + CHAMP_ETAT + "," + CHAMP_DATE_EMBAUCHE + ","
				+ CHAMP_CARRIERE_SIMU + "," + CHAMP_MONTANT_PRIME_1200 + "," + CHAMP_CODE_PA
				+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, directionService, sectionService, grade, libGrade, annee,
				numArrete, dateArrete, etat, dateEmbauche, carriereSimu, montantPrime1200, codePa });
	}

	@Override
	public AvancementConvCol chercherAvancementConvColAvecAnneeEtAgent(Integer annee, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + " = ? and " + CHAMP_ID_AGENT + "=?";
		AvancementConvCol cadre = (AvancementConvCol) jdbcTemplate.queryForObject(sql, new Object[] { annee, idAgent },
				new BeanPropertyRowMapper<AvancementConvCol>(AvancementConvCol.class));
		return cadre;
	}
}
