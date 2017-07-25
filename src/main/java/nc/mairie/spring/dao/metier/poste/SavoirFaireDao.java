package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.SavoirFaire;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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

    @Override
    public List<SavoirFaire> listerTousSavoirFaireGeneraux(FichePoste fp) {
        String sql = "SELECT DISTINCT SF.ID_SAVOIR_FAIRE, SF.NOM_SAVOIR_FAIRE, " +
                "CASE WHEN SF_FP.ID_SAVOIR_FAIRE IS NULL THEN '0' ELSE '1' END AS CHECKED  " +
                "FROM SAVOIR_FAIRE_FM SF_FM " +
                "JOIN SAVOIR_FAIRE SF ON SF.ID_SAVOIR_FAIRE = SF_FM.ID_SAVOIR_FAIRE " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = SF_FM.ID_FICHE_METIER " +
                "LEFT JOIN SAVOIR_FAIRE_FP SF_FP ON SF_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND SF_FP.ID_SAVOIR_FAIRE = SF.ID_SAVOIR_FAIRE " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY SF.NOM_SAVOIR_FAIRE";
        return jdbcTemplate.query(sql, new Object[]{fp.getIdFichePoste()}, new BeanPropertyRowMapper<>(SavoirFaire.class));
    }

    public List<SavoirFaire> listerTousSavoirFaireGeneraux(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire) {
        Integer idFichePoste = fp != null ? fp.getIdFichePoste() : null;
        String sql = "SELECT DISTINCT SF_FM.ID_SAVOIR_FAIRE, SF.NOM_SAVOIR_FAIRE, " +
                "                CASE WHEN SF_FP.ID_SAVOIR_FAIRE IS NULL THEN '0' ELSE '1' END AS CHECKED " +
                "                FROM SAVOIR_FAIRE_FM SF_FM " +
                "                JOIN FM_FP ON FM_FP.ID_FICHE_METIER = SF_FM.ID_FICHE_METIER " +
                "                JOIN SAVOIR_FAIRE SF ON SF.ID_SAVOIR_FAIRE = SF_FM.ID_SAVOIR_FAIRE " +
                "                LEFT JOIN SAVOIR_FAIRE_FP SF_FP ON SF_FP.ID_SAVOIR_FAIRE = SF.ID_SAVOIR_FAIRE AND (SF_FP.ID_FICHE_POSTE = ?) " +
                "                WHERE (FM_FP.ID_FICHE_POSTE = ? AND FM_FP.ID_FICHE_METIER IN(?, ?)) " +
                "                OR (FM_FP.ID_FICHE_METIER NOT IN (SELECT FM_FP.ID_FICHE_METIER FROM FM_FP WHERE FM_FP.ID_FICHE_POSTE = ?) AND FM_FP.ID_FICHE_METIER IN (?, ?)) " +
                "                ORDER BY SF.NOM_SAVOIR_FAIRE";
        return jdbcTemplate.query(sql, new Object[]{idFichePoste, idFichePoste, idFicheMetierPrimaire, idFicheMetierSecondaire, idFichePoste, idFicheMetierPrimaire, idFicheMetierSecondaire}, new BeanPropertyRowMapper<>(SavoirFaire.class));
    }

    public void supprimerTousSavoirFaireGeneraux(FichePoste fp) {
        String sql = "DELETE FROM SAVOIR_FAIRE_FP WHERE ID_FICHE_POSTE = ?";
        jdbcTemplate.update(sql, new Object[]{fp.getIdFichePoste()});
    }
}
