package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.EAE.EaeTypeDeveloppement;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeTypeDeveloppementDao implements EaeTypeDeveloppementDaoInterface {

	public static final String NOM_TABLE = "EAE_TYPE_DEVELOPPEMENT";

	public static final String NOM_SEQUENCE = "EAE_S_TYPE_DEVELOPPEMENT";

	public static final String CHAMP_ID_EAE_TYPE_DEVELOPPEMENT = "ID_EAE_TYPE_DEVELOPPEMENT";
	public static final String CHAMP_LIBELLE_TYPE_DEVELOPPEMENT = "LIBELLE_TYPE_DEVELOPPEMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeTypeDeveloppementDao() {

	}

	@Override
	public ArrayList<EaeTypeDeveloppement> listerEaeTypeDeveloppement() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIBELLE_TYPE_DEVELOPPEMENT;

		ArrayList<EaeTypeDeveloppement> listeEaeTypeDeveloppement = new ArrayList<EaeTypeDeveloppement>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			EaeTypeDeveloppement dev = new EaeTypeDeveloppement();
			BigDecimal id = (BigDecimal) row.get(CHAMP_ID_EAE_TYPE_DEVELOPPEMENT);
			dev.setIdEaeTypeDeveloppement(id.intValue());
			dev.setLibelleTypeDeveloppement((String) row.get(CHAMP_LIBELLE_TYPE_DEVELOPPEMENT));

			listeEaeTypeDeveloppement.add(dev);
		}
		return listeEaeTypeDeveloppement;
	}
}
