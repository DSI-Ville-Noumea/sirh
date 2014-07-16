package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.spring.dao.SirhDao;

public class NatureAvantageDao extends SirhDao implements NatureAvantageDaoInterface {

	public static final String CHAMP_LIB_NATURE_AVANTAGE = "LIB_NATURE_AVANTAGE";

	public NatureAvantageDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_NATURE_AVANTAGE";
		super.CHAMP_ID = "ID_NATURE_AVANTAGE";
	}

	@Override
	public void creerNatureAvantage(String libelleNatureAvantage) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_NATURE_AVANTAGE + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleNatureAvantage.toUpperCase() });
	}

	@Override
	public void supprimerNatureAvantage(Integer idNatureAvantage) throws Exception {
		super.supprimerObject(idNatureAvantage);
	}

	@Override
	public ArrayList<NatureAvantage> listerNatureAvantage() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_NATURE_AVANTAGE;

		ArrayList<NatureAvantage> listeMotif = new ArrayList<NatureAvantage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NatureAvantage motif = new NatureAvantage();
			motif.setIdNatureAvantage((Integer) row.get(CHAMP_ID));
			motif.setLibNatureAvantage((String) row.get(CHAMP_LIB_NATURE_AVANTAGE));

			listeMotif.add(motif);
		}

		return listeMotif;
	}

	@Override
	public NatureAvantage chercherNatureAvantage(Integer idNatureAvantage) throws Exception {
		return super.chercherObject(NatureAvantage.class, idNatureAvantage);
	}
}
