package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.StatutFP;
import nc.mairie.spring.dao.utils.SirhDao;

public class StatutFPDao extends SirhDao implements StatutFPDaoInterface {

	public static final String CHAMP_LIB_STATUT_FP = "LIB_STATUT_FP";

	public StatutFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_STATUT_FP";
		super.CHAMP_ID = "ID_STATUT_FP";
	}

	@Override
	public List<StatutFP> listerStatutFP() throws Exception {
		return super.getListe(StatutFP.class);
	}

	@Override
	public StatutFP chercherStatutFP(Integer idStatut) throws Exception {
		return super.chercherObject(StatutFP.class, idStatut);
	}
}
