package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoAffectation;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoAffectationDao extends SirhDao implements HistoAffectationDaoInterface {

	public static final String CHAMP_ID_AFFECTATION = "ID_AFFECTATION";
	public static final String CHAMP_ID_MOTIF_AFFECTATION = "ID_MOTIF_AFFECTATION";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_REF_ARRETE_AFF = "REF_ARRETE_AFF";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_DATE_DEBUT_AFF = "DATE_DEBUT_AFF";
	public static final String CHAMP_DATE_FIN_AFF = "DATE_FIN_AFF";
	public static final String CHAMP_TEMPS_TRAVAIL = "TEMPS_TRAVAIL";
	public static final String CHAMP_CODE_ECOLE = "CODE_ECOLE";
	public static final String CHAMP_ID_FICHE_POSTE_SECONDAIRE = "ID_FICHE_POSTE_SECONDAIRE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";
	public static final String CHAMP_DATE_HISTO = "DATE_HISTO";
	public static final String CHAMP_ID_BASE_HORAIRE_POINTAGE = "ID_BASE_HORAIRE_POINTAGE";
	public static final String CHAMP_ID_BASE_HORAIRE_ABSENCE = "ID_BASE_HORAIRE_ABSENCE";

	public HistoAffectationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_AFFECTATION";
	}

	private void creerHistoAffectationBD(HistoAffectation histo) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AFFECTATION + "," + CHAMP_ID_MOTIF_AFFECTATION + ","
				+ CHAMP_ID_FICHE_POSTE + "," + CHAMP_ID_AGENT + "," + CHAMP_REF_ARRETE_AFF + "," + CHAMP_DATE_ARRETE
				+ "," + CHAMP_DATE_DEBUT_AFF + "," + CHAMP_DATE_FIN_AFF + "," + CHAMP_TEMPS_TRAVAIL + ","
				+ CHAMP_CODE_ECOLE + "," + CHAMP_ID_FICHE_POSTE_SECONDAIRE + "," + CHAMP_COMMENTAIRE + ","
				+ CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + "," + CHAMP_ID_BASE_HORAIRE_POINTAGE + ","
				+ CHAMP_ID_BASE_HORAIRE_ABSENCE + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histo.getIdAffectation(), histo.getIdMotifAffectation(), histo.getIdFichePoste(),
						histo.getIdAgent(), histo.getRefArreteAff(), histo.getDateArrete(), histo.getDateDebutAff(),
						histo.getDateFinAff(), histo.getTempsTravail(), histo.getCodeEcole(),
						histo.getIdFichePosteSecondaire(), histo.getCommentaire(), histo.getTypeHisto(),
						histo.getUserHisto(), histo.getIdBaseHorairePointage(), histo.getIdBaseHoraireAbsence() });
	}

	@Override
	public void creerHistoAffectation(HistoAffectation histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoAffectation
		creerHistoAffectationBD(histo);
	}

	@Override
	public ArrayList<HistoAffectation> listerAffectationHistoAvecAgent(Integer idAgent) throws Exception {
		String sql = " select * from " + NOM_TABLE + "  WHERE " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_HISTO
				+ " desc";

		ArrayList<HistoAffectation> liste = new ArrayList<HistoAffectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			HistoAffectation a = new HistoAffectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID_AFFECTATION));
			a.setIdMotifAffectation((Integer) row.get(CHAMP_ID_MOTIF_AFFECTATION));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setRefArreteAff((String) row.get(CHAMP_REF_ARRETE_AFF));
			a.setDateArrete((Date) row.get(CHAMP_DATE_ARRETE));
			a.setDateDebutAff((Date) row.get(CHAMP_DATE_DEBUT_AFF));
			a.setDateFinAff((Date) row.get(CHAMP_DATE_FIN_AFF));
			a.setTempsTravail((String) row.get(CHAMP_TEMPS_TRAVAIL));
			a.setCodeEcole((String) row.get(CHAMP_CODE_ECOLE));
			a.setIdFichePosteSecondaire((Integer) row.get(CHAMP_ID_FICHE_POSTE_SECONDAIRE));
			a.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			a.setTypeHisto((String) row.get(CHAMP_TYPE_HISTO));
			a.setUserHisto((String) row.get(CHAMP_USER_HISTO));
			a.setDateHisto((Date) row.get(CHAMP_DATE_HISTO));
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			a.setIdBaseHoraireAbsence((Integer) row.get(CHAMP_ID_BASE_HORAIRE_ABSENCE));
			liste.add(a);
		}

		return liste;
	}
}
