package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGenerale;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.SavoirFaire;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public class ActiviteGeneraleDao extends SirhDao implements ActiviteGeneraleInterface {

    public ActiviteGeneraleDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "ACTIVITE_GENERALE";
        super.CHAMP_ID = "ID_ACTIVITE_GENERALE";
    }

    public List<ActiviteGenerale> listerToutesActiviteGenerale(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        Integer idFMSecondaire = idFicheMetierSecondaire != null ? idFicheMetierSecondaire : idFicheMetierPrimaire;
        // #45020 : Le premier SUM() permet de prendre l'ordre de la FEV primaire 
        // Le 2e SUM() permet de prendre l'ordre de la FEV secondaire
        String sql = "SELECT ID_ACTIVITE_GENERALE, NOM_ACTIVITE_GENERALE, CHECKED, MIN(FM_ORDRE) AS AGR_ORDRE, " + 
        		"SUM(CASE WHEN FM_PRIMAIRE = 1 THEN AG_ORDRE ELSE 0 END) AS AG_ORDRE_F1, " +
        		"SUM(CASE WHEN FM_PRIMAIRE is null OR FM_PRIMAIRE = 0 THEN AG_ORDRE ELSE 0 END) AS AG_ORDRE_F2 FROM " +
                "(SELECT DISTINCT AG_FM.ID_ACTIVITE_GENERALE, AG.NOM_ACTIVITE_GENERALE, FM_FP.FM_PRIMAIRE, " +
                "AG_FM.ORDRE AS AG_ORDRE, " +
                "CASE WHEN AG_FP.ID_ACTIVITE_GENERALE IS NULL THEN '0' ELSE '1' END AS CHECKED, " +
                "CASE WHEN AG_FM.ID_FICHE_METIER = ? THEN '0' ELSE '1' END AS FM_ORDRE " +
                "FROM ACTIVITE_GENERALE_FM AG_FM " +
                "LEFT JOIN FM_FP ON FM_FP.ID_FICHE_METIER = AG_FM.ID_FICHE_METIER AND FM_FP.ID_FICHE_POSTE = ? " +
                "JOIN ACTIVITE_GENERALE AG ON AG.ID_ACTIVITE_GENERALE = AG_FM.ID_ACTIVITE_GENERALE " +
                "LEFT JOIN ACTIVITE_GENERALE_FP AG_FP ON AG_FP.ID_ACTIVITE_GENERALE = AG.ID_ACTIVITE_GENERALE AND (AG_FP.ID_FICHE_POSTE = ?) " +
                "WHERE AG_FM.ID_FICHE_METIER IN (?, ?) " +
                "ORDER BY FM_ORDRE, AG_ORDRE) AGR " +
                "GROUP BY ID_ACTIVITE_GENERALE, NOM_ACTIVITE_GENERALE, CHECKED " +
                "ORDER BY AGR_ORDRE, AG_ORDRE_F1, AG_ORDRE_F2";
        return jdbcTemplate.query(sql, new Object[]{idFicheMetierPrimaire, idFichePoste, idFichePoste, idFicheMetierPrimaire, idFMSecondaire}, new BeanPropertyRowMapper<>(ActiviteGenerale.class));
    }

    public void supprimerToutesActiviteGenerale(FichePoste fp) {
        String sql = "DELETE FROM ACTIVITE_GENERALE_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }

    public List<ActiviteGenerale> listerToutesActiviteGeneraleChecked(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        List<ActiviteGenerale> listActiviteGenerale = listerToutesActiviteGenerale(fp, idFicheMetierPrimaire, idFicheMetierSecondaire);
        List<ActiviteGenerale> listActiviteGeneraleChecked = new ArrayList<>();
        for (int i = 0; i < listActiviteGenerale.size(); i++) {
            ActiviteGenerale ag = listActiviteGenerale.get(i);
            if (ag.getChecked()) {
                listActiviteGeneraleChecked.add(ag);
            }
        }
        return listActiviteGeneraleChecked;
    }
}
