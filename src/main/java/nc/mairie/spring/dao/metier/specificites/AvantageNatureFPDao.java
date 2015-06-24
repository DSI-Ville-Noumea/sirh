package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.AvantageNatureFP;
import nc.mairie.spring.dao.utils.SirhDao;

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

	@Override
	public ArrayList<AvantageNatureFP> listerAvantageNatureFPAvecFP(Integer idFichePoste) {
		String sql = "select f.* from " + NOM_TABLE + " f where " + CHAMP_ID_FICHE_POSTE + "=? ";

		ArrayList<AvantageNatureFP> liste = new ArrayList<AvantageNatureFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			AvantageNatureFP a = new AvantageNatureFP();
			a.setIdAvantage((Integer) row.get(CHAMP_ID_AVANTAGE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			liste.add(a);
		}

		return liste;
	}

}