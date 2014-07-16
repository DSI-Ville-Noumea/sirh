package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class MotifAvancementDao extends SirhDao implements MotifAvancementDaoInterface {

	public static final String CHAMP_LIB_MOTIF_AVCT = "LIB_MOTIF_AVCT";
	public static final String CHAMP_CODE = "CODE";

	public MotifAvancementDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MOTIF_AVCT";
		super.CHAMP_ID = "ID_MOTIF_AVCT";
	}

	@Override
	public ArrayList<MotifAvancement> listerMotifAvancement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MOTIF_AVCT;

		ArrayList<MotifAvancement> listeMotif = new ArrayList<MotifAvancement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifAvancement motif = new MotifAvancement();
			motif.setIdMotifAvct((Integer) row.get(CHAMP_ID));
			motif.setLibMotifAvct((String) row.get(CHAMP_LIB_MOTIF_AVCT));
			motif.setCodeMotifAvct((String) row.get(CHAMP_CODE));

			listeMotif.add(motif);
		}

		return listeMotif;
	}

	@Override
	public MotifAvancement chercherMotifAvancement(Integer idMotifAvancement) throws Exception {
		return super.chercherObject(MotifAvancement.class, idMotifAvancement);
	}

	@Override
	public MotifAvancement chercherMotifAvancementByLib(String libMotifAvancement) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_MOTIF_AVCT + " = ? ";
		MotifAvancement motif = (MotifAvancement) jdbcTemplate.queryForObject(sql, new Object[] { libMotifAvancement },
				new BeanPropertyRowMapper<MotifAvancement>(MotifAvancement.class));
		return motif;
	}

	@Override
	public ArrayList<MotifAvancement> listerMotifAvancementSansRevalo() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_MOTIF_AVCT + " <> 'REVALORISATION' order by "
				+ CHAMP_LIB_MOTIF_AVCT;

		ArrayList<MotifAvancement> listeMotif = new ArrayList<MotifAvancement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifAvancement motif = new MotifAvancement();
			motif.setIdMotifAvct((Integer) row.get(CHAMP_ID));
			motif.setLibMotifAvct((String) row.get(CHAMP_LIB_MOTIF_AVCT));
			motif.setCodeMotifAvct((String) row.get(CHAMP_CODE));

			listeMotif.add(motif);
		}

		return listeMotif;
	}
}
