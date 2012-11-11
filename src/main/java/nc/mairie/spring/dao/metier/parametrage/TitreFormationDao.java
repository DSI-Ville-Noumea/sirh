package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.parametrage.TitreFormationRowMapper;
import nc.mairie.spring.domain.metier.parametrage.TitreFormation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class TitreFormationDao implements TitreFormationDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(TitreFormationDao.class);

	public static final String NOM_TABLE = "SIRH.P_TITRE_FORMATION";

	public static final String CHAMP_ID_TITRE_FORMATION = "ID_TITRE_FORMATION";
	public static final String CHAMP_LIB_TITRE_FORMATION = "LIB_TITRE_FORMATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TitreFormationDao() {

	}

	@Override
	public ArrayList<TitreFormation> listerTitreFormation() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<TitreFormation> listeTitreFormation = new ArrayList<TitreFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			TitreFormation titre = new TitreFormation();
			titre.setIdTitreFormation((Integer) row.get(CHAMP_ID_TITRE_FORMATION));
			titre.setLibTitreFormation((String) row.get(CHAMP_LIB_TITRE_FORMATION));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TitreFormation chercherTitreFormation(Integer idTitreFormation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TITRE_FORMATION + " = ? ";
		TitreFormation titre = (TitreFormation) jdbcTemplate.queryForObject(sql, new Object[] { idTitreFormation }, new TitreFormationRowMapper());
		return titre;
	}

	@Override
	public void creerTitreFormation(String libelleTitre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TITRE_FORMATION + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTitre.toUpperCase() });
	}

	@Override
	public void modifierTitreFormation(Integer idTitre, String libelleTitre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_TITRE_FORMATION + "=? where " + CHAMP_ID_TITRE_FORMATION + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleTitre, idTitre });
	}

	@Override
	public void supprimerTitreFormation(Integer idTitreFormation) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_TITRE_FORMATION + "=?";
		jdbcTemplate.update(sql, new Object[] { idTitreFormation });
	}
}
