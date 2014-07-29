package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.MotifRecrutement;
import nc.mairie.spring.dao.utils.SirhDao;

public class MotifRecrutementDao extends SirhDao implements MotifRecrutementDaoInterface {

	public static final String CHAMP_LIB_MOTIF_RECRUT = "LIB_MOTIF_RECRUT";

	public MotifRecrutementDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MOTIF_RECRUT";
		super.CHAMP_ID = "ID_MOTIF_RECRUT";
	}

	@Override
	public void creerMotifRecrutement(String libelleMotifRecrutement) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_MOTIF_RECRUT + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleMotifRecrutement.toUpperCase() });
	}

	@Override
	public void supprimerMotifRecrutement(Integer idMotifRecrutement) throws Exception {
		super.supprimerObject(idMotifRecrutement);
	}

	@Override
	public ArrayList<MotifRecrutement> listerMotifRecrutement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MOTIF_RECRUT;

		ArrayList<MotifRecrutement> listeMotif = new ArrayList<MotifRecrutement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifRecrutement motif = new MotifRecrutement();
			motif.setIdMotifRecrut((Integer) row.get(CHAMP_ID));
			motif.setLibMotifRecrut((String) row.get(CHAMP_LIB_MOTIF_RECRUT));

			listeMotif.add(motif);
		}

		return listeMotif;
	}
}
