package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.TypeDroit;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeDroitDao extends SirhDao implements TypeDroitDaoInterface {

	public static final String CHAMP_LIB_TYPE_DROIT = "LIB_TYPE_DROIT";

	public TypeDroitDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_DROIT";
		super.CHAMP_ID = "ID_TYPE_DROIT";
	}

	@Override
	public ArrayList<TypeDroit> listerTypeDroit() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TYPE_DROIT;

		ArrayList<TypeDroit> liste = new ArrayList<TypeDroit>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeDroit a = new TypeDroit();
			a.setIdTypeDroit((Integer) row.get(CHAMP_ID));
			a.setLibTypeDroit((String) row.get(CHAMP_LIB_TYPE_DROIT));
			liste.add(a);
		}

		return liste;
	}
}
