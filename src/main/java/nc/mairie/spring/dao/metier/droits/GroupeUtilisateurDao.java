package nc.mairie.spring.dao.metier.droits;

import nc.mairie.metier.droits.GroupeUtilisateur;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class GroupeUtilisateurDao extends SirhDao implements GroupeUtilisateurDaoInterface {

	public static final String CHAMP_ID_UTILISATEUR = "ID_UTILISATEUR";
	public static final String CHAMP_ID_GROUPE = "ID_GROUPE";

	public GroupeUtilisateurDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "GROUPE_UTILISATEUR";
	}

	@Override
	public void supprimerGroupeUtilisateurAvecGroupe(Integer idGroupe) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_GROUPE + "=? ";
		jdbcTemplate.update(sql, new Object[] { idGroupe });
	}

	@Override
	public void creerGroupeUtilisateur(Integer idUtilisateur, Integer idGroupe) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_UTILISATEUR + "," + CHAMP_ID_GROUPE + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idUtilisateur, idGroupe });
	}

	@Override
	public GroupeUtilisateur chercherGroupeUtilisateur(Integer idUtilisateur, Integer idGroupe) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_UTILISATEUR + " = ? and " + CHAMP_ID_GROUPE
				+ "=?";
		GroupeUtilisateur cadre = (GroupeUtilisateur) jdbcTemplate.queryForObject(sql, new Object[] { idUtilisateur,
				idGroupe }, new BeanPropertyRowMapper<GroupeUtilisateur>(GroupeUtilisateur.class));
		return cadre;
	}
}
