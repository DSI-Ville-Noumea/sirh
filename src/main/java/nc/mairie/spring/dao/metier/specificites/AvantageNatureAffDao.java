package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.metier.specificites.AvantageNatureAFF;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AvantageNatureAffDao extends SirhDao implements AvantageNatureAffDaoInterface {

	public static final String CHAMP_ID_AVANTAGE = "ID_AVANTAGE";
	public static final String CHAMP_ID_AFFECTATION = "ID_AFFECTATION";

	public AvantageNatureAffDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVANTAGE_NATURE_AFF";
	}

	@Override
	public void creerAvantageNatureAff(Integer idAvantage, Integer idAffectation) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AVANTAGE + "," + CHAMP_ID_AFFECTATION + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idAvantage, idAffectation });
	}

	@Override
	public void supprimerAvantageNatureAff(Integer idAvantage, Integer idAffectation) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? and " + CHAMP_ID_AVANTAGE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idAffectation, idAvantage });
	}

	@Override
	public AvantageNatureAFF chercherAvantageNatureAFF(Integer idAvantage, Integer idAffectation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AFFECTATION + " = ? and " + CHAMP_ID_AVANTAGE
				+ "=?";
		AvantageNatureAFF c = (AvantageNatureAFF) jdbcTemplate.queryForObject(sql, new Object[] { idAffectation,
				idAvantage }, new BeanPropertyRowMapper<AvantageNatureAFF>(AvantageNatureAFF.class));
		return c;
	}

}