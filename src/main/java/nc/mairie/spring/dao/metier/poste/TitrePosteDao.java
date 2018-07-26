package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.dao.utils.SirhDao;

public class TitrePosteDao extends SirhDao implements TitrePosteDaoInterface {

	public static final String CHAMP_LIB_TITRE_POSTE = "LIB_TITRE_POSTE";
	public static final String CHAMP_LIB_TITRE_COURT = "LIB_TITRE_COURT";
	public static final String CHAMP_LIB_TITRE_LONG = "LIB_TITRE_LONG";

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
			a.setLibTitreCourt((String) row.get(CHAMP_LIB_TITRE_COURT));
			a.setLibTitreLong((String) row.get(CHAMP_LIB_TITRE_LONG));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public TitrePoste chercherTitrePoste(Integer idTitrePoste) throws Exception {
		return super.chercherObject(TitrePoste.class, idTitrePoste);
	}

	@Override
	public void creerTitrePoste(TitrePoste titrePoste) throws Exception {
		if (titrePoste == null)
			return;
		String titreCourt = StringUtils.isNotEmpty(titrePoste.getLibTitreCourt()) ? titrePoste.getLibTitreCourt().toUpperCase() : null;
		String titreLong = StringUtils.isNotEmpty(titrePoste.getLibTitreLong()) ? titrePoste.getLibTitreLong().toUpperCase() : null;
		
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TITRE_POSTE + ", " + CHAMP_LIB_TITRE_COURT + ", " + CHAMP_LIB_TITRE_LONG + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { titrePoste.getLibTitrePoste().toUpperCase(), titreCourt, titreLong  });
	}

	@Override
	public void modifierTitrePoste(TitrePoste titrePoste) throws Exception {
		if (titrePoste == null)
			return;
		
		String titreCourt = StringUtils.isNotEmpty(titrePoste.getLibTitreCourt()) ? titrePoste.getLibTitreCourt().toUpperCase() : null;
		String titreLong = StringUtils.isNotEmpty(titrePoste.getLibTitreLong()) ? titrePoste.getLibTitreLong().toUpperCase() : null;
		
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_TITRE_COURT + "=?," + CHAMP_LIB_TITRE_LONG + "=? where " + CHAMP_ID + " =?";
		
		jdbcTemplate.update(sql, new Object[] { titreCourt, titreLong, titrePoste.getIdTitrePoste()  });
	}

	@Override
	public void supprimerTitrePoste(Integer idTitrePoste) throws Exception {
		super.supprimerObject(idTitrePoste);
	}
}
