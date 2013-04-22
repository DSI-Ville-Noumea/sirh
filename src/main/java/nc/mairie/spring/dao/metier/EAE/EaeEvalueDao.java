package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeEvalueRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeEvalueDao implements EaeEvalueDaoInterface {

	public static final String NOM_TABLE = "EAE_EVALUE";

	public static final String NOM_SEQUENCE = "EAE_S_EVALUE";

	public static final String CHAMP_ID_EAE_EVALUE = "ID_EAE_EVALUE";
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

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeEvalueDao() {

	}

	@Override
	public void creerEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite, Date dateEntreeFonctionnaire,
			Date dateEntreeAdministration, String statut, Integer ancienneteEchelon, String cadre, String categorie, String classification,
			String grade, String echelon, Date dateEffectAvct, String nouvGrade, String nouvEchelon, String position, String typeAvct,
			String statutPrecision, Integer durMin, Integer durMoy, Integer durMax, boolean agentDetache) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_EVALUE + "," + CHAMP_ID_EAE + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_DATE_ENTREE_SERVICE + "," + CHAMP_DATE_ENTREE_COLLECTIVITE + "," + CHAMP_DATE_ENTREE_FONCTIONNAIRE + ","
				+ CHAMP_DATE_ENTREE_ADMINISTRATION + "," + CHAMP_STATUT + "," + CHAMP_ANCIENNETE_ECHELON_JOURS + "," + CHAMP_CADRE + ","
				+ CHAMP_CATEGORIE + "," + CHAMP_CLASSIFICATION + "," + CHAMP_GRADE + "," + CHAMP_ECHELON + "," + CHAMP_DATE_EFFET_AVCT + ","
				+ CHAMP_NOUV_GRADE + "," + CHAMP_NOUV_ECHELON + "," + CHAMP_POSITION + "," + CHAMP_TYPE_AVCT + "," + CHAMP_STATUT_PRECISION + ","
				+ CHAMP_AVCT_DUR_MIN + "," + CHAMP_AVCT_DUR_MOY + "," + CHAMP_AVCT_DUR_MAX + "," + CHAMP_AGENT_DETACHE + ") values(" + NOM_SEQUENCE
				+ ".nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, idAgent, dateEntreeService, dateEntreeCollectivite, dateEntreeFonctionnaire,
				dateEntreeAdministration, statut, ancienneteEchelon, cadre, categorie, classification, grade, echelon, dateEffectAvct, nouvGrade,
				nouvEchelon, position, typeAvct, statutPrecision, durMin, durMoy, durMax, agentDetache });

	}

	@Override
	public EaeEvalue chercherEaeEvalue(Integer idEae) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + " = ? ";
		EaeEvalue eval = (EaeEvalue) jdbcTemplate.queryForObject(sql, new Object[] { idEae }, new EaeEvalueRowMapper());
		return eval;
	}

	@Override
	public ArrayList<EaeEvalue> listerEaeEvalueSans2012(Integer idAgent) throws Exception {
		String sql = "select ev.* from " + NOM_TABLE + " ev inner join EAE e on e.id_eae=ev." + CHAMP_ID_EAE
				+ " inner join EAE_CAMPAGNE_EAE c on e.id_camapagne_eae=c.id_camapgne_eae where  ev." + CHAMP_ID_AGENT + "=? and c.ANNEE!=2012";

		ArrayList<EaeEvalue> listeEaeEvalue = new ArrayList<EaeEvalue>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map row : rows) {
			EaeEvalue evalue = new EaeEvalue();
			// logger.debug("List evalue : " + row.toString());
			BigDecimal idEAEEvalue = (BigDecimal) row.get(CHAMP_ID_EAE_EVALUE);
			evalue.setIdEaeEvalue(idEAEEvalue.intValue());
			BigDecimal idEAE = (BigDecimal) row.get(CHAMP_ID_EAE);
			evalue.setIdEae(idEAE.intValue());
			BigDecimal idAg = (BigDecimal) row.get(CHAMP_ID_AGENT);
			evalue.setIdAgent(idAg.intValue());
			evalue.setDateEntreeService((Date) row.get(CHAMP_DATE_ENTREE_SERVICE));
			evalue.setDateEntreeCollectivite((Date) row.get(CHAMP_DATE_ENTREE_COLLECTIVITE));
			evalue.setDateEntreeFonctionnaire((Date) row.get(CHAMP_DATE_ENTREE_FONCTIONNAIRE));
			evalue.setDateEntreeAdministration((Date) row.get(CHAMP_DATE_ENTREE_ADMINISTRATION));
			evalue.setStatut((String) row.get(CHAMP_STATUT));
			BigDecimal anciennete = (BigDecimal) row.get(CHAMP_ANCIENNETE_ECHELON_JOURS);
			evalue.setAncienneteEchelonJours(anciennete == null ? null : anciennete.intValue());
			evalue.setCadre((String) row.get(CHAMP_CADRE));
			evalue.setCategorie((String) row.get(CHAMP_CATEGORIE));
			evalue.setClassification((String) row.get(CHAMP_CLASSIFICATION));
			evalue.setGrade((String) row.get(CHAMP_GRADE));
			evalue.setEchelon((String) row.get(CHAMP_ECHELON));
			evalue.setDateEffetAvct((Date) row.get(CHAMP_DATE_EFFET_AVCT));
			evalue.setNouvGrade((String) row.get(CHAMP_NOUV_GRADE));
			evalue.setNouvEchelon((String) row.get(CHAMP_NOUV_ECHELON));
			evalue.setPosition((String) row.get(CHAMP_POSITION));
			evalue.setPrecisionStatut((String) row.get(CHAMP_STATUT_PRECISION));
			evalue.setTypeAvct((String) row.get(CHAMP_TYPE_AVCT));
			BigDecimal min = (BigDecimal) row.get(CHAMP_AVCT_DUR_MIN);
			evalue.setNbMoisDureeMin(min == null ? null : min.intValue());
			BigDecimal moy = (BigDecimal) row.get(CHAMP_AVCT_DUR_MOY);
			evalue.setNbMoisDureeMoy(moy == null ? null : moy.intValue());
			BigDecimal max = (BigDecimal) row.get(CHAMP_AVCT_DUR_MAX);
			evalue.setNbMoisDureeMax(max == null ? null : max.intValue());
			BigDecimal detache = (BigDecimal) row.get(CHAMP_AGENT_DETACHE);
			evalue.setAgentDetache(detache.intValue() == 0 ? false : true);

			listeEaeEvalue.add(evalue);
		}
		return listeEaeEvalue;
	}

	@Override
	public void modifierEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite, Date dateEntreeFonctionnaire,
			Date dateEntreeAdministration, String statut, Integer ancienneteEchelon, String cadre, String categorie, String classification,
			String grade, String echelon, Date dateEffectAvct, String nouvGrade, String nouvEchelon, String position, String typeAvct,
			String statutPrecision, Integer durMin, Integer durMoy, Integer durMax, boolean agentDetache) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_DATE_ENTREE_SERVICE + "=?," + CHAMP_DATE_ENTREE_COLLECTIVITE
				+ "=?," + CHAMP_DATE_ENTREE_FONCTIONNAIRE + "=?," + CHAMP_DATE_ENTREE_ADMINISTRATION + "=?," + CHAMP_STATUT + "=?,"
				+ CHAMP_ANCIENNETE_ECHELON_JOURS + "=?," + CHAMP_CADRE + "=?," + CHAMP_CATEGORIE + "=?," + CHAMP_CLASSIFICATION + "=?," + CHAMP_GRADE
				+ "=?," + CHAMP_ECHELON + "=?," + CHAMP_DATE_EFFET_AVCT + "=?," + CHAMP_NOUV_GRADE + "=?," + CHAMP_NOUV_ECHELON + "=?,"
				+ CHAMP_POSITION + "=?," + CHAMP_TYPE_AVCT + "=?," + CHAMP_STATUT_PRECISION + "=?," + CHAMP_AVCT_DUR_MIN + "=?," + CHAMP_AVCT_DUR_MOY
				+ "=?," + CHAMP_AVCT_DUR_MAX + "=?," + CHAMP_AGENT_DETACHE + "=? where " + CHAMP_ID_EAE + "=?";

		jdbcTemplate.update(sql, new Object[] { idAgent, dateEntreeService, dateEntreeCollectivite, dateEntreeFonctionnaire,
				dateEntreeAdministration, statut, ancienneteEchelon, cadre, categorie, classification, grade, echelon, dateEffectAvct, nouvGrade,
				nouvEchelon, position, typeAvct, statutPrecision, durMin, durMoy, durMax, agentDetache, idEae });

	}
}
