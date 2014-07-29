package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.EmployeurCap;
import nc.mairie.spring.dao.utils.SirhDao;

public class EmployeurCapDao extends SirhDao implements EmployeurCapDaoInterface {

	public static final String CHAMP_ID_EMPLOYEUR = "ID_EMPLOYEUR";
	public static final String CHAMP_ID_CAP = "ID_CAP";
	public static final String CHAMP_POSITION = "POSITION";

	public EmployeurCapDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "EMPLOYEUR_CAP";
	}

	@Override
	public void creerEmployeurCap(Integer idEmployeur, Integer idCap, Integer position) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EMPLOYEUR + "," + CHAMP_ID_CAP + "," + CHAMP_POSITION
				+ ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idEmployeur, idCap, position });
	}

	@Override
	public void supprimerEmployeurCapParCap(Integer idCap) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=? ";
		jdbcTemplate.update(sql, new Object[] { idCap });
	}

	@Override
	public ArrayList<EmployeurCap> listerEmployeurCapParEmployeur(Integer idEmployeur) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EMPLOYEUR + "=?";

		ArrayList<EmployeurCap> listeEmployeurCap = new ArrayList<EmployeurCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEmployeur });
		for (Map<String, Object> row : rows) {
			EmployeurCap employeurCap = new EmployeurCap();
			employeurCap.setIdEmployeur((Integer) row.get(CHAMP_ID_EMPLOYEUR));
			employeurCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			employeurCap.setPosition((Integer) row.get(CHAMP_POSITION));
			listeEmployeurCap.add(employeurCap);
		}

		return listeEmployeurCap;
	}

	@Override
	public ArrayList<EmployeurCap> listerEmployeurCapParCap(Integer idCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=?";

		ArrayList<EmployeurCap> listeEmployeurCap = new ArrayList<EmployeurCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCap });
		for (Map<String, Object> row : rows) {
			EmployeurCap employeurCap = new EmployeurCap();
			employeurCap.setIdEmployeur((Integer) row.get(CHAMP_ID_EMPLOYEUR));
			employeurCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			employeurCap.setPosition((Integer) row.get(CHAMP_POSITION));
			listeEmployeurCap.add(employeurCap);
		}

		return listeEmployeurCap;
	}
}
