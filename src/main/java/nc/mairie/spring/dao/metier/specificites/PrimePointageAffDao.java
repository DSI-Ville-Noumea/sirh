package nc.mairie.spring.dao.metier.specificites;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageAffDao implements PrimePointageAffDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE_AFF";

	public static final String CHAMP_ID_PRIME_POINTAGE = "ID_PRIME_POINTAGE";
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
	public void creerPrimePointageAff(Integer idPrimePointage, Integer idAffectation) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_PRIME_POINTAGE + "," + CHAMP_ID_AFFECTATION + ") " + "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idPrimePointage, idAffectation });
	}

	@Override
	public void supprimerPrimePointageAff(Integer idAffectation, Integer idPrimePointage) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? and " + CHAMP_ID_PRIME_POINTAGE + "=?";
		jdbcTemplate.update(sql, new Object[] { idAffectation, idPrimePointage });
	}
}
