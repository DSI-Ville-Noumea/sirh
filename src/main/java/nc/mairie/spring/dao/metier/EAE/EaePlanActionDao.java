package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaePlanActionDao implements EaePlanActionDaoInterface {

	public static final String NOM_TABLE = "EAE_PLAN_ACTION";

	public static final String NOM_SEQUENCE = "EAE_S_PLAN_ACTION";

	public static final String CHAMP_ID_EAE_PLAN_ACTION = "ID_EAE_PLAN_ACTION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_EAE_TYPE_OBJECTIF = "ID_EAE_TYPE_OBJECTIF";
	public static final String CHAMP_OBJECTIF = "OBJECTIF";
	public static final String CHAMP_MESURE = "MESURE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaePlanActionDao() {

	}

	@Override
	public ArrayList<EaePlanAction> listerPlanActionParType(Integer idEAE, Integer idtypeObj) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? and " + CHAMP_ID_EAE_TYPE_OBJECTIF + "=?";

		ArrayList<EaePlanAction> listeEaePlanAction = new ArrayList<EaePlanAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE, idtypeObj });
		for (Map<String, Object> row : rows) {
			EaePlanAction plan = new EaePlanAction();
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_PLAN_ACTION);
			plan.setIdEaePlanAction(id.intValue());
			BigDecimal idEae = (BigDecimal) row.get(CHAMP_ID_EAE);
			plan.setIdEae(idEae.intValue());
			BigDecimal idTypeObjectif = (BigDecimal) row.get(CHAMP_ID_EAE_TYPE_OBJECTIF);
			plan.setIdTypeObjectif(idTypeObjectif.intValue());
			plan.setObjectif((String) row.get(CHAMP_OBJECTIF));
			plan.setMesure((String) row.get(CHAMP_MESURE));

			listeEaePlanAction.add(plan);
		}
		return listeEaePlanAction;
	}

	@Override
	public void creerPlanAction(Integer idEae, Integer idTypeObjectif, String objectif, String mesure) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_PLAN_ACTION + "," + CHAMP_ID_EAE + "," + CHAMP_ID_EAE_TYPE_OBJECTIF + ","
				+ CHAMP_OBJECTIF + "," + CHAMP_MESURE + ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idTypeObjectif, objectif, mesure });
	}

	@Override
	public void supprimerEaePlanAction(Integer idEaePlanAction) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_PLAN_ACTION + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaePlanAction });
	}

	@Override
	public void modifierEaePlanAction(Integer idEaePlanAction, Integer idTypeObjectif, String objectif, String mesure) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_EAE_TYPE_OBJECTIF + " =?," + CHAMP_OBJECTIF + "=?," + CHAMP_MESURE + "=? where "
				+ CHAMP_ID_EAE_PLAN_ACTION + "=?";
		jdbcTemplate.update(sql, new Object[] { idTypeObjectif, objectif, mesure, idEaePlanAction });
	}
}
