package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.parametrage.TitrePermisRowMapper;
import nc.mairie.spring.domain.metier.parametrage.TitrePermis;

import org.springframework.jdbc.core.JdbcTemplate;

public class TitrePermisDao implements TitrePermisDaoInterface {

	public static final String NOM_TABLE = "P_PERMIS";

	public static final String CHAMP_ID_PERMIS = "ID_PERMIS";
	public static final String CHAMP_LIB_PERMIS = "LIB_PERMIS";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TitrePermisDao() {

	}

	@Override
	public ArrayList<TitrePermis> listerTitrePermis() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<TitrePermis> listeTitrePermis = new ArrayList<TitrePermis>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TitrePermis titre = new TitrePermis();
			titre.setIdTitrePermis((Integer) row.get(CHAMP_ID_PERMIS));
			titre.setLibTitrePermis((String) row.get(CHAMP_LIB_PERMIS));
			listeTitrePermis.add(titre);
		}

		return listeTitrePermis;
	}

	@Override
	public TitrePermis chercherTitrePermis(Integer idTitrePermis) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_PERMIS + " = ? ";
		TitrePermis titre = (TitrePermis) jdbcTemplate.queryForObject(sql, new Object[] { idTitrePermis }, new TitrePermisRowMapper());
		return titre;
	}

	@Override
	public void creerTitrePermis(String libelleTitre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_PERMIS + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTitre.toUpperCase() });
	}

	@Override
	public void modifierTitrePermis(Integer idTitre, String libelleTitre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_PERMIS + "=? where " + CHAMP_ID_PERMIS + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleTitre, idTitre });
	}

	@Override
	public void supprimerTitrePermis(Integer idTitrePermis) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_PERMIS + "=?";
		jdbcTemplate.update(sql, new Object[] { idTitrePermis });
	}
}
