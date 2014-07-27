package nc.mairie.spring.dao.metier.avancement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AvancementContractuelsDao extends SirhDao implements AvancementContractuelsDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_EMBAUCHE = "DATE_EMBAUCHE";
	public static final String CHAMP_NUM_FP = "NUM_FP";
	public static final String CHAMP_PA = "PA";
	public static final String CHAMP_DATE_GRADE = "DATE_GRADE";
	public static final String CHAMP_DATE_PROCHAIN_GRADE = "DATE_PROCHAIN_GRADE";
	public static final String CHAMP_IBAN = "IBAN";
	public static final String CHAMP_INM = "INM";
	public static final String CHAMP_INA = "INA";
	public static final String CHAMP_NOUV_IBAN = "NOUV_IBAN";
	public static final String CHAMP_NOUV_INM = "NOUV_INM";
	public static final String CHAMP_NOUV_INA = "NOUV_INA";
	public static final String CHAMP_ETAT = "ETAT";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_NUM_ARRETE = "NUM_ARRETE";
	public static final String CHAMP_CARRIERE_SIMU = "CARRIERE_SIMU";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_DIRECTION_SERVICE = "DIRECTION_SERVICE";
	public static final String CHAMP_SECTION_SERVICE = "SECTION_SERVICE";
	public static final String CHAMP_CDCADR = "CDCADR";

	public AvancementContractuelsDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVCT_CONTRACTUELS";
		super.CHAMP_ID = "ID_AVCT";
	}

	@Override
	public void supprimerAvancementContractuelsTravailAvecAnnee(Integer annee) {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? and " + CHAMP_ETAT + "=?";
		jdbcTemplate.update(sql, new Object[] { annee, "T" });
	}

	@Override
	public void modifierAvancementContractuels(Integer idAvct, Integer idAgent, Date dateEmbauche, String numFp,
			String pa, Date dateGrade, Date dateProchainGrade, String iban, Integer inm, Integer ina, String nouvIban,
			Integer nouvInm, Integer nouvIna, String etat, Date dateArrete, String numArrete, String carriereSimu,
			Integer annee, String directionService, String sectionService, String codeCadre) {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_DATE_EMBAUCHE + "=?,"
				+ CHAMP_NUM_FP + "=?," + CHAMP_PA + "=?," + CHAMP_DATE_GRADE + "=?," + CHAMP_DATE_PROCHAIN_GRADE
				+ "=?," + CHAMP_IBAN + "=?," + CHAMP_INM + "=?," + CHAMP_INA + "=?," + CHAMP_NOUV_IBAN + "=?,"
				+ CHAMP_NOUV_INM + "=?," + CHAMP_NOUV_INA + "=?," + CHAMP_ETAT + "=?," + CHAMP_DATE_ARRETE + "=?,"
				+ CHAMP_NUM_ARRETE + "=?," + CHAMP_CARRIERE_SIMU + "=?," + CHAMP_ANNEE + "=?,"
				+ CHAMP_DIRECTION_SERVICE + "=?," + CHAMP_SECTION_SERVICE + "=?," + CHAMP_CDCADR + "=? where "
				+ CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { idAgent, dateEmbauche, numFp, pa, dateGrade, dateProchainGrade, iban,
				inm, ina, nouvIban, nouvInm, nouvIna, etat, dateArrete, numArrete, carriereSimu, annee,
				directionService, sectionService, codeCadre, idAvct });
	}

	@Override
	public void creerAvancementContractuels(Integer idAgent, Date dateEmbauche, String numFp, String pa,
			Date dateGrade, Date dateProchainGrade, String iban, Integer inm, Integer ina, String nouvIban,
			Integer nouvInm, Integer nouvIna, String etat, Date dateArrete, String numArrete, String carriereSimu,
			Integer annee, String directionService, String sectionService, String codeCadre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_DATE_EMBAUCHE + ","
				+ CHAMP_NUM_FP + "," + CHAMP_PA + "," + CHAMP_DATE_GRADE + "," + CHAMP_DATE_PROCHAIN_GRADE + ","
				+ CHAMP_IBAN + "," + CHAMP_INM + "," + CHAMP_INA + "," + CHAMP_NOUV_IBAN + "," + CHAMP_NOUV_INM + ","
				+ CHAMP_NOUV_INA + "," + CHAMP_ETAT + "," + CHAMP_DATE_ARRETE + "," + CHAMP_NUM_ARRETE + ","
				+ CHAMP_CARRIERE_SIMU + "," + CHAMP_ANNEE + "," + CHAMP_DIRECTION_SERVICE + "," + CHAMP_SECTION_SERVICE
				+ "," + CHAMP_CDCADR + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, dateEmbauche, numFp, pa, dateGrade, dateProchainGrade, iban,
				inm, ina, nouvIban, nouvInm, nouvIna, etat, dateArrete, numArrete, carriereSimu, annee,
				directionService, sectionService, codeCadre });
	}

	@Override
	public AvancementContractuels chercherAvancementContractuelsAvecAnneeEtAgent(Integer annee, Integer idAgent)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + " = ? and " + CHAMP_ID_AGENT + "=?";
		AvancementContractuels cadre = (AvancementContractuels) jdbcTemplate.queryForObject(sql, new Object[] { annee,
				idAgent }, new BeanPropertyRowMapper<AvancementContractuels>(AvancementContractuels.class));
		return cadre;
	}

	@Override
	public ArrayList<AvancementContractuels> listerAvancementContractuelsAnnee(Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? order by "
				+ CHAMP_DATE_PROCHAIN_GRADE;

		ArrayList<AvancementContractuels> liste = new ArrayList<AvancementContractuels>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee });
		for (Map<String, Object> row : rows) {
			AvancementContractuels a = new AvancementContractuels();
			a.setIdAvct((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateEmbauche((Date) row.get(CHAMP_DATE_EMBAUCHE));
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setPa((String) row.get(CHAMP_PA));
			a.setDateGrade((Date) row.get(CHAMP_DATE_GRADE));
			a.setDateProchainGrade((Date) row.get(CHAMP_DATE_PROCHAIN_GRADE));
			a.setIban((String) row.get(CHAMP_IBAN));
			a.setInm((Integer) row.get(CHAMP_INM));
			a.setIna((Integer) row.get(CHAMP_INA));
			a.setNouvIban((String) row.get(CHAMP_NOUV_IBAN));
			a.setNouvInm((Integer) row.get(CHAMP_NOUV_INM));
			a.setNouvIna((Integer) row.get(CHAMP_NOUV_INA));
			a.setEtat((String) row.get(CHAMP_ETAT));
			a.setDateArrete((Date) row.get(CHAMP_DATE_ARRETE));
			a.setNumArrete((String) row.get(CHAMP_NUM_ARRETE));
			a.setCarriereSimu((String) row.get(CHAMP_CARRIERE_SIMU));
			a.setAnnee((Integer) row.get(CHAMP_ANNEE));
			a.setDirectionService((String) row.get(CHAMP_DIRECTION_SERVICE));
			a.setSectionService((String) row.get(CHAMP_SECTION_SERVICE));
			a.setCdcadr((String) row.get(CHAMP_CDCADR));
			liste.add(a);
		}

		return liste;
	}
}
