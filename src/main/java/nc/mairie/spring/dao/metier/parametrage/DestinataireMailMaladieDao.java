package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import nc.mairie.metier.parametrage.DestinataireMailMaladie;
import nc.mairie.spring.dao.utils.SirhDao;

public class DestinataireMailMaladieDao extends SirhDao implements DestinataireMailMaladieDaoInterface {

	public static final String CHAMP_ID_GROUPE = "ID_GROUPE";
	public static final String CHAMP_IS_FOR_JOB 	= "IS_FOR_JOB";

	public DestinataireMailMaladieDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_DESTINATAIRE_MAIL_MALADIE";
		super.CHAMP_ID = "ID_DESTINATAIRE_MAIL_MALADIE";
	}

	@Override
	public ArrayList<DestinataireMailMaladie> listerDestinataireMailMaladie(boolean isForJob) throws Exception {
		String isForJobString = (isForJob == true) ? "1" : "0";
		
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_IS_FOR_JOB + " = " + isForJobString;

		ArrayList<DestinataireMailMaladie> listeDestinataireMailMaladie = new ArrayList<DestinataireMailMaladie>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			DestinataireMailMaladie dest = new DestinataireMailMaladie();
			dest.setIdDestinataireMailMaladie((Integer) row.get(CHAMP_ID));
			dest.setIdGroupe((Integer) row.get(CHAMP_ID_GROUPE));

			listeDestinataireMailMaladie.add(dest);
		}

		return listeDestinataireMailMaladie;
	}

	@Override
	public void creerDestinataireMailMaladie(Integer idGroupe, boolean isForJob) throws Exception {
		// Il ne faut pas créer le groupe s'il existe déjà.
		if (chercherDestinataireMailMaladieByIdGroupe(idGroupe, isForJob) != null)
			return;
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_GROUPE + ", " + CHAMP_IS_FOR_JOB + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { idGroupe, isForJob });
	}

	@Override
	public void supprimerDestinataireMailMaladie(Integer idDestinataireMailMaladie) throws Exception {
		super.supprimerObject(idDestinataireMailMaladie);
	}

	@Override
	public DestinataireMailMaladie chercherDestinataireMailMaladieById(Integer idDestinataireMailMaladie) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + " = ? ";
		DestinataireMailMaladie cadre = (DestinataireMailMaladie) jdbcTemplate.queryForObject(sql, new Object[] { idDestinataireMailMaladie },
				new BeanPropertyRowMapper<DestinataireMailMaladie>(DestinataireMailMaladie.class));
		return cadre;
	}

	@Override
	public DestinataireMailMaladie chercherDestinataireMailMaladieByIdGroupe(Integer idGroupe, boolean isForJob) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_GROUPE + " = ? AND " + CHAMP_IS_FOR_JOB + " =?";
		String isForJobString = isForJob == true ? "1" : "0";
		DestinataireMailMaladie groupe = null;
		try {
			groupe = (DestinataireMailMaladie) jdbcTemplate.queryForObject(sql, new Object[] { idGroupe, isForJobString },
				new BeanPropertyRowMapper<DestinataireMailMaladie>(DestinataireMailMaladie.class));
		} catch (EmptyResultDataAccessException e) {
			// S'il n'y a pas d'enregistrement, on renvoi NULL
		}
		
		return groupe;
	}
}
