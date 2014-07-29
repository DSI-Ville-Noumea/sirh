package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.SiegeLesion;
import nc.mairie.spring.dao.utils.SirhDao;

public class SiegeLesionDao extends SirhDao implements SiegeLesionDaoInterface {

	public static final String CHAMP_DESC_SIEGE = "DESC_SIEGE";

	public SiegeLesionDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_SIEGE_LESION";
		super.CHAMP_ID = "ID_SIEGE";
	}

	@Override
	public void creerSiegeLesion(String description) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_DESC_SIEGE + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { description.toUpperCase() });
	}

	@Override
	public void supprimerSiegeLesion(Integer idSiege) throws Exception {
		super.supprimerObject(idSiege);
	}

	@Override
	public ArrayList<SiegeLesion> listerSiegeLesion() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_DESC_SIEGE;

		ArrayList<SiegeLesion> liste = new ArrayList<SiegeLesion>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			SiegeLesion a = new SiegeLesion();
			a.setIdSiege((Integer) row.get(CHAMP_ID));
			a.setDescSiege((String) row.get(CHAMP_DESC_SIEGE));
			liste.add(a);
		}

		return liste;
	}
}
