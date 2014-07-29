package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.RepresentantCap;
import nc.mairie.spring.dao.utils.SirhDao;

public class RepresentantCapDao extends SirhDao implements RepresentantCapDaoInterface {

	public static final String CHAMP_ID_REPRESENTANT = "ID_REPRESENTANT";
	public static final String CHAMP_ID_CAP = "ID_CAP";
	public static final String CHAMP_POSITION = "POSITION";

	public RepresentantCapDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "REPRESENTANT_CAP";
	}

	@Override
	public void creerRepresentantCap(Integer idRepresentant, Integer idCap, Integer position) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_REPRESENTANT + "," + CHAMP_ID_CAP + ","
				+ CHAMP_POSITION + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idRepresentant, idCap, position });
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
		for (Map<String, Object> row : rows) {
			RepresentantCap repreCap = new RepresentantCap();
			repreCap.setIdRepresentant((Integer) row.get(CHAMP_ID_REPRESENTANT));
			repreCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			repreCap.setPosition((Integer) row.get(CHAMP_POSITION));
			listeRepresentantCap.add(repreCap);
		}

		return listeRepresentantCap;
	}

	@Override
	public ArrayList<RepresentantCap> listerRepresentantCapParCap(Integer idCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=? order by " + CHAMP_POSITION;

		ArrayList<RepresentantCap> listeRepresentantCap = new ArrayList<RepresentantCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCap });
		for (Map<String, Object> row : rows) {
			RepresentantCap repreCap = new RepresentantCap();
			repreCap.setIdRepresentant((Integer) row.get(CHAMP_ID_REPRESENTANT));
			repreCap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			repreCap.setPosition((Integer) row.get(CHAMP_POSITION));
			listeRepresentantCap.add(repreCap);
		}

		return listeRepresentantCap;
	}
}
