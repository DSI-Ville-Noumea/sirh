package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.spring.dao.SirhDao;

public class RecommandationDao extends SirhDao implements RecommandationDaoInterface {

	public static final String CHAMP_DESC_RECOMMANDATION = "DESC_RECOMMANDATION";

	public RecommandationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_RECOMMANDATION";
		super.CHAMP_ID = "ID_RECOMMANDATION";
	}

	@Override
	public void creerRecommandation(String description) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_DESC_RECOMMANDATION + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { description.toUpperCase() });
	}

	@Override
	public void supprimerRecommandation(Integer idRecommandation) throws Exception {
		super.supprimerObject(idRecommandation);
	}

	@Override
	public ArrayList<Recommandation> listerRecommandation() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_DESC_RECOMMANDATION;

		ArrayList<Recommandation> liste = new ArrayList<Recommandation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Recommandation a = new Recommandation();
			a.setIdRecommandation((Integer) row.get(CHAMP_ID));
			a.setDescRecommandation((String) row.get(CHAMP_DESC_RECOMMANDATION));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public Recommandation chercherRecommandation(Integer idRecommandation) throws Exception {
		return super.chercherObject(Recommandation.class, idRecommandation);
	}
}
