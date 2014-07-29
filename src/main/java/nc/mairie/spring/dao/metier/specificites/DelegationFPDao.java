package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.spring.dao.utils.SirhDao;

public class DelegationFPDao extends SirhDao implements DelegationFPDaoInterface {

	public static final String CHAMP_ID_DELEGATION = "ID_DELEGATION";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public DelegationFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DELEGATION_FP";
	}

	@Override
	public void creerDelegationFP(Integer idDelegation, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_DELEGATION + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idDelegation, idFichePoste });
	}

	@Override
	public void supprimerDelegationFP(Integer idDelegation, Integer idFichePoste) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_DELEGATION + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idDelegation, idFichePoste });
	}

}