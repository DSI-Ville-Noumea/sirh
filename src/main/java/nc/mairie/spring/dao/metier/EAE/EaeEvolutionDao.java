package nc.mairie.spring.dao.metier.EAE;


import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeEvolutionRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeEvolutionDao implements EaeEvolutionDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeEvolutionDao.class);

	public static final String NOM_TABLE = "EAE_EVOLUTION";

	public static final String NOM_SEQUENCE = "EAE_S_EVOLUTION";

	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_MOBILITE_GEO = "MOBILITE_GEO";
	public static final String CHAMP_MOBILITE_FONCT = "MOBILITE_FONCT";
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
			
		}
		return evol;
	}
}
