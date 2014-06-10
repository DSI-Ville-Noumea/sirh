package nc.mairie.spring.dao.metier.suiviMedical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.suiviMedical.MotifVisiteMed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class MotifVisiteMedDao implements MotifVisiteMedDaoInterface {

	private Logger logger = LoggerFactory.getLogger(MotifVisiteMedDao.class);

	public static final String NOM_TABLE = "R_MOTIF_VM";

	public static final String CHAMP_ID_MOTIF_VM = "ID_MOTIF_VM";
	public static final String CHAMP_LIB_MOTIF_VM = "LIB_MOTIF_VM";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public MotifVisiteMedDao() {

	}

	@Override
	public MotifVisiteMed chercherMotifByLib(String lib) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_MOTIF_VM + " = ? ";

		MotifVisiteMed motifVM = (MotifVisiteMed) jdbcTemplate.queryForObject(sql, new Object[] { lib }, new BeanPropertyRowMapper<MotifVisiteMed>(MotifVisiteMed.class));

		return motifVM;
	}

	@Override
	public MotifVisiteMed chercherMotif(Integer idMotif) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_MOTIF_VM + " = ? ";

		MotifVisiteMed motifVM = (MotifVisiteMed) jdbcTemplate.queryForObject(sql, new Object[] { idMotif }, new BeanPropertyRowMapper<MotifVisiteMed>(MotifVisiteMed.class));

		return motifVM;
	}

	@Override
	public ArrayList<MotifVisiteMed> listerMotifVisiteMed() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<MotifVisiteMed> listeMotifVisiteMed = new ArrayList<MotifVisiteMed>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			MotifVisiteMed motif = new MotifVisiteMed();
			motif.setIdMotifVm((Integer) row.get(CHAMP_ID_MOTIF_VM));
			motif.setLibMotifVm((String) row.get(CHAMP_LIB_MOTIF_VM));
			listeMotifVisiteMed.add(motif);
		}

		return listeMotifVisiteMed;
	}
}
