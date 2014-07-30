package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.ActiviteFE;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ActiviteFEDao extends SirhDao implements ActiviteFEDaoInterface {

	public static final String CHAMP_ID_ACTIVITE = "ID_ACTIVITE";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public ActiviteFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ACTIVITE_FE";
	}

	@Override
	public ArrayList<ActiviteFE> listerActiviteFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=?  ";

		ArrayList<ActiviteFE> liste = new ArrayList<ActiviteFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			ActiviteFE a = new ActiviteFE();
			a.setIdActivite((Integer) row.get(CHAMP_ID_ACTIVITE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<ActiviteFE> listerActiviteFEAvecActivite(Integer idActivite) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ACTIVITE + "=?  ";

		ArrayList<ActiviteFE> liste = new ArrayList<ActiviteFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idActivite });
		for (Map<String, Object> row : rows) {
			ActiviteFE a = new ActiviteFE();
			a.setIdActivite((Integer) row.get(CHAMP_ID_ACTIVITE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ActiviteFE chercherActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + " = ? and " + CHAMP_ID_ACTIVITE
				+ "=?";
		ActiviteFE cadre = (ActiviteFE) jdbcTemplate.queryForObject(sql, new Object[] { idFicheEmploi, idActivite },
				new BeanPropertyRowMapper<ActiviteFE>(ActiviteFE.class));
		return cadre;
	}

	@Override
	public void creerActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_ACTIVITE + "," + CHAMP_ID_FICHE_EMPLOI + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idActivite, idFicheEmploi });
	}

	@Override
	public void supprimerActiviteFE(Integer idFicheEmploi, Integer idActivite) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_ACTIVITE + "=? and " + CHAMP_ID_FICHE_EMPLOI
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idActivite, idFicheEmploi });
	}

	@Override
	public void supprimerActiviteFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}
}
