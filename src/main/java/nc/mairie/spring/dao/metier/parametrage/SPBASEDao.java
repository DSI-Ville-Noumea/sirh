package nc.mairie.spring.dao.metier.parametrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.SPBASE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SPBASEDao implements SPBASEDaoInterface {

	private Logger logger = LoggerFactory.getLogger(SPBASEDao.class);

	public static final String NOM_TABLE = "MAIRIE.SPBASE";

	public static final String CHAMP_CDBASE = "CDBASE";
	public static final String CHAMP_NBASHH = "NBASHH";
	public static final String CHAMP_CDCBAS = "CDCBAS";
	public static final String CHAMP_LIBASE = "LIBASE";
	public static final String CHAMP_NBHSA = "NBHSA";
	public static final String CHAMP_NBHDI = "NBHDI";
	public static final String CHAMP_NBHLU = "NBHLU";
	public static final String CHAMP_NBHMA = "NBHMA";
	public static final String CHAMP_NBHME = "NBHME";
	public static final String CHAMP_NBHJE = "NBHJE";
	public static final String CHAMP_NBHVE = "NBHVE";
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
			logger.info("List SPBASE : " + row.toString());
			base.setCdBase((String) row.get(CHAMP_CDBASE));
			BigDecimal nbasHH = (BigDecimal) row.get(CHAMP_NBASHH);
			base.setNbasHH(nbasHH == null ? 0 : nbasHH.intValue());
			BigDecimal cdcbas = (BigDecimal) row.get(CHAMP_CDCBAS);
			base.setCdcbas(cdcbas == null ? 0 : cdcbas.intValue());
			base.setLiBase((String) row.get(CHAMP_LIBASE));
			BigDecimal nbhSa = (BigDecimal) row.get(CHAMP_NBHSA);
			base.setNbhSa(nbhSa == null ? 0 : nbhSa.intValue());
			BigDecimal nbhDi = (BigDecimal) row.get(CHAMP_NBHDI);
			base.setNbhDi(nbhDi == null ? 0 : nbhDi.intValue());
			BigDecimal nbhLu = (BigDecimal) row.get(CHAMP_NBHLU);
			base.setNbhLu(nbhLu == null ? 0 : nbhLu.intValue());
			BigDecimal nbhMa = (BigDecimal) row.get(CHAMP_NBHMA);
			base.setNbhMa(nbhMa == null ? 0 : nbhMa.intValue());
			BigDecimal nbhMe = (BigDecimal) row.get(CHAMP_NBHME);
			base.setNbhMe(nbhMe == null ? 0 : nbhMe.intValue());
			BigDecimal nbhJe = (BigDecimal) row.get(CHAMP_NBHJE);
			base.setNbhJe(nbhJe == null ? 0 : nbhJe.intValue());
			BigDecimal nbhVe = (BigDecimal) row.get(CHAMP_NBHVE);
			base.setNbhVe(nbhVe == null ? 0 : nbhVe.intValue());
			BigDecimal nbasch = (BigDecimal) row.get(CHAMP_NBASCH);
			base.setNbasCH(nbasch == null ? 0 : nbasch.intValue());
			listeSPBASE.add(base);
		}

		return listeSPBASE;
	}
}
