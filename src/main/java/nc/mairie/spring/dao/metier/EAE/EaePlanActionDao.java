package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaePlanAction;
import nc.mairie.spring.dao.EaeDao;

public class EaePlanActionDao extends EaeDao implements EaePlanActionDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_EAE_TYPE_OBJECTIF = "ID_EAE_TYPE_OBJECTIF";
	public static final String CHAMP_OBJECTIF = "OBJECTIF";
	public static final String CHAMP_MESURE = "MESURE";

	public EaePlanActionDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_PLAN_ACTION";
		super.CHAMP_ID = "ID_EAE_PLAN_ACTION";
	}

	@Override
	public ArrayList<EaePlanAction> listerPlanActionParType(Integer idEAE, Integer idtypeObj) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? and " + CHAMP_ID_EAE_TYPE_OBJECTIF
				+ "=?";

		ArrayList<EaePlanAction> listeEaePlanAction = new ArrayList<EaePlanAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE, idtypeObj });
		for (Map<String, Object> row : rows) {
			EaePlanAction plan = new EaePlanAction();
			plan.setIdEaePlanAction((Integer) row.get(CHAMP_ID));
			plan.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			plan.setIdEaeTypeObjectif((Integer) row.get(CHAMP_ID_EAE_TYPE_OBJECTIF));
			plan.setObjectif((String) row.get(CHAMP_OBJECTIF));
			plan.setMesure((String) row.get(CHAMP_MESURE));

			listeEaePlanAction.add(plan);
		}
		return listeEaePlanAction;
	}

	@Override
	public void creerPlanAction(Integer idEae, Integer idTypeObjectif, String objectif, String mesure) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_ID_EAE_TYPE_OBJECTIF + ","
				+ CHAMP_OBJECTIF + "," + CHAMP_MESURE + ") " + "VALUES (?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idTypeObjectif, objectif, mesure });
	}

	@Override
	public void supprimerEaePlanAction(Integer idEaePlanAction) throws Exception {
		super.supprimerObject(idEaePlanAction);
	}

	@Override
	public void modifierEaePlanAction(Integer idEaePlanAction, Integer idTypeObjectif, String objectif, String mesure)
			throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_EAE_TYPE_OBJECTIF + " =?," + CHAMP_OBJECTIF + "=?,"
				+ CHAMP_MESURE + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { idTypeObjectif, objectif, mesure, idEaePlanAction });
	}
}
