package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DiplomeFEDao extends SirhDao implements DiplomeFEDaoInterface {

	public static final String CHAMP_ID_DIPLOME_GENERIQUE = "ID_DIPLOME_GENERIQUE";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";

	public DiplomeFEDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DIPLOME_FE";
	}

	@Override
	public void supprimerDiplomeFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? ";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi });
	}

	@Override
	public void supprimerDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? and "
				+ CHAMP_ID_DIPLOME_GENERIQUE + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idDiplome });
	}

	@Override
	public void creerDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_EMPLOI + "," + CHAMP_ID_DIPLOME_GENERIQUE
				+ ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idDiplome });
	}

	@Override
	public DiplomeFE chercherDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_DIPLOME_GENERIQUE + " = ? and "
				+ CHAMP_ID_FICHE_EMPLOI + "=?";
		DiplomeFE cadre = (DiplomeFE) jdbcTemplate.queryForObject(sql, new Object[] { idDiplome, idFicheEmploi },
				new BeanPropertyRowMapper<DiplomeFE>(DiplomeFE.class));
		return cadre;
	}

	@Override
	public ArrayList<DiplomeFE> listerDiplomeFEAvecDiplome(Integer idDiplome) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_DIPLOME_GENERIQUE + "=? ";

		ArrayList<DiplomeFE> liste = new ArrayList<DiplomeFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idDiplome });
		for (Map<String, Object> row : rows) {
			DiplomeFE a = new DiplomeFE();
			a.setIdDiplomeGenerique((Integer) row.get(CHAMP_ID_DIPLOME_GENERIQUE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<DiplomeFE> listerDiplomeFEAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<DiplomeFE> liste = new ArrayList<DiplomeFE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			DiplomeFE a = new DiplomeFE();
			a.setIdDiplomeGenerique((Integer) row.get(CHAMP_ID_DIPLOME_GENERIQUE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			liste.add(a);
		}

		return liste;
	}
}
