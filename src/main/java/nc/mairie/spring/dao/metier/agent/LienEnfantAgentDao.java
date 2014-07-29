package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.LienEnfantAgent;
import nc.mairie.spring.dao.utils.SirhDao;

public class LienEnfantAgentDao extends SirhDao implements LienEnfantAgentDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_ENFANT = "ID_ENFANT";
	public static final String CHAMP_ENFANT_A_CHARGE = "ENFANT_A_CHARGE";

	public LienEnfantAgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "PARENT_ENFANT";
	}

	@Override
	public ArrayList<LienEnfantAgent> listerLienEnfantAgentAvecEnfant(Integer idEnfant) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ENFANT + "=? ";

		ArrayList<LienEnfantAgent> liste = new ArrayList<LienEnfantAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEnfant });
		for (Map<String, Object> row : rows) {
			LienEnfantAgent a = new LienEnfantAgent();
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdEnfant((Integer) row.get(CHAMP_ID_ENFANT));
			Integer charge = (Integer) row.get(CHAMP_ENFANT_A_CHARGE);
			a.setEnfantACharge(charge == 0 ? false : true);
			liste.add(a);
		}
		return liste;
	}

	@Override
	public ArrayList<LienEnfantAgent> listerLienEnfantAgentAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? ";

		ArrayList<LienEnfantAgent> liste = new ArrayList<LienEnfantAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			LienEnfantAgent a = new LienEnfantAgent();
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdEnfant((Integer) row.get(CHAMP_ID_ENFANT));
			Integer charge = (Integer) row.get(CHAMP_ENFANT_A_CHARGE);
			a.setEnfantACharge(charge == 0 ? false : true);
			liste.add(a);
		}
		return liste;
	}

	@Override
	public void creerLienEnfantAgent(Integer idAgent, Integer idEnfant, boolean enfantACharge) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_ENFANT + ","
				+ CHAMP_ENFANT_A_CHARGE + ") VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idEnfant, enfantACharge });
	}

	@Override
	public void modifierLienEnfantAgent(Integer idAgent, Integer idEnfant, boolean enfantACharge) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ENFANT_A_CHARGE + "=? where " + CHAMP_ID_AGENT
				+ " =? and " + CHAMP_ID_ENFANT + "=?";
		jdbcTemplate.update(sql, new Object[] { enfantACharge, idAgent, idEnfant });
	}

	@Override
	public void supprimerLienEnfantAgent(Integer idAgent, Integer idEnfant) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_ID_ENFANT + "=?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idEnfant });
	}
}
