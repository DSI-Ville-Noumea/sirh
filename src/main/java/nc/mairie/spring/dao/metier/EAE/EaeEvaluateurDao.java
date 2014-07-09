package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeEvaluateur;
import nc.mairie.spring.dao.EaeDao;

public class EaeEvaluateurDao extends EaeDao implements EaeEvaluateurDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_FONCTION = "FONCTION";
	public static final String CHAMP_DATE_ENTREE_SERVICE = "DATE_ENTREE_SERVICE";
	public static final String CHAMP_DATE_ENTREE_COLLECTIVITE = "DATE_ENTREE_COLLECTIVITE";
	public static final String CHAMP_DATE_ENTREE_FONCTION = "DATE_ENTREE_FONCTION";

	public EaeEvaluateurDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_EVALUATEUR";
		super.CHAMP_ID = "ID_EAE_EVALUATEUR";
	}

	@Override
	public void creerEaeEvaluateur(Integer idEae, Integer idAgent, String fonction, Date dateEntreeService,
			Date dateEntreeCollectivite, Date dateEntreeFonction) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_ID_AGENT + "," + CHAMP_FONCTION
				+ "," + CHAMP_DATE_ENTREE_SERVICE + "," + CHAMP_DATE_ENTREE_COLLECTIVITE + ","
				+ CHAMP_DATE_ENTREE_FONCTION + ") VALUES (?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idAgent, fonction, dateEntreeService, dateEntreeCollectivite,
				dateEntreeFonction });
	}

	@Override
	public ArrayList<EaeEvaluateur> listerEvaluateurEAE(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeEvaluateur> listeEaeEvaluateur = new ArrayList<EaeEvaluateur>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeEvaluateur eval = new EaeEvaluateur();
			eval.setIdEaeEvaluateur((Integer) row.get(CHAMP_ID));
			eval.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			eval.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			eval.setFonction((String) row.get(CHAMP_FONCTION));
			eval.setDateEntreeService((Date) row.get(CHAMP_DATE_ENTREE_SERVICE));
			eval.setDateEntreeCollectivite((Date) row.get(CHAMP_DATE_ENTREE_COLLECTIVITE));
			eval.setDateEntreeFonction((Date) row.get(CHAMP_DATE_ENTREE_FONCTION));
			listeEaeEvaluateur.add(eval);
		}
		return listeEaeEvaluateur;
	}

	@Override
	public void supprimerEaeEvaluateur(Integer idEvaluateur) throws Exception {
		super.supprimerObject(idEvaluateur);
	}

}
