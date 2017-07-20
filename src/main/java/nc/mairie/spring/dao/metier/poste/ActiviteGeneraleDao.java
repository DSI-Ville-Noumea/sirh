package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGenerale;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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

    @Override
    public List<ActiviteGenerale> listerToutesActiviteGenerale(FichePoste fp) {
        String sql = "SELECT DISTINCT AG.ID_ACTIVITE_GENERALE, AG.NOM_ACTIVITE_GENERALE, " +
                "CASE WHEN AG_FP.ID_ACTIVITE_GENERALE IS NULL THEN '0' ELSE '1' END AS CHECKED " +
                "FROM ACTIVITE_GENERALE_FM AG_FM " +
                "JOIN ACTIVITE_GENERALE AG ON AG.ID_ACTIVITE_GENERALE = AG_FM.ID_ACTIVITE_GENERALE " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = AG_FM.ID_FICHE_METIER " +
                "LEFT JOIN ACTIVITE_GENERALE_FP AG_FP ON AG_FP.ID_FICHE_POSTE = FM_FP.ID_FICHE_POSTE AND AG_FP.ID_ACTIVITE_GENERALE = AG.ID_ACTIVITE_GENERALE " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? " +
                "ORDER BY AG.NOM_ACTIVITE_GENERALE";
        return jdbcTemplate.query(sql, new Object[]{fp.getIdFichePoste()}, new BeanPropertyRowMapper<>(ActiviteGenerale.class));
    }
}
