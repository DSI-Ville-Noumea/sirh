package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeCommentaire;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeCommentaireDao implements EaeCommentaireDaoInterface {

	public static final String NOM_TABLE = "EAE_COMMENTAIRE";

	public static final String NOM_SEQUENCE = "EAE_S_COMMENTAIRE";

	public static final String CHAMP_ID_EAE_COMMENTAIRE = "ID_EAE_COMMENTAIRE";
	public static final String CHAMP_TEXT = "TEXT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeCommentaireDao() {

	}

	@Override
	public EaeCommentaire chercherEaeCommentaire(Integer idEaeCommentaire) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_COMMENTAIRE + " = ? ";
		EaeCommentaire comm = null;
		try {
			comm = (EaeCommentaire) jdbcTemplate.queryForObject(sql, new Object[] { idEaeCommentaire }, 
					new BeanPropertyRowMapper<EaeCommentaire>(EaeCommentaire.class));

		} catch (Exception e) {
		}
		return comm;
	}

	@Override
	public Integer creerEaeCommentaire(String commentaire) throws Exception {

		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_COMMENTAIRE + "," + CHAMP_TEXT + ") VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] { id, commentaire });

		return id;
	}

	@Override
	public void modifierEaeCommentaire(Integer idEaeCommentaire, String commentaire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TEXT + " =? where " + CHAMP_ID_EAE_COMMENTAIRE + "=?";
		jdbcTemplate.update(sql, new Object[] { commentaire, idEaeCommentaire });
	}
}
