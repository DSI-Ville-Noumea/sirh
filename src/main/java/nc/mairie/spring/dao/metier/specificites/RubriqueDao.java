package nc.mairie.spring.dao.metier.specificites;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.utils.MairieDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class RubriqueDao extends MairieDao implements RubriqueDaoInterface {

	public static final String CHAMP_LIRUBR = "LIRUBR";
	public static final String CHAMP_TYIMPO = "TYIMPO";
	public static final String CHAMP_CDRCAF = "CDRCAF";
	public static final String CHAMP_RUBRAP = "RUBRAP";
	public static final String CHAMP_TYPRIM = "TYPRIM";
	public static final String CHAMP_TYRUBR = "TYRUBR";
	public static final String CHAMP_CATPOS = "CATPOS";
	public static final String CHAMP_DATINA = "DATINA";

	public RubriqueDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "SPRUBR";
		super.CHAMP_ID = "NORUBR";
	}

	@Override
	public ArrayList<Rubrique> listerRubriqueAvecTypeRubrAvecInactives(String typeRubrique) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYRUBR + "=? order by " + CHAMP_LIRUBR;

		ArrayList<Rubrique> liste = new ArrayList<Rubrique>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { typeRubrique });
		for (Map<String, Object> row : rows) {
			Rubrique d = new Rubrique();
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_ID);
			d.setNorubr(norubr.intValue());
			d.setLirubr((String) row.get(CHAMP_LIRUBR));
			d.setTyimpo((String) row.get(CHAMP_TYIMPO));
			d.setCdrcaf((String) row.get(CHAMP_CDRCAF));
			BigDecimal rubrap = (BigDecimal) row.get(CHAMP_RUBRAP);
			d.setRubrap(rubrap != null ? rubrap.intValue() : null);
			d.setTyprim((String) row.get(CHAMP_TYPRIM));
			d.setTyrubr((String) row.get(CHAMP_TYRUBR));
			d.setCatpos((String) row.get(CHAMP_CATPOS));
			BigDecimal datina = (BigDecimal) row.get(CHAMP_DATINA);
			d.setDatina(datina != null ? datina.intValue() : null);
			liste.add(d);
		}

		return liste;
	}

	@Override
	public Rubrique chercherRubrique(Integer norubr) {

		try {
			return super.chercherObject(Rubrique.class, norubr);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ArrayList<Rubrique> listerRubrique7000() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Integer dateJour = Integer.valueOf(sdf.format(new Date()));

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + "> 6999 and " + CHAMP_ID
				+ " < 8000 and ( ? < " + CHAMP_DATINA + " or " + CHAMP_DATINA + "=0)";

		ArrayList<Rubrique> liste = new ArrayList<Rubrique>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { dateJour });
		for (Map<String, Object> row : rows) {
			Rubrique d = new Rubrique();
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_ID);
			d.setNorubr(norubr.intValue());
			d.setLirubr((String) row.get(CHAMP_LIRUBR));
			d.setTyimpo((String) row.get(CHAMP_TYIMPO));
			d.setCdrcaf((String) row.get(CHAMP_CDRCAF));
			BigDecimal rubrap = (BigDecimal) row.get(CHAMP_RUBRAP);
			d.setRubrap(rubrap != null ? rubrap.intValue() : null);
			d.setTyprim((String) row.get(CHAMP_TYPRIM));
			d.setTyrubr((String) row.get(CHAMP_TYRUBR));
			d.setCatpos((String) row.get(CHAMP_CATPOS));
			BigDecimal datina = (BigDecimal) row.get(CHAMP_DATINA);
			d.setDatina(datina != null ? datina.intValue() : null);
			liste.add(d);
		}

		return liste;
	}

	@Override
	public ArrayList<Rubrique> listerRubriqueAvecTypeRubr(String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Integer dateJour = Integer.valueOf(sdf.format(new Date()));

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYRUBR + "=?  and ( ? < " + CHAMP_DATINA + " or "
				+ CHAMP_DATINA + "=0) order by " + CHAMP_LIRUBR;

		ArrayList<Rubrique> liste = new ArrayList<Rubrique>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { type, dateJour });
		for (Map<String, Object> row : rows) {
			Rubrique d = new Rubrique();
			BigDecimal norubr = (BigDecimal) row.get(CHAMP_ID);
			d.setNorubr(norubr.intValue());
			d.setLirubr((String) row.get(CHAMP_LIRUBR));
			d.setTyimpo((String) row.get(CHAMP_TYIMPO));
			d.setCdrcaf((String) row.get(CHAMP_CDRCAF));
			BigDecimal rubrap = (BigDecimal) row.get(CHAMP_RUBRAP);
			d.setRubrap(rubrap != null ? rubrap.intValue() : null);
			d.setTyprim((String) row.get(CHAMP_TYPRIM));
			d.setTyrubr((String) row.get(CHAMP_TYRUBR));
			d.setCatpos((String) row.get(CHAMP_CATPOS));
			BigDecimal datina = (BigDecimal) row.get(CHAMP_DATINA);
			d.setDatina(datina != null ? datina.intValue() : null);
			liste.add(d);
		}

		return liste;
	}

	public Rubrique chercherRubriqueCharge(Integer norubrChoisi) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_TYRUBR + "=?  and " + CHAMP_ID + "=? ";
		Rubrique aff = (Rubrique) jdbcTemplate.queryForObject(sql, new Object[] { "C", norubrChoisi },
				new BeanPropertyRowMapper<Rubrique>(Rubrique.class));
		return aff;
	}

}