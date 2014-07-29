package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeContratDao extends SirhDao implements TypeContratDaoInterface {

	public static final String CHAMP_LIB_TYPE_CONTRAT = "LIB_TYPE_CONTRAT";

	public TypeContratDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_CONTRAT";
		super.CHAMP_ID = "ID_TYPE_CONTRAT";
	}

	@Override
	public List<TypeContrat> listerTypeContrat() throws Exception {
		return super.getListe(TypeContrat.class);
	}

	@Override
	public TypeContrat chercherTypeContrat(Integer idTypeContrat) throws Exception {
		return super.chercherObject(TypeContrat.class, idTypeContrat);
	}
}
