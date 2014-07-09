package nc.mairie.spring.dao.metier.suiviMedical;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.SirhDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class SuiviMedicalDao extends SirhDao implements SuiviMedicalDaoInterface {

	private Logger logger = LoggerFactory.getLogger(SuiviMedicalDao.class);

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_NOMATR = "NOMATR";
	public static final String CHAMP_AGENT = "AGENT";
	public static final String CHAMP_STATUT = "STATUT";
	public static final String CHAMP_ID_SERVI = "ID_SERVI";
	public static final String CHAMP_DATE_DERNIERE_VISITE = "DATE_DERNIERE_VISITE";
	public static final String CHAMP_DATE_PREVISION_VISITE = "DATE_PREVISION_VISITE";
	public static final String CHAMP_ID_MOTIF_VM = "ID_MOTIF_VM";
	public static final String CHAMP_NB_VISITES_RATEES = "NB_VISITES_RATEES";
	public static final String CHAMP_ID_MEDECIN = "ID_MEDECIN";
	public static final String CHAMP_DATE_PROCHAINE_VISITE = "DATE_PROCHAINE_VISITE";
	public static final String CHAMP_HEURE_PROCHAINE_VISITE = "HEURE_PROCHAINE_VISITE";
	public static final String CHAMP_ETAT = "ETAT";
	public static final String CHAMP_MOIS = "MOIS";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_RELANCE = "RELANCE";

	public SuiviMedicalDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "SUIVI_MEDICAL";
		super.CHAMP_ID = "ID_SUIVI_MED";
	}

	@Override
	public SuiviMedical chercherSuiviMedical(Integer idSuiviMed) throws Exception {
		return super.chercherObject(SuiviMedical.class, idSuiviMed);
	}

	@Override
	public String getStatutSM(String codeStatut) {
		if (codeStatut.equals("1") || codeStatut.equals("2") || codeStatut.equals("3") || codeStatut.equals("6")
				|| codeStatut.equals("16") || codeStatut.equals("17") || codeStatut.equals("18")
				|| codeStatut.equals("19") || codeStatut.equals("20")) {
			return "F";
		} else if (codeStatut.equals("7") || codeStatut.equals("8")) {
			return "CC";
		} else if (codeStatut.equals("4")) {
			return "C";
		} else {
			return Const.CHAINE_VIDE;
		}
	}

	@Override
	public void supprimerSuiviMedicalTravail(String etat) throws Exception {
		String sql = "DELETE from " + NOM_TABLE + " where " + CHAMP_ETAT + "=? ";
		jdbcTemplate.update(sql, new Object[] { etat });
	}

	@Override
	public void supprimerSuiviMedicalTravailAvecMoisetAnnee(String etat, Integer mois, Integer annee) throws Exception {
		String sql = "DELETE from " + NOM_TABLE + " where " + CHAMP_ETAT + "=? and " + CHAMP_MOIS + "=? and "
				+ CHAMP_ANNEE + "=?";
		jdbcTemplate.update(sql, new Object[] { etat, mois, annee });
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalAvecMoisetAnneeSansEffectue(Integer mois, Integer annee,
			AgentNW agent, ArrayList<String> listeSousService, String relance, String motifVM, String etat,
			String statut) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=? and "
				+ CHAMP_ETAT + "!= ? ";
		if (agent != null) {
			sql += " and " + CHAMP_ID_AGENT + "=" + agent.getIdAgent() + " ";
		}

		if (!relance.equals(Const.CHAINE_VIDE)) {
			if (relance.equals("oui")) {
				sql += " and " + CHAMP_NB_VISITES_RATEES + " > 10";
			} else {
				sql += " and " + CHAMP_NB_VISITES_RATEES + " = 0 ";
			}
		}

		if (!statut.equals(Const.CHAINE_VIDE)) {
			sql += " and " + CHAMP_STATUT + " ='" + statut + "' ";

		}

		if (!motifVM.equals(Const.CHAINE_VIDE)) {
			sql += " and " + CHAMP_ID_MOTIF_VM + " = " + motifVM + " ";
		}

		if (!etat.equals(Const.CHAINE_VIDE)) {
			sql += " and " + CHAMP_ETAT + " = '" + etat + "' ";
		}

		if (listeSousService != null) {
			String list = Const.CHAINE_VIDE;
			for (String codeServ : listeSousService) {
				list += "'" + codeServ + "',";
			}
			if (!list.equals(Const.CHAINE_VIDE))
				list = list.substring(0, list.length() - 1);
			sql += " and (" + CHAMP_ID_SERVI + " in (" + list + ")) ";
		}

		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { mois, annee,
				EnumEtatSuiviMed.EFFECTUE.getCode() });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
			sm.setIdServi((String) row.get(CHAMP_ID_SERVI));
			sm.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			sm.setDatePrevisionVisite((Date) row.get(CHAMP_DATE_PREVISION_VISITE));
			sm.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			sm.setNbVisitesRatees((Integer) row.get(CHAMP_NB_VISITES_RATEES));
			sm.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			sm.setDateProchaineVisite((Date) row.get(CHAMP_DATE_PROCHAINE_VISITE));
			sm.setHeureProchaineVisite((String) row.get(CHAMP_HEURE_PROCHAINE_VISITE));
			sm.setEtat((String) row.get(CHAMP_ETAT));
			sm.setMois((Integer) row.get(CHAMP_MOIS));
			sm.setAnnee((Integer) row.get(CHAMP_ANNEE));
			sm.setRelance((Integer) row.get(CHAMP_RELANCE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public void creerSuiviMedical(Integer idAgent, Integer nomatr, String agent, String statut, String idServi,
			Date dateDerniereVisite, Date datePrevisionVisite, Integer idMotifVM, Integer nbVisitesRatees,
			Integer idMedecin, Date dateProchaineVisite, String heureProchaineVisite, String etat, Integer mois,
			Integer annee, Integer relance) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_NOMATR + "," + CHAMP_AGENT + ","
				+ CHAMP_STATUT + "," + CHAMP_ID_SERVI + "," + CHAMP_DATE_DERNIERE_VISITE + ","
				+ CHAMP_DATE_PREVISION_VISITE + "," + CHAMP_ID_MOTIF_VM + "," + CHAMP_NB_VISITES_RATEES + ","
				+ CHAMP_ID_MEDECIN + "," + CHAMP_DATE_PROCHAINE_VISITE + "," + CHAMP_HEURE_PROCHAINE_VISITE + ","
				+ CHAMP_ETAT + "," + CHAMP_MOIS + "," + CHAMP_ANNEE + "," + CHAMP_RELANCE
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, nomatr, agent, statut, idServi, dateDerniereVisite,
				datePrevisionVisite, idMotifVM, nbVisitesRatees, idMedecin, dateProchaineVisite, heureProchaineVisite,
				etat, mois, annee, relance });
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalNonEffectue(Integer mois, Integer annee, String etat)
			throws Exception {
		// si mois=1 alors il faut rechercher sur le mois precedent de l'année
		// precedente
		if (mois == 1) {
			mois = 12;
			annee = annee - 1;
		}
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=? and "
				+ CHAMP_ETAT + "=?";

		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { mois - 1, annee, etat });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
			sm.setIdServi((String) row.get(CHAMP_ID_SERVI));
			sm.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			sm.setDatePrevisionVisite((Date) row.get(CHAMP_DATE_PREVISION_VISITE));
			sm.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			sm.setNbVisitesRatees((Integer) row.get(CHAMP_NB_VISITES_RATEES));
			sm.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			sm.setDateProchaineVisite((Date) row.get(CHAMP_DATE_PROCHAINE_VISITE));
			sm.setHeureProchaineVisite((String) row.get(CHAMP_HEURE_PROCHAINE_VISITE));
			sm.setEtat((String) row.get(CHAMP_ETAT));
			sm.setMois((Integer) row.get(CHAMP_MOIS));
			sm.setAnnee((Integer) row.get(CHAMP_ANNEE));
			sm.setRelance((Integer) row.get(CHAMP_RELANCE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public SuiviMedical chercherSuiviMedicalAgentMoisetAnnee(Integer idAgent, Integer mois, Integer annee)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_MOIS + "=? and "
				+ CHAMP_ANNEE + "=? ";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, mois, annee },
				new BeanPropertyRowMapper<SuiviMedical>(SuiviMedical.class));

		return sm;
	}

	@Override
	public SuiviMedical chercherSuiviMedicalAgentNomatrMoisetAnnee(Integer noMatr, Integer mois, Integer annee)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " = ? and " + CHAMP_MOIS + "=? and "
				+ CHAMP_ANNEE + "=? ";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate.queryForObject(sql, new Object[] { noMatr, mois, annee },
				new BeanPropertyRowMapper<SuiviMedical>(SuiviMedical.class));

		return sm;
	}

	@Override
	public void modifierSuiviMedicalTravail(Integer idSuiviMed, SuiviMedical smSelct) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_NOMATR + "=?," + CHAMP_AGENT
				+ "=?," + CHAMP_STATUT + "=?," + CHAMP_ID_SERVI + "=?," + CHAMP_DATE_DERNIERE_VISITE + "=?,"
				+ CHAMP_DATE_PREVISION_VISITE + "=?," + CHAMP_ID_MOTIF_VM + "=?," + CHAMP_NB_VISITES_RATEES + "=?,"
				+ CHAMP_ID_MEDECIN + "=?," + CHAMP_DATE_PROCHAINE_VISITE + "=?," + CHAMP_HEURE_PROCHAINE_VISITE + "=?,"
				+ CHAMP_ETAT + "=?," + CHAMP_MOIS + "=?," + CHAMP_ANNEE + "=?, " + CHAMP_RELANCE + "=? where "
				+ CHAMP_ID + "=?";
		jdbcTemplate.update(
				sql,
				new Object[] { smSelct.getIdAgent(), smSelct.getNomatr(), smSelct.getAgent(), smSelct.getStatut(),
						smSelct.getIdServi(), smSelct.getDateDerniereVisite(), smSelct.getDatePrevisionVisite(),
						smSelct.getIdMotifVm(), smSelct.getNbVisitesRatees(), smSelct.getIdMedecin(),
						smSelct.getDateProchaineVisite(), smSelct.getHeureProchaineVisite(), smSelct.getEtat(),
						smSelct.getMois(), smSelct.getAnnee(), smSelct.getRelance(), idSuiviMed });
	}

	@Override
	public void supprimerSuiviMedicalById(Integer idSuiviMed) throws Exception {
		super.supprimerObject(idSuiviMed);
	}

	@Override
	public ArrayList<SuiviMedical> listerHistoriqueSuiviMedical(Integer annee, Integer mois, String etatConvoq,
			String etatAccomp, String etatPlanif) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? and " + CHAMP_MOIS + "=? and ("
				+ CHAMP_ETAT + "= ? or " + CHAMP_ETAT + "= ? or " + CHAMP_ETAT + "= ?)";
		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee, mois, etatConvoq,
				etatAccomp, etatPlanif });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
			sm.setIdServi((String) row.get(CHAMP_ID_SERVI));
			sm.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			sm.setDatePrevisionVisite((Date) row.get(CHAMP_DATE_PREVISION_VISITE));
			sm.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			sm.setNbVisitesRatees((Integer) row.get(CHAMP_NB_VISITES_RATEES));
			sm.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			sm.setDateProchaineVisite((Date) row.get(CHAMP_DATE_PROCHAINE_VISITE));
			sm.setHeureProchaineVisite((String) row.get(CHAMP_HEURE_PROCHAINE_VISITE));
			sm.setEtat((String) row.get(CHAMP_ETAT));
			sm.setMois((Integer) row.get(CHAMP_MOIS));
			sm.setAnnee((Integer) row.get(CHAMP_ANNEE));
			sm.setRelance((Integer) row.get(CHAMP_RELANCE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalEtatAgent(Integer idAgentChoisi, String etatConvoq,
			String etatPlanif, String etatImprime) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and (" + CHAMP_ETAT + "= ? or "
				+ CHAMP_ETAT + "= ? or " + CHAMP_ETAT + "= ?)";
		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgentChoisi, etatConvoq,
				etatPlanif, etatImprime });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
			sm.setIdServi((String) row.get(CHAMP_ID_SERVI));
			sm.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			sm.setDatePrevisionVisite((Date) row.get(CHAMP_DATE_PREVISION_VISITE));
			sm.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			sm.setNbVisitesRatees((Integer) row.get(CHAMP_NB_VISITES_RATEES));
			sm.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			sm.setDateProchaineVisite((Date) row.get(CHAMP_DATE_PROCHAINE_VISITE));
			sm.setHeureProchaineVisite((String) row.get(CHAMP_HEURE_PROCHAINE_VISITE));
			sm.setEtat((String) row.get(CHAMP_ETAT));
			sm.setMois((Integer) row.get(CHAMP_MOIS));
			sm.setAnnee((Integer) row.get(CHAMP_ANNEE));
			sm.setRelance((Integer) row.get(CHAMP_RELANCE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalAgentAnterieurDate(Integer idAgentChoisi, Integer mois,
			Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_MOIS + "<=? and "
				+ CHAMP_ANNEE + "<=?";
		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgentChoisi, mois, annee });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
			sm.setIdServi((String) row.get(CHAMP_ID_SERVI));
			sm.setDateDerniereVisite((Date) row.get(CHAMP_DATE_DERNIERE_VISITE));
			sm.setDatePrevisionVisite((Date) row.get(CHAMP_DATE_PREVISION_VISITE));
			sm.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			sm.setNbVisitesRatees((Integer) row.get(CHAMP_NB_VISITES_RATEES));
			sm.setIdMedecin((Integer) row.get(CHAMP_ID_MEDECIN));
			sm.setDateProchaineVisite((Date) row.get(CHAMP_DATE_PROCHAINE_VISITE));
			sm.setHeureProchaineVisite((String) row.get(CHAMP_HEURE_PROCHAINE_VISITE));
			sm.setEtat((String) row.get(CHAMP_ETAT));
			sm.setMois((Integer) row.get(CHAMP_MOIS));
			sm.setAnnee((Integer) row.get(CHAMP_ANNEE));
			sm.setRelance((Integer) row.get(CHAMP_RELANCE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public SuiviMedical chercherDernierSuiviMedicalAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_ID
				+ " in (select max(" + CHAMP_ID + ") from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "="
				+ CHAMP_ID_AGENT + ")";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate.queryForObject(sql, new Object[] { idAgent },
				new BeanPropertyRowMapper<SuiviMedical>(SuiviMedical.class));

		return sm;
	}
}
