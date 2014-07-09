package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.metier.parametrage.NatureCredit;

import org.springframework.jdbc.core.JdbcTemplate;

public class NatureCreditDao implements NatureCreditDaoInterface {

	public static final String NOM_TABLE = "P_NATURE_CREDIT";

	public static final String CHAMP_ID_NATURE_CREDIT = "ID_NATURE_CREDIT";
	public static final String CHAMP_LIB_NATURE_CREDIT = "LIB_NATURE_CREDIT";
	public static final String CHAMP_ORDRE_AFF_NATURE_CREDIT = "ORDRE_AFF";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public NatureCreditDao() {

	}

	@Override
	public ArrayList<NatureCredit> listerNatureCreditOrderBy() {
		String sql = "select *  from " + NOM_TABLE + " order by " + CHAMP_ORDRE_AFF_NATURE_CREDIT + " WITH UR";

		ArrayList<NatureCredit> listeNatureCredit = new ArrayList<NatureCredit>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NatureCredit nature = new NatureCredit();
			nature.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			nature.setLibNatureCredit((String) row.get(CHAMP_LIB_NATURE_CREDIT));
			nature.setOrdreAffichage((Integer) row.get(CHAMP_ORDRE_AFF_NATURE_CREDIT));
			listeNatureCredit.add(nature);
		}

		return listeNatureCredit;
	}
}
