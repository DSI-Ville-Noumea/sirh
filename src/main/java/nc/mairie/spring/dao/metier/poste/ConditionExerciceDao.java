package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGenerale;
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

    public List<ConditionExercice> listerToutesConditionExercice(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        String sql = "SELECT ID_CONDITION_EXERCICE, NOM_CONDITION_EXERCICE, MAX(ORDRE) AS CEX_ORDRE, CHECKED " +
                "FROM (SELECT DISTINCT CE_FM.ID_CONDITION_EXERCICE, CE.NOM_CONDITION_EXERCICE,FM_FP.FM_PRIMAIRE, " +
                "CASE WHEN CE_FP.ORDRE IS NULL THEN CE_FM.ORDRE ELSE CE_FP.ORDRE END AS ORDRE, " +
                "CASE WHEN CE_FP.ID_CONDITION_EXERCICE IS NULL THEN '0' ELSE '1' END AS CHECKED " +
                "FROM CONDITION_EXERCICE_FM CE_FM " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = CE_FM.ID_FICHE_METIER " +
                "JOIN CONDITION_EXERCICE CE ON CE.ID_CONDITION_EXERCICE = CE_FM.ID_CONDITION_EXERCICE " +
                "LEFT JOIN CONDITION_EXERCICE_FP CE_FP ON CE_FP.ID_CONDITION_EXERCICE = CE.ID_CONDITION_EXERCICE AND (CE_FP.ID_FICHE_POSTE = ?) " +
                "WHERE (FM_FP.ID_FICHE_POSTE = ? AND FM_FP.ID_FICHE_METIER IN(?, ?)) " +
                "OR (FM_FP.ID_FICHE_METIER NOT IN (SELECT FM_FP.ID_FICHE_METIER FROM FM_FP WHERE FM_FP.ID_FICHE_POSTE = ?) AND FM_FP.ID_FICHE_METIER IN (?, ?)) " +
                "ORDER BY FM_FP.FM_PRIMAIRE DESC, ORDRE) CEX " +
                "GROUP BY ID_CONDITION_EXERCICE, NOM_CONDITION_EXERCICE, CHECKED " +
                "ORDER BY CEX_ORDRE";
        return jdbcTemplate.query(sql, new Object[]{idFichePoste, idFichePoste, idFicheMetierPrimaire, idFicheMetierSecondaire, idFichePoste, idFicheMetierPrimaire, idFicheMetierSecondaire}, new BeanPropertyRowMapper<>(ConditionExercice.class));
    }

    public void supprimerToutesConditionExercice(FichePoste fp) {
        String sql = "DELETE FROM CONDITION_EXERCICE_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }

    public List<ConditionExercice> listerToutesConditionExerciceChecked(FichePoste fp) {
        String sql = "SELECT DISTINCT CE_FM.ID_CONDITION_EXERCICE, CE.NOM_CONDITION_EXERCICE,FM_FP.FM_PRIMAIRE, " +
                "CASE WHEN CE_FP.ORDRE IS NULL THEN CE_FM.ORDRE ELSE CE_FP.ORDRE END AS ORDRE, " +
                "CASE WHEN CE_FP.ID_CONDITION_EXERCICE IS NULL THEN '0' ELSE '1' END AS CHECKED " +
                "FROM CONDITION_EXERCICE_FM CE_FM " +
                "JOIN CONDITION_EXERCICE CE ON CE.ID_CONDITION_EXERCICE = CE_FM.ID_CONDITION_EXERCICE " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = CE_FM.ID_FICHE_METIER " +
                "LEFT JOIN CONDITION_EXERCICE_FP CE_FP ON CE_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND CE_FP.ID_CONDITION_EXERCICE = CE.ID_CONDITION_EXERCICE " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? AND CE_FP.ID_CONDITION_EXERCICE IS NOT NULL " +
                "ORDER BY FM_FP.FM_PRIMAIRE DESC, ORDRE";
        return jdbcTemplate.query(sql, new Object[]{fp.getIdFichePoste()}, new BeanPropertyRowMapper<>(ConditionExercice.class));
    }
}
