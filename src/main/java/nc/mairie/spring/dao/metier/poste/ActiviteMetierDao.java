package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteMetier;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.SavoirFaire;
import nc.mairie.spring.dao.utils.SirhDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gael on 18/07/2017.
 */
public class ActiviteMetierDao extends SirhDao implements ActiviteMetierDaoInterface {

    public static final String CHAMP_NOM_ACTIVITE_METIER = "NOM_ACTIVITE_METIER";

    public ActiviteMetierDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "ACTIVITE_METIER";
        super.CHAMP_ID = "ID_ACTIVITE_METIER";
    }

    public List<ActiviteMetier> listerToutesActiviteMetier(FichePoste fp) {
        String sql = "SELECT DISTINCT AM_FM.ID_ACTIVITE_METIER, SF.ID_SAVOIR_FAIRE, AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE, " +
                "CASE WHEN AMS_FP.ID_ACTIVITE_METIER IS NULL THEN '0' ELSE '1' END AS ACT_CHECKED, " +
                "CASE WHEN AMS_FP2.ID_SAVOIR_FAIRE IS NULL THEN '0' ELSE '1' END AS SF_CHECKED " +
                "FROM ACTIVITE_METIER_FM AM_FM " +
                "JOIN ACTIVITE_METIER AM ON AM.ID_ACTIVITE_METIER = AM_FM.ID_ACTIVITE_METIER " +
                "LEFT JOIN SAVOIR_FAIRE SF ON SF.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = AM_FM.ID_FICHE_METIER " +
                "LEFT JOIN ACTIVITE_METIER_SAVOIR_FP AMS_FP ON AMS_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND AMS_FP.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER " +
                "LEFT JOIN ACTIVITE_METIER_SAVOIR_FP AMS_FP2 ON AMS_FP2.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND AMS_FP2.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER AND AMS_FP2.ID_SAVOIR_FAIRE = SF.ID_SAVOIR_FAIRE " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE";
        List<Map<String, Object>> mapsActiviteMetier = jdbcTemplate.queryForList(sql, fp.getIdFichePoste());
        List<ActiviteMetier> listeActiviteMetier = new ArrayList<>();
        for (Map<String, Object> am : mapsActiviteMetier) {
            ActiviteMetier activiteMetier = new ActiviteMetier();
            activiteMetier.setIdActiviteMetier((Integer)am.get("ID_ACTIVITE_METIER"));
            activiteMetier.setNomActiviteMetier(am.get("NOM_ACTIVITE_METIER").toString());
            activiteMetier.setChecked(am.get("ACT_CHECKED").equals("1"));
            if (!listeActiviteMetier.contains(activiteMetier)) {
                listeActiviteMetier.add(activiteMetier);
            } else {
                activiteMetier = listeActiviteMetier.get(listeActiviteMetier.indexOf(activiteMetier));
            }
            if (am.get("ID_SAVOIR_FAIRE") != null) {
                SavoirFaire savoirFaire = new SavoirFaire();
                savoirFaire.setIdActiviteMetier(activiteMetier.getIdActiviteMetier());
                savoirFaire.setIdSavoirFaire((Integer)am.get("ID_SAVOIR_FAIRE"));
                savoirFaire.setNomSavoirFaire(am.get("NOM_SAVOIR_FAIRE").toString());
                savoirFaire.setChecked(am.get("SF_CHECKED").equals("1"));
                activiteMetier.addToListSavoirFaire(savoirFaire);
            }
        }
        return listeActiviteMetier;
    }
}
