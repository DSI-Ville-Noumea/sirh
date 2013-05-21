package nc.mairie.spring.dao.metier.parametrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.SPBASE;

import org.springframework.jdbc.core.JdbcTemplate;

public class SPBASEDao implements SPBASEDaoInterface {

	public static final String NOM_TABLE = "MAIRIE.SPBASE";

	public static final String CHAMP_CDBASE = "CDBASE";
	public static final String CHAMP_NBASHH = "NBASHH";
	public static final String CHAMP_LIBASE = "LIBASE";
	public static final String CHAMP_NBAHSA = "NBAHSA";
	public static final String CHAMP_NBAHDI = "NBAHDI";
	public static final String CHAMP_NBAHLU = "NBAHLU";
	public static final String CHAMP_NBAHMA = "NBAHMA";
	public static final String CHAMP_NBAHME = "NBAHME";
	public static final String CHAMP_NBAHJE = "NBAHJE";
	public static final String CHAMP_NBAHVE = "NBAHVE";
	public static final String CHAMP_NBASCH = "NBASCH";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public SPBASEDao() {

	}

	@Override
	public ArrayList<SPBASE> listerSPBASE() throws Exception {

		String sql = "select * from " + NOM_TABLE;

		ArrayList<SPBASE> listeSPBASE = new ArrayList<SPBASE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			SPBASE base = new SPBASE();
			base.setCdBase((String) row.get(CHAMP_CDBASE));
			BigDecimal nbasHH = (BigDecimal) row.get(CHAMP_NBASHH);
			base.setNbasHH(nbasHH == null ? 0 : nbasHH.doubleValue());
			base.setLiBase((String) row.get(CHAMP_LIBASE));
			BigDecimal nbhSa = (BigDecimal) row.get(CHAMP_NBAHSA);
			base.setNbhSa(nbhSa == null ? 0 : nbhSa.doubleValue());
			BigDecimal nbhDi = (BigDecimal) row.get(CHAMP_NBAHDI);
			base.setNbhDi(nbhDi == null ? 0 : nbhDi.doubleValue());
			BigDecimal nbhLu = (BigDecimal) row.get(CHAMP_NBAHLU);
			base.setNbhLu(nbhLu == null ? 0 : nbhLu.doubleValue());
			BigDecimal nbhMa = (BigDecimal) row.get(CHAMP_NBAHMA);
			base.setNbhMa(nbhMa == null ? 0 : nbhMa.doubleValue());
			BigDecimal nbhMe = (BigDecimal) row.get(CHAMP_NBAHME);
			base.setNbhMe(nbhMe == null ? 0 : nbhMe.doubleValue());
			BigDecimal nbhJe = (BigDecimal) row.get(CHAMP_NBAHJE);
			base.setNbhJe(nbhJe == null ? 0 : nbhJe.doubleValue());
			BigDecimal nbhVe = (BigDecimal) row.get(CHAMP_NBAHVE);
			base.setNbhVe(nbhVe == null ? 0 : nbhVe.doubleValue());
			BigDecimal nbasch = (BigDecimal) row.get(CHAMP_NBASCH);
			base.setNbasCH(nbasch == null ? 0 : nbasch.doubleValue());
			listeSPBASE.add(base);
		}

		return listeSPBASE;
	}
}
