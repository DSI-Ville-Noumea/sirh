package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.metier.diplome.FormationAgent;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class FormationAgentDao implements FormationAgentDaoInterface {

	public static final String NOM_TABLE = "FORMATION_AGENT";

	public static final String CHAMP_ID_FORMATION = "ID_FORMATION";
	public static final String CHAMP_ID_TITRE_FORMATION = "ID_TITRE_FORMATION";
	public static final String CHAMP_ID_CENTRE_FORMATION = "ID_CENTRE_FORMATION";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DUREE_FORMATION = "DUREE_FORMATION";
	public static final String CHAMP_UNITE_DUREE = "UNITE_DUREE";
	public static final String CHAMP_ANNEE_FORMATION = "ANNEE_FORMATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public FormationAgentDao() {

	}

	@Override
	public ArrayList<FormationAgent> listerFormationAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_ANNEE_FORMATION
				+ " desc";

		ArrayList<FormationAgent> listeFormationAgent = new ArrayList<FormationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			FormationAgent formation = new FormationAgent();
			formation.setIdFormation((Integer) row.get(CHAMP_ID_FORMATION));
			formation.setIdTitreFormation((Integer) row.get(CHAMP_ID_TITRE_FORMATION));
			formation.setIdCentreFormation((Integer) row.get(CHAMP_ID_CENTRE_FORMATION));
			formation.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			formation.setDureeFormation((Integer) row.get(CHAMP_DUREE_FORMATION));
			formation.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			formation.setAnneeFormation((Integer) row.get(CHAMP_ANNEE_FORMATION));
			listeFormationAgent.add(formation);
		}
		return listeFormationAgent;
	}

	@Override
	public void supprimerFormationAgent(Integer idFormationAgent) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_FORMATION + "=?";
		jdbcTemplate.update(sql, new Object[] { idFormationAgent });
	}

	@Override
	public Integer creerFormationAgent(Integer idTitreFormation, Integer idCentreFormation, Integer idAgent,
			Integer dureeFormation, String uniteDuree, Integer anneeFormation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TITRE_FORMATION + "," + CHAMP_ID_CENTRE_FORMATION
				+ "," + CHAMP_ID_AGENT + "," + CHAMP_DUREE_FORMATION + "," + CHAMP_UNITE_DUREE + ","
				+ CHAMP_ANNEE_FORMATION + ") " + "VALUES (?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idTitreFormation, idCentreFormation, idAgent, dureeFormation,
				uniteDuree, anneeFormation });

		String sqlId = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TITRE_FORMATION + "=? and "
				+ CHAMP_ID_CENTRE_FORMATION + "=? and " + CHAMP_ID_AGENT + "=? and " + CHAMP_DUREE_FORMATION
				+ "=? and " + CHAMP_UNITE_DUREE + "=? and " + CHAMP_ANNEE_FORMATION + "=?";

		FormationAgent form = (FormationAgent) jdbcTemplate
				.queryForObject(sqlId, new Object[] { idTitreFormation, idCentreFormation, idAgent, dureeFormation,
						uniteDuree, anneeFormation }, new BeanPropertyRowMapper<FormationAgent>(FormationAgent.class));

		return form.getIdFormation();
	}

	@Override
	public void modifierFormationAgent(Integer idFormation, Integer idTitreFormation, Integer idCentreFormation,
			Integer idAgent, Integer dureeFormation, String uniteDuree, Integer anneeFormation) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TITRE_FORMATION + "=? , " + CHAMP_ID_CENTRE_FORMATION
				+ "=?," + CHAMP_ID_AGENT + "=?," + CHAMP_DUREE_FORMATION + "=?," + CHAMP_UNITE_DUREE + "=?,"
				+ CHAMP_ANNEE_FORMATION + "=? where " + CHAMP_ID_FORMATION + " =?";

		jdbcTemplate.update(sql, new Object[] { idTitreFormation, idCentreFormation, idAgent, dureeFormation,
				uniteDuree, anneeFormation, idFormation });
	}

	@Override
	public ArrayList<FormationAgent> listerFormationAgentAvecTitreFormation(Integer idTitreFormation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TITRE_FORMATION + "=?";

		ArrayList<FormationAgent> listeFormationAgent = new ArrayList<FormationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTitreFormation });
		for (Map<String, Object> row : rows) {
			FormationAgent formation = new FormationAgent();
			formation.setIdFormation((Integer) row.get(CHAMP_ID_FORMATION));
			formation.setIdTitreFormation((Integer) row.get(CHAMP_ID_TITRE_FORMATION));
			formation.setIdCentreFormation((Integer) row.get(CHAMP_ID_CENTRE_FORMATION));
			formation.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			formation.setDureeFormation((Integer) row.get(CHAMP_DUREE_FORMATION));
			formation.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			formation.setAnneeFormation((Integer) row.get(CHAMP_ANNEE_FORMATION));
			listeFormationAgent.add(formation);
		}
		return listeFormationAgent;
	}

	@Override
	public ArrayList<FormationAgent> listerFormationAgentAvecCentreFormation(Integer idCentreFormation)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CENTRE_FORMATION + "=?";

		ArrayList<FormationAgent> listeFormationAgent = new ArrayList<FormationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCentreFormation });
		for (Map<String, Object> row : rows) {
			FormationAgent formation = new FormationAgent();
			formation.setIdFormation((Integer) row.get(CHAMP_ID_FORMATION));
			formation.setIdTitreFormation((Integer) row.get(CHAMP_ID_TITRE_FORMATION));
			formation.setIdCentreFormation((Integer) row.get(CHAMP_ID_CENTRE_FORMATION));
			formation.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			formation.setDureeFormation((Integer) row.get(CHAMP_DUREE_FORMATION));
			formation.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			formation.setAnneeFormation((Integer) row.get(CHAMP_ANNEE_FORMATION));
			listeFormationAgent.add(formation);
		}
		return listeFormationAgent;
	}

	@Override
	public ArrayList<FormationAgent> listerFormationAgentByAnnee(Integer idAgent, Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_ANNEE_FORMATION
				+ "=? order by " + CHAMP_ID_FORMATION + " desc";

		ArrayList<FormationAgent> listeFormationAgent = new ArrayList<FormationAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent, annee });
		for (Map<String, Object> row : rows) {
			FormationAgent formation = new FormationAgent();
			formation.setIdFormation((Integer) row.get(CHAMP_ID_FORMATION));
			formation.setIdTitreFormation((Integer) row.get(CHAMP_ID_TITRE_FORMATION));
			formation.setIdCentreFormation((Integer) row.get(CHAMP_ID_CENTRE_FORMATION));
			formation.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			formation.setDureeFormation((Integer) row.get(CHAMP_DUREE_FORMATION));
			formation.setUniteDuree((String) row.get(CHAMP_UNITE_DUREE));
			formation.setAnneeFormation((Integer) row.get(CHAMP_ANNEE_FORMATION));
			listeFormationAgent.add(formation);
		}
		return listeFormationAgent;
	}
}
