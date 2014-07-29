package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeAvantageDao extends SirhDao implements TypeAvantageDaoInterface {

	public static final String CHAMP_LIB_TYPE_AVANTAGE = "LIB_TYPE_AVANTAGE";

	public TypeAvantageDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_AVANTAGE";
		super.CHAMP_ID = "ID_TYPE_AVANTAGE";
	}

	@Override
	public ArrayList<TypeAvantage> listerTypeAvantage() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TYPE_AVANTAGE;

		ArrayList<TypeAvantage> listeTitreFormation = new ArrayList<TypeAvantage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeAvantage titre = new TypeAvantage();
			titre.setIdTypeAvantage((Integer) row.get(CHAMP_ID));
			titre.setLibTypeAvantage((String) row.get(CHAMP_LIB_TYPE_AVANTAGE));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TypeAvantage chercherTypeAvantage(Integer idTypeAvantage) throws Exception {
		return super.chercherObject(TypeAvantage.class, idTypeAvantage);
	}

	@Override
	public void creerTypeAvantage(String libelleTypeAvantage) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TYPE_AVANTAGE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTypeAvantage.toUpperCase() });
	}

	@Override
	public void supprimerTypeAvantage(Integer idTypeAvantage) throws Exception {
		super.supprimerObject(idTypeAvantage);
	}
}
