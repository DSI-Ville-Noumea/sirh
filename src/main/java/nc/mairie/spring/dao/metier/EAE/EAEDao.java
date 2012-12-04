package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.dao.mapper.metier.EAE.EAERowMapper;
import nc.mairie.spring.domain.metier.EAE.EAE;

import org.springframework.jdbc.core.JdbcTemplate;

public class EAEDao implements EAEDaoInterface {

	public static final String NOM_TABLE = "EAE";

	public static final String NOM_SEQUENCE = "EAE_S_EAE";

	public static final String CHAMP_ID_EAE = "ID_EAE";
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

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EAEDao() {

	}

	@Override
	public ArrayList<EAE> listerEAETravailPourCampagne(String etat, Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ETAT + "=? and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { etat, idCampagneEAE });
		for (Map row : rows) {
			EAE eae = new EAE();
			// logger.debug("List eae : " + row.toString());
			BigDecimal idEAE = (BigDecimal) row.get(CHAMP_ID_EAE);
			eae.setIdEAE(idEAE.intValue());
			BigDecimal idCampEAE = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			eae.setIdCampagneEAE(idCampEAE.intValue());
			eae.setEtat((String) row.get(CHAMP_ETAT));
			BigDecimal cap = (BigDecimal) row.get(CHAMP_CAP);
			eae.setCap(cap.intValue() == 0 ? false : true);
			BigDecimal document = (BigDecimal) row.get(CHAMP_DOC_ATTACHE);
			eae.setDocumentAttache(document.intValue() == 0 ? false : true);
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			BigDecimal dureeEntretien = (BigDecimal) row.get(CHAMP_DUREE_ENTRETIEN);
			eae.setDureeEntretien(dureeEntretien == null ? null : dureeEntretien.intValue());
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			BigDecimal idDelegataire = (BigDecimal) row.get(CHAMP_ID_DELEGATAIRE);
			eae.setIdDelegataire(idDelegataire == null ? null : idDelegataire.intValue());
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public void supprimerEAE(Integer idEAE) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { idEAE });
	}

