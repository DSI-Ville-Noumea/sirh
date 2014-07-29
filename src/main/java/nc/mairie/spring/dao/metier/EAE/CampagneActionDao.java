package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.CampagneAction;
import nc.mairie.spring.dao.utils.EaeDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CampagneActionDao extends EaeDao implements CampagneActionDaoInterface {

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_ACTION";

	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_NOM_ACTION = "NOM_ACTION";
	public static final String CHAMP_MESSAGE = "MESSAGE";
	public static final String CHAMP_DATE_TRANSMISSION = "DATE_TRANSMISSION";
	public static final String CHAMP_DATE_A_FAIRE_LE = "DATE_A_FAIRE_LE";
	public static final String CHAMP_DATE_FAIT_LE = "DATE_FAIT_LE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";
	public static final String CHAMP_ID_AGENT_REALISATION = "ID_AGENT_REALISATION";

	public CampagneActionDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_CAMPAGNE_ACTION";
		super.CHAMP_ID = "ID_CAMPAGNE_ACTION";
	}

	@Override
	public CampagneAction chercherCampagneAction(Integer idCampagneAction) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + " = ? ";

		CampagneAction camp = (CampagneAction) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneAction },
				new BeanPropertyRowMapper<CampagneAction>(CampagneAction.class));

		return camp;
	}

	@Override
	public Integer creerCampagneAction(String nomAction, String message, Date transmettreLe, Date pourLe, Date faitLe,
			String commentaire, Integer idAgentRealisation, Integer idCampagneEAE) throws Exception {

		String sqlClePrimaire = "select nextval('" + NOM_SEQUENCE + "')";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_NOM_ACTION + "," + CHAMP_MESSAGE + ","
				+ CHAMP_DATE_TRANSMISSION + "," + CHAMP_DATE_A_FAIRE_LE + "," + CHAMP_DATE_FAIT_LE + ","
				+ CHAMP_COMMENTAIRE + "," + CHAMP_ID_AGENT_REALISATION + "," + CHAMP_ID_CAMPAGNE_EAE
				+ ") VALUES (?,?, ?, ?, ?,?, ?, ?, ?)";

		jdbcTemplate.update(sql, new Object[] { id, nomAction, message, transmettreLe, pourLe, faitLe, commentaire,
				idAgentRealisation, idCampagneEAE });

		return id;
	}

	@Override
	public ArrayList<CampagneAction> listerCampagneActionPourCampagne(Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<CampagneAction> listeCampagneAction = new ArrayList<CampagneAction>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEAE });
		for (Map<String, Object> row : rows) {
			CampagneAction camp = new CampagneAction();
			camp.setIdCampagneAction((Integer) row.get(CHAMP_ID));
			camp.setIdCampagneEae((Integer) row.get(CHAMP_ID_CAMPAGNE_EAE));
			camp.setNomAction((String) row.get(CHAMP_NOM_ACTION));
			camp.setMessage((String) row.get(CHAMP_MESSAGE));
			camp.setDateTransmission((Date) row.get(CHAMP_DATE_TRANSMISSION));
			camp.setDateAFaireLe((Date) row.get(CHAMP_DATE_A_FAIRE_LE));
			camp.setDateFaitLe((Date) row.get(CHAMP_DATE_FAIT_LE));
			camp.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			camp.setIdAgentRealisation((Integer) row.get(CHAMP_ID_AGENT_REALISATION));
			listeCampagneAction.add(camp);
		}

		return listeCampagneAction;
	}

	@Override
	public void modifierCampagneAction(Integer idCampagneAction, String nomAction, String message,
			Date dateTransmission, Date dateAFaireLe, Date dateFaitLe, String commentaire, Integer idAgentRealisation)
			throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_NOM_ACTION + " =?," + CHAMP_MESSAGE + "=?,"
				+ CHAMP_DATE_TRANSMISSION + "=?," + CHAMP_DATE_A_FAIRE_LE + "=?," + CHAMP_DATE_FAIT_LE + "=?,"
				+ CHAMP_COMMENTAIRE + "=?," + CHAMP_ID_AGENT_REALISATION + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { nomAction, message, dateTransmission, dateAFaireLe, dateFaitLe,
				commentaire, idAgentRealisation, idCampagneAction });

	}

	@Override
	public void supprimerCampagneAction(Integer idCampagneAction) throws Exception {
		super.supprimerObject(idCampagneAction);
	}
}
