package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeFDPCompetence;
import nc.mairie.spring.dao.utils.EaeDao;

public class EaeFDPCompetenceDao extends EaeDao implements EaeFDPCompetenceDaoInterface {

	public static final String CHAMP_ID_EAE_FICHE_POSTE = "ID_EAE_FICHE_POSTE";
	public static final String CHAMP_TYPE_COMPETENCE = "TYPE_COMPETENCE";
	public static final String CHAMP_LIBELLE_COMPETENCE = "LIBELLE_COMPETENCE";

	public EaeFDPCompetenceDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_FDP_COMPETENCE";
		super.CHAMP_ID = "ID_EAE_FDP_COMPETENCE";
	}

	@Override
	public void creerEaeFDPCompetence(Integer idEaeFichePoste, String typeComp, String libComp) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FICHE_POSTE + "," + CHAMP_TYPE_COMPETENCE + ","
				+ CHAMP_LIBELLE_COMPETENCE + ") " + "VALUES (?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeFichePoste, typeComp, libComp });
	}

	@Override
	public ArrayList<EaeFDPCompetence> listerEaeFDPCompetence(Integer idEaeFichePoste) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_FICHE_POSTE + "=?";

		ArrayList<EaeFDPCompetence> listeEaeFDPComp = new ArrayList<EaeFDPCompetence>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEaeFichePoste });
		for (Map<String, Object> row : rows) {
			EaeFDPCompetence comp = new EaeFDPCompetence();
			comp.setIdEaeFdpCompetence((Integer) row.get(CHAMP_ID));
			comp.setIdEaeFichePoste((Integer) row.get(CHAMP_ID_EAE_FICHE_POSTE));
			comp.setTypeCompetence((String) row.get(CHAMP_TYPE_COMPETENCE));
			comp.setLibelleCompetence((String) row.get(CHAMP_LIBELLE_COMPETENCE));
			listeEaeFDPComp.add(comp);
		}

		return listeEaeFDPComp;
	}

	@Override
	public void supprimerEaeFDPCompetence(Integer idEaeFDPComp) throws Exception {
		super.supprimerObject(idEaeFDPComp);
	}
}
