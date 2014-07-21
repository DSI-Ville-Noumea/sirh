package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.MaladiePro;
import nc.mairie.spring.dao.SirhDao;

public class MaladieProDao extends SirhDao implements MaladieProDaoInterface {

	public static final String CHAMP_CODE_MALADIE_PRO = "CODE_MALADIE_PRO";
	public static final String CHAMP_LIB_MALADIE_PRO = "LIB_MALADIE_PRO";

	public MaladieProDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MALADIE_PRO";
		super.CHAMP_ID = "ID_MALADIE_PRO";
	}

	@Override
	public ArrayList<MaladiePro> listerMaladiePro() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_MALADIE_PRO;

		ArrayList<MaladiePro> liste = new ArrayList<MaladiePro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MaladiePro a = new MaladiePro();
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
