package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Utilisateur;
import nc.mairie.spring.dao.SirhDao;

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
	public void creerUtilisateur(String login) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LOGIN_UTILISATEUR + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { login });
	}
}
