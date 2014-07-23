package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.commun.TypeContact;
import nc.mairie.spring.dao.SirhDao;

public class TypeContactDao extends SirhDao implements TypeContactDaoInterface {

	public static final String CHAMP_LIBELLE = "LIBELLE";

	public TypeContactDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_CONTACT";
		super.CHAMP_ID = "ID_TYPE_CONTACT";
	}

	@Override
	public ArrayList<TypeContact> listerTypeContact() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by  " + CHAMP_ID;

		ArrayList<TypeContact> liste = new ArrayList<TypeContact>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeContact a = new TypeContact();
			a.setIdTypeContact((Integer) row.get(CHAMP_ID));
			a.setLibelle((String) row.get(CHAMP_LIBELLE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public TypeContact chercherTypeContact(Integer idTypeContact) throws Exception {
		return super.chercherObject(TypeContact.class, idTypeContact);
	}
}
