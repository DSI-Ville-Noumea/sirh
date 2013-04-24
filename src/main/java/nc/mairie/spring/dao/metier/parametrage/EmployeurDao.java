package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.parametrage.EmployeurRowMapper;
import nc.mairie.spring.domain.metier.parametrage.Employeur;

import org.springframework.jdbc.core.JdbcTemplate;

public class EmployeurDao implements EmployeurDaoInterface {

	public static final String NOM_TABLE = "SIRH.P_EMPLOYEUR";

	public static final String CHAMP_ID_EMPLOYEUR = "ID_EMPLOYEUR";
	public static final String CHAMP_LIB_EMPLOYEUR = "LIB_EMPLOYEUR";
	public static final String CHAMP_TITRE_EMPLOYEUR = "TITRE_EMPLOYEUR";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EmployeurDao() {

	}

	@Override
	public ArrayList<Employeur> listerEmployeur() throws Exception {
		String sql = "select * from " + NOM_TABLE ;

		ArrayList<Employeur> listeEmployeur = new ArrayList<Employeur>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Employeur employeur = new Employeur();
			employeur.setIdEmployeur((Integer) row.get(CHAMP_ID_EMPLOYEUR));
			employeur.setLibEmployeur((String) row.get(CHAMP_LIB_EMPLOYEUR));
			employeur.setTitreEmployeur((String) row.get(CHAMP_TITRE_EMPLOYEUR));
			listeEmployeur.add(employeur);
		}

		return listeEmployeur;
	}

	@Override
	public Employeur chercherEmployeur(Integer idEmployeur) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EMPLOYEUR + " = ? ";
		Employeur centre = (Employeur) jdbcTemplate.queryForObject(sql, new Object[] { idEmployeur }, new EmployeurRowMapper());
		return centre;
	}

	@Override
	public void creerEmployeur(String libelleEmployeur, String titreEmployeur) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_EMPLOYEUR + "," + CHAMP_TITRE_EMPLOYEUR + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleEmployeur, titreEmployeur });
	}

	@Override
	public void modifierEmployeur(Integer idEmployeur, String libelleEmployeur, String titreEmployeur) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_EMPLOYEUR + "=?," + CHAMP_TITRE_EMPLOYEUR + "=? where " + CHAMP_ID_EMPLOYEUR + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleEmployeur, titreEmployeur, idEmployeur });
	}

	@Override
	public void supprimerEmployeur(Integer idEmployeur) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EMPLOYEUR + "=?";
		jdbcTemplate.update(sql, new Object[] { idEmployeur });
	}
}
