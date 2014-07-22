package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.EtatServiceMilitaire;
import nc.mairie.spring.dao.SirhDao;

public class EtatServiceMilitaireDao extends SirhDao implements EtatServiceMilitaireDaoInterface {

	public static final String CHAMP_CODE_ETAT_SERVICE = "CODE_ETAT_SERVICE";
	public static final String CHAMP_LIB_ETAT_SERVICE = "LIB_ETAT_SERVICE";

	public EtatServiceMilitaireDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_ETAT_SERVICE_MILITAIRE";
		super.CHAMP_ID = "ID_ETAT_SERVICE";
	}

	@Override
	public List<EtatServiceMilitaire> listerEtatServiceMilitaire() throws Exception {
		return super.getListe(EtatServiceMilitaire.class);
	}
}
