package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.FEFP;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class FEFPDao extends SirhDao implements FEFPDaoInterface {

	public static final String CHAMP_ID_FICHE_POSTE = "ID_FICHE_POSTE";
	public static final String CHAMP_ID_FICHE_EMPLOI = "ID_FICHE_EMPLOI";
	public static final String CHAMP_FE_PRIMAIRE = "FE_PRIMAIRE";

	public FEFPDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "FE_FP";
	}

	@Override
	public FEFP chercherFEFPAvecNumFPPrimaire(Integer idFichePoste, boolean emploiPrimaire) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + " = ? and " + CHAMP_FE_PRIMAIRE
				+ "=?";
		FEFP cadre = (FEFP) jdbcTemplate.queryForObject(sql, new Object[] { idFichePoste, emploiPrimaire ? 1 : 0 },
				new BeanPropertyRowMapper<FEFP>(FEFP.class));
		return cadre;
	}

	@Override
	public void supprimerFEFP(Integer idFicheEmploi, Integer idFichePoste, boolean emploiPrimaire) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_FICHE_EMPLOI + "=? and " + CHAMP_ID_FICHE_POSTE
				+ "=? and " + CHAMP_FE_PRIMAIRE + "=?";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idFichePoste, emploiPrimaire ? 1 : 0 });
	}

	@Override
	public void creerFEFP(Integer idFicheEmploi, Integer idFichePoste, boolean emploiPrimaire) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_FICHE_EMPLOI + "," + CHAMP_ID_FICHE_POSTE + ","
				+ CHAMP_FE_PRIMAIRE + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idFicheEmploi, idFichePoste, emploiPrimaire ? 1 : 0 });
	}

	@Override
	public ArrayList<FEFP> listerFEFPAvecFE(Integer idFicheEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_EMPLOI + "=? ";

		ArrayList<FEFP> liste = new ArrayList<FEFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFicheEmploi });
		for (Map<String, Object> row : rows) {
			FEFP a = new FEFP();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			Integer primaire = (Integer) row.get(CHAMP_FE_PRIMAIRE);
			a.setFePrimaire(primaire == 1 ? true : false);
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FEFP> listerFEFPAvecFP(Integer idFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FICHE_POSTE + "=? ";

		ArrayList<FEFP> liste = new ArrayList<FEFP>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFichePoste });
		for (Map<String, Object> row : rows) {
			FEFP a = new FEFP();
			a.setIdFichePoste((Integer) row.get(CHAMP_ID_FICHE_POSTE));
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID_FICHE_EMPLOI));
			Integer primaire = (Integer) row.get(CHAMP_FE_PRIMAIRE);
			a.setFePrimaire(primaire == 1 ? true : false);
			liste.add(a);
		}

		return liste;
	}
}
