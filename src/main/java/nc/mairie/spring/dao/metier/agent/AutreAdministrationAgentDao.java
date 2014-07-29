package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AutreAdministrationAgentDao extends SirhDao implements AutreAdministrationAgentDaoInterface {

	public static final String CHAMP_ID_AUTRE_ADMIN = "ID_AUTRE_ADMIN";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_ENTREE = "DATE_ENTREE";
	public static final String CHAMP_DATE_SORTIE = "DATE_SORTIE";
	public static final String CHAMP_FONCTIONNAIRE = "FONCTIONNAIRE";

	public AutreAdministrationAgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AUTRE_ADMIN_AGENT";
	}

	@Override
	public ArrayList<AutreAdministrationAgent> listerAutreAdministrationAgentAvecAgent(Integer idAgent)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_ENTREE;

		ArrayList<AutreAdministrationAgent> liste = new ArrayList<AutreAdministrationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			AutreAdministrationAgent a = new AutreAdministrationAgent();
			a.setIdAutreAdmin((Integer) row.get(CHAMP_ID_AUTRE_ADMIN));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateEntree((Date) row.get(CHAMP_DATE_ENTREE));
			a.setDateSortie((Date) row.get(CHAMP_DATE_SORTIE));
			a.setFonctionnaire((Integer) row.get(CHAMP_FONCTIONNAIRE));
			liste.add(a);
		}
		return liste;
	}

	@Override
	public ArrayList<AutreAdministrationAgent> listerAutreAdministrationAgentAvecAutreAdministration(
			Integer idAutreAdmin) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AUTRE_ADMIN + "=? ";

		ArrayList<AutreAdministrationAgent> liste = new ArrayList<AutreAdministrationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAutreAdmin });
		for (Map<String, Object> row : rows) {
			AutreAdministrationAgent a = new AutreAdministrationAgent();
			a.setIdAutreAdmin((Integer) row.get(CHAMP_ID_AUTRE_ADMIN));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setDateEntree((Date) row.get(CHAMP_DATE_ENTREE));
			a.setDateSortie((Date) row.get(CHAMP_DATE_SORTIE));
			a.setFonctionnaire((Integer) row.get(CHAMP_FONCTIONNAIRE));
			liste.add(a);
		}
		return liste;
	}

	@Override
	public void creerAutreAdministrationAgent(Integer idAutreAdmin, Integer idAgent, Date dateEntree, Date dateSortie,
			Integer fonctionnaire) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AUTRE_ADMIN + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_DATE_ENTREE + "," + CHAMP_DATE_SORTIE + "," + CHAMP_FONCTIONNAIRE + ") VALUES (?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAutreAdmin, idAgent, dateEntree, dateSortie, fonctionnaire });
	}

	@Override
	public void supprimerAutreAdministrationAgent(Integer idAutreAdmin, Integer idAgent, Date dateEntree)
			throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AUTRE_ADMIN + "=? and " + CHAMP_ID_AGENT
				+ "=? and " + CHAMP_DATE_ENTREE + "=?";
		jdbcTemplate.update(sql, new Object[] { idAutreAdmin, idAgent, dateEntree });
	}

	@Override
	public AutreAdministrationAgent chercherAutreAdministrationAgentFonctionnaireAncienne(Integer idAgent)
			throws Exception {
		String sql = "select a.* from " + NOM_TABLE + " a where a." + CHAMP_ID_AGENT + "=? and a."
				+ CHAMP_FONCTIONNAIRE + "= 1 and a." + CHAMP_DATE_ENTREE + "= (select min(aa." + CHAMP_DATE_ENTREE
				+ ") from " + NOM_TABLE + " aa where a." + CHAMP_ID_AGENT + "=aa." + CHAMP_ID_AGENT + ")";
		AutreAdministrationAgent doc = (AutreAdministrationAgent) jdbcTemplate.queryForObject(sql,
				new Object[] { idAgent }, new BeanPropertyRowMapper<AutreAdministrationAgent>(
						AutreAdministrationAgent.class));
		return doc;
	}

	@Override
	public AutreAdministrationAgent chercherAutreAdministrationAgentAncienne(Integer idAgent) throws Exception {
		String sql = "select a.* from " + NOM_TABLE + " a where a." + CHAMP_ID_AGENT + "=? and a." + CHAMP_DATE_ENTREE
				+ "= (select min(aa." + CHAMP_DATE_ENTREE + ") from " + NOM_TABLE + " aa where a." + CHAMP_ID_AGENT
				+ "=aa." + CHAMP_ID_AGENT + ")";
		AutreAdministrationAgent doc = (AutreAdministrationAgent) jdbcTemplate.queryForObject(sql,
				new Object[] { idAgent }, new BeanPropertyRowMapper<AutreAdministrationAgent>(
						AutreAdministrationAgent.class));
		return doc;
	}

	@Override
	public AutreAdministrationAgent chercherAutreAdministrationAgentDateDebut(Integer idAgent, Date dateEntree)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_DATE_ENTREE + "= ?";
		AutreAdministrationAgent doc = (AutreAdministrationAgent) jdbcTemplate.queryForObject(sql, new Object[] {
				idAgent, dateEntree }, new BeanPropertyRowMapper<AutreAdministrationAgent>(
				AutreAdministrationAgent.class));
		return doc;
	}

	@Override
	public AutreAdministrationAgent chercherAutreAdministrationAgentActive(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_DATE_ENTREE
				+ "<= ? and (" + CHAMP_DATE_SORTIE + " is null or " + CHAMP_DATE_SORTIE + ">=?)";
		AutreAdministrationAgent doc = (AutreAdministrationAgent) jdbcTemplate.queryForObject(sql, new Object[] {
				idAgent, new Date(), new Date() }, new BeanPropertyRowMapper<AutreAdministrationAgent>(
				AutreAdministrationAgent.class));
		return doc;
	}
}
