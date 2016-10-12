package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.Inaptitude;
import nc.mairie.spring.dao.utils.SirhDao;

public class InaptitudeDao extends SirhDao implements InaptitudeDaoInterface {

	public static final String	CHAMP_ID_VISITE				= "ID_VISITE";
	public static final String	CHAMP_ID_TYPE_INAPTITUDE	= "ID_TYPE_INAPTITUDE";
	public static final String	CHAMP_DATE_DEBUT_INAPTITUDE	= "DATE_DEBUT_INAPTITUDE";
	public static final String	CHAMP_DUREE_ANNEE			= "DUREE_ANNEE";
	public static final String	CHAMP_DUREE_MOIS			= "DUREE_MOIS";
	public static final String	CHAMP_DUREE_JOUR			= "DUREE_JOUR";

	public InaptitudeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "INAPTITUDE";
		super.CHAMP_ID = "ID_INAPTITUDE";
	}

	@Override
	public ArrayList<Inaptitude> listerInaptitudeAvecTypeInaptitude(Integer idTypeInaptitude) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_INAPTITUDE + "=?";

		ArrayList<Inaptitude> liste = new ArrayList<Inaptitude>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeInaptitude });
		for (Map<String, Object> row : rows) {
			Inaptitude a = new Inaptitude();
			a.setIdInaptitude((Integer) row.get(CHAMP_ID));
			a.setIdVisite((Integer) row.get(CHAMP_ID_VISITE));
			a.setIdTypeInaptitude((Integer) row.get(CHAMP_ID_TYPE_INAPTITUDE));
			a.setDateDebutInaptitude((Date) row.get(CHAMP_DATE_DEBUT_INAPTITUDE));
			a.setDureeAnnee((Integer) row.get(CHAMP_DUREE_ANNEE));
			a.setDureeMois((Integer) row.get(CHAMP_DUREE_JOUR));
			a.setDureeJour((Integer) row.get(CHAMP_DUREE_JOUR));
			liste.add(a);
		}

		return liste;
	}

}
