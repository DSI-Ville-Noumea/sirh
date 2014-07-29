package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeFDPActivite;
import nc.mairie.spring.dao.utils.EaeDao;

public class EaeFDPActiviteDao extends EaeDao implements EaeFDPActiviteDaoInterface {

	public static final String CHAMP_ID_EAE_FICHE_POSTE = "ID_EAE_FICHE_POSTE";
	public static final String CHAMP_LIBELLE_ACTIVITE = "LIBELLE_ACTIVITE";

	public EaeFDPActiviteDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_FDP_ACTIVITE";
		super.CHAMP_ID = "ID_EAE_FDP_ACTIVITE";
	}

	@Override
	public void creerEaeFDPActivite(Integer idEaeFichePoste, String libActi) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FICHE_POSTE + "," + CHAMP_LIBELLE_ACTIVITE + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeFichePoste, libActi });
	}

	@Override
	public ArrayList<EaeFDPActivite> listerEaeFDPActivite(Integer idEaeFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";

		ArrayList<EaeFDPActivite> listeEaeFDPActivite = new ArrayList<EaeFDPActivite>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEaeFichePoste });
		for (Map<String, Object> row : rows) {
			EaeFDPActivite acti = new EaeFDPActivite();
			acti.setIdEaeFdpActivite((Integer) row.get(CHAMP_ID));
			acti.setIdEaeFichePoste((Integer) row.get(CHAMP_ID_EAE_FICHE_POSTE));
			acti.setLibelleActivite((String) row.get(CHAMP_LIBELLE_ACTIVITE));
			listeEaeFDPActivite.add(acti);
		}

		return listeEaeFDPActivite;
	}

	@Override
	public void supprimerEaeFDPActivite(Integer idEaeFDPActivite) throws Exception {
		super.supprimerObject(idEaeFDPActivite);
	}
}
