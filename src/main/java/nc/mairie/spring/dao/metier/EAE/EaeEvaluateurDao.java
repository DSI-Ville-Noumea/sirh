package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeEvaluateurDao implements EaeEvaluateurDaoInterface {

	public static final String NOM_TABLE = "EAE_EVALUATEUR";

	public static final String NOM_SEQUENCE = "EAE_S_EVALUATEUR";

	public static final String CHAMP_ID_EAE_EVALUATEUR = "ID_EAE_EVALUATEUR";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_FONCTION = "FONCTION";
	public static final String CHAMP_DATE_ENTREE_SERVICE = "DATE_ENTREE_SERVICE";
	public static final String CHAMP_DATE_ENTREE_COLLECTIVITE = "DATE_ENTREE_COLLECTIVITE";
	public static final String CHAMP_DATE_ENTREE_FONCTION = "DATE_ENTREE_FONCTION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeEvaluateurDao() {

	}

	@Override
	public void creerEaeEvaluateur(Integer idEae, Integer idAgent, String fonction, Date dateEntreeService, Date dateEntreeCollectivite,
			Date dateEntreeFonction) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_EVALUATEUR + "," + CHAMP_ID_EAE + "," + CHAMP_ID_AGENT + "," + CHAMP_FONCTION
				+ "," + CHAMP_DATE_ENTREE_SERVICE + "," + CHAMP_DATE_ENTREE_COLLECTIVITE + "," + CHAMP_DATE_ENTREE_FONCTION + ") VALUES ("
				+ NOM_SEQUENCE + ".nextval,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idAgent, fonction, dateEntreeService, dateEntreeCollectivite, dateEntreeFonction });
	}

	@Override
	public ArrayList<EaeEvaluateur> listerEvaluateurEAE(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeEvaluateur> listeEaeEvaluateur = new ArrayList<EaeEvaluateur>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map row : rows) {
			EaeEvaluateur eval = new EaeEvaluateur();
			// logger.debug("List evaluateurs : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_EVALUATEUR);
			eval.setIdEaeEvaluateur(id.intValue());
			BigDecimal idEae = (BigDecimal) row.get(CHAMP_ID_EAE);
			eval.setIdEae(idEae.intValue());
			BigDecimal idAgent = (BigDecimal) row.get(CHAMP_ID_AGENT);
			eval.setIdAgent(idAgent.intValue());
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
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_EVALUATEUR + "=?";
		jdbcTemplate.update(sql, new Object[] { idEvaluateur });
	}

}
