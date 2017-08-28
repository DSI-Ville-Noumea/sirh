package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.CompetenceManagement;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

public class CompetenceManagementDao extends SirhDao implements CompetenceManagementInterface {

    public CompetenceManagementDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "COMPETENCE_MANAGEMENT";
        super.CHAMP_ID = "ID_COMPETENCE_MANAGEMENT";
    }

    public List<CompetenceManagement> listerToutesCompetencesManagement(Integer idNiveauManagement) {
        String sql = "SELECT ID_COMPETENCE_MANAGEMENT, ID_NIVEAU_MANAGEMENT, LIB_COMPETENCE_MANAGEMENT, ORDRE FROM "
                + NOM_TABLE + " WHERE ID_NIVEAU_MANAGEMENT=? ORDER BY ORDRE";
        return jdbcTemplate.query(sql, new Object[]{idNiveauManagement}, new BeanPropertyRowMapper<>(CompetenceManagement.class));
    }

}
