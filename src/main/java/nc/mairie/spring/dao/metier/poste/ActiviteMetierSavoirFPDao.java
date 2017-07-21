package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteMetierSavoirFP;
import nc.mairie.spring.dao.utils.SirhDao;

/**
 * Created by gael on 21/07/2017.
 */
public class ActiviteMetierSavoirFPDao extends SirhDao implements ActiviteMetierSavoirFPInterface {

    public ActiviteMetierSavoirFPDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "ACTIVITE_METIER_SAVOIR_FP";
    }

    @Override
    public void supprimerActiviteMetierSavoirFP(ActiviteMetierSavoirFP activiteMetierSavoirFP) {
        String sql = "DELETE FROM " + NOM_TABLE + " where ID_FICHE_POSTE=? AND ID_ACTIVITE_METIER=? AND ";
        if (activiteMetierSavoirFP.getIdSavoirFaire() == null) {
            sql += " ID_SAVOIR_FAIRE IS NULL";
            jdbcTemplate.update(sql, new Object[] { activiteMetierSavoirFP.getIdFichePoste(), activiteMetierSavoirFP.getIdActiviteMetier() });
        } else {
            sql += "ID_SAVOIR_FAIRE = ?";
            jdbcTemplate.update(sql, new Object[] { activiteMetierSavoirFP.getIdFichePoste(), activiteMetierSavoirFP.getIdActiviteMetier(), activiteMetierSavoirFP.getIdSavoirFaire() });
        }
    }

    @Override
    public void ajouterActiviteMetierSavoirFP(ActiviteMetierSavoirFP activiteMetierSavoirFP) {
        if (activiteMetierSavoirFP.getIdSavoirFaire() == null) {
            String sql = "INSERT INTO " + NOM_TABLE + "(ID_FICHE_POSTE, ID_ACTIVITE_METIER, ID_SAVOIR_FAIRE) VALUES(?,?, NULL)";
            jdbcTemplate.update(sql, new Object[] { activiteMetierSavoirFP.getIdFichePoste(), activiteMetierSavoirFP.getIdActiviteMetier() });
        } else {
            String sql = "INSERT INTO " + NOM_TABLE + "(ID_FICHE_POSTE, ID_ACTIVITE_METIER, ID_SAVOIR_FAIRE) VALUES(?,?,?)";
            jdbcTemplate.update(sql, new Object[] { activiteMetierSavoirFP.getIdFichePoste(), activiteMetierSavoirFP.getIdActiviteMetier(), activiteMetierSavoirFP.getIdSavoirFaire() });
        }
    }
}
