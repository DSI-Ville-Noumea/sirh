package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.diplome.PermisAgent;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class PermisAgentDao extends SirhDao implements PermisAgentDaoInterface {

	public static final String CHAMP_ID_PERMIS = "ID_PERMIS";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DUREE_PERMIS = "DUREE_PERMIS";
	public static final String CHAMP_UNITE_DUREE = "UNITE_DUREE";
	public static final String CHAMP_DATE_OBTENTION = "DATE_OBTENTION";

	public PermisAgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "PERMIS_AGENT";
		super.CHAMP_ID = "ID_PERMIS_AGENT";
	}

	@Override
	public ArrayList<PermisAgent> listerPermisAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=?";

		ArrayList<PermisAgent> listePermisAgent = new ArrayList<PermisAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			PermisAgent permis = new PermisAgent();
			permis.setIdPermisAgent((Integer) row.get(CHAMP_ID));
			permis.setIdPermis((Integer) row.get(CHAMP_ID_PERMIS));
			permis.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			permis.setDureePermis((Integer) row.get(CHAMP_DUREE_PERMIS));
			permis.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			permis.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			listePermisAgent.add(permis);
		}
		return listePermisAgent;
	}

	@Override
	public void supprimerPermisAgent(Integer idPermisAgent) throws Exception {
		super.supprimerObject(idPermisAgent);
	}

	@Override
	public Integer creerPermisAgent(Integer idPermis, Integer idAgent, Integer dureePermis, String uniteDuree,
			Date dateObtention) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_PERMIS + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_DUREE_PERMIS + "," + CHAMP_UNITE_DUREE + "," + CHAMP_DATE_OBTENTION + ") "
				+ "VALUES (?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idPermis, idAgent, dureePermis, uniteDuree, dateObtention });
		if (dureePermis == null && uniteDuree == null) {
			String sqlId = "select * from " + NOM_TABLE + " where " + CHAMP_ID_PERMIS + "=? and " + CHAMP_ID_AGENT
					+ "=? and " + CHAMP_DUREE_PERMIS + " is null and " + CHAMP_UNITE_DUREE + " is null and "
					+ CHAMP_DATE_OBTENTION + "=?";

			PermisAgent perm = (PermisAgent) jdbcTemplate.queryForObject(sqlId, new Object[] { idPermis, idAgent,
					dateObtention }, new BeanPropertyRowMapper<PermisAgent>(PermisAgent.class));

			return perm.getIdPermisAgent();
		} else {
			String sqlId = "select * from " + NOM_TABLE + " where " + CHAMP_ID_PERMIS + "=? and " + CHAMP_ID_AGENT
					+ "=? and " + CHAMP_DUREE_PERMIS + "=? and " + CHAMP_UNITE_DUREE + "=? and " + CHAMP_DATE_OBTENTION
					+ "=?";

			PermisAgent perm = (PermisAgent) jdbcTemplate
					.queryForObject(sqlId, new Object[] { idPermis, idAgent, dureePermis, uniteDuree, dateObtention },
							new BeanPropertyRowMapper<PermisAgent>(PermisAgent.class));

			return perm.getIdPermisAgent();
		}
	}

	@Override
	public void modifierPermisAgent(Integer idPermisAgent, Integer idPermis, Integer idAgent, Integer dureePermis,
			String uniteDuree, Date dateObtention) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_PERMIS + "=? , " + CHAMP_ID_AGENT + "=?,"
				+ CHAMP_DUREE_PERMIS + "=?," + CHAMP_UNITE_DUREE + "=?," + CHAMP_DATE_OBTENTION + "=? where "
				+ CHAMP_ID + " =?";

		jdbcTemplate.update(sql, new Object[] { idPermis, idAgent, dureePermis, uniteDuree, dateObtention,
				idPermisAgent });
	}

	@Override
	public ArrayList<PermisAgent> listerPermisAgentAvecTitrePermis(Integer idPermis) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_PERMIS + "=?";

		ArrayList<PermisAgent> listePermisAgent = new ArrayList<PermisAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idPermis });
		for (Map<String, Object> row : rows) {
			PermisAgent permis = new PermisAgent();
			permis.setIdPermisAgent((Integer) row.get(CHAMP_ID));
			permis.setIdPermis((Integer) row.get(CHAMP_ID_PERMIS));
			permis.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			permis.setDureePermis((Integer) row.get(CHAMP_DUREE_PERMIS));
			permis.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			permis.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			listePermisAgent.add(permis);
		}
		return listePermisAgent;
	}
}
