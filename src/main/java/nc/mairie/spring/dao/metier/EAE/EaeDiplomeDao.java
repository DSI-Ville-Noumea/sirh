package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeDiplome;
import nc.mairie.spring.dao.EaeDao;

public class EaeDiplomeDao extends EaeDao implements EaeDiplomeDaoInterface {

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_LIBELLE_DIPLOME = "LIBELLE_DIPLOME";

	public EaeDiplomeDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_DIPLOME";
		super.CHAMP_ID = "ID_EAE_DIPLOME";
	}

	@Override
	public void creerEaeDiplome(Integer idEae, String libDiplome) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_LIBELLE_DIPLOME + ") "
				+ "VALUES (?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, libDiplome });
	}

	@Override
	public ArrayList<EaeDiplome> listerEaeDiplome(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeDiplome> listeEaeDiplome = new ArrayList<EaeDiplome>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeDiplome dip = new EaeDiplome();
			dip.setIdEaeDiplome((Integer) row.get(CHAMP_ID));
			dip.setIdEae((Integer) row.get(CHAMP_ID_EAE));
			dip.setLibelleDiplome((String) row.get(CHAMP_LIBELLE_DIPLOME));

			listeEaeDiplome.add(dip);
		}
		return listeEaeDiplome;
	}

	@Override
	public void supprimerEaeDiplome(Integer idDiplome) throws Exception {
		super.supprimerObject(idDiplome);
	}
}
