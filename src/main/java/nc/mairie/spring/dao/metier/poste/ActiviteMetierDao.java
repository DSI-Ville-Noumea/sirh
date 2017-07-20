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
        //Liste des activites depuis la fiche de poste
        String sql = "SELECT AMS_FP.ID_ACTIVITE_METIER, SF.ID_SAVOIR_FAIRE, AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE FROM ACTIVITE_METIER_SAVOIR_FP AMS_FP " +
                "JOIN ACTIVITE_METIER AM ON AM.ID_ACTIVITE_METIER = AMS_FP.ID_ACTIVITE_METIER " +
                "JOIN SAVOIR_FAIRE SF ON SF.ID_SAVOIR_FAIRE = AMS_FP.ID_SAVOIR_FAIRE " +
                "WHERE AMS_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE";
        List<Map<String, Object>> activitesFromFichePoste = jdbcTemplate.queryForList(sql, fp.getIdFichePoste());
        //Liste des activités depuis la fiche métier
        sql = "SELECT DISTINCT AM_FM.ID_ACTIVITE_METIER, SF.ID_SAVOIR_FAIRE, AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE FROM ACTIVITE_METIER_FM AM_FM " +
                "JOIN ACTIVITE_METIER AM ON AM.ID_ACTIVITE_METIER = AM_FM.ID_ACTIVITE_METIER " +
                "JOIN SAVOIR_FAIRE SF ON SF.ID_ACTIVITE_METIER = AM.ID_ACTIVITE_METIER " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = AM_FM.ID_FICHE_METIER " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY AM.NOM_ACTIVITE_METIER, SF.NOM_SAVOIR_FAIRE";
        //Ajout des activités métiers manquantes dans les activités de la fiche de poste
        List<Map<String, Object>> activitesFromFicheMetier = jdbcTemplate.queryForList(sql, fp.getIdFichePoste());
        for (int i = 0; i < activitesFromFicheMetier.size(); i++) {
            Map<String, Object> activiteFromFM = activitesFromFicheMetier.get(i);
            if (!activitesFromFichePoste.contains(activitesFromFicheMetier.get(i))) {
                //Rajout d'un attribut checked pour dire qu'il n'est pas coché
                activiteFromFM.put("unchecked", true);
                activitesFromFichePoste.add(activiteFromFM);
            }
        }
        //Création des POJOs ActiviteMetier et des Savoirs faire liés
        List<ActiviteMetier> listeActiviteMetier = new ArrayList<>();
        for (int i = 0; i < activitesFromFichePoste.size(); i++) {
            Map<String, Object> activiteFromFP = activitesFromFichePoste.get(i);
            ActiviteMetier activiteMetier = new ActiviteMetier();
            activiteMetier.setIdActiviteMetier((Integer)activiteFromFP.get("ID_ACTIVITE_METIER"));
            activiteMetier.setNomActiviteMetier(activiteFromFP.get("NOM_ACTIVITE_METIER").toString());
            //activiteMetier.setChecked(!activiteFromFP.containsKey("unchecked"));
            if (!listeActiviteMetier.contains(activiteMetier)) {
                listeActiviteMetier.add(activiteMetier);
            } else {
                activiteMetier = listeActiviteMetier.get(listeActiviteMetier.indexOf(activiteMetier));
            }
            if (activiteFromFP.get("ID_SAVOIR_FAIRE") != null) {
                SavoirFaire savoirFaire = new SavoirFaire();
                savoirFaire.setIdActiviteMetier(activiteMetier.getIdActiviteMetier());
                savoirFaire.setIdSavoirFaire((Integer)activiteFromFP.get("ID_SAVOIR_FAIRE"));
                savoirFaire.setNomSavoirFaire(activiteFromFP.get("NOM_SAVOIR_FAIRE").toString());
                savoirFaire.setChecked(!activiteFromFP.containsKey("unchecked"));
                activiteMetier.addToListSavoirFaire(savoirFaire);
            }
        }

        return listeActiviteMetier;
    }
}
