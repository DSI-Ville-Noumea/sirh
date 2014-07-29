package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.Collectivite;
import nc.mairie.spring.dao.utils.SirhDao;

public class CollectiviteDao extends SirhDao implements CollectiviteDaoInterface {

	public static final String CHAMP_CODE_COLLECTIVITE = "CODE_COLLECTIVITE";
	public static final String CHAMP_LIB_COURT_COLLECTIVITE = "LIB_COURT_COLLECTIVITE";
	public static final String CHAMP_LIB_LONG_COLLECTIVITE = "LIB_LONG_COLLECTIVITE";

	public CollectiviteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_COLLECTIVITE";
		super.CHAMP_ID = "ID_COLLECTIVITE";
	}

	@Override
	public List<Collectivite> listerCollectivite() throws Exception {
		return super.getListe(Collectivite.class);
	}
}
