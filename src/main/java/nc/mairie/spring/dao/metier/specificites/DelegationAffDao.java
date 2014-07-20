package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.metier.specificites.DelegationAFF;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DelegationAffDao extends SirhDao implements DelegationAffDaoInterface {

	public static final String CHAMP_ID_DELEGATION = "ID_DELEGATION";
	public static final String CHAMP_ID_AFFECTATION = "ID_AFFECTATION";

	public DelegationAffDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DELEGATION_AFF";
	}

	@Override
	public void creerDelegationAFF(Integer idDelegation, Integer idAffectation) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_DELEGATION + "," + CHAMP_ID_AFFECTATION + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idDelegation, idAffectation });
	}

	@Override
	public void supprimerDelegationAFF(Integer idDelegation, Integer idAffectation) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_AFFECTATION + "=? and " + CHAMP_ID_DELEGATION
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idAffectation, idDelegation });
	}

	@Override
	public DelegationAFF chercherDelegationAFF(Integer idDelegation, Integer idAffectation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AFFECTATION + " = ? and "
				+ CHAMP_ID_DELEGATION + "=?";
		DelegationAFF c = (DelegationAFF) jdbcTemplate.queryForObject(sql,
				new Object[] { idAffectation, idDelegation }, new BeanPropertyRowMapper<DelegationAFF>(
						DelegationAFF.class));
		return c;
	}

}