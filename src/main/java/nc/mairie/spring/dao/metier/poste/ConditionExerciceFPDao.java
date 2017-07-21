package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ConditionExerciceFP;
import nc.mairie.spring.dao.utils.SirhDao;

/**
 * Created by gael on 21/07/2017.
 */
public class ConditionExerciceFPDao extends SirhDao implements ConditionExerciceFPInterface {

    public ConditionExerciceFPDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "CONDITION_EXERCICE_FP";
    }

    @Override
    public void supprimerConditionExerciceFP(ConditionExerciceFP conditionExerciceFP) {
        String sql = "DELETE FROM " + NOM_TABLE + " where ID_FICHE_POSTE=? AND ID_CONDITION_EXERCICE=?";
        jdbcTemplate.update(sql, new Object[] { conditionExerciceFP.getIdFichePoste(), conditionExerciceFP.getIdConditionExercice() });
    }

    @Override
    public void ajouterConditionExerciceFP(ConditionExerciceFP conditionExerciceFP) {
        String sql = "INSERT INTO " + NOM_TABLE + "(ID_FICHE_POSTE, ID_CONDITION_EXERCICE) VALUES(?,?)";
        jdbcTemplate.update(sql, new Object[] { conditionExerciceFP.getIdFichePoste(), conditionExerciceFP.getIdConditionExercice() });
    }
}
