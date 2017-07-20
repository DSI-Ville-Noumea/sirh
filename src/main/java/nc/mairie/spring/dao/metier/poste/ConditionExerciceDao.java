package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ConditionExercice;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public class ConditionExerciceDao extends SirhDao implements ConditionExerciceInterface {

    public ConditionExerciceDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "CONDITION_EXERCICE";
        super.CHAMP_ID = "ID_CONDITION_EXERCICE";
    }

    @Override
    public List<ConditionExercice> listerToutesConditionExercice(FichePoste fp) {
        String sql = "SELECT DISTINCT CE.ID_CONDITION_EXERCICE, CE.NOM_CONDITION_EXERCICE, " +
                "CASE WHEN CE_FP.ID_CONDITION_EXERCICE IS NULL THEN '0' ELSE '1' END AS CHECKED " +
                "FROM CONDITION_EXERCICE_FM CE_FM " +
                "JOIN CONDITION_EXERCICE CE ON CE.ID_CONDITION_EXERCICE = CE_FM.ID_CONDITION_EXERCICE " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = CE_FM.ID_FICHE_METIER " +
                "LEFT JOIN CONDITION_EXERCICE_FP CE_FP ON CE_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND CE_FP.ID_CONDITION_EXERCICE = CE.ID_CONDITION_EXERCICE " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY CE.NOM_CONDITION_EXERCICE";
        return jdbcTemplate.query(sql, new Object[]{fp.getIdFichePoste()}, new BeanPropertyRowMapper<>(ConditionExercice.class));
    }
}
