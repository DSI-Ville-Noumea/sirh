package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.CasierJudiciaire;
import nc.mairie.spring.dao.SirhDao;

public class CasierJudiciaireDao extends SirhDao implements CasierJudiciaireDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";
	public static final String CHAMP_NUM_EXTRAIT = "NUM_EXTRAIT";
	public static final String CHAMP_DATE_EXTRAIT = "DATE_EXTRAIT";
	public static final String CHAMP_PRIVATION_DROITS_CIV = "PRIVATION_DROITS_CIV";
	public static final String CHAMP_COMM_EXTRAIT = "COMM_EXTRAIT";

	public CasierJudiciaireDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "CASIER_JUDICIAIRE";
		super.CHAMP_ID = "ID_CASIER_JUD";
	}

	@Override
	public ArrayList<CasierJudiciaire> listerCasierJudiciaireAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_EXTRAIT;

		ArrayList<CasierJudiciaire> liste = new ArrayList<CasierJudiciaire>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			CasierJudiciaire a = new CasierJudiciaire();
			a.setIdCasierJud((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			a.setNumExtrait((String) row.get(CHAMP_NUM_EXTRAIT));
			a.setDateExtrait((Date) row.get(CHAMP_DATE_EXTRAIT));
			Integer priva = (Integer) row.get(CHAMP_PRIVATION_DROITS_CIV);
			a.setPrivationDroitsCiv(priva == 0 ? false : true);
			a.setCommExtrait((String) row.get(CHAMP_COMM_EXTRAIT));

			liste.add(a);
		}
		return liste;
	}

	@Override
	public void creerCasierJudiciaire(Integer idAgent, Integer idDocument, String numExtrait, Date dateExtrait,
			boolean privationDroitsCiv, String commExtrait) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_DOCUMENT + ","
				+ CHAMP_NUM_EXTRAIT + "," + CHAMP_DATE_EXTRAIT + "," + CHAMP_PRIVATION_DROITS_CIV + ","
				+ CHAMP_COMM_EXTRAIT + ") VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idDocument, numExtrait, dateExtrait, privationDroitsCiv,
				commExtrait });
	}

	@Override
	public void modifierCasierJudiciaire(Integer idCasierJud, Integer idAgent, Integer idDocument, String numExtrait,
			Date dateExtrait, boolean privationDroitsCiv, String commExtrait) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_ID_DOCUMENT + "=?,"
				+ CHAMP_NUM_EXTRAIT + "=?," + CHAMP_DATE_EXTRAIT + "=?," + CHAMP_PRIVATION_DROITS_CIV + "=?,"
				+ CHAMP_COMM_EXTRAIT + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idDocument, numExtrait, dateExtrait, privationDroitsCiv,
				commExtrait, idCasierJud });
	}

	@Override
	public void supprimerCasierJudiciaire(Integer idCasierJud) throws Exception {
		super.supprimerObject(idCasierJud);
	}
}
