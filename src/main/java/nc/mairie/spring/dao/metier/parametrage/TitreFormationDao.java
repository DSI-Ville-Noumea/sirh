package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TitreFormation;
import nc.mairie.spring.dao.SirhDao;

public class TitreFormationDao extends SirhDao implements TitreFormationDaoInterface {

	public static final String CHAMP_LIB_TITRE_FORMATION = "LIB_TITRE_FORMATION";

	public TitreFormationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TITRE_FORMATION";
		super.CHAMP_ID = "ID_TITRE_FORMATION";
	}

	@Override
	public ArrayList<TitreFormation> listerTitreFormation() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TITRE_FORMATION;

		ArrayList<TitreFormation> listeTitreFormation = new ArrayList<TitreFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TitreFormation titre = new TitreFormation();
			titre.setIdTitreFormation((Integer) row.get(CHAMP_ID));
			titre.setLibTitreFormation((String) row.get(CHAMP_LIB_TITRE_FORMATION));
			listeTitreFormation.add(titre);
		}

		return listeTitreFormation;
	}

	@Override
	public TitreFormation chercherTitreFormation(Integer idTitreFormation) throws Exception {
		return super.chercherObject(TitreFormation.class, idTitreFormation);
	}

	@Override
	public void creerTitreFormation(String libelleTitre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TITRE_FORMATION + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTitre.toUpperCase() });
	}

	@Override
	public void modifierTitreFormation(Integer idTitre, String libelleTitre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_TITRE_FORMATION + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleTitre, idTitre });
	}

	@Override
	public void supprimerTitreFormation(Integer idTitreFormation) throws Exception {
		super.supprimerObject(idTitreFormation);
	}
}
