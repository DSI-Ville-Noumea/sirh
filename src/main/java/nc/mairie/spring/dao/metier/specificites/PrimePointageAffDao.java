package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageAffDao implements PrimePointageAffDaoInterface {

	public static final String NOM_TABLE = "PRIME_POINTAGE_AFF";

	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";
	public static final String CHAMP_ID_AFFECTATION = "ID_AFFECTATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public PrimePointageAffDao() {

	}

	@Override
	public void creerPrimePointageAff(Integer numRubrique, Integer idAffectation) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NUM_RUBRIQUE + "," + CHAMP_ID_AFFECTATION + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { numRubrique, idAffectation });
	}

	@Override
	public void supprimerPrimePointageAff(Integer idAffectation, Integer numRubrique) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? and " + CHAMP_NUM_RUBRIQUE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idAffectation, numRubrique });
	}

	@Override
	public ArrayList<PrimePointageAff> listerPrimePointageAff(Integer idAffectation) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AFFECTATION + "=? order by "
				+ CHAMP_NUM_RUBRIQUE + " WITH UR";

		ArrayList<PrimePointageAff> listePrime = new ArrayList<PrimePointageAff>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAffectation });
		for (Map<String, Object> row : rows) {
			PrimePointageAff prime = new PrimePointageAff();
			prime.setIdAffectation((Integer) row.get(CHAMP_ID_AFFECTATION));
			prime.setNumRubrique((Integer) row.get(CHAMP_NUM_RUBRIQUE));
			listePrime.add(prime);
		}

		return listePrime;
	}

	public void supprimerToutesPrimePointageAff(String idAffectation) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? ";
		jdbcTemplate.update(sql, new Object[] { idAffectation });
	}

}
