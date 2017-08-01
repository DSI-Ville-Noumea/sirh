package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.SavoirFaire;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public class SavoirFaireDao extends SirhDao implements SavoirFaireInterface {

    public SavoirFaireDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "SAVOIR_FAIRE";
        super.CHAMP_ID = "ID_SAVOIR_FAIRE";
    }

    public List<SavoirFaire> listerTousSavoirFaireGeneraux(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        Integer idFMSecondaire = idFicheMetierSecondaire != null ? idFicheMetierSecondaire : idFicheMetierPrimaire;
        String sql = "SELECT ID_SAVOIR_FAIRE, NOM_SAVOIR_FAIRE, CHECKED, MIN(FM_ORDRE) AS SFR_ORDRE, MIN(SF_ORDRE) AS SF_ORDRE FROM " +
                "(SELECT DISTINCT SF_FM.ID_SAVOIR_FAIRE, SF.NOM_SAVOIR_FAIRE, FM_FP.FM_PRIMAIRE, " +
                "CASE WHEN SF_FP.ORDRE IS NULL THEN SF_FM.ORDRE ELSE SF_FP.ORDRE END AS SF_ORDRE, " +
                "CASE WHEN SF_FP.ID_SAVOIR_FAIRE IS NULL THEN '0' ELSE '1' END AS CHECKED, " +
                "CASE WHEN SF_FM.ID_FICHE_METIER = ? THEN '0' ELSE '1' END AS FM_ORDRE " +
                "FROM SAVOIR_FAIRE_FM SF_FM " +
                "LEFT JOIN FM_FP ON FM_FP.ID_FICHE_METIER = SF_FM.ID_FICHE_METIER AND FM_FP.ID_FICHE_POSTE = ? " +
                "JOIN SAVOIR_FAIRE SF ON SF.ID_SAVOIR_FAIRE = SF_FM.ID_SAVOIR_FAIRE " +
                "LEFT JOIN SAVOIR_FAIRE_FP SF_FP ON SF_FP.ID_SAVOIR_FAIRE = SF.ID_SAVOIR_FAIRE AND (SF_FP.ID_FICHE_POSTE = ?) " +
                "WHERE SF_FM.ID_FICHE_METIER IN (?, ?) " +
                "ORDER BY FM_ORDRE, SF_ORDRE) SFR " +
                "GROUP BY ID_SAVOIR_FAIRE, NOM_SAVOIR_FAIRE, CHECKED " +
                "ORDER BY SFR_ORDRE, SF_ORDRE";
        return jdbcTemplate.query(sql, new Object[]{idFicheMetierPrimaire, idFichePoste, idFichePoste, idFicheMetierPrimaire, idFMSecondaire}, new BeanPropertyRowMapper<>(SavoirFaire.class));
    }

    public void supprimerTousSavoirFaireGeneraux(FichePoste fp) {
        String sql = "DELETE FROM SAVOIR_FAIRE_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }

    public List<SavoirFaire> listerTousSavoirFaireGenerauxChecked(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        List<SavoirFaire> listSavoirFaire = listerTousSavoirFaireGeneraux(fp, idFicheMetierPrimaire, idFicheMetierSecondaire);
        List<SavoirFaire> listSavoirFaireChecked = new ArrayList<>();
        for (int i = 0; i < listSavoirFaire.size(); i++) {
            SavoirFaire sf = listSavoirFaire.get(i);
            if (sf.getChecked()) {
                listSavoirFaireChecked.add(sf);
            }
        }
        return listSavoirFaireChecked;
    }
}
