package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.parametrage.CapRowMapper;
import nc.mairie.spring.domain.metier.parametrage.Cap;

import org.springframework.jdbc.core.JdbcTemplate;

public class CapDao implements CapDaoInterface {

	public static final String NOM_TABLE = "P_CAP";

	public static final String CHAMP_ID_CAP = "ID_CAP";
	public static final String CHAMP_CODE_CAP = "CODE_CAP";
	public static final String CHAMP_REF_CAP = "REF_CAP";
	public static final String CHAMP_DESCRIPTION = "DESCRIPTION";
	public static final String CHAMP_TYPE_CAP = "TYPE_CAP";
	public static final String CHAMP_CAP_VDN = "CAP_VDN";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CapDao() {

	}

	@Override
	public ArrayList<Cap> listerCap() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_ID_CAP;

		ArrayList<Cap> listeCap = new ArrayList<Cap>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Cap cap = new Cap();
			cap.setIdCap((Integer) row.get(CHAMP_ID_CAP));
			cap.setCodeCap((String) row.get(CHAMP_CODE_CAP));
			cap.setRefCap((String) row.get(CHAMP_REF_CAP));
			cap.setDescription((String) row.get(CHAMP_DESCRIPTION));
			cap.setTypeCap((String) row.get(CHAMP_TYPE_CAP));
			cap.setCapVDN((Integer) row.get(CHAMP_CAP_VDN));

			listeCap.add(cap);
		}

		return listeCap;
	}

	@Override
	public void creerCap(String codeCap, String refCap, String description, String typeCap, Integer capVDN)
			throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_CAP + "," + CHAMP_REF_CAP + "," + CHAMP_DESCRIPTION
				+ "," + CHAMP_TYPE_CAP + "," + CHAMP_CAP_VDN + ") " + "VALUES (?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { codeCap.toUpperCase(), refCap.toUpperCase(), description, typeCap,
				capVDN });
	}

	@Override
	public void supprimerCap(Integer idCap) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_CAP + "=?";
		jdbcTemplate.update(sql, new Object[] { idCap });
	}

	@Override
	public Cap chercherCap(String codeCap, String refCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_CODE_CAP + " = ? and " + CHAMP_REF_CAP + " =? ";
		Cap c = (Cap) jdbcTemplate.queryForObject(sql, new Object[] { codeCap, refCap }, new CapRowMapper());
		return c;
	}

	@Override
	public void modifierCap(Integer idCap, String codeCap, String refCap, String description, String typeCap,
			Integer capVDN) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CODE_CAP + "=?," + CHAMP_REF_CAP + "=?,"
				+ CHAMP_DESCRIPTION + "=?," + CHAMP_TYPE_CAP + "=?," + CHAMP_CAP_VDN + "=? where " + CHAMP_ID_CAP
				+ "=?";
		jdbcTemplate.update(sql, new Object[] { codeCap.toUpperCase(), refCap.toUpperCase(), description, typeCap,
				capVDN, idCap });
	}

	@Override
	public Cap chercherCapByCodeCap(String codeCap) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_CODE_CAP + " = ? ";
		Cap c = (Cap) jdbcTemplate.queryForObject(sql, new Object[] { codeCap }, new CapRowMapper());
		return c;
	}

	@Override
	public Cap chercherCapByAgent(Integer idAgent, String type, Integer annee) {
		StringBuilder sb = new StringBuilder();
		sb.append("select pc.* from avct_fonct av ");
		sb.append("inner join spgradn g on av.id_nouv_grade=g.cdgrad ");
		sb.append("inner join spgeng e on g.codgrg=e.cdgeng ");
		sb.append("inner join corps_cap c on e.cdgeng=c.cdgeng ");
		sb.append("inner join p_cap pc on c.id_cap=pc.id_cap ");
		sb.append("where av.id_agent= ? and av.annee= ? ");
		sb.append("and av.agent_vdn = pc.cap_vdn ");
		sb.append("and pc.type_cap= ? ");
		Cap c = (Cap) jdbcTemplate.queryForObject(sb.toString(), new Object[] { idAgent, annee, type },
				new CapRowMapper());
		return c;
	}
}
