package nc.mairie.spring.dao.metier.specificites;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.specificites.PrimePointage;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageDao implements PrimePointageDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE";

	public static final String CHAMP_ID_PRIME_POINTAGE = "ID_PRIME_POINTAGE";
	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public PrimePointageDao() {

	}

	@Override
	public ArrayList<PrimePointage> listerPrimePointageAvecFP(Integer idFDP) {
		String sql = "select p.* from " + NOM_TABLE + " p inner join SIRH.PRIME_POINTAGE_FP fp on fp.id_prime_pointage = p."
				+ CHAMP_ID_PRIME_POINTAGE + " where fp.ID_FICHE_POSTE=?";

		ArrayList<PrimePointage> listePrimePointage = new ArrayList<PrimePointage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFDP });
		for (Map row : rows) {
			PrimePointage primePointage = new PrimePointage();
			primePointage.setIdPrimePointage((Integer) row.get(CHAMP_ID_PRIME_POINTAGE));
			BigDecimal rubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			primePointage.setIdRubrique(rubr.intValue());
			listePrimePointage.add(primePointage);
		}

		return listePrimePointage;
	}

	@Override
	public ArrayList<PrimePointage> listerPrimePointageAvecAFF(Integer idAff) {
		String sql = "select p.* from " + NOM_TABLE + " p inner join SIRH.PRIME_POINTAGE_AFF aff on aff.id_prime_pointage = p."
				+ CHAMP_ID_PRIME_POINTAGE + " where aff.ID_AFFECTATION=?";

		ArrayList<PrimePointage> listePrimePointage = new ArrayList<PrimePointage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAff });
		for (Map row : rows) {
			PrimePointage primePointage = new PrimePointage();
			primePointage.setIdPrimePointage((Integer) row.get(CHAMP_ID_PRIME_POINTAGE));
			BigDecimal rubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			primePointage.setIdRubrique(rubr.intValue());
			listePrimePointage.add(primePointage);
		}

		return listePrimePointage;
	}

	@Override
	public Integer creerPrimePointage(Integer idRubrique) {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NUM_RUBRIQUE + ") " + "VALUES (?)";

		jdbcTemplate.update(sql, new Object[] { idRubrique });

		String sqlClePrimaire = "select max(" + CHAMP_ID_PRIME_POINTAGE + ") from " + NOM_TABLE;
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);
		return id;
	}

	@Override
	public void supprimerPrimePointage(Integer idPrimePointage) {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_PRIME_POINTAGE + "=?";
		jdbcTemplate.update(sql, new Object[] { idPrimePointage });
	}
}
