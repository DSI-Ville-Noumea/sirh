package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.poste.CadreEmploiFE;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.technique.MairieMessages;
import nc.mairie.technique.Transaction;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CadreEmploiDao extends SirhDao implements CadreEmploiDaoInterface {

	public static final String CHAMP_LIB_CADRE_EMPLOI = "LIB_CADRE_EMPLOI";

	public CadreEmploiDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_CADRE_EMPLOI";
		super.CHAMP_ID = "ID_CADRE_EMPLOI";
	}

	@Override
	public void creerCadreEmploi(String libelleCadreEmploi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_CADRE_EMPLOI + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleCadreEmploi.toUpperCase() });

	}

	@Override
	public void supprimerCadreEmploi(Integer idCadreEmploi) throws Exception {
		super.supprimerObject(idCadreEmploi);
	}

	@Override
	public ArrayList<CadreEmploi> listerCadreEmploi() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_CADRE_EMPLOI;

		ArrayList<CadreEmploi> listeCadreEmploi = new ArrayList<CadreEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			CadreEmploi centre = new CadreEmploi();
			centre.setIdCadreEmploi((Integer) row.get(CHAMP_ID));
			centre.setLibCadreEmploi((String) row.get(CHAMP_LIB_CADRE_EMPLOI));
			listeCadreEmploi.add(centre);
		}

		return listeCadreEmploi;
	}

	@Override
	public CadreEmploi chercherCadreEmploi(Integer idCadreEmploi) throws Exception {
		return super.chercherObject(CadreEmploi.class, idCadreEmploi);
	}

	@Override
	public CadreEmploi chercherCadreEmploiByLib(String libCadreEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_CADRE_EMPLOI + " = ? ";
		CadreEmploi cadre = (CadreEmploi) jdbcTemplate.queryForObject(sql, new Object[] { libCadreEmploi },
				new BeanPropertyRowMapper<CadreEmploi>(CadreEmploi.class));
		return cadre;
	}

	@Override
	public ArrayList<CadreEmploi> listerCadreEmploiAvecFicheEmploi(Transaction aTransaction, FicheEmploi ficheEmploi)
			throws Exception {
		// Test du paramètre FicheEmploi
		if (ficheEmploi == null || ficheEmploi.getIdFicheEmploi() == null) {
			aTransaction.declarerErreur(MairieMessages.getMessage("ERR003", "FicheEmploi"));
			return new ArrayList<CadreEmploi>();
		}

		// Recherche de tous les liens FicheEmploi / Cadre emploi
		ArrayList<CadreEmploiFE> liens = CadreEmploiFE.listerCadreEmploiFEAvecFicheEmploi(aTransaction,
				ficheEmploi.getIdFicheEmploi());
		if (aTransaction.isErreur())
			return new ArrayList<CadreEmploi>();

		// Construction de la liste
		ArrayList<CadreEmploi> result = new ArrayList<CadreEmploi>();
		for (int i = 0; i < liens.size(); i++) {
			CadreEmploiFE aLien = (CadreEmploiFE) liens.get(i);
			CadreEmploi cadreE = chercherCadreEmploi(Integer.valueOf(aLien.getIdCadreEmploi()));
			if (aTransaction.isErreur())
				return new ArrayList<CadreEmploi>();
			result.add(cadreE);
		}

		return result;
	}
}
