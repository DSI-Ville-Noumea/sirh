package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.PathAlfresco;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.dao.EmptyResultDataAccessException;

public class PathAlfrescoDao extends SirhDao  implements PathAlfrescoDaoInterface {

	public static final String CHAMP_LIB_PATH_ALFRESCO = "PATH_ALFRESCO";

	public PathAlfrescoDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_PATH_ALFRESCO";
		super.CHAMP_ID = "ID_PATH_ALFRESCO";
	}

	@Override
	public ArrayList<PathAlfresco> listerPathAlfresco() throws Exception {
		
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_PATH_ALFRESCO;

		ArrayList<PathAlfresco> listePathAlfresco = new ArrayList<PathAlfresco>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] {});
		for (Map<String, Object> row : rows) {
			PathAlfresco path = new PathAlfresco();
			path.setIdPathAlfresco((Integer) row.get(CHAMP_ID));
			path.setPathAlfresco((String) row.get(CHAMP_LIB_PATH_ALFRESCO));
			listePathAlfresco.add(path);
		}

		return listePathAlfresco;
	}

	@Override
	public PathAlfresco chercherPathAlfresco(Integer idPathAlfresco) throws Exception {
		try {
			return super.chercherObject(PathAlfresco.class, idPathAlfresco);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

}
