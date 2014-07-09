package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import nc.mairie.metier.referentiel.TypeRepresentant;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class TypeRepresentantDao implements TypeRepresentantDaoInterface {

	public static final String NOM_TABLE = "R_TYPE_REPRESENTANT";

	public static final String CHAMP_ID_TYPE_REPRESENTANT = "ID_TYPE_REPRESENTANT";
	public static final String CHAMP_LIB_TYPE_REPRESENTANT = "LIB_TYPE_REPRESENTANT";

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TypeRepresentantDao() {

	}

	@Override
	public ArrayList<TypeRepresentant> listerTypeRepresentant() throws Exception {
		String sql = "select * from " + NOM_TABLE;

		ArrayList<TypeRepresentant> listeTypeRepresentant = new ArrayList<TypeRepresentant>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TypeRepresentant typeRepresentant = new TypeRepresentant();
			typeRepresentant.setIdTypeRepresentant((Integer) row.get(CHAMP_ID_TYPE_REPRESENTANT));
			typeRepresentant.setLibTypeRepresentant((String) row.get(CHAMP_LIB_TYPE_REPRESENTANT));
			listeTypeRepresentant.add(typeRepresentant);
		}

		return listeTypeRepresentant;
	}

	@Override
	public TypeRepresentant chercherTypeRepresentant(Integer idTypeRepresentant) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_TYPE_REPRESENTANT + " = ? ";
		TypeRepresentant type = (TypeRepresentant) jdbcTemplate.queryForObject(sql, new Object[] { idTypeRepresentant },
				new BeanPropertyRowMapper<TypeRepresentant>(TypeRepresentant.class));
		return type;
	}
}
