package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.SpecialiteDiplome;
import nc.mairie.spring.dao.SirhDao;

public class SpecialiteDiplomeDao extends SirhDao implements SpecialiteDiplomeDaoInterface {

	public static final String CHAMP_LIB_SPECIALITE_DIPLOME = "LIB_SPECIALITE_DIPLOME";

	public SpecialiteDiplomeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_SPECIALITE_DIPLOME";
		super.CHAMP_ID = "ID_SPECIALITE_DIPLOME";
	}

	@Override
	public void creerSpecialiteDiplome(String libelleSpecialiteDiplome) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_SPECIALITE_DIPLOME + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleSpecialiteDiplome.toUpperCase() });
	}

	@Override
	public void supprimerSpecialiteDiplome(Integer idSpecialiteDiplome) throws Exception {
		super.supprimerObject(idSpecialiteDiplome);
	}

	@Override
	public ArrayList<SpecialiteDiplome> listerSpecialiteDiplome() throws Exception {
		String sql = "select *  from " + NOM_TABLE + " order by " + CHAMP_LIB_SPECIALITE_DIPLOME;

		ArrayList<SpecialiteDiplome> listeSpe = new ArrayList<SpecialiteDiplome>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			SpecialiteDiplome s = new SpecialiteDiplome();
			s.setIdSpecialiteDiplome((Integer) row.get(CHAMP_ID));
			s.setLibSpecialiteDiplome((String) row.get(CHAMP_LIB_SPECIALITE_DIPLOME));
			listeSpe.add(s);
		}

		return listeSpe;
	}

	@Override
	public SpecialiteDiplome chercherSpecialiteDiplome(Integer idSpecialiteDiplome) throws Exception {
		return super.chercherObject(SpecialiteDiplome.class, idSpecialiteDiplome);
	}
}
