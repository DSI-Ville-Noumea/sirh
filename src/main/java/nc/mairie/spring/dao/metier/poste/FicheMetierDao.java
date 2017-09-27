package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FMFP;
import nc.mairie.metier.poste.FicheMetier;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

/**
 * Created by gael on 29/06/2017.
 */
public class FicheMetierDao extends SirhDao implements FicheMetierDaoInterface {

    public static final String CHAMP_ID_FICHE_METIER = "ID_FICHE_METIER";
    public static final String CHAMP_ID_DOMAINE_FM = "ID_DOMAINE_FM";
    public static final String CHAMP_ID_FAMILLE_METIER = "ID_FAMILLE_METIER";
    public static final String CHAMP_REF_MAIRIE = "REF_MAIRIE";
    public static final String CHAMP_NOM_METIER = "NOM_METIER";
    public static final String CHAMP_DEFINITION_METIER = "DEFINITION_METIER";
    public static final String CHAMP_CADRE_STATUTAIRE = "CADRE_STATUTAIRE";

    public FicheMetierDao(SirhDao sirhDao) {
        super.dataSource = sirhDao.getDataSource();
        super.jdbcTemplate = sirhDao.getJdbcTemplate();
        super.NOM_TABLE = "FICHE_METIER";
    }

    @Override
    public FicheMetier chercherFicheMetierAvecFichePoste(FMFP lien) {
        String sql = "SELECT * FROM " + NOM_TABLE + " WHERE " + CHAMP_ID_FICHE_METIER + " = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{lien.getIdFicheMetier()},
                    new BeanPropertyRowMapper<>(FicheMetier.class));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public FicheMetier chercherFicheMetierAvecFichePoste(Integer idFichePoste, boolean fmPrimaire) {
        String sql = "SELECT FM.* FROM FICHE_METIER FM " +
                "JOIN FM_FP ON FM_FP.ID_FICHE_METIER = FM.ID_FICHE_METIER " +
                "WHERE FM_FP.ID_FICHE_POSTE = ? AND FM_FP.FM_PRIMAIRE = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{idFichePoste, fmPrimaire},
                    new BeanPropertyRowMapper<>(FicheMetier.class));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public List<FicheMetier> listerFicheMetierAvecRefMairieOuLibelle(String keyword) {
        String searchParam = keyword == null? "" : keyword.toUpperCase();
        String sql = "select * from " + NOM_TABLE + " where char(" + CHAMP_REF_MAIRIE + ") like ?  or upper(" + CHAMP_NOM_METIER + ") like ? order by " + CHAMP_NOM_METIER;
        return jdbcTemplate.query(sql, new Object[] { searchParam + "%", "%" + searchParam + "%" }, new BeanPropertyRowMapper<>(FicheMetier.class));
    }
}
