package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.NiveauEtudeFP;
import nc.mairie.spring.dao.utils.SirhDao;

public class NiveauEtudeFPDao extends SirhDao implements NiveauEtudeFPDaoInterface {

	public static final String CHAMP_ID_NIVEAU_ETUDE = "ID_NIVEAU_ETUDE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public NiveauEtudeFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "NIVEAU_ETUDE_FP";
	}

	@Override
	public ArrayList<NiveauEtudeFP> listerNiveauEtudeFPAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where  " + CHAMP_ID_FICHE_POSTE + "=?";

		ArrayList<NiveauEtudeFP> liste = new ArrayList<NiveauEtudeFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			NiveauEtudeFP a = new NiveauEtudeFP();
			a.setIdNiveauEtude((Integer) row.get(CHAMP_ID_NIVEAU_ETUDE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void creerNiveauEtudeFP(Integer idNiveau, Integer idFichePoste) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_NIVEAU_ETUDE + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idNiveau, idFichePoste });
	}

	@Override
	public void supprimerNiveauEtudeFP(Integer idNiveau, Integer idFichePoste) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_NIVEAU_ETUDE + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idNiveau, idFichePoste });
	}
}
