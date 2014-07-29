package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Utilisateur;
import nc.mairie.spring.dao.utils.SirhDao;

public class UtilisateurDao extends SirhDao implements UtilisateurDaoInterface {

	public static final String CHAMP_LOGIN_UTILISATEUR = "LOGIN_UTILISATEUR";

	public UtilisateurDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "UTILISATEUR";
		super.CHAMP_ID = "ID_UTILISATEUR";
	}

	@Override
	public ArrayList<Utilisateur> listerUtilisateur() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LOGIN_UTILISATEUR;

		ArrayList<Utilisateur> liste = new ArrayList<Utilisateur>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Utilisateur a = new Utilisateur();
			a.setIdUtilisateur((Integer) row.get(CHAMP_ID));
			a.setLoginUtilisateur((String) row.get(CHAMP_LOGIN_UTILISATEUR));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerUtilisateur(Integer idUtilisateur) throws Exception {
		super.supprimerObject(idUtilisateur);
	}

	@Override
	public void modifierUtilisateur(Integer idUtilisateur, String login) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LOGIN_UTILISATEUR + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { login, idUtilisateur });
	}

	@Override
	public Integer creerUtilisateur(String login) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " ("
				+ CHAMP_LOGIN_UTILISATEUR + ") " + "VALUES (?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { login }, Integer.class);
		return id;
	}
}
