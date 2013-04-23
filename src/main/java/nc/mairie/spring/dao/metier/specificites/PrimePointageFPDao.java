package nc.mairie.spring.dao.metier.specificites;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageFPDao implements PrimePointageFPDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE_FP";

	public static final String CHAMP_ID_PRIME_POINTAGE = "ID_PRIME_POINTAGE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public PrimePointageFPDao() {

	}

	@Override
	public void creerPrimePointageFP(Integer idPrimePointage, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_PRIME_POINTAGE + "," + CHAMP_ID_FICHE_POSTE + ") " + "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idPrimePointage, idFichePoste });
	}

	@Override
	public void supprimerPrimePointageFP(Integer idFichePoste, Integer idPrimePointage) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_POSTE + "=? and " + CHAMP_ID_PRIME_POINTAGE + "=?";
		jdbcTemplate.update(sql, new Object[] { idFichePoste, idPrimePointage });
	}

}
