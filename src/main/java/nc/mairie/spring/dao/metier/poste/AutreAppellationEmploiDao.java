package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.AutreAppellationEmploi;
import nc.mairie.spring.dao.SirhDao;

public class AutreAppellationEmploiDao extends SirhDao implements AutreAppellationEmploiDaoInterface {

	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";
	public static final String CHAMP_LIB_AUTRE_APPELLATION_EMPLOI = "LIB_AUTRE_APPELLATION_EMPLOI";

	public AutreAppellationEmploiDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AUTRE_APPELLATION_EMPLOI";
		super.CHAMP_ID = "ID_AUTRE_APPELLATION_EMPLOI";
	}

	@Override
	public void supprimerAutreAppellationEmploiAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? ";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}

	@Override
	public void supprimerAutreAppellationEmploi(Integer idAutreAppellationEmploi) throws Exception {
		super.supprimerObject(idAutreAppellationEmploi);
	}

	@Override
	public void creerAutreAppellationEmploi(Integer idFicheEmploi, String libAutreAppellationEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_EMPLOI + ","
				+ CHAMP_LIB_AUTRE_APPELLATION_EMPLOI + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, libAutreAppellationEmploi });
	}

	@Override
	public ArrayList<AutreAppellationEmploi> listerAutreAppellationEmploiAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<AutreAppellationEmploi> liste = new ArrayList<AutreAppellationEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			AutreAppellationEmploi a = new AutreAppellationEmploi();
			a.setIdAutreAppellationEmploi((Integer) row.get(CHAMP_ID));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			a.setLibAutreAppellationEmploi((String) row.get(CHAMP_LIB_AUTRE_APPELLATION_EMPLOI));
			liste.add(a);
		}

		return liste;
	}
}
