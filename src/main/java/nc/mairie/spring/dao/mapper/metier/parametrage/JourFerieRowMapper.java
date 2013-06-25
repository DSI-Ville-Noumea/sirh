package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class JourFerieRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		JourFerieResultSetExtractor extractor = new JourFerieResultSetExtractor();
		return extractor.extractData(rs);
	}

}
