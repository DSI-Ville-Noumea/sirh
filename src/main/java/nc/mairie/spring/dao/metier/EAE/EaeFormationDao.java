package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeFormation;
import nc.mairie.spring.dao.EaeDao;

public class EaeFormationDao extends EaeDao implements EaeFormationDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ANNEE_FORMATION = "ANNEE_FORMATION";
	public static final String CHAMP_DUREE_FORMATION = "DUREE_FORMATION";
	public static final String CHAMP_LIBELLE_FORMATION = "LIBELLE_FORMATION";

	public EaeFormationDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_FORMATION";
		super.CHAMP_ID = "ID_EAE_FORMATION";
	}

	@Override
	public void creerEaeFormation(Integer idEae, Integer anneeFormation, String dureeFormation, String libFormation)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_ANNEE_FORMATION + ","
				+ CHAMP_DUREE_FORMATION + "," + CHAMP_LIBELLE_FORMATION + ") " + "VALUES (?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, anneeFormation, dureeFormation, libFormation });

	}

	@Override
	public ArrayList<EaeFormation> listerEaeFormation(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeFormation> listeEaeFormation = new ArrayList<EaeFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeFormation form = new EaeFormation();
			form.setIdEaeFormation((Integer) row.get(CHAMP_ID));
			form.setIdEAE((Integer) row.get(CHAMP_ID_EAE));
			form.setAnneeFormation((Integer) row.get(CHAMP_ANNEE_FORMATION));
			form.setDureeFormation((String) row.get(CHAMP_DUREE_FORMATION));
			form.setLibelleFormation((String) row.get(CHAMP_LIBELLE_FORMATION));

			listeEaeFormation.add(form);
		}
		return listeEaeFormation;
	}

	@Override
	public void supprimerEaeFormation(Integer idFormation) throws Exception {
		super.supprimerObject(idFormation);
	}
}
