package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.metier.eae.EaeFDPActivite;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFDPActiviteDao implements EaeFDPActiviteDaoInterface {

	public static final String NOM_TABLE = "EAE_FDP_ACTIVITE";

	public static final String CHAMP_ID_EAE_FDP_ACTIVITE = "ID_EAE_FDP_ACTIVITE";
	public static final String CHAMP_ID_EAE_FICHE_POSTE = "ID_EAE_FICHE_POSTE";
	public static final String CHAMP_LIBELLE_ACTIVITE = "LIBELLE_ACTIVITE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeFDPActiviteDao() {

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
			// logger.debug("List activites : " + row.toString());
			acti.setIdEaeFDPActivite((Integer) row.get(CHAMP_ID_EAE_FDP_ACTIVITE));
			acti.setIdEaeFDP((Integer) row.get(CHAMP_ID_EAE_FICHE_POSTE));
			acti.setLibActivite((String) row.get(CHAMP_LIBELLE_ACTIVITE));
			listeEaeFDPActivite.add(acti);
		}

		return listeEaeFDPActivite;
	}

	@Override
	public void supprimerEaeFDPActivite(Integer idEaeFDPActivite) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EAE_FDP_ACTIVITE + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaeFDPActivite });
	}
}
