package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeDelegationDao extends SirhDao implements TypeDelegationDaoInterface {

	public static final String CHAMP_LIB_TYPE_DELEGATION = "LIB_TYPE_DELEGATION";

	public TypeDelegationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TYPE_DELEGATION";
		super.CHAMP_ID = "ID_TYPE_DELEGATION";
	}

	@Override
	public ArrayList<TypeDelegation> listerTypeDelegation() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TYPE_DELEGATION;

		ArrayList<TypeDelegation> listeTitreFormation = new ArrayList<TypeDelegation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeDelegation titre = new TypeDelegation();
			titre.setIdTypeDelegation((Integer) row.get(CHAMP_ID));
			titre.setLibTypeDelegation((String) row.get(CHAMP_LIB_TYPE_DELEGATION));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TypeDelegation chercherTypeDelegation(Integer idTypeDelegation) throws Exception {
		return super.chercherObject(TypeDelegation.class, idTypeDelegation);
	}

	@Override
	public void creerTypeDelegation(String libelleTypeDelegation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TYPE_DELEGATION + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTypeDelegation.toUpperCase() });
	}

	@Override
	public void supprimerTypeDelegation(Integer idTypeDelegation) throws Exception {
		super.supprimerObject(idTypeDelegation);
	}
}
