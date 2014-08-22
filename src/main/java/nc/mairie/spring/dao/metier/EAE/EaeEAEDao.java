package nc.mairie.spring.dao.metier.EAE;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.eae.EAE;
import nc.mairie.spring.dao.utils.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class EaeEAEDao extends EaeDao implements EaeEAEDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeEAEDao.class);

	public static final String NOM_SEQUENCE = "EAE_S_EAE";
	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_ETAT = "ETAT";
	public static final String CHAMP_CAP = "CAP";
	public static final String CHAMP_DOC_ATTACHE = "DOC_ATTACHE";
	public static final String CHAMP_DATE_CREATION = "DATE_CREATION";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_DATE_ENTRETIEN = "DATE_ENTRETIEN";
	public static final String CHAMP_DUREE_ENTRETIEN = "DUREE_ENTRETIEN";
	public static final String CHAMP_DATE_FINALISE = "DATE_FINALISE";
	public static final String CHAMP_DATE_CONTROLE = "DATE_CONTROLE";
	public static final String CHAMP_HEURE_CONTROLE = "HEURE_CONTROLE";
	public static final String CHAMP_USER_CONTROLE = "USER_CONTROLE";
	public static final String CHAMP_ID_DELEGATAIRE = "ID_DELEGATAIRE";

	public EaeEAEDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE";
		super.CHAMP_ID = "ID_EAE";
	}

	@Override
	public ArrayList<EAE> listerEAETravailPourCampagne(String etat, Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ETAT + "=? and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { etat, idCampagneEAE });
		for (Map<String, Object> row : rows) {
			EAE eae = new EAE();
			eae.setIdEae((Integer) row.get(CHAMP_ID));
			eae.setIdCampagneEae((Integer) row.get(CHAMP_ID_CAMPAGNE_EAE));
			eae.setEtat((String) row.get(CHAMP_ETAT));
			eae.setCap((boolean) row.get(CHAMP_CAP));
			eae.setDocAttache((boolean) row.get(CHAMP_DOC_ATTACHE));
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			eae.setDureeEntretien((Integer) row.get(CHAMP_DUREE_ENTRETIEN));
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			eae.setIdDelegataire((Integer) row.get(CHAMP_ID_DELEGATAIRE));
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public void supprimerEAE(Integer idEAE) throws Exception {
		super.supprimerObject(idEAE);
	}

	@Override
	public EAE chercherEAEAgent(Integer idAgent, Integer idCampagneEAE) {
		String sql = "select * from " + NOM_TABLE
				+ " e inner join EAE_EVALUE ev on e.id_eae=ev.id_eae where ev.id_agent = ? and "
				+ CHAMP_ID_CAMPAGNE_EAE + "=?";
		try {
			EAE eae = (EAE) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idCampagneEAE },
					new BeanPropertyRowMapper<EAE>(EAE.class));
			return eae;
		} catch (Exception e) {
			logger.debug("Aucun EAE trouvé pour l'agent " + idAgent.toString() + " pour la campagne id="
					+ idCampagneEAE.toString());
			return null;
		}
	}

	@Override
	public Integer creerEAE(Integer idCampagneEae, String etat, boolean cap, boolean docAttache, Date dateCreation,
			Date dateFin, Date dateEntretien, Integer dureeEntretien, Date dateFinalise, Date dateControle,
			String heureControle, String userControle, Integer idDelegataire) throws Exception {

		String sqlClePrimaire = "select nextval('" + NOM_SEQUENCE + "')";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ETAT
				+ "," + CHAMP_CAP + "," + CHAMP_DOC_ATTACHE + "," + CHAMP_DATE_CREATION + "," + CHAMP_DATE_FIN + ","
				+ CHAMP_DATE_ENTRETIEN + "," + CHAMP_DUREE_ENTRETIEN + "," + CHAMP_DATE_FINALISE + ","
				+ CHAMP_DATE_CONTROLE + "," + CHAMP_HEURE_CONTROLE + "," + CHAMP_USER_CONTROLE + ","
				+ CHAMP_ID_DELEGATAIRE + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcTemplate
				.update(sql, new Object[] { id, idCampagneEae, etat, cap, docAttache, dateCreation, dateFin,
						dateEntretien, dureeEntretien, dateFinalise, dateControle, heureControle, userControle,
						idDelegataire });

		return id;
	}

	@Override
	public ArrayList<EAE> listerEAEPourCampagne(Integer idCampagneEAE, String etat, String statut,
			ArrayList<String> listeSousService, String capBool, Agent agentEvaluateur, Agent agentEvalue,
			String affecte) throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		String reqInner = Const.CHAINE_VIDE;
		if (!etat.equals(Const.CHAINE_VIDE)) {
			reqWhere += " and " + CHAMP_ETAT + " like '" + etat + "' ";
		}
		if (!statut.equals(Const.CHAINE_VIDE)) {
			reqWhere += " and eval.STATUT like '" + statut + "' ";
		}
		if (listeSousService != null) {
			String list = Const.CHAINE_VIDE;
			for (String codeServ : listeSousService) {
				list += "'" + codeServ + "',";
			}
			if (!list.equals(Const.CHAINE_VIDE))
				list = list.substring(0, list.length() - 1);
			reqInner += " inner join EAE_FICHE_POSTE fp on e." + CHAMP_ID + "=fp.id_eae ";
			reqWhere += " and (fp.CODE_SERVICE in (" + list + ")) ";
		}
		if (!capBool.equals(Const.CHAINE_VIDE)) {
			if (capBool.equals("oui")) {
				reqWhere += " and " + CHAMP_CAP + " = true ";
			} else {
				reqWhere += " and " + CHAMP_CAP + " = false";
			}
		}
		if (!affecte.equals(Const.CHAINE_VIDE)) {
			if (affecte.equals("oui")) {
				reqWhere += " and eval.AGENT_DETACHE = true ";
			} else {
				reqWhere += " and eval.AGENT_DETACHE = false ";
			}
		}
		if (agentEvaluateur != null) {
			reqInner += " inner join EAE_EVALUATEUR evaluateur on e." + CHAMP_ID + "=evaluateur.id_eae ";
			reqWhere += " and evaluateur.id_agent = " + agentEvaluateur.getIdAgent();
		}

		if (agentEvalue != null) {
			reqWhere += " and eval.id_agent = " + agentEvalue.getIdAgent();
		}

		String sql = "select e.* from " + NOM_TABLE + " e  inner join EAE_EVALUE eval on eval.id_eae = e.id_eae "
				+ reqInner + " where e." + CHAMP_ID_CAMPAGNE_EAE + "=? " + reqWhere;

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEAE });
		for (Map<String, Object> row : rows) {
			EAE eae = new EAE();
			eae.setIdEae((Integer) row.get(CHAMP_ID));
			eae.setIdCampagneEae((Integer) row.get(CHAMP_ID_CAMPAGNE_EAE));
			eae.setEtat((String) row.get(CHAMP_ETAT));
			eae.setCap((boolean) row.get(CHAMP_CAP));
			eae.setDocAttache((boolean) row.get(CHAMP_DOC_ATTACHE));
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			eae.setDureeEntretien((Integer) row.get(CHAMP_DUREE_ENTRETIEN));
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			eae.setIdDelegataire((Integer) row.get(CHAMP_ID_DELEGATAIRE));
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public void modifierDelegataire(Integer idEAE, Integer idDelegataire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_DELEGATAIRE + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { idDelegataire, idEAE });
	}

	@Override
	public void modifierCAP(Integer idEAE, boolean cap) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CAP + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { cap, idEAE });
	}

	@Override
	public void modifierSuiteCreation(Integer idEAE, Date dateCreation, String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_CREATION + " =?," + CHAMP_ETAT + "=? where "
				+ CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateCreation, etat, idEAE });
	}

	@Override
	public void modifierEtat(Integer idEAE, String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ETAT + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { etat, idEAE });
	}

	@Override
	public void modifierControle(Integer idEAE, Date dateControle, String heureControle, String userControle,
			String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_CONTROLE + "=?," + CHAMP_HEURE_CONTROLE + "=?,"
				+ CHAMP_USER_CONTROLE + "=?," + CHAMP_ETAT + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateControle, heureControle, userControle, etat, idEAE });
	}

	@Override
	public ArrayList<EAE> listerEAEFinaliseControlePourCampagne(Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where (" + CHAMP_ETAT + "=? or " + CHAMP_ETAT + "=?) and  "
				+ CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { EnumEtatEAE.FINALISE.getCode(),
				EnumEtatEAE.CONTROLE.getCode(), idCampagneEAE });
		for (Map<String, Object> row : rows) {
			EAE eae = new EAE();
			eae.setIdEae((Integer) row.get(CHAMP_ID));
			eae.setIdCampagneEae((Integer) row.get(CHAMP_ID_CAMPAGNE_EAE));
			eae.setEtat((String) row.get(CHAMP_ETAT));
			eae.setCap((boolean) row.get(CHAMP_CAP));
			eae.setDocAttache((boolean) row.get(CHAMP_DOC_ATTACHE));
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			eae.setDureeEntretien((Integer) row.get(CHAMP_DUREE_ENTRETIEN));
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			eae.setIdDelegataire((Integer) row.get(CHAMP_ID_DELEGATAIRE));
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public int compterEAEDirectionSectionEtat(Integer idCampagneEAE, String direction, String section, String etat)
			throws Exception {
		String sql = Const.CHAINE_VIDE;
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE=? and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, etat, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE is null and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, etat, idCampagneEAE });
		} else if (direction == null && section == null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { etat, idCampagneEAE });
		} else {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE=? and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, etat, idCampagneEAE });
		}
		return total;
	}

	@Override
	public int compterEAEDirectionSectionCAP(Integer idCampagneEAE, String direction, String section) throws Exception {
		String sql = Const.CHAINE_VIDE;
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE=? and "
					+ CHAMP_CAP + "=true and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE is null and "
					+ CHAMP_CAP + "=true and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, idCampagneEAE });
		} else if (direction == null && section == null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and "
					+ CHAMP_CAP + "=true and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { idCampagneEAE });
		} else {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE=? and "
					+ CHAMP_CAP + "=true and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, idCampagneEAE });
		}
		return total;
	}

	@Override
	public EAE chercherEAE(Integer idEae) throws Exception {
		return super.chercherObject(EAE.class, idEae);
	}
}