	@Override
	public EAE chercherEAEAgent(Integer idAgent, Integer idCampagneEAE) {
		String sql = "select * from " + NOM_TABLE + " e inner join EAE_EVALUE ev on e.id_eae=ev.id_eae where ev.id_agent = ? and "
				+ CHAMP_ID_CAMPAGNE_EAE + "=?";
		EAE eae = (EAE) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idCampagneEAE }, new EAERowMapper());
		return eae;
	}

	@Override
	public Integer creerEAE(Integer idCampagneEae, String etat, boolean cap, boolean docAttache, Date dateCreation, Date dateFin, Date dateEntretien,
			Integer dureeEntretien, Date dateFinalise, Date dateControle, String heureControle, String userControle, Integer idDelegataire)
			throws Exception {

		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ETAT + "," + CHAMP_CAP + ","
				+ CHAMP_DOC_ATTACHE + "," + CHAMP_DATE_CREATION + "," + CHAMP_DATE_FIN + "," + CHAMP_DATE_ENTRETIEN + "," + CHAMP_DUREE_ENTRETIEN
				+ "," + CHAMP_DATE_FINALISE + "," + CHAMP_DATE_CONTROLE + "," + CHAMP_HEURE_CONTROLE + "," + CHAMP_USER_CONTROLE + ","
				+ CHAMP_ID_DELEGATAIRE + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { id, idCampagneEae, etat, cap, docAttache, dateCreation, dateFin, dateEntretien, dureeEntretien,
				dateFinalise, dateControle, heureControle, userControle, idDelegataire });

		return id;
	}

	@Override
	public ArrayList<EAE> listerEAEPourCampagne(Integer idCampagneEAE, String etat, String statut, ArrayList<String> listeSousService,
			String capBool, AgentNW agentEvaluateur, AgentNW agentEvalue) throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		if (!etat.equals(Const.CHAINE_VIDE)) {
			reqWhere += " and " + CHAMP_ETAT + " like '" + etat + "' ";
		}
		if (!statut.equals(Const.CHAINE_VIDE)) {
			reqWhere += " and eval.STATUT like '" + statut + "' ";
		}
		if (listeSousService != null) {
			String list = "";
			for (String codeServ : listeSousService) {
				list += "'" + codeServ + "',";
			}
			if (!list.equals(""))
				list = list.substring(0, list.length() - 1);
			reqWhere += " and (fp.CODE_SERVICE in (" + list + ")) ";
		}
		if (!capBool.equals(Const.CHAINE_VIDE)) {
			if (capBool.equals("oui")) {
				reqWhere += " and " + CHAMP_CAP + " = 1 ";
			} else {
				reqWhere += " and " + CHAMP_CAP + " = 0";
			}
		}

		if (agentEvaluateur != null) {
			reqWhere += " and evaluateur.id_agent = " + agentEvaluateur.getIdAgent();
		}

		if (agentEvalue != null) {
			reqWhere += " and eval.id_agent = " + agentEvalue.getIdAgent();
		}

		String sql = "select e.* from " + NOM_TABLE + " e inner join EAE_FICHE_POSTE fp on fp.id_eae = e." + CHAMP_ID_EAE
				+ " inner join EAE_EVALUE eval on eval.id_eae = e.id_eae left join EAE_EVALUATEUR evaluateur on e." + CHAMP_ID_EAE
				+ "=evaluateur.id_eae where " + CHAMP_ID_CAMPAGNE_EAE + "=? and fp.primaire=1 " + reqWhere;

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEAE });
		for (Map row : rows) {
			EAE eae = new EAE();
			// logger.debug("List eae : " + row.toString());
			BigDecimal idEAE = (BigDecimal) row.get(CHAMP_ID_EAE);
			eae.setIdEAE(idEAE.intValue());
			BigDecimal idCampEAE = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			eae.setIdCampagneEAE(idCampEAE.intValue());
			eae.setEtat((String) row.get(CHAMP_ETAT));
			BigDecimal cap = (BigDecimal) row.get(CHAMP_CAP);
			eae.setCap(cap.intValue() == 0 ? false : true);
			BigDecimal document = (BigDecimal) row.get(CHAMP_DOC_ATTACHE);
			eae.setDocumentAttache(document.intValue() == 0 ? false : true);
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			BigDecimal dureeEntretien = (BigDecimal) row.get(CHAMP_DUREE_ENTRETIEN);
			eae.setDureeEntretien(dureeEntretien == null ? null : dureeEntretien.intValue());
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			BigDecimal idDelegataire = (BigDecimal) row.get(CHAMP_ID_DELEGATAIRE);
			eae.setIdDelegataire(idDelegataire == null ? null : idDelegataire.intValue());
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public void modifierDelegataire(Integer idEAE, Integer idDelegataire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_DELEGATAIRE + " =? where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { idDelegataire, idEAE });
	}

	@Override
	public void modifierCAP(Integer idEAE, boolean cap) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CAP + " =? where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { cap, idEAE });
	}

	@Override
	public void modifierSuiteCreation(Integer idEAE, Date dateCreation, String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_CREATION + " =?," + CHAMP_ETAT + "=? where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateCreation, etat, idEAE });
	}

	@Override
	public void modifierEtat(Integer idEAE, String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ETAT + "=? where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { etat, idEAE });
	}

	@Override
	public void modifierControle(Integer idEAE, Date dateControle, String heureControle, String userControle, String etat) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_CONTROLE + "=?," + CHAMP_HEURE_CONTROLE + "=?," + CHAMP_USER_CONTROLE + "=?,"
				+ CHAMP_ETAT + "=? where " + CHAMP_ID_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateControle, heureControle, userControle, etat, idEAE });
	}

	@Override
	public ArrayList<EAE> listerEAEFinaliseControlePourCampagne(Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where (" + CHAMP_ETAT + "=? or " + CHAMP_ETAT + "=?) and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { EnumEtatEAE.FINALISE.getCode(),
				EnumEtatEAE.CONTROLE.getCode(), idCampagneEAE });
		for (Map row : rows) {
			EAE eae = new EAE();
			// logger.debug("List eae : " + row.toString());
			BigDecimal idEAE = (BigDecimal) row.get(CHAMP_ID_EAE);
			eae.setIdEAE(idEAE.intValue());
			BigDecimal idCampEAE = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			eae.setIdCampagneEAE(idCampEAE.intValue());
			eae.setEtat((String) row.get(CHAMP_ETAT));
			BigDecimal cap = (BigDecimal) row.get(CHAMP_CAP);
			eae.setCap(cap.intValue() == 0 ? false : true);
			BigDecimal document = (BigDecimal) row.get(CHAMP_DOC_ATTACHE);
			eae.setDocumentAttache(document.intValue() == 0 ? false : true);
			eae.setDateCreation((Date) row.get(CHAMP_DATE_CREATION));
			eae.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			eae.setDateEntretien((Date) row.get(CHAMP_DATE_ENTRETIEN));
			BigDecimal dureeEntretien = (BigDecimal) row.get(CHAMP_DUREE_ENTRETIEN);
			eae.setDureeEntretien(dureeEntretien == null ? null : dureeEntretien.intValue());
			eae.setDateFinalise((Date) row.get(CHAMP_DATE_FINALISE));
			eae.setDateControle((Date) row.get(CHAMP_DATE_CONTROLE));
			eae.setHeureControle((String) row.get(CHAMP_HEURE_CONTROLE));
			eae.setUserControle((String) row.get(CHAMP_USER_CONTROLE));
			BigDecimal idDelegataire = (BigDecimal) row.get(CHAMP_ID_DELEGATAIRE);
			eae.setIdDelegataire(idDelegataire == null ? null : idDelegataire.intValue());
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public int compterEAEDirectionSectionEtat(Integer idCampagneEAE, String direction, String section, String etat) throws Exception {
		String sql = "";
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(e.id_eae) from " + NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE=? and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, etat, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(e.id_eae) from " + NOM_TABLE
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
			sql = "select count(e.id_eae) from " + NOM_TABLE
					+ " e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE=? and e."
					+ CHAMP_ETAT + "=? and  e." + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, etat, idCampagneEAE });
		}
		return total;
	}

	@Override
	public int compterEAEDirectionSectionCAP(Integer idCampagneEAE, String direction, String section) throws Exception {
		String sql = "";
		int total = 0;
		if (direction == null && section != null) {
			sql = "select count(e.id_eae) from " + NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE=? and "
					+ CHAMP_CAP + "=1 and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { section, idCampagneEAE });
		} else if (section == null && direction != null) {
			sql = "select count(e.id_eae) from " + NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE is null and "
					+ CHAMP_CAP + "=1 and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, idCampagneEAE });
		} else if (direction == null && section == null) {
			sql = "select count(e.id_eae) from "
					+ NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE is null and fp.SECTION_SERVICE is null and "
					+ CHAMP_CAP + "=1 and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { idCampagneEAE });
		} else {
			sql = "select count(e.id_eae) from " + NOM_TABLE
					+ "  e inner join EAE_FICHE_POSTE fp on e.id_eae=fp.id_eae where fp.DIRECTION_SERVICE=? and fp.SECTION_SERVICE=? and "
					+ CHAMP_CAP + "=1 and  " + CHAMP_ID_CAMPAGNE_EAE + "=?";
			total = jdbcTemplate.queryForInt(sql, new Object[] { direction, section, idCampagneEAE });
		}
		return total;
	}

	@Override
	public EAE chercherEAE(Integer idEae) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + " = ? ";
		EAE eae = (EAE) jdbcTemplate.queryForObject(sql, new Object[] { idEae }, new EAERowMapper());
		return eae;
	}
}
