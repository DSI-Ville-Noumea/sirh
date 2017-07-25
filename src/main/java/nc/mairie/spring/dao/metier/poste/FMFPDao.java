package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FMFP;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gael on 29/06/2017.
 */
public class FMFPDao extends SirhDao implements FMFPDaoInterface {

    public static final String CHAMP_ID_FICHE_METIER = "ID_FICHE_METIER";

    public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";

    public static final String CHAMP_FM_PRIMAIRE = "FM_PRIMAIRE";

    public FMFPDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "FM_FP";
    }

    @Override
    public FMFP chercherFMFPAvecNumFP(Integer idFicheMetier, boolean metierPrimaire) throws Exception {
        String sql = "SELECT * FROM " + NOM_TABLE + " WHERE " + CHAMP_ID_FICHE_POSTE + " = ? AND " + CHAMP_FM_PRIMAIRE + " = ?";
        List<FMFP> fmfps = jdbcTemplate.query(sql, new Object[]{idFicheMetier, metierPrimaire ? 1 : 0},
                new BeanPropertyRowMapper<FMFP>(FMFP.class));
        return fmfps.isEmpty() ? null : fmfps.get(0);
    }

    @Override
    public void creerFMFP(Integer idFicheMetier, Integer idFichePoste, boolean metierPrimaire) {
        String sql = "INSERT INTO " + NOM_TABLE + " (ID_FICHE_METIER, ID_FICHE_POSTE, FM_PRIMAIRE) VALUES(?,?,?)";
        jdbcTemplate.update(sql, new Object[] { idFicheMetier, idFichePoste, metierPrimaire });
    }

    public void supprimerFMFP(Integer idFicheMetier, Integer idFichePoste, boolean metierPrimaire) {
        String sql = "DELETE FROM " + NOM_TABLE + " WHERE ID_FICHE_METIER = ? AND ID_FICHE_POSTE = ? AND FM_PRIMAIRE = ?";
        jdbcTemplate.update(sql, new Object[] { idFicheMetier, idFichePoste, metierPrimaire });
    }


}
