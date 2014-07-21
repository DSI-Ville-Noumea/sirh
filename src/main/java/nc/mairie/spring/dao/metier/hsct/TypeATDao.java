package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.TypeAT;
import nc.mairie.spring.dao.SirhDao;

public class TypeATDao extends SirhDao implements TypeATDaoInterface {

	public static final String CHAMP_DESC_TYPE_AT = "DESC_TYPE_AT";

	public TypeATDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_AT";
		super.CHAMP_ID = "ID_TYPE_AT";
	}

	@Override
	public void creerTypeAT(String description) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_DESC_TYPE_AT + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { description.toUpperCase() });
	}

	@Override
	public void supprimerTypeAT(Integer idTypeAT) throws Exception {
		super.supprimerObject(idTypeAT);
	}

	@Override
	public ArrayList<TypeAT> listerTypeAT() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_DESC_TYPE_AT;

		ArrayList<TypeAT> liste = new ArrayList<TypeAT>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeAT a = new TypeAT();
			a.setIdTypeAt((Integer) row.get(CHAMP_ID));
			a.setDescTypeAt((String) row.get(CHAMP_DESC_TYPE_AT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public TypeAT chercherTypeAT(Integer idTypeAT) throws Exception {
		return super.chercherObject(TypeAT.class, idTypeAT);
	}
}
