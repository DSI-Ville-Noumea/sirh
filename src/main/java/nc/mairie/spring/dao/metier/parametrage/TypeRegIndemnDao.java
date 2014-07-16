package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.spring.dao.SirhDao;

public class TypeRegIndemnDao extends SirhDao implements TypeRegIndemnDaoInterface {

	public static final String CHAMP_LIB_TYPE_REG_INDEMN = "LIB_TYPE_REG_INDEMN";

	public TypeRegIndemnDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_REG_INDEMN";
		super.CHAMP_ID = "ID_TYPE_REG_INDEMN";
	}

	@Override
	public ArrayList<TypeRegIndemn> listerTypeRegIndemn() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TYPE_REG_INDEMN;

		ArrayList<TypeRegIndemn> listeTitreFormation = new ArrayList<TypeRegIndemn>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeRegIndemn titre = new TypeRegIndemn();
			titre.setIdTypeRegIndemn((Integer) row.get(CHAMP_ID));
			titre.setLibTypeRegIndemn((String) row.get(CHAMP_LIB_TYPE_REG_INDEMN));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TypeRegIndemn chercherTypeRegIndemn(Integer idTypeRegIndemn) throws Exception {
		return super.chercherObject(TypeRegIndemn.class, idTypeRegIndemn);
	}

	@Override
	public void creerTypeRegIndemn(String libelleTypeRegIndemn) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TYPE_REG_INDEMN + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTypeRegIndemn.toUpperCase() });
	}

	@Override
	public void supprimerTypeRegIndemn(Integer idTypeRegIndemn) throws Exception {
		super.supprimerObject(idTypeRegIndemn);
	}
}
