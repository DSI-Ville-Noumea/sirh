package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.Affectation;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AffectationDao extends SirhDao implements AffectationDaoInterface {

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
	public static final String CHAMP_ID_BASE_HORAIRE_POINTAGE = "ID_BASE_HORAIRE_POINTAGE";

	public AffectationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AFFECTATION";
		super.CHAMP_ID = "ID_AFFECTATION";
	}

	@Override
	public Affectation chercherAffectationAgentPourDate(Integer idAgent, Date dateCreation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_DATE_DEBUT_AFF
				+ "<=? and( " + CHAMP_DATE_FIN_AFF + " is null or " + CHAMP_DATE_FIN_AFF + ">=?)";
		Affectation aff = (Affectation) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, dateCreation,
				dateCreation }, new BeanPropertyRowMapper<Affectation>(Affectation.class));
		return aff;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAvecFPEtAgent(Integer idFichePoste, Integer idAgent)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? and " + CHAMP_ID_AGENT
				+ "=?";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste, idAgent });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAvecFPOrderDatDeb(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? order by "
				+ CHAMP_DATE_DEBUT_AFF + " desc";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAgentAvecService(Integer idAgent, String idServi) throws Exception {
		String sql = "select aff.* from " + NOM_TABLE + " aff inner join FICHE_POSTE fp on aff." + CHAMP_ID_FICHE_POSTE
				+ " = fp.ID_FICHE_POSTE where aff." + CHAMP_ID_AGENT + "=? and fp.ID_SERVI =? order by "
				+ CHAMP_DATE_DEBUT_AFF + " desc";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent, idServi });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public Affectation chercherAffectationActiveAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? and "
				+ CHAMP_DATE_DEBUT_AFF + "<=? and( " + CHAMP_DATE_FIN_AFF + " is null or " + CHAMP_DATE_FIN_AFF
				+ ">=?)";
		Affectation aff = (Affectation) jdbcTemplate.queryForObject(sql, new Object[] { idFichePoste, new Date(),
				new Date() }, new BeanPropertyRowMapper<Affectation>(Affectation.class));
		return aff;
	}

	@Override
	public Affectation chercherAffectationAvecFP(Integer idFPResponsable) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? ";
		Affectation aff = (Affectation) jdbcTemplate.queryForObject(sql, new Object[] { idFPResponsable },
				new BeanPropertyRowMapper<Affectation>(Affectation.class));
		return aff;
	}

	@Override
	public Affectation chercherAffectationActiveAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_DATE_DEBUT_AFF
				+ "<=? and( " + CHAMP_DATE_FIN_AFF + " is null or " + CHAMP_DATE_FIN_AFF + ">=?)";
		Affectation aff = (Affectation) jdbcTemplate.queryForObject(sql,
				new Object[] { idAgent, new Date(), new Date() }, new BeanPropertyRowMapper<Affectation>(
						Affectation.class));
		return aff;
	}

	@Override
	public ArrayList<Affectation> listerAffectationActiveAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_DATE_DEBUT_AFF
				+ "<=? and( " + CHAMP_DATE_FIN_AFF + " is null or " + CHAMP_DATE_FIN_AFF + ">=?)";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				new Object[] { idAgent, new Date(), new Date() });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAvecFPPrimaireOuSecondaire(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where (" + CHAMP_ID_FICHE_POSTE + " = ? or "
				+ CHAMP_ID_FICHE_POSTE_SECONDAIRE + " =?) and " + CHAMP_DATE_DEBUT_AFF + "<=? and ( "
				+ CHAMP_DATE_FIN_AFF + " is null or " + CHAMP_DATE_FIN_AFF + ">=?)";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste, idFichePoste,
				new Date(), new Date() });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? ";

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Affectation> listerAffectationAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? order by "
				+ CHAMP_DATE_DEBUT_AFF;

		ArrayList<Affectation> liste = new ArrayList<Affectation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			Affectation a = new Affectation();
			a.setIdAffectation((Integer) row.get(CHAMP_ID));
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
			a.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID_BASE_HORAIRE_POINTAGE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerAffectation(Integer idAffectation) throws Exception {
		super.supprimerObject(idAffectation);

	}

	@Override
	public void modifierAffectation(Affectation aff) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_MOTIF_AFFECTATION + "=?," + CHAMP_ID_FICHE_POSTE
				+ "=?," + CHAMP_ID_AGENT + "=?," + CHAMP_REF_ARRETE_AFF + "=?," + CHAMP_DATE_ARRETE + "=?" + ","
				+ CHAMP_DATE_DEBUT_AFF + "=?," + CHAMP_DATE_FIN_AFF + "=?," + CHAMP_TEMPS_TRAVAIL + "=?,"
				+ CHAMP_CODE_ECOLE + "=?," + CHAMP_ID_FICHE_POSTE_SECONDAIRE + "=?," + CHAMP_COMMENTAIRE + "=?,"
				+ CHAMP_ID_BASE_HORAIRE_POINTAGE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(
				sql,
				new Object[] { aff.getIdMotifAffectation(), aff.getIdFichePoste(), aff.getIdAgent(),
						aff.getRefArreteAff(), aff.getDateArrete(), aff.getDateDebutAff(), aff.getDateFinAff(),
						aff.getTempsTravail(), aff.getCodeEcole(), aff.getIdFichePosteSecondaire(),
						aff.getCommentaire(), aff.getIdBaseHorairePointage(), aff.getIdAffectation() });
	}

	@Override
	public Integer creerAffectation(Affectation aff) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " ("
				+ CHAMP_ID_MOTIF_AFFECTATION + "," + CHAMP_ID_FICHE_POSTE + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_REF_ARRETE_AFF + "," + CHAMP_DATE_ARRETE + "," + CHAMP_DATE_DEBUT_AFF + ","
				+ CHAMP_DATE_FIN_AFF + "," + CHAMP_TEMPS_TRAVAIL + "," + CHAMP_CODE_ECOLE + ","
				+ CHAMP_ID_FICHE_POSTE_SECONDAIRE + "," + CHAMP_COMMENTAIRE + "," + CHAMP_ID_BASE_HORAIRE_POINTAGE
				+ ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?))";
		Integer id = jdbcTemplate.queryForObject(
				sql,
				new Object[] { aff.getIdMotifAffectation(), aff.getIdFichePoste(), aff.getIdAgent(),
						aff.getRefArreteAff(), aff.getDateArrete(), aff.getDateDebutAff(), aff.getDateFinAff(),
						aff.getTempsTravail(), aff.getCodeEcole(), aff.getIdFichePosteSecondaire(),
						aff.getCommentaire(), aff.getIdBaseHorairePointage() }, Integer.class);
		return id;
	}
}
