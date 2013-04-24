package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.CampagneActeurRowMapper;
import nc.mairie.spring.domain.metier.EAE.CampagneActeur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampagneActeurDao implements CampagneActeurDaoInterface {

	private Logger logger = LoggerFactory.getLogger(CampagneActeurDao.class);

	public static final String NOM_TABLE = "EAE_CAMPAGNE_ACTEURS";

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_ACTEURS";

	public static final String CHAMP_ID_CAMPAGNE_ACTEUR = "ID_CAMPAGNE_ACTEURS";
	public static final String CHAMP_ID_CAMPAGNE_ACTION = "ID_CAMPAGNE_ACTION";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CampagneActeurDao() {

	}

	@Override
	public void creerCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_ACTEUR + "," + CHAMP_ID_CAMPAGNE_ACTION + "," + CHAMP_ID_AGENT
				+ ") VALUES (" + NOM_SEQUENCE + ".nextval,?, ?)";
		jdbcTemplate.update(sql, new Object[] { idCampagneAction, idAgent });
	}

	@Override
	public CampagneActeur chercherCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTION + " = ? and " + CHAMP_ID_AGENT + "=?";

		@SuppressWarnings("unchecked")
		CampagneActeur acteur = (CampagneActeur) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneAction, idAgent },
				new CampagneActeurRowMapper());

		return acteur;
	}

	@Override
	public void supprimerCampagneActeur(Integer idCampagneActeur) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTEUR + "=?";
		jdbcTemplate.update(sql, new Object[] { idCampagneActeur });
	}

	@Override
	public ArrayList<CampagneActeur> listerCampagneActeur(Integer idCampagneAction) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTION + "=?";

		ArrayList<CampagneActeur> listeCampagneActeur = new ArrayList<CampagneActeur>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneAction });
		for (Map<String, Object> row : rows) {
			CampagneActeur camp = new CampagneActeur();
			logger.info("List Campagne Acteur : " + row.toString());
			BigDecimal idCampActeur = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_ACTEUR);
			camp.setIdCampagneActeur(idCampActeur.intValue());
			BigDecimal idCampAction = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_ACTION);
			camp.setIdCampagneAction(idCampAction.intValue());
			BigDecimal idAgent = (BigDecimal) row.get(CHAMP_ID_AGENT);
			camp.setIdAgent(idAgent.intValue());
			listeCampagneActeur.add(camp);
		}

		return listeCampagneActeur;
	}

	@Override
	public void supprimerTousCampagneActeurCampagne(Integer idCampagneAction) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTION+ "=?";
		jdbcTemplate.update(sql, new Object[] { idCampagneAction });
	}
}
