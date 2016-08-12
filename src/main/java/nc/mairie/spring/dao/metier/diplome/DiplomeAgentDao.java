package nc.mairie.spring.dao.metier.diplome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DiplomeAgentDao extends SirhDao implements DiplomeAgentDaoInterface {

	public static final String CHAMP_ID_TITRE_DIPLOME = "ID_TITRE_DIPLOME";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";
	public static final String CHAMP_ID_SPECIALITE_DIPLOME = "ID_SPECIALITE_DIPLOME";
	public static final String CHAMP_DATE_OBTENTION = "DATE_OBTENTION";
	public static final String CHAMP_NOM_ECOLE = "NOM_ECOLE";

	public DiplomeAgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DIPLOME_AGENT";
		super.CHAMP_ID = "ID_DIPLOME";
	}

	@Override
	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecTitreDiplome(Integer idTitreDiplome) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TITRE_DIPLOME + "=? ";

		ArrayList<DiplomeAgent> liste = new ArrayList<DiplomeAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idTitreDiplome });
		for (Map<String, Object> row : rows) {
			DiplomeAgent dip = new DiplomeAgent();
			dip.setIdDiplome((Integer) row.get(CHAMP_ID));
			dip.setIdTitreDiplome((Integer) row.get(CHAMP_ID_TITRE_DIPLOME));
			dip.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			dip.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			dip.setIdSpecialiteDiplome((Integer) row.get(CHAMP_ID_SPECIALITE_DIPLOME));
			dip.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			dip.setNomEcole((String) row.get(CHAMP_NOM_ECOLE));
			liste.add(dip);
		}
		return liste;
	}

	@Override
	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecSpecialiteDiplome(Integer idSpecialite) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_SPECIALITE_DIPLOME + "=? ";

		ArrayList<DiplomeAgent> liste = new ArrayList<DiplomeAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idSpecialite });
		for (Map<String, Object> row : rows) {
			DiplomeAgent dip = new DiplomeAgent();
			dip.setIdDiplome((Integer) row.get(CHAMP_ID));
			dip.setIdTitreDiplome((Integer) row.get(CHAMP_ID_TITRE_DIPLOME));
			dip.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			dip.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			dip.setIdSpecialiteDiplome((Integer) row.get(CHAMP_ID_SPECIALITE_DIPLOME));
			dip.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			dip.setNomEcole((String) row.get(CHAMP_NOM_ECOLE));
			liste.add(dip);
		}
		return liste;
	}

	@Override
	public ArrayList<DiplomeAgent> listerDiplomeAgentAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATE_OBTENTION;

		ArrayList<DiplomeAgent> liste = new ArrayList<DiplomeAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			DiplomeAgent dip = new DiplomeAgent();
			dip.setIdDiplome((Integer) row.get(CHAMP_ID));
			dip.setIdTitreDiplome((Integer) row.get(CHAMP_ID_TITRE_DIPLOME));
			dip.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			dip.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			dip.setIdSpecialiteDiplome((Integer) row.get(CHAMP_ID_SPECIALITE_DIPLOME));
			dip.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			dip.setNomEcole((String) row.get(CHAMP_NOM_ECOLE));
			liste.add(dip);
		}
		return liste;
	}

	@Override
	public DiplomeAgent chercherDernierDiplomeAgentAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and " + CHAMP_DATE_OBTENTION
				+ "= (SELECT MAX(" + CHAMP_DATE_OBTENTION + ") FROM " + NOM_TABLE + " WHERE ID_AGENT = ?)";
		DiplomeAgent dip = (DiplomeAgent) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idAgent },
				new BeanPropertyRowMapper<DiplomeAgent>(DiplomeAgent.class));
		return dip;
	}

	@Override
	public ArrayList<DiplomeAgent> listerEcolesDiplomeAgent() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOM_ECOLE + "!= ''";

		ArrayList<DiplomeAgent> liste = new ArrayList<DiplomeAgent>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			DiplomeAgent dip = new DiplomeAgent();
			dip.setIdDiplome((Integer) row.get(CHAMP_ID));
			dip.setIdTitreDiplome((Integer) row.get(CHAMP_ID_TITRE_DIPLOME));
			dip.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			dip.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			dip.setIdSpecialiteDiplome((Integer) row.get(CHAMP_ID_SPECIALITE_DIPLOME));
			dip.setDateObtention((Date) row.get(CHAMP_DATE_OBTENTION));
			dip.setNomEcole((String) row.get(CHAMP_NOM_ECOLE));
			liste.add(dip);
		}
		return liste;
	}

	@Override
	public Integer creerDiplomeAgent(Integer idTitreDiplome, Integer idAgent, Integer idDocument,
			Integer idSpecialiteDiplome, Date dateObtention, String nomEcole) throws Exception {
		
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TITRE_DIPLOME + "," + CHAMP_ID_AGENT + ","
				+ CHAMP_ID_DOCUMENT + "," + CHAMP_ID_SPECIALITE_DIPLOME + "," + CHAMP_DATE_OBTENTION + ","
				+ CHAMP_NOM_ECOLE + " ) VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idTitreDiplome, idAgent, idDocument, idSpecialiteDiplome,
				dateObtention, nomEcole });
		
		String sqlId = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TITRE_DIPLOME + "=? and " 
				+ CHAMP_ID_AGENT + "=? and " + CHAMP_ID_SPECIALITE_DIPLOME + "=? and " + CHAMP_DATE_OBTENTION + "=?";

		DiplomeAgent form = (DiplomeAgent) jdbcTemplate.queryForObject(sqlId, new Object[] { idTitreDiplome,
				idAgent, idSpecialiteDiplome, dateObtention },
				new BeanPropertyRowMapper<DiplomeAgent>(DiplomeAgent.class));

		return form.getIdDiplome();
	}

	@Override
	public void modifierDiplomeAgent(Integer idDiplome, Integer idTitreDiplome, Integer idAgent, Integer idDocument,
			Integer idSpecialiteDiplome, Date dateObtention, String nomEcole) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TITRE_DIPLOME + "=?," + CHAMP_ID_AGENT + "=?,"
				+ CHAMP_ID_DOCUMENT + "=?," + CHAMP_ID_SPECIALITE_DIPLOME + "=?," + CHAMP_DATE_OBTENTION + "=?,"
				+ CHAMP_NOM_ECOLE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idTitreDiplome, idAgent, idDocument, idSpecialiteDiplome,
				dateObtention, nomEcole, idDiplome });
	}

	@Override
	public void supprimerDiplomeAgent(Integer idDiplome) throws Exception {
		super.supprimerObject(idDiplome);
	}
}
