package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FMFP;
import nc.mairie.metier.poste.FicheMetier;
import nc.mairie.spring.dao.utils.SirhDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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
    public FicheMetier chercherFicheMetierAvecFichePoste(FMFP lien) throws Exception {
        String sql = "SELECT * FROM " + NOM_TABLE + " WHERE " + CHAMP_ID_FICHE_METIER  + " = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { lien.getIdFicheMetier() },
                new BeanPropertyRowMapper<FicheMetier>(FicheMetier.class));
    }

}
