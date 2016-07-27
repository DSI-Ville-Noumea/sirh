package nc.mairie.spring.dao.metier.suiviMedical;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.Const;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.utils.SirhDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class SuiviMedicalDao extends SirhDao implements SuiviMedicalDaoInterface {

	private Logger				logger							= LoggerFactory.getLogger(SuiviMedicalDao.class);

	public static final String	CHAMP_ID_AGENT					= "ID_AGENT";
	public static final String	CHAMP_NOMATR					= "NOMATR";
	public static final String	CHAMP_AGENT						= "AGENT";
	public static final String	CHAMP_STATUT					= "STATUT";
	public static final String	CHAMP_ID_SERVI					= "ID_SERVI";
	public static final String	CHAMP_DATE_DERNIERE_VISITE		= "DATE_DERNIERE_VISITE";
	public static final String	CHAMP_DATE_PREVISION_VISITE		= "DATE_PREVISION_VISITE";
	public static final String	CHAMP_ID_MOTIF_VM				= "ID_MOTIF_VM";
	public static final String	CHAMP_NB_VISITES_RATEES			= "NB_VISITES_RATEES";
	public static final String	CHAMP_ID_MEDECIN				= "ID_MEDECIN";
	public static final String	CHAMP_DATE_PROCHAINE_VISITE		= "DATE_PROCHAINE_VISITE";
	public static final String	CHAMP_HEURE_PROCHAINE_VISITE	= "HEURE_PROCHAINE_VISITE";
	public static final String	CHAMP_ETAT						= "ETAT";
	public static final String	CHAMP_MOIS						= "MOIS";
	public static final String	CHAMP_ANNEE						= "ANNEE";
	public static final String	CHAMP_RELANCE					= "RELANCE";
	public static final String	CHAMP_ID_SERVICE_ADS			= "ID_SERVICE_ADS";
	public static final String	CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE		= "ID_RECOMMANDATION_DERNIERE_VISITE";
	public static final String	CHAMP_COMMENTAIRE_DERNIERE_VISITE			= "COMMENTAIRE_DERNIERE_VISITE";

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
		if (codeStatut.equals("1") || codeStatut.equals("2") || codeStatut.equals("3") || codeStatut.equals("6") || codeStatut.equals("16")
				|| codeStatut.equals("17") || codeStatut.equals("18") || codeStatut.equals("19") || codeStatut.equals("20")) {
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
		String sql = "DELETE from " + NOM_TABLE + " where " + CHAMP_ETAT + "=? and " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=?";
		jdbcTemplate.update(sql, new Object[] { etat, mois, annee });
	}

	@Override
	public void creerSuiviMedical(Integer idAgent, Integer nomatr, String agent, String statut, Date dateDerniereVisite, Date datePrevisionVisite,
			Integer idMotifVM, Integer nbVisitesRatees, Integer idMedecin, Date dateProchaineVisite, String heureProchaineVisite, String etat, Integer mois,
			Integer annee, Integer relance, Integer idServiceADS, String idServi,Integer idRecommandationDerniereVisite,String commentaireDerniereVisite) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_NOMATR + "," + CHAMP_AGENT + "," + CHAMP_STATUT + ","
				+ CHAMP_DATE_DERNIERE_VISITE + "," + CHAMP_DATE_PREVISION_VISITE + "," + CHAMP_ID_MOTIF_VM + "," + CHAMP_NB_VISITES_RATEES + ","
				+ CHAMP_ID_MEDECIN + "," + CHAMP_DATE_PROCHAINE_VISITE + "," + CHAMP_HEURE_PROCHAINE_VISITE + "," + CHAMP_ETAT + "," + CHAMP_MOIS + ","
				+ CHAMP_ANNEE + "," + CHAMP_RELANCE + "," + CHAMP_ID_SERVICE_ADS + "," + CHAMP_ID_SERVI
				+ "," + CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE+"," + CHAMP_COMMENTAIRE_DERNIERE_VISITE				
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, nomatr, agent, statut, dateDerniereVisite, datePrevisionVisite, idMotifVM, nbVisitesRatees, idMedecin,
				dateProchaineVisite, heureProchaineVisite, etat, mois, annee, relance, idServiceADS, idServi, idRecommandationDerniereVisite,commentaireDerniereVisite });
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalNonEffectue(Integer mois, Integer annee, String etat) throws Exception {
		// si mois=1 alors il faut rechercher sur le mois precedent de l'année
		// precedente
		if (mois == 1) {
			mois = 12;
			annee = annee - 1;
		}
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=? and " + CHAMP_ETAT + "=?";

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
			sm.setIdServiceAds((Integer) row.get(CHAMP_ID_SERVICE_ADS));
			sm.setIdRecommandationDerniereVisite((Integer) row.get(CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE));
			sm.setCommentaireDerniereViste((String) row.get(CHAMP_COMMENTAIRE_DERNIERE_VISITE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public SuiviMedical chercherSuiviMedicalAgentMoisetAnnee(Integer idAgent, Integer mois, Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=? ";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, mois, annee }, new BeanPropertyRowMapper<SuiviMedical>(
				SuiviMedical.class));

		return sm;
	}

	@Override
	public SuiviMedical chercherSuiviMedicalAgentNomatrMoisetAnnee(Integer noMatr, Integer mois, Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " = ? and " + CHAMP_MOIS + "=? and " + CHAMP_ANNEE + "=? ";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate.queryForObject(sql, new Object[] { noMatr, mois, annee }, new BeanPropertyRowMapper<SuiviMedical>(
				SuiviMedical.class));

		return sm;
	}

	@Override
	public void modifierSuiviMedicalTravail(Integer idSuiviMed, SuiviMedical smSelct) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_NOMATR + "=?," + CHAMP_AGENT + "=?," + CHAMP_STATUT + "=?,"
				+ CHAMP_DATE_DERNIERE_VISITE + "=?," + CHAMP_DATE_PREVISION_VISITE + "=?," + CHAMP_ID_MOTIF_VM + "=?," + CHAMP_NB_VISITES_RATEES + "=?,"
				+ CHAMP_ID_MEDECIN + "=?," + CHAMP_DATE_PROCHAINE_VISITE + "=?," + CHAMP_HEURE_PROCHAINE_VISITE + "=?," + CHAMP_ETAT + "=?," + CHAMP_MOIS
				+ "=?," + CHAMP_ANNEE + "=?, " + CHAMP_RELANCE + "=?, " + CHAMP_ID_SERVICE_ADS + "=? , " + CHAMP_ID_SERVI + "=? , " + CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE + "=?, " + CHAMP_COMMENTAIRE_DERNIERE_VISITE + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(
				sql,
				new Object[] { smSelct.getIdAgent(), smSelct.getNomatr(), smSelct.getAgent(), smSelct.getStatut(), smSelct.getDateDerniereVisite(),
						smSelct.getDatePrevisionVisite(), smSelct.getIdMotifVm(), smSelct.getNbVisitesRatees(), smSelct.getIdMedecin(),
						smSelct.getDateProchaineVisite(), smSelct.getHeureProchaineVisite(), smSelct.getEtat(), smSelct.getMois(), smSelct.getAnnee(),
						smSelct.getRelance(), smSelct.getIdServiceAds(), smSelct.getIdServi(),smSelct.getIdRecommandationDerniereVisite(),smSelct.getCommentaireDerniereViste(), idSuiviMed });
	}

	@Override
	public void supprimerSuiviMedicalById(Integer idSuiviMed) throws Exception {
		super.supprimerObject(idSuiviMed);
	}

	@Override
	public ArrayList<SuiviMedical> listerHistoriqueSuiviMedical(Integer annee, Integer mois,  String etatPlanif)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? and " + CHAMP_MOIS + "=? and " + CHAMP_ETAT + "= ? ";
		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee, mois, etatPlanif });
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
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
			sm.setIdServiceAds((Integer) row.get(CHAMP_ID_SERVICE_ADS));
			sm.setIdRecommandationDerniereVisite((Integer) row.get(CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE));
			sm.setCommentaireDerniereViste((String) row.get(CHAMP_COMMENTAIRE_DERNIERE_VISITE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalEtatAgent(Integer idAgentChoisi, String etatPlanif)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_ETAT + "= ? ";
		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgentChoisi,  etatPlanif});
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			logger.info("List suiviMed listerSuiviMedicalNonEffectue : " + row.toString());
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
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
			sm.setIdServiceAds((Integer) row.get(CHAMP_ID_SERVICE_ADS));
			sm.setIdRecommandationDerniereVisite((Integer) row.get(CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE));
			sm.setCommentaireDerniereViste((String) row.get(CHAMP_COMMENTAIRE_DERNIERE_VISITE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalAgentAnterieurDate(Integer idAgentChoisi, Integer mois, Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_MOIS + "<=? and " + CHAMP_ANNEE + "<=?";
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
			sm.setIdServiceAds((Integer) row.get(CHAMP_ID_SERVICE_ADS));
			sm.setIdRecommandationDerniereVisite((Integer) row.get(CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE));
			sm.setCommentaireDerniereViste((String) row.get(CHAMP_COMMENTAIRE_DERNIERE_VISITE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}

	@Override
	public SuiviMedical chercherDernierSuiviMedicalAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_ID + " in (select max(" + CHAMP_ID + ") from " + NOM_TABLE
				+ " where " + CHAMP_ID_AGENT + "=" + CHAMP_ID_AGENT + ")";

		SuiviMedical sm = (SuiviMedical) jdbcTemplate
				.queryForObject(sql, new Object[] { idAgent }, new BeanPropertyRowMapper<SuiviMedical>(SuiviMedical.class));

		return sm;
	}

	@Override
	public ArrayList<SuiviMedical> listerSuiviMedicalAvecMoisetAnneeBetweenDate(Date dateDebut, Date dateFin, List<Integer> listeAgent,
			List<Integer> listeSousService, String statut, boolean CDD, Recommandation recommandation, String etat) throws Exception {

		String sql = "select * from " + NOM_TABLE + " sm ";
		if (CDD) {
			sql += " inner join AGENT ag on sm." + CHAMP_ID_AGENT + "=ag." + CHAMP_ID_AGENT;
			sql += " inner join CONTRAT c on c." + CHAMP_ID_AGENT + "=ag." + CHAMP_ID_AGENT;
			sql += " where c.DATDEB<=? and ( c.DATE_FIN is null or c.DATE_FIN>=?) ";
			sql += " and c.ID_TYPE_CONTRAT=1 ";
		}
		sql += (CDD ? " and " : " where ") + CHAMP_DATE_PREVISION_VISITE + " between ? and ? ";
		if (listeAgent != null && listeAgent.size() > 0) {

			String list = Const.CHAINE_VIDE;
			for (Integer idAg : listeAgent) {
				list += idAg + ",";
			}
			if (!list.equals(Const.CHAINE_VIDE)) {
				list = list.substring(0, list.length() - 1);
			}
			sql += " and " + CHAMP_ID_AGENT + " in (" + list + ") ";
		}

		if (recommandation!=null) {
			sql += " and " + CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE + " =" + recommandation.getIdRecommandation() + " ";
		}

		if (!statut.equals(Const.CHAINE_VIDE)) {
			sql += " and " + CHAMP_STATUT + " ='" + statut + "' ";
		}

		if (!etat.equals(Const.CHAINE_VIDE)) {
			sql += " and " + CHAMP_ETAT + " = '" + etat + "' ";
		}

		if (listeSousService != null) {
			String list = Const.CHAINE_VIDE;
			for (Integer codeServ : listeSousService) {
				list += codeServ + ",";
			}
			if (!list.equals(Const.CHAINE_VIDE))
				list = list.substring(0, list.length() - 1);
			sql += " and (" + CHAMP_ID_SERVICE_ADS + " in (" + list + ")) ";
		}

		ArrayList<SuiviMedical> listeSuiviMedical = new ArrayList<SuiviMedical>();

		List<Map<String, Object>> rows = null;
		if (CDD) {
			Date dateJour = new Date();
			rows = jdbcTemplate.queryForList(sql, new Object[] { dateJour, dateJour, dateDebut, dateFin });
		} else {
			rows = jdbcTemplate.queryForList(sql, new Object[] { dateDebut, dateFin });
		}
		for (Map<String, Object> row : rows) {
			SuiviMedical sm = new SuiviMedical();
			BigDecimal idSuivi = (BigDecimal) row.get(CHAMP_ID);
			sm.setIdSuiviMed(idSuivi.intValue());
			sm.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			sm.setNomatr((Integer) row.get(CHAMP_NOMATR));
			sm.setAgent((String) row.get(CHAMP_AGENT));
			sm.setStatut((String) row.get(CHAMP_STATUT));
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
			sm.setIdServiceAds((Integer) row.get(CHAMP_ID_SERVICE_ADS));
			sm.setIdRecommandationDerniereVisite((Integer) row.get(CHAMP_ID_RECOMMANDATION_DERNIERE_VISITE));
			sm.setCommentaireDerniereViste((String) row.get(CHAMP_COMMENTAIRE_DERNIERE_VISITE));
			listeSuiviMedical.add(sm);
		}

		return listeSuiviMedical;
	}
}
