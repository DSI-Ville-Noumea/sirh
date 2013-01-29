package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.Cap;

import org.springframework.jdbc.core.JdbcTemplate;

public class CapDao implements CapDaoInterface {

	public static final String NOM_TABLE = "SIRH.P_CAP";

	public static final String CHAMP_ID_CAP = "ID_CAP";
	public static final String CHAMP_CODE_CAP = "CODE_CAP";
	public static final String CHAMP_REF_CAP = "REF_CAP";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CapDao() {

	}

	@Override
	public ArrayList<Cap> listerCap() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<Cap> listeCap = new ArrayList<Cap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			Cap cap = new Cap();
			cap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			cap.setCodeCap((String) row.get(CHAMP_CODE_CAP));
			cap.setRefCap((String) row.get(CHAMP_REF_CAP));

			listeCap.add(cap);
		}

		return listeCap;
	}

	@Override
	public void creerCap(String codeCap, String refCap) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_CAP + "," + CHAMP_REF_CAP + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { codeCap.toUpperCase(), refCap.toUpperCase() });

	}

	@Override
	public void supprimerCap(Integer idCap) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=?";
		jdbcTemplate.update(sql, new Object[] { idCap });
	}
}
