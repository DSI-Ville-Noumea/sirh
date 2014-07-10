package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeFinalisation;
import nc.mairie.spring.dao.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EaeFinalisationDao extends EaeDao implements EaeFinalisationDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_DATE_FINALISATION = "DATE_FINALISATION";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_GED_DOCUMENT = "ID_GED_DOCUMENT";
	public static final String CHAMP_VERSION_GED_DOCUMENT = "VERSION_GED_DOCUMENT";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";

	private Logger logger = LoggerFactory.getLogger(EaeFinalisationDao.class);

	public EaeFinalisationDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_FINALISATION";
		super.CHAMP_ID = "ID_EAE_FINALISATION";
	}

	@Override
	public String chercherDernierDocumentFinalise(Integer idEAE) throws Exception {
		String sql = "select " + CHAMP_ID_GED_DOCUMENT + " from " + NOM_TABLE + " where " + CHAMP_ID_EAE
				+ " = ? order by " + CHAMP_DATE_FINALISATION + " desc";

		String finalisation = null;
		try {
			finalisation = (String) jdbcTemplate.queryForList(sql, new Object[] { idEAE }, String.class).get(0);

		} catch (Exception e) {
			logger.error("Erreur dans la recherche du document finalise : ", e);
		}
		return finalisation;
	}

	@Override
	public ArrayList<EaeFinalisation> listerDocumentFinalise(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? order by "
				+ CHAMP_VERSION_GED_DOCUMENT + " desc ";

		ArrayList<EaeFinalisation> listeEaeFinalisation = new ArrayList<EaeFinalisation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeFinalisation dev = new EaeFinalisation();
			dev.setIdEaeFinalisation((Integer) row.get(CHAMP_ID));
			dev.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			dev.setDateFinalisation((Date) row.get(CHAMP_DATE_FINALISATION));
			dev.setIdGedDocument((String) row.get(CHAMP_ID_GED_DOCUMENT));
			dev.setVersionGedDocument((String) row.get(CHAMP_VERSION_GED_DOCUMENT));
			dev.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));

			listeEaeFinalisation.add(dev);
		}
		return listeEaeFinalisation;
	}
}
