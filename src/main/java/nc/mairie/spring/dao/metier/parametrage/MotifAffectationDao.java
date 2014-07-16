package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.MotifAffectation;
import nc.mairie.spring.dao.SirhDao;

public class MotifAffectationDao extends SirhDao implements MotifAffectationDaoInterface {

	public static final String CHAMP_LIB_MOTIF_AFFECTATION = "LIB_MOTIF_AFFECTATION";

	public MotifAffectationDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MOTIF_AFFECTATION";
		super.CHAMP_ID = "ID_MOTIF_AFFECTATION";
	}

	@Override
	public List<MotifAffectation> listerMotifAffectation() throws Exception {
		return super.getListe(MotifAffectation.class);
	}

	@Override
	public MotifAffectation chercherMotifAffectation(Integer idMotifAffectation) throws Exception {
		return super.chercherObject(MotifAffectation.class, idMotifAffectation);
	}
}
