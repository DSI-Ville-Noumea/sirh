package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.CentreFormation;
import nc.mairie.spring.dao.SirhDao;

public class CentreFormationDao extends SirhDao implements CentreFormationDaoInterface {

	public static final String CHAMP_LIB_CENTRE_FORMATION = "LIB_CENTRE_FORMATION";

	public CentreFormationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_CENTRE_FORMATION";
		super.CHAMP_ID = "ID_CENTRE_FORMATION";
	}

	@Override
	public ArrayList<CentreFormation> listerCentreFormation() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_CENTRE_FORMATION;

		ArrayList<CentreFormation> listeCentreFormation = new ArrayList<CentreFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			CentreFormation centre = new CentreFormation();
			centre.setIdCentreFormation((Integer) row.get(CHAMP_ID));
			centre.setLibCentreFormation((String) row.get(CHAMP_LIB_CENTRE_FORMATION));
			listeCentreFormation.add(centre);
		}

		return listeCentreFormation;
	}

	@Override
	public CentreFormation chercherCentreFormation(Integer idCentreFormation) throws Exception {
		return super.chercherObject(CentreFormation.class, idCentreFormation);
	}

	@Override
	public void creerCentreFormation(String libelleCentre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_CENTRE_FORMATION + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleCentre.toUpperCase() });
	}

	@Override
	public void modifierCentreFormation(Integer idCentre, String libelleCentre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_CENTRE_FORMATION + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleCentre, idCentre });
	}

	@Override
	public void supprimerCentreFormation(Integer idCentreFormation) throws Exception {
		super.supprimerObject(idCentreFormation);
	}
}
