package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.NiveauEtudeFE;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class NiveauEtudeFEDao extends SirhDao implements NiveauEtudeFEDaoInterface {

	public static final String CHAMP_ID_NIVEAU_ETUDE = "ID_NIVEAU_ETUDE";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public NiveauEtudeFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "NIVEAU_ETUDE_FE";
	}

	@Override
	public ArrayList<NiveauEtudeFE> listerNiveauEtudeFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where  " + CHAMP_ID_FICHE_EMPLOI + "=?";

		ArrayList<NiveauEtudeFE> liste = new ArrayList<NiveauEtudeFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			NiveauEtudeFE a = new NiveauEtudeFE();
			a.setIdNiveauEtude((Integer) row.get(CHAMP_ID_NIVEAU_ETUDE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public NiveauEtudeFE chercherNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_NIVEAU_ETUDE + " = ? and "
				+ CHAMP_ID_FICHE_EMPLOI + "=?";
		NiveauEtudeFE cadre = (NiveauEtudeFE) jdbcTemplate
				.queryForObject(sql, new Object[] { idNiveau, idFicheEmploi },
						new BeanPropertyRowMapper<NiveauEtudeFE>(NiveauEtudeFE.class));
		return cadre;
	}

	@Override
	public void creerNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_NIVEAU_ETUDE + "," + CHAMP_ID_FICHE_EMPLOI + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idNiveau, idFicheEmploi });
	}

	@Override
	public void supprimerNiveauEtudeFE(Integer idNiveau, Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_NIVEAU_ETUDE + "=? and "
				+ CHAMP_ID_FICHE_EMPLOI + "=?";
		jdbcTemplate.update(sql, new Object[] { idNiveau, idFicheEmploi });
	}

	@Override
	public void supprimerNiveauEtudeFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}
}
