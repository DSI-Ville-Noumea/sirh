package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Groupe;
import nc.mairie.spring.dao.SirhDao;

public class GroupeDao extends SirhDao implements GroupeDaoInterface {

	public static final String CHAMP_LIB_GROUPE = "LIB_GROUPE";

	public GroupeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DROITS_GROUPE";
		super.CHAMP_ID = "ID_GROUPE";
	}

	@Override
	public ArrayList<Groupe> listerGroupe() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_GROUPE;

		ArrayList<Groupe> liste = new ArrayList<Groupe>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Groupe a = new Groupe();
			a.setIdGroupe((Integer) row.get(CHAMP_ID));
			a.setLibGroupe((String) row.get(CHAMP_LIB_GROUPE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Groupe> listerGroupeAvecUtilisateur(Integer idUtilisateur) throws Exception {
		String sql = "select prof.* from " + NOM_TABLE
				+ " prof, GROUPE_UTILISATEUR pu where pu.ID_UTILISATEUR =? and prof." + CHAMP_ID
				+ " = pu.ID_GROUPE order by prof." + CHAMP_LIB_GROUPE;

		ArrayList<Groupe> liste = new ArrayList<Groupe>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idUtilisateur });
		for (Map<String, Object> row : rows) {
			Groupe a = new Groupe();
			a.setIdGroupe((Integer) row.get(CHAMP_ID));
			a.setLibGroupe((String) row.get(CHAMP_LIB_GROUPE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerGroupe(Integer idGroupe) throws Exception {
		super.supprimerObject(idGroupe);
	}

	@Override
	public void modifierGroupe(Integer idGroupe, String libGroupe) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_GROUPE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libGroupe, idGroupe });
	}

	@Override
	public Integer creerGroupe(String libGroupe) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_GROUPE
				+ ") " + "VALUES (?)) ";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { libGroupe }, Integer.class);
		return id;
	}
}
