package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.FamilleEmploi;
import nc.mairie.spring.dao.SirhDao;

public class FamilleEmploiDao extends SirhDao implements FamilleEmploiDaoInterface {

	public static final String CHAMP_CODE_FAMILLE_EMPLOI = "CODE_FAMILLE_EMPLOI";
	public static final String CHAMP_LIB_FAMILLE_EMPLOI = "LIB_FAMILLE_EMPLOI";

	public FamilleEmploiDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_FAMILLE_EMPLOI";
		super.CHAMP_ID = "ID_FAMILLE_EMPLOI";
	}

	@Override
	public void creerFamilleEmploi(String libelleFamilleEmploi, String codeFamilleEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_FAMILLE_EMPLOI + "," + CHAMP_CODE_FAMILLE_EMPLOI
				+ ") VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleFamilleEmploi.toUpperCase(), codeFamilleEmploi.toUpperCase() });

	}

	@Override
	public void supprimerFamilleEmploi(Integer idFamilleEmploi) throws Exception {
		super.supprimerObject(idFamilleEmploi);
	}

	@Override
	public ArrayList<FamilleEmploi> listerFamilleEmploi() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_CODE_FAMILLE_EMPLOI;

		ArrayList<FamilleEmploi> listeFam = new ArrayList<FamilleEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			FamilleEmploi fam = new FamilleEmploi();
			fam.setIdFamilleEmploi((Integer) row.get(CHAMP_ID));
			fam.setLibFamilleEmploi((String) row.get(CHAMP_LIB_FAMILLE_EMPLOI));
			fam.setCodeFamilleEmploi((String) row.get(CHAMP_CODE_FAMILLE_EMPLOI));

			listeFam.add(fam);
		}

		return listeFam;
	}

	@Override
	public FamilleEmploi chercherFamilleEmploi(Integer idFamilleEmploi) throws Exception {
		return super.chercherObject(FamilleEmploi.class, idFamilleEmploi);
	}
}
