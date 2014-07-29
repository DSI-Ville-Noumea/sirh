package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.MotifCarriere;
import nc.mairie.spring.dao.utils.SirhDao;

public class MotifCarriereDao extends SirhDao implements MotifCarriereDaoInterface {

	public static final String CHAMP_LIB_MOTIF_CARRIERE = "LIB_MOTIF_CARRIERE";

	public MotifCarriereDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MOTIF_CARRIERE";
		super.CHAMP_ID = "ID_MOTIF_CARRIERE";
	}

	@Override
	public void creerMotifCarriere(String libelleMotifCarriere) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_MOTIF_CARRIERE + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleMotifCarriere });
	}

	@Override
	public void supprimerMotifCarriere(Integer idMotifCarriere) throws Exception {
		super.supprimerObject(idMotifCarriere);
	}

	@Override
	public ArrayList<MotifCarriere> listerMotifCarriere() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MOTIF_CARRIERE;

		ArrayList<MotifCarriere> listeMotif = new ArrayList<MotifCarriere>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifCarriere motif = new MotifCarriere();
			motif.setIdMotifCarriere((Integer) row.get(CHAMP_ID));
			motif.setLibMotifCarriere((String) row.get(CHAMP_LIB_MOTIF_CARRIERE));

			listeMotif.add(motif);
		}

		return listeMotif;
	}
}
