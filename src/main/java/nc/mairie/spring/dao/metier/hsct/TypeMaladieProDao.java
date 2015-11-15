package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.TypeMaladiePro;
import nc.mairie.spring.dao.utils.SirhDao;

public class TypeMaladieProDao extends SirhDao implements TypeMaladieProDaoInterface {

	public static final String CHAMP_CODE_MALADIE_PRO = "CODE_MALADIE_PRO";
	public static final String CHAMP_LIB_MALADIE_PRO = "LIB_MALADIE_PRO";

	public TypeMaladieProDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MALADIE_PRO";
		super.CHAMP_ID = "ID_MALADIE_PRO";
	}

	@Override
	public ArrayList<TypeMaladiePro> listerMaladiePro() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MALADIE_PRO;

		ArrayList<TypeMaladiePro> liste = new ArrayList<TypeMaladiePro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeMaladiePro a = new TypeMaladiePro();
			a.setIdMaladiePro((Integer) row.get(CHAMP_ID));
			a.setCodeMaladiePro((String) row.get(CHAMP_CODE_MALADIE_PRO));
			a.setLibMaladiePro((String) row.get(CHAMP_LIB_MALADIE_PRO));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void creerMaladiePro(String codeMaladiePro, String libMaladiePro) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_MALADIE_PRO + "," + CHAMP_LIB_MALADIE_PRO
				+ ") VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { codeMaladiePro.toUpperCase(), libMaladiePro.toUpperCase() });
	}

	@Override
	public void supprimerMaladiePro(Integer idMaladiePro) throws Exception {
		super.supprimerObject(idMaladiePro);
	}
}
