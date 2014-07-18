package nc.mairie.spring.dao.metier.specificites;

import nc.mairie.spring.dao.SirhDao;

public class AvantageNatureFPDao extends SirhDao implements AvantageNatureFPDaoInterface {

	public static final String CHAMP_ID_AVANTAGE = "ID_AVANTAGE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public AvantageNatureFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVANTAGE_NATURE_FP";
	}

	@Override
	public void creerAvantageNatureFP(Integer idAvantage, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AVANTAGE + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idAvantage, idFichePoste });
	}

	@Override
	public void supprimerAvantageNatureFP(Integer idAvantage, Integer idFichePoste) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_POSTE + "=? and " + CHAMP_ID_AVANTAGE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idFichePoste, idAvantage });
	}

}