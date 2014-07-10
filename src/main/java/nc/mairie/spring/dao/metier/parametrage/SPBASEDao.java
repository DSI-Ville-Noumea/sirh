package nc.mairie.spring.dao.metier.parametrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.SPBASE;
import nc.mairie.spring.dao.MairieDao;

public class SPBASEDao extends MairieDao implements SPBASEDaoInterface {

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

	public SPBASEDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "SPBASE";
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

	@Override
	public void creerSPBASE(String cdBase, String liBase, Double nbhLu, Double nbhMa, Double nbhMe, Double nbhJe,
			Double nbhVe, Double nbhSa, Double nbhDi, Double nbasCH, Double nbasHH) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CDBASE + "," + CHAMP_LIBASE + "," + CHAMP_NBAHLU + ","
				+ CHAMP_NBAHMA + "," + CHAMP_NBAHME + "," + CHAMP_NBAHJE + "," + CHAMP_NBAHVE + "," + CHAMP_NBAHSA
				+ "," + CHAMP_NBAHDI + "," + CHAMP_NBASCH + "," + CHAMP_NBASHH + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { cdBase, liBase, nbhLu, nbhMa, nbhMe, nbhJe, nbhVe, nbhSa, nbhDi,
				nbasCH, nbasHH });

	}

	@Override
	public void modifierSPBASE(String cdBase, String liBase, Double nbhLu, Double nbhMa, Double nbhMe, Double nbhJe,
			Double nbhVe, Double nbhSa, Double nbhDi, Double nbasCH, Double nbasHH) {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIBASE + "=?," + CHAMP_NBAHLU + "=?," + CHAMP_NBAHMA
				+ "=?," + CHAMP_NBAHME + "=?," + CHAMP_NBAHJE + "=?," + CHAMP_NBAHVE + "=?," + CHAMP_NBAHSA + "=?,"
				+ CHAMP_NBAHDI + "=?," + CHAMP_NBASCH + "=?," + CHAMP_NBASHH + "=? where " + CHAMP_CDBASE + " =?";
		jdbcTemplate.update(sql, new Object[] { liBase, nbhLu, nbhMa, nbhMe, nbhJe, nbhVe, nbhSa, nbhDi, nbasCH,
				nbasHH, cdBase });

	}
}
