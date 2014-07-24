package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.CategorieFE;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CategorieFEDao extends SirhDao implements CategorieFEDaoInterface {

	public static final String CHAMP_ID_CATEGORIE_STATUT = "ID_CATEGORIE_STATUT";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public CategorieFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "CATEGORIE_FE";
	}

	@Override
	public ArrayList<CategorieFE> listerCategorieFEAvecCategorie(Integer idCategorie) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CATEGORIE_STATUT + "=? ";

		ArrayList<CategorieFE> liste = new ArrayList<CategorieFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCategorie });
		for (Map<String, Object> row : rows) {
			CategorieFE a = new CategorieFE();
			a.setIdCategorieStatut((Integer) row.get(CHAMP_ID_CATEGORIE_STATUT));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerCategorieFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}

	@Override
	public void supprimerCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? and "
				+ CHAMP_ID_CATEGORIE_STATUT + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idCategorie });
	}

	@Override
	public void creerCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_EMPLOI + "," + CHAMP_ID_CATEGORIE_STATUT + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idCategorie });
	}

	@Override
	public CategorieFE chercherCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CATEGORIE_STATUT + " = ? and "
				+ CHAMP_ID_FICHE_EMPLOI + "=?";
		CategorieFE cadre = (CategorieFE) jdbcTemplate.queryForObject(sql, new Object[] { idCategorie, idFicheEmploi },
				new BeanPropertyRowMapper<CategorieFE>(CategorieFE.class));
		return cadre;
	}

	@Override
	public ArrayList<CategorieFE> listerCategorieFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<CategorieFE> liste = new ArrayList<CategorieFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			CategorieFE a = new CategorieFE();
			a.setIdCategorieStatut((Integer) row.get(CHAMP_ID_CATEGORIE_STATUT));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public List<CategorieFE> listerCategorieFE() throws Exception {
		return super.getListe(CategorieFE.class);
	}
}
