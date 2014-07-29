package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.dao.utils.SirhDao;

public class TitrePosteDao extends SirhDao implements TitrePosteDaoInterface {

	public static final String CHAMP_LIB_TITRE_POSTE = "LIB_TITRE_POSTE";

	public TitrePosteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TITRE_POSTE";
		super.CHAMP_ID = "ID_TITRE_POSTE";
	}

	@Override
	public ArrayList<TitrePoste> listerTitrePoste() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TITRE_POSTE;

		ArrayList<TitrePoste> liste = new ArrayList<TitrePoste>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TitrePoste a = new TitrePoste();
			a.setIdTitrePoste((Integer) row.get(CHAMP_ID));
			a.setLibTitrePoste((String) row.get(CHAMP_LIB_TITRE_POSTE));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public TitrePoste chercherTitrePoste(Integer idTitrePoste) throws Exception {
		return super.chercherObject(TitrePoste.class, idTitrePoste);
	}

	@Override
	public void creerTitrePoste(String libTitrePoste) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TITRE_POSTE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libTitrePoste.toUpperCase() });
	}

	@Override
	public void supprimerTitrePoste(Integer idTitrePoste) throws Exception {
		super.supprimerObject(idTitrePoste);
	}
}
