package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeParcoursPro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeParcoursProDao implements EaeParcoursProDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaeParcoursProDao.class);

	public static final String NOM_TABLE = "EAE_PARCOURS_PRO";

	public static final String NOM_SEQUENCE = "EAE_S_PARCOURS_PRO";

	public static final String CHAMP_ID_EAE_PARCOURS_PRO = "ID_EAE_PARCOURS_PRO";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_LIBELLE_PARCOURS_PRO = "LIBELLE_PARCOURS_PRO";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeParcoursProDao() {

	}

	@Override
	public void creerParcoursPro(Integer idEae, Date dateDebut, Date dateFin, String libParcours) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_PARCOURS_PRO + "," + CHAMP_ID_EAE + "," + CHAMP_DATE_DEBUT + ","
				+ CHAMP_DATE_FIN + "," + CHAMP_LIBELLE_PARCOURS_PRO + ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, dateDebut, dateFin, libParcours });
	}

	@Override
	public ArrayList<EaeParcoursPro> listerEaeParcoursPro(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeParcoursPro> listeEaeParcoursPro = new ArrayList<EaeParcoursPro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map row : rows) {
			EaeParcoursPro parc = new EaeParcoursPro();
			// logger.debug("List diplomes : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_PARCOURS_PRO);
			parc.setIdEaeParcoursPro(id.intValue());
			BigDecimal idEae = (BigDecimal) row.get(CHAMP_ID_EAE);
			parc.setIdEAE(idEae.intValue());
			parc.setDateDebut((Date) row.get(CHAMP_DATE_DEBUT));
			parc.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			parc.setLibelleParcoursPro((String) row.get(CHAMP_LIBELLE_PARCOURS_PRO));

			listeEaeParcoursPro.add(parc);
		}
		return listeEaeParcoursPro;
	}

	@Override
	public void supprimerEaeParcoursPro(Integer idParcoursPro) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_PARCOURS_PRO + "=?";
		jdbcTemplate.update(sql, new Object[] { idParcoursPro });
	}
}
