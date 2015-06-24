package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.DelegationFP;
import nc.mairie.spring.dao.utils.SirhDao;

public class DelegationFPDao extends SirhDao implements DelegationFPDaoInterface {

	public static final String CHAMP_ID_DELEGATION = "ID_DELEGATION";
	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

	public DelegationFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DELEGATION_FP";
	}

	@Override
	public void creerDelegationFP(Integer idDelegation, Integer idFichePoste) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_DELEGATION + "," + CHAMP_ID_FICHE_POSTE + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idDelegation, idFichePoste });
	}

	@Override
	public void supprimerDelegationFP(Integer idDelegation, Integer idFichePoste) {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_DELEGATION + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { idDelegation, idFichePoste });
	}

	@Override
	public ArrayList<DelegationFP> listerDelegationFPAvecFP(Integer idFichePoste) {
		String sql = "select f.* from " + NOM_TABLE + " f where " + CHAMP_ID_FICHE_POSTE + "=? ";

		ArrayList<DelegationFP> liste = new ArrayList<DelegationFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			DelegationFP a = new DelegationFP();
			a.setIdDelegation((Integer) row.get(CHAMP_ID_DELEGATION));
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			liste.add(a);
		}

		return liste;
	}

}