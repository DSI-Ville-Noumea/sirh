package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CompetenceFPDao extends SirhDao implements CompetenceFPDaoInterface {

	public static final String CHAMP_ID_COMPETENCE = "ID_COMPETENCE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public CompetenceFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "COMPETENCE_FP";
	}

	@Override
	public ArrayList<CompetenceFP> listerCompetenceFPAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select f.* from " + NOM_TABLE + " f inner join COMPETENCE c on c.id_competence=f."
				+ CHAMP_ID_COMPETENCE + " where " + CHAMP_ID_FICHE_POSTE
				+ "=? order by c.id_type_competence, c.nom_competence ";

		ArrayList<CompetenceFP> liste = new ArrayList<CompetenceFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			CompetenceFP a = new CompetenceFP();
			a.setIdCompetence((Integer) row.get(CHAMP_ID_COMPETENCE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<CompetenceFP> listerCompetenceFPAvecCompetence(Integer idCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_COMPETENCE + "=? ";

		ArrayList<CompetenceFP> liste = new ArrayList<CompetenceFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCompetence });
		for (Map<String, Object> row : rows) {
			CompetenceFP a = new CompetenceFP();
			a.setIdCompetence((Integer) row.get(CHAMP_ID_COMPETENCE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_COMPETENCE + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idCompetence, idFichePoste });
	}

	@Override
	public void creerCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_COMPETENCE + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idCompetence, idFichePoste });
	}

	@Override
	public CompetenceFP chercherCompetenceFP(Integer idFichePoste, Integer idCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? and "
				+ CHAMP_ID_COMPETENCE + "=?";
		CompetenceFP cadre = (CompetenceFP) jdbcTemplate.queryForObject(sql,
				new Object[] { idFichePoste, idCompetence },
				new BeanPropertyRowMapper<CompetenceFP>(CompetenceFP.class));
		return cadre;
	}
}
