package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeFormation;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeFormationDao implements EaeFormationDaoInterface {

	public static final String NOM_TABLE = "EAE_FORMATION";

	public static final String NOM_SEQUENCE = "EAE_S_FORMATION";

	public static final String CHAMP_ID_EAE_FORMATION = "ID_EAE_FORMATION";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_ANNEE_FORMATION = "ANNEE_FORMATION";
	public static final String CHAMP_DUREE_FORMATION = "DUREE_FORMATION";
	public static final String CHAMP_LIBELLE_FORMATION = "LIBELLE_FORMATION";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeFormationDao() {

	}

	@Override
	public void creerEaeFormation(Integer idEae, Integer anneeFormation, String dureeFormation, String libFormation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_FORMATION + "," + CHAMP_ID_EAE + "," + CHAMP_ANNEE_FORMATION + ","
				+ CHAMP_DUREE_FORMATION + "," + CHAMP_LIBELLE_FORMATION + ") " + "VALUES (" + NOM_SEQUENCE + ".nextval,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, anneeFormation, dureeFormation, libFormation });

	}

	@Override
	public ArrayList<EaeFormation> listerEaeFormation(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeFormation> listeEaeFormation = new ArrayList<EaeFormation>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map row : rows) {
			EaeFormation form = new EaeFormation();
			// logger.debug("List diplomes : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_FORMATION);
			form.setIdEaeFormation(id.intValue());
			BigDecimal idEae = (BigDecimal) row.get(CHAMP_ID_EAE);
			form.setIdEAE(idEae.intValue());
			BigDecimal annee = (BigDecimal) row.get(CHAMP_ANNEE_FORMATION);
			form.setAnneeFormation(annee.intValue());
			form.setDureeFormation((String) row.get(CHAMP_DUREE_FORMATION));
			form.setLibelleFormation((String) row.get(CHAMP_LIBELLE_FORMATION));

			listeEaeFormation.add(form);
		}
		return listeEaeFormation;
	}

	@Override
	public void supprimerEaeFormation(Integer idFormation) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_FORMATION + "=?";
		jdbcTemplate.update(sql, new Object[] { idFormation });
	}
}
