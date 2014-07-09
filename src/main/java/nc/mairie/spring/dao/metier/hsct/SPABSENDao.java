package nc.mairie.spring.dao.metier.hsct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.metier.hsct.SPABSEN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SPABSENDao implements SPABSENDaoInterface {

	private Logger logger = LoggerFactory.getLogger(SPABSENDao.class);

	public static final String NOM_TABLE = "SPABSEN";

	public static final String CHAMP_NOMATR = "NOMATR";
	public static final String CHAMP_TYPE3 = "TYPE3";
	public static final String CHAMP_DATDEB = "DATDEB";
	public static final String CHAMP_DATFIN = "DATFIN";
	public static final String CHAMP_NBJOUR = "NBJOUR";
	public static final String CHAMP_TOTPRI = "TOTPRI";
	public static final String CHAMP_NBJCDS = "NBJCDS";
	public static final String CHAMP_NBJCPS = "NBJCPS";
	public static final String CHAMP_RAPPS = "RAPPS";
	public static final String CHAMP_RAPDS = "RAPDS";
	public static final String CHAMP_DATESS = "DATESS";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public SPABSENDao() {

	}

	@Override
	public ArrayList<Integer> listerMatriculeAbsencePourSM(String type, Integer moisChoisi, Integer anneeChoisi) throws Exception {
		String mois = moisChoisi.toString();
		if (mois.toString().length() == 1) {
			mois = "0" + moisChoisi.toString();
		}
		String sql = "select " + CHAMP_NOMATR + " from " + NOM_TABLE + " where " + CHAMP_TYPE3 + "=? and substring(" + CHAMP_DATFIN
				+ ",5,2)=? and substring(" + CHAMP_DATFIN + ",0,5)=? group by " + CHAMP_NOMATR;

		ArrayList<Integer> listeMatricule = new ArrayList<Integer>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { type, mois, anneeChoisi });
		for (Map<String, Object> row : rows) {
			BigDecimal noMat = (BigDecimal) row.get(CHAMP_NOMATR);
			listeMatricule.add(noMat.intValue());
		}

		return listeMatricule;
	}

	@Override
	public ArrayList<SPABSEN> listerAbsencePourAgentTypeEtMoisAnnee(Integer nomatr, String type, Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		String mois = moisChoisi.toString();
		if (mois.toString().length() == 1) {
			mois = "0" + moisChoisi.toString();
		}
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + "=? and " + CHAMP_TYPE3 + "=? and substring(" + CHAMP_DATFIN
				+ ",5,2)=? and substring(" + CHAMP_DATFIN + ",0,5)=? order by " + CHAMP_DATDEB + " desc";

		ArrayList<SPABSEN> listeSPABSEN = new ArrayList<SPABSEN>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { nomatr, type, mois, anneeChoisi });
		for (Map<String, Object> row : rows) {
			SPABSEN abs = new SPABSEN();
			BigDecimal noMat = (BigDecimal) row.get(CHAMP_NOMATR);
			abs.setNoMatr(noMat.intValue());
			abs.setType((String) row.get(CHAMP_TYPE3));
			BigDecimal datDeb = (BigDecimal) row.get(CHAMP_DATDEB);
			abs.setDatDeb(datDeb.intValue());
			BigDecimal datFin = (BigDecimal) row.get(CHAMP_DATFIN);
			abs.setDatFin(datFin.intValue());
			BigDecimal nbJ = (BigDecimal) row.get(CHAMP_NBJOUR);
			abs.setNbJour(nbJ.intValue());
			BigDecimal totPri = (BigDecimal) row.get(CHAMP_TOTPRI);
			abs.setTotPri(totPri.intValue());
			BigDecimal nbCds = (BigDecimal) row.get(CHAMP_NBJCDS);
			abs.setNbjCds(nbCds.intValue());
			BigDecimal nbCps = (BigDecimal) row.get(CHAMP_NBJCPS);
			abs.setNbjCps(nbCps.intValue());
			BigDecimal rapPs = (BigDecimal) row.get(CHAMP_RAPPS);
			abs.setRapps(rapPs.intValue());
			BigDecimal rapDs = (BigDecimal) row.get(CHAMP_RAPDS);
			abs.setRapds(rapDs.intValue());
			BigDecimal datEss = (BigDecimal) row.get(CHAMP_DATESS);
			abs.setDatEss(datEss.intValue());
			listeSPABSEN.add(abs);
		}

		return listeSPABSEN;
	}

	@Override
	public ArrayList<Integer> listerMatriculeAbsencePourSMDoubleType(String typeMA, String typeLM, Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		String mois = moisChoisi.toString();
		if (mois.toString().length() == 1) {
			mois = "0" + moisChoisi.toString();
		}
		String sql = "select " + CHAMP_NOMATR + " from " + NOM_TABLE + " where (" + CHAMP_TYPE3 + "=? or " + CHAMP_TYPE3 + "=? )and substring("
				+ CHAMP_DATFIN + ",5,2)=? and substring(" + CHAMP_DATFIN + ",0,5)=? group by " + CHAMP_NOMATR;

		ArrayList<Integer> listeMatricule = new ArrayList<Integer>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { typeMA, typeLM, mois, anneeChoisi });
		for (Map<String, Object> row : rows) {
			BigDecimal noMat = (BigDecimal) row.get(CHAMP_NOMATR);
			listeMatricule.add(noMat.intValue());
		}

		return listeMatricule;
	}

	@Override
	public ArrayList<SPABSEN> listerAbsencePourAgentTypeEtMoisAnneeDoubleType(Integer nomatr, String typeMA, String typeLM, Integer moisChoisi,
			Integer anneeChoisi) throws Exception {
		String mois = moisChoisi.toString();
		if (mois.toString().length() == 1) {
			mois = "0" + moisChoisi.toString();
		}
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + "=? and (" + CHAMP_TYPE3 + "=? or " + CHAMP_TYPE3
				+ "=? ) and substring(" + CHAMP_DATFIN + ",5,2)=? and substring(" + CHAMP_DATFIN + ",0,5)=? order by " + CHAMP_DATDEB + " desc";

		ArrayList<SPABSEN> listeSPABSEN = new ArrayList<SPABSEN>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { nomatr, typeMA, typeLM, mois, anneeChoisi });
		for (Map<String, Object> row : rows) {
			SPABSEN abs = new SPABSEN();
			BigDecimal noMat = (BigDecimal) row.get(CHAMP_NOMATR);
			abs.setNoMatr(noMat.intValue());
			abs.setType((String) row.get(CHAMP_TYPE3));
			BigDecimal datDeb = (BigDecimal) row.get(CHAMP_DATDEB);
			abs.setDatDeb(datDeb.intValue());
			BigDecimal datFin = (BigDecimal) row.get(CHAMP_DATFIN);
			abs.setDatFin(datFin.intValue());
			BigDecimal nbJ = (BigDecimal) row.get(CHAMP_NBJOUR);
			abs.setNbJour(nbJ.intValue());
			BigDecimal totPri = (BigDecimal) row.get(CHAMP_TOTPRI);
			abs.setTotPri(totPri.intValue());
			BigDecimal nbCds = (BigDecimal) row.get(CHAMP_NBJCDS);
			abs.setNbjCds(nbCds.intValue());
			BigDecimal nbCps = (BigDecimal) row.get(CHAMP_NBJCPS);
			abs.setNbjCps(nbCps.intValue());
			BigDecimal rapPs = (BigDecimal) row.get(CHAMP_RAPPS);
			abs.setRapps(rapPs.intValue());
			BigDecimal rapDs = (BigDecimal) row.get(CHAMP_RAPDS);
			abs.setRapds(rapDs.intValue());
			BigDecimal datEss = (BigDecimal) row.get(CHAMP_DATESS);
			abs.setDatEss(datEss.intValue());
			listeSPABSEN.add(abs);
		}

		return listeSPABSEN;
	}
}
