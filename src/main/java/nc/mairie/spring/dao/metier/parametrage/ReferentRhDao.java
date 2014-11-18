package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ReferentRhDao extends SirhDao implements ReferentRhDaoInterface {

	public static final String CHAMP_SERVI = "SERVI";
	public static final String CHAMP_ID_AGENT_REFERENT = "ID_AGENT_REFERENT";
	public static final String CHAMP_NUMERO_TELEPHONE = "NUMERO_TELEPHONE";

	public ReferentRhDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_REFERENT_RH";
		super.CHAMP_ID = "ID_REFERENT_RH";
	}

	@Override
	public void creerReferentRh(String servi, Integer idAgentReferent, Integer numeroTelephone) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_SERVI + "," + CHAMP_ID_AGENT_REFERENT + ","
				+ CHAMP_NUMERO_TELEPHONE + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { servi, idAgentReferent, numeroTelephone });
	}

	@Override
	public void supprimerReferentRh(Integer idRferent) throws Exception {
		super.supprimerObject(idRferent);
	}

	@Override
	public List<ReferentRh> listerDistinctReferentRh() throws Exception {
		String sql = "select distinct(" + CHAMP_ID_AGENT_REFERENT + ")," + CHAMP_NUMERO_TELEPHONE + " from "
				+ NOM_TABLE + " where " + CHAMP_SERVI + " is not null ";

		ArrayList<ReferentRh> listeRef = new ArrayList<ReferentRh>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			ReferentRh ref = new ReferentRh();
			ref.setIdAgentReferent((Integer) row.get(CHAMP_ID_AGENT_REFERENT));
			ref.setNumeroTelephone((Integer) row.get(CHAMP_NUMERO_TELEPHONE));

			listeRef.add(ref);
		}

		return listeRef;
	}

	@Override
	public List<ReferentRh> listerServiceAvecReferentRh(Integer idAgentReferent) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT_REFERENT + "=? and " + CHAMP_SERVI
				+ " is not null ";

		ArrayList<ReferentRh> listeRef = new ArrayList<ReferentRh>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgentReferent });
		for (Map<String, Object> row : rows) {
			ReferentRh ref = new ReferentRh();
			ref.setIdReferentRh((Integer) row.get(CHAMP_ID));
			ref.setServi((String) row.get(CHAMP_SERVI));
			ref.setIdAgentReferent((Integer) row.get(CHAMP_ID_AGENT_REFERENT));
			ref.setNumeroTelephone((Integer) row.get(CHAMP_NUMERO_TELEPHONE));

			listeRef.add(ref);
		}

		return listeRef;
	}

	@Override
	public ReferentRh getReferentRhGlobal() {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_SERVI + " is null ";
		ReferentRh type = (ReferentRh) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<ReferentRh>(
				ReferentRh.class));
		return type;
	}
}
