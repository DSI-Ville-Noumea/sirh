package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.CampagneActeur;
import nc.mairie.spring.dao.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CampagneActeurDao extends EaeDao implements CampagneActeurDaoInterface {

	private Logger logger = LoggerFactory.getLogger(CampagneActeurDao.class);

	public static final String CHAMP_ID_CAMPAGNE_ACTION = "ID_CAMPAGNE_ACTION";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";

	public CampagneActeurDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_CAMPAGNE_ACTEURS";
		super.CHAMP_ID = "ID_CAMPAGNE_ACTEURS";
	}

	@Override
	public void creerCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_ACTION + "," + CHAMP_ID_AGENT
				+ ") VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] { idCampagneAction, idAgent });
	}

	@Override
	public CampagneActeur chercherCampagneActeur(Integer idCampagneAction, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTION + " = ? and " + CHAMP_ID_AGENT
				+ "=?";

		CampagneActeur acteur = (CampagneActeur) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneAction,
				idAgent }, new BeanPropertyRowMapper<CampagneActeur>(CampagneActeur.class));

		return acteur;
	}

	@Override
	public void supprimerCampagneActeur(Integer idCampagneActeur) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID + "=?";
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
			camp.setIdCampagneActeurs((Integer) row.get(CHAMP_ID));
			camp.setIdCampagneAction((Integer) row.get(CHAMP_ID_CAMPAGNE_ACTION));
			camp.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			listeCampagneActeur.add(camp);
		}

		return listeCampagneActeur;
	}

	@Override
	public void supprimerTousCampagneActeurCampagne(Integer idCampagneAction) throws Exception {
		super.supprimerObject(idCampagneAction);
	}
}
