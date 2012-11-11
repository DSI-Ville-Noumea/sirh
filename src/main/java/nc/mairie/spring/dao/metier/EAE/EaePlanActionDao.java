package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaePlanActionDao implements EaePlanActionDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaePlanActionDao.class);

	public static final String NOM_TABLE = "EAE_PLAN_ACTION";

	public static final String NOM_SEQUENCE = "EAE_S_PLAN_ACTION";

	public static final String CHAMP_ID_EAE_PLAN_ACTION = "ID_EAE_PLAN_ACTION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_TYPE_OBJECTIF = "ID_TYPE_OBJECTIF";
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
	public ArrayList<EaePlanAction> listerEaePlanAction(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaePlanAction> listeEaePlanAction = new ArrayList<EaePlanAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map row : rows) {
			EaePlanAction plan = new EaePlanAction();
			// logger.debug("List plan action : " + row.toString());
			BigDecimal idEAEPlanAction = (BigDecimal) row.get(CHAMP_ID_EAE_PLAN_ACTION);
			plan.setIdEaePlanAction(idEAEPlanAction.intValue());
			BigDecimal eae = (BigDecimal) row.get(CHAMP_ID_EAE);
			plan.setIdEae(eae.intValue());
			BigDecimal idType = (BigDecimal) row.get(CHAMP_ID_TYPE_OBJECTIF);
			plan.setIdTypeObjectif(idType.intValue());
			plan.setObjectif((String) row.get(CHAMP_OBJECTIF));
			plan.setMesure((String) row.get(CHAMP_MESURE));
			listeEaePlanAction.add(plan);
		}

		return listeEaePlanAction;
	}

	@Override
	public ArrayList<EaePlanAction> listerEaePlanActionPourType(Integer idEAE, Integer idEaeTypeObjectif) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? and " + CHAMP_ID_TYPE_OBJECTIF + " =?";

		ArrayList<EaePlanAction> listeEaePlanAction = new ArrayList<EaePlanAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE, idEaeTypeObjectif });
		for (Map row : rows) {
			EaePlanAction plan = new EaePlanAction();
			// logger.debug("List plan action : " + row.toString());
			BigDecimal idEAEPlanAction = (BigDecimal) row.get(CHAMP_ID_EAE_PLAN_ACTION);
			plan.setIdEaePlanAction(idEAEPlanAction.intValue());
			BigDecimal eae = (BigDecimal) row.get(CHAMP_ID_EAE);
			plan.setIdEae(eae.intValue());
			BigDecimal idType = (BigDecimal) row.get(CHAMP_ID_TYPE_OBJECTIF);
			plan.setIdTypeObjectif(idType.intValue());
			plan.setObjectif((String) row.get(CHAMP_OBJECTIF));
			plan.setMesure((String) row.get(CHAMP_MESURE));
			listeEaePlanAction.add(plan);
		}

		return listeEaePlanAction;
	}
}
