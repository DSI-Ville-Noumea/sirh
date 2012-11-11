package nc.mairie.spring.dao.metier.EAE;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeTypeObjectifRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeTypeObjectif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class EaeTypeObjectifDao implements EaeTypeObjectifDaoInterface {

	private static Logger logger = LoggerFactory.getLogger(EaeTypeObjectifDao.class);

	public static final String NOM_TABLE = "EAE_TYPE_OBJECTIF";

	public static final String NOM_SEQUENCE = "EAE_S_TYPE_OBJECTIF";

	public static final String CHAMP_ID_EAE_TYPE_OBJECTIF = "ID_EAE_TYPE_OBJECTIF";
	public static final String CHAMP_LIBELLE_TYPE_OBJECTIF = "LIBELLE_TYPE_OBJECTIF";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public EaeTypeObjectifDao() {

	}

	@Override
	public EaeTypeObjectif chercherTypeObjectifByLib(String lib) {
		String sql = "select * from " + NOM_TABLE + " where UPPER(" + CHAMP_LIBELLE_TYPE_OBJECTIF + ") = ? ";
		EaeTypeObjectif typeRes = (EaeTypeObjectif) jdbcTemplate.queryForObject(sql, new Object[] { lib }, new EaeTypeObjectifRowMapper());
		return typeRes;
	}

}
