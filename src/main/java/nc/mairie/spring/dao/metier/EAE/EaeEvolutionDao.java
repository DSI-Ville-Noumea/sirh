package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeEvolutionRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeEvolutionDao implements EaeEvolutionDaoInterface {

	public static final String NOM_TABLE = "EAE_EVOLUTION";

	public static final String NOM_SEQUENCE = "EAE_S_EVOLUTION";

	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_MOBILITE_GEO = "MOBILIE_GEO";
	public static final String CHAMP_MOBILITE_FONCT = "MOBILIE_FONCT";
	public static final String CHAMP_CHANGEMENT_METIER = "CHANGEMENT_METIER";
	public static final String CHAMP_DELAI_ENVISAGE = "DELAI_ENVISAGE";
	public static final String CHAMP_MOBILITE_SERVICE = "MOBILITE_SERVICE";
	public static final String CHAMP_MOBILITE_DIRECTION = "MOBILITE_DIRECTION";
	public static final String CHAMP_MOBILITE_COLLECTIVITE = "MOBILITE_COLLECTIVITE";
	public static final String CHAMP_MOBILITE_AUTRE = "MOBILITE_AUTRE";
	public static final String CHAMP_NOM_COLLECTIVITE = "NOM_COLLECTIVITE";
	public static final String CHAMP_CONCOURS = "CONCOURS";
	public static final String CHAMP_NOM_CONCOURS = "NOM_CONCOURS";
	public static final String CHAMP_VAE = "VAE";
	public static final String CHAMP_NOM_VAE = "NOM_VAE";
	public static final String CHAMP_TEMPS_PARTIEL = "TEMPS_PARTIEL";
	public static final String CHAMP_TEMPS_PARTIEL_ID_SPBHOR = "TEMPS_PARTIEL_ID_SPBHOR";
	public static final String CHAMP_RETRAITE = "RETRAITE";
	public static final String CHAMP_DATE_RETRAITE = "DATE_RETRAITE";
	public static final String CHAMP_AUTRE_PERSPECTIVE = "AUTRE_PERSPECTIVE";
	public static final String CHAMP_LIB_AUTRE_PERSPECTIVE = "LIB_AUTRE_PERSPECTIVE";
	public static final String CHAMP_ID_EAE_COM_EVOLUTION = "ID_EAE_COM_EVOLUTION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeEvolutionDao() {

	}

	@Override
	public EaeEvolution chercherEaeEvolution(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? ";
		EaeEvolution evol = null;
		try {
			evol = (EaeEvolution) jdbcTemplate.queryForObject(sql, new Object[] { idEAE }, new EaeEvolutionRowMapper());
		} catch (Exception e) {
			System.out.println("ici" + e);
		}
		return evol;
	}

	@Override
	public void modifierMobiliteEaeEvolution(Integer idEaeEvolution, boolean mobGeo, boolean mobFonct, boolean mobServ, boolean mobDir,
			boolean mobColl, boolean mobAutre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_MOBILITE_GEO + " =?," + CHAMP_MOBILITE_FONCT + "=?," + CHAMP_MOBILITE_SERVICE + "=?,"
				+ CHAMP_MOBILITE_DIRECTION + "=?," + CHAMP_MOBILITE_COLLECTIVITE + "=?," + CHAMP_MOBILITE_AUTRE + "=? where "
				+ CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { mobGeo, mobFonct, mobServ, mobDir, mobColl, mobAutre, idEaeEvolution });
	}

	@Override
	public void modifierChangementMetierEaeEvolution(Integer idEaeEvolution, boolean chgtMetier) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CHANGEMENT_METIER + " =? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { chgtMetier, idEaeEvolution });
	}

	@Override
	public void modifierDelaiEaeEvolution(Integer idEaeEvolution, String delai) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DELAI_ENVISAGE + " =? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { delai, idEaeEvolution });
	}

	@Override
	public void modifierAutresInfosEaeEvolution(Integer idEaeEvolution, boolean concours, boolean vae, boolean tempsPartiel, boolean retraite,
			boolean autrePerspective) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CONCOURS + " =?," + CHAMP_VAE + "=?," + CHAMP_TEMPS_PARTIEL + "=?," + CHAMP_RETRAITE
				+ "=?," + CHAMP_AUTRE_PERSPECTIVE + "=? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { concours, vae, tempsPartiel, retraite, autrePerspective, idEaeEvolution });
	}

	@Override
	public void modifierLibelleEaeEvolution(Integer idEaeEvolution, String nomCollectivite, String nomConcours, String nomVae,
			String libAutrePerspective) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_NOM_COLLECTIVITE + " =?," + CHAMP_NOM_CONCOURS + "=?," + CHAMP_NOM_VAE + "=?,"
				+ CHAMP_LIB_AUTRE_PERSPECTIVE + "=? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { nomCollectivite, nomConcours, nomVae, libAutrePerspective, idEaeEvolution });
	}

	@Override
	public void modifierDateRetraiteEaeEvolution(Integer idEaeEvolution, Date dateRetraite) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_RETRAITE + " =? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { dateRetraite, idEaeEvolution });
	}

	@Override
	public void modifierCommentaireEaeEvaluation(Integer idEaeEvolution, Integer idCree) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_EAE_COM_EVOLUTION + " =? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { idCree, idEaeEvolution });
	}

	@Override
	public void modifierPourcTpsPartielEaeEvolution(Integer idEaeEvolution, Integer idSpbhorTpsPartiel) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TEMPS_PARTIEL_ID_SPBHOR + " =? where " + CHAMP_ID_EAE_EVOLUTION + "=?";
		jdbcTemplate.update(sql, new Object[] { idSpbhorTpsPartiel, idEaeEvolution });
	}
}
