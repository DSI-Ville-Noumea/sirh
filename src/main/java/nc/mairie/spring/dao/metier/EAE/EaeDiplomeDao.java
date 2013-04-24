package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeDiplome;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeDiplomeDao implements EaeDiplomeDaoInterface {

	public static final String NOM_TABLE = "EAE_DIPLOME";

	public static final String NOM_SEQUENCE = "EAE_S_DIPLOME";

	public static final String CHAMP_ID_EAE_DIPLOME = "ID_EAE_DIPLOME";
	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_LIBELLE_DIPLOME = "LIBELLE_DIPLOME";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeDiplomeDao() {

	}

	@Override
	public void creerEaeDiplome(Integer idEae, String libDiplome) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE_DIPLOME + "," + CHAMP_ID_EAE + "," + CHAMP_LIBELLE_DIPLOME + ") " + "VALUES ("
				+ NOM_SEQUENCE + ".nextval,?,?)";

		jdbcTemplate.update(sql, new Object[] { idEae, libDiplome });
	}

	@Override
	public ArrayList<EaeDiplome> listerEaeDiplome(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeDiplome> listeEaeDiplome = new ArrayList<EaeDiplome>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeDiplome dip = new EaeDiplome();
			// logger.debug("List diplomes : " + row.toString());
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_DIPLOME);
			dip.setIdEaeDiplome(id.intValue());
			BigDecimal idEae = (BigDecimal) row.get(CHAMP_ID_EAE);
			dip.setIdEae(idEae.intValue());
			dip.setLibelleDiplome((String) row.get(CHAMP_LIBELLE_DIPLOME));

			listeEaeDiplome.add(dip);
		}
		return listeEaeDiplome;
	}

	@Override
	public void supprimerEaeDiplome(Integer idDiplome) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_ID_EAE_DIPLOME + "=?";
		jdbcTemplate.update(sql, new Object[] { idDiplome });
	}
}
