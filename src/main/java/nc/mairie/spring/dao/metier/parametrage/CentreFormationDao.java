package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.parametrage.CentreFormationRowMapper;
import nc.mairie.spring.domain.metier.parametrage.CentreFormation;

import org.springframework.jdbc.core.JdbcTemplate;

public class CentreFormationDao implements CentreFormationDaoInterface {

	public static final String NOM_TABLE = "SIRH.P_CENTRE_FORMATION";

	public static final String CHAMP_ID_CENTRE_FORMATION = "ID_CENTRE_FORMATION";
	public static final String CHAMP_LIB_CENTRE_FORMATION = "LIB_CENTRE_FORMATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CentreFormationDao() {

	}

	@Override
	public ArrayList<CentreFormation> listerCentreFormation() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<CentreFormation> listeCentreFormation = new ArrayList<CentreFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			CentreFormation centre = new CentreFormation();
			centre.setIdCentreFormation((Integer) row.get(CHAMP_ID_CENTRE_FORMATION));
			centre.setLibCentreFormation((String) row.get(CHAMP_LIB_CENTRE_FORMATION));
			listeCentreFormation.add(centre);
		}

		return listeCentreFormation;
	}

	@Override
	public CentreFormation chercherCentreFormation(Integer idCentreFormation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CENTRE_FORMATION + " = ? ";
		CentreFormation centre = (CentreFormation) jdbcTemplate.queryForObject(sql, new Object[] { idCentreFormation },
				new CentreFormationRowMapper());
		return centre;
	}

	@Override
	public void creerCentreFormation(String libelleCentre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_CENTRE_FORMATION + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleCentre.toUpperCase() });
	}

	@Override
	public void modifierCentreFormation(Integer idCentre, String libelleCentre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_CENTRE_FORMATION + "=?where " + CHAMP_ID_CENTRE_FORMATION + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleCentre, idCentre });
	}

	@Override
	public void supprimerCentreFormation(Integer idCentreFormation) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CENTRE_FORMATION + "=?";
		jdbcTemplate.update(sql, new Object[] { idCentreFormation });
	}
}
