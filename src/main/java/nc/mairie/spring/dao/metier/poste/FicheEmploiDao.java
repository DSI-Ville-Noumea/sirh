package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class FicheEmploiDao extends SirhDao implements FicheEmploiDaoInterface {

	public static final String CHAMP_ID_DOMAINE_FE = "ID_DOMAINE_FE";
	public static final String CHAMP_ID_FAMILLE_EMPLOI = "ID_FAMILLE_EMPLOI";
	public static final String CHAMP_REF_MAIRIE = "REF_MAIRIE";
	public static final String CHAMP_NOM_METIER_EMPLOI = "NOM_METIER_EMPLOI";
	public static final String CHAMP_PRECISIONS_DIPLOMES = "PRECISIONS_DIPLOMES";
	public static final String CHAMP_LIEN_HIERARCHIQUE = "LIEN_HIERARCHIQUE";
	public static final String CHAMP_DEFINITION_EMPLOI = "DEFINITION_EMPLOI";
	public static final String CHAMP_ID_CODE_ROME = "ID_CODE_ROME";

	public FicheEmploiDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "FICHE_EMPLOI";
		super.CHAMP_ID = "ID_FICHE_EMPLOI";
	}

	@Override
	public void supprimerFicheEmploi(Integer idFicheEmploi) throws Exception {
		super.supprimerObject(idFicheEmploi);
	}

	@Override
	public void modifierFicheEmploi(Integer idFicheEmploi, Integer idDomaineFe, Integer idFamilleEmploi,
			String refMairie, String nomMetierEmploi, String precisionsDiplomes, String lienHierarchique,
			String definitionEmploi, Integer idCodeRome) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_DOMAINE_FE + "=?," + CHAMP_ID_FAMILLE_EMPLOI + "=?,"
				+ CHAMP_REF_MAIRIE + "=?," + CHAMP_NOM_METIER_EMPLOI + "=?," + CHAMP_PRECISIONS_DIPLOMES + "=?,"
				+ CHAMP_LIEN_HIERARCHIQUE + "=?," + CHAMP_DEFINITION_EMPLOI + "=?," + CHAMP_ID_CODE_ROME + "=? where "
				+ CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idDomaineFe, idFamilleEmploi, refMairie.toUpperCase(), nomMetierEmploi,
				precisionsDiplomes, lienHierarchique, definitionEmploi, idCodeRome, idFicheEmploi });
	}

	@Override
	public Integer creerFicheEmploi(Integer idDomaineFe, Integer idFamilleEmploi, String refMairie,
			String nomMetierEmploi, String precisionsDiplomes, String lienHierarchique, String definitionEmploi,
			Integer idCodeRome) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_DOMAINE_FE
				+ "," + CHAMP_ID_FAMILLE_EMPLOI + "," + CHAMP_REF_MAIRIE + "," + CHAMP_NOM_METIER_EMPLOI + ","
				+ CHAMP_PRECISIONS_DIPLOMES + "," + CHAMP_LIEN_HIERARCHIQUE + "," + CHAMP_DEFINITION_EMPLOI + ","
				+ CHAMP_ID_CODE_ROME + ") " + "VALUES (?,?,?,?,?,?,?,?))";
		Integer id = jdbcTemplate.queryForObject(sql,
				new Object[] { idDomaineFe, idFamilleEmploi, refMairie.toUpperCase(), nomMetierEmploi,
						precisionsDiplomes, lienHierarchique, definitionEmploi, idCodeRome }, Integer.class);
		return id;
	}

	@Override
	public Integer genererNumChrono(String prefixe) throws Exception {
		String sql = "select max(" + CHAMP_REF_MAIRIE + ") from " + NOM_TABLE + " where " + CHAMP_REF_MAIRIE
				+ " like ? ";
		try {
			String cadre = (String) jdbcTemplate.queryForObject(sql, new Object[] { prefixe.toUpperCase() + "%" },
					String.class);
			return (Integer.valueOf(cadre.substring(5)) + 1);
		} catch (Exception e) {
			return 0;
		}

	}

	@Override
	public FicheEmploi chercherFicheEmploiAvecRefMairie(String refMairie) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_REF_MAIRIE + " = ? ";
		FicheEmploi cadre = (FicheEmploi) jdbcTemplate.queryForObject(sql, new Object[] { refMairie.toUpperCase() },
				new BeanPropertyRowMapper<FicheEmploi>(FicheEmploi.class));
		return cadre;
	}

	@Override
	public FicheEmploi chercherFicheEmploi(Integer idFicheEmploi) throws Exception {
		return super.chercherObject(FicheEmploi.class, idFicheEmploi);
	}

	@Override
	public FicheEmploi chercherFicheEmploiAvecFichePoste(boolean emploiPrimaire, ArrayList<FEFP> liens)
			throws Exception {
		// recherche de l'emploi primaire ou secondaire
		for (int i = 0; i < liens.size(); i++) {
			FEFP aLien = (FEFP) liens.get(i);
			if (aLien.isFePrimaire() == emploiPrimaire) {
				try {
					FicheEmploi fe = chercherFicheEmploi(aLien.getIdFicheEmploi());
					return fe;
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiAvecDiplome(ArrayList<DiplomeFE> liens) throws Exception {
		// Construction de la liste
		ArrayList<FicheEmploi> listeFicheEmploi = new ArrayList<FicheEmploi>();
		for (int i = 0; i < liens.size(); i++) {
			DiplomeFE lien = (DiplomeFE) liens.get(i);
			try {
				FicheEmploi fe = chercherFicheEmploi(lien.getIdFicheEmploi());
				listeFicheEmploi.add(fe);
			} catch (Exception e) {
				return listeFicheEmploi;
			}
		}
		return listeFicheEmploi;
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiAvecFamilleEmploi(Integer idFamilleEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_FAMILLE_EMPLOI + "=? ";

		ArrayList<FicheEmploi> liste = new ArrayList<FicheEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idFamilleEmploi });
		for (Map<String, Object> row : rows) {
			FicheEmploi a = new FicheEmploi();
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID));
			a.setIdDomaineFe((Integer) row.get(CHAMP_ID_DOMAINE_FE));
			a.setIdFamilleEmploi((Integer) row.get(CHAMP_ID_FAMILLE_EMPLOI));
			a.setRefMairie((String) row.get(CHAMP_REF_MAIRIE));
			a.setNomMetierEmploi((String) row.get(CHAMP_NOM_METIER_EMPLOI));
			a.setLienHierarchique((String) row.get(CHAMP_LIEN_HIERARCHIQUE));
			a.setDefinitionEmploi((String) row.get(CHAMP_DEFINITION_EMPLOI));
			a.setPrecisionsDiplomes((String) row.get(CHAMP_PRECISIONS_DIPLOMES));
			a.setIdCodeRome((Integer) row.get(CHAMP_ID_CODE_ROME));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiAvecDomaineEmploi(Integer idDomaineEmploi) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_DOMAINE_FE + "=? ";

		ArrayList<FicheEmploi> liste = new ArrayList<FicheEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idDomaineEmploi });
		for (Map<String, Object> row : rows) {
			FicheEmploi a = new FicheEmploi();
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID));
			a.setIdDomaineFe((Integer) row.get(CHAMP_ID_DOMAINE_FE));
			a.setIdFamilleEmploi((Integer) row.get(CHAMP_ID_FAMILLE_EMPLOI));
			a.setRefMairie((String) row.get(CHAMP_REF_MAIRIE));
			a.setNomMetierEmploi((String) row.get(CHAMP_NOM_METIER_EMPLOI));
			a.setLienHierarchique((String) row.get(CHAMP_LIEN_HIERARCHIQUE));
			a.setDefinitionEmploi((String) row.get(CHAMP_DEFINITION_EMPLOI));
			a.setPrecisionsDiplomes((String) row.get(CHAMP_PRECISIONS_DIPLOMES));
			a.setIdCodeRome((Integer) row.get(CHAMP_ID_CODE_ROME));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiavecRefMairie(String refMairie) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where char(" + CHAMP_REF_MAIRIE + ") like ? order by "
				+ CHAMP_NOM_METIER_EMPLOI;

		ArrayList<FicheEmploi> liste = new ArrayList<FicheEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { refMairie.toUpperCase() + "%" });
		for (Map<String, Object> row : rows) {
			FicheEmploi a = new FicheEmploi();
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID));
			a.setIdDomaineFe((Integer) row.get(CHAMP_ID_DOMAINE_FE));
			a.setIdFamilleEmploi((Integer) row.get(CHAMP_ID_FAMILLE_EMPLOI));
			a.setRefMairie((String) row.get(CHAMP_REF_MAIRIE));
			a.setNomMetierEmploi((String) row.get(CHAMP_NOM_METIER_EMPLOI));
			a.setLienHierarchique((String) row.get(CHAMP_LIEN_HIERARCHIQUE));
			a.setDefinitionEmploi((String) row.get(CHAMP_DEFINITION_EMPLOI));
			a.setPrecisionsDiplomes((String) row.get(CHAMP_PRECISIONS_DIPLOMES));
			a.setIdCodeRome((Integer) row.get(CHAMP_ID_CODE_ROME));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public List<FicheEmploi> listerFicheEmploi() throws Exception {
		return super.getListe(FicheEmploi.class);
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiAvecCodeRome(Integer idCodeRome) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CODE_ROME + "= ? ";

		ArrayList<FicheEmploi> liste = new ArrayList<FicheEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idCodeRome });
		for (Map<String, Object> row : rows) {
			FicheEmploi a = new FicheEmploi();
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID));
			a.setIdDomaineFe((Integer) row.get(CHAMP_ID_DOMAINE_FE));
			a.setIdFamilleEmploi((Integer) row.get(CHAMP_ID_FAMILLE_EMPLOI));
			a.setRefMairie((String) row.get(CHAMP_REF_MAIRIE));
			a.setNomMetierEmploi((String) row.get(CHAMP_NOM_METIER_EMPLOI));
			a.setLienHierarchique((String) row.get(CHAMP_LIEN_HIERARCHIQUE));
			a.setDefinitionEmploi((String) row.get(CHAMP_DEFINITION_EMPLOI));
			a.setPrecisionsDiplomes((String) row.get(CHAMP_PRECISIONS_DIPLOMES));
			a.setIdCodeRome((Integer) row.get(CHAMP_ID_CODE_ROME));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public ArrayList<FicheEmploi> listerFicheEmploiAvecCriteresAvances(Integer idDomaineEmploi, Integer idFamEmploi,
			String codeRome, String refMairie, String nomMetierEmploi) throws Exception {
		String sql = "select distinct fe.* from " + NOM_TABLE + " fe left outer join P_CODE_ROME codeRome on fe."
				+ CHAMP_ID_CODE_ROME + " = codeRome.id_code_rome ";

		int indice = 0;
		if (idDomaineEmploi != null) {
			if (indice == 0) {
				sql += " where fe." + CHAMP_ID_DOMAINE_FE + " = " + idDomaineEmploi;
				indice = 1;
			} else {
				sql += " and fe." + CHAMP_ID_DOMAINE_FE + " = " + idDomaineEmploi;
			}
		}
		if (idFamEmploi != null) {
			if (indice == 0) {
				sql += " where fe." + CHAMP_ID_FAMILLE_EMPLOI + " = " + idFamEmploi;
				indice = 1;
			} else {
				sql += " and fe." + CHAMP_ID_FAMILLE_EMPLOI + " = " + idFamEmploi;
			}
		}
		if (codeRome != null) {
			if (indice == 0) {
				sql += " where codeRome.LIB_CODE_ROME like '" + codeRome + "%' ";
				indice = 1;
			} else {
				sql += " and codeRome.LIB_CODE_ROME like '" + codeRome + "%' ";
			}
		}
		if (refMairie != null) {
			if (indice == 0) {
				sql += " where fe." + CHAMP_REF_MAIRIE + " like '" + refMairie + "%' ";
				indice = 1;
			} else {
				sql += " and fe." + CHAMP_REF_MAIRIE + " like '" + refMairie + "%' ";
			}
		}

		if (nomMetierEmploi != null) {
			if (indice == 0) {
				sql += " where UPPER(fe." + CHAMP_NOM_METIER_EMPLOI + ") like ' "
						+ nomMetierEmploi.replaceAll("'", "''").toUpperCase() + "%' ";
				indice = 1;
			} else {
				sql += " and UPPER(fe." + CHAMP_NOM_METIER_EMPLOI + ") like '"
						+ nomMetierEmploi.replaceAll("'", "''").toUpperCase() + "%' ";
			}
		}

		sql += " ORDER BY fe." + CHAMP_REF_MAIRIE;

		ArrayList<FicheEmploi> liste = new ArrayList<FicheEmploi>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			FicheEmploi a = new FicheEmploi();
			a.setIdFicheEmploi((Integer) row.get(CHAMP_ID));
			a.setIdDomaineFe((Integer) row.get(CHAMP_ID_DOMAINE_FE));
			a.setIdFamilleEmploi((Integer) row.get(CHAMP_ID_FAMILLE_EMPLOI));
			a.setRefMairie((String) row.get(CHAMP_REF_MAIRIE));
			a.setNomMetierEmploi((String) row.get(CHAMP_NOM_METIER_EMPLOI));
			a.setLienHierarchique((String) row.get(CHAMP_LIEN_HIERARCHIQUE));
			a.setDefinitionEmploi((String) row.get(CHAMP_DEFINITION_EMPLOI));
			a.setPrecisionsDiplomes((String) row.get(CHAMP_PRECISIONS_DIPLOMES));
			a.setIdCodeRome((Integer) row.get(CHAMP_ID_CODE_ROME));
			liste.add(a);
		}

		return liste;
	}
}
