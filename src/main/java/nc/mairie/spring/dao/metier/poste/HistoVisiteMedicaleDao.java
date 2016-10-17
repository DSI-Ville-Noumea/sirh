package nc.mairie.spring.dao.metier.poste;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoVisiteMedicale;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoVisiteMedicaleDao extends SirhDao implements HistoVisiteMedicaleDaoInterface {

	public static final String	CHAMP_ID_VISITE				= "ID_VISITE";
	public static final String	CHAMP_ID_AGENT				= "ID_AGENT";
	public static final String	CHAMP_ID_MEDECIN			= "ID_MEDECIN";
	public static final String	CHAMP_ID_RECOMMANDATION		= "ID_RECOMMANDATION";
	public static final String	CHAMP_DATE_DERNIERE_VISITE	= "DATE_DERNIERE_VISITE";
	public static final String	CHAMP_DUREE_VALIDITE		= "DUREE_VALIDITE";
	public static final String	CHAMP_ID_MOTIF_VM			= "ID_MOTIF_VM";
	public static final String	CHAMP_ID_SUIVI_MED			= "ID_SUIVI_MED";
	public static final String	CHAMP_COMMENTAIRE			= "COMMENTAIRE";
	public static final String	CHAMP_TYPE_HISTO			= "TYPE_HISTO";
	public static final String	CHAMP_USER_HISTO			= "USER_HISTO";

	public HistoVisiteMedicaleDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_VISITE_MEDICALE";
	}

	@Override
	public void creerHistoVisiteMedicale(HistoVisiteMedicale histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// on injecte en BD
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_VISITE + "," + CHAMP_ID_AGENT + "," + CHAMP_ID_MEDECIN + ","
				+ CHAMP_ID_RECOMMANDATION + "," + CHAMP_DATE_DERNIERE_VISITE + "," + CHAMP_DUREE_VALIDITE + "," + CHAMP_ID_MOTIF_VM + ","
				+ CHAMP_ID_SUIVI_MED + "," + CHAMP_COMMENTAIRE + "," + CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { histo.getIdVisite(), histo.getIdAgent(), histo.getIdMedecin(), histo.getIdRecommandation(),
						histo.getDateDerniereVisite(), histo.getDureeValidite(), histo.getIdMotifVm(), histo.getIdSuiviMed(), histo.getCommentaire(),
						histo.getTypeHisto(), histo.getUserHisto() });

	}
}
