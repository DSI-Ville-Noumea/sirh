package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FicheMetier;
import nc.mairie.metier.poste.NiveauManagement;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

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

}
