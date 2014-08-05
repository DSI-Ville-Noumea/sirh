package nc.mairie.spring.dao.metier.poste;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoFichePosteDao extends SirhDao implements HistoFichePosteDaoInterface {

	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_ID_TITRE_POSTE = "ID_TITRE_POSTE";
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
	public static final String CHAMP_CODE_GRADE = "CODE_GRADE";
	public static final String CHAMP_NUM_FP = "NUM_FP";
	public static final String CHAMP_DATE_HISTO = "DATE_HISTO";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";
	public static final String CHAMP_DATE_DEBUT_VALIDITE_FP = "DATE_DEBUT_VALIDITE_FP";
	public static final String CHAMP_DATE_DEB_APPLI_SERV = "DATE_DEB_APPLI_SERV";
	public static final String CHAMP_DATE_FIN_APPLI_SERV = "DATE_FIN_APPLI_SERV";
	public static final String CHAMP_ID_NATURE_CREDIT = "ID_NATURE_CREDIT";
	public static final String CHAMP_NUM_DELIBERATION = "NUM_DELIBERATION";

	public HistoFichePosteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_FICHE_POSTE";
	}

	@Override
	public void creerHistoFichePoste(HistoFichePoste histoFP, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		// avant de creer une histo, on met une date de fin à la precedente
		ArrayList<HistoFichePoste> ancienHisto = listerHistoFichePosteById(histoFP.getIdFichePoste());
		if (ancienHisto != null && ancienHisto.size() > 0) {
			HistoFichePoste histoAModifier = (HistoFichePoste) ancienHisto.get(0);
			histoAModifier.setDateFinAppliServ(histoFP.getDateDebAppliServ());
			modifierDateFinAppliServHistoFichePoste(histoAModifier);
		}

		histoFP.setUserHisto(user.getUserName());
		histoFP.setTypeHisto(typeHisto.getValue());
		histoFP.setDateFinAppliServ(null);

		// Creation du HistoFichePoste
		creerHistoFichePosteBD(histoFP);

	}

	@Override
	public void modifierDateFinAppliServHistoFichePoste(HistoFichePoste histoFP) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_FIN_APPLI_SERV + "=? where " + CHAMP_ID_FICHE_POSTE
				+ " =?";
		jdbcTemplate.update(sql, new Object[] { histoFP.getDateFinAppliServ(), histoFP.getIdFichePoste() });

	}

	@Override
	public ArrayList<HistoFichePoste> listerHistoFichePosteById(Integer idFichePoste) throws Exception {
		String sql = " select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? order by "
				+ CHAMP_DATE_HISTO + " desc";

		ArrayList<HistoFichePoste> liste = new ArrayList<HistoFichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			HistoFichePoste a = new HistoFichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
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
			a.setTypeHisto((String) row.get(CHAMP_TYPE_HISTO));
			a.setUserHisto((String) row.get(CHAMP_USER_HISTO));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setDateFinAppliServ((Date) row.get(CHAMP_DATE_FIN_APPLI_SERV));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<HistoFichePoste> listerHistoFichePosteDansDate(Integer idFichePoste, Date dateDebutAff,
			Date dateFinAff) throws Exception {
		String sql = " select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? and "
				+ CHAMP_DATE_DEB_APPLI_SERV + ">= ? and " + CHAMP_DATE_DEB_APPLI_SERV + " is not null and "
				+ CHAMP_DATE_DEB_APPLI_SERV + "< ? order by " + CHAMP_DATE_HISTO + " desc";

		ArrayList<HistoFichePoste> liste = new ArrayList<HistoFichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste, dateDebutAff,
				dateFinAff });
		for (Map<String, Object> row : rows) {
			HistoFichePoste a = new HistoFichePoste();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
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
			a.setTypeHisto((String) row.get(CHAMP_TYPE_HISTO));
			a.setUserHisto((String) row.get(CHAMP_USER_HISTO));
			a.setDateDebutValiditeFp((Date) row.get(CHAMP_DATE_DEBUT_VALIDITE_FP));
			a.setDateDebAppliServ((Date) row.get(CHAMP_DATE_DEB_APPLI_SERV));
			a.setDateFinAppliServ((Date) row.get(CHAMP_DATE_FIN_APPLI_SERV));
			a.setCodeGrade((String) row.get(CHAMP_CODE_GRADE));
			a.setIdNatureCredit((Integer) row.get(CHAMP_ID_NATURE_CREDIT));
			a.setNumDeliberation((String) row.get(CHAMP_NUM_DELIBERATION));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void creerHistoFichePosteBD(HistoFichePoste histoFP) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_POSTE + "," + CHAMP_ID_TITRE_POSTE + ","
				+ CHAMP_ID_ENTITE_GEO + "," + CHAMP_ID_BUDGET + "," + CHAMP_ID_STATUT_FP + "," + CHAMP_ID_RESPONSABLE
				+ "," + CHAMP_ID_REMPLACEMENT + "," + CHAMP_ID_CDTHOR_BUD + "," + CHAMP_ID_CDTHOR_REG + ","
				+ CHAMP_ID_SERVI + "," + CHAMP_DATE_FIN_VALIDITE_FP + "," + CHAMP_OPI + "," + CHAMP_NFA + ","
				+ CHAMP_MISSIONS + "," + CHAMP_ANNEE_CREATION + "," + CHAMP_NUM_FP + "," + CHAMP_DATE_HISTO + ","
				+ CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + "," + CHAMP_DATE_DEBUT_VALIDITE_FP + ","
				+ CHAMP_DATE_DEB_APPLI_SERV + "," + CHAMP_DATE_FIN_APPLI_SERV + "," + CHAMP_CODE_GRADE + ","
				+ CHAMP_ID_NATURE_CREDIT + "," + CHAMP_NUM_DELIBERATION + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histoFP.getIdFichePoste(), histoFP.getIdTitrePoste(), histoFP.getIdEntiteGeo(),
						histoFP.getIdBudget(), histoFP.getIdStatutFp(), histoFP.getIdResponsable(),
						histoFP.getIdRemplacement(), histoFP.getIdCdthorBud(), histoFP.getIdCdthorReg(),
						histoFP.getIdServi(), histoFP.getDateFinValiditeFp(), histoFP.getOpi(), histoFP.getNfa(),
						histoFP.getMissions(), histoFP.getAnneeCreation(), histoFP.getNumFp(), new Date(),
						histoFP.getTypeHisto(), histoFP.getUserHisto(), histoFP.getDateDebutValiditeFp(),
						histoFP.getDateDebAppliServ(), histoFP.getDateFinAppliServ(), histoFP.getCodeGrade(),
						histoFP.getIdNatureCredit(), histoFP.getNumDeliberation() });
	}
}
