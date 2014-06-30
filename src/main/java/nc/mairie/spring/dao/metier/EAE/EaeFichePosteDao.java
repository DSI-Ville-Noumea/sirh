package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFichePosteDao implements EaeFichePosteDaoInterface {

	public static final String NOM_TABLE = "EAE_FICHE_POSTE";

	public static final String NOM_SEQUENCE = "EAE_S_FICHE_POSTE";

	public static final String CHAMP_ID_EAE_FICHE_POSTE = "ID_EAE_FICHE_POSTE";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_SHD = "ID_SHD";
	public static final String CHAMP_PRIMAIRE = "PRIMAIRE";
	public static final String CHAMP_DIRECTION_SERVICE = "DIRECTION_SERVICE";
	public static final String CHAMP_SERVICE = "SERVICE";
	public static final String CHAMP_SECTION_SERVICE = "SECTION_SERVICE";
	public static final String CHAMP_EMPLOI = "EMPLOI";
	public static final String CHAMP_FONCTION = "FONCTION";
	public static final String CHAMP_DATE_ENTREE_FONCTION = "DATE_ENTREE_FONCTION";
	public static final String CHAMP_GRADE_POSTE = "GRADE_POSTE";
	public static final String CHAMP_LOCALISATION = "LOCALISATION";
	public static final String CHAMP_MISSIONS = "MISSIONS";
	public static final String CHAMP_FONCTION_RESP = "FONCTION_RESP";
	public static final String CHAMP_DATE_ENTREE_SERVICE_RESP = "DATE_ENTREE_SERVICE_RESP";
	public static final String CHAMP_DATE_ENTREE_COLLECT_RESP = "DATE_ENTREE_COLLECT_RESP";
	public static final String CHAMP_DATE_ENTREE_FONCTION_RESP = "DATE_ENTREE_FONCTION_RESP";
	public static final String CHAMP_CODE_SERVICE = "CODE_SERVICE";
	public static final String CHAMP_ID_SIRH_FICHE_POSTE = "ID_SIRH_FICHE_POSTE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeFichePosteDao() {

	}

	@Override
	public void creerEaeFichePoste(Integer id, Integer idEae, Integer idSHD, boolean typeFDP, String direction,
			String service, String section, String emploi, String fonction, Date dateEntreeFonction, String grade,
			String localisation, String mission, String fonctionResp, Date dateEntreeServiceResp,
			Date dateEntreeCollectiviteResp, Date dateEntreeFonctionResp, String codeService, Integer idSirhFichePoste)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FICHE_POSTE + "," + CHAMP_ID_EAE + ","
				+ CHAMP_ID_SHD + "," + CHAMP_PRIMAIRE + "," + CHAMP_DIRECTION_SERVICE + "," + CHAMP_SERVICE + ","
				+ CHAMP_SECTION_SERVICE + "," + CHAMP_EMPLOI + "," + CHAMP_FONCTION + "," + CHAMP_DATE_ENTREE_FONCTION
				+ "," + CHAMP_GRADE_POSTE + "," + CHAMP_LOCALISATION + "," + CHAMP_MISSIONS + "," + CHAMP_FONCTION_RESP
				+ "," + CHAMP_DATE_ENTREE_SERVICE_RESP + "," + CHAMP_DATE_ENTREE_COLLECT_RESP + ","
				+ CHAMP_DATE_ENTREE_FONCTION_RESP + "," + CHAMP_CODE_SERVICE + "," + CHAMP_ID_SIRH_FICHE_POSTE + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { id, idEae, idSHD, typeFDP, direction, service, section, emploi,
				fonction, dateEntreeFonction, grade, localisation, mission, fonctionResp, dateEntreeServiceResp,
				dateEntreeCollectiviteResp, dateEntreeFonctionResp, codeService, idSirhFichePoste });

	}

	@Override
	public Integer getIdEaeFichePoste() throws Exception {
		String sqlClePrimaire = "select nextval('" + NOM_SEQUENCE + "')";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);
		return id;
	}

	@Override
	public EaeFichePoste chercherEaeFichePoste(Integer idEAE, boolean type) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=? and " + CHAMP_PRIMAIRE + "=?";
		EaeFichePoste eaeFDP = (EaeFichePoste) jdbcTemplate.queryForObject(sql, new Object[] { idEAE, type }, new BeanPropertyRowMapper<EaeFichePoste>(EaeFichePoste.class));
		return eaeFDP;
	}

	@Override
	public ArrayList<EaeFichePoste> listerEaeFichePosteGrouperParDirectionSection(Integer idCampagneEAE) {
		String sql = "select fp." + CHAMP_DIRECTION_SERVICE + ",fp." + CHAMP_SECTION_SERVICE + " from " + NOM_TABLE
				+ " fp inner join EAE e on e.id_eae=fp." + CHAMP_ID_EAE + " where e.ID_CAMPAGNE_EAE=? group by fp."
				+ CHAMP_DIRECTION_SERVICE + ",fp." + CHAMP_SECTION_SERVICE + " order by fp." + CHAMP_DIRECTION_SERVICE;

		ArrayList<EaeFichePoste> listeEAE = new ArrayList<EaeFichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCampagneEAE });
		for (Map<String, Object> row : rows) {
			EaeFichePoste eae = new EaeFichePoste();
			eae.setDirectionService((String) row.get(CHAMP_DIRECTION_SERVICE));
			eae.setSectionService((String) row.get(CHAMP_SECTION_SERVICE));
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public ArrayList<EaeFichePoste> chercherEaeFichePosteIdEae(Integer idEAE) {
		String sql = "select * from " + NOM_TABLE + "  where " + CHAMP_ID_EAE + "=? ";

		ArrayList<EaeFichePoste> listeEAE = new ArrayList<EaeFichePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeFichePoste eae = new EaeFichePoste();
			eae.setIdEaeFichePoste((Integer) row.get(CHAMP_ID_EAE_FICHE_POSTE));
			eae.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			eae.setIdSHD((Integer) row.get(CHAMP_ID_SHD));
			eae.setPrimaire((boolean) row.get(CHAMP_PRIMAIRE));
			eae.setDirectionService((String) row.get(CHAMP_DIRECTION_SERVICE));
			eae.setService((String) row.get(CHAMP_SERVICE));
			eae.setSectionService((String) row.get(CHAMP_SECTION_SERVICE));
			eae.setEmploi((String) row.get(CHAMP_EMPLOI));
			eae.setFonction((String) row.get(CHAMP_FONCTION));
			eae.setDateEntreeFonction((Date) row.get(CHAMP_DATE_ENTREE_FONCTION));
			eae.setGradePoste((String) row.get(CHAMP_GRADE_POSTE));
			eae.setLocalisation((String) row.get(CHAMP_LOCALISATION));
			eae.setMissions((String) row.get(CHAMP_MISSIONS));
			eae.setFonctionResp((String) row.get(CHAMP_FONCTION_RESP));
			listeEAE.add(eae);
		}

		return listeEAE;
	}

	@Override
	public void modifierEaeFichePoste(Integer idEaeFichePoste, Integer idEae, Integer idSHD, boolean typeFDP,
			String direction, String service, String section, String emploi, String fonction, Date dateEntreeFonction,
			String grade, String localisation, String mission, String fonctionResp, Date dateEntreeServiceResp,
			Date dateEntreeCollectiviteResp, Date dateEntreeFonctionResp, String codeService, Integer idSirhFichePoste)
			throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_EAE + "=?," + CHAMP_ID_SHD + "=?," + CHAMP_PRIMAIRE
				+ "=?," + CHAMP_DIRECTION_SERVICE + "=?," + CHAMP_SERVICE + "=?," + CHAMP_SECTION_SERVICE + "=?,"
				+ CHAMP_EMPLOI + "=?," + CHAMP_FONCTION + "=?," + CHAMP_DATE_ENTREE_FONCTION + "=?,"
				+ CHAMP_GRADE_POSTE + "=?," + CHAMP_LOCALISATION + "=?," + CHAMP_MISSIONS + "=?," + CHAMP_FONCTION_RESP
				+ "=?," + CHAMP_DATE_ENTREE_SERVICE_RESP + "=?," + CHAMP_DATE_ENTREE_COLLECT_RESP + "=?,"
				+ CHAMP_DATE_ENTREE_FONCTION_RESP + "=?," + CHAMP_CODE_SERVICE + "=?," + CHAMP_ID_SIRH_FICHE_POSTE
				+ "=? where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";

		jdbcTemplate.update(sql, new Object[] { idEae, idSHD, typeFDP, direction, service, section, emploi, fonction,
				dateEntreeFonction, grade, localisation, mission, fonctionResp, dateEntreeServiceResp,
				dateEntreeCollectiviteResp, dateEntreeFonctionResp, codeService, idSirhFichePoste, idEaeFichePoste });

	}

	@Override
	public void supprimerEaeFichePoste(Integer idEaeFichePoste) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaeFichePoste });
	}
}
