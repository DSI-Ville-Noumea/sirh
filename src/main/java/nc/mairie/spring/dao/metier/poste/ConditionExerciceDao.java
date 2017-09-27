package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGenerale;
import nc.mairie.metier.poste.ConditionExercice;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
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

    public List<ConditionExercice> listerToutesConditionExercice(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        Integer idFMSecondaire = idFicheMetierSecondaire != null ? idFicheMetierSecondaire : idFicheMetierPrimaire;
        String sql = "SELECT ID_CONDITION_EXERCICE, NOM_CONDITION_EXERCICE, CHECKED, MIN(FM_ORDRE) AS CER_ORDRE, MIN(CE_ORDRE) AS CE_ORDRE FROM " +
                "(SELECT DISTINCT CE_FM.ID_CONDITION_EXERCICE, CE.NOM_CONDITION_EXERCICE, FM_FP.FM_PRIMAIRE, " +
                "CASE WHEN CE_FP.ORDRE IS NULL THEN CE_FM.ORDRE ELSE CE_FP.ORDRE END AS CE_ORDRE, " +
                "CASE WHEN CE_FP.ID_CONDITION_EXERCICE IS NULL THEN '0' ELSE '1' END AS CHECKED, " +
                "CASE WHEN CE_FM.ID_FICHE_METIER = ? THEN '0' ELSE '1' END AS FM_ORDRE " +
                "FROM CONDITION_EXERCICE_FM CE_FM " +
                "LEFT JOIN FM_FP ON FM_FP.ID_FICHE_METIER = CE_FM.ID_FICHE_METIER AND FM_FP.ID_FICHE_POSTE = ? " +
                "JOIN CONDITION_EXERCICE CE ON CE.ID_CONDITION_EXERCICE = CE_FM.ID_CONDITION_EXERCICE " +
                "LEFT JOIN CONDITION_EXERCICE_FP CE_FP ON CE_FP.ID_CONDITION_EXERCICE = CE.ID_CONDITION_EXERCICE AND (CE_FP.ID_FICHE_POSTE = ?) " +
                "WHERE CE_FM.ID_FICHE_METIER IN (?, ?) " +
                "ORDER BY FM_ORDRE, CE_ORDRE) CER " +
                "GROUP BY ID_CONDITION_EXERCICE, NOM_CONDITION_EXERCICE, CHECKED " +
                "ORDER BY CER_ORDRE, CE_ORDRE";
        return jdbcTemplate.query(sql, new Object[]{idFicheMetierPrimaire, idFichePoste, idFichePoste, idFicheMetierPrimaire, idFMSecondaire}, new BeanPropertyRowMapper<>(ConditionExercice.class));
    }

    public void supprimerToutesConditionExercice(FichePoste fp) {
        String sql = "DELETE FROM CONDITION_EXERCICE_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }

    public List<ConditionExercice> listerToutesConditionExerciceChecked(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        List<ConditionExercice> listConditionExercice = listerToutesConditionExercice(fp, idFicheMetierPrimaire, idFicheMetierSecondaire);
        List<ConditionExercice> listConditionExerciceChecked = new ArrayList<>();
        for (int i = 0; i < listConditionExercice.size(); i++) {
            ConditionExercice ce = listConditionExercice.get(i);
            if (ce.getChecked()) {
                listConditionExerciceChecked.add(ce);
            }
        }
        return listConditionExerciceChecked;
    }
}
