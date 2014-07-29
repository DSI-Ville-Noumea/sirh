package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.CampagneEAE;
import nc.mairie.spring.dao.utils.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class CampagneEAEDao extends EaeDao implements CampagneEAEDaoInterface {

	private Logger logger = LoggerFactory.getLogger(CampagneEAEDao.class);

	public static final String NOM_SEQUENCE = "EAE_S_CAMPAGNE_EAE";

	public static final String CHAMP_ANNEE = "ANNEE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_DATE_OUVERTURE_KIOSQUE = "DATE_OUVERTURE_KIOSQUE";
	public static final String CHAMP_DATE_FERMETURE_KIOSQUE = "DATE_FERMETURE_KIOSQUE";
	public static final String CHAMP_COMMENTAIRE = "COMMENTAIRE";

	public CampagneEAEDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_CAMPAGNE_EAE";
		super.CHAMP_ID = "ID_CAMPAGNE_EAE";
	}

	@Override
	public ArrayList<CampagneEAE> listerCampagneEAE() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_ANNEE + " desc";

		ArrayList<CampagneEAE> listeCampagneEAE = new ArrayList<CampagneEAE>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			CampagneEAE camp = new CampagneEAE();
			logger.info("List campagne EAE : " + row.toString());
			camp.setIdCampagneEae((Integer) row.get(CHAMP_ID));
			camp.setAnnee((Integer) row.get(CHAMP_ANNEE));
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
		return super.chercherObject(CampagneEAE.class, idCampagneEAE);
	}

	@Override
	public Integer creerCampagneEAE(Integer annee, Date dateDebut, String commentaire) throws Exception {
		String sqlClePrimaire = "select nextval('" + NOM_SEQUENCE + "')";
		Integer id = jdbcTemplate.queryForInt(sqlClePrimaire);

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_ANNEE + "," + CHAMP_DATE_DEBUT + ","
				+ CHAMP_COMMENTAIRE + ") VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { id, annee, dateDebut, commentaire });

		return id;
	}

	@Override
	public void modifierCampagneEAE(Integer idCampagneEAE, Date dateDebut, String commentaire) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_DEBUT + " =?," + CHAMP_COMMENTAIRE + "=? where "
				+ CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateDebut, commentaire, idCampagneEAE });
	}

	@Override
	public void modifierOuvertureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateOuvertureKiosque) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_OUVERTURE_KIOSQUE + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateOuvertureKiosque, idCampagneEAE });
	}

	@Override
	public void modifierFermetureKiosqueCampagneEAE(Integer idCampagneEAE, Date dateFermetureKiosque) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_FERMETURE_KIOSQUE + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateFermetureKiosque, idCampagneEAE });
	}

	@Override
	public void modifierFinCampagneEAE(Integer idCampagneEAE, Date dateFin) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_DATE_FIN + " =? where " + CHAMP_ID + "=?";
		jdbcTemplate.update(sql, new Object[] { dateFin, idCampagneEAE });
	}

	@Override
	public CampagneEAE chercherCampagneEAEAnnee(Integer annee) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ANNEE + " = ? ";

		CampagneEAE camp = (CampagneEAE) jdbcTemplate.queryForObject(sql, new Object[] { annee },
				new BeanPropertyRowMapper<CampagneEAE>(CampagneEAE.class));

		return camp;
	}

	@Override
	public CampagneEAE chercherCampagneEAEOuverte() throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_DATE_FIN + " is null ";
		CampagneEAE camp = (CampagneEAE) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<CampagneEAE>(
				CampagneEAE.class));
		return camp;
	}
}
