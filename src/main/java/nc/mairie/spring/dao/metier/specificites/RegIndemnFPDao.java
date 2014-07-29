package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.spring.dao.utils.SirhDao;

public class RegIndemnFPDao extends SirhDao implements RegIndemnFPDaoInterface {

	public static final String CHAMP_ID_REGIME = "ID_REGIME";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public RegIndemnFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "REG_INDEMN_FP";
	}

	@Override
	public void creerRegIndemFP(Integer idRegime, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_REGIME + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idRegime, idFichePoste });
	}

	@Override
	public void supprimerRegIndemFP(Integer idRegime, Integer idFichePoste) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_REGIME + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idRegime, idFichePoste });
	}

}