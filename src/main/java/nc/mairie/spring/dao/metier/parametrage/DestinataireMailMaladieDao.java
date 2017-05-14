package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import nc.mairie.metier.parametrage.DestinataireMailMaladie;
import nc.mairie.spring.dao.utils.SirhDao;

public class DestinataireMailMaladieDao extends SirhDao implements DestinataireMailMaladieDaoInterface {

	public static final String CHAMP_ID_GROUPE = "ID_GROUPE";

	public DestinataireMailMaladieDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_DESTINATAIRE_MAIL_MALADIE";
		super.CHAMP_ID = "ID_DESTINATAIRE_MAIL_MALADIE";
	}

	@Override
	public ArrayList<DestinataireMailMaladie> listerDestinataireMailMaladie() throws Exception {
		String sql = "select * from " + NOM_TABLE;

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
	public void creerDestinataireMailMaladie(Integer idGroupe) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_GROUPE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { idGroupe });
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
}
