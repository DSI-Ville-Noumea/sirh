package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.spring.dao.utils.SirhDao;

public class SituationFamilialeDao extends SirhDao implements SituationFamilialeDaoInterface {

	public static final String CHAMP_CODE_SITUATION = "CODE_SITUATION";
	public static final String CHAMP_LIB_SITUATION = "LIB_SITUATION";

	public SituationFamilialeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_SITUATION_FAMILIALE";
		super.CHAMP_ID = "ID_SITUATION";
	}

	@Override
	public SituationFamiliale chercherSituationFamilialeById(Integer idSituation) throws Exception {
		return super.chercherObject(SituationFamiliale.class, idSituation);
	}

	@Override
	public List<SituationFamiliale> listerSituationFamiliale() throws Exception {
		return super.getListe(SituationFamiliale.class);
	}
}
