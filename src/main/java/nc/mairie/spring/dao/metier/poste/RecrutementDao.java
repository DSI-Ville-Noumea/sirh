package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.Recrutement;
import nc.mairie.spring.dao.utils.SirhDao;

public class RecrutementDao extends SirhDao implements RecrutementDaoInterface {

	public static final String CHAMP_ID_MOTIF_RECRUT = "ID_MOTIF_RECRUT";
	public static final String CHAMP_ID_MOTIF_NON_RECRUT = "ID_MOTIF_NON_RECRUT";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_REFERENCE_SES = "REFERENCE_SES";
	public static final String CHAMP_REFERENCE_MAIRIE = "REFERENCE_MAIRIE";
	public static final String CHAMP_REFERENCE_DRHFPNC = "REFERENCE_DRHFPNC";
	public static final String CHAMP_DATE_OUVERTURE = "DATE_OUVERTURE";
	public static final String CHAMP_DATE_VALIDATION = "DATE_VALIDATION";
	public static final String CHAMP_DATE_CLOTURE = "DATE_CLOTURE";
	public static final String CHAMP_DATE_TRANSMISSION = "DATE_TRANSMISSION";
	public static final String CHAMP_DATE_REPONSE = "DATE_REPONSE";
	public static final String CHAMP_NB_CAND_RECUES = "NB_CAND_RECUES";
	public static final String CHAMP_NOM_AGENT_RECRUTE = "NOM_AGENT_RECRUTE";

	public RecrutementDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "RECRUTEMENT";
		super.CHAMP_ID = "ID_RECRUTEMENT";
	}

	@Override
	public ArrayList<Recrutement> listerRecrutementAvecMotifNonRec(Integer idMotifNonRecr) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_MOTIF_NON_RECRUT + "=?";

		ArrayList<Recrutement> liste = new ArrayList<Recrutement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idMotifNonRecr });
		for (Map<String, Object> row : rows) {
			Recrutement a = new Recrutement();
			a.setIdRecrutement((Integer) row.get(CHAMP_ID));
			a.setIdMotifRecrut((Integer) row.get(CHAMP_ID_MOTIF_RECRUT));
			a.setIdMotifNonRecrut((Integer) row.get(CHAMP_ID_MOTIF_NON_RECRUT));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			a.setReferenceSes((Integer) row.get(CHAMP_REFERENCE_SES));
			a.setReferenceMairie((String) row.get(CHAMP_REFERENCE_MAIRIE));
			a.setReferenceDrhfpnc((String) row.get(CHAMP_REFERENCE_DRHFPNC));
			a.setDateOuverture((Date) row.get(CHAMP_DATE_OUVERTURE));
			a.setDateValidation((Date) row.get(CHAMP_DATE_VALIDATION));
			a.setDateCloture((Date) row.get(CHAMP_DATE_CLOTURE));
			a.setDateTransmission((Date) row.get(CHAMP_DATE_TRANSMISSION));
			a.setDateReponse((Date) row.get(CHAMP_DATE_REPONSE));
			a.setNbCandRecues((Integer) row.get(CHAMP_NB_CAND_RECUES));
			a.setNomAgentRecrute((String) row.get(CHAMP_NOM_AGENT_RECRUTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Recrutement> listerRecrutementAvecMotifRec(Integer idMotifRecr) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_MOTIF_RECRUT + "=?";

		ArrayList<Recrutement> liste = new ArrayList<Recrutement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idMotifRecr });
		for (Map<String, Object> row : rows) {
			Recrutement a = new Recrutement();
			a.setIdRecrutement((Integer) row.get(CHAMP_ID));
			a.setIdMotifRecrut((Integer) row.get(CHAMP_ID_MOTIF_RECRUT));
			a.setIdMotifNonRecrut((Integer) row.get(CHAMP_ID_MOTIF_NON_RECRUT));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			a.setReferenceSes((Integer) row.get(CHAMP_REFERENCE_SES));
			a.setReferenceMairie((String) row.get(CHAMP_REFERENCE_MAIRIE));
			a.setReferenceDrhfpnc((String) row.get(CHAMP_REFERENCE_DRHFPNC));
			a.setDateOuverture((Date) row.get(CHAMP_DATE_OUVERTURE));
			a.setDateValidation((Date) row.get(CHAMP_DATE_VALIDATION));
			a.setDateCloture((Date) row.get(CHAMP_DATE_CLOTURE));
			a.setDateTransmission((Date) row.get(CHAMP_DATE_TRANSMISSION));
			a.setDateReponse((Date) row.get(CHAMP_DATE_REPONSE));
			a.setNbCandRecues((Integer) row.get(CHAMP_NB_CAND_RECUES));
			a.setNomAgentRecrute((String) row.get(CHAMP_NOM_AGENT_RECRUTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void creerRecrutement(Integer idMotifRecrut, Integer idMotifNonRecrut, Integer idFichePoste,
			Integer referenceSes, String referenceMairie, String referenceDrhfpnc, Date dateOuverture,
			Date dateValidation, Date dateCloture, Date dateTransmission, Date dateReponse, Integer nbCandRecues,
			String nomAgentRecrute) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_MOTIF_RECRUT + "," + CHAMP_ID_MOTIF_NON_RECRUT + ","
				+ CHAMP_ID_FICHE_POSTE + "," + CHAMP_REFERENCE_SES + "," + CHAMP_REFERENCE_MAIRIE + ","
				+ CHAMP_REFERENCE_DRHFPNC + "," + CHAMP_DATE_OUVERTURE + "," + CHAMP_DATE_VALIDATION + ","
				+ CHAMP_DATE_CLOTURE + "," + CHAMP_DATE_TRANSMISSION + "," + CHAMP_DATE_REPONSE + ","
				+ CHAMP_NB_CAND_RECUES + "," + CHAMP_NOM_AGENT_RECRUTE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { idMotifRecrut, idMotifNonRecrut, idFichePoste, referenceSes,
				referenceMairie, referenceDrhfpnc, dateOuverture, dateValidation, dateCloture, dateTransmission,
				dateReponse, nbCandRecues, nomAgentRecrute });
	}

	@Override
	public void supprimerRecrutement(Integer idRecrutement) throws Exception {
		super.supprimerObject(idRecrutement);
	}
}
