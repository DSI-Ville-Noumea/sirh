package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.AccueilKiosque;
import nc.mairie.spring.dao.utils.SirhDao;

public class AccueilKiosqueDao extends SirhDao implements AccueilKiosqueDaoInterface {

	public static final String CHAMP_TEXTE_ACCUEIL_KIOSQUE = "TEXTE_ACCUEIL_KIOSQUE";

	public AccueilKiosqueDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_ACCUEIL_KIOSQUE";
		super.CHAMP_ID = "ID_ACCUEIL_KIOSQUE";
	}

	@Override
	public void creerAccueilKiosque(String texte) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_TEXTE_ACCUEIL_KIOSQUE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { texte });
	}

	@Override
	public void supprimerAccueilKiosque(Integer idAccueil) throws Exception {
		super.supprimerObject(idAccueil);
	}

	@Override
	public void modifierAccueilKiosque(Integer idAccueil, String texte) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TEXTE_ACCUEIL_KIOSQUE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { texte, idAccueil });
	}

	@Override
	public List<AccueilKiosque> getAccueilKiosque() throws Exception {
		return super.getListe(AccueilKiosque.class);
	}
}
