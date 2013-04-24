package nc.mairie.spring.dao.mapper.metier.diplome;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PermisAgentRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		PermisAgentResultSetExtractor extractor = new PermisAgentResultSetExtractor();
		return extractor.extractData(rs);
	}

}
