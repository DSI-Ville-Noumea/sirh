package nc.mairie.spring.dao.metier.agent;

import nc.mairie.metier.agent.SISERV;
import nc.mairie.spring.dao.utils.MairieDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class SISERVDao extends MairieDao implements SISERVDaoInterface {

	public static final String CHAMP_SERVI = "SERVI";
	public static final String CHAMP_LISERV = "LISERV";
	public static final String CHAMP_SIGLE = "SIGLE";

	public SISERVDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "SISERV";
	}

	@Override
	public SISERV chercherSiserv(String idServi) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_SERVI + " = ? ";
		SISERV cadre = (SISERV) jdbcTemplate.queryForObject(sql, new Object[] { idServi }, new BeanPropertyRowMapper<SISERV>(SISERV.class));
		return cadre;
	}
}
