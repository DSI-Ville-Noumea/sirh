package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.TypeInaptitude;
import nc.mairie.spring.dao.SirhDao;

public class TypeInaptitudeDao extends SirhDao implements TypeInaptitudeDaoInterface {

	public static final String CHAMP_DESC_TYPE_INAPTITUDE = "DESC_TYPE_INAPTITUDE";

	public TypeInaptitudeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_INAPTITUDE";
		super.CHAMP_ID = "ID_TYPE_INAPTITUDE";
	}

	@Override
	public void creerTypeInaptitude(String description) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_DESC_TYPE_INAPTITUDE + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { description.toUpperCase() });
	}

	@Override
	public void supprimerTypeInaptitude(Integer idTypeInaptitude) throws Exception {
		super.supprimerObject(idTypeInaptitude);
	}

	@Override
	public ArrayList<TypeInaptitude> listerTypeInaptitude() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_DESC_TYPE_INAPTITUDE;

		ArrayList<TypeInaptitude> liste = new ArrayList<TypeInaptitude>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeInaptitude a = new TypeInaptitude();
			a.setIdTypeInaptitude((Integer) row.get(CHAMP_ID));
			a.setDescTypeInaptitude((String) row.get(CHAMP_DESC_TYPE_INAPTITUDE));
			liste.add(a);
		}

		return liste;
	}
}
