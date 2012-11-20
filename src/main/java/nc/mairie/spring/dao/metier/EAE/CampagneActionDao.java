package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.CampagneActionRowMapper;
import nc.mairie.spring.domain.metier.EAE.CampagneAction;

import org.springframework.jdbc.core.JdbcTemplate;

public class CampagneActionDao implements CampagneActionDaoInterface {

	private static Logger logger = Logger.getLogger(CampagneActionDao.class.getName());

	public static final String NOM_TABLE = "EAE_CAMPAGNE_ACTION";

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_ACTION";

	public static final String CHAMP_ID_CAMPAGNE_ACTION = "ID_CAMPAGNE_ACTION";
	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_NOM_ACTION = "NOM_ACTION";
	public static final String CHAMP_MESSAGE = "MESSAGE";
	public static final String CHAMP_DATE_TRANSMISSION = "DATE_TRANSMISSION";
	public static final String CHAMP_DIFFUSE = "DIFFUSE";
	public static final String CHAMP_DATE_A_FAIRE_LE = "DATE_A_FAIRE_LE";
	public static final String CHAMP_DATE_FAIT_LE = "DATE_FAIT_LE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";
	public static final String CHAMP_ID_AGENT_REALISATION = "ID_AGENT_REALISATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CampagneActionDao() {

	}

	@Override
	public ArrayList<CampagneAction> listerCampagneAction() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<CampagneAction> listeCampagneAction = new ArrayList<CampagneAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			CampagneAction camp = new CampagneAction();
			logger.info("List campagne Action : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_ACTION);
			camp.setIdCampagneAction(id.intValue());
			BigDecimal idCamp = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			camp.setIdCampagneEAE(idCamp.intValue());
			camp.setNomAction((String) row.get(CHAMP_NOM_ACTION));
			camp.setMessage((String) row.get(CHAMP_MESSAGE));
			camp.setDateTransmission((Date) row.get(CHAMP_DATE_TRANSMISSION));
			BigDecimal diffuse = (BigDecimal) row.get(CHAMP_DIFFUSE);
			camp.setDiffuse(diffuse.intValue() == 0 ? false : true);
			camp.setDateAFaireLe((Date) row.get(CHAMP_DATE_A_FAIRE_LE));
			camp.setDateFaitLe((Date) row.get(CHAMP_DATE_FAIT_LE));
			camp.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			BigDecimal idAgent = (BigDecimal) row.get(CHAMP_ID_AGENT_REALISATION);
			camp.setIdAgentRealisation(idAgent.intValue());
			listeCampagneAction.add(camp);
		}

		return listeCampagneAction;
	}

	@Override
	public CampagneAction chercherCampagneAction(Integer idCampagneAction) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_ACTION + " = ? ";

		@SuppressWarnings("unchecked")
		CampagneAction camp = (CampagneAction) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneAction }, new CampagneActionRowMapper());

		return camp;
	}

	@Override
	public Integer creerCampagneAction(String nomAction, String message, Date transmettreLe, boolean diffuse, Date pourLe, Date faitLe,
			String commentaire, Integer idAgentRealisation, Integer idCampagneEAE) throws Exception {

		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_ACTION + "," + CHAMP_NOM_ACTION + "," + CHAMP_MESSAGE + ","
				+ CHAMP_DATE_TRANSMISSION + "," + CHAMP_DIFFUSE + "," + CHAMP_DATE_A_FAIRE_LE + "," + CHAMP_DATE_FAIT_LE + "," + CHAMP_COMMENTAIRE
				+ "," + CHAMP_ID_AGENT_REALISATION + "," + CHAMP_ID_CAMPAGNE_EAE + ") VALUES (?,?, ?, ?, ?,?, ?, ?, ?,?)";

		Integer diffuseAction = diffuse ? 1 : 0;
		jdbcTemplate.update(sql, new Object[] { id, nomAction, message, transmettreLe, diffuseAction, pourLe, faitLe, commentaire,
				idAgentRealisation, idCampagneEAE });

		return id;
	}

	@Override
	public ArrayList<CampagneAction> listerCampagneActionPourCampagne(Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<CampagneAction> listeCampagneAction = new ArrayList<CampagneAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEAE });
		for (Map row : rows) {
			CampagneAction camp = new CampagneAction();
			logger.info("List campagne Action : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_ACTION);
			camp.setIdCampagneAction(id.intValue());
			BigDecimal idCamp = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			camp.setIdCampagneEAE(idCamp.intValue());
			camp.setNomAction((String) row.get(CHAMP_NOM_ACTION));
			camp.setMessage((String) row.get(CHAMP_MESSAGE));
			camp.setDateTransmission((Date) row.get(CHAMP_DATE_TRANSMISSION));
			BigDecimal diffuse = (BigDecimal) row.get(CHAMP_DIFFUSE);
			camp.setDiffuse(diffuse.intValue() == 0 ? false : true);
			camp.setDateAFaireLe((Date) row.get(CHAMP_DATE_A_FAIRE_LE));
			camp.setDateFaitLe((Date) row.get(CHAMP_DATE_FAIT_LE));
			camp.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			BigDecimal idAgent = (BigDecimal) row.get(CHAMP_ID_AGENT_REALISATION);
			camp.setIdAgentRealisation(idAgent.intValue());
			listeCampagneAction.add(camp);
		}

		return listeCampagneAction;
	}

	@Override
	public void modifierCampagneAction(Integer idCampagneAction, String nomAction, String message, Date dateTransmission, Date dateAFaireLe,
			Date dateFaitLe, String commentaire, Integer idAgentRealisation) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_NOM_ACTION + " =?," + CHAMP_MESSAGE + "=?," + CHAMP_DATE_TRANSMISSION + "=?,"
				+ CHAMP_DATE_A_FAIRE_LE + "=?," + CHAMP_DATE_FAIT_LE + "=?," + CHAMP_COMMENTAIRE + "=?," + CHAMP_ID_AGENT_REALISATION + "=? where "
				+ CHAMP_ID_CAMPAGNE_ACTION + "=?";
		jdbcTemplate.update(sql, new Object[] { nomAction, message, dateTransmission, dateAFaireLe, dateFaitLe, commentaire, idAgentRealisation,
				idCampagneAction });

	}

	@Override
	public void supprimerCampagneAction(Integer idCampagneAction) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_CAMPAGNE_ACTION + "=?";
		jdbcTemplate.update(sql, new Object[] { idCampagneAction });
	}
}
