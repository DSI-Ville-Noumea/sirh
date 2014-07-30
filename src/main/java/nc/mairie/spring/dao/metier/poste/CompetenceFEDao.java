package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CompetenceFEDao extends SirhDao implements CompetenceFEDaoInterface {

	public static final String CHAMP_ID_COMPETENCE = "ID_COMPETENCE";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public CompetenceFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "COMPETENCE_FE";
	}

	@Override
	public ArrayList<CompetenceFE> listerCompetenceFEAvecCompetence(Integer idCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_COMPETENCE + "=? ";

		ArrayList<CompetenceFE> liste = new ArrayList<CompetenceFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCompetence });
		for (Map<String, Object> row : rows) {
			CompetenceFE a = new CompetenceFE();
			a.setIdCompetence((Integer) row.get(CHAMP_ID_COMPETENCE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<CompetenceFE> listerCompetenceFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<CompetenceFE> liste = new ArrayList<CompetenceFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			CompetenceFE a = new CompetenceFE();
			a.setIdCompetence((Integer) row.get(CHAMP_ID_COMPETENCE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_COMPETENCE + "=? and " + CHAMP_ID_FICHE_EMPLOI
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idCompetence, idFicheEmploi });
	}

	@Override
	public void creerCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_COMPETENCE + "," + CHAMP_ID_FICHE_EMPLOI + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idCompetence, idFicheEmploi });
	}

	@Override
	public CompetenceFE chercherCompetenceFE(Integer idFicheEmploi, Integer idCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + " = ? and "
				+ CHAMP_ID_COMPETENCE + "=?";
		CompetenceFE cadre = (CompetenceFE) jdbcTemplate.queryForObject(sql,
				new Object[] { idFicheEmploi, idCompetence }, new BeanPropertyRowMapper<CompetenceFE>(
						CompetenceFE.class));
		return cadre;
	}

	@Override
	public ArrayList<CompetenceFE> listerCompetenceFEAvecFEEtTypeComp(Integer idFicheEmploi, Integer idCompetence)
			throws Exception {
		String sql = "select lien.* from " + NOM_TABLE + "  lien inner join COMPETENCE comp on lien."
				+ CHAMP_ID_COMPETENCE + " = comp.ID_COMPETENCE WHERE comp.ID_TYPE_COMPETENCE= ? and lien."
				+ CHAMP_ID_FICHE_EMPLOI + "=?";

		ArrayList<CompetenceFE> liste = new ArrayList<CompetenceFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCompetence, idFicheEmploi });
		for (Map<String, Object> row : rows) {
			CompetenceFE a = new CompetenceFE();
			a.setIdCompetence((Integer) row.get(CHAMP_ID_COMPETENCE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}
}
