package nc.mairie.spring.dao.metier.EAE;

import nc.mairie.metier.eae.EaeCommentaire;
import nc.mairie.spring.dao.EaeDao;

public class EaeCommentaireDao extends EaeDao implements EaeCommentaireDaoInterface {

	public static final String NOM_SEQUENCE = "EAE_S_COMMENTAIRE";

	public static final String CHAMP_TEXT = "TEXT";

	public EaeCommentaireDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_COMMENTAIRE";
		super.CHAMP_ID = "ID_EAE_COMMENTAIRE";
	}

	@Override
	public EaeCommentaire chercherEaeCommentaire(Integer idEaeCommentaire) throws Exception {
		return super.chercherObject(EaeCommentaire.class, idEaeCommentaire);
	}

	@Override
	public Integer creerEaeCommentaire(String commentaire) throws Exception {

		String sqlClePrimaire = "select nextval('" + NOM_SEQUENCE + "')";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_TEXT + ") VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] { id, commentaire });

		return id;
	}

	@Override
	public void modifierEaeCommentaire(Integer idEaeCommentaire, String commentaire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TEXT + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { commentaire, idEaeCommentaire });
	}
}
