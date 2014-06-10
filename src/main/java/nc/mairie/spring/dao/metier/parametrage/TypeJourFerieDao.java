package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.spring.domain.metier.parametrage.TypeJourFerie;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class TypeJourFerieDao implements TypeJourFerieDaoInterface {

	public static final String NOM_TABLE = "R_TYPE_JOUR_FERIE";

	public static final String CHAMP_ID_TYPE_JOUR_FERIE = "ID_TYPE_JOUR_FERIE";
	public static final String CHAMP_LIB_TYPE_JOUR_FERIE = "LIB_TYPE_JOUR_FERIE";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TypeJourFerieDao() {

	}

	@Override
	public ArrayList<TypeJourFerie> listerTypeJour() {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<TypeJourFerie> listeTypeJourFerie = new ArrayList<TypeJourFerie>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeJourFerie type = new TypeJourFerie();
			type.setIdTypeJourFerie((Integer) row.get(CHAMP_ID_TYPE_JOUR_FERIE));
			type.setLibTypeJourFerie((String) row.get(CHAMP_LIB_TYPE_JOUR_FERIE));
			listeTypeJourFerie.add(type);
		}

		return listeTypeJourFerie;
	}

	@Override
	public TypeJourFerie chercherTypeJourByLibelle(String libelle) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_TYPE_JOUR_FERIE + " = ? ";
		TypeJourFerie type = (TypeJourFerie) jdbcTemplate.queryForObject(sql, new Object[] { libelle }, new BeanPropertyRowMapper<TypeJourFerie>(TypeJourFerie.class));
		return type;
	}
}
