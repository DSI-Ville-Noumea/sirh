package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.Deliberation;
import nc.mairie.spring.dao.SirhDao;

public class DeliberationDao extends SirhDao implements DeliberationDaoInterface {

	public static final String CHAMP_CODE_DELIBERATION = "CODE_DELIBERATION";
	public static final String CHAMP_LIB_DELIBERATION = "LIB_DELIBERATION";
	public static final String CHAMP_TYPE_DELIBERATION = "TYPE_DELIBERATION";
	public static final String CHAMP_TEXTE_CAP = "TEXTE_CAP";

	public DeliberationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_DELIBERATION";
		super.CHAMP_ID = "ID_DELIBERATION";
	}

	@Override
	public ArrayList<Deliberation> listerDeliberation() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_TYPE_DELIBERATION;

		ArrayList<Deliberation> listeDeliberation = new ArrayList<Deliberation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Deliberation delib = new Deliberation();
			delib.setIdDeliberation((Integer) row.get(CHAMP_ID));
			delib.setCodeDeliberation((String) row.get(CHAMP_CODE_DELIBERATION));
			delib.setLibDeliberation((String) row.get(CHAMP_LIB_DELIBERATION));
			delib.setTypeDeliberation((String) row.get(CHAMP_TYPE_DELIBERATION));
			delib.setTexteCAP((String) row.get(CHAMP_TEXTE_CAP));

			listeDeliberation.add(delib);
		}

		return listeDeliberation;
	}

	@Override
	public void creerDeliberation(String codeDeliberation, String libelleDeliberation, String typeDeliberation,
			String texteCAPDeliberation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_DELIBERATION + "," + CHAMP_LIB_DELIBERATION + ","
				+ CHAMP_TYPE_DELIBERATION + "," + CHAMP_TEXTE_CAP + ") " + "VALUES (?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { codeDeliberation.toUpperCase(), libelleDeliberation.toUpperCase(),
				typeDeliberation.toUpperCase(), texteCAPDeliberation });

	}

	@Override
	public void supprimerDeliberation(Integer idDeliberation) throws Exception {
		super.supprimerObject(idDeliberation);
	}

	@Override
	public ArrayList<Deliberation> listerDeliberationCommunale() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_DELIBERATION + " =?";

		ArrayList<Deliberation> listeDeliberation = new ArrayList<Deliberation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { "COMMUNAL" });
		for (Map<String, Object> row : rows) {
			Deliberation delib = new Deliberation();
			delib.setIdDeliberation((Integer) row.get(CHAMP_ID));
			delib.setCodeDeliberation((String) row.get(CHAMP_CODE_DELIBERATION));
			delib.setLibDeliberation((String) row.get(CHAMP_LIB_DELIBERATION));
			delib.setTypeDeliberation((String) row.get(CHAMP_TYPE_DELIBERATION));
			delib.setTexteCAP((String) row.get(CHAMP_TEXTE_CAP));

			listeDeliberation.add(delib);
		}

		return listeDeliberation;
	}

	@Override
	public ArrayList<Deliberation> listerDeliberationTerritoriale() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYPE_DELIBERATION + " =?";

		ArrayList<Deliberation> listeDeliberation = new ArrayList<Deliberation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { "TERRITORIAL" });
		for (Map<String, Object> row : rows) {
			Deliberation delib = new Deliberation();
			delib.setIdDeliberation((Integer) row.get(CHAMP_ID));
			delib.setCodeDeliberation((String) row.get(CHAMP_CODE_DELIBERATION));
			delib.setLibDeliberation((String) row.get(CHAMP_LIB_DELIBERATION));
			delib.setTypeDeliberation((String) row.get(CHAMP_TYPE_DELIBERATION));
			delib.setTexteCAP((String) row.get(CHAMP_TEXTE_CAP));

			listeDeliberation.add(delib);
		}

		return listeDeliberation;
	}

	@Override
	public void modifierDeliberation(String codeDeliberation, String libelleDeliberation, String typeDeliberation,
			String texteCAPDeliberation) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_DELIBERATION + "=?," + CHAMP_TYPE_DELIBERATION + "=?,"
				+ CHAMP_TEXTE_CAP + "=? where " + CHAMP_CODE_DELIBERATION + "=?";
		jdbcTemplate.update(sql, new Object[] { libelleDeliberation.toUpperCase(), typeDeliberation.toUpperCase(),
				texteCAPDeliberation, codeDeliberation.toUpperCase() });
	}
}
