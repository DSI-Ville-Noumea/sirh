package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.JourFerie;
import nc.mairie.spring.dao.SirhDao;

public class JourFerieDao extends SirhDao implements JourFerieDaoInterface {

	public static final String CHAMP_ID_TYPE_JOUR_FERIE = "ID_TYPE_JOUR_FERIE";
	public static final String CHAMP_DATE_JOUR = "DATE_JOUR";
	public static final String CHAMP_DESCRIPTION = "DESCRIPTION";

	public JourFerieDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_JOUR_FERIE";
		super.CHAMP_ID = "ID_JOUR_FERIE";
	}

	@Override
	public ArrayList<String> listerAnnee() {
		String sql = "select distinct(year(date_jour)) as annee from " + NOM_TABLE + " order by annee desc WITH UR";

		ArrayList<String> listeAnnee = new ArrayList<String>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			listeAnnee.add(row.get("annee").toString());
		}

		return listeAnnee;
	}

	@Override
	public ArrayList<JourFerie> listerJourByAnnee(String annee) {
		String sql = "select * from " + NOM_TABLE + " where year(" + CHAMP_DATE_JOUR + ")=? order by "
				+ CHAMP_DATE_JOUR + " asc";

		ArrayList<JourFerie> listeJourFerie = new ArrayList<JourFerie>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee });
		for (Map<String, Object> row : rows) {
			JourFerie jour = new JourFerie();
			jour.setIdJourFerie((Integer) row.get(CHAMP_ID));
			jour.setIdTypeJour((Integer) row.get(CHAMP_ID_TYPE_JOUR_FERIE));
			jour.setDateJour((Date) row.get(CHAMP_DATE_JOUR));
			jour.setDescription((String) row.get(CHAMP_DESCRIPTION));
			listeJourFerie.add(jour);
		}

		return listeJourFerie;
	}

	@Override
	public void supprimerJourFerie(Integer idJourFerie) throws Exception {
		super.supprimerObject(idJourFerie);
	}

	@Override
	public void modifierJourFerie(Integer idJourFerie, Integer idTypeJour, Date dateJour, String description) {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_JOUR_FERIE + "=?," + CHAMP_DATE_JOUR + "=?,"
				+ CHAMP_DESCRIPTION + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idTypeJour, dateJour, description, idJourFerie });
	}

	@Override
	public void creerJourFerie(Integer idTypeJour, Date dateJour, String description) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TYPE_JOUR_FERIE + "," + CHAMP_DATE_JOUR + ","
				+ CHAMP_DESCRIPTION + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idTypeJour, dateJour, description });
	}

	@Override
	public ArrayList<JourFerie> listerJourByAnneeWithType(String annee, Integer idTypeJour) {
		String sql = "select * from " + NOM_TABLE + " where year(" + CHAMP_DATE_JOUR + ")=? and "
				+ CHAMP_ID_TYPE_JOUR_FERIE + "=?";

		ArrayList<JourFerie> listeJourFerie = new ArrayList<JourFerie>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { annee, idTypeJour });
		for (Map<String, Object> row : rows) {
			JourFerie jour = new JourFerie();
			jour.setIdJourFerie((Integer) row.get(CHAMP_ID));
			jour.setIdTypeJour((Integer) row.get(CHAMP_ID_TYPE_JOUR_FERIE));
			jour.setDateJour((Date) row.get(CHAMP_DATE_JOUR));
			jour.setDescription((String) row.get(CHAMP_DESCRIPTION));
			listeJourFerie.add(jour);
		}

		return listeJourFerie;
	}

}
