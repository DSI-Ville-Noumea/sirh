package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AvisCapDao extends SirhDao implements AvisCapDaoInterface {

	public static final String CHAMP_LIB_COURT_AVIS_CAP = "LIB_COURT_AVIS_CAP";
	public static final String CHAMP_LIB_LONG_AVIS_CAP = "LIB_LONG_AVIS_CAP";

	public AvisCapDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_AVIS_CAP";
		super.CHAMP_ID = "ID_AVIS_CAP";
	}

	@Override
	public ArrayList<AvisCap> listerAvisCapFavDefav() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where  " + CHAMP_LIB_COURT_AVIS_CAP
				+ " not in('Min','Moy','Max')";

		ArrayList<AvisCap> liste = new ArrayList<AvisCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			AvisCap a = new AvisCap();
			a.setIdAvisCap((Integer) row.get(CHAMP_ID));
			a.setLibCourtAvisCap((String) row.get(CHAMP_LIB_COURT_AVIS_CAP));
			a.setLibLongAvisCap((String) row.get(CHAMP_LIB_LONG_AVIS_CAP));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<AvisCap> listerAvisCapMinMoyMax() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where  " + CHAMP_LIB_COURT_AVIS_CAP + " in('Min','Moy','Max')";

		ArrayList<AvisCap> liste = new ArrayList<AvisCap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			AvisCap a = new AvisCap();
			a.setIdAvisCap((Integer) row.get(CHAMP_ID));
			a.setLibCourtAvisCap((String) row.get(CHAMP_LIB_COURT_AVIS_CAP));
			a.setLibLongAvisCap((String) row.get(CHAMP_LIB_LONG_AVIS_CAP));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public AvisCap chercherAvisCapByLibCourt(String libAvisCapCourt) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_COURT_AVIS_CAP + " = ? ";
		AvisCap avis = (AvisCap) jdbcTemplate.queryForObject(sql, new Object[] { libAvisCapCourt },
				new BeanPropertyRowMapper<AvisCap>(AvisCap.class));
		return avis;
	}

	@Override
	public AvisCap chercherAvisCap(Integer idAvisCap) throws Exception {
		return super.chercherObject(AvisCap.class, idAvisCap);
	}

	@Override
	public List<AvisCap> listerAvisCap() throws Exception {
		return super.getListe(AvisCap.class);
	}
}
