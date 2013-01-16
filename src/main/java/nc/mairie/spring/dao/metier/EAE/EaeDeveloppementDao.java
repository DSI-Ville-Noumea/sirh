package nc.mairie.spring.dao.metier.EAE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import nc.mairie.spring.dao.mapper.metier.EAE.EaeEvolutionRowMapper;
import nc.mairie.spring.domain.metier.EAE.EaeDeveloppement;
import nc.mairie.spring.domain.metier.EAE.EaeDiplome;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

import org.springframework.jdbc.core.JdbcTemplate;

public class EaeDeveloppementDao implements EaeDeveloppementDaoInterface {

	private static Logger logger = Logger.getLogger(EaeDeveloppementDao.class.getName());

	public static final String NOM_TABLE = "EAE_DEVELOPPEMENT";

	public static final String NOM_SEQUENCE = "EAE_S_DEVELOPPEMENT";

	public static final String CHAMP_ID_EAE_DEVELOPPEMENT = "ID_EAE_DEVELOPPEMENT";
	public static final String CHAMP_ID_EAE_EVOLUTION = "ID_EAE_EVOLUTION";
	public static final String CHAMP_LIBELLE = "LIBELLE";
	public static final String CHAMP_ECHEANCE = "ECHEANCE";
	public static final String CHAMP_PRIORISATION= "PRIORISATION";
	public static final String CHAMP_TYPE_DEVELOPPEMENT = "TYPE_DEVELOPPEMENT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public EaeDeveloppementDao() {

	}
}
