package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.Handicap;
import nc.mairie.spring.dao.SirhDao;

public class HandicapDao extends SirhDao implements HandicapDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_TYPE_HANDICAP = "ID_TYPE_HANDICAP";
	public static final String CHAMP_ID_MALADIE_PRO = "ID_MALADIE_PRO";
	public static final String CHAMP_POURCENT_INCAPACITE = "POURCENT_INCAPACITE";
	public static final String CHAMP_RECONNAISSANCE_MP = "RECONNAISSANCE_MP";
	public static final String CHAMP_DATE_DEBUT_HANDICAP = "DATE_DEBUT_HANDICAP";
	public static final String CHAMP_DATE_FIN_HANDICAP = "DATE_FIN_HANDICAP";
	public static final String CHAMP_HANDICAP_CRDHNC = "HANDICAP_CRDHNC";
	public static final String CHAMP_NUM_CARTE_CRDHNC = "NUM_CARTE_CRDHNC";
	public static final String CHAMP_AMENAGEMENT_POSTE = "AMENAGEMENT_POSTE";
	public static final String CHAMP_COMMENTAIRE_HANDICAP = "COMMENTAIRE_HANDICAP";
	public static final String CHAMP_RENOUVELLEMENT = "RENOUVELLEMENT";

	public HandicapDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HANDICAP";
		super.CHAMP_ID = "ID_HANDICAP";
	}

	@Override
	public void creerHandicap(Integer idAgent, Integer idTypeHandicap, Integer idMaladiePro,
			Integer pourcentIncapacite, boolean reconnaissanceMp, Date dateDebutHandicap, Date dateFinHandicap,
			boolean handicapCRDHNC, String numCarteCrdhnc, boolean amenagementPoste, String commentaireHandicap,
			boolean renouvellement) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_TYPE_HANDICAP + ","
				+ CHAMP_ID_MALADIE_PRO + "," + CHAMP_POURCENT_INCAPACITE + "," + CHAMP_RECONNAISSANCE_MP + ","
				+ CHAMP_DATE_DEBUT_HANDICAP + "," + CHAMP_DATE_FIN_HANDICAP + "," + CHAMP_HANDICAP_CRDHNC + ","
				+ CHAMP_NUM_CARTE_CRDHNC + "," + CHAMP_AMENAGEMENT_POSTE + "," + CHAMP_COMMENTAIRE_HANDICAP + ","
				+ CHAMP_RENOUVELLEMENT + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idTypeHandicap, idMaladiePro, pourcentIncapacite,
				reconnaissanceMp, dateDebutHandicap, dateFinHandicap, handicapCRDHNC, numCarteCrdhnc, amenagementPoste,
				commentaireHandicap, renouvellement });
	}

	@Override
	public void modifierHandicap(Integer idHandicap, Integer idAgent, Integer idTypeHandicap, Integer idMaladiePro,
			Integer pourcentIncapacite, boolean reconnaissanceMp, Date dateDebutHandicap, Date dateFinHandicap,
			boolean handicapCRDHNC, String numCarteCrdhnc, boolean amenagementPoste, String commentaireHandicap,
			boolean renouvellement) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_ID_TYPE_HANDICAP + "=?,"
				+ CHAMP_ID_MALADIE_PRO + "=?," + CHAMP_POURCENT_INCAPACITE + "=?," + CHAMP_RECONNAISSANCE_MP + "=?,"
				+ CHAMP_DATE_DEBUT_HANDICAP + "=?," + CHAMP_DATE_FIN_HANDICAP + "=?," + CHAMP_HANDICAP_CRDHNC + "=?,"
				+ CHAMP_NUM_CARTE_CRDHNC + "=?," + CHAMP_AMENAGEMENT_POSTE + "=?," + CHAMP_COMMENTAIRE_HANDICAP + "=?,"
				+ CHAMP_RENOUVELLEMENT + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idTypeHandicap, idMaladiePro, pourcentIncapacite,
				reconnaissanceMp, dateDebutHandicap, dateFinHandicap, handicapCRDHNC, numCarteCrdhnc, amenagementPoste,
				commentaireHandicap, renouvellement, idHandicap });
	}

	@Override
	public void supprimerHandicap(Integer idHandicap) throws Exception {
		super.supprimerObject(idHandicap);
	}

	@Override
	public Handicap chercherHandicap(Integer idHandicap) throws Exception {
		return super.chercherObject(Handicap.class, idHandicap);
	}

	@Override
	public ArrayList<Handicap> listerHandicapAvecMaladiePro(Integer idMaladiePro) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_MALADIE_PRO + "=? ";

		ArrayList<Handicap> liste = new ArrayList<Handicap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idMaladiePro });
		for (Map<String, Object> row : rows) {
			Handicap a = new Handicap();
			a.setIdHandicap((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdTypeHandicap((Integer) row.get(CHAMP_ID_TYPE_HANDICAP));
			a.setIdMaladiePro((Integer) row.get(CHAMP_ID_MALADIE_PRO));
			a.setPourcentIncapacite((Integer) row.get(CHAMP_POURCENT_INCAPACITE));
			Integer recoMP = (Integer) row.get(CHAMP_RECONNAISSANCE_MP);
			a.setReconnaissanceMp(recoMP == 0 ? false : true);
			a.setDateDebutHandicap((Date) row.get(CHAMP_DATE_DEBUT_HANDICAP));
			a.setDateFinHandicap((Date) row.get(CHAMP_DATE_FIN_HANDICAP));
			Integer handiCrd = (Integer) row.get(CHAMP_HANDICAP_CRDHNC);
			a.setHandicapCRDHNC(handiCrd == 0 ? false : true);
			a.setNumCarteCrdhnc((String) row.get(CHAMP_NUM_CARTE_CRDHNC));
			Integer ame = (Integer) row.get(CHAMP_AMENAGEMENT_POSTE);
			a.setAmenagementPoste(ame == 0 ? false : true);
			a.setCommentaireHandicap((String) row.get(CHAMP_COMMENTAIRE_HANDICAP));
			Integer renouv = (Integer) row.get(CHAMP_RENOUVELLEMENT);
			a.setRenouvellement(renouv == 0 ? false : true);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<Handicap> listerHandicapAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by "
				+ CHAMP_DATE_DEBUT_HANDICAP + " desc";

		ArrayList<Handicap> liste = new ArrayList<Handicap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			Handicap a = new Handicap();
			a.setIdHandicap((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdTypeHandicap((Integer) row.get(CHAMP_ID_TYPE_HANDICAP));
			a.setIdMaladiePro((Integer) row.get(CHAMP_ID_MALADIE_PRO));
			a.setPourcentIncapacite((Integer) row.get(CHAMP_POURCENT_INCAPACITE));
			Integer recoMP = (Integer) row.get(CHAMP_RECONNAISSANCE_MP);
			a.setReconnaissanceMp(recoMP == 0 ? false : true);
			a.setDateDebutHandicap((Date) row.get(CHAMP_DATE_DEBUT_HANDICAP));
			a.setDateFinHandicap((Date) row.get(CHAMP_DATE_FIN_HANDICAP));
			Integer handiCrd = (Integer) row.get(CHAMP_HANDICAP_CRDHNC);
			a.setHandicapCRDHNC(handiCrd == 0 ? false : true);
			a.setNumCarteCrdhnc((String) row.get(CHAMP_NUM_CARTE_CRDHNC));
			Integer ame = (Integer) row.get(CHAMP_AMENAGEMENT_POSTE);
			a.setAmenagementPoste(ame == 0 ? false : true);
			a.setCommentaireHandicap((String) row.get(CHAMP_COMMENTAIRE_HANDICAP));
			Integer renouv = (Integer) row.get(CHAMP_RENOUVELLEMENT);
			a.setRenouvellement(renouv == 0 ? false : true);
			liste.add(a);
		}

		return liste;
	}
}
