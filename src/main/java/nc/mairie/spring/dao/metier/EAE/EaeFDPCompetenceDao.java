package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeFDPCompetence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFDPCompetenceDao implements EaeFDPCompetenceDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaeFDPCompetenceDao.class);

	public static final String NOM_TABLE = "EAE_FDP_COMPETENCE";

	public static final String NOM_SEQUENCE = "EAE_S_FDP_COMPETENCE";

	public static final String CHAMP_ID_EAE_FDP_COMPETENCE = "ID_EAE_FDP_COMPETENCE";
	public static final String CHAMP_ID_EAE_FICHE_POSTE = "ID_EAE_FICHE_POSTE";
	public static final String CHAMP_TYPE_COMPETENCE = "TYPE_COMPETENCE";
	public static final String CHAMP_LIBELLE_COMPETENCE = "LIBELLE_COMPETENCE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeFDPCompetenceDao() {

	}

	@Override
	public void creerEaeFDPCompetence(Integer idEaeFichePoste, String typeComp, String libComp) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FDP_COMPETENCE + "," + CHAMP_ID_EAE_FICHE_POSTE + "," + CHAMP_TYPE_COMPETENCE
				+ "," + CHAMP_LIBELLE_COMPETENCE + ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeFichePoste, typeComp, libComp });
	}

	@Override
	public ArrayList<EaeFDPCompetence> listerEaeFDPCompetence(Integer idEaeFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";

		ArrayList<EaeFDPCompetence> listeEaeFDPComp = new ArrayList<EaeFDPCompetence>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEaeFichePoste });
		for (Map row : rows) {
			EaeFDPCompetence comp = new EaeFDPCompetence();
			// logger.debug("List competences : " + row.toString());
			BigDecimal idComp = (BigDecimal) row.get(CHAMP_ID_EAE_FDP_COMPETENCE);
			comp.setIdEaeFDPCompetence(idComp.intValue());
			BigDecimal idFDP = (BigDecimal) row.get(CHAMP_ID_EAE_FICHE_POSTE);
			comp.setIdEaeFDP(idFDP.intValue());
			comp.setTypeCompetence((String) row.get(CHAMP_TYPE_COMPETENCE));
			comp.setLibCompetence((String) row.get(CHAMP_LIBELLE_COMPETENCE));
			listeEaeFDPComp.add(comp);
		}

		return listeEaeFDPComp;
	}

	@Override
	public void supprimerEaeFDPCompetence(Integer idEaeFDPComp) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EAE_FDP_COMPETENCE + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaeFDPComp });
	}
}
