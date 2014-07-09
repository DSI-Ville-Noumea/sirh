package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.TitrePermis;
import nc.mairie.spring.dao.SirhDao;

public class TitrePermisDao extends SirhDao implements TitrePermisDaoInterface {

	public static final String CHAMP_LIB_PERMIS = "LIB_PERMIS";

	public TitrePermisDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_PERMIS";
		super.CHAMP_ID = "ID_PERMIS";
	}

	@Override
	public List<TitrePermis> listerTitrePermis() throws Exception {
		return super.getListe(TitrePermis.class);
	}

	@Override
	public TitrePermis chercherTitrePermis(Integer idTitrePermis) throws Exception {
		return super.chercherObject(TitrePermis.class, idTitrePermis);
	}

	@Override
	public void creerTitrePermis(String libelleTitre) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_PERMIS + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleTitre.toUpperCase() });
	}

	@Override
	public void modifierTitrePermis(Integer idTitre, String libelleTitre) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_PERMIS + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleTitre, idTitre });
	}

	@Override
	public void supprimerTitrePermis(Integer idTitrePermis) throws Exception {
		super.supprimerObject(idTitrePermis);
	}
}
