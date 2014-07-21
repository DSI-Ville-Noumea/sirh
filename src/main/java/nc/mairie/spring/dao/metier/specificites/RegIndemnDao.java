package nc.mairie.spring.dao.metier.specificites;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.spring.dao.SirhDao;

public class RegIndemnDao extends SirhDao implements RegIndemnDaoInterface {

	public static final String CHAMP_ID_TYPE_REG_INDEMN = "ID_TYPE_REG_INDEMN";
	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";
	public static final String CHAMP_FORFAIT = "FORFAIT";
	public static final String CHAMP_NOMBRE_POINTS = "NOMBRE_POINTS";

	public RegIndemnDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "REGIME_INDEMNITAIRE";
		super.CHAMP_ID = "ID_REG_INDEMN";
	}

	@Override
	public Integer creerRegimeIndemnitaire(Integer idTypeRegIndemn, Integer numRubrique, Double forfait,
			Integer nombrePoints) {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " ("
				+ CHAMP_ID_TYPE_REG_INDEMN + "," + CHAMP_NUM_RUBRIQUE + "," + CHAMP_FORFAIT + "," + CHAMP_NOMBRE_POINTS
				+ ") " + "VALUES (?,?,?,?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { idTypeRegIndemn, numRubrique, forfait,
				nombrePoints }, Integer.class);
		return id;
	}

	@Override
	public void supprimerRegimeIndemnitaire(Integer idRegime) throws Exception {
		super.supprimerObject(idRegime);
	}

	@Override
	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecTypeRegime(Integer idTypeRegIndemn)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_REG_INDEMN + "=? ";

		ArrayList<RegimeIndemnitaire> liste = new ArrayList<RegimeIndemnitaire>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeRegIndemn });
		for (Map<String, Object> row : rows) {
			RegimeIndemnitaire d = new RegimeIndemnitaire();
			d.setIdRegIndemn((Integer) row.get(CHAMP_ID));
			d.setIdTypeRegIndemn((Integer) row.get(CHAMP_ID_TYPE_REG_INDEMN));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			d.setNumRubrique(norubr == null ? null : norubr.intValue());
			BigDecimal forfait = (BigDecimal) row.get(CHAMP_FORFAIT);
			d.setForfait(forfait.doubleValue());
			d.setNombrePoints((Integer) row.get(CHAMP_NOMBRE_POINTS));
			liste.add(d);
		}

		return liste;
	}

	@Override
	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select ri.* from " + NOM_TABLE + " ri, REG_INDEMN_FP riFP where riFP.ID_FICHE_POSTE =? and ri."
				+ CHAMP_ID + " = riFP.ID_REGIME order by ri." + CHAMP_ID + " ASC";

		ArrayList<RegimeIndemnitaire> liste = new ArrayList<RegimeIndemnitaire>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			RegimeIndemnitaire d = new RegimeIndemnitaire();
			d.setIdRegIndemn((Integer) row.get(CHAMP_ID));
			d.setIdTypeRegIndemn((Integer) row.get(CHAMP_ID_TYPE_REG_INDEMN));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			d.setNumRubrique(norubr == null ? null : norubr.intValue());
			BigDecimal forfait = (BigDecimal) row.get(CHAMP_FORFAIT);
			d.setForfait(forfait.doubleValue());
			d.setNombrePoints((Integer) row.get(CHAMP_NOMBRE_POINTS));
			liste.add(d);
		}

		return liste;
	}

	@Override
	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecAFF(Integer idAffectation) throws Exception {
		String sql = "select ri.* from " + NOM_TABLE
				+ " ri, REG_INDEMN_AFF riAff where riAff.ID_AFFECTATION =? and ri." + CHAMP_ID
				+ " = riAff.ID_REGIME order by ri." + CHAMP_ID + " ASC";

		ArrayList<RegimeIndemnitaire> liste = new ArrayList<RegimeIndemnitaire>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAffectation });
		for (Map<String, Object> row : rows) {
			RegimeIndemnitaire d = new RegimeIndemnitaire();
			d.setIdRegIndemn((Integer) row.get(CHAMP_ID));
			d.setIdTypeRegIndemn((Integer) row.get(CHAMP_ID_TYPE_REG_INDEMN));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			d.setNumRubrique(norubr == null ? null : norubr.intValue());
			BigDecimal forfait = (BigDecimal) row.get(CHAMP_FORFAIT);
			d.setForfait(forfait.doubleValue());
			d.setNombrePoints((Integer) row.get(CHAMP_NOMBRE_POINTS));
			liste.add(d);
		}

		return liste;
	}

}