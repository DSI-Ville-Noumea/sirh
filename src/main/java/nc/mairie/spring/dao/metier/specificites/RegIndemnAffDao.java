package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.RegIndemnAFF;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class RegIndemnAffDao extends SirhDao implements RegIndemnAffDaoInterface {

	public static final String CHAMP_ID_REGIME = "ID_REGIME";
	public static final String CHAMP_ID_AFFECTATION = "ID_AFFECTATION";

	public RegIndemnAffDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "REG_INDEMN_AFF";
	}

	@Override
	public void creerRegIndemnAFF(Integer idRegime, Integer idAffectation) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_REGIME + "," + CHAMP_ID_AFFECTATION + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idRegime, idAffectation });
	}

	@Override
	public void supprimerRegIndemnAFF(Integer idRegime, Integer idAffectation) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? and " + CHAMP_ID_REGIME
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idAffectation, idRegime });
	}

	@Override
	public RegIndemnAFF chercherRegIndemnAFF(Integer idRegime, Integer idAffectation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AFFECTATION + " = ? and " + CHAMP_ID_REGIME
				+ "=?";
		RegIndemnAFF c = (RegIndemnAFF) jdbcTemplate.queryForObject(sql, new Object[] { idAffectation, idRegime },
				new BeanPropertyRowMapper<RegIndemnAFF>(RegIndemnAFF.class));
		return c;
	}

	@Override
	public ArrayList<RegIndemnAFF> listerRegIndemnAFFAvecRI(Integer idRegime) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_REGIME + "=? ";

		ArrayList<RegIndemnAFF> liste = new ArrayList<RegIndemnAFF>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idRegime });
		for (Map<String, Object> row : rows) {
			RegIndemnAFF d = new RegIndemnAFF();
			d.setIdRegime((Integer) row.get(CHAMP_ID_REGIME));
			d.setIdAffectation((Integer) row.get(CHAMP_ID_AFFECTATION));
			liste.add(d);
		}

		return liste;
	}

}