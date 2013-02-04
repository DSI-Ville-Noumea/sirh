package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.CorpsCap;

import org.springframework.jdbc.core.JdbcTemplate;

public class CorpsCapDao implements CorpsCapDaoInterface {

	public static final String NOM_TABLE = "SIRH.CORPS_CAP";

	public static final String CHAMP_CDGENG = "CDGENG";
	public static final String CHAMP_ID_CAP = "ID_CAP";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CorpsCapDao() {

	}

	@Override
	public ArrayList<CorpsCap> listerCorpsCap() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<CorpsCap> listeCorpsCap = new ArrayList<CorpsCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			CorpsCap corpsCap = new CorpsCap();
			corpsCap.setCodeSpgeng((String) row.get(CHAMP_CDGENG));
			corpsCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			listeCorpsCap.add(corpsCap);
		}

		return listeCorpsCap;
	}

	@Override
	public void creerCorpsCap(String codeSpgeng, Integer idCap) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CDGENG + "," + CHAMP_ID_CAP + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { codeSpgeng, idCap });
	}

	@Override
	public void supprimerCorpsCapParCap(Integer idCap) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=? ";
		jdbcTemplate.update(sql, new Object[] { idCap });
	}

	@Override
	public ArrayList<CorpsCap> listerCorpsCapParCap(Integer idCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=?";

		ArrayList<CorpsCap> listeCorpsCap = new ArrayList<CorpsCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCap });
		for (Map row : rows) {
			CorpsCap corpsCap = new CorpsCap();
			corpsCap.setCodeSpgeng((String) row.get(CHAMP_CDGENG));
			corpsCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			listeCorpsCap.add(corpsCap);
		}

		return listeCorpsCap;
	}
}
