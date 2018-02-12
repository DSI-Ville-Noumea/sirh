package nc.mairie.spring.dao.metier.carriere;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import nc.mairie.enums.EnumModificationPA;
import nc.mairie.metier.carriere.MATMUT;
import nc.mairie.metier.carriere.MATMUTHIST;
import nc.mairie.spring.dao.utils.MairieDao;

public class MATMUTHistDao extends MairieDao implements MATMUTHISTDaoInterface {

	public static final String CHAMP_PKEY = "PKEY";
	public static final String CHAMP_NOMATR = "NOMATR";
	public static final String CHAMP_PERREP = "PERREP";
	public static final String CHAMP_CODVAL = "CODVAL";
	public static final String CHAMP_TIMELOG = "TIMELOG";
	public static final String CHAMP_IDUSER = "IDUSER";

	public MATMUTHistDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "MATMUTHIST";
		super.CHAMP_ID = CHAMP_PKEY;
	}

	@Override
	public void creerMATMUTHIST(MATMUT matmut) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_PKEY + "," + CHAMP_NOMATR + "," + CHAMP_PERREP
				+ "," + CHAMP_CODVAL + "," + CHAMP_TIMELOG + "," + CHAMP_IDUSER +") " 
				+ "VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { matmut.getPkey(), matmut.getNomatr(), matmut.getPerrep(),
						matmut.getCodval(), matmut.getTimelog(), matmut.getIduser() });
	}

	@Override
	public MATMUTHIST chercherMATMUTHISTVentileByAgentAndPeriod(Integer idAgent, Integer perrep) {
		MATMUTHIST matmutHist = null;
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " = ? AND " + CHAMP_PERREP + " = ? AND " + CHAMP_CODVAL + " = ?";
		try {
			matmutHist = (MATMUTHIST) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, perrep, EnumModificationPA.VENTILE.getCode() }, new BeanPropertyRowMapper<MATMUTHIST>(MATMUTHIST.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			return new MATMUTHIST();
		}
		return matmutHist;
	}
}
