package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.Delegation;
import nc.mairie.spring.dao.utils.SirhDao;

public class DelegationDao extends SirhDao implements DelegationDaoInterface {

	public static final String CHAMP_ID_TYPE_DELEGATION = "ID_TYPE_DELEGATION";
	public static final String CHAMP_LIB_DELEGATION = "LIB_DELEGATION";

	public DelegationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DELEGATION";
		super.CHAMP_ID = "ID_DELEGATION";
	}

	@Override
	public ArrayList<Delegation> listerDelegationAvecTypeDelegation(Integer idTypeDelegation) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_DELEGATION + "=? ";

		ArrayList<Delegation> liste = new ArrayList<Delegation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeDelegation });
		for (Map<String, Object> row : rows) {
			Delegation d = new Delegation();
			d.setIdDelegation((Integer) row.get(CHAMP_ID));
			d.setIdTypeDelegation((Integer) row.get(CHAMP_ID_TYPE_DELEGATION));
			d.setLibDelegation((String) row.get(CHAMP_LIB_DELEGATION));
			liste.add(d);
		}

		return liste;
	}

	@Override
	public ArrayList<Delegation> listerDelegationAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select del.* from " + NOM_TABLE
				+ " del, DELEGATION_FP delFP where delFP.ID_FICHE_POSTE =? and del." + CHAMP_ID
				+ "= delFP.ID_DELEGATION order by del." + CHAMP_ID + " asc";

		ArrayList<Delegation> liste = new ArrayList<Delegation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			Delegation d = new Delegation();
			d.setIdDelegation((Integer) row.get(CHAMP_ID));
			d.setIdTypeDelegation((Integer) row.get(CHAMP_ID_TYPE_DELEGATION));
			d.setLibDelegation((String) row.get(CHAMP_LIB_DELEGATION));
			liste.add(d);
		}

		return liste;
	}

	@Override
	public ArrayList<Delegation> listerDelegationAvecAFF(Integer idAffectation) throws Exception {
		String sql = "select del.* from " + NOM_TABLE
				+ " del, DELEGATION_AFF delAff where delAff.ID_AFFECTATION =? and del." + CHAMP_ID
				+ "= delAff.ID_DELEGATION order by del." + CHAMP_ID + " asc";

		ArrayList<Delegation> liste = new ArrayList<Delegation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAffectation });
		for (Map<String, Object> row : rows) {
			Delegation d = new Delegation();
			d.setIdDelegation((Integer) row.get(CHAMP_ID));
			d.setIdTypeDelegation((Integer) row.get(CHAMP_ID_TYPE_DELEGATION));
			d.setLibDelegation((String) row.get(CHAMP_LIB_DELEGATION));
			liste.add(d);
		}

		return liste;
	}

	@Override
	public Integer creerDelegation(Integer idTypeDelegation, String libDelegation) {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " ("
				+ CHAMP_ID_TYPE_DELEGATION + "," + CHAMP_LIB_DELEGATION + ") " + "VALUES (?,?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { idTypeDelegation, libDelegation }, Integer.class);
		return id;
	}

	@Override
	public void supprimerDelegation(Integer idDelegation) throws Exception {
		super.supprimerObject(idDelegation);
	}

}