package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.NatureCredit;
import nc.mairie.spring.dao.SirhDao;

public class NatureCreditDao extends SirhDao implements NatureCreditDaoInterface {

	public static final String CHAMP_LIB_NATURE_CREDIT = "LIB_NATURE_CREDIT";
	public static final String CHAMP_ORDRE_AFF_NATURE_CREDIT = "ORDRE_AFF";

	public NatureCreditDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_NATURE_CREDIT";
		super.CHAMP_ID = "ID_NATURE_CREDIT";
	}

	@Override
	public ArrayList<NatureCredit> listerNatureCreditOrderBy() {
		String sql = "select *  from " + NOM_TABLE + " order by " + CHAMP_ORDRE_AFF_NATURE_CREDIT + " WITH UR";

		ArrayList<NatureCredit> listeNatureCredit = new ArrayList<NatureCredit>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			NatureCredit nature = new NatureCredit();
			nature.setIdNatureCredit((Integer) row.get(CHAMP_ID));
			nature.setLibNatureCredit((String) row.get(CHAMP_LIB_NATURE_CREDIT));
			nature.setOrdreAffichage((Integer) row.get(CHAMP_ORDRE_AFF_NATURE_CREDIT));
			listeNatureCredit.add(nature);
		}

		return listeNatureCredit;
	}
}
