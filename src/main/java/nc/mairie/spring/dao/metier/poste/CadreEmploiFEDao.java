package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.CadreEmploiFE;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CadreEmploiFEDao extends SirhDao implements CadreEmploiFEDaoInterface {

	public static final String CHAMP_ID_CADRE_EMPLOI = "ID_CADRE_EMPLOI";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public CadreEmploiFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "CADRE_EMPLOI_FE";
	}

	@Override
	public ArrayList<CadreEmploiFE> listerCadreEmploiFEAvecFicheEmploi(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<CadreEmploiFE> liste = new ArrayList<CadreEmploiFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			CadreEmploiFE a = new CadreEmploiFE();
			a.setIdCadreEmploi((Integer) row.get(CHAMP_ID_CADRE_EMPLOI));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public CadreEmploiFE chercherCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CADRE_EMPLOI + " = ? and "
				+ CHAMP_ID_FICHE_EMPLOI + "=?";
		CadreEmploiFE cadre = (CadreEmploiFE) jdbcTemplate.queryForObject(sql, new Object[] { idCadreEmploi,
				idFicheEmploi }, new BeanPropertyRowMapper<CadreEmploiFE>(CadreEmploiFE.class));
		return cadre;
	}

	@Override
	public void creerCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_EMPLOI + "," + CHAMP_ID_CADRE_EMPLOI + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idCadreEmploi });
	}

	@Override
	public void supprimerCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? and "
				+ CHAMP_ID_CADRE_EMPLOI + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idCadreEmploi });
	}

	@Override
	public void supprimerCadreEmploiFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? ";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}
}
