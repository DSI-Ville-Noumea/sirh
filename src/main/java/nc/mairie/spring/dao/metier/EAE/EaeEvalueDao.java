package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeEvalue;
import nc.mairie.spring.dao.utils.EaeDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class EaeEvalueDao extends EaeDao implements EaeEvalueDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_DATE_ENTREE_SERVICE = "DATE_ENTREE_SERVICE";
	public static final String CHAMP_DATE_ENTREE_COLLECTIVITE = "DATE_ENTREE_COLLECTIVITE";
	public static final String CHAMP_DATE_ENTREE_FONCTIONNAIRE = "DATE_ENTREE_FONCTIONNAIRE";
	public static final String CHAMP_DATE_ENTREE_ADMINISTRATION = "DATE_ENTREE_ADMINISTRATION";
	public static final String CHAMP_STATUT = "STATUT";
	public static final String CHAMP_STATUT_PRECISION = "STATUT_PRECISION";
	public static final String CHAMP_ANCIENNETE_ECHELON_JOURS = "ANCIENNETE_ECHELON_JOURS";
	public static final String CHAMP_CADRE = "CADRE";
	public static final String CHAMP_CATEGORIE = "CATEGORIE";
	public static final String CHAMP_CLASSIFICATION = "CLASSIFICATION";
	public static final String CHAMP_GRADE = "GRADE";
	public static final String CHAMP_ECHELON = "ECHELON";
	public static final String CHAMP_DATE_EFFET_AVCT = "DATE_EFFET_AVCT";
	public static final String CHAMP_NOUV_GRADE = "NOUV_GRADE";
	public static final String CHAMP_NOUV_ECHELON = "NOUV_ECHELON";
	public static final String CHAMP_POSITION = "POSITION";
	public static final String CHAMP_TYPE_AVCT = "TYPE_AVCT";
	public static final String CHAMP_AVCT_DUR_MIN = "AVCT_DUR_MIN";
	public static final String CHAMP_AVCT_DUR_MOY = "AVCT_DUR_MOY";
	public static final String CHAMP_AVCT_DUR_MAX = "AVCT_DUR_MAX";
	public static final String CHAMP_AGENT_DETACHE = "AGENT_DETACHE";

	public EaeEvalueDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_EVALUE";
		super.CHAMP_ID = "ID_EAE_EVALUE";
	}

	@Override
	public void creerEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite,
			Date dateEntreeFonctionnaire, Date dateEntreeAdministration, String statut, Integer ancienneteEchelon,
			String cadre, String categorie, String classification, String grade, String echelon, Date dateEffectAvct,
			String nouvGrade, String nouvEchelon, String position, String typeAvct, String statutPrecision,
			Integer durMin, Integer durMoy, Integer durMax, boolean agentAffecte) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_DATE_ENTREE_SERVICE + "," + CHAMP_DATE_ENTREE_COLLECTIVITE + ","
				+ CHAMP_DATE_ENTREE_FONCTIONNAIRE + "," + CHAMP_DATE_ENTREE_ADMINISTRATION + "," + CHAMP_STATUT + ","
				+ CHAMP_ANCIENNETE_ECHELON_JOURS + "," + CHAMP_CADRE + "," + CHAMP_CATEGORIE + ","
				+ CHAMP_CLASSIFICATION + "," + CHAMP_GRADE + "," + CHAMP_ECHELON + "," + CHAMP_DATE_EFFET_AVCT + ","
				+ CHAMP_NOUV_GRADE + "," + CHAMP_NOUV_ECHELON + "," + CHAMP_POSITION + "," + CHAMP_TYPE_AVCT + ","
				+ CHAMP_STATUT_PRECISION + "," + CHAMP_AVCT_DUR_MIN + "," + CHAMP_AVCT_DUR_MOY + ","
				+ CHAMP_AVCT_DUR_MAX + "," + CHAMP_AGENT_DETACHE
				+ ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idAgent, dateEntreeService, dateEntreeCollectivite,
				dateEntreeFonctionnaire, dateEntreeAdministration, statut, ancienneteEchelon, cadre, categorie,
				classification, grade, echelon, dateEffectAvct, nouvGrade, nouvEchelon, position, typeAvct,
				statutPrecision, durMin, durMoy, durMax, agentAffecte });

	}

	@Override
	public EaeEvalue chercherEaeEvalue(Integer idEae) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + " = ? ";
		EaeEvalue eval = (EaeEvalue) jdbcTemplate.queryForObject(sql, new Object[] { idEae },
				new BeanPropertyRowMapper<EaeEvalue>(EaeEvalue.class));
		return eval;
	}

	@Override
	public ArrayList<EaeEvalue> listerEaeEvalueSans2012(Integer idAgent) throws Exception {
		String sql = "select ev.* from " + NOM_TABLE + " ev inner join EAE e on e.id_eae=ev." + CHAMP_ID_EAE
				+ " inner join EAE_CAMPAGNE_EAE c on e.id_campagne_eae=c.id_campagne_eae where  ev." + CHAMP_ID_AGENT
				+ "=? and c.ANNEE!=2012";

		ArrayList<EaeEvalue> listeEaeEvalue = new ArrayList<EaeEvalue>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			EaeEvalue evalue = new EaeEvalue();
			evalue.setIdEaeEvalue((Integer) row.get(CHAMP_ID));
			evalue.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			evalue.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			evalue.setDateEntreeService((Date) row.get(CHAMP_DATE_ENTREE_SERVICE));
			evalue.setDateEntreeCollectivite((Date) row.get(CHAMP_DATE_ENTREE_COLLECTIVITE));
			evalue.setDateEntreeFonctionnaire((Date) row.get(CHAMP_DATE_ENTREE_FONCTIONNAIRE));
			evalue.setDateEntreeAdministration((Date) row.get(CHAMP_DATE_ENTREE_ADMINISTRATION));
			evalue.setStatut((String) row.get(CHAMP_STATUT));
			evalue.setAncienneteEchelonJours((Integer) row.get(CHAMP_ANCIENNETE_ECHELON_JOURS));
			evalue.setCadre((String) row.get(CHAMP_CADRE));
			evalue.setCategorie((String) row.get(CHAMP_CATEGORIE));
			evalue.setClassification((String) row.get(CHAMP_CLASSIFICATION));
			evalue.setGrade((String) row.get(CHAMP_GRADE));
			evalue.setEchelon((String) row.get(CHAMP_ECHELON));
			evalue.setDateEffetAvct((Date) row.get(CHAMP_DATE_EFFET_AVCT));
			evalue.setNouvGrade((String) row.get(CHAMP_NOUV_GRADE));
			evalue.setNouvEchelon((String) row.get(CHAMP_NOUV_ECHELON));
			evalue.setPosition((String) row.get(CHAMP_POSITION));
			evalue.setStatutPrecision((String) row.get(CHAMP_STATUT_PRECISION));
			evalue.setTypeAvct((String) row.get(CHAMP_TYPE_AVCT));
			evalue.setAvctDurMin((Integer) row.get(CHAMP_AVCT_DUR_MIN));
			evalue.setAvctDurMoy((Integer) row.get(CHAMP_AVCT_DUR_MOY));
			evalue.setAvctDurMax((Integer) row.get(CHAMP_AVCT_DUR_MAX));
			evalue.setAgentDetache((boolean) row.get(CHAMP_AGENT_DETACHE));

			listeEaeEvalue.add(evalue);
		}
		return listeEaeEvalue;
	}

	@Override
	public void modifierEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite,
			Date dateEntreeFonctionnaire, Date dateEntreeAdministration, String statut, Integer ancienneteEchelon,
			String cadre, String categorie, String classification, String grade, String echelon, Date dateEffectAvct,
			String nouvGrade, String nouvEchelon, String position, String typeAvct, String statutPrecision,
			Integer durMin, Integer durMoy, Integer durMax, boolean agentAffecte) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_DATE_ENTREE_SERVICE + "=?,"
				+ CHAMP_DATE_ENTREE_COLLECTIVITE + "=?," + CHAMP_DATE_ENTREE_FONCTIONNAIRE + "=?,"
				+ CHAMP_DATE_ENTREE_ADMINISTRATION + "=?," + CHAMP_STATUT + "=?," + CHAMP_ANCIENNETE_ECHELON_JOURS
				+ "=?," + CHAMP_CADRE + "=?," + CHAMP_CATEGORIE + "=?," + CHAMP_CLASSIFICATION + "=?," + CHAMP_GRADE
				+ "=?," + CHAMP_ECHELON + "=?," + CHAMP_DATE_EFFET_AVCT + "=?," + CHAMP_NOUV_GRADE + "=?,"
				+ CHAMP_NOUV_ECHELON + "=?," + CHAMP_POSITION + "=?," + CHAMP_TYPE_AVCT + "=?,"
				+ CHAMP_STATUT_PRECISION + "=?," + CHAMP_AVCT_DUR_MIN + "=?," + CHAMP_AVCT_DUR_MOY + "=?,"
				+ CHAMP_AVCT_DUR_MAX + "=?," + CHAMP_AGENT_DETACHE + "=? where " + CHAMP_ID_EAE + "=?";

		jdbcTemplate.update(sql, new Object[] { idAgent, dateEntreeService, dateEntreeCollectivite,
				dateEntreeFonctionnaire, dateEntreeAdministration, statut, ancienneteEchelon, cadre, categorie,
				classification, grade, echelon, dateEffectAvct, nouvGrade, nouvEchelon, position, typeAvct,
				statutPrecision, durMin, durMoy, durMax, agentAffecte, idEae });

	}
}
