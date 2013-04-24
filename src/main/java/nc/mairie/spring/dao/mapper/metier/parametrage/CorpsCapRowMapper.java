package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CorpsCapRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		CorpsCapResultSetExtractor extractor = new CorpsCapResultSetExtractor();
		return extractor.extractData(rs);
	}

}
