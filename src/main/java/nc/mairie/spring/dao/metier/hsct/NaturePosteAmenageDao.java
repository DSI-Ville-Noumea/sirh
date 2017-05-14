package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.NaturePosteAmenage;
import nc.mairie.spring.dao.utils.SirhDao;

public class NaturePosteAmenageDao extends SirhDao implements NaturePosteAmenageDaoInterface {

	public static final String LIB_NATURE_POSTE_AMENAGE = "LIB_NATURE_POSTE_AMENAGE";

	public NaturePosteAmenageDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_NATURE_POSTE_AMENAGE";
		super.CHAMP_ID = "ID_NATURE_POSTE_AMENAGE";
	}
		
	@Override
	public NaturePosteAmenage chercherNaturePosteAmenagee(Integer idNaturePosteAmenagee) throws Exception {
		return super.chercherObject(NaturePosteAmenage.class, idNaturePosteAmenagee);
	}

	@Override
	public ArrayList<NaturePosteAmenage> listerNaturePosteAmenagee() throws Exception {
		
		String sql = "select * from " + NOM_TABLE + " order by " + LIB_NATURE_POSTE_AMENAGE;

		ArrayList<NaturePosteAmenage> liste = new ArrayList<NaturePosteAmenage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NaturePosteAmenage a = new NaturePosteAmenage();
			a.setIdNaturePosteAmenage((Integer) row.get(CHAMP_ID));
			a.setLibNaturePosteAmenage((String) row.get(LIB_NATURE_POSTE_AMENAGE));
			liste.add(a);
		}

		return liste;
	}

}
