package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.DomaineEmploi;
import nc.mairie.spring.dao.SirhDao;

public class DomaineEmploiDao extends SirhDao implements DomaineEmploiDaoInterface {

	public static final String CHAMP_CODE_DOMAINE_FE = "CODE_DOMAINE_FE";
	public static final String CHAMP_LIB_DOMAINE_FE = "LIB_DOMAINE_FE";

	public DomaineEmploiDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_DOMAINE_FE";
		super.CHAMP_ID = "ID_DOMAINE_FE";
	}

	@Override
	public void creerDomaineEmploi(String libelleDomaineEmploi, String codeDomaineEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_DOMAINE_FE + "," + CHAMP_CODE_DOMAINE_FE
				+ ") VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleDomaineEmploi.toUpperCase(), codeDomaineEmploi.toUpperCase() });

	}

	@Override
	public void supprimerDomaineEmploi(Integer idDomaineEmploi) throws Exception {
		super.supprimerObject(idDomaineEmploi);

	}

	@Override
	public ArrayList<DomaineEmploi> listerDomaineEmploi() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_DOMAINE_FE;

		ArrayList<DomaineEmploi> listeDom = new ArrayList<DomaineEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			DomaineEmploi dom = new DomaineEmploi();
			dom.setIdDomaineEmploi((Integer) row.get(CHAMP_ID));
			dom.setLibDomaineEmploi((String) row.get(CHAMP_LIB_DOMAINE_FE));
			dom.setCodeDomaineEmploi((String) row.get(CHAMP_CODE_DOMAINE_FE));

			listeDom.add(dom);
		}

		return listeDom;
	}

	@Override
	public DomaineEmploi chercherDomaineEmploi(Integer idDomaineEmploi) throws Exception {
		return super.chercherObject(DomaineEmploi.class, idDomaineEmploi);
	}
}
