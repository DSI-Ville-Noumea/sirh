package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.NatureCredit;

import org.springframework.jdbc.core.JdbcTemplate;

public class NatureCreditDao implements NatureCreditDaoInterface {

	public static final String NOM_TABLE = "P_NATURE_CREDIT";

	public static final String CHAMP_ID_NATURE_CREDIT = "ID_NATURE_CREDIT";
	public static final String CHAMP_LIB_NATURE_CREDIT = "LIB_NATURE_CREDIT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public NatureCreditDao() {

	}

	@Override
	public ArrayList<NatureCredit> listerNatureCredit() {
		String sql = "select *  from " + NOM_TABLE + "  WITH UR";

		ArrayList<NatureCredit> listeNatureCredit = new ArrayList<NatureCredit>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NatureCredit nature = new NatureCredit();
			nature.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			nature.setLibNatureCredit((String) row.get(CHAMP_LIB_NATURE_CREDIT));
			listeNatureCredit.add(nature);
		}

		return listeNatureCredit;
	}

	@Override
	public void creerNatureCredit(String libelleNatureCredit) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_NATURE_CREDIT + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleNatureCredit.toUpperCase() });

	}

	@Override
	public void supprimerNatureCredit(Integer idNatureCredit) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_NATURE_CREDIT + "=?";
		jdbcTemplate.update(sql, new Object[] { idNatureCredit });
	}
}
