package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.MotifNonRecrutement;
import nc.mairie.spring.dao.SirhDao;

public class MotifNonRecrutementDao extends SirhDao implements MotifNonRecrutementDaoInterface {

	public static final String CHAMP_LIB_MOTIF_NON_RECRUT = "LIB_MOTIF_NON_RECRUT";

	public MotifNonRecrutementDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MOTIF_NON_RECRUT";
		super.CHAMP_ID = "ID_MOTIF_NON_RECRUT";
	}

	@Override
	public void creerMotifNonRecrutement(String libelleMotifNonRecrutement) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_MOTIF_NON_RECRUT + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleMotifNonRecrutement.toUpperCase() });
	}

	@Override
	public void supprimerMotifNonRecrutement(Integer idMotifNonRecrutement) throws Exception {
		super.supprimerObject(idMotifNonRecrutement);
	}

	@Override
	public ArrayList<MotifNonRecrutement> listerMotifNonRecrutement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MOTIF_NON_RECRUT;

		ArrayList<MotifNonRecrutement> listeMotif = new ArrayList<MotifNonRecrutement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifNonRecrutement motif = new MotifNonRecrutement();
			motif.setIdMotifNonRecrut((Integer) row.get(CHAMP_ID));
			motif.setLibMotifNonRecrut((String) row.get(CHAMP_LIB_MOTIF_NON_RECRUT));

			listeMotif.add(motif);
		}

		return listeMotif;
	}
}
