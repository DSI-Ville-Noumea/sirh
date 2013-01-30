package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EmployeurCapRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EmployeurCapResultSetExtractor extractor = new EmployeurCapResultSetExtractor();
		return extractor.extractData(rs);
	}

}
