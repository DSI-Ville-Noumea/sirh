package nc.mairie.spring.dao.metier.poste;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.connecteur.Connecteur;
import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.Const;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class FichePosteDao extends SirhDao implements FichePosteDaoInterface {

	public static final String CHAMP_ID_TITRE_POSTE = "ID_TITRE_POSTE";
	public static final String CHAMP_CODE_GRADE = "CODE_GRADE";
	public static final String CHAMP_ID_ENTITE_GEO = "ID_ENTITE_GEO";
	public static final String CHAMP_ID_BUDGET = "ID_BUDGET";
	public static final String CHAMP_ID_STATUT_FP = "ID_STATUT_FP";
	public static final String CHAMP_ID_RESPONSABLE = "ID_RESPONSABLE";
	public static final String CHAMP_ID_REMPLACEMENT = "ID_REMPLACEMENT";
	public static final String CHAMP_ID_CDTHOR_BUD = "ID_CDTHOR_BUD";
	public static final String CHAMP_ID_CDTHOR_REG = "ID_CDTHOR_REG";
	public static final String CHAMP_ID_SERVI = "ID_SERVI";
	public static final String CHAMP_DATE_FIN_VALIDITE_FP = "DATE_FIN_VALIDITE_FP";
	public static final String CHAMP_OPI = "OPI";
	public static final String CHAMP_NFA = "NFA";
	public static final String CHAMP_MISSIONS = "MISSIONS";
	public static final String CHAMP_ANNEE_CREATION = "ANNEE_CREATION";
	public static final String CHAMP_NUM_FP = "NUM_FP";
	public static final String CHAMP_DATE_DEBUT_VALIDITE_FP = "DATE_DEBUT_VALIDITE_FP";
	public static final String CHAMP_DATE_DEB_APPLI_SERV = "DATE_DEB_APPLI_SERV";
	public static final String CHAMP_OBSERVATION = "OBSERVATION";
	public static final String CHAMP_ID_NATURE_CREDIT = "ID_NATURE_CREDIT";
	public static final String CHAMP_NUM_DELIBERATION = "NUM_DELIBERATION";
	public static final String CHAMP_ID_BASE_HORAIRE_POINTAGE = "ID_BASE_HORAIRE_POINTAGE";
	public static final String CHAMP_ID_BASE_HORAIRE_ABSENCE = "ID_BASE_HORAIRE_ABSENCE";

	public FichePosteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "FICHE_POSTE";
		super.CHAMP_ID = "ID_FICHE_POSTE";
	}

	@Override
	public boolean estAffectee(Integer idFichePoste, ArrayList<Affectation> listeAff) throws Exception {
		if (idFichePoste == null || listeAff.size() <= 0)
			return false;
		return true;
	}

	@Override
	public FichePoste chercherDerniereFichePoste(Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE_CREATION + " = ? order by " + CHAMP_ID
				+ " desc";
		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee });
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal anneeCrea = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(anneeCrea == null ? null : anneeCrea.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste.size() > 0 ? liste.get(0) : null;
	}

	@Override
	public FichePoste chercherFichePosteAvecNumeroFP(String numeroFP) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NUM_FP + " = ? ";
		FichePoste cadre = (FichePoste) jdbcTemplate.queryForObject(sql, new Object[] { numeroFP },
				new BeanPropertyRowMapper<FichePoste>(FichePoste.class));
		return cadre;
	}

	@Override
	public FichePoste chercherFichePoste(Integer idFichePoste) throws Exception {
		return super.chercherObject(FichePoste.class, idFichePoste);
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecCriteresAvances(String prefixeServ, Integer idStatutFP,
			Integer idTitre, String numero, Integer idAgent) throws Exception {

		String sql = Const.CHAINE_VIDE;
		if (idAgent != null) {
			sql = "select fp.* from "
					+ NOM_TABLE
					+ " fp inner join p_titre_poste tp on fp.id_titre_poste=tp.id_titre_poste left join affectation aff1 on aff1.id_fiche_poste = fp.id_fiche_poste left join affectation aff2 on aff2.id_fiche_poste_secondaire = fp.id_fiche_poste  where fp.id_servi like '"
					+ prefixeServ + "%'";
		} else {
			sql = "select fp.* from " + NOM_TABLE
					+ " fp inner join p_titre_poste tp on fp.id_titre_poste=tp.id_titre_poste where fp.id_servi like '"
					+ prefixeServ + "%'";
		}
		if (idStatutFP != null) {
			sql += " and fp.id_statut_fp = " + idStatutFP;
		} else {
			sql += " and fp.id_statut_fp <> " + EnumStatutFichePoste.INACTIVE.getId();
		}

		if (idTitre != null)
			sql += " and fp.id_titre_poste = " + idTitre;

		if (numero != null)
			sql += " and NUM_FP LIKE '%" + numero + "%'";

		if (idAgent != null) {
			String dateJour = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			sql += " and (aff1.id_agent='" + idAgent + "' or aff2.id_agent='" + idAgent + "')";
			sql += " AND ((aff1.DATE_DEBUT_AFF <= '" + dateJour
					+ "' AND (aff1.DATE_FIN_AFF is null or  aff1.DATE_FIN_AFF >='" + dateJour
					+ "'))or(aff2.DATE_DEBUT_AFF <= '" + dateJour
					+ "' AND (aff2.DATE_FIN_AFF is null or  aff2.DATE_FIN_AFF >='" + dateJour + "')))";
		}
		sql += " order by tp.LIB_TITRE_POSTE,fp.NUM_FP,fp.id_fiche_poste";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecService(String codeService) throws Exception {
		String sql = " select fp.* from " + NOM_TABLE + " fp, p_titre_poste tp WHERE fp." + CHAMP_ID_TITRE_POSTE
				+ "=tp.id_titre_poste and " + CHAMP_ID_SERVI + "=? order by tp.LIB_TITRE_POSTE";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { codeService });
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecAgent(ArrayList<Affectation> liens) throws Exception {

		// Construction de la liste
		ArrayList<FichePoste> result = new ArrayList<FichePoste>();
		for (int i = 0; i < liens.size(); i++) {
			Affectation aLien = (Affectation) liens.get(i);
			try {
				FichePoste fp = chercherFichePoste(aLien.getIdFichePoste());
				result.add(fp);
				if (aLien.getIdFichePosteSecondaire() != null) {
					try {
						FichePoste fpSecondaire = chercherFichePoste(aLien.getIdFichePosteSecondaire());
						result.add(fpSecondaire);
					} catch (Exception e) {
						return new ArrayList<FichePoste>();
					}
				}
			} catch (Exception e) {
				return new ArrayList<FichePoste>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecTitrePoste(Integer idTitrePoste) throws Exception {
		String sql = " select * from " + NOM_TABLE + " WHERE " + CHAMP_ID_TITRE_POSTE + "=? ";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTitrePoste });
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecEntiteGeo(Integer idEntiteGeo) throws Exception {
		String sql = " select * from " + NOM_TABLE + " WHERE " + CHAMP_ID_ENTITE_GEO + "=? ";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEntiteGeo });
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteValideesOuGeleesNonAffecteesAvecNumPartiel(String numPartiel)
			throws Exception {
		String sql = "select fp.* from "
				+ NOM_TABLE
				+ " FP, R_STATUT_FP STAT WHERE NUM_FP LIKE '%"
				+ numPartiel
				+ "%' AND ID_FICHE_POSTE NOT IN (SELECT ID_FICHE_POSTE_SECONDAIRE FROM AFFECTATION  WHERE DATE_FIN_AFF is null and id_fiche_poste_secondaire is not null) "
				+ "AND ID_FICHE_POSTE NOT IN (SELECT ID_FICHE_POSTE FROM AFFECTATION WHERE DATE_FIN_AFF is null) AND STAT.ID_STATUT_FP = FP.ID_STATUT_FP "
				+ "AND (STAT.LIB_STATUT_FP = '" + EnumStatutFichePoste.VALIDEE.getLibLong()
				+ "' OR STAT.LIB_STATUT_FP = '" + EnumStatutFichePoste.GELEE.getLibLong() + "') WITH UR";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteAvecNumPartiel(String numPartiel) throws Exception {
		String sql = " select * from " + NOM_TABLE + " WHERE " + CHAMP_NUM_FP + " like ? ";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { "%" + numPartiel + "%" });
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FichePoste> listerFichePosteValideesouGeleeNonAffectees() throws Exception {

		String sql = "select fp.* from "
				+ NOM_TABLE
				+ " FP, R_STATUT_FP STAT WHERE FP.ID_FICHE_POSTE NOT IN (SELECT ID_FICHE_POSTE_SECONDAIRE FROM AFFECTATION  WHERE DATE_FIN_AFF is null and id_fiche_poste_secondaire is not null) "
				+ "AND FP.ID_FICHE_POSTE NOT IN (SELECT ID_FICHE_POSTE FROM AFFECTATION WHERE DATE_FIN_AFF is null) AND STAT.ID_STATUT_FP = FP.ID_STATUT_FP "
				+ "AND (STAT.LIB_STATUT_FP = '" + EnumStatutFichePoste.VALIDEE.getLibLong()
				+ "' or STAT.LIB_STATUT_FP = '" + EnumStatutFichePoste.GELEE.getLibLong() + "') WITH UR";

		ArrayList<FichePoste> liste = new ArrayList<FichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			FichePoste a = new FichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID));
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID_TITRE_POSTE));
			BigDecimal entiteGeo = (BigDecimal) row.get(CHAMP_ID_ENTITE_GEO);
			a.setIdEntiteGeo(entiteGeo == null ? null : entiteGeo.intValue());
			a.setIdBudget((Integer) row.get(CHAMP_ID_BUDGET));
			a.setIdStatutFp((Integer) row.get(CHAMP_ID_STATUT_FP));
			a.setIdResponsable((Integer) row.get(CHAMP_ID_RESPONSABLE));
			a.setIdRemplacement((Integer) row.get(CHAMP_ID_REMPLACEMENT));
			BigDecimal bud = (BigDecimal) row.get(CHAMP_ID_CDTHOR_BUD);
			a.setIdCdthorBud(bud == null ? null : bud.intValue());
			BigDecimal reg = (BigDecimal) row.get(CHAMP_ID_CDTHOR_REG);
			a.setIdCdthorReg(reg == null ? null : reg.intValue());
			a.setIdServi((String) row.get(CHAMP_ID_SERVI));
			a.setDateFinValiditeFp((Date) row.get(CHAMP_DATE_FIN_VALIDITE_FP));
			a.setOpi((String) row.get(CHAMP_OPI));
			a.setNfa((String) row.get(CHAMP_NFA));
			a.setMissions((String) row.get(CHAMP_MISSIONS));
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_CREATION);
			a.setAnneeCreation(annee == null ? null : annee.intValue());
			a.setNumFp((String) row.get(CHAMP_NUM_FP));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setObservation((String) row.get(CHAMP_OBSERVATION));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public List<FichePoste> listerFichePoste() throws Exception {
		return super.getListe(FichePoste.class);
	}

	@Override
	public String createFichePosteNumber(Integer annee) throws Exception {
		// RG_PE_FP_C01
		FichePoste derniereFP = null;
		try {
			derniereFP = chercherDerniereFichePoste(annee);
		} catch (Exception e) {

		}

		if (derniereFP != null && derniereFP.getIdFichePoste() != null) {
			return (annee + "/" + String.valueOf(Integer.parseInt(derniereFP.getNumFp().substring(5)) + 1));
		} else {
			return (annee + "/" + String.valueOf(1));
		}
	}

	@Override
	public void modifierFichePoste(FichePoste fp, HistoFichePosteDao histoDao, UserAppli user,
			Transaction aTransaction, AffectationDao affDao) throws Exception {
		// Modification du FichePoste

		// historisation
		HistoFichePoste histoFP = new HistoFichePoste(fp);
		histoDao.creerHistoFichePoste(histoFP, user, EnumTypeHisto.MODIFICATION);

		modifierFichePosteBD(fp);
		FichePoste fpResponsable = null;
		if (fp.getIdResponsable() != null) {
			fpResponsable = chercherFichePoste(fp.getIdResponsable());
		}
		ArrayList<Affectation> listeAffFP = affDao.listerAffectationAvecFP(fp.getIdFichePoste());
		Connecteur.modifierSPPOST(aTransaction, fp, fpResponsable, listeAffFP);

	}

	@Override
	public Integer creerFichePoste(FichePoste fp, UserAppli user, HistoFichePosteDao histoDao, Transaction aTransaction)
			throws Exception {
		// Génération du numéro de fiche de poste
		fp.setNumFp(createFichePosteNumber(fp.getAnneeCreation()));

		// Creation de la FichePoste
		Integer idCRee = creerFichePosteBD(fp);

		FichePoste fpResponsable = null;
		if (fp.getIdResponsable() != null) {
			fpResponsable = chercherFichePoste(fp.getIdResponsable());
		}
		Connecteur.creerSPPOST(aTransaction, fp, fpResponsable);

		return idCRee;
	}

	public void modifierFichePosteBD(FichePoste fichePoste) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TITRE_POSTE + "=?," + CHAMP_ID_ENTITE_GEO + "=?,"
				+ CHAMP_ID_BUDGET + "=?," + CHAMP_ID_STATUT_FP + "=?," + CHAMP_ID_RESPONSABLE + "=?,"
				+ CHAMP_ID_REMPLACEMENT + "=?," + CHAMP_ID_CDTHOR_BUD + "=?," + CHAMP_ID_CDTHOR_REG + "=?,"
				+ CHAMP_ID_SERVI + "=?," + CHAMP_DATE_FIN_VALIDITE_FP + "=?," + CHAMP_OPI + "=?," + CHAMP_NFA + "=?,"
				+ CHAMP_MISSIONS + "=?," + CHAMP_ANNEE_CREATION + "=?," + CHAMP_NUM_FP + "=?,"
				+ CHAMP_DATE_DEBUT_VALIDITE_FP + "=?," + CHAMP_DATE_DEB_APPLI_SERV + "=?," + CHAMP_OBSERVATION + "=?,"
				+ CHAMP_CODE_GRADE + "=?," + CHAMP_ID_NATURE_CREDIT + "=?," + CHAMP_NUM_DELIBERATION + "=?,"
				+ CHAMP_ID_BASE_HORAIRE_POINTAGE + "=?," + CHAMP_ID_BASE_HORAIRE_ABSENCE + "=? where " + CHAMP_ID
				+ " =?";
		jdbcTemplate.update(
				sql,
				new Object[] { fichePoste.getIdTitrePoste(), fichePoste.getIdEntiteGeo(), fichePoste.getIdBudget(),
						fichePoste.getIdStatutFp(), fichePoste.getIdResponsable(), fichePoste.getIdRemplacement(),
						fichePoste.getIdCdthorBud(), fichePoste.getIdCdthorReg(), fichePoste.getIdServi(),
						fichePoste.getDateFinValiditeFp(), fichePoste.getOpi(), fichePoste.getNfa(),
						fichePoste.getMissions(), fichePoste.getAnneeCreation(), fichePoste.getNumFp(),
						fichePoste.getDateDebutValiditeFp(), fichePoste.getDateDebAppliServ(),
						fichePoste.getObservation(), fichePoste.getCodeGrade(), fichePoste.getIdNatureCredit(),
						fichePoste.getNumDeliberation(), fichePoste.getIdBaseHorairePointage(),
						fichePoste.getIdBaseHoraireAbsence(), fichePoste.getIdFichePoste() });
	}

	public Integer creerFichePosteBD(FichePoste fp) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TITRE_POSTE
				+ "," + CHAMP_ID_ENTITE_GEO + "," + CHAMP_ID_BUDGET + "," + CHAMP_ID_STATUT_FP + ","
				+ CHAMP_ID_RESPONSABLE + "," + CHAMP_ID_REMPLACEMENT + "," + CHAMP_ID_CDTHOR_BUD + ","
				+ CHAMP_ID_CDTHOR_REG + "," + CHAMP_ID_SERVI + "," + CHAMP_DATE_FIN_VALIDITE_FP + "," + CHAMP_OPI + ","
				+ CHAMP_NFA + "," + CHAMP_MISSIONS + "," + CHAMP_ANNEE_CREATION + "," + CHAMP_NUM_FP + ","
				+ CHAMP_DATE_DEBUT_VALIDITE_FP + "," + CHAMP_DATE_DEB_APPLI_SERV + "," + CHAMP_OBSERVATION + ","
				+ CHAMP_CODE_GRADE + "," + CHAMP_ID_NATURE_CREDIT + "," + CHAMP_NUM_DELIBERATION + ","
				+ CHAMP_ID_BASE_HORAIRE_POINTAGE + "," + CHAMP_ID_BASE_HORAIRE_ABSENCE + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?))";
		Integer id = jdbcTemplate.queryForObject(
				sql,
				new Object[] { fp.getIdTitrePoste(), fp.getIdEntiteGeo(), fp.getIdBudget(), fp.getIdStatutFp(),
						fp.getIdResponsable(), fp.getIdRemplacement(), fp.getIdCdthorBud(), fp.getIdCdthorReg(),
						fp.getIdServi(), fp.getDateFinValiditeFp(), fp.getOpi(), fp.getNfa(), fp.getMissions(),
						fp.getAnneeCreation(), fp.getNumFp(), fp.getDateDebutValiditeFp(), fp.getDateDebAppliServ(),
						fp.getObservation(), fp.getCodeGrade(), fp.getIdNatureCredit(), fp.getNumDeliberation(),
						fp.getIdBaseHorairePointage(), fp.getIdBaseHoraireAbsence() }, Integer.class);
		return id;
	}
}
