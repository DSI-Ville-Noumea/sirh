package nc.mairie.spring.dao.metier.poste;

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
				+ CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histo.getIdAffectation(), histo.getIdMotifAffectation(), histo.getIdFichePoste(),
						histo.getIdAgent(), histo.getRefArreteAff(), histo.getDateArrete(), histo.getDateDebutAff(),
						histo.getDateFinAff(), histo.getTempsTravail(), histo.getCodeEcole(),
						histo.getIdFichePosteSecondaire(), histo.getCommentaire(), histo.getTypeHisto(),
						histo.getUserHisto() });
	}

	@Override
	public void creerHistoAffectation(HistoAffectation histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoAffectation
		creerHistoAffectationBD(histo);
	}
}
