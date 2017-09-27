package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGeneraleFP;
import nc.mairie.spring.dao.utils.SirhDao;

/**
 * Created by gael on 21/07/2017.
 */
public class ActiviteGeneraleFPDao extends SirhDao implements ActiviteGeneraleFPInterface {

    public ActiviteGeneraleFPDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "ACTIVITE_GENERALE_FP";
    }

    @Override
    public void supprimerActiviteGeneraleFP(ActiviteGeneraleFP activiteGeneraleFP) {
        String sql = "DELETE FROM " + NOM_TABLE + " where ID_FICHE_POSTE=? AND ID_ACTIVITE_GENERALE=?";
        jdbcTemplate.update(sql, new Object[] { activiteGeneraleFP.getIdFichePoste(), activiteGeneraleFP.getIdActiviteGenerale() });
    }

    @Override
    public void ajouterActiviteGeneraleFP(ActiviteGeneraleFP activiteGeneraleFP) {
        String sql = "INSERT INTO " + NOM_TABLE + "(ID_FICHE_POSTE, ID_ACTIVITE_GENERALE, ORDRE) VALUES(?,?, ?)";
        jdbcTemplate.update(sql, new Object[] { activiteGeneraleFP.getIdFichePoste(), activiteGeneraleFP.getIdActiviteGenerale(), activiteGeneraleFP.getOrdre()});
    }
}
