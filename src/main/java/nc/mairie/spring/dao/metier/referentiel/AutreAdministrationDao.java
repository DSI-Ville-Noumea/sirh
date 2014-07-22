package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.referentiel.AutreAdministration;
import nc.mairie.spring.dao.SirhDao;

public class AutreAdministrationDao extends SirhDao implements AutreAdministrationDaoInterface {

	public static final String CHAMP_LIB_AUTRE_ADMIN = "LIB_AUTRE_ADMIN";

	public AutreAdministrationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_AUTRE_ADMIN";
		super.CHAMP_ID = "ID_AUTRE_ADMIN";
	}

	@Override
	public ArrayList<AutreAdministration> listerAutreAdministration() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_AUTRE_ADMIN;

		ArrayList<AutreAdministration> liste = new ArrayList<AutreAdministration>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			AutreAdministration a = new AutreAdministration();
			a.setIdAutreAdmin((Integer) row.get(CHAMP_ID));
			a.setLibAutreAdmin((String) row.get(CHAMP_LIB_AUTRE_ADMIN));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public AutreAdministration chercherAutreAdministration(Integer idAutreAdmin) throws Exception {
		return super.chercherObject(AutreAdministration.class, idAutreAdmin);
	}

	@Override
	public void supprimerAutreAdministration(Integer idAutreAdmin) throws Exception {
		super.supprimerObject(idAutreAdmin);
	}

	@Override
	public void creerAutreAdministration(String libAutreAdmin) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_AUTRE_ADMIN + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libAutreAdmin.toUpperCase() });
	}
}
