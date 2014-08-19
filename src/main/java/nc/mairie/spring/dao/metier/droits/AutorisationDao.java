package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Autorisation;
import nc.mairie.spring.dao.utils.SirhDao;

public class AutorisationDao extends SirhDao implements AutorisationDaoInterface {

	public AutorisationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
	}

	@Override
	public ArrayList<Autorisation> listerAutorisationAvecUtilisateur(String userName) throws Exception {
		String sql = "select concat(concat(de.lib_element, '-'),SUBSTR(td.lib_type_droit,1,1)) as LIB_AUTORISATION "
				+ "from droits_element de, droits d, groupe_utilisateur gu, utilisateur u, r_type_droit td "
				+ "where u.login_utilisateur='"
				+ userName
				+ "' and d.id_type_droit is not null "
				+ "and d.id_type_droit = td.id_type_droit and u.id_utilisateur = gu.id_utilisateur and gu.id_groupe = d.id_groupe and d.id_element = de.id_element";

		ArrayList<Autorisation> liste = new ArrayList<Autorisation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Autorisation a = new Autorisation();
			a.setLibAutorisation((String) row.get("LIB_AUTORISATION"));
			liste.add(a);
		}

		return liste;
	}
}
