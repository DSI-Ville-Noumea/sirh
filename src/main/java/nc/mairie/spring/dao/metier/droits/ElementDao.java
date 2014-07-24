package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Element;
import nc.mairie.spring.dao.SirhDao;

public class ElementDao extends SirhDao implements ElementDaoInterface {

	public static final String CHAMP_LIB_ELEMENT = "LIB_ELEMENT";

	public ElementDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DROITS_ELEMENT";
		super.CHAMP_ID = "ID_ELEMENT";
	}

	@Override
	public ArrayList<Element> listerElement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_ELEMENT;

		ArrayList<Element> liste = new ArrayList<Element>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Element a = new Element();
			a.setIdElement((Integer) row.get(CHAMP_ID));
			a.setLibElement((String) row.get(CHAMP_LIB_ELEMENT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public Integer creerElement(String libElement) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_ELEMENT
				+ ") " + "VALUES (?)) ";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { libElement }, Integer.class);
		return id;
	}
}
