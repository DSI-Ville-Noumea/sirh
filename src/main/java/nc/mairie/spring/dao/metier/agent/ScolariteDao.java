package nc.mairie.spring.dao.metier.agent;

import java.util.Date;
import java.util.List;

import nc.mairie.metier.agent.Scolarite;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ScolariteDao extends SirhDao implements ScolariteDaoInterface {

	public static final String CHAMP_ID_ENFANT = "ID_ENFANT";
	public static final String CHAMP_DATE_DEBUT_SCOLARITE = "DATE_DEBUT_SCOLARITE";
	public static final String CHAMP_DATE_FIN_SCOLARITE = "DATE_FIN_SCOLARITE";

	public ScolariteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "SCOLARITE";
		super.CHAMP_ID = "ID_SCOLARITE";
	}

	@Override
	public void creerScolarite(Integer idEnfant, Date dateDebut, Date dateFin) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_ENFANT + "," + CHAMP_DATE_DEBUT_SCOLARITE + ","
				+ CHAMP_DATE_FIN_SCOLARITE + ") " + "VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idEnfant, dateDebut, dateFin });
	}

	@Override
	public void modifierScolarite(Integer idScolarite, Integer idEnfant, Date dateDebut, Date dateFin) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_ENFANT + "=?," + CHAMP_DATE_DEBUT_SCOLARITE + "=?,"
				+ CHAMP_DATE_FIN_SCOLARITE + "=?where " + CHAMP_ID + " =?";

		jdbcTemplate.update(sql, new Object[] { idEnfant, dateDebut, dateFin, idScolarite });
	}

	@Override
	public List<Scolarite> listerScolariteEnfant(Integer idEnfant) throws Exception {

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ENFANT + " =? ";
		List<Scolarite> rows = jdbcTemplate.query(sql, new Object[] { idEnfant }, new BeanPropertyRowMapper<Scolarite>(
				Scolarite.class));

		return rows;
	}

	@Override
	public void supprimerScolarite(Integer idScolarite) throws Exception {
		super.supprimerObject(idScolarite);
	}

	@Override
	public List<Scolarite> getListe() throws Exception {

		return super.getListe(Scolarite.class);
	}

}
