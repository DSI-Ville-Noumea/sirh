package nc.mairie.spring.dao.metier.agent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.Enfant;
import nc.mairie.metier.agent.LienEnfantAgent;
import nc.mairie.spring.dao.utils.SirhDao;

public class EnfantDao extends SirhDao implements EnfantDaoInterface {

	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";
	public static final String CHAMP_NOM = "NOM";
	public static final String CHAMP_PRENOM = "PRENOM";
	public static final String CHAMP_SEXE = "SEXE";
	public static final String CHAMP_DATE_NAISSANCE = "DATE_NAISSANCE";
	public static final String CHAMP_CODE_PAYS_NAISS_ET = "CODE_PAYS_NAISS_ET";
	public static final String CHAMP_CODE_COMMUNE_NAISS_ET = "CODE_COMMUNE_NAISS_ET";
	public static final String CHAMP_CODE_COMMUNE_NAISS_FR = "CODE_COMMUNE_NAISS_FR";
	public static final String CHAMP_DATE_DECES = "DATE_DECES";
	public static final String CHAMP_NATIONALITE = "NATIONALITE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";

	public EnfantDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ENFANT";
		super.CHAMP_ID = "ID_ENFANT";
	}

	@Override
	public Integer creerEnfant(Integer idDocument, String nom, String prenom, String sexe, Date dateNaissance,
			Integer codePaysNaissEt, Integer codeCommuneNaissEt, Integer codeCommuneNaissFr, Date dateDeces,
			String nationalite, String commentaire) throws Exception {
		String sql = "select " + CHAMP_ID + " from NEW TABLE (INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_DOCUMENT
				+ "," + CHAMP_NOM + "," + CHAMP_PRENOM + "," + CHAMP_SEXE + "," + CHAMP_DATE_NAISSANCE + ","
				+ CHAMP_CODE_PAYS_NAISS_ET + "," + CHAMP_CODE_COMMUNE_NAISS_ET + "," + CHAMP_CODE_COMMUNE_NAISS_FR
				+ "," + CHAMP_DATE_DECES + "," + CHAMP_NATIONALITE + "," + CHAMP_COMMENTAIRE
				+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?))";

		Integer id = jdbcTemplate.queryForObject(sql, new Object[] { idDocument, nom, prenom, sexe, dateNaissance,
				codePaysNaissEt, codeCommuneNaissEt, codeCommuneNaissFr, dateDeces, nationalite, commentaire },
				Integer.class);
		return id;
	}

	@Override
	public void modifierEnfant(Integer idEnfant, Integer idDocument, String nom, String prenom, String sexe,
			Date dateNaissance, Integer codePaysNaissEt, Integer codeCommuneNaissEt, Integer codeCommuneNaissFr,
			Date dateDeces, String nationalite, String commentaire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_DOCUMENT + "=?," + CHAMP_NOM + "=?," + CHAMP_PRENOM
				+ "=?," + CHAMP_SEXE + "=?," + CHAMP_DATE_NAISSANCE + "=?," + CHAMP_CODE_PAYS_NAISS_ET + "=?,"
				+ CHAMP_CODE_COMMUNE_NAISS_ET + "=?," + CHAMP_CODE_COMMUNE_NAISS_FR + "=?," + CHAMP_DATE_DECES + "=?,"
				+ CHAMP_NATIONALITE + "=?," + CHAMP_COMMENTAIRE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idDocument, nom, prenom, sexe, dateNaissance, codePaysNaissEt,
				codeCommuneNaissEt, codeCommuneNaissFr, dateDeces, nationalite, commentaire, idEnfant });
	}

	@Override
	public void supprimerEnfant(Integer idEnfant, LienEnfantAgentDao lienEnfantDao) throws Exception {
		// Recherche des liens de l'enfant
		ArrayList<LienEnfantAgent> aListeLien = lienEnfantDao.listerLienEnfantAgentAvecEnfant(idEnfant);

		// Parcours des liens pour suppression
		for (int i = 0; i < aListeLien.size(); i++) {
			LienEnfantAgent aLien = (LienEnfantAgent) aListeLien.get(i);
			lienEnfantDao.supprimerLienEnfantAgent(aLien.getIdAgent(), aLien.getIdEnfant());
		}
		// Suppression de l'Enfant
		super.supprimerObject(idEnfant);
	}

	@Override
	public ArrayList<Enfant> listerEnfantAgent(Integer idAgent, LienEnfantAgentDao lienEnfantDao) throws Exception {
		// Recherche de tous les liens Agent/ Enfant
		ArrayList<LienEnfantAgent> liens = lienEnfantDao.listerLienEnfantAgentAvecAgent(idAgent);

		// Construction de la liste
		ArrayList<Enfant> result = new ArrayList<Enfant>();
		for (int i = 0; i < liens.size(); i++) {
			LienEnfantAgent aLien = (LienEnfantAgent) liens.get(i);
			try {
				Enfant aEnfant = chercherEnfant(aLien.getIdEnfant());
				result.add(aEnfant);
			} catch (Exception e) {
				return new ArrayList<Enfant>();
			}
		}
		return result;
	}

	@Override
	public ArrayList<Enfant> listerEnfantHomonyme(String nom, String prenom, Date dateNaiss) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where upper(" + CHAMP_NOM + ")=? and upper(" + CHAMP_PRENOM
				+ ")=? and " + CHAMP_DATE_NAISSANCE + "=?";

		ArrayList<Enfant> liste = new ArrayList<Enfant>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				new Object[] { nom.toUpperCase(), prenom.toUpperCase(), dateNaiss });
		for (Map<String, Object> row : rows) {
			Enfant a = new Enfant();
			a.setIdEnfant((Integer) row.get(CHAMP_ID));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			a.setNom((String) row.get(CHAMP_NOM));
			a.setPrenom((String) row.get(CHAMP_PRENOM));
			a.setSexe((String) row.get(CHAMP_SEXE));
			a.setDateNaissance((Date) row.get(CHAMP_DATE_NAISSANCE));
			BigDecimal naissEt = (BigDecimal) row.get(CHAMP_CODE_PAYS_NAISS_ET);
			a.setCodePaysNaissEt(naissEt == null ? null : naissEt.intValue());
			BigDecimal commEt = (BigDecimal) row.get(CHAMP_CODE_COMMUNE_NAISS_ET);
			a.setCodeCommuneNaissEt(commEt == null ? null : commEt.intValue());
			BigDecimal naissFr = (BigDecimal) row.get(CHAMP_CODE_COMMUNE_NAISS_FR);
			a.setCodeCommuneNaissFr(naissFr == null ? null : naissFr.intValue());
			a.setDateDeces((Date) row.get(CHAMP_DATE_DECES));
			a.setNationalite((String) row.get(CHAMP_NATIONALITE));
			a.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			liste.add(a);
		}
		return liste;
	}

	@Override
	public Enfant chercherEnfant(Integer idEnfant) throws Exception {
		return super.chercherObject(Enfant.class, idEnfant);
	}
}
