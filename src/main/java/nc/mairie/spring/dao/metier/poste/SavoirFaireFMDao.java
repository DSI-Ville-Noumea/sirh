package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.SavoirFaireFP;
import nc.mairie.spring.dao.utils.SirhDao;

/**
 * Created by gael on 21/07/2017.
 */
public class SavoirFaireFMDao extends SirhDao implements SavoirFaireFMInterface {

    public SavoirFaireFMDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "SAVOIR_FAIRE_FP";
    }

    public void supprimerSavoirFaireFP(SavoirFaireFP savoirFaireFP) {
        String sql = "DELETE FROM " + NOM_TABLE + " where ID_FICHE_POSTE=? AND ID_SAVOIR_FAIRE=?";
        jdbcTemplate.update(sql, new Object[] { savoirFaireFP.getIdFichePoste(), savoirFaireFP.getIdSavoirFaire() });
    }

    public void ajouterSavoirFaireFP(SavoirFaireFP savoirFaireFP) {
        String sql = "INSERT INTO " + NOM_TABLE + "(ID_FICHE_POSTE, ID_SAVOIR_FAIRE) VALUES(?,?)";
        jdbcTemplate.update(sql, new Object[] { savoirFaireFP.getIdFichePoste(), savoirFaireFP.getIdSavoirFaire() });
    }

}
