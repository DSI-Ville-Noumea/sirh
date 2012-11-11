package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeFDPActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFDPActiviteDao implements EaeFDPActiviteDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaeFDPActiviteDao.class);

	public static final String NOM_TABLE = "EAE_FDP_ACTIVITE";

	public static final String NOM_SEQUENCE = "EAE_S_FDP_ACTIVITE";

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

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FDP_ACTIVITE + "," + CHAMP_ID_EAE_FICHE_POSTE + "," + CHAMP_LIBELLE_ACTIVITE
				+ ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeFichePoste, libActi });
	}

	@Override
	public ArrayList<EaeFDPActivite> listerEaeFDPActivite(Integer idEaeFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";

		ArrayList<EaeFDPActivite> listeEaeFDPActivite = new ArrayList<EaeFDPActivite>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEaeFichePoste });
		for (Map row : rows) {
			EaeFDPActivite acti = new EaeFDPActivite();
			// logger.debug("List activites : " + row.toString());
			BigDecimal idActi = (BigDecimal) row.get(CHAMP_ID_EAE_FDP_ACTIVITE);
			acti.setIdEaeFDPActivite(idActi.intValue());
			BigDecimal idFDP = (BigDecimal) row.get(CHAMP_ID_EAE_FICHE_POSTE);
			acti.setIdEaeFDP(idFDP.intValue());
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
