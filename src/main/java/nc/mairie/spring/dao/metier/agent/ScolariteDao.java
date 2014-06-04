package nc.mairie.spring.dao.metier.agent;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import nc.mairie.metier.agent.Scolarite;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class ScolariteDao implements ScolariteDaoInterface {

	public static final String NOM_TABLE = "SCOLARITE";

	public static final String CHAMP_ID_SCOLARITE = "ID_SCOLARITE";
	public static final String CHAMP_ID_ENFANT = "ID_ENFANT";
	public static final String CHAMP_DATE_DEBUT_SCOLARITE = "DATE_DEBUT_SCOLARITE";
	public static final String CHAMP_DATE_FIN_SCOLARITE = "DATE_FIN_SCOLARITE";

	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public ScolariteDao() {

	}

	@Override
	public void creerScolarite(Integer idEnfant, Date dateDebut, Date dateFin) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_ENFANT + "," + CHAMP_DATE_DEBUT_SCOLARITE + "," + CHAMP_DATE_FIN_SCOLARITE + ") "
				+ "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idEnfant, dateDebut, dateFin });
	}

	@Override
	public void modifierScolarite(Integer idScolarite, Integer idEnfant, Date dateDebut, Date dateFin) throws Exception {
		
			String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_ENFANT
					+ "=?," + CHAMP_DATE_DEBUT_SCOLARITE + "=?," + CHAMP_DATE_FIN_SCOLARITE + "=?where " + CHAMP_ID_SCOLARITE + " =?";

			jdbcTemplate.update(sql, new Object[] { idEnfant, dateDebut, dateFin, idScolarite });
	}

	@Override
	public void supprimerScolarite(Integer idScolarite)
			throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_SCOLARITE + "=? ";
		jdbcTemplate.update(sql, new Object[] { idScolarite });
	}

	@Override
	public List<Scolarite> listerScolarite()
			throws Exception {
		
		String sql = "select * from " + NOM_TABLE;
		List<Scolarite> rows = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Scolarite.class));
		
		return rows;
	}

	@Override
	public List<Scolarite> listerScolariteEnfant(Integer idEnfant) throws Exception {
		
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ENFANT +" =? ";

		List<Scolarite> rows = jdbcTemplate.query(sql, new Object[] { idEnfant }, new BeanPropertyRowMapper(Scolarite.class));
		
		
		return rows;
	}

	@Override
	public Scolarite chercherScolarite(Integer idScolarite)
			throws Exception {
		
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_SCOLARITE +" =? ";

		Scolarite rows = jdbcTemplate.queryForObject(sql, new Object[] { idScolarite }, new BeanPropertyRowMapper(Scolarite.class));
		
		
		return rows;
		
	}

	
}
