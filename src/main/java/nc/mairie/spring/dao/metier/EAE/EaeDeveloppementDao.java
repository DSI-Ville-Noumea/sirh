package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeDeveloppement;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeDeveloppementDao implements EaeDeveloppementDaoInterface {

	public static final String NOM_TABLE = "EAE_DEVELOPPEMENT";

	public static final String NOM_SEQUENCE = "EAE_S_DEVELOPPEMENT";

	public static final String CHAMP_ID_EAE_DEVELOPPEMENT = "ID_EAE_DEVELOPPEMENT";
	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_LIBELLE = "LIBELLE";
	public static final String CHAMP_ECHEANCE = "ECHEANCE";
	public static final String CHAMP_PRIORISATION = "PRIORISATION";
	public static final String CHAMP_TYPE_DEVELOPPEMENT = "TYPE_DEVELOPPEMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeDeveloppementDao() {

	}

	@Override
	public void modifierEaeDeveloppement(Integer idEaeDeveloppement, String typeDeveloppement, String libelleDeveloppement,
			Date echeanceDeveloppement, Integer priorisation) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TYPE_DEVELOPPEMENT + " =?," + CHAMP_LIBELLE + "=?," + CHAMP_ECHEANCE + "=?,"
				+ CHAMP_PRIORISATION + "=? where " + CHAMP_ID_EAE_DEVELOPPEMENT + "=?";
		jdbcTemplate.update(sql, new Object[] { typeDeveloppement, libelleDeveloppement, echeanceDeveloppement, priorisation, idEaeDeveloppement });
	}

	@Override
	public void creerEaeDeveloppement(Integer idEaeEvolution, String typeDeveloppement, String libelleDeveloppement, Date echeanceDeveloppement,
			Integer priorisation) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_DEVELOPPEMENT + "," + CHAMP_ID_EAE_EVOLUTION + "," + CHAMP_LIBELLE + ","
				+ CHAMP_ECHEANCE + "," + CHAMP_PRIORISATION + "," + CHAMP_TYPE_DEVELOPPEMENT + ") " + "VALUES (" + NOM_SEQUENCE
				+ ".nextval,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEaeEvolution, libelleDeveloppement, echeanceDeveloppement, priorisation, typeDeveloppement });
	}

	@Override
	public ArrayList<EaeDeveloppement> listerEaeDeveloppementParEvolution(Integer idEvolution) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE_EVOLUTION + "=? order by " + CHAMP_PRIORISATION;

		ArrayList<EaeDeveloppement> listeEaeDeveloppement = new ArrayList<EaeDeveloppement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEvolution });
		for (Map<String, Object> row : rows) {
			EaeDeveloppement dev = new EaeDeveloppement();
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_DEVELOPPEMENT);
			dev.setIdEaeDeveloppement(id.intValue());
			BigDecimal idEaeEvolution = (BigDecimal) row.get(CHAMP_ID_EAE_EVOLUTION);
			dev.setIdEaeEvolution(idEaeEvolution.intValue());
			dev.setLibelleDeveloppement((String) row.get(CHAMP_LIBELLE));
			dev.setEcheanceDeveloppement((Date) row.get(CHAMP_ECHEANCE));
			BigDecimal prio = (BigDecimal) row.get(CHAMP_PRIORISATION);
			dev.setPriorisation(prio == null ? null : prio.intValue());
			dev.setTypeDeveloppement((String) row.get(CHAMP_TYPE_DEVELOPPEMENT));

			listeEaeDeveloppement.add(dev);
		}
		return listeEaeDeveloppement;
	}

	@Override
	public void supprimerEaeDeveloppement(Integer idEaeDeveloppement) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_DEVELOPPEMENT + "=?";
		jdbcTemplate.update(sql, new Object[] { idEaeDeveloppement });
	}
}
