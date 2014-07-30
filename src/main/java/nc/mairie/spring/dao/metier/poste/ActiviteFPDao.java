package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.ActiviteFP;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ActiviteFPDao extends SirhDao implements ActiviteFPDaoInterface {

	public static final String CHAMP_ID_ACTIVITE = "ID_ACTIVITE";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_ACTIVITE_PRINCIPALE = "ACTIVITE_PRINCIPALE";

	public ActiviteFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ACTIVITE_FP";
	}

	@Override
	public ActiviteFP chercherActiviteFP(Integer idFichePoste, Integer idActivite) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? and " + CHAMP_ID_ACTIVITE
				+ "=?";
		ActiviteFP cadre = (ActiviteFP) jdbcTemplate.queryForObject(sql, new Object[] { idFichePoste, idActivite },
				new BeanPropertyRowMapper<ActiviteFP>(ActiviteFP.class));
		return cadre;
	}

	@Override
	public void creerActiviteFP(Integer idFichePoste, Integer idActivite, boolean principale) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_ACTIVITE + "," + CHAMP_ID_FICHE_POSTE + ","
				+ CHAMP_ACTIVITE_PRINCIPALE + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idActivite, idFichePoste, principale ? 1 : 0 });
	}

	@Override
	public void supprimerActiviteFP(Integer idFichePoste, Integer idActivite, boolean principale) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_ACTIVITE + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=? and " + CHAMP_ACTIVITE_PRINCIPALE + "=?";
		jdbcTemplate.update(sql, new Object[] { idActivite, idFichePoste, principale ? 1 : 0 });
	}

	@Override
	public ArrayList<ActiviteFP> listerActiviteFPAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select f.* from " + NOM_TABLE + " f inner join activite a on a.id_activite= f."
				+ CHAMP_ID_ACTIVITE + " where " + CHAMP_ID_FICHE_POSTE + "=? order by a.nom_activite ";

		ArrayList<ActiviteFP> liste = new ArrayList<ActiviteFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			ActiviteFP a = new ActiviteFP();
			a.setIdActivite((Integer) row.get(CHAMP_ID_ACTIVITE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			Integer principale = (Integer) row.get(CHAMP_ACTIVITE_PRINCIPALE);
			a.setActivitePrincipale(principale == 1 ? true : false);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<ActiviteFP> listerActiviteFPAvecActivite(Integer idActivite) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ACTIVITE + "=?  ";

		ArrayList<ActiviteFP> liste = new ArrayList<ActiviteFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idActivite });
		for (Map<String, Object> row : rows) {
			ActiviteFP a = new ActiviteFP();
			a.setIdActivite((Integer) row.get(CHAMP_ID_ACTIVITE));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			Integer principale = (Integer) row.get(CHAMP_ACTIVITE_PRINCIPALE);
			a.setActivitePrincipale(principale == 1 ? true : false);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void modifierActiviteFP(Integer idFichePoste, Integer idActivite, boolean principale) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ACTIVITE_PRINCIPALE + "=? where " + CHAMP_ID_ACTIVITE
				+ " =? and " + CHAMP_ID_FICHE_POSTE + "=?";
		jdbcTemplate.update(sql, new Object[] { principale ? 1 : 0, idActivite, idFichePoste });
	}
}
