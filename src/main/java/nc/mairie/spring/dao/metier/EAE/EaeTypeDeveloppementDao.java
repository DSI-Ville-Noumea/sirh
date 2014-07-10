package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeTypeDeveloppement;
import nc.mairie.spring.dao.EaeDao;

public class EaeTypeDeveloppementDao extends EaeDao implements EaeTypeDeveloppementDaoInterface {

	public static final String CHAMP_LIBELLE_TYPE_DEVELOPPEMENT = "LIBELLE_TYPE_DEVELOPPEMENT";

	public EaeTypeDeveloppementDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_TYPE_DEVELOPPEMENT";
		super.CHAMP_ID = "ID_EAE_TYPE_DEVELOPPEMENT";
	}

	@Override
	public ArrayList<EaeTypeDeveloppement> listerEaeTypeDeveloppement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIBELLE_TYPE_DEVELOPPEMENT;

		ArrayList<EaeTypeDeveloppement> listeEaeTypeDeveloppement = new ArrayList<EaeTypeDeveloppement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			EaeTypeDeveloppement dev = new EaeTypeDeveloppement();
			dev.setIdEaeTypeDeveloppement((Integer) row.get(CHAMP_ID));
			dev.setLibelleTypeDeveloppement((String) row.get(CHAMP_LIBELLE_TYPE_DEVELOPPEMENT));

			listeEaeTypeDeveloppement.add(dev);
		}
		return listeEaeTypeDeveloppement;
	}
}
