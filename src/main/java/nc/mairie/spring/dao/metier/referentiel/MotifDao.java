package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.Motif;
import nc.mairie.spring.dao.utils.SirhDao;

public class MotifDao extends SirhDao implements MotifDaoInterface {

	public static final String CHAMP_LIB_MOTIF = "LIB_MOTIF";

	public MotifDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_MOTIF";
		super.CHAMP_ID = "ID_MOTIF";
	}

	@Override
	public List<Motif> listerMotif() throws Exception {
		return super.getListe(Motif.class);
	}
}
