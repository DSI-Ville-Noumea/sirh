package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.Representant;
import nc.mairie.spring.dao.SirhDao;

public class RepresentantDao extends SirhDao implements RepresentantDaoInterface {

	public static final String CHAMP_ID_TYPE_REPRESENTANT = "ID_TYPE_REPRESENTANT";
	public static final String CHAMP_NOM_REPRESENTANT = "NOM_REPRESENTANT";
	public static final String CHAMP_PRENOM_REPRESENTANT = "PRENOM_REPRESENTANT";

	public RepresentantDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_REPRESENTANT";
		super.CHAMP_ID = "ID_REPRESENTANT";
	}

	@Override
	public Representant chercherRepresentant(Integer idRepresentant) throws Exception {
		return super.chercherObject(Representant.class, idRepresentant);
	}

	@Override
	public void creerRepresentant(Integer idTypeRepresentant, String nomRepresentant, String prenomRepresentant)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TYPE_REPRESENTANT + "," + CHAMP_NOM_REPRESENTANT
				+ "," + CHAMP_PRENOM_REPRESENTANT + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { idTypeRepresentant, nomRepresentant.toUpperCase(), prenomRepresentant.toUpperCase() });
	}

	@Override
	public void modifierRepresentant(Integer idRepresentant, Integer idTypeRepresentant, String nomRepresentant,
			String prenomRepresentant) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_REPRESENTANT + "=?," + CHAMP_NOM_REPRESENTANT
				+ "=?," + CHAMP_PRENOM_REPRESENTANT + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql,
				new Object[] { idTypeRepresentant, nomRepresentant.toUpperCase(), prenomRepresentant.toUpperCase(),
						idRepresentant });
	}

	@Override
	public void supprimerRepresentant(Integer idRepresentant) throws Exception {
		super.supprimerObject(idRepresentant);
	}

	@Override
	public ArrayList<Representant> listerRepresentantOrderByNom() {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_NOM_REPRESENTANT;

		ArrayList<Representant> listeRepresentant = new ArrayList<Representant>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Representant repre = new Representant();
			repre.setIdRepresentant((Integer) row.get(CHAMP_ID));
			repre.setIdTypeRepresentant((Integer) row.get(CHAMP_ID_TYPE_REPRESENTANT));
			repre.setNomRepresentant((String) row.get(CHAMP_NOM_REPRESENTANT));
			repre.setPrenomRepresentant((String) row.get(CHAMP_PRENOM_REPRESENTANT));
			listeRepresentant.add(repre);
		}

		return listeRepresentant;
	}
}
