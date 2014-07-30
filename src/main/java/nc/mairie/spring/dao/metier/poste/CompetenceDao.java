package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CompetenceDao extends SirhDao implements CompetenceDaoInterface {

	public static final String CHAMP_ID_TYPE_COMPETENCE = "ID_TYPE_COMPETENCE";
	public static final String CHAMP_NOM_COMPETENCE = "NOM_COMPETENCE";

	public CompetenceDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "COMPETENCE";
		super.CHAMP_ID = "ID_COMPETENCE";
	}

	@Override
	public Competence chercherCompetenceAvecType(Integer idCompetence, Integer idTypeCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + " = ? and " + CHAMP_ID_TYPE_COMPETENCE
				+ "=?";
		Competence cadre = (Competence) jdbcTemplate.queryForObject(sql,
				new Object[] { idCompetence, idTypeCompetence },
				new BeanPropertyRowMapper<Competence>(Competence.class));
		return cadre;
	}

	@Override
	public ArrayList<Competence> listerCompetenceAvecFEEtTypeComp(Integer idTypeCompetence,
			ArrayList<CompetenceFE> liens) throws Exception {
		// Construction de la liste
		ArrayList<Competence> result = new ArrayList<Competence>();
		for (int i = 0; i < liens.size(); i++) {
			CompetenceFE aLien = (CompetenceFE) liens.get(i);
			try {
				Competence comp = chercherCompetenceAvecType(aLien.getIdCompetence(), idTypeCompetence);
				result.add(comp);
			} catch (Exception e) {
				return new ArrayList<Competence>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<Competence> listerCompetenceAvecFP(ArrayList<CompetenceFP> liens) throws Exception {
		// Construction de la liste
		ArrayList<Competence> result = new ArrayList<Competence>();
		for (int i = 0; i < liens.size(); i++) {
			CompetenceFP aLien = (CompetenceFP) liens.get(i);
			try {
				Competence comp = chercherCompetence(aLien.getIdCompetence());
				result.add(comp);
			} catch (Exception e) {
				return new ArrayList<Competence>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<Competence> listerCompetenceAvecFE(ArrayList<CompetenceFE> liens) throws Exception {

		// Construction de la liste
		ArrayList<Competence> result = new ArrayList<Competence>();
		for (int i = 0; i < liens.size(); i++) {
			CompetenceFE aLien = (CompetenceFE) liens.get(i);
			try {
				Competence comp = chercherCompetence(aLien.getIdCompetence());
				result.add(comp);
			} catch (Exception e) {
				return new ArrayList<Competence>();
			}
		}

		return result;
	}

	@Override
	public void supprimerCompetence(Integer idCompetence) throws Exception {
		super.supprimerObject(idCompetence);
	}

	@Override
	public void modifierCompetence(Integer idCompetence, Integer idTypeCompetence, String nomComp) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_COMPETENCE + "=?," + CHAMP_NOM_COMPETENCE
				+ "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idTypeCompetence, nomComp, idCompetence });
	}

	@Override
	public void creerCompetence(Integer idTypeCompetence, String nomComp) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TYPE_COMPETENCE + "," + CHAMP_NOM_COMPETENCE + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idTypeCompetence, nomComp });
	}

	@Override
	public Competence chercherCompetence(Integer idCompetence) throws Exception {
		return super.chercherObject(Competence.class, idCompetence);
	}

	@Override
	public ArrayList<Competence> listerCompetenceAvecType(Integer idTypeCompetence) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_COMPETENCE + "=? ";

		ArrayList<Competence> liste = new ArrayList<Competence>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTypeCompetence });
		for (Map<String, Object> row : rows) {
			Competence a = new Competence();
			a.setIdCompetence((Integer) row.get(CHAMP_ID));
			a.setIdTypeCompetence((Integer) row.get(CHAMP_ID_TYPE_COMPETENCE));
			a.setNomCompetence((String) row.get(CHAMP_NOM_COMPETENCE));
			liste.add(a);
		}

		return liste;
	}
}
