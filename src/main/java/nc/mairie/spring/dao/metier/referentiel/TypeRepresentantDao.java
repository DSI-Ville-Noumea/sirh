package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.TypeRepresentant;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeRepresentantDao extends SirhDao implements TypeRepresentantDaoInterface {

	public static final String CHAMP_LIB_TYPE_REPRESENTANT = "LIB_TYPE_REPRESENTANT";

	public TypeRepresentantDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_REPRESENTANT";
		super.CHAMP_ID = "ID_TYPE_REPRESENTANT";
	}

	@Override
	public List<TypeRepresentant> listerTypeRepresentant() throws Exception {
		return super.getListe(TypeRepresentant.class);
	}

	@Override
	public TypeRepresentant chercherTypeRepresentant(Integer idTypeRepresentant) throws Exception {
		return super.chercherObject(TypeRepresentant.class, idTypeRepresentant);
	}
}
