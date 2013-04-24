package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EmployeurRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EmployeurResultSetExtractor extractor = new EmployeurResultSetExtractor();
		return extractor.extractData(rs);
	}

}
