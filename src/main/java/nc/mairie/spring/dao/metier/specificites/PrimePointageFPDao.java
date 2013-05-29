package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.specificites.PrimePointageFP;

import org.springframework.jdbc.core.JdbcTemplate;

public class PrimePointageFPDao implements PrimePointageFPDaoInterface {

	public static final String NOM_TABLE = "SIRH.PRIME_POINTAGE_FP";

	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";
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
	public void creerPrimePointageFP(Integer numRubr, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_POSTE + "," + CHAMP_NUM_RUBRIQUE + ") " + "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idFichePoste, numRubr });
	}

	@Override
	public void supprimerPrimePointageFP(Integer idFichePoste, Integer numRubr) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_POSTE + "=? and " + CHAMP_NUM_RUBRIQUE + "=?";
		jdbcTemplate.update(sql, new Object[] { idFichePoste, numRubr });
	}

	@Override
	public ArrayList<PrimePointageFP> listerPrimePointageFP(Integer idFichePoste) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? WITH UR";

		ArrayList<PrimePointageFP> listePrime = new ArrayList<PrimePointageFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			PrimePointageFP prime = new PrimePointageFP();
			prime.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			prime.setNumRubrique((Integer) row.get(CHAMP_NUM_RUBRIQUE));
			listePrime.add(prime);
		}

		return listePrime;
	}

}
