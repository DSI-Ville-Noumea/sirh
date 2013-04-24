package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.CampagneEAERowMapper;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampagneEAEDao implements CampagneEAEDaoInterface {

	private Logger logger = LoggerFactory.getLogger(CampagneEAEDao.class);

	public static final String NOM_TABLE = "EAE_CAMPAGNE_EAE";

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_EAE";

	public static final String CHAMP_ID_CAMPAGNE_EAE = "ID_CAMPAGNE_EAE";
	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_DATE_OUVERTURE_KIOSQUE = "DATE_OUVERTURE_KIOSQUE";
	public static final String CHAMP_DATE_FERMETURE_KIOSQUE = "DATE_FERMETURE_KIOSQUE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CampagneEAEDao() {

	}

	@Override
	public ArrayList<CampagneEAE> listerCampagneEAE() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_ANNEE + " desc";

		ArrayList<CampagneEAE> listeCampagneEAE = new ArrayList<CampagneEAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			CampagneEAE camp = new CampagneEAE();
			logger.info("List campagne EAE : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_CAMPAGNE_EAE);
			camp.setIdCampagneEAE(id.intValue());
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE);
			camp.setAnnee(annee.intValue());
			camp.setDateDebut((Date) row.get(CHAMP_DATE_DEBUT));
			camp.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			camp.setDateOuvertureKiosque((Date) row.get(CHAMP_DATE_OUVERTURE_KIOSQUE));
			camp.setDateFermetureKiosque((Date) row.get(CHAMP_DATE_FERMETURE_KIOSQUE));
			camp.setCommentaire((String) row.get(CHAMP_COMMENTAIRE));
			listeCampagneEAE.add(camp);
		}

		return listeCampagneEAE;
	}

	@Override
	public CampagneEAE chercherCampagneEAE(Integer idCampagneEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CAMPAGNE_EAE + " = ? ";

		CampagneEAE camp = (CampagneEAE) jdbcTemplate.queryForObject(sql, new Object[] { idCampagneEAE }, new CampagneEAERowMapper());

		return camp;
	}

	@Override
	public Integer creerCampagneEAE(Integer annee, Date dateDebut, String commentaire) throws Exception {
		String sqlClePrimaire = "select " + NOM_SEQUENCE + ".nextval from DUAL";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_CAMPAGNE_EAE + "," + CHAMP_ANNEE + "," + CHAMP_DATE_DEBUT + "," + CHAMP_COMMENTAIRE
				+ ") VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { id, annee, dateDebut, commentaire });

		return id;
	}

	@Override
	public void modifierCampagneEAE(Integer idCampagneEAE, Date dateDebut, String commentaire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_DEBUT + " =?," + CHAMP_COMMENTAIRE + "=? where " + CHAMP_ID_CAMPAGNE_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateDebut, commentaire, idCampagneEAE });
	}

	@Override
	public void modifierOuvertureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateOuvertureKiosque) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_OUVERTURE_KIOSQUE + " =? where " + CHAMP_ID_CAMPAGNE_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateOuvertureKiosque, idCampagneEAE });
	}

	@Override
	public void modifierFermetureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateFermetureKiosque) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_FERMETURE_KIOSQUE + " =? where " + CHAMP_ID_CAMPAGNE_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateFermetureKiosque, idCampagneEAE });
	}

	@Override
	public void modifierFinCampagneEAE(Integer idCampagneEAE, Date dateFin) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_FIN + " =? where " + CHAMP_ID_CAMPAGNE_EAE + "=?";
		jdbcTemplate.update(sql, new Object[] { dateFin, idCampagneEAE });
	}

	@Override
	public CampagneEAE chercherCampagneEAEAnnee(Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + " = ? ";

		CampagneEAE camp = (CampagneEAE) jdbcTemplate.queryForObject(sql, new Object[] { annee }, new CampagneEAERowMapper());

		return camp;
	}

	@Override
	public CampagneEAE chercherCampagneEAEOuverte() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_DATE_FIN + " is null ";
		CampagneEAE camp = (CampagneEAE) jdbcTemplate.queryForObject(sql, new CampagneEAERowMapper());
		return camp;
	}
}
