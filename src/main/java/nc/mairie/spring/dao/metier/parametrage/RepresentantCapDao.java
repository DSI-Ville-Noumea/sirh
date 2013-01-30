package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.RepresentantCap;

import org.springframework.jdbc.core.JdbcTemplate;

public class RepresentantCapDao implements RepresentantCapDaoInterface {

	public static final String NOM_TABLE = "SIRH.REPRESENTANT_CAP";

	public static final String CHAMP_ID_REPRESENTANT = "ID_REPRESENTANT";
	public static final String CHAMP_ID_CAP = "ID_CAP";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public RepresentantCapDao() {

	}

	@Override
	public ArrayList<RepresentantCap> listerRepresentantCap() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<RepresentantCap> listeRepresentantCap = new ArrayList<RepresentantCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			RepresentantCap repreCap = new RepresentantCap();
			repreCap.setIdRepresentant((Integer) row.get(CHAMP_ID_REPRESENTANT));
			repreCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			listeRepresentantCap.add(repreCap);
		}

		return listeRepresentantCap;
	}

	@Override
	public void creerRepresentantCap(Integer idRepresentant, Integer idCap) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_REPRESENTANT + "," + CHAMP_ID_CAP + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idRepresentant, idCap });
	}

	@Override
	public void supprimerRepresentantCapParCap(Integer idCap) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=? ";
		jdbcTemplate.update(sql, new Object[] { idCap });
	}

	@Override
	public ArrayList<RepresentantCap> listerRepresentantCapParRepresentant(Integer idRepresentant) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_REPRESENTANT + "=?";

		ArrayList<RepresentantCap> listeRepresentantCap = new ArrayList<RepresentantCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idRepresentant });
		for (Map row : rows) {
			RepresentantCap repreCap = new RepresentantCap();
			repreCap.setIdRepresentant((Integer) row.get(CHAMP_ID_REPRESENTANT));
			repreCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			listeRepresentantCap.add(repreCap);
		}

		return listeRepresentantCap;
	}

	@Override
	public ArrayList<RepresentantCap> listerRepresentantCapParCap(Integer idCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=?";

		ArrayList<RepresentantCap> listeRepresentantCap = new ArrayList<RepresentantCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCap });
		for (Map row : rows) {
			RepresentantCap repreCap = new RepresentantCap();
			repreCap.setIdRepresentant((Integer) row.get(CHAMP_ID_REPRESENTANT));
			repreCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			listeRepresentantCap.add(repreCap);
		}

		return listeRepresentantCap;
	}
}
