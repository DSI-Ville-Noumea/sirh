package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.NomHandicap;
import nc.mairie.spring.dao.SirhDao;

public class NomHandicapDao extends SirhDao implements NomHandicapDaoInterface {

	public static final String CHAMP_NOM_TYPE_HANDICAP = "NOM_TYPE_HANDICAP";

	public NomHandicapDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_NOM_HANDICAP";
		super.CHAMP_ID = "ID_TYPE_HANDICAP";
	}

	@Override
	public ArrayList<NomHandicap> listerNomHandicap() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_NOM_TYPE_HANDICAP;

		ArrayList<NomHandicap> liste = new ArrayList<NomHandicap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NomHandicap a = new NomHandicap();
			a.setIdTypeHandicap((Integer) row.get(CHAMP_ID));
			a.setNomTypeHandicap((String) row.get(CHAMP_NOM_TYPE_HANDICAP));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public NomHandicap chercherNomHandicap(Integer idTypeHandicap) throws Exception {
		return super.chercherObject(NomHandicap.class, idTypeHandicap);
	}
}
