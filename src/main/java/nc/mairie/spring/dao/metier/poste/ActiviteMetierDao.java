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

    /**
     * Liste toutes les activités métier et savoir de la fiche de poste rattachés ou en instance de rattachement à
     * une fiche de poste.
     * Les activités et savoir faire sont rappatriés avec leur état coché, dans l'ordre de la fiche métier et
     * en commencant par la fiche primaire
     * @param fp
     * @param idFicheMetierPrimaire
     * @param idFicheMetierSecondaire
     * @return
     */
    public List<ActiviteMetier> listerToutesActiviteMetier(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        String sql = "SELECT DISTINCT AM_FM.ID_ACTIVITE_METIER, SF.ID_SAVOIR_FAIRE, AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE, " +
                "                CASE WHEN AMS_FP.ID_ACTIVITE_METIER IS NULL OR AMS_FP.ID_FICHE_POSTE <> ? THEN '0' ELSE '1' END AS ACT_CHECKED, " +
                "                CASE WHEN AMS_FP.ID_SAVOIR_FAIRE IS NULL OR AMS_FP.ID_FICHE_POSTE <> ? THEN '0' ELSE '1' END AS SF_CHECKED, " +
                "                CASE WHEN AM_FM.ID_FICHE_METIER = ? THEN '0' ELSE '1' END AS FM_ORDRE, " +
                "                AM_FM.ORDRE AS ACT_ORDRE, " +
                "                SF.ORDRE AS SF_ORDRE " +
                "                FROM ACTIVITE_METIER_FM AM_FM " +
                "                JOIN ACTIVITE_METIER AM ON AM.ID_ACTIVITE_METIER = AM_FM.ID_ACTIVITE_METIER " +
                "                LEFT JOIN SAVOIR_FAIRE SF ON SF.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER " +
                "                LEFT JOIN FM_FP ON FM_FP.ID_FICHE_METIER = AM_FM.ID_FICHE_METIER AND FM_FP.ID_FICHE_POSTE = ? " +
                "                LEFT JOIN ACTIVITE_METIER_SAVOIR_FP AMS_FP ON AMS_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND AMS_FP.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER AND (AMS_FP.ID_SAVOIR_FAIRE IS NULL OR AMS_FP.ID_SAVOIR_FAIRE = SF.ID_SAVOIR_FAIRE) " +
                "                WHERE AM_FM.ID_FICHE_METIER IN (?, ?) " +
                "                ORDER BY FM_ORDRE, ACT_ORDRE, SF_ORDRE";
        List<Map<String, Object>> mapsActiviteMetier = jdbcTemplate.queryForList(sql, idFichePoste, idFichePoste, idFicheMetierPrimaire, idFichePoste, idFicheMetierPrimaire, idFicheMetierSecondaire);
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

    /**
     * Même résultat que {listerToutesActiviteMetier} mais seulement avec les activités et savoir-faire cochés
     * @param fp
     * @param idFicheMetierPrimaire
     * @param idFicheMetierSecondaire
     * @return
     */
    public List<ActiviteMetier> listerToutesActiviteMetierChecked(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        List<ActiviteMetier> listeActiviteMetier = listerToutesActiviteMetier(fp, idFicheMetierPrimaire, idFicheMetierSecondaire);
        List<ActiviteMetier> listeActiviteMetierChecked = new ArrayList<>();
        for (int i = 0; i < listeActiviteMetier.size(); i++) {
            ActiviteMetier am = listeActiviteMetier.get(i);
            if (am.isChecked()) {
                listeActiviteMetierChecked.add(am);
                List<SavoirFaire> listeSavoirFaire = new ArrayList<>();
                for (int j = 0; j < am.getListSavoirFaire().size(); j++) {
                    SavoirFaire sf = am.getListSavoirFaire().get(j);
                    if (sf.getChecked()) {
                        listeSavoirFaire.add(sf);
                    }
                }
                am.setListSavoirFaire(listeSavoirFaire);
            }
        }
        return listeActiviteMetierChecked;
    }

    public void supprimerToutesActiviteMetier(FichePoste fp) {
        String sql = "DELETE FROM ACTIVITE_METIER_SAVOIR_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }
}
