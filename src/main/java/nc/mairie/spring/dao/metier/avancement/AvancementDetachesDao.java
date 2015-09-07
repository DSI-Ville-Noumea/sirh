package nc.mairie.spring.dao.metier.avancement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AvancementDetachesDao extends SirhDao implements AvancementDetachesDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_MOTIF_AVCT = "ID_MOTIF_AVCT";
	public static final String CHAMP_DIRECTION_SERVICE = "DIRECTION_SERVICE";
	public static final String CHAMP_SECTION_SERVICE = "SECTION_SERVICE";
	public static final String CHAMP_FILIERE = "FILIERE";
	public static final String CHAMP_GRADE = "GRADE";
	public static final String CHAMP_ID_NOUV_GRADE = "ID_NOUV_GRADE";
	public static final String CHAMP_CDCADR = "CDCADR";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_BM_ANNEE = "BM_ANNEE";
	public static final String CHAMP_BM_MOIS = "BM_MOIS";
	public static final String CHAMP_BM_JOUR = "BM_JOUR";
	public static final String CHAMP_ACC_ANNEE = "ACC_ANNEE";
	public static final String CHAMP_ACC_MOIS = "ACC_MOIS";
	public static final String CHAMP_ACC_JOUR = "ACC_JOUR";
	public static final String CHAMP_NOUV_BM_ANNEE = "NOUV_BM_ANNEE";
	public static final String CHAMP_NOUV_BM_MOIS = "NOUV_BM_MOIS";
	public static final String CHAMP_NOUV_BM_JOUR = "NOUV_BM_JOUR";
	public static final String CHAMP_NOUV_ACC_ANNEE = "NOUV_ACC_ANNEE";
	public static final String CHAMP_NOUV_ACC_MOIS = "NOUV_ACC_MOIS";
	public static final String CHAMP_NOUV_ACC_JOUR = "NOUV_ACC_JOUR";
	public static final String CHAMP_IBAN = "IBAN";
	public static final String CHAMP_INM = "INM";
	public static final String CHAMP_INA = "INA";
	public static final String CHAMP_NOUV_IBAN = "NOUV_IBAN";
	public static final String CHAMP_NOUV_INM = "NOUV_INM";
	public static final String CHAMP_NOUV_INA = "NOUV_INA";
	public static final String CHAMP_DATE_GRADE = "DATE_GRADE";
	public static final String CHAMP_PERIODE_STANDARD = "PERIODE_STANDARD";
	public static final String CHAMP_DATE_AVCT_MOY = "DATE_AVCT_MOY";
	public static final String CHAMP_NUM_ARRETE = "NUM_ARRETE";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_ETAT = "ETAT";
	public static final String CHAMP_CODE_CATEGORIE = "CODE_CATEGORIE";
	public static final String CHAMP_CARRIERE_SIMU = "CARRIERE_SIMU";
	public static final String CHAMP_USER_VERIF_SGC = "USER_VERIF_SGC";
	public static final String CHAMP_DATE_VERIF_SGC = "DATE_VERIF_SGC";
	public static final String CHAMP_HEURE_VERIF_SGC = "HEURE_VERIF_SGC";
	public static final String CHAMP_USER_VERIF_SEF = "USER_VERIF_SEF";
	public static final String CHAMP_DATE_VERIF_SEF = "DATE_VERIF_SEF";
	public static final String CHAMP_HEURE_VERIF_SEF = "HEURE_VERIF_SEF";
	public static final String CHAMP_USER_VERIF_ARR = "USER_VERIF_ARR";
	public static final String CHAMP_DATE_VERIF_ARR = "DATE_VERIF_ARR";
	public static final String CHAMP_HEURE_VERIF_ARR = "HEURE_VERIF_ARR";
	public static final String CHAMP_OBSERVATION_ARR = "OBSERVATION_ARR";
	public static final String CHAMP_USER_VERIF_ARR_IMPR = "USER_VERIF_ARR_IMPR";
	public static final String CHAMP_DATE_VERIF_ARR_IMPR = "DATE_VERIF_ARR_IMPR";
	public static final String CHAMP_HEURE_VERIF_ARR_IMPR = "HEURE_VERIF_ARR_IMPR";
	public static final String CHAMP_REGULARISATION = "REGULARISATION";
	public static final String CHAMP_AGENT_VDN = "AGENT_VDN";
	public static final String CHAMP_CODE_PA = "CODE_PA";

	public AvancementDetachesDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVCT_DETACHE";
		super.CHAMP_ID = "ID_AVCT";
	}

	@Override
	public ArrayList<AvancementDetaches> listerAvancementAvecAnneeEtat(Integer annee, String etat, String libFiliere,
			Integer idAgent, List<String> listeSousService, String categorie) throws Exception {

		String reqWhere = Const.CHAINE_VIDE;
		if (libFiliere != null) {
			reqWhere = " and " + CHAMP_FILIERE + " = '" + libFiliere + "' ";
		}
		if (categorie != null) {
			reqWhere = " and " + CHAMP_CDCADR + " = '" + categorie + "' ";
		}
		if (etat != null) {
			reqWhere += etat;
		}
		if (idAgent != null) {
			reqWhere += " and " + CHAMP_ID_AGENT + "=" + idAgent + " ";
		}

		if (listeSousService != null) {
			String list = Const.CHAINE_VIDE;
			for (String sigleServ : listeSousService) {
				list += "'" + sigleServ + "',";
			}
			if (!list.equals(Const.CHAINE_VIDE))
				list = list.substring(0, list.length() - 1);
			reqWhere += " and (" + CHAMP_DIRECTION_SERVICE + " in (" + list + ") or " + CHAMP_SECTION_SERVICE + " in ("
					+ list + ")) ";
		}

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? " + reqWhere + " order by "
				+ CHAMP_ID_AGENT;

		ArrayList<AvancementDetaches> liste = new ArrayList<AvancementDetaches>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee });
		for (Map<String, Object> row : rows) {
			AvancementDetaches a = new AvancementDetaches();
			a.setIdAvct((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdMotifAvct((Integer) row.get(CHAMP_ID_MOTIF_AVCT));
			a.setDirectionService((String) row.get(CHAMP_DIRECTION_SERVICE));
			a.setSectionService((String) row.get(CHAMP_SECTION_SERVICE));
			a.setFiliere((String) row.get(CHAMP_FILIERE));
			a.setGrade((String) row.get(CHAMP_GRADE));
			a.setIdNouvGrade((String) row.get(CHAMP_ID_NOUV_GRADE));
			a.setCdcadr((String) row.get(CHAMP_CDCADR));
			a.setAnnee((Integer) row.get(CHAMP_ANNEE));
			a.setBmAnnee((Integer) row.get(CHAMP_BM_ANNEE));
			a.setBmMois((Integer) row.get(CHAMP_BM_MOIS));
			a.setBmJour((Integer) row.get(CHAMP_BM_JOUR));
			a.setAccAnnee((Integer) row.get(CHAMP_ACC_ANNEE));
			a.setAccMois((Integer) row.get(CHAMP_ACC_MOIS));
			a.setAccJour((Integer) row.get(CHAMP_ACC_JOUR));
			a.setNouvBmAnnee((Integer) row.get(CHAMP_NOUV_BM_ANNEE));
			a.setNouvBmMois((Integer) row.get(CHAMP_NOUV_BM_MOIS));
			a.setNouvBmJour((Integer) row.get(CHAMP_NOUV_BM_JOUR));
			a.setNouvAccAnnee((Integer) row.get(CHAMP_NOUV_ACC_ANNEE));
			a.setNouvAccMois((Integer) row.get(CHAMP_NOUV_ACC_MOIS));
			a.setNouvAccJour((Integer) row.get(CHAMP_NOUV_ACC_JOUR));
			a.setIban((String) row.get(CHAMP_IBAN));
			a.setInm((Integer) row.get(CHAMP_INM));
			a.setIna((Integer) row.get(CHAMP_INA));
			a.setNouvIban((String) row.get(CHAMP_NOUV_IBAN));
			a.setNouvInm((Integer) row.get(CHAMP_NOUV_INM));
			a.setNouvIna((Integer) row.get(CHAMP_NOUV_INA));
			a.setDateGrade((Date) row.get(CHAMP_DATE_GRADE));
			BigDecimal periode = (BigDecimal) row.get(CHAMP_PERIODE_STANDARD);
			a.setPeriodeStandard(periode.intValue());
			a.setDateAvctMoy((Date) row.get(CHAMP_DATE_AVCT_MOY));
			a.setNumArrete((String) row.get(CHAMP_NUM_ARRETE));
			a.setDateArrete((Date) row.get(CHAMP_DATE_ARRETE));
			a.setEtat((String) row.get(CHAMP_ETAT));
			a.setCodeCategorie((Integer) row.get(CHAMP_CODE_CATEGORIE));
			a.setCarriereSimu((String) row.get(CHAMP_CARRIERE_SIMU));
			a.setUserVerifSgc((String) row.get(CHAMP_USER_VERIF_SGC));
			a.setDateVerifSgc((Date) row.get(CHAMP_DATE_VERIF_SGC));
			a.setHeureVerifSgc((String) row.get(CHAMP_HEURE_VERIF_SGC));
			a.setUserVerifSef((String) row.get(CHAMP_USER_VERIF_SEF));
			a.setDateVerifSef((Date) row.get(CHAMP_DATE_VERIF_SEF));
			a.setHeureVerifSef((String) row.get(CHAMP_HEURE_VERIF_SEF));
			a.setUserVerifArr((String) row.get(CHAMP_USER_VERIF_ARR));
			a.setDateVerifArr((Date) row.get(CHAMP_DATE_VERIF_ARR));
			a.setHeureVerifArr((String) row.get(CHAMP_HEURE_VERIF_ARR));
			a.setObservationArr((String) row.get(CHAMP_OBSERVATION_ARR));
			a.setUserVerifArrImpr((String) row.get(CHAMP_USER_VERIF_ARR_IMPR));
			a.setDateVerifArrImpr((Date) row.get(CHAMP_DATE_VERIF_ARR_IMPR));
			a.setHeureVerifArrImpr((String) row.get(CHAMP_HEURE_VERIF_ARR_IMPR));
			Integer regu = (Integer) row.get(CHAMP_REGULARISATION);
			a.setRegularisation(regu == 1 ? true : false);
			Integer agentVDN = (Integer) row.get(CHAMP_AGENT_VDN);
			a.setAgentVdn(agentVDN == 1 ? true : false);
			a.setCodePa((String) row.get(CHAMP_CODE_PA));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public AvancementDetaches chercherAvancementAvecAnneeEtAgent(Integer annee, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + " = ? and " + CHAMP_ID_AGENT + "=?";
		AvancementDetaches cadre = (AvancementDetaches) jdbcTemplate.queryForObject(sql,
				new Object[] { annee, idAgent },
				new BeanPropertyRowMapper<AvancementDetaches>(AvancementDetaches.class));
		return cadre;
	}

	@Override
	public void supprimerAvancementTravailAvecCategorie(Integer annee) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ANNEE + "=? and " + CHAMP_ETAT + "=?";
		jdbcTemplate.update(sql, new Object[] { annee, "T" });
	}

	@Override
	public void modifierAvancement(Integer idAvct, Integer idAgent, Integer idMotifAvct, String directionService,
			String sectionService, String filiere, String grade, String idNouvGrade, Integer annee, String cdcadr,
			Integer bmAnnee, Integer bmMois, Integer bmJour, Integer accAnnee, Integer accMois, Integer accJour,
			Integer nouvBmAnnee, Integer nouvBmMois, Integer nouvBmJour, Integer nouvAccAnnee, Integer nouvAccMois,
			Integer nouvAccJour, String iban, Integer inm, Integer ina, String nouvIban, Integer nouvInm,
			Integer nouvIna, Date dateGrade, Integer periodeStandard, Date dateAvctMoy, String numArrete,
			Date dateArrete, String etat, Integer codeCategorie, String carriereSimu, String userVerifSgc,
			Date dateVerifSgc, String heureVerifSgc, String userVerifSef, Date dateVerifSef, String heureVerifSef,
			String userVerifArr, Date dateVerifArr, String heureVerifArr, String observationArr,
			String userVerifArrImpr, Date dateVerifArrImpr, String heureVerifArrImpr, boolean regularisation,
			boolean agentVdn, String codePa) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_ID_MOTIF_AVCT + "=?,"
				+ CHAMP_DIRECTION_SERVICE + "=?," + CHAMP_SECTION_SERVICE + "=?," + CHAMP_FILIERE + "=?," + CHAMP_GRADE
				+ "=?," + CHAMP_ID_NOUV_GRADE + "=?," + CHAMP_CDCADR + "=?," + CHAMP_ANNEE + "=?," + CHAMP_BM_ANNEE
				+ "=?," + CHAMP_BM_MOIS + "=?," + CHAMP_BM_JOUR + "=?," + CHAMP_ACC_ANNEE + "=?," + CHAMP_ACC_MOIS
				+ "=?," + CHAMP_ACC_JOUR + "=?," + CHAMP_NOUV_BM_ANNEE + "=?," + CHAMP_NOUV_BM_MOIS + "=?,"
				+ CHAMP_NOUV_BM_JOUR + "=?," + CHAMP_NOUV_ACC_ANNEE + "=?," + CHAMP_NOUV_ACC_MOIS + "=?,"
				+ CHAMP_NOUV_ACC_JOUR + "=?," + CHAMP_IBAN + "=?," + CHAMP_INM + "=?," + CHAMP_INA + "=?,"
				+ CHAMP_NOUV_IBAN + "=?," + CHAMP_NOUV_INM + "=?," + CHAMP_NOUV_INA + "=?," + CHAMP_DATE_GRADE + "=?,"
				+ CHAMP_PERIODE_STANDARD + "=?," + CHAMP_DATE_AVCT_MOY + "=?," + CHAMP_NUM_ARRETE + "=?,"
				+ CHAMP_DATE_ARRETE + "=?," + CHAMP_ETAT + "=?," + CHAMP_CODE_CATEGORIE + "=?," + CHAMP_CARRIERE_SIMU
				+ "=?," + CHAMP_USER_VERIF_SGC + "=?," + CHAMP_DATE_VERIF_SGC + "=?," + CHAMP_HEURE_VERIF_SGC + "=?,"
				+ CHAMP_USER_VERIF_SEF + "=?," + CHAMP_DATE_VERIF_SEF + "=?," + CHAMP_HEURE_VERIF_SEF + "=?,"
				+ CHAMP_USER_VERIF_ARR + "=?," + CHAMP_DATE_VERIF_ARR + "=?," + CHAMP_HEURE_VERIF_ARR + "=?,"
				+ CHAMP_OBSERVATION_ARR + "=?," + CHAMP_USER_VERIF_ARR_IMPR + "=?," + CHAMP_DATE_VERIF_ARR_IMPR + "=?,"
				+ CHAMP_HEURE_VERIF_ARR_IMPR + "=?," + CHAMP_REGULARISATION + "=?," + CHAMP_AGENT_VDN + "=?,"
				+ CHAMP_CODE_PA + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idMotifAvct, directionService, sectionService, filiere, grade,
				idNouvGrade, cdcadr, annee, bmAnnee, bmMois, bmJour, accAnnee, accMois, accJour, nouvBmAnnee,
				nouvBmMois, nouvBmJour, nouvAccAnnee, nouvAccMois, nouvAccJour, iban, inm, ina, nouvIban, nouvInm,
				nouvIna, dateGrade, periodeStandard, dateAvctMoy, numArrete, dateArrete, etat, codeCategorie,
				carriereSimu, userVerifSgc, dateVerifSgc, heureVerifSgc, userVerifSef, dateVerifSef, heureVerifSef,
				userVerifArr, dateVerifArr, heureVerifArr, observationArr, userVerifArrImpr, dateVerifArrImpr,
				heureVerifArrImpr, regularisation ? 1 : 0, agentVdn ? 1 : 0, codePa, idAvct });
	}

	@Override
	public void creerAvancement(Integer idAgent, Integer idMotifAvct, String directionService, String sectionService,
			String filiere, String grade, String idNouvGrade, Integer annee, String cdcadr, Integer bmAnnee,
			Integer bmMois, Integer bmJour, Integer accAnnee, Integer accMois, Integer accJour, Integer nouvBmAnnee,
			Integer nouvBmMois, Integer nouvBmJour, Integer nouvAccAnnee, Integer nouvAccMois, Integer nouvAccJour,
			String iban, Integer inm, Integer ina, String nouvIban, Integer nouvInm, Integer nouvIna, Date dateGrade,
			Integer periodeStandard, Date dateAvctMoy, String numArrete, Date dateArrete, String etat,
			Integer codeCategorie, String carriereSimu, String userVerifSgc, Date dateVerifSgc, String heureVerifSgc,
			String userVerifSef, Date dateVerifSef, String heureVerifSef, String userVerifArr, Date dateVerifArr,
			String heureVerifArr, String observationArr, String userVerifArrImpr, Date dateVerifArrImpr,
			String heureVerifArrImpr, boolean regularisation, boolean agentVdn, String codePa) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_MOTIF_AVCT + ","
				+ CHAMP_DIRECTION_SERVICE + "," + CHAMP_SECTION_SERVICE + "," + CHAMP_FILIERE + "," + CHAMP_GRADE + ","
				+ CHAMP_ID_NOUV_GRADE + "," + CHAMP_CDCADR + "," + CHAMP_ANNEE + "," + CHAMP_BM_ANNEE + ","
				+ CHAMP_BM_MOIS + "," + CHAMP_BM_JOUR + "," + CHAMP_ACC_ANNEE + "," + CHAMP_ACC_MOIS + ","
				+ CHAMP_ACC_JOUR + "," + CHAMP_NOUV_BM_ANNEE + "," + CHAMP_NOUV_BM_MOIS + "," + CHAMP_NOUV_BM_JOUR
				+ "," + CHAMP_NOUV_ACC_ANNEE + "," + CHAMP_NOUV_ACC_MOIS + "," + CHAMP_NOUV_ACC_JOUR + "," + CHAMP_IBAN
				+ "," + CHAMP_INM + "," + CHAMP_INA + "," + CHAMP_NOUV_IBAN + "," + CHAMP_NOUV_INM + ","
				+ CHAMP_NOUV_INA + "," + CHAMP_DATE_GRADE + "," + CHAMP_PERIODE_STANDARD + "," + CHAMP_DATE_AVCT_MOY
				+ "," + CHAMP_NUM_ARRETE + "," + CHAMP_DATE_ARRETE + "," + CHAMP_ETAT + "," + CHAMP_CODE_CATEGORIE
				+ "," + CHAMP_CARRIERE_SIMU + "," + CHAMP_USER_VERIF_SGC + "," + CHAMP_DATE_VERIF_SGC + ","
				+ CHAMP_HEURE_VERIF_SGC + "," + CHAMP_USER_VERIF_SEF + "," + CHAMP_DATE_VERIF_SEF + ","
				+ CHAMP_HEURE_VERIF_SEF + "," + CHAMP_USER_VERIF_ARR + "," + CHAMP_DATE_VERIF_ARR + ","
				+ CHAMP_HEURE_VERIF_ARR + "," + CHAMP_OBSERVATION_ARR + "," + CHAMP_USER_VERIF_ARR_IMPR + ","
				+ CHAMP_DATE_VERIF_ARR_IMPR + "," + CHAMP_HEURE_VERIF_ARR_IMPR + "," + CHAMP_REGULARISATION + ","
				+ CHAMP_AGENT_VDN + "," + CHAMP_CODE_PA + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
				+ ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idMotifAvct, directionService, sectionService, filiere, grade,
				idNouvGrade, cdcadr, annee, bmAnnee, bmMois, bmJour, accAnnee, accMois, accJour, nouvBmAnnee,
				nouvBmMois, nouvBmJour, nouvAccAnnee, nouvAccMois, nouvAccJour, iban, inm, ina, nouvIban, nouvInm,
				nouvIna, dateGrade, periodeStandard, dateAvctMoy, numArrete, dateArrete, etat, codeCategorie,
				carriereSimu, userVerifSgc, dateVerifSgc, heureVerifSgc, userVerifSef, dateVerifSef, heureVerifSef,
				userVerifArr, dateVerifArr, heureVerifArr, observationArr, userVerifArrImpr, dateVerifArrImpr,
				heureVerifArrImpr, regularisation ? 1 : 0, agentVdn ? 1 : 0, codePa });
	}
}
