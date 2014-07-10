package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeDeveloppement;
import nc.mairie.spring.dao.EaeDao;

public class EaeDeveloppementDao extends EaeDao implements EaeDeveloppementDaoInterface {

	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_LIBELLE = "LIBELLE";
	public static final String CHAMP_ECHEANCE = "ECHEANCE";
	public static final String CHAMP_PRIORISATION = "PRIORISATION";
	public static final String CHAMP_TYPE_DEVELOPPEMENT = "TYPE_DEVELOPPEMENT";

	public EaeDeveloppementDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_DEVELOPPEMENT";
		super.CHAMP_ID = "ID_EAE_DEVELOPPEMENT";
	}

	@Override
	public void modifierEaeDeveloppement(Integer idEaeDeveloppement, String typeDeveloppement,
			String libelleDeveloppement, Date echeanceDeveloppement, Integer priorisation) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TYPE_DEVELOPPEMENT + " =?," + CHAMP_LIBELLE + "=?,"
				+ CHAMP_ECHEANCE + "=?," + CHAMP_PRIORISATION + "=? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { typeDeveloppement, libelleDeveloppement, echeanceDeveloppement,
				priorisation, idEaeDeveloppement });
	}

	@Override
	public void creerEaeDeveloppement(Integer idEaeEvolution, String typeDeveloppement, String libelleDeveloppement,
			Date echeanceDeveloppement, Integer priorisation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_EVOLUTION + "," + CHAMP_LIBELLE + ","
				+ CHAMP_ECHEANCE + "," + CHAMP_PRIORISATION + "," + CHAMP_TYPE_DEVELOPPEMENT + ") "
				+ "VALUES (?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeEvolution, libelleDeveloppement, echeanceDeveloppement,
				priorisation, typeDeveloppement });
	}

	@Override
	public ArrayList<EaeDeveloppement> listerEaeDeveloppementParEvolution(Integer idEvolution) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_EVOLUTION + "=? order by "
				+ CHAMP_PRIORISATION;

		ArrayList<EaeDeveloppement> listeEaeDeveloppement = new ArrayList<EaeDeveloppement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEvolution });
		for (Map<String, Object> row : rows) {
			EaeDeveloppement dev = new EaeDeveloppement();
			dev.setIdEaeDeveloppement((Integer) row.get(CHAMP_ID));
			dev.setIdEaeEvolution((Integer) row.get(CHAMP_ID_EAE_EVOLUTION));
			dev.setLibelle((String) row.get(CHAMP_LIBELLE));
			dev.setEcheance((Date) row.get(CHAMP_ECHEANCE));
			dev.setPriorisation((Integer) row.get(CHAMP_PRIORISATION));
			dev.setTypeDeveloppement((String) row.get(CHAMP_TYPE_DEVELOPPEMENT));

			listeEaeDeveloppement.add(dev);
		}
		return listeEaeDeveloppement;
	}

	@Override
	public void supprimerEaeDeveloppement(Integer idEaeDeveloppement) throws Exception {
		super.supprimerObject(idEaeDeveloppement);
	}
}
