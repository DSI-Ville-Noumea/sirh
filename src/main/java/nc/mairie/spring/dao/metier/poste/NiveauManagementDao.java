package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import nc.mairie.metier.poste.NiveauManagement;
import nc.mairie.spring.dao.utils.SirhDao;

public class NiveauManagementDao extends SirhDao implements NiveauManagementDaoInterface {
	
    public NiveauManagementDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "NIVEAU_MANAGEMENT";
        super.CHAMP_ID = "ID_NIVEAU_MANAGEMENT";
    }

    public List<NiveauManagement> listerNiveauManagement() {
        String sql = "SELECT ID_NIVEAU_MANAGEMENT, LIB_NIVEAU_MANAGEMENT FROM " + NOM_TABLE
                + " ORDER BY ORDRE";
        return jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper<>(NiveauManagement.class));
    }

    public NiveauManagement getNiveauManagement(Integer idNiveauManagement) {
    	
    	if (idNiveauManagement == null)
    		return null;
    	
        String sql = "SELECT ID_NIVEAU_MANAGEMENT, LIB_NIVEAU_MANAGEMENT FROM " + NOM_TABLE
                + " WHERE ID_NIVEAU_MANAGEMENT=?";

        return jdbcTemplate.queryForObject(sql, new Object[] {idNiveauManagement}, new BeanPropertyRowMapper<>(NiveauManagement.class));
    }

}
