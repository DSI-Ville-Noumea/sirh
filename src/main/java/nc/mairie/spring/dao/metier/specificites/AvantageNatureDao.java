package nc.mairie.spring.dao.metier.specificites;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.spring.dao.utils.SirhDao;

public class AvantageNatureDao extends SirhDao implements AvantageNatureDaoInterface {

	public static final String CHAMP_NUM_RUBRIQUE = "NUM_RUBRIQUE";
	public static final String CHAMP_ID_TYPE_AVANTAGE = "ID_TYPE_AVANTAGE";
	public static final String CHAMP_ID_NATURE_AVANTAGE = "ID_NATURE_AVANTAGE";
	public static final String CHAMP_MONTANT = "MONTANT";

	public AvantageNatureDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AVANTAGE_NATURE";
		super.CHAMP_ID = "ID_AVANTAGE";
	}

	@Override
	public void supprimerAvantageNature(Integer idAvantage) throws Exception {
		super.supprimerObject(idAvantage);
	}

	@Override
	public Integer creerAvantageNature(Integer numRubrique, Integer idTypeAvantage, Integer idNatureAvantage,
			Double montant) {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_NUM_RUBRIQUE
				+ "," + CHAMP_ID_TYPE_AVANTAGE + "," + CHAMP_ID_NATURE_AVANTAGE + "," + CHAMP_MONTANT + ") "
				+ "VALUES (?,?,?,?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { numRubrique, idTypeAvantage, idNatureAvantage,
				montant }, Integer.class);
		return id;
	}

	@Override
	public ArrayList<AvantageNature> listerAvantageNatureAvecAFF(Integer idAffectation) throws Exception {
		String sql = "select an.* from " + NOM_TABLE
				+ " an, AVANTAGE_NATURE_AFF anAff where anAff.ID_AFFECTATION =? and an." + CHAMP_ID
				+ "=anAff.ID_AVANTAGE order by an." + CHAMP_ID + " asc";

		ArrayList<AvantageNature> liste = new ArrayList<AvantageNature>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAffectation });
		for (Map<String, Object> row : rows) {
			AvantageNature av = new AvantageNature();
			av.setIdAvantage((Integer) row.get(CHAMP_ID));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			av.setNumRubrique(norubr.intValue());
			av.setIdTypeAvantage((Integer) row.get(CHAMP_ID_TYPE_AVANTAGE));
			av.setIdNatureAvantage((Integer) row.get(CHAMP_ID_NATURE_AVANTAGE));
			BigDecimal montant = (BigDecimal) row.get(CHAMP_MONTANT);
			av.setMontant(montant.doubleValue());
			liste.add(av);
		}

		return liste;
	}

	@Override
	public ArrayList<AvantageNature> listerAvantageNatureAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select an.* from " + NOM_TABLE
				+ " an, AVANTAGE_NATURE_FP anFP where anFP.ID_FICHE_POSTE =? and an." + CHAMP_ID
				+ "=anFP.ID_AVANTAGE order by an." + CHAMP_ID + " asc";

		ArrayList<AvantageNature> liste = new ArrayList<AvantageNature>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			AvantageNature av = new AvantageNature();
			av.setIdAvantage((Integer) row.get(CHAMP_ID));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			av.setNumRubrique(norubr.intValue());
			av.setIdTypeAvantage((Integer) row.get(CHAMP_ID_TYPE_AVANTAGE));
			av.setIdNatureAvantage((Integer) row.get(CHAMP_ID_NATURE_AVANTAGE));
			BigDecimal montant = (BigDecimal) row.get(CHAMP_MONTANT);
			av.setMontant(montant.doubleValue());
			liste.add(av);
		}

		return liste;
	}

	@Override
	public ArrayList<AvantageNature> listerAvantageNatureAvecTypeAvantage(Integer idTypeAvantage) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_AVANTAGE + "=?";

		ArrayList<AvantageNature> liste = new ArrayList<AvantageNature>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeAvantage });
		for (Map<String, Object> row : rows) {
			AvantageNature av = new AvantageNature();
			av.setIdAvantage((Integer) row.get(CHAMP_ID));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			av.setNumRubrique(norubr.intValue());
			av.setIdTypeAvantage((Integer) row.get(CHAMP_ID_TYPE_AVANTAGE));
			av.setIdNatureAvantage((Integer) row.get(CHAMP_ID_NATURE_AVANTAGE));
			BigDecimal montant = (BigDecimal) row.get(CHAMP_MONTANT);
			av.setMontant(montant.doubleValue());
			liste.add(av);
		}

		return liste;
	}

	@Override
	public ArrayList<AvantageNature> listerAvantageNatureAvecNatureAvantage(Integer idNatureAvantage) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_NATURE_AVANTAGE + "=?";

		ArrayList<AvantageNature> liste = new ArrayList<AvantageNature>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idNatureAvantage });
		for (Map<String, Object> row : rows) {
			AvantageNature av = new AvantageNature();
			av.setIdAvantage((Integer) row.get(CHAMP_ID));
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_NUM_RUBRIQUE);
			av.setNumRubrique(norubr.intValue());
			av.setIdTypeAvantage((Integer) row.get(CHAMP_ID_TYPE_AVANTAGE));
			av.setIdNatureAvantage((Integer) row.get(CHAMP_ID_NATURE_AVANTAGE));
			BigDecimal montant = (BigDecimal) row.get(CHAMP_MONTANT);
			av.setMontant(montant.doubleValue());
			liste.add(av);
		}

		return liste;
	}

}